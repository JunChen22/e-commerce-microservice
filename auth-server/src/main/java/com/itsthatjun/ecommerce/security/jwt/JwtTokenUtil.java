package com.itsthatjun.ecommerce.security.jwt;

import com.itsthatjun.ecommerce.security.CustomUserDetail;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
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

    public String generateToken(CustomUserDetail userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUserId());
        claims.put("username", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities());  // TODO: add getAuthorities
        claims.put("iat", new Date());
        claims.put("name", userDetails.getName());
        claims.put("iss", issuer);

        return Jwts.builder()
                .setIssuer(issuer)          // TODO: idk why it won't set issuer, need to user claim.put("iss", issuer) instead
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails){
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

    private boolean isTokenExpired(String token){
        Claims claims= getClaimsFromToken(token);
        Date date = claims.getExpiration();
        return date.before(new Date());
    }

    private Date generateExpirationDate(){
        return new Date(System.currentTimeMillis() + expiration * 1000 * 60);
    }

    public String getUsernameFromToken(String token){
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username =  (String) claims.get("userName");
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public String getNameFromToken(String token){
        String name;
        try {
            Claims claims = getClaimsFromToken(token);
            name =  (String) claims.get("name");
        } catch (Exception e) {
            name = null;
        }
        return name;
    }

    public int getUserIdFromToken(String token){
        Integer userId;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = (Integer) claims.get("userId");
        } catch (Exception e) {
            userId = null;
        }
        return (userId != null) ? userId.intValue() : 0;
    }

    private Claims getClaimsFromToken(String token){
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
