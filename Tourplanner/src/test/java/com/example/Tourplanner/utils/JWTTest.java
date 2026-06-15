package com.example.Tourplanner.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JWTTest {

    private JWT jwt;

    @BeforeEach
    void setUp() {
        jwt = new JWT();
        ReflectionTestUtils.setField(jwt, "secret", "MeinSuperGeheimesJWTSecret1234567890");
        ReflectionTestUtils.setField(jwt, "validity", 3600L);
    }

    @Test
    void createToken_shouldReturnValidToken() {
        String token = jwt.createToken("testuser");
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void getUsername_shouldReturnCorrectUsername() {
        String token = jwt.createToken("testuser");
        String username = jwt.getUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void isValid_shouldReturnTrueForValidToken() {
        String token = jwt.createToken("testuser");
        assertTrue(jwt.isValid(token));
    }

    @Test
    void isValid_shouldReturnFalseForInvalidToken() {
        assertFalse(jwt.isValid("invalid.token.here"));
    }

    @Test
    void isExpired_shouldReturnFalseForNewToken() {
        String token = jwt.createToken("testuser");
        assertFalse(jwt.isExpired(token));
    }
}
