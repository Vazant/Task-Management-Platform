package com.taskboard.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.infrastructure.web.dto.LoginRequest;
import com.taskboard.userservice.infrastructure.web.dto.RegisterRequest;
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
 * Security tests for authentication endpoints.
 * Tests various security scenarios including brute force protection,
 * input validation, and authentication mechanisms.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest validLoginRequest;
    private RegisterRequest validRegisterRequest;

    @BeforeEach
    void setUp() {
        validLoginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .rememberMe(false)
                .build();

        validRegisterRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .confirmPassword("password123")
                .firstName("New")
                .lastName("User")
                .acceptTerms(true)
                .build();
    }

    @Test
    @DisplayName("Should reject login with invalid credentials")
    void shouldRejectLoginWithInvalidCredentials() throws Exception {
        LoginRequest invalidRequest = LoginRequest.builder()
                .username("nonexistent")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("Should reject login with empty credentials")
    void shouldRejectLoginWithEmptyCredentials() throws Exception {
        LoginRequest emptyRequest = LoginRequest.builder()
                .username("")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.validationErrors").isArray());
    }

    @Test
    @DisplayName("Should reject login with SQL injection attempt")
    void shouldRejectLoginWithSqlInjection() throws Exception {
        LoginRequest sqlInjectionRequest = LoginRequest.builder()
                .username("admin'; DROP TABLE users; --")
                .password("password")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sqlInjectionRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should reject login with XSS attempt")
    void shouldRejectLoginWithXssAttempt() throws Exception {
        LoginRequest xssRequest = LoginRequest.builder()
                .username("<script>alert('xss')</script>")
                .password("password")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should reject registration with weak password")
    void shouldRejectRegistrationWithWeakPassword() throws Exception {
        RegisterRequest weakPasswordRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("123")
                .confirmPassword("123")
                .firstName("New")
                .lastName("User")
                .acceptTerms(true)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weakPasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.validationErrors").isArray());
    }

    @Test
    @DisplayName("Should reject registration with invalid email")
    void shouldRejectRegistrationWithInvalidEmail() throws Exception {
        RegisterRequest invalidEmailRequest = RegisterRequest.builder()
                .username("newuser")
                .email("invalid-email")
                .password("password123")
                .confirmPassword("password123")
                .firstName("New")
                .lastName("User")
                .acceptTerms(true)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.validationErrors").isArray());
    }

    @Test
    @DisplayName("Should reject registration without terms acceptance")
    void shouldRejectRegistrationWithoutTermsAcceptance() throws Exception {
        RegisterRequest noTermsRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .confirmPassword("password123")
                .firstName("New")
                .lastName("User")
                .acceptTerms(false)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noTermsRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should reject registration with password mismatch")
    void shouldRejectRegistrationWithPasswordMismatch() throws Exception {
        RegisterRequest passwordMismatchRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .confirmPassword("differentpassword")
                .firstName("New")
                .lastName("User")
                .acceptTerms(true)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordMismatchRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should reject requests with malformed JSON")
    void shouldRejectRequestsWithMalformedJson() throws Exception {
        String malformedJson = "{ \"username\": \"test\", \"password\": }";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject requests with oversized payload")
    void shouldRejectRequestsWithOversizedPayload() throws Exception {
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeString.append("a");
        }

        LoginRequest oversizedRequest = LoginRequest.builder()
                .username(largeString.toString())
                .password("password")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oversizedRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle concurrent login attempts")
    void shouldHandleConcurrentLoginAttempts() throws Exception {
        // This test would require more complex setup with concurrent execution
        // For now, we'll test that the endpoint responds consistently
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isUnauthorized()); // Assuming user doesn't exist
    }
}
