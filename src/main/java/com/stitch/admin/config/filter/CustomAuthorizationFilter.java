package com.stitch.admin.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stitch.admin.service.impl.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static com.stitch.admin.utils.Constants.ALLOWED_URLS;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
public class CustomAuthorizationFilter  extends OncePerRequestFilter {
    private final Algorithm algorithm;

    private final TokenBlacklistService blacklistService;


    public CustomAuthorizationFilter(Algorithm algorithm, TokenBlacklistService blacklistService) {
        this.algorithm = algorithm;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("request path : {}",request.getServletPath());
        if(ALLOWED_URLS.contains(request.getServletPath())){
            filterChain.doFilter(request,response);
        }else {
            String jwtToken = request.getHeader("Authorization");
            try {
                if(Objects.nonNull(jwtToken) && jwtToken.startsWith("Bearer ")){
                    jwtToken = jwtToken.substring(7);

                    if (blacklistService.isTokenBlacklisted(jwtToken)){
                        writeError("token has been blacklisted",response, UNAUTHORIZED);
                    }else{
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
                    }

                }else {
                    writeError("Missing or invalid Authorization header ",response, FORBIDDEN);
                }
            }catch (Exception e){
                writeError(e.getMessage(),response, FORBIDDEN);
            }
        }
    }

    private void writeError(String message, HttpServletResponse response, HttpStatus status) throws IOException {
        log.error("Error in auth Filter ==> {}", message);
        response.setStatus(status.value());
        response.setContentType("application/json");
        Map<String,String> error = new HashMap<>();
        error.put("error_message",message);
        new ObjectMapper().writeValue(response.getOutputStream(),error);
    }
}
