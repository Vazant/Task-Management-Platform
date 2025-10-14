package com.taskboard.userservice.integration;

import com.taskboard.userservice.application.dto.*;
import com.taskboard.userservice.application.usecase.*;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.model.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Интеграционные тесты для Use Cases с реальной базой данных.
 */
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserUseCaseIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private GetUserUseCase getUserUseCase;

    @Autowired
    private UpdateUserUseCase updateUserUseCase;

    @Autowired
    private DeleteUserUseCase deleteUserUseCase;

    @Autowired
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Test
    @DisplayName("Should create user successfully with valid data")
    void shouldCreateUserSuccessfullyWithValidData() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
                .build();

        // When
        CreateUserResponse response = createUserUseCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isNotNull();
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getEmail()).isEqualTo("newuser@example.com");
        assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing username")
    void shouldThrowExceptionWhenCreatingUserWithExistingUsername() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser1") // Already exists in test data
                .email("newemail@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
                .build();

        // When & Then
        assertThatThrownBy(() -> createUserUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing email")
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newusername")
                .email("test1@example.com") // Already exists in test data
                .password("password123")
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
                .build();

        // When & Then
        assertThatThrownBy(() -> createUserUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("Should get user successfully by ID")
    void shouldGetUserSuccessfullyById() {
        // Given
        Long userId = 1L;

        // When
        GetUserResponse response = getUserUseCase.execute(GetUserRequest.builder()
                .userId(userId)
                .build());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("testuser1");
        assertThat(response.getEmail()).isEqualTo("test1@example.com");
        assertThat(response.getFirstName()).isEqualTo("Test");
        assertThat(response.getLastName()).isEqualTo("User1");
        assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent user")
    void shouldThrowExceptionWhenGettingNonExistentUser() {
        // Given
        Long nonExistentUserId = 999L;

        // When & Then
        assertThatThrownBy(() -> getUserUseCase.execute(GetUserRequest.builder()
                .userId(nonExistentUserId)
                .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .userId(userId)
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .build();

        // When
        UpdateUserResponse response = updateUserUseCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getFirstName()).isEqualTo("Updated");
        assertThat(response.getLastName()).isEqualTo("Name");
        assertThat(response.getEmail()).isEqualTo("updated@example.com");
        assertThat(response.getUpdatedAt()).isNotNull();

        // Verify changes were persisted
        GetUserResponse updatedUser = getUserUseCase.execute(GetUserRequest.builder()
                .userId(userId)
                .build());
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        Long nonExistentUserId = 999L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .userId(nonExistentUserId)
                .firstName("Updated")
                .build();

        // When & Then
        assertThatThrownBy(() -> updateUserUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should throw exception when updating user with existing email")
    void shouldThrowExceptionWhenUpdatingUserWithExistingEmail() {
        // Given
        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .userId(userId)
                .email("test2@example.com") // Email of another user
                .build();

        // When & Then
        assertThatThrownBy(() -> updateUserUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        Long userId = 3L; // inactiveuser

        // When
        DeleteUserResponse response = deleteUserUseCase.execute(DeleteUserRequest.builder()
                .userId(userId)
                .build());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.isDeleted()).isTrue();

        // Verify user was actually deleted
        assertThatThrownBy(() -> getUserUseCase.execute(GetUserRequest.builder()
                .userId(userId)
                .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        Long nonExistentUserId = 999L;

        // When & Then
        assertThatThrownBy(() -> deleteUserUseCase.execute(DeleteUserRequest.builder()
                .userId(nonExistentUserId)
                .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should authenticate user successfully with valid credentials")
    void shouldAuthenticateUserSuccessfullyWithValidCredentials() {
        // Given
        AuthenticateUserRequest request = AuthenticateUserRequest.builder()
                .username("testuser1")
                .password("password123") // Default password in test data
                .build();

        // When
        AuthenticateUserResponse response = authenticateUserUseCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser1");
        assertThat(response.getToken()).isNotNull();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isPositive();
    }

    @Test
    @DisplayName("Should fail authentication with invalid username")
    void shouldFailAuthenticationWithInvalidUsername() {
        // Given
        AuthenticateUserRequest request = AuthenticateUserRequest.builder()
                .username("nonexistent")
                .password("password123")
                .build();

        // When
        AuthenticateUserResponse response = authenticateUserUseCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isAuthenticated()).isFalse();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getToken()).isNull();
        assertThat(response.getErrorMessage()).isNotNull();
    }

    @Test
    @DisplayName("Should fail authentication with invalid password")
    void shouldFailAuthenticationWithInvalidPassword() {
        // Given
        AuthenticateUserRequest request = AuthenticateUserRequest.builder()
                .username("testuser1")
                .password("wrongpassword")
                .build();

        // When
        AuthenticateUserResponse response = authenticateUserUseCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isAuthenticated()).isFalse();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getToken()).isNull();
        assertThat(response.getErrorMessage()).isNotNull();
    }

    @Test
    @DisplayName("Should fail authentication for inactive user")
    void shouldFailAuthenticationForInactiveUser() {
        // Given
        AuthenticateUserRequest request = AuthenticateUserRequest.builder()
                .username("inactiveuser")
                .password("password123")
                .build();

        // When
        AuthenticateUserResponse response = authenticateUserUseCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isAuthenticated()).isFalse();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getToken()).isNull();
        assertThat(response.getErrorMessage()).contains("inactive");
    }
}
