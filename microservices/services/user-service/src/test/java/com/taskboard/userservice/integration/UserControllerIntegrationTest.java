package com.taskboard.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.application.dto.*;
import com.taskboard.userservice.application.usecase.*;
import com.taskboard.userservice.infrastructure.web.dto.ApiResponse;
import com.taskboard.userservice.infrastructure.web.dto.LoginRequest;
import com.taskboard.userservice.infrastructure.web.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для UserController.
 * Тестирует REST API endpoints для управления пользователями.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("integration-test")
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @MockBean
    private GetUserUseCase getUserUseCase;

    @MockBean
    private UpdateUserUseCase updateUserUseCase;

    @MockBean
    private DeleteUserUseCase deleteUserUseCase;

    @MockBean
    private AuthenticateUserUseCase authenticateUserUseCase;

    private MockMvc mockMvc;

    @Override
    protected void setUp() {
        super.setUp();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        CreateUserRequest request = TestDataFactory.createUserRequest("testuser", "test@example.com");
        CreateUserResponse response = CreateUserResponse.builder()
                .userId(1L)
                .username("testuser")
                .email("test@example.com")
                .message("User created successfully")
                .build();

        when(createUserUseCase.execute(any(CreateUserRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    @DisplayName("Should return 403 when non-admin tries to create user")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenNonAdminTriesToCreateUser() throws Exception {
        // Given
        CreateUserRequest request = TestDataFactory.createUserRequest("testuser", "test@example.com");

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 401 when unauthenticated user tries to create user")
    void shouldReturn401WhenUnauthenticatedUserTriesToCreateUser() throws Exception {
        // Given
        CreateUserRequest request = TestDataFactory.createUserRequest("testuser", "test@example.com");

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should get user successfully")
    @WithMockUser(roles = "USER")
    void shouldGetUserSuccessfully() throws Exception {
        // Given
        Long userId = 1L;
        GetUserResponse response = GetUserResponse.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role("USER")
                .isActive(true)
                .build();

        when(getUserUseCase.execute(any(GetUserRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"));
    }

    @Test
    @DisplayName("Should return 404 when user not found")
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenUserNotFound() throws Exception {
        // Given
        Long userId = 999L;
        when(getUserUseCase.execute(any(GetUserRequest.class)))
                .thenThrow(new IllegalArgumentException("User not found with ID: " + userId));

        // When & Then
        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("User not found with ID: " + userId));
    }

    @Test
    @DisplayName("Should update user successfully")
    @WithMockUser(roles = "USER")
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given
        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .build();

        UpdateUserResponse response = UpdateUserResponse.builder()
                .userId(userId)
                .username("testuser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("Name")
                .message("User updated successfully")
                .build();

        when(updateUserUseCase.execute(any(UpdateUserRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.firstName").value("Updated"))
                .andExpect(jsonPath("$.data.lastName").value("Name"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    @DisplayName("Should delete user successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserSuccessfully() throws Exception {
        // Given
        Long userId = 1L;
        DeleteUserResponse response = DeleteUserResponse.builder()
                .userId(userId)
                .message("User deleted successfully")
                .build();

        when(deleteUserUseCase.execute(any(DeleteUserRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.message").value("User deleted successfully"))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @DisplayName("Should return 400 when validation fails")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenValidationFails() throws Exception {
        // Given
        CreateUserRequest request = TestDataFactory.createInvalidUserRequest();

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors[0].field").exists())
                .andExpect(jsonPath("$.error.validationErrors[0].message").exists());
    }

    @Test
    @DisplayName("Should handle internal server error")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleInternalServerError() throws Exception {
        // Given
        CreateUserRequest request = TestDataFactory.createUserRequest("testuser", "test@example.com");
        when(createUserUseCase.execute(any(CreateUserRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Failed to create user"));
    }
}