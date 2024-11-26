package com.itsthatjun.ecommerce.security.verification;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class VerificationTokenUtil {

    private static final Logger LOG = LoggerFactory.getLogger(VerificationTokenUtil.class);

    @Value("${verification.token.secret}")
    private String secret;

    @Value("${verification.token.issuer}")
    private String issuer;

    @Value("${verification.token.expiration}")
    private long expirationTimeInMinutes;

    public Mono<String> generateToken(String memberId, String email) {
        return Mono.fromCallable(() -> Jwts.builder()
                .setIssuer(issuer)
                .setSubject(memberId)
                .claim("email", email)
                .claim("type", "verification")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMinutes * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact());
    }

    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            String tokenIssuer = getIssuerFromToken(token);
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return tokenIssuer.equals(issuer) && !isTokenExpired(token);
        }).onErrorResume(ex -> {
            LOG.error("Verification token validation error: {}", ex.getMessage());
            return Mono.just(false);
        });
    }

    public Mono<String> extractEmail(String token) {
        return Mono.fromCallable(() -> getClaimsFromToken(token).get("email", String.class));
    }

    public Mono<String> extractUserId(String token) {
        return Mono.fromCallable(() -> getClaimsFromToken(token).getSubject());
    }

    private boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().before(new Date());
    }

    private String getIssuerFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getIssuer();
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            LOG.error("Unable to parse claims: {}", e.getMessage());
            return null;
        }
    }

    private Key getSigningKey() {
        return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    }
}