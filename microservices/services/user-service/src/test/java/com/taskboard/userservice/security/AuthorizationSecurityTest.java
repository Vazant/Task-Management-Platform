package com.taskboard.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for authorization and access control.
 * Tests role-based access control, resource ownership, and permission checks.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorizationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        
        createUserRequest = CreateUserRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();

        updateUserRequest = UpdateUserRequest.builder()
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .build();
    }

    @Test
    @DisplayName("Should reject unauthenticated access to protected endpoints")
    void shouldRejectUnauthenticatedAccessToProtectedEndpoints() throws Exception {
        // Test user creation without authentication
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isUnauthorized());

        // Test user retrieval without authentication
        mockMvc.perform(get("/api/users/{id}", testUserId))
                .andExpect(status().isUnauthorized());

        // Test user update without authentication
        mockMvc.perform(put("/api/users/{id}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isUnauthorized());

        // Test user deletion without authentication
        mockMvc.perform(delete("/api/users/{id}", testUserId))
                .andExpect(status().isUnauthorized());

        // Test user listing without authentication
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject user access to admin-only endpoints")
    @WithMockUser(roles = "USER")
    void shouldRejectUserAccessToAdminOnlyEndpoints() throws Exception {
        // Test user creation (admin only)
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isForbidden());

        // Test user deletion (admin only)
        mockMvc.perform(delete("/api/users/{id}", testUserId))
                .andExpect(status().isForbidden());

        // Test user listing (admin only)
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow admin access to all endpoints")
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccessToAllEndpoints() throws Exception {
        // Test user creation (admin allowed)
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated());

        // Test user retrieval (admin allowed)
        mockMvc.perform(get("/api/users/{id}", testUserId))
                .andExpect(status().isOk());

        // Test user update (admin allowed)
        mockMvc.perform(put("/api/users/{id}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());

        // Test user deletion (admin allowed)
        mockMvc.perform(delete("/api/users/{id}", testUserId))
                .andExpect(status().isNoContent());

        // Test user listing (admin allowed)
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow users to access their own resources")
    @WithMockUser(username = "testuser", roles = "USER")
    void shouldAllowUsersToAccessTheirOwnResources() throws Exception {
        // Test user retrieval (own resource)
        mockMvc.perform(get("/api/users/{id}", testUserId))
                .andExpect(status().isOk());

        // Test user update (own resource)
        mockMvc.perform(put("/api/users/{id}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should reject users from accessing other users' resources")
    @WithMockUser(username = "otheruser", roles = "USER")
    void shouldRejectUsersFromAccessingOtherUsersResources() throws Exception {
        Long otherUserId = 999L;

        // Test user retrieval (other user's resource)
        mockMvc.perform(get("/api/users/{id}", otherUserId))
                .andExpect(status().isForbidden());

        // Test user update (other user's resource)
        mockMvc.perform(put("/api/users/{id}", otherUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should reject requests with invalid user IDs")
    @WithMockUser(roles = "USER")
    void shouldRejectRequestsWithInvalidUserIds() throws Exception {
        // Test with negative user ID
        mockMvc.perform(get("/api/users/{id}", -1L))
                .andExpect(status().isBadRequest());

        // Test with zero user ID
        mockMvc.perform(get("/api/users/{id}", 0L))
                .andExpect(status().isBadRequest());

        // Test with non-numeric user ID
        mockMvc.perform(get("/api/users/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject requests with malformed user IDs")
    @WithMockUser(roles = "USER")
    void shouldRejectRequestsWithMalformedUserIds() throws Exception {
        // Test with UUID instead of Long
        String uuid = UUID.randomUUID().toString();
        mockMvc.perform(get("/api/users/{id}", uuid))
                .andExpect(status().isBadRequest());

        // Test with special characters
        mockMvc.perform(get("/api/users/{id}", "1'; DROP TABLE users; --"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle CSRF protection correctly")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleCsrfProtectionCorrectly() throws Exception {
        // Test POST request without CSRF token (should be rejected)
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isForbidden());

        // Test POST request with CSRF token (should be allowed)
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should reject requests with invalid HTTP methods")
    @WithMockUser(roles = "ADMIN")
    void shouldRejectRequestsWithInvalidHttpMethods() throws Exception {
        // Test PATCH method (not allowed)
        mockMvc.perform(patch("/api/users/{id}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isMethodNotAllowed());

        // Test HEAD method (not allowed)
        mockMvc.perform(head("/api/users/{id}", testUserId))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Should reject requests with invalid content types")
    @WithMockUser(roles = "ADMIN")
    void shouldRejectRequestsWithInvalidContentTypes() throws Exception {
        // Test with XML content type
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<user><username>test</username></user>"))
                .andExpect(status().isUnsupportedMediaType());

        // Test with plain text content type
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("test data"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Should handle role hierarchy correctly")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldHandleRoleHierarchyCorrectly() throws Exception {
        // User with both USER and ADMIN roles should have admin access
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should reject requests with excessive parameters")
    @WithMockUser(roles = "USER")
    void shouldRejectRequestsWithExcessiveParameters() throws Exception {
        // Test with too many query parameters
        StringBuilder queryParams = new StringBuilder("?");
        for (int i = 0; i < 100; i++) {
            queryParams.append("param").append(i).append("=value").append(i);
            if (i < 99) queryParams.append("&");
        }

        mockMvc.perform(get("/api/users" + queryParams.toString()))
                .andExpect(status().isBadRequest());
    }
}
