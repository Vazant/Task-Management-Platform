package com.taskboard.userservice.infrastructure.repository;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryIntegrationTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .password("hashedPassword")
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .build();
    }
    
    @Nested
    @DisplayName("User CRUD Operations Tests")
    class UserCrudOperationsTests {
        
        @Test
        @DisplayName("Should save user successfully")
        void shouldSaveUserSuccessfully() {
            // When
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();
            
            // Then
            assertThat(savedUser.getId()).isNotNull();
            assertThat(savedUser.getUsername()).isEqualTo("testuser");
            assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
            assertThat(savedUser.getCreatedAt()).isNotNull();
            assertThat(savedUser.getUpdatedAt()).isNotNull();
        }
        
        @Test
        @DisplayName("Should find user by ID")
        void shouldFindUserById() {
            // Given
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> foundUser = userRepository.findById(savedUser.getId());
            
            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
            assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        }
        
        @Test
        @DisplayName("Should find user by username")
        void shouldFindUserByUsername() {
            // Given
            userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> foundUser = userRepository.findByUsername("testuser");
            
            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        }
        
        @Test
        @DisplayName("Should find user by email")
        void shouldFindUserByEmail() {
            // Given
            userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> foundUser = userRepository.getUserByEmail("test@example.com");
            
            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        }
        
        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Given
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();
            
            // When
            User userToUpdate = savedUser.toBuilder()
                .firstName("Updated")
                .lastName("Name")
                .status(UserStatus.INACTIVE)
                .build();
            
            User updatedUser = userRepository.save(userToUpdate);
            entityManager.flush();
            entityManager.clear();
            
            // Then
            Optional<User> foundUser = userRepository.findById(savedUser.getId());
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getFirstName()).isEqualTo("Updated");
            assertThat(foundUser.get().getLastName()).isEqualTo("Name");
            assertThat(foundUser.get().isActive()).isFalse();
        }
        
        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Given
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();
            
            // When
            userRepository.deleteById(savedUser.getId());
            entityManager.flush();
            entityManager.clear();
            
            // Then
            Optional<User> foundUser = userRepository.findById(savedUser.getId());
            assertThat(foundUser).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("User Query Methods Tests")
    class UserQueryMethodsTests {
        
        @Test
        @DisplayName("Should find all active users")
        void shouldFindAllActiveUsers() {
            // Given
            User activeUser1 = testUser.toBuilder().username("active1").email("active1@example.com").build();
            User activeUser2 = testUser.toBuilder().username("active2").email("active2@example.com").build();
            User inactiveUser = testUser.toBuilder().username("inactive").email("inactive@example.com").status(UserStatus.INACTIVE).build();
            
            userRepository.saveAll(List.of(activeUser1, activeUser2, inactiveUser));
            entityManager.flush();
            entityManager.clear();
            
            // When
            List<User> activeUsers = userRepository.findByIsActiveTrue();
            
            // Then
            assertThat(activeUsers).hasSize(2);
            assertThat(activeUsers).extracting(User::getUsername).containsExactlyInAnyOrder("active1", "active2");
        }
        
        @Test
        @DisplayName("Should find users by role")
        void shouldFindUsersByRole() {
            // Given
            User adminUser = testUser.toBuilder().username("admin").email("admin@example.com").role(UserRole.ADMIN).build();
            User regularUser1 = testUser.toBuilder().username("user1").email("user1@example.com").role(UserRole.USER).build();
            User regularUser2 = testUser.toBuilder().username("user2").email("user2@example.com").role(UserRole.USER).build();
            
            userRepository.saveAll(List.of(adminUser, regularUser1, regularUser2));
            entityManager.flush();
            entityManager.clear();
            
            // When
            List<User> adminUsers = userRepository.findByRole(UserRole.ADMIN);
            List<User> regularUsers = userRepository.findByRole(UserRole.USER);
            
            // Then
            assertThat(adminUsers).hasSize(1);
            assertThat(adminUsers.get(0).getUsername()).isEqualTo("admin");
            
            assertThat(regularUsers).hasSize(2);
            assertThat(regularUsers).extracting(User::getUsername).containsExactlyInAnyOrder("user1", "user2");
        }
        
        @Test
        @DisplayName("Should check if username exists")
        void shouldCheckIfUsernameExists() {
            // Given
            userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();
            
            // When & Then
            assertThat(userRepository.existsByUsername("testuser")).isTrue();
            assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
        }
        
        @Test
        @DisplayName("Should check if email exists")
        void shouldCheckIfEmailExists() {
            // Given
            userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();
            
            // When & Then
            assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
            assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
        }
        
        @Test
        @DisplayName("Should find users created after date")
        void shouldFindUsersCreatedAfterDate() {
            // Given
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(1);
            
            User oldUser = testUser.toBuilder()
                .username("olduser")
                .email("old@example.com")
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
            
            User newUser = testUser.toBuilder()
                .username("newuser")
                .email("new@example.com")
                .createdAt(LocalDateTime.now())
                .build();
            
            userRepository.saveAll(List.of(oldUser, newUser));
            entityManager.flush();
            entityManager.clear();
            
            // When
            List<User> recentUsers = userRepository.findByCreatedAtAfter(cutoffDate);
            
            // Then
            assertThat(recentUsers).hasSize(1);
            assertThat(recentUsers.get(0).getUsername()).isEqualTo("newuser");
        }
    }
    
    @Nested
    @DisplayName("User Database Constraints Tests")
    class UserDatabaseConstraintsTests {
        
        @Test
        @DisplayName("Should enforce unique username constraint")
        void shouldEnforceUniqueUsernameConstraint() {
            // Given
            userRepository.save(testUser);
            entityManager.flush();
            
            User duplicateUser = testUser.toBuilder()
                .email("different@example.com")
                .build();
            
            // When & Then
            assertThatThrownBy(() -> {
                userRepository.save(duplicateUser);
                entityManager.flush();
            }).hasMessageContaining("duplicate key value violates unique constraint");
        }
        
        @Test
        @DisplayName("Should enforce unique email constraint")
        void shouldEnforceUniqueEmailConstraint() {
            // Given
            userRepository.save(testUser);
            entityManager.flush();
            
            User duplicateUser = testUser.toBuilder()
                .username("differentuser")
                .build();
            
            // When & Then
            assertThatThrownBy(() -> {
                userRepository.save(duplicateUser);
                entityManager.flush();
            }).hasMessageContaining("duplicate key value violates unique constraint");
        }
        
        @Test
        @DisplayName("Should enforce not null constraints")
        void shouldEnforceNotNullConstraints() {
            // Given
            User invalidUser = User.builder()
                .username(null)
                .email("test@example.com")
                .password("hash")
                .role(UserRole.USER)
                .build();
            
            // When & Then
            assertThatThrownBy(() -> {
                userRepository.save(invalidUser);
                entityManager.flush();
            }).hasMessageContaining("not-null property references a null or transient value");
        }
    }
    
    @Nested
    @DisplayName("User Performance Tests")
    class UserPerformanceTests {
        
        @Test
        @DisplayName("Should handle bulk user creation efficiently")
        void shouldHandleBulkUserCreationEfficiently() {
            // Given
            List<User> users = List.of(
                testUser.toBuilder().username("user1").email("user1@example.com").build(),
                testUser.toBuilder().username("user2").email("user2@example.com").build(),
                testUser.toBuilder().username("user3").email("user3@example.com").build(),
                testUser.toBuilder().username("user4").email("user4@example.com").build(),
                testUser.toBuilder().username("user5").email("user5@example.com").build()
            );
            
            // When
            long startTime = System.currentTimeMillis();
            List<User> savedUsers = userRepository.saveAll(users);
            entityManager.flush();
            long endTime = System.currentTimeMillis();
            
            // Then
            assertThat(savedUsers).hasSize(5);
            assertThat(endTime - startTime).isLessThan(1000); // Should complete within 1 second
            
            // Verify all users were saved
            List<User> allUsers = userRepository.findAll();
            assertThat(allUsers).hasSize(5);
        }
        
        @Test
        @DisplayName("Should handle large result set queries efficiently")
        void shouldHandleLargeResultSetQueriesEfficiently() {
            // Given
            List<User> users = List.of();
            for (int i = 0; i < 100; i++) {
                users.add(testUser.toBuilder()
                    .username("user" + i)
                    .email("user" + i + "@example.com")
                    .build());
            }
            
            userRepository.saveAll(users);
            entityManager.flush();
            entityManager.clear();
            
            // When
            long startTime = System.currentTimeMillis();
            List<User> allUsers = userRepository.findAll();
            long endTime = System.currentTimeMillis();
            
            // Then
            assertThat(allUsers).hasSize(100);
            assertThat(endTime - startTime).isLessThan(2000); // Should complete within 2 seconds
        }
    }
}
