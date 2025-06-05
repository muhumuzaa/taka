package com.example.taka.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    //creating a signing key from secret
    private Key signingKey;

    @PostConstruct
    public void init(){
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //generate jwt given username
    public String generateToken(String email){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    //extract email (subject) from a token
    public String ExtractEmail(String token){
        return parseClaims(token).getSubject();
    }

    //check if token is not expired
    public boolean isTokenExpired(String token){
        Date expiration = parseClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    //validate token -> signature + expiration
    public boolean validateToken(String token){
        try{
            parseClaims(token);
            return true;
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    //internal helper to parse claims
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
