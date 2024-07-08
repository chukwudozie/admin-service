package com.stitch.admin.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtils {
    private static final int TOKEN_EXPIRATION = 3600000;
    private static final int REFRESH_TOKEN_EXPIRATION = 86400000;

    private final Algorithm algorithm;

    public String generateJwtToken(String email, List<String> roles, List<String> permissions){

        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() +TOKEN_EXPIRATION))
                .withSubject(email)
                .withArrayClaim("permissions",permissions.toArray(new String[0]))
                .withArrayClaim("roles",roles.toArray(new String[0]))
                .sign(algorithm);

    }

    public String generateJwtRefreshToken(String email, List<String> roles, List<String> permissions){

        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .withSubject(email)
                .withArrayClaim("permissions",permissions.toArray(new String[0]))
                .withArrayClaim("roles",roles.toArray(new String[0]))
                .sign(algorithm);

    }

    private String extractUsernameFromToken(String token){
        return JWT.decode(token).getSubject();
    }

    private boolean validateToken(String token){
        try{
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        }catch (Exception e){
            log.error("Could not validate token --> {}",e.getMessage());
            return false;
        }
    }

    private Set<String> getPermissionsFromToken(String token){
        return new HashSet<>(JWT.decode(token).getClaim("permissions").asList(String.class));
    }

    private Set<String> getRolesFromToken(String token){
        return new HashSet<>(JWT.decode(token).getClaim("roles").asList(String.class));
    }
}
