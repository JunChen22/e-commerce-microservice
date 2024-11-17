package com.itsthatjun.ecommerce.security.jwt;

import com.itsthatjun.ecommerce.security.CustomUserDetail;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secretKey}")
    private String secret;

    @Value("${jwt.expirationTimeMinute}")
    private int expiration;

    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(CustomUserDetail userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getMemberId());
        claims.put("username", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities());  // TODO: add getAuthorities maybe, even for members
        claims.put("iat", new Date());
        claims.put("name", userDetails.getName());
        claims.put("iss", issuer);

        return Jwts.builder()
                .setIssuer(issuer)          // IDK why it won't set issuer, need to user claim.put("iss", issuer) instead
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // TODO: add refresh token
    private boolean isTokenExpired(String token) {
        Claims claims= getClaimsFromToken(token);
        Date date = claims.getExpiration();
        return date.before(new Date());
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000 * 60);
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username =  (String) claims.get("userName");
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("unable to get claim");
        }
        return claims;
    }
}
