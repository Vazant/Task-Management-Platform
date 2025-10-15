package com.taskboard.userservice.e2e;

import com.taskboard.userservice.application.dto.LoginRequest;
import com.taskboard.userservice.application.dto.RegisterRequest;
import com.taskboard.userservice.e2e.util.E2ETestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E тесты для AuthController.
 * 
 * <p>Тестирует полный цикл аутентификации:
 * <ul>
 *   <li>Регистрация пользователя</li>
 *   <li>Вход в систему</li>
 *   <li>Обновление токена</li>
 *   <li>Выход из системы</li>
 * </ul>
 * </p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@DisplayName("AuthController E2E Tests")
class AuthControllerE2ETest extends BaseE2ETest {
    
    private static final String AUTH_BASE_URL = "/api/v1/auth";
    
    @Nested
    @DisplayName("User Registration Tests")
    class RegistrationTests {
        
        @Test
        @DisplayName("Should register new user successfully")
        void shouldRegisterNewUserSuccessfully() throws Exception {
            // Given
            String uniqueUsername = E2ETestDataFactory.createUniqueUsername("testuser");
            String uniqueEmail = E2ETestDataFactory.createUniqueEmail("test");
            
            RegisterRequest request = E2ETestDataFactory.createRegisterRequest(
                uniqueUsername, uniqueEmail, E2ETestDataFactory.TEST_PASSWORD
            );
            
            // When & Then
            mockMvc.perform(post(AUTH_BASE_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.username").value(uniqueUsername))
                    .andExpect(jsonPath("$.data.email").value(uniqueEmail))
                    .andExpect(jsonPath("$.data.firstName").value(E2ETestDataFactory.TEST_FIRST_NAME))
                    .andExpect(jsonPath("$.data.lastName").value(E2ETestDataFactory.TEST_LAST_NAME))
                    .andExpect(jsonPath("$.data.role").value("USER"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.data.id").exists())
                    .andExpect(jsonPath("$.data.createdAt").exists());
        }
        
        @Test
        @DisplayName("Should fail to register user with existing username")
        void shouldFailToRegisterUserWithExistingUsername() throws Exception {
            // Given
            String username = E2ETestDataFactory.createUniqueUsername("duplicate");
            String email1 = E2ETestDataFactory.createUniqueEmail("user1");
            String email2 = E2ETestDataFactory.createUniqueEmail("user2");
            
            RegisterRequest firstRequest = E2ETestDataFactory.createRegisterRequest(username, email1, E2ETestDataFactory.TEST_PASSWORD);
            RegisterRequest secondRequest = E2ETestDataFactory.createRegisterRequest(username, email2, E2ETestDataFactory.TEST_PASSWORD);
            
            // Register first user
            mockMvc.perform(post(AUTH_BASE_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(firstRequest)))
                    .andExpect(status().isCreated());
            
            // When & Then - try to register with same username
            mockMvc.perform(post(AUTH_BASE_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(secondRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
        
        @Test
        @DisplayName("Should fail to register user with invalid data")
        void shouldFailToRegisterUserWithInvalidData() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                    .username("") // Invalid: empty username
                    .email("invalid-email") // Invalid: malformed email
                    .password("123") // Invalid: too short password
                    .firstName("") // Invalid: empty first name
                    .lastName("") // Invalid: empty last name
                    .build();
            
            // When & Then
            mockMvc.perform(post(AUTH_BASE_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
    }
    
    @Nested
    @DisplayName("User Login Tests")
    class LoginTests {
        
        @Test
        @DisplayName("Should login user successfully")
        void shouldLoginUserSuccessfully() throws Exception {
            // Given
            String username = E2ETestDataFactory.createUniqueUsername("loginuser");
            String email = E2ETestDataFactory.createUniqueEmail("loginuser");
            
            // Register user first
            RegisterRequest registerRequest = E2ETestDataFactory.createRegisterRequest(username, email, E2ETestDataFactory.TEST_PASSWORD);
            mockMvc.perform(post(AUTH_BASE_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(registerRequest)))
                    .andExpect(status().isCreated());
            
            // Login request
            LoginRequest loginRequest = E2ETestDataFactory.createLoginRequest(username, E2ETestDataFactory.TEST_PASSWORD);
            
            // When & Then
            mockMvc.perform(post(AUTH_BASE_URL + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists())
                    .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.data.expiresIn").exists())
                    .andExpect(jsonPath("$.data.user.username").value(username))
                    .andExpect(jsonPath("$.data.user.email").value(email));
        }
        
        @Test
        @DisplayName("Should fail to login with invalid credentials")
        void shouldFailToLoginWithInvalidCredentials() throws Exception {
            // Given
            LoginRequest loginRequest = E2ETestDataFactory.createLoginRequest("nonexistent", "wrongpassword");
            
            // When & Then
            mockMvc.perform(post(AUTH_BASE_URL + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
        
        @Test
        @DisplayName("Should fail to login with empty credentials")
        void shouldFailToLoginWithEmptyCredentials() throws Exception {
            // Given
            LoginRequest loginRequest = LoginRequest.builder()
                    .username("")
                    .password("")
                    .build();
            
            // When & Then
            mockMvc.perform(post(AUTH_BASE_URL + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
    }
    
    @Nested
    @DisplayName("Token Refresh Tests")
    class TokenRefreshTests {
        
        @Test
        @DisplayName("Should refresh token successfully")
        void shouldRefreshTokenSuccessfully() throws Exception {
            // Given
            String username = E2ETestDataFactory.createUniqueUsername("refreshuser");
            String email = E2ETestDataFactory.createUniqueEmail("refreshuser");
            
            // Register and login user
            RegisterRequest registerRequest = E2ETestDataFactory.createRegisterRequest(username, email, E2ETestDataFactory.TEST_PASSWORD);
            mockMvc.perform(post(AUTH_BASE_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(registerRequest)))
                    .andExpect(status().isCreated());
            
            LoginRequest loginRequest = E2ETestDataFactory.createLoginRequest(username, E2ETestDataFactory.TEST_PASSWORD);
            ResultActions loginResult = mockMvc.perform(post(AUTH_BASE_URL + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(loginRequest)))
                    .andExpect(status().isOk());
            
            // Extract refresh token from login response
            String loginResponse = loginResult.andReturn().getResponse().getContentAsString();
            String refreshToken = extractRefreshToken(loginResponse);
            
            // When & Then
            mockMvc.perform(post(AUTH_BASE_URL + "/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + refreshToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists())
                    .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.data.expiresIn").exists());
        }
        
        @Test
        @DisplayName("Should fail to refresh token with invalid token")
        void shouldFailToRefreshTokenWithInvalidToken() throws Exception {
            // When & Then
            mockMvc.perform(post(AUTH_BASE_URL + "/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
    }
    
    @Nested
    @DisplayName("User Logout Tests")
    class LogoutTests {
        
        @Test
        @DisplayName("Should logout user successfully")
        void shouldLogoutUserSuccessfully() throws Exception {
            // Given
            String username = E2ETestDataFactory.createUniqueUsername("logoutuser");
            String email = E2ETestDataFactory.createUniqueEmail("logoutuser");
            
            // Register and login user
            RegisterRequest registerRequest = E2ETestDataFactory.createRegisterRequest(username, email, E2ETestDataFactory.TEST_PASSWORD);
            mockMvc.perform(post(AUTH_BASE_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(registerRequest)))
                    .andExpect(status().isCreated());
            
            LoginRequest loginRequest = E2ETestDataFactory.createLoginRequest(username, E2ETestDataFactory.TEST_PASSWORD);
            ResultActions loginResult = mockMvc.perform(post(AUTH_BASE_URL + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(loginRequest)))
                    .andExpect(status().isOk());
            
            // Extract access token from login response
            String loginResponse = loginResult.andReturn().getResponse().getContentAsString();
            String accessToken = extractAccessToken(loginResponse);
            
            // When & Then
            mockMvc.perform(post(AUTH_BASE_URL + "/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User logged out successfully"));
        }
    }
    
    /**
     * Извлекает access token из JSON ответа.
     * 
     * @param jsonResponse JSON ответ
     * @return access token
     */
    private String extractAccessToken(String jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse)
                    .path("data")
                    .path("accessToken")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract access token", e);
        }
    }
    
    /**
     * Извлекает refresh token из JSON ответа.
     * 
     * @param jsonResponse JSON ответ
     * @return refresh token
     */
    private String extractRefreshToken(String jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse)
                    .path("data")
                    .path("refreshToken")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract refresh token", e);
        }
    }
}
