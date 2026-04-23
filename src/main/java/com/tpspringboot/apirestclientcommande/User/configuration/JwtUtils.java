package com.tpspringboot.apirestclientcommande.User.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private long expirationTime;

    public String generateToken(String username, List<String> roles) {
        // Normalize roles before storing
        List<String> normalizedRoles = roles.stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", normalizedRoles);   // lowercase - frontend compatible
        claims.put("Roles", normalizedRoles);   // uppercase - backward compat
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts roles from the JWT token.
     * Tries "roles" (lowercase), then "Roles" (uppercase), then "role" (single string).
     * Always normalizes result to include ROLE_ prefix.
     */
    public List<String> extractRoles(String token) {
        final Claims claims = extractAllClaims(token);

        // Try "roles" (lowercase)
        Object rolesObj = claims.get("roles");

        // Fallback to "Roles" (uppercase - legacy)
        if (rolesObj == null) {
            rolesObj = claims.get("Roles");
        }

        if (rolesObj instanceof List<?> roleList) {
            return roleList.stream()
                    .map(String::valueOf)
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .toList();
        }

        // Fallback to single "role" string
        Object singleRole = claims.get("role");
        if (singleRole instanceof String r) {
            return List.of(r.startsWith("ROLE_") ? r : "ROLE_" + r);
        }

        log.warn("[JWT] No role/roles claim found in token for subject: {}", claims.getSubject());
        return List.of();
    }

    /** @deprecated Use extractRoles() instead */
    @Deprecated
    public List<String> extractRole(String token) {
        return extractRoles(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}