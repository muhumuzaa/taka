package com.example.taka.security;

import io.jsonwebtoken.Claims; // Imports the Claims interface from the JJWT library, used to represent the payload of a JWT.
import io.jsonwebtoken.JwtException; // Imports JwtException, a common exception thrown by the JJWT library for JWT-related errors.
import org.junit.jupiter.api.BeforeEach; // Imports BeforeEach annotation from JUnit 5, indicating a method to be run before each test.
import org.junit.jupiter.api.Test; // Imports Test annotation from JUnit 5, marking a method as a test case.
import org.springframework.test.util.ReflectionTestUtils; // Imports ReflectionTestUtils from Spring's test utilities, used to access and set private fields via reflection.

import static org.junit.jupiter.api.Assertions.*; // Imports all static assertion methods from JUnit, like assertEquals, assertTrue, etc.

public class JwtUtilTest {
    private JwtUtil jwtUtil; // Declares an instance of JwtUtil, the class being tested.

    private final String secret ="0123456789ABCDEF0123456789ABCDEF"; // Defines a secret key used for signing and verifying JWTs.
    private final long expirationMs =1200L; // Defines the expiration time for JWTs in milliseconds.

    @BeforeEach // This method will run before every test method in this class.
    void setUp(){
        jwtUtil = new JwtUtil(); // Initializes a new JwtUtil instance for each test.

        // Uses reflection to set private fields in the jwtUtil instance.
        // This is often done in tests to inject dependencies or configurations that are private.
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", expirationMs);

        jwtUtil.init(); // Calls an initialization method on JwtUtil, likely to set up the signing key.
    }

    @Test // This method is a test case.
    void extractAllClaims_shouldReturnCorrectSubjectAndDates(){
        String email = "test@example.com"; // Defines a sample email to be used as the JWT subject.
        String token = jwtUtil.generateToken(email); // Generates a JWT using the JwtUtil with the specified email.

        // Calls the method under test: extractAllClaims from jwtUtil to parse the token.
        Claims claims = jwtUtil.extractAllClaims(token);

        assertNotNull(claims, "Claims should not be null"); // Asserts that the extracted claims object is not null.
        assertEquals(email, claims.getSubject(), "subject must match the email"); // Asserts that the 'subject' claim in the JWT matches the original email.
        assertTrue(claims.getIssuedAt().before(claims.getExpiration()), "Issued at must be before expiration"); // Asserts that the token's issued-at time is before its expiration time.
    }

    @Test // This method is a test case.
    void validateToken_withValidAndInvalidEmail(){
        String email = "user@domain.com"; // Defines a sample email.
        String token = jwtUtil.generateToken(email); // Generates a token for the sample email.

        // Asserts that the token validates successfully when the provided email matches the token's subject.
        assertTrue(jwtUtil.validateToken(token, email), "token should validate for matching email");
        // Asserts that the token does NOT validate when the provided email does not match the token's subject.
        assertFalse(jwtUtil.validateToken(token, "new@gmail.com"), "token should not validate for non-matching email");
    }

    @Test // This method is a test case.
    void isTokenExpired_and_validateToken_afterExpiry() throws InterruptedException{
        String email = "expired@soon.com"; // Defines a sample email for a token that will expire.
        String token = jwtUtil.generateToken(email); // Generates the token.

        // Test immediately after generation - token should not be expired.
        assertFalse(jwtUtil.isTokenExpired(token), "Token just issues must not be expired");
        assertTrue(jwtUtil.validateToken(token, email), "still valid before expiry");

        // Pauses the execution for a duration longer than the token's expiration time.
        Thread.sleep(expirationMs +50);

        // Test after waiting - token should now be expired.
        assertTrue(jwtUtil.isTokenExpired(token), "Token should now be expired after expiryMs");
        // Asserts that an expired token does NOT validate.
        assertFalse(jwtUtil.validateToken(token, email), "expired token should not validate");
    }

    @Test // This method is a test case.
    void extractAllClaims_withMalformedToken_shouldThrowJwtException(){
        String badToken ="this is not a jwt"; // Defines a string that is not a valid JWT.
        // Asserts that calling extractAllClaims with a malformed token throws a JwtException.
        assertThrows(JwtException.class, () -> jwtUtil.extractAllClaims(badToken), "bad token must trigger jwtException");
    }
}