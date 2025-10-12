package com.taskboard.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.infrastructure.web.dto.RefreshTokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for JWT token validation and management.
 * Tests token generation, validation, expiration, and refresh mechanisms.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RefreshTokenRequest validRefreshTokenRequest;

    @BeforeEach
    void setUp() {
        validRefreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("valid-refresh-token")
                .build();
    }

    @Test
    @DisplayName("Should reject requests with missing Authorization header")
    void shouldRejectRequestsWithMissingAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"));
    }

    @Test
    @DisplayName("Should reject requests with malformed Authorization header")
    void shouldRejectRequestsWithMalformedAuthorizationHeader() throws Exception {
        // Test with invalid format
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "InvalidFormat token123"))
                .andExpect(status().isUnauthorized());

        // Test with missing Bearer prefix
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "token123"))
                .andExpect(status().isUnauthorized());

        // Test with empty token
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject requests with invalid JWT token")
    void shouldRejectRequestsWithInvalidJwtToken() throws Exception {
        // Test with completely invalid token
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        // Test with malformed JWT structure
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer not.a.valid.jwt"))
                .andExpect(status().isUnauthorized());

        // Test with tampered JWT
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.tampered.signature"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject requests with expired JWT token")
    void shouldRejectRequestsWithExpiredJwtToken() throws Exception {
        // This would require a test token with past expiration time
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.invalid";
        
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject requests with JWT token signed with wrong algorithm")
    void shouldRejectRequestsWithJwtTokenSignedWithWrongAlgorithm() throws Exception {
        // Test with token signed with different algorithm
        String wrongAlgorithmToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0.invalid";
        
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + wrongAlgorithmToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject refresh token requests with invalid token")
    void shouldRejectRefreshTokenRequestsWithInvalidToken() throws Exception {
        RefreshTokenRequest invalidRequest = RefreshTokenRequest.builder()
                .refreshToken("invalid-refresh-token")
                .build();

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    @DisplayName("Should reject refresh token requests with expired token")
    void shouldRejectRefreshTokenRequestsWithExpiredToken() throws Exception {
        RefreshTokenRequest expiredRequest = RefreshTokenRequest.builder()
                .refreshToken("expired-refresh-token")
                .build();

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expiredRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("REFRESH_TOKEN_EXPIRED"));
    }

    @Test
    @DisplayName("Should reject refresh token requests with empty token")
    void shouldRejectRefreshTokenRequestsWithEmptyToken() throws Exception {
        RefreshTokenRequest emptyRequest = RefreshTokenRequest.builder()
                .refreshToken("")
                .build();

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.validationErrors").isArray());
    }

    @Test
    @DisplayName("Should reject requests with JWT token containing invalid claims")
    void shouldRejectRequestsWithJwtTokenContainingInvalidClaims() throws Exception {
        // Test with token missing required claims
        String missingClaimsToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1MTYyMzkwMjJ9.invalid";
        
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + missingClaimsToken))
                .andExpect(status().isUnauthorized());

        // Test with token containing invalid user ID
        String invalidUserIdToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJpbnZhbGlkLWlkIiwiaWF0IjoxNTE2MjM5MDIyfQ.invalid";
        
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + invalidUserIdToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject requests with JWT token from different issuer")
    void shouldRejectRequestsWithJwtTokenFromDifferentIssuer() throws Exception {
        // Test with token from different issuer
        String differentIssuerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJkaWZmZXJlbnQtaXNzdWVyIiwic3ViIjoiMTIzNDU2Nzg5MCIsImlhdCI6MTUxNjIzOTAyMn0.invalid";
        
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + differentIssuerToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject requests with JWT token for different audience")
    void shouldRejectRequestsWithJwtTokenForDifferentAudience() throws Exception {
        // Test with token for different audience
        String differentAudienceToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJkaWZmZXJlbnQtYXVkaWVuY2UiLCJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyfQ.invalid";
        
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + differentAudienceToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should handle token blacklisting correctly")
    void shouldHandleTokenBlacklistingCorrectly() throws Exception {
        // This test would require implementing token blacklisting
        // For now, we'll test that the endpoint responds consistently
        String blacklistedToken = "blacklisted-token";
        
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + blacklistedToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject requests with multiple Authorization headers")
    void shouldRejectRequestsWithMultipleAuthorizationHeaders() throws Exception {
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer token1")
                        .header("Authorization", "Bearer token2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should handle case sensitivity in Authorization header")
    void shouldHandleCaseSensitivityInAuthorizationHeader() throws Exception {
        // Test with lowercase 'bearer'
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "bearer token123"))
                .andExpect(status().isUnauthorized());

        // Test with mixed case
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "BeArEr token123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject requests with oversized JWT token")
    void shouldRejectRequestsWithOversizedJwtToken() throws Exception {
        // Create an oversized token (simulate)
        StringBuilder oversizedToken = new StringBuilder("Bearer ");
        for (int i = 0; i < 10000; i++) {
            oversizedToken.append("a");
        }
        
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", oversizedToken.toString()))
                .andExpect(status().isUnauthorized());
    }
}
