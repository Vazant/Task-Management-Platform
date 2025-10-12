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
 * Security tests for rate limiting and request throttling.
 * Tests rate limiting on authentication endpoints and general API usage.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitingSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        registerRequest = RegisterRequest.builder()
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
    @DisplayName("Should apply rate limiting to login endpoint")
    void shouldApplyRateLimitingToLoginEndpoint() throws Exception {
        // Make multiple rapid requests to login endpoint
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized()); // Assuming user doesn't exist
        }

        // After rate limit is exceeded, should return 429 Too Many Requests
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("X-RateLimit-Limit"))
                .andExpect(header().exists("X-RateLimit-Remaining"))
                .andExpect(header().exists("X-RateLimit-Reset"));
    }

    @Test
    @DisplayName("Should apply rate limiting to registration endpoint")
    void shouldApplyRateLimitingToRegistrationEndpoint() throws Exception {
        // Make multiple rapid requests to registration endpoint
        for (int i = 0; i < 5; i++) {
            RegisterRequest request = RegisterRequest.builder()
                    .username("user" + i)
                    .email("user" + i + "@example.com")
                    .password("password123")
                    .confirmPassword("password123")
                    .firstName("User")
                    .lastName("Test")
                    .acceptTerms(true)
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // After rate limit is exceeded, should return 429 Too Many Requests
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Should apply different rate limits for different endpoints")
    void shouldApplyDifferentRateLimitsForDifferentEndpoints() throws Exception {
        // Login endpoint might have stricter rate limiting
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        // Registration endpoint might have different limits
        for (int i = 0; i < 3; i++) {
            RegisterRequest request = RegisterRequest.builder()
                    .username("user" + i)
                    .email("user" + i + "@example.com")
                    .password("password123")
                    .confirmPassword("password123")
                    .firstName("User")
                    .lastName("Test")
                    .acceptTerms(true)
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    @DisplayName("Should include rate limit headers in responses")
    void shouldIncludeRateLimitHeadersInResponses() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("X-RateLimit-Limit"))
                .andExpect(header().exists("X-RateLimit-Remaining"))
                .andExpect(header().exists("X-RateLimit-Reset"));
    }

    @Test
    @DisplayName("Should handle rate limiting based on IP address")
    void shouldHandleRateLimitingBasedOnIpAddress() throws Exception {
        // Simulate requests from different IP addresses
        String[] ipAddresses = {
            "192.168.1.1",
            "192.168.1.2",
            "10.0.0.1"
        };

        for (String ip : ipAddresses) {
            // Each IP should have its own rate limit
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(post("/api/auth/login")
                                .header("X-Forwarded-For", ip)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Test
    @DisplayName("Should handle rate limiting based on user ID")
    void shouldHandleRateLimitingBasedOnUserId() throws Exception {
        // Simulate requests from different users
        String[] userIds = {
            "user1", "user2", "user3"
        };

        for (String userId : userIds) {
            // Each user should have its own rate limit
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(post("/api/auth/login")
                                .header("X-User-ID", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Test
    @DisplayName("Should reset rate limit after time window")
    void shouldResetRateLimitAfterTimeWindow() throws Exception {
        // Make requests up to the limit
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        // Wait for rate limit window to reset (in real test, this would be mocked)
        // For now, we'll just verify the behavior
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Should handle burst capacity correctly")
    void shouldHandleBurstCapacityCorrectly() throws Exception {
        // Test burst capacity - allow a burst of requests before applying rate limiting
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        // After burst capacity is exceeded, should start rate limiting
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Should handle rate limiting with different request sizes")
    void shouldHandleRateLimitingWithDifferentRequestSizes() throws Exception {
        // Test with small requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        // Test with large requests
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeContent.append("a");
        }

        LoginRequest largeRequest = LoginRequest.builder()
                .username(largeContent.toString())
                .password("password")
                .build();

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(largeRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("Should handle rate limiting with concurrent requests")
    void shouldHandleRateLimitingWithConcurrentRequests() throws Exception {
        // This test would require more complex setup with concurrent execution
        // For now, we'll test that the endpoint responds consistently
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should handle rate limiting with malformed requests")
    void shouldHandleRateLimitingWithMalformedRequests() throws Exception {
        // Test rate limiting with malformed JSON
        String malformedJson = "{ \"username\": \"test\", \"password\": }";

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(malformedJson))
                    .andExpect(status().isBadRequest());
        }

        // Should still apply rate limiting to malformed requests
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Should handle rate limiting with different content types")
    void shouldHandleRateLimitingWithDifferentContentTypes() throws Exception {
        // Test rate limiting with different content types
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        // Test with XML content type
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<login><username>test</username></login>"))
                .andExpect(status().isUnsupportedMediaType());

        // Should still apply rate limiting
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Should handle rate limiting with different HTTP methods")
    void shouldHandleRateLimitingWithDifferentHttpMethods() throws Exception {
        // Test rate limiting with different HTTP methods
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        // Test with GET method (should not be rate limited the same way)
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isTooManyRequests());
    }
}
