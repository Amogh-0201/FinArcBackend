package com.app.finarc.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;
    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(SECRET_KEY);
    }

    private final long sevenDaysInMs = 604800000L;

    public String generateToken(String userId, String username) {
        return JWT.create()
                .withSubject(userId)
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + sevenDaysInMs))
                .sign(algorithm);
    }

    public DecodedJWT validateAndDecodeToken(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }
}
