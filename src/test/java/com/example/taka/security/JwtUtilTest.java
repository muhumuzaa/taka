package com.example.taka.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {
    private JwtUtil jwtUtil;

    private final String secret ="0123456789ABCDEF0123456789ABCDEF";
    private final long expirationMs =200l;

    @BeforeEach
    void setUp(){
        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(jwtUtil, "secretKey", secret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", expirationMs);

        jwtUtil.init();
    }

    @Test
    void extractAllClaims_shouldReturnCorrectSubjectAndDates(){
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        //call to extractAllClaims
        Claims claims = jwtUtil.extractAllClaims(token);

        assertNotNull(claims, "Claims should not be null");
        assertEquals(email, claims.getSubject(), "subject must match the email");
        assertTrue(claims.getIssuedAt().before(claims.getExpiration()), "Issued at must be before expiration");
    }

    @Test
    void validateToken_withValidAndInvalidEmail(){
        String email = "user@domain.com";
        String token = jwtUtil.generateToken(email);

        assertTrue(jwtUtil.validateToken(token, email), "token should validate for matching email");
        assertFalse(jwtUtil.validateToken(token, "new@gmail.com"), "token should not validate for non-matching email");
    }

    @Test
    void isTokenExpired_and_validateToken_afterExpiry() throws InterruptedException{
        String email = "expired@soon.com";
        String token = jwtUtil.generateToken(email);

        //immediately - not expired
        assertFalse(jwtUtil.isTokenExpired(token), "Token just issues must not be expired");
        assertTrue(jwtUtil.validateToken(token, email), "still valid before expiry");

        //wait past expiry
        Thread.sleep(expirationMs +50);


        assertTrue(jwtUtil.isTokenExpired(token), "Token should now be expired after expiryMs");
        //validateExpiry catches expiration and returns false
        assertFalse(jwtUtil.validateToken(token, email), "expired token should not validate");
    }

    @Test
    void extractAllClaims_withMalformedToken_shouldThrowJwtException(){
        String badToken ="this is not a jwt";
        assertThrows(JwtException.class, () -> jwtUtil.extractAllClaims(badToken), "bad token must trigger jwtException");
    }
}
