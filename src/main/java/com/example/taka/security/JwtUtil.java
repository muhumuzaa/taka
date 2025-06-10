package com.example.taka.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
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
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    //generate jwt token given email
    public String generateToken(String email){
        Date now = new Date(); //Issued at
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    //extract email (subject) from a token
    public String ExtractEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    //check if token is not expired
    public boolean isTokenExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    //validate token -> signature + expiration
    public boolean validateToken(String token, String userEmail){
        try{
            String email = ExtractEmail(token);
            return (email.equals(userEmail) && !isTokenExpired(token));
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith((SecretKey) signingKey).build().parseSignedClaims(token).getPayload();
    }

}
