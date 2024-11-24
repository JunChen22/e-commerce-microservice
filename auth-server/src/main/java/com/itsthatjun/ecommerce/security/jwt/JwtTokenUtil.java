package com.itsthatjun.ecommerce.security.jwt;

import com.itsthatjun.ecommerce.exception.InvalidJwtTokenException;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secretKey}")
    private String secret;         // Plain text secret

    @Value("${jwt.expirationTimeMinute}")
    private int expiration;          // Access token expiration in minutes

    @Value("${jwt.refreshExpirationTimeMinute}")
    private int refreshExpiration;  // Refresh token expiration in minutes

    @Value("${jwt.issuer}")
    private String issuer;

    public Mono<String> generateToken(CustomUserDetail userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities());
        claims.put("name", userDetails.getName());

        return generateJwtToken(userDetails, expiration, claims);
    }

    public Mono<String> generateRefreshToken(CustomUserDetail userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());

        return generateJwtToken(userDetails, refreshExpiration, claims);
    }

    private Mono<String> generateJwtToken(CustomUserDetail userDetails, int expirationMinutes, Map<String, Object> claims) {
        return Mono.fromCallable(() -> Jwts.builder()
                .setIssuer(issuer)
                .setSubject(userDetails.getMemberId().toString())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate(expirationMinutes))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact()).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> getUsernameFromToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Claims claims = getClaimsFromToken(token);
                return claims.get("username", String.class);
            } catch (JwtException e) {
                logger.error("Error extracting username from token", e);
                throw new InvalidJwtTokenException("Failed to extract username from token" + e.toString());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private SecretKey getSigningKey() {
        // Convert the plain text secret into a securely encoded SecretKey
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Date generateExpirationDate(int expirationMinutes) {
        return new Date(System.currentTimeMillis() + (long) expirationMinutes * 1000 * 60);
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new InvalidJwtTokenException("Invalid JWT token " + e.toString());
        }
    }
}
