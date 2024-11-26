package com.itsthatjun.ecommerce.security.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secretKey}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            String tokenIssuer = getIssuerFromToken(token);

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return tokenIssuer.equals(issuer) && !isTokenExpired(token);
        }).onErrorResume(ex -> {
            LOG.error("JWT validation error: {}", ex.getMessage());
            return Mono.just(false);
        });
    }

    private Key getSigningKey() {
        return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    }

    private boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        Date date = claims.getExpiration();
        return date.before(new Date());
    }

    public String getIssuerFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getIssuer() : null;
    }

    public Mono<String>  getNameFromToken(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = getClaimsFromToken(token);
            return (String) claims.get("name");
        });
    }

    public Mono<String> getUserNameFromToken(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = getClaimsFromToken(token);
            return (String) claims.get("username");
        });
    }

    public Mono<UUID> getMemberIdFromToken(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = getClaimsFromToken(token);
            return UUID.fromString(claims.getSubject()); // Assuming subject is a UUID string
        });
    }
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Use the Key object
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("Unable to get claims");
            return null;
        }
    }
}
