package com.taskboard.userservice.domain.service;

import com.taskboard.userservice.domain.exception.UserAlreadyExistsException;
import com.taskboard.userservice.domain.exception.UserDomainException;
import com.taskboard.userservice.domain.exception.UserNotFoundException;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserDomainService.
 * 
 * <p>These tests verify the business logic and behavior of the UserDomainService,
 * including user creation, validation, and business rule enforcement.</p>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDomainService Tests")
class UserDomainServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    private UserDomainService userDomainService;
    
    @BeforeEach
    void setUp() {
        userDomainService = new UserDomainService(userRepository);
    }
    
    /**
     * Helper method to create a User with a specific ID for testing.
     * Uses reflection to set the protected setId method.
     */
    private User createUserWithId(String username, String email, String passwordHash, 
                                String firstName, String lastName, UserRole role, Long id) {
        User user = new User(username, email, passwordHash, firstName, lastName, role);
        try {
            java.lang.reflect.Method setIdMethod = User.class.getDeclaredMethod("setId", Long.class);
            setIdMethod.setAccessible(true);
            setIdMethod.invoke(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user ID", e);
        }
        return user;
    }
    
    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {
        
        @Test
        @DisplayName("Should create user successfully with valid parameters")
        void shouldCreateUserSuccessfullyWithValidParameters() {
            // Given
            String username = "testuser";
            String email = "test@example.com";
            String passwordHash = "hashedPassword123";
            String firstName = "John";
            String lastName = "Doe";
            UserRole role = UserRole.USER;
            
            User expectedUser = new User(username, email, passwordHash, firstName, lastName, role);
            
            when(userRepository.existsByUsername(username)).thenReturn(false);
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(expectedUser);
            
            // When
            User result = userDomainService.createUser(username, email, passwordHash, firstName, lastName, role);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(username);
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getRole()).isEqualTo(role);
            
            verify(userRepository).existsByUsername(username);
            verify(userRepository).existsByEmail(email);
            verify(userRepository).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should create user with default USER role when role is null")
        void shouldCreateUserWithDefaultUserRoleWhenRoleIsNull() {
            // Given
            String username = "testuser";
            String email = "test@example.com";
            String passwordHash = "hashedPassword123";
            String firstName = "John";
            String lastName = "Doe";
            
            User expectedUser = new User(username, email, passwordHash, firstName, lastName, UserRole.USER);
            
            when(userRepository.existsByUsername(username)).thenReturn(false);
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(expectedUser);
            
            // When
            User result = userDomainService.createUser(username, email, passwordHash, firstName, lastName, null);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getRole()).isEqualTo(UserRole.USER);
        }
        
        @Test
        @DisplayName("Should throw UserAlreadyExistsException when username already exists")
        void shouldThrowUserAlreadyExistsExceptionWhenUsernameAlreadyExists() {
            // Given
            String username = "existinguser";
            String email = "test@example.com";
            String passwordHash = "hashedPassword123";
            String firstName = "John";
            String lastName = "Doe";
            
            when(userRepository.existsByUsername(username)).thenReturn(true);
            
            // When & Then
            assertThatThrownBy(() -> userDomainService.createUser(username, email, passwordHash, firstName, lastName, UserRole.USER))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("User already exists with username: " + username);
            
            verify(userRepository).existsByUsername(username);
            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email already exists")
        void shouldThrowUserAlreadyExistsExceptionWhenEmailAlreadyExists() {
            // Given
            String username = "testuser";
            String email = "existing@example.com";
            String passwordHash = "hashedPassword123";
            String firstName = "John";
            String lastName = "Doe";
            
            when(userRepository.existsByUsername(username)).thenReturn(false);
            when(userRepository.existsByEmail(email)).thenReturn(true);
            
            // When & Then
            assertThatThrownBy(() -> userDomainService.createUser(username, email, passwordHash, firstName, lastName, UserRole.USER))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("User already exists with email: " + email);
            
            verify(userRepository).existsByUsername(username);
            verify(userRepository).existsByEmail(email);
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw UserDomainException when username is null")
        void shouldThrowUserDomainExceptionWhenUsernameIsNull() {
            // When & Then
            assertThatThrownBy(() -> userDomainService.createUser(null, "test@example.com", "hash", "John", "Doe", UserRole.USER))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Username cannot be null or empty");
        }
        
        @Test
        @DisplayName("Should throw UserDomainException when email is invalid")
        void shouldThrowUserDomainExceptionWhenEmailIsInvalid() {
            // When & Then
            assertThatThrownBy(() -> userDomainService.createUser("testuser", "invalid-email", "hash", "John", "Doe", UserRole.USER))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Invalid email format");
        }
    }
    
    @Nested
    @DisplayName("User Profile Update Tests")
    class UserProfileUpdateTests {
        
        @Test
        @DisplayName("Should update user profile successfully")
        void shouldUpdateUserProfileSuccessfully() {
            // Given
            Long userId = 1L;
            User existingUser = createUserWithId("testuser", "old@example.com", "hash", "John", "Doe", UserRole.USER, userId);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            
            // When
            User result = userDomainService.updateUserProfile(userId, "Jane", "Smith", "new@example.com");
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("Jane");
            assertThat(result.getLastName()).isEqualTo("Smith");
            assertThat(result.getEmail()).isEqualTo("new@example.com");
            
            verify(userRepository).findById(userId);
            verify(userRepository).existsByEmail("new@example.com");
            verify(userRepository).save(existingUser);
        }
        
        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            Long userId = 999L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            
            // When & Then
            assertThatThrownBy(() -> userDomainService.updateUserProfile(userId, "Jane", "Smith", "new@example.com"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found with ID: " + userId);
            
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email is already in use")
        void shouldThrowUserAlreadyExistsExceptionWhenEmailIsAlreadyInUse() {
            // Given
            Long userId = 1L;
            User existingUser = createUserWithId("testuser", "old@example.com", "hash", "John", "Doe", UserRole.USER, userId);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
            
            // When & Then
            assertThatThrownBy(() -> userDomainService.updateUserProfile(userId, "Jane", "Smith", "existing@example.com"))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("User already exists with email: existing@example.com");
            
            verify(userRepository).findById(userId);
            verify(userRepository).existsByEmail("existing@example.com");
            verify(userRepository, never()).save(any(User.class));
        }
    }
    
    @Nested
    @DisplayName("User Status Management Tests")
    class UserStatusManagementTests {
        
        @Test
        @DisplayName("Should activate user successfully")
        void shouldActivateUserSuccessfully() {
            // Given
            Long userId = 1L;
            User user = createUserWithId("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER, userId);
            user.deactivate();
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            
            // When
            User result = userDomainService.activateUser(userId);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
            
            verify(userRepository).findById(userId);
            verify(userRepository).save(user);
        }
        
        @Test
        @DisplayName("Should deactivate user successfully")
        void shouldDeactivateUserSuccessfully() {
            // Given
            Long userId = 1L;
            User user = createUserWithId("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER, userId);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            
            // When
            User result = userDomainService.deactivateUser(userId);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(UserStatus.INACTIVE);
            
            verify(userRepository).findById(userId);
            verify(userRepository).save(user);
        }
        
        @Test
        @DisplayName("Should block user successfully when user has permission")
        void shouldBlockUserSuccessfullyWhenUserHasPermission() {
            // Given
            Long userId = 1L;
            User userToBlock = createUserWithId("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER, userId);
            User adminUser = createUserWithId("admin", "admin@example.com", "hash", "Admin", "User", UserRole.ADMIN, 2L);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(userToBlock));
            when(userRepository.save(any(User.class))).thenReturn(userToBlock);
            
            // When
            User result = userDomainService.blockUser(userId, adminUser);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(UserStatus.BLOCKED);
            
            verify(userRepository).findById(userId);
            verify(userRepository).save(userToBlock);
        }
        
        @Test
        @DisplayName("Should throw UserDomainException when user tries to block themselves")
        void shouldThrowUserDomainExceptionWhenUserTriesToBlockThemselves() {
            // Given
            Long userId = 1L;
            User user = createUserWithId("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER, userId);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            
            // When & Then
            assertThatThrownBy(() -> userDomainService.blockUser(userId, user))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Users cannot block themselves");
            
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Should throw UserDomainException when non-admin tries to block user")
        void shouldThrowUserDomainExceptionWhenNonAdminTriesToBlockUser() {
            // Given
            Long userId = 1L;
            User userToBlock = createUserWithId("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER, userId);
            User regularUser = createUserWithId("regular", "regular@example.com", "hash", "Regular", "User", UserRole.USER, 2L);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(userToBlock));
            
            // When & Then
            assertThatThrownBy(() -> userDomainService.blockUser(userId, regularUser))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Only administrators can block users");
            
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any(User.class));
        }
    }
    
    @Nested
    @DisplayName("User Role Management Tests")
    class UserRoleManagementTests {
        
        @Test
        @DisplayName("Should update user role successfully when user has permission")
        void shouldUpdateUserRoleSuccessfullyWhenUserHasPermission() {
            // Given
            Long userId = 1L;
            User userToUpdate = createUserWithId("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER, userId);
            User adminUser = createUserWithId("admin", "admin@example.com", "hash", "Admin", "User", UserRole.ADMIN, 2L);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
            when(userRepository.save(any(User.class))).thenReturn(userToUpdate);
            
            // When
            User result = userDomainService.updateUserRole(userId, UserRole.MANAGER, adminUser);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getRole()).isEqualTo(UserRole.MANAGER);
            
            verify(userRepository).findById(userId);
            verify(userRepository).save(userToUpdate);
        }
        
        @Test
        @DisplayName("Should throw UserDomainException when user tries to change their own role")
        void shouldThrowUserDomainExceptionWhenUserTriesToChangeTheirOwnRole() {
            // Given
            Long userId = 1L;
            User user = createUserWithId("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER, userId);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            
            // When & Then
            assertThatThrownBy(() -> userDomainService.updateUserRole(userId, UserRole.MANAGER, user))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Users cannot change their own role");
            
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any(User.class));
        }
    }
}
