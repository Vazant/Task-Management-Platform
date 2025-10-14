package com.taskboard.userservice.application.service;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.infrastructure.config.PasswordEncoderConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PasswordEncoderConfig.class)
@ActiveProfiles("test")
@DisplayName("UserService Integration Tests")
class UserServiceIntegrationTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoderConfig passwordEncoderConfig;
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoderConfig.passwordEncoder());
    }
    
    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {
        
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
            User createdUser = userService.createUser(request);
            entityManager.flush();
            entityManager.clear();
            
            // Then
            assertThat(createdUser.getId()).isNotNull();
            assertThat(createdUser.getUsername()).isEqualTo("newuser");
            assertThat(createdUser.getEmail()).isEqualTo("newuser@example.com");
            assertThat(createdUser.getFirstName()).isEqualTo("New");
            assertThat(createdUser.getLastName()).isEqualTo("User");
            assertThat(createdUser.getRole()).isEqualTo(UserRole.USER);
            assertThat(createdUser.isActive()).isTrue();
            assertThat(createdUser.getPassword()).isNotEqualTo("password123"); // Should be hashed
            assertThat(createdUser.getCreatedAt()).isNotNull();
            assertThat(createdUser.getUpdatedAt()).isNotNull();
            
            // Verify user was saved to database
            Optional<User> savedUser = userRepository.findById(createdUser.getId());
            assertThat(savedUser).isPresent();
            assertThat(savedUser.get().getUsername()).isEqualTo("newuser");
        }
        
        @Test
        @DisplayName("Should throw exception when username already exists")
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            // Given
            User existingUser = User.builder()
                .username("existinguser")
                .email("existing@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
            
            userRepository.save(existingUser);
            entityManager.flush();
            
            CreateUserRequest request = CreateUserRequest.builder()
                .username("existinguser")
                .email("new@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
                .build();
            
            // When & Then
            assertThatThrownBy(() -> userService.createUser(request))
                .hasMessageContaining("Username already exists");
        }
        
        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Given
            User existingUser = User.builder()
                .username("existinguser")
                .email("existing@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
            
            userRepository.save(existingUser);
            entityManager.flush();
            
            CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("existing@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
                .build();
            
            // When & Then
            assertThatThrownBy(() -> userService.createUser(request))
                .hasMessageContaining("Email already exists");
        }
        
        @Test
        @DisplayName("Should create admin user successfully")
        void shouldCreateAdminUserSuccessfully() {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                .username("admin")
                .email("admin@example.com")
                .password("admin123")
                .firstName("Admin")
                .lastName("User")
                .role(UserRole.ADMIN)
                .build();
            
            // When
            User createdUser = userService.createUser(request);
            entityManager.flush();
            entityManager.clear();
            
            // Then
            assertThat(createdUser.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(createdUser.getUsername()).isEqualTo("admin");
        }
    }
    
    @Nested
    @DisplayName("User Retrieval Tests")
    class UserRetrievalTests {
        
        @Test
        @DisplayName("Should find user by ID successfully")
        void shouldFindUserByIdSuccessfully() {
            // Given
            User savedUser = userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> foundUser = userService.findById(savedUser.getId());
            
            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
            assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        }
        
        @Test
        @DisplayName("Should return empty when user not found by ID")
        void shouldReturnEmptyWhenUserNotFoundById() {
            // When
            Optional<User> foundUser = userService.findById(999L);
            
            // Then
            assertThat(foundUser).isEmpty();
        }
        
        @Test
        @DisplayName("Should find user by username successfully")
        void shouldFindUserByUsernameSuccessfully() {
            // Given
            userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> foundUser = userService.findByUsername("testuser");
            
            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        }
        
        @Test
        @DisplayName("Should find user by email successfully")
        void shouldFindUserByEmailSuccessfully() {
            // Given
            userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> foundUser = userService.getUserByEmail("test@example.com");
            
            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        }
        
        @Test
        @DisplayName("Should find all active users")
        void shouldFindAllActiveUsers() {
            // Given
            User activeUser1 = User.builder()
                .username("active1")
                .email("active1@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
            
            User activeUser2 = User.builder()
                .username("active2")
                .email("active2@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
            
            User inactiveUser = User.builder()
                .username("inactive")
                .email("inactive@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.INACTIVE)
                .build();
            
            userRepository.saveAll(List.of(activeUser1, activeUser2, inactiveUser));
            entityManager.flush();
            entityManager.clear();
            
            // When
            List<User> activeUsers = userService.getActiveUsers();
            
            // Then
            assertThat(activeUsers).hasSize(2);
            assertThat(activeUsers).extracting(User::getUsername).containsExactlyInAnyOrder("active1", "active2");
        }
    }
    
    @Nested
    @DisplayName("User Update Tests")
    class UserUpdateTests {
        
        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Given
            User savedUser = userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hash")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());
            entityManager.flush();
            entityManager.clear();
            
            UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .status(UserStatus.INACTIVE)
                .build();
            
            // When
            User updatedUser = userService.updateUser(savedUser.getId(), updateRequest);
            entityManager.flush();
            entityManager.clear();
            
            // Then
            assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
            assertThat(updatedUser.getLastName()).isEqualTo("Name");
            assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
            assertThat(updatedUser.isActive()).isFalse();
            assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
            
            // Verify changes were persisted
            Optional<User> persistedUser = userRepository.findById(savedUser.getId());
            assertThat(persistedUser).isPresent();
            assertThat(persistedUser.get().getFirstName()).isEqualTo("Updated");
        }
        
        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            // Given
            UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .firstName("Updated")
                .build();
            
            // When & Then
            assertThatThrownBy(() -> userService.updateUser(999L, updateRequest))
                .hasMessageContaining("User not found");
        }
        
        @Test
        @DisplayName("Should update password successfully")
        void shouldUpdatePasswordSuccessfully() {
            // Given
            User savedUser = userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("oldHash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());
            entityManager.flush();
            entityManager.clear();
            
            String oldPasswordHash = savedUser.getPassword();
            
            // When
            userService.updatePassword(savedUser.getId(), "newPassword123");
            entityManager.flush();
            entityManager.clear();
            
            // Then
            Optional<User> updatedUser = userRepository.findById(savedUser.getId());
            assertThat(updatedUser).isPresent();
            assertThat(updatedUser.get().getPassword()).isNotEqualTo(oldPasswordHash);
            assertThat(updatedUser.get().getPassword()).isNotEqualTo("newPassword123"); // Should be hashed
        }
    }
    
    @Nested
    @DisplayName("User Deletion Tests")
    class UserDeletionTests {
        
        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Given
            User savedUser = userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());
            entityManager.flush();
            entityManager.clear();
            
            // When
            userService.deleteUser(savedUser.getId());
            entityManager.flush();
            entityManager.clear();
            
            // Then
            Optional<User> deletedUser = userRepository.findById(savedUser.getId());
            assertThat(deletedUser).isEmpty();
        }
        
        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            // When & Then
            assertThatThrownBy(() -> userService.deleteUser(999L))
                .hasMessageContaining("User not found");
        }
    }
    
    @Nested
    @DisplayName("User Authentication Tests")
    class UserAuthenticationTests {
        
        @Test
        @DisplayName("Should authenticate user with correct credentials")
        void shouldAuthenticateUserWithCorrectCredentials() {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .build();
            
            User createdUser = userService.createUser(request);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> authenticatedUser = userService.authenticateUser("testuser", "password123");
            
            // Then
            assertThat(authenticatedUser).isPresent();
            assertThat(authenticatedUser.get().getUsername()).isEqualTo("testuser");
        }
        
        @Test
        @DisplayName("Should not authenticate user with incorrect password")
        void shouldNotAuthenticateUserWithIncorrectPassword() {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .build();
            
            userService.createUser(request);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> authenticatedUser = userService.authenticateUser("testuser", "wrongpassword");
            
            // Then
            assertThat(authenticatedUser).isEmpty();
        }
        
        @Test
        @DisplayName("Should not authenticate inactive user")
        void shouldNotAuthenticateInactiveUser() {
            // Given
            User inactiveUser = User.builder()
                .username("inactive")
                .email("inactive@example.com")
                .password(passwordEncoderConfig.passwordEncoder().encode("password123"))
                .role(UserRole.USER)
                .status(UserStatus.INACTIVE)
                .build();
            
            userRepository.save(inactiveUser);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> authenticatedUser = userService.authenticateUser("inactive", "password123");
            
            // Then
            assertThat(authenticatedUser).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("User Service Business Logic Tests")
    class UserServiceBusinessLogicTests {
        
        @Test
        @DisplayName("Should handle concurrent user creation")
        void shouldHandleConcurrentUserCreation() {
            // Given
            CreateUserRequest request1 = CreateUserRequest.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password123")
                .firstName("User")
                .lastName("One")
                .role(UserRole.USER)
                .build();
            
            CreateUserRequest request2 = CreateUserRequest.builder()
                .username("user2")
                .email("user2@example.com")
                .password("password123")
                .firstName("User")
                .lastName("Two")
                .role(UserRole.USER)
                .build();
            
            // When
            User user1 = userService.createUser(request1);
            User user2 = userService.createUser(request2);
            entityManager.flush();
            entityManager.clear();
            
            // Then
            assertThat(user1.getId()).isNotNull();
            assertThat(user2.getId()).isNotNull();
            assertThat(user1.getId()).isNotEqualTo(user2.getId());
            
            List<User> allUsers = userRepository.findAll();
            assertThat(allUsers).hasSize(2);
        }
        
        @Test
        @DisplayName("Should maintain referential integrity")
        void shouldMaintainReferentialIntegrity() {
            // Given
            User user = userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hash")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<User> foundUser = userService.findById(user.getId());
            
            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getId()).isEqualTo(user.getId());
            assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
        }
    }
}
