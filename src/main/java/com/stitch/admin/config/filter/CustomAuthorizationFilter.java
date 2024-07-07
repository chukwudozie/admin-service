package com.stitch.admin.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
public class CustomAuthorizationFilter  extends OncePerRequestFilter {
    private final Algorithm algorithm;

    public CustomAuthorizationFilter(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("request path : {}",request.getServletPath());
        if(request.getServletPath().startsWith("/api/v1/admin/auth")){
            filterChain.doFilter(request,response);
        }else {
            String jwtToken = request.getHeader("Authorization");
            try {
                if(Objects.nonNull(jwtToken) && jwtToken.startsWith("Bearer ")){
                    jwtToken = jwtToken.substring(7);
                    DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(jwtToken);
                    String userEmail = decodedJWT.getSubject();
                    List<String> permissions = decodedJWT.getClaim("permissions").asList(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
                    if(userEmail != null){
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userEmail,"",authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        filterChain.doFilter(request,response);
                    }
                }else {
                    writeError("Missing or invalid Authorization header ",response);
                }
            }catch (Exception e){
                writeError(e.getMessage(),response);
            }
        }
    }

    private void writeError(String message, HttpServletResponse response) throws IOException {
        log.error("Error in auth Filter ==> {}", message);
        response.setStatus(FORBIDDEN.value());
        response.setContentType("application/json");
        Map<String,String> error = new HashMap<>();
        error.put("error_message",message);
        new ObjectMapper().writeValue(response.getOutputStream(),error);
    }
}
