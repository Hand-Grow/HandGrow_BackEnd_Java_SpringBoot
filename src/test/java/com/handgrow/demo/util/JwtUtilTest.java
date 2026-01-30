package com.handgrow.demo.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secretKey = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tZ2VuZXJhdGlvbi1hbmQtdmFsaWRhdGlvbg==";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);
    }

    @Test
    void generateToken_WithUsernameAndRole_Success() {
        // Given
        String username = "testuser";
        String role = "FARMER";

        // When
        String token = jwtUtil.generateToken(username, role);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals(username, jwtUtil.extractUsername(token));
        assertEquals(role, jwtUtil.extractRole(token));
    }

    @Test
    void generateToken_WithUsernameOnly_Success() {
        // Given
        String username = "testuser";

        // When
        String token = jwtUtil.generateToken(username);

        // Then
        assertNotNull(token);
        assertEquals(username, jwtUtil.extractUsername(token));
        assertNull(jwtUtil.extractRole(token));
    }

    @Test
    void extractUsername_ValidToken_Success() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username, "FARMER");

        // When
        String extractedUsername = jwtUtil.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractRole_ValidToken_Success() {
        // Given
        String role = "ENTERPRISE";
        String token = jwtUtil.generateToken("testuser", role);

        // When
        String extractedRole = jwtUtil.extractRole(token);

        // Then
        assertEquals(role, extractedRole);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username, "COOP");

        // When
        boolean isValid = jwtUtil.validateToken(token, username);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_WrongUsername_ReturnsFalse() {
        // Given
        String token = jwtUtil.generateToken("testuser", "FARMER");

        // When
        boolean isValid = jwtUtil.validateToken(token, "wronguser");

        // Then
        assertFalse(isValid);
    }
}
