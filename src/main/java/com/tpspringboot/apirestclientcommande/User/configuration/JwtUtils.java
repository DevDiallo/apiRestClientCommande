package com.tpspringboot.apirestclientcommande.User.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts ;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey ;

    @Value("${app.expiration-time}")
    private long expirationTime ;

    public String generateToken(String username , List<String> roles){
        Map<String , Object> claims = new HashMap<>() ;
        claims.put("Roles" ,roles) ;
        return createToken(claims , username) ;
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey() , SignatureAlgorithm.HS256)
                .compact() ;
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Validation du token lorsque User se connecte

    public Boolean validateToken(String token , UserDetails userDetails){
        String username = extractUsername(token) ;
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)) ;
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date()) ;
    }

    private Date extractExpirationDate(String token){
        return extractClaim(token , Claims::getExpiration) ;
    }

    public String extractUsername(String token) {
        return extractClaim(token , Claims::getSubject) ;
    }

    public List<String> extractRole(String token){
        final Claims claims = extractAllClaims(token) ;
        return claims.get("Roles", List.class) ;
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token) ;
        return claimsResolver.apply(claims) ;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody() ;
    }


}
