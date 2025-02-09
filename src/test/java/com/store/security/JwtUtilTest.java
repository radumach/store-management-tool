package com.store.security;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "y3xTo8Xa8LpA/KxiNon+wuxTrnBj4txSwb3JwQmvVBg="; // Must be at least 32 chars long
    private final Long expiration = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        
        // Manually inject secret and expiration BEFORE calling init()
        jwtUtil.setSecret(secret);
        jwtUtil.setExpiration(expiration);

        // Manually call init() to ensure key is initialized
        jwtUtil.init();
    }

    @Test
    void testGenerateToken() {
        // Arrange
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());

        // Act
        String token = jwtUtil.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    void testValidateToken() {
        // Arrange
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Act & Assert
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }
}
