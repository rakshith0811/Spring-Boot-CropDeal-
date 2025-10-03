package com.cropdeal.util;

import java.util.Base64;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.spec.SecretKeySpec;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.cropdeal.model.CustomUserDetails;

//import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretString;
    
    private SecretKey secretKey;
    
    @PostConstruct
    public void init() {
        // Check if the secret is Base64 encoded or plain text
        try {
            // Try to decode as Base64 first
            byte[] keyBytes = Base64.getDecoder().decode(secretString);
            // Ensure the key is at least 256 bits (32 bytes) for HS256
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 bytes) long");
            }
            secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        } catch (IllegalArgumentException e) {
            // If Base64 decoding fails, treat as plain text
            byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
            // Ensure the key is at least 256 bits (32 bytes) for HS256
            if (keyBytes.length < 32) {
                // If too short, use Keys.hmacShaKeyFor to generate a proper key
                secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
            } else {
                secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
            }
        }
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(CustomUserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        return createToken(claims, user.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }
}