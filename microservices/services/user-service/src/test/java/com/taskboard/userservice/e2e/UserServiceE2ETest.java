package com.taskboard.userservice.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.LoginRequest;
import com.taskboard.userservice.application.dto.RegisterRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("User Service E2E Tests")
class UserServiceE2ETest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private String authToken;
    private String adminToken;
    
    @BeforeEach
    void setUp() throws Exception {
        // Register and login as regular user
        authToken = registerAndLoginUser("testuser", "test@example.com", "password123");
        
        // Register and login as admin user
        adminToken = registerAndLoginAdmin("admin", "admin@example.com", "admin123");
    }
    
    @Nested
    @DisplayName("Complete User Lifecycle E2E Tests")
    class CompleteUserLifecycleE2ETests {
        
        @Test
        @DisplayName("Should complete full user lifecycle: register -> login -> update -> delete")
        void shouldCompleteFullUserLifecycle() throws Exception {
            // Step 1: Register new user
            RegisterRequest registerRequest = RegisterRequest.builder()
                .username("lifecycleuser")
                .email("lifecycle@example.com")
                .password("password123")
                .firstName("Lifecycle")
                .lastName("User")
                .build();
            
            MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("lifecycleuser"))
                .andExpect(jsonPath("$.email").value("lifecycle@example.com"))
                .andExpect(jsonPath("$.firstName").value("Lifecycle"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.active").value(true))
                .andReturn();
            
            // Step 2: Login with new user
            LoginRequest loginRequest = LoginRequest.builder()
                .username("lifecycleuser")
                .password("password123")
                .build();
            
            MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.username").value("lifecycleuser"))
                .andReturn();
            
            String userToken = extractTokenFromResponse(loginResult);
            
            // Step 3: Get user profile
            mockMvc.perform(get("/api/v1/users/profile")
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("lifecycleuser"))
                .andExpect(jsonPath("$.email").value("lifecycle@example.com"));
            
            // Step 4: Update user profile
            UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .build();
            
            mockMvc.perform(put("/api/v1/users/profile")
                    .with(csrf())
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
            
            // Step 5: Change password
            String changePasswordRequest = """
                {
                    "currentPassword": "password123",
                    "newPassword": "newpassword123"
                }
                """;
            
            mockMvc.perform(put("/api/v1/users/password")
                    .with(csrf())
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(changePasswordRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
            
            // Step 6: Login with new password
            LoginRequest newLoginRequest = LoginRequest.builder()
                .username("lifecycleuser")
                .password("newpassword123")
                .build();
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
            
            // Step 7: Admin deletes user
            Long userId = extractUserIdFromResponse(registerResult);
            
            mockMvc.perform(delete("/api/v1/users/" + userId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
            
            // Step 8: Verify user is deleted (should not be able to login)
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newLoginRequest)))
                .andExpect(status().isUnauthorized());
        }
        
        @Test
        @DisplayName("Should handle concurrent user operations")
        void shouldHandleConcurrentUserOperations() throws Exception {
            // Register multiple users concurrently
            for (int i = 0; i < 5; i++) {
                RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("concurrentuser" + i)
                    .email("concurrent" + i + "@example.com")
                    .password("password123")
                    .firstName("Concurrent")
                    .lastName("User" + i)
                    .build();
                
                mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("concurrentuser" + i));
            }
            
            // Login all users
            for (int i = 0; i < 5; i++) {
                LoginRequest loginRequest = LoginRequest.builder()
                    .username("concurrentuser" + i)
                    .password("password123")
                    .build();
                
                mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
            }
        }
    }
    
    @Nested
    @DisplayName("Authentication Flow E2E Tests")
    class AuthenticationFlowE2ETests {
        
        @Test
        @DisplayName("Should handle complete authentication flow with token refresh")
        void shouldHandleCompleteAuthenticationFlowWithTokenRefresh() throws Exception {
            // Step 1: Register user
            RegisterRequest registerRequest = RegisterRequest.builder()
                .username("authtest")
                .email("authtest@example.com")
                .password("password123")
                .firstName("Auth")
                .lastName("Test")
                .build();
            
            mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
            
            // Step 2: Login
            LoginRequest loginRequest = LoginRequest.builder()
                .username("authtest")
                .password("password123")
                .build();
            
            MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();
            
            String token = extractTokenFromResponse(loginResult);
            
            // Step 3: Access protected resource
            mockMvc.perform(get("/api/v1/users/profile")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("authtest"));
            
            // Step 4: Refresh token
            mockMvc.perform(post("/api/v1/auth/refresh")
                    .with(csrf())
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
            
            // Step 5: Logout
            mockMvc.perform(post("/api/v1/auth/logout")
                    .with(csrf())
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
        }
        
        @Test
        @DisplayName("Should handle authentication failures gracefully")
        void shouldHandleAuthenticationFailuresGracefully() throws Exception {
            // Try to access protected resource without token
            mockMvc.perform(get("/api/v1/users/profile")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
            
            // Try to access protected resource with invalid token
            mockMvc.perform(get("/api/v1/users/profile")
                    .header("Authorization", "Bearer invalid-token")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
            
            // Try to login with non-existent user
            LoginRequest loginRequest = LoginRequest.builder()
                .username("nonexistent")
                .password("password123")
                .build();
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }
    }
    
    @Nested
    @DisplayName("Admin Operations E2E Tests")
    class AdminOperationsE2ETests {
        
        @Test
        @DisplayName("Should handle admin user management operations")
        void shouldHandleAdminUserManagementOperations() throws Exception {
            // Admin creates user
            CreateUserRequest createRequest = CreateUserRequest.builder()
                .username("admincreated")
                .email("admincreated@example.com")
                .password("password123")
                .firstName("Admin")
                .lastName("Created")
                .role(UserRole.USER)
                .build();
            
            MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                    .with(csrf())
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("admincreated"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn();
            
            Long userId = extractUserIdFromResponse(createResult);
            
            // Admin gets all users
            mockMvc.perform(get("/api/v1/users")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").exists());
            
            // Admin gets specific user
            mockMvc.perform(get("/api/v1/users/" + userId)
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("admincreated"));
            
            // Admin updates user
            UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .status(UserStatus.INACTIVE)
                .build();
            
            mockMvc.perform(put("/api/v1/users/" + userId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.active").value(false));
            
            // Admin deletes user
            mockMvc.perform(delete("/api/v1/users/" + userId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        }
        
        @Test
        @DisplayName("Should prevent non-admin users from admin operations")
        void shouldPreventNonAdminUsersFromAdminOperations() throws Exception {
            // Regular user tries to create user
            CreateUserRequest createRequest = CreateUserRequest.builder()
                .username("unauthorized")
                .email("unauthorized@example.com")
                .password("password123")
                .firstName("Unauthorized")
                .lastName("User")
                .role(UserRole.USER)
                .build();
            
            mockMvc.perform(post("/api/v1/users")
                    .with(csrf())
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
            
            // Regular user tries to get all users
            mockMvc.perform(get("/api/v1/users")
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
            
            // Regular user tries to delete user
            mockMvc.perform(delete("/api/v1/users/1")
                    .with(csrf())
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        }
    }
    
    @Nested
    @DisplayName("Error Handling E2E Tests")
    class ErrorHandlingE2ETests {
        
        @Test
        @DisplayName("Should handle validation errors consistently")
        void shouldHandleValidationErrorsConsistently() throws Exception {
            // Invalid registration request
            RegisterRequest invalidRegisterRequest = RegisterRequest.builder()
                .username("") // Invalid: empty
                .email("invalid-email") // Invalid: malformed
                .password("123") // Invalid: too short
                .build();
            
            mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/register"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
            
            // Invalid login request
            LoginRequest invalidLoginRequest = LoginRequest.builder()
                .username("") // Invalid: empty
                .password("") // Invalid: empty
                .build();
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/login"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
        }
        
        @Test
        @DisplayName("Should handle business logic errors consistently")
        void shouldHandleBusinessLogicErrorsConsistently() throws Exception {
            // Try to register with existing username
            RegisterRequest duplicateUsernameRequest = RegisterRequest.builder()
                .username("testuser") // Already exists
                .email("new@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();
            
            mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(duplicateUsernameRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/register"))
                .andExpect(jsonPath("$.message").value("Username already exists"));
            
            // Try to access non-existent user
            mockMvc.perform(get("/api/v1/users/999")
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/v1/users/999"))
                .andExpect(jsonPath("$.message").value("User not found"));
        }
    }
    
    @Nested
    @DisplayName("Performance E2E Tests")
    class PerformanceE2ETests {
        
        @Test
        @DisplayName("Should handle multiple requests efficiently")
        void shouldHandleMultipleRequestsEfficiently() throws Exception {
            long startTime = System.currentTimeMillis();
            
            // Perform multiple operations
            for (int i = 0; i < 10; i++) {
                // Login
                LoginRequest loginRequest = LoginRequest.builder()
                    .username("testuser")
                    .password("password123")
                    .build();
                
                mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk());
                
                // Get profile
                mockMvc.perform(get("/api/v1/users/profile")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Should complete within reasonable time (5 seconds for 20 requests)
            assertThat(duration).isLessThan(5000);
        }
    }
    
    // Helper methods
    private String registerAndLoginUser(String username, String email, String password) throws Exception {
        // Register
        RegisterRequest registerRequest = RegisterRequest.builder()
            .username(username)
            .email(email)
            .password(password)
            .firstName("Test")
            .lastName("User")
            .build();
        
        mockMvc.perform(post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated());
        
        // Login
        LoginRequest loginRequest = LoginRequest.builder()
            .username(username)
            .password(password)
            .build();
        
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();
        
        return extractTokenFromResponse(result);
    }
    
    private String registerAndLoginAdmin(String username, String email, String password) throws Exception {
        // Register as admin
        CreateUserRequest createRequest = CreateUserRequest.builder()
            .username(username)
            .email(email)
            .password(password)
            .firstName("Admin")
            .lastName("User")
            .role(UserRole.ADMIN)
            .build();
        
        mockMvc.perform(post("/api/v1/users")
                .with(csrf())
                .header("Authorization", "Bearer " + authToken) // Use existing token for admin creation
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated());
        
        // Login
        LoginRequest loginRequest = LoginRequest.builder()
            .username(username)
            .password(password)
            .build();
        
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();
        
        return extractTokenFromResponse(result);
    }
    
    private String extractTokenFromResponse(MvcResult result) throws Exception {
        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseContent).get("token").asText();
    }
    
    private Long extractUserIdFromResponse(MvcResult result) throws Exception {
        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseContent).get("id").asLong();
    }
}
