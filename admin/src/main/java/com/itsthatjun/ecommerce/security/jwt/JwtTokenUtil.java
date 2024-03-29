package com.itsthatjun.ecommerce.security.jwt;

import com.itsthatjun.ecommerce.security.CustomUserDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.*;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secretKey}")
    private String secret;
    @Value("${jwt.expirationTimeMinute}")
    private int expiration;

    public String generateToken(CustomUserDetail userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date());
        claims.put("adminId", userDetails.getAdmin().getId());           // used in stateless to get information, using session in monolith.
        claims.put("adminName", userDetails.getAdmin().getName());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return userDetails.getUsername().equals(tokenUsername) && !isTokenExpired(token);
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT Token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    }

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
            username =  claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public String getAdminNameFromToken(String token) {
        String adminName;
        try {
            Claims claims = getClaimsFromToken(token);
            adminName =  (String) claims.get("adminName");
        } catch (Exception e) {
            adminName = null;
        }
        return adminName;
    }

    public int getAdminIdFromToken(String token) {
        Integer adminId;
        try {
            Claims claims = getClaimsFromToken(token);
            adminId = (Integer) claims.get("adminId");
        } catch (Exception e) {
            adminId = null;
        }
        return (adminId != null) ? adminId.intValue() : 0;
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
