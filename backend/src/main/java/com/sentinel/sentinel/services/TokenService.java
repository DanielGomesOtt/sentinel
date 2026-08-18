package com.sentinel.sentinel.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.sentinel.sentinel.models.SystemIntegration;
import com.sentinel.sentinel.models.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.duration}")
    private Long duration;

    public String signUserToken(Users user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("sentinel")
                    .withSubject(user.getEmail())
                    .withClaim("type", "user")
                    .withClaim("id", user.getId().toString())
                    .withClaim("email", user.getEmail())
                    .withClaim("organization_id", user.getOrganization().getId().toString())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new JWTCreationException(exception.getMessage(), exception);
        }
    }

    public String signSystemToken(SystemIntegration client) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("sentinel")
                    .withSubject(client.getClientId())
                    .withClaim("type", "system")
                    .withClaim("client_id", client.getClientId())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new JWTCreationException(exception.getMessage(), exception);
        }
    }

    private Instant expirationDate() {
        return LocalDateTime.now()
                .plusHours(duration)
                .toInstant(ZoneOffset.of("-03:00"));
    }

    public Map<String, Claim> verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("sentinel")
                    .build()
                    .verify(token)
                    .getClaims();
        } catch (JWTVerificationException exception){
            throw new JWTVerificationException(exception.getMessage(), exception);
        }
    }
}
