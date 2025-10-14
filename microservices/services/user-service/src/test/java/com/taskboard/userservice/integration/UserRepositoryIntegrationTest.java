package com.taskboard.userservice.integration;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.infrastructure.persistence.adapter.UserRepositoryAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты для UserRepository с реальной базой данных PostgreSQL.
 */
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepositoryAdapter userRepository;

    @Test
    @DisplayName("Should find user by ID from database")
    void shouldFindUserByIdFromDatabase() {
        // Given
        Long userId = 1L;

        // When
        Optional<User> user = userRepository.findById(userId);

        // Then
        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo(userId);
        assertThat(user.get().getUsername()).isEqualTo("testuser1");
        assertThat(user.get().getEmail()).isEqualTo("test1@example.com");
        assertThat(user.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.get().getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Should find user by username from database")
    void shouldFindUserByUsernameFromDatabase() {
        // Given
        String username = "testuser2";

        // When
        Optional<User> user = userRepository.findByUsername(username);

        // Then
        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo(username);
        assertThat(user.get().getEmail()).isEqualTo("test2@example.com");
        assertThat(user.get().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("Should find user by email from database")
    void shouldFindUserByEmailFromDatabase() {
        // Given
        String email = "test1@example.com";

        // When
        Optional<User> user = userRepository.getUserByEmail(email);

        // Then
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo(email);
        assertThat(user.get().getUsername()).isEqualTo("testuser1");
    }

    @Test
    @DisplayName("Should find users by status from database")
    void shouldFindUsersByStatusFromDatabase() {
        // Given
        UserStatus status = UserStatus.ACTIVE;

        // When
        List<User> users = userRepository.findByStatus(status);

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).allMatch(user -> user.getStatus() == UserStatus.ACTIVE);
        assertThat(users).extracting(User::getUsername)
                .containsExactlyInAnyOrder("testuser1", "testuser2");
    }

    @Test
    @DisplayName("Should find users by role from database")
    void shouldFindUsersByRoleFromDatabase() {
        // Given
        UserRole role = UserRole.USER;

        // When
        List<User> users = userRepository.findByRole(role);

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).allMatch(user -> user.getRole() == UserRole.USER);
        assertThat(users).extracting(User::getUsername)
                .containsExactlyInAnyOrder("testuser1", "inactiveuser");
    }

    @Test
    @DisplayName("Should save new user to database")
    void shouldSaveNewUserToDatabase() {
        // Given
        User newUser = User.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi")
                .firstName("New")
                .lastName("User")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
        assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);

        // Verify user was actually saved to database
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getUsername()).isEqualTo("newuser");
    }

    @Test
    @DisplayName("Should update existing user in database")
    void shouldUpdateExistingUserInDatabase() {
        // Given
        Long userId = 1L;
        Optional<User> existingUser = userRepository.findById(userId);
        assertThat(existingUser).isPresent();

        User userToUpdate = existingUser.get().toBuilder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        User updatedUser = userRepository.save(userToUpdate);

        // Then
        assertThat(updatedUser.getId()).isEqualTo(userId);
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        assertThat(updatedUser.getLastName()).isEqualTo("Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");

        // Verify changes were persisted
        Optional<User> retrievedUser = userRepository.findById(userId);
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getFirstName()).isEqualTo("Updated");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Should delete user from database")
    void shouldDeleteUserFromDatabase() {
        // Given
        Long userId = 3L; // inactiveuser
        Optional<User> userToDelete = userRepository.findById(userId);
        assertThat(userToDelete).isPresent();

        // When
        userRepository.delete(userId);

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("Should check if user exists by username")
    void shouldCheckIfUserExistsByUsername() {
        // Given
        String existingUsername = "testuser1";
        String nonExistingUsername = "nonexistent";

        // When & Then
        assertThat(userRepository.existsByUsername(existingUsername)).isTrue();
        assertThat(userRepository.existsByUsername(nonExistingUsername)).isFalse();
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        // Given
        String existingEmail = "test1@example.com";
        String nonExistingEmail = "nonexistent@example.com";

        // When & Then
        assertThat(userRepository.existsByEmail(existingEmail)).isTrue();
        assertThat(userRepository.existsByEmail(nonExistingEmail)).isFalse();
    }

    @Test
    @DisplayName("Should find all users from database")
    void shouldFindAllUsersFromDatabase() {
        // When
        List<User> allUsers = userRepository.findAll();

        // Then
        assertThat(allUsers).hasSize(3);
        assertThat(allUsers).extracting(User::getUsername)
                .containsExactlyInAnyOrder("testuser1", "testuser2", "inactiveuser");
    }

    @Test
    @DisplayName("Should count users by status")
    void shouldCountUsersByStatus() {
        // When
        long activeUsersCount = userRepository.countByStatus(UserStatus.ACTIVE);
        long inactiveUsersCount = userRepository.countByStatus(UserStatus.INACTIVE);

        // Then
        assertThat(activeUsersCount).isEqualTo(2);
        assertThat(inactiveUsersCount).isEqualTo(1);
    }
}
