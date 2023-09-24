package com.itsthatjun.ecommerce.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secretKey}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    public boolean validateToken(String token){
        try {
            String tokenIssuer = getIssuerFromToken(token);
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return tokenIssuer.equals(issuer) && !isTokenExpired(token);
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

    public String getIssuerFromToken(String token){
        String issuer;
        try {
            Claims claims = getClaimsFromToken(token);
            issuer =  claims.getSubject();
        } catch (Exception e) {
            issuer = null;
        }
        return issuer;
    }

    public String getNameFromToken(String token){
        String name;
        try {
            Claims claims = getClaimsFromToken(token);
            name =  claims.getSubject();
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
