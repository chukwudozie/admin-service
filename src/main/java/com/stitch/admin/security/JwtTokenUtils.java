package com.stitch.admin.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    private final Algorithm algorithm;

    public String generateJwtToken(String email, List<String> roles, List<String> permissions){

        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + 6000000))
                .withSubject(email)
                .withArrayClaim("permissions",permissions.toArray(new String[0]))
                .withArrayClaim("roles",roles.toArray(new String[0]))
                .sign(algorithm);

    }
}
