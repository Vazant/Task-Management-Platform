package com.taskboard.userservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for User domain entity.
 * 
 * <p>These tests verify the business logic and behavior of the User entity,
 * including validation, state transitions, and business rule enforcement.</p>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@DisplayName("User Domain Entity Tests")
class UserTest {
    
    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {
        
        @Test
        @DisplayName("Should create user with valid parameters")
        void shouldCreateUserWithValidParameters() {
            // Given
            String username = "testuser";
            String email = "test@example.com";
            String passwordHash = "hashedPassword123";
            String firstName = "John";
            String lastName = "Doe";
            UserRole role = UserRole.USER;
            
            // When
            User user = new User(username, email, passwordHash, firstName, lastName, role);
            
            // Then
            assertThat(user.getUsername()).isEqualTo(username);
            assertThat(user.getEmail()).isEqualTo(email);
            assertThat(user.getPasswordHash()).isEqualTo(passwordHash);
            assertThat(user.getFirstName()).isEqualTo(firstName);
            assertThat(user.getLastName()).isEqualTo(lastName);
            assertThat(user.getRole()).isEqualTo(role);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNotNull();
            assertThat(user.isEmailVerified()).isFalse();
        }
        
        @Test
        @DisplayName("Should throw exception when username is null")
        void shouldThrowExceptionWhenUsernameIsNull() {
            // When & Then
            assertThatThrownBy(() -> new User(null, "test@example.com", "hash", "John", "Doe", UserRole.USER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Username cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // When & Then
            assertThatThrownBy(() -> new User("testuser", null, "hash", "John", "Doe", UserRole.USER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Email cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when password hash is null")
        void shouldThrowExceptionWhenPasswordHashIsNull() {
            // When & Then
            assertThatThrownBy(() -> new User("testuser", "test@example.com", null, "John", "Doe", UserRole.USER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Password hash cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when first name is null")
        void shouldThrowExceptionWhenFirstNameIsNull() {
            // When & Then
            assertThatThrownBy(() -> new User("testuser", "test@example.com", "hash", null, "Doe", UserRole.USER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("First name cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when last name is null")
        void shouldThrowExceptionWhenLastNameIsNull() {
            // When & Then
            assertThatThrownBy(() -> new User("testuser", "test@example.com", "hash", "John", null, UserRole.USER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Last name cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when role is null")
        void shouldThrowExceptionWhenRoleIsNull() {
            // When & Then
            assertThatThrownBy(() -> new User("testuser", "test@example.com", "hash", "John", "Doe", null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Role cannot be null");
        }
    }
    
    @Nested
    @DisplayName("User Profile Update Tests")
    class UserProfileUpdateTests {
        
        private User createTestUser() {
            return new User("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER);
        }
        
        @Test
        @DisplayName("Should update profile with valid parameters")
        void shouldUpdateProfileWithValidParameters() {
            // Given
            User user = createTestUser();
            LocalDateTime originalUpdatedAt = user.getUpdatedAt();
            
            // When
            user.updateProfile("Jane", "Smith", "jane@example.com");
            
            // Then
            assertThat(user.getFirstName()).isEqualTo("Jane");
            assertThat(user.getLastName()).isEqualTo("Smith");
            assertThat(user.getEmail()).isEqualTo("jane@example.com");
            assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
        
        @Test
        @DisplayName("Should throw exception when updating with null first name")
        void shouldThrowExceptionWhenUpdatingWithNullFirstName() {
            // Given
            User user = createTestUser();
            
            // When & Then
            assertThatThrownBy(() -> user.updateProfile(null, "Smith", "jane@example.com"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("First name cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when updating with null last name")
        void shouldThrowExceptionWhenUpdatingWithNullLastName() {
            // Given
            User user = createTestUser();
            
            // When & Then
            assertThatThrownBy(() -> user.updateProfile("Jane", null, "jane@example.com"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Last name cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when updating with null email")
        void shouldThrowExceptionWhenUpdatingWithNullEmail() {
            // Given
            User user = createTestUser();
            
            // When & Then
            assertThatThrownBy(() -> user.updateProfile("Jane", "Smith", null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Email cannot be null");
        }
    }
    
    @Nested
    @DisplayName("User Status Tests")
    class UserStatusTests {
        
        private User createTestUser() {
            return new User("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER);
        }
        
        @Test
        @DisplayName("Should activate user")
        void shouldActivateUser() {
            // Given
            User user = createTestUser();
            user.deactivate();
            
            // When
            user.activate();
            
            // Then
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.isActive()).isTrue();
        }
        
        @Test
        @DisplayName("Should deactivate user")
        void shouldDeactivateUser() {
            // Given
            User user = createTestUser();
            
            // When
            user.deactivate();
            
            // Then
            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
            assertThat(user.isActive()).isFalse();
        }
        
        @Test
        @DisplayName("Should block user")
        void shouldBlockUser() {
            // Given
            User user = createTestUser();
            
            // When
            user.block();
            
            // Then
            assertThat(user.getStatus()).isEqualTo(UserStatus.BLOCKED);
            assertThat(user.isBlocked()).isTrue();
        }
    }
    
    @Nested
    @DisplayName("User Role Tests")
    class UserRoleTests {
        
        private User createTestUser() {
            return new User("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER);
        }
        
        @Test
        @DisplayName("Should update user role")
        void shouldUpdateUserRole() {
            // Given
            User user = createTestUser();
            LocalDateTime originalUpdatedAt = user.getUpdatedAt();
            
            // When
            user.updateRole(UserRole.MANAGER);
            
            // Then
            assertThat(user.getRole()).isEqualTo(UserRole.MANAGER);
            assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
        
        @Test
        @DisplayName("Should throw exception when updating with null role")
        void shouldThrowExceptionWhenUpdatingWithNullRole() {
            // Given
            User user = createTestUser();
            
            // When & Then
            assertThatThrownBy(() -> user.updateRole(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Role cannot be null");
        }
        
        @Test
        @DisplayName("Should identify admin user correctly")
        void shouldIdentifyAdminUserCorrectly() {
            // Given
            User adminUser = new User("admin", "admin@example.com", "hash", "Admin", "User", UserRole.ADMIN);
            User regularUser = createTestUser();
            
            // When & Then
            assertThat(adminUser.isAdmin()).isTrue();
            assertThat(regularUser.isAdmin()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("User Utility Methods Tests")
    class UserUtilityMethodsTests {
        
        private User createTestUser() {
            return new User("testuser", "test@example.com", "hash", "John", "Doe", UserRole.USER);
        }
        
        @Test
        @DisplayName("Should get full name correctly")
        void shouldGetFullNameCorrectly() {
            // Given
            User user = createTestUser();
            
            // When
            String fullName = user.getFullName();
            
            // Then
            assertThat(fullName).isEqualTo("John Doe");
        }
        
        @Test
        @DisplayName("Should record login time")
        void shouldRecordLoginTime() {
            // Given
            User user = createTestUser();
            LocalDateTime originalUpdatedAt = user.getUpdatedAt();
            
            // When
            user.recordLogin();
            
            // Then
            assertThat(user.getLastLoginAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
        
        @Test
        @DisplayName("Should verify email")
        void shouldVerifyEmail() {
            // Given
            User user = createTestUser();
            LocalDateTime originalUpdatedAt = user.getUpdatedAt();
            
            // When
            user.verifyEmail();
            
            // Then
            assertThat(user.isEmailVerified()).isTrue();
            assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
    }
    
    @Nested
    @DisplayName("User Equality and HashCode Tests")
    class UserEqualityTests {
        
        @Test
        @DisplayName("Should be equal when users have same id and username")
        void shouldBeEqualWhenUsersHaveSameIdAndUsername() {
            // Given
            User user1 = createUserWithId("testuser", "test1@example.com", "hash1", "John", "Doe", UserRole.USER, 1L);
            User user2 = createUserWithId("testuser", "test2@example.com", "hash2", "Jane", "Smith", UserRole.ADMIN, 1L);
            
            // When & Then
            assertThat(user1)
                .isEqualTo(user2)
                .hasSameHashCodeAs(user2);
        }
        
        @Test
        @DisplayName("Should not be equal when users have different ids")
        void shouldNotBeEqualWhenUsersHaveDifferentIds() {
            // Given
            User user1 = createUserWithId("testuser", "test1@example.com", "hash1", "John", "Doe", UserRole.USER, 1L);
            User user2 = createUserWithId("testuser", "test2@example.com", "hash2", "Jane", "Smith", UserRole.ADMIN, 2L);
            
            // When & Then
            assertThat(user1).isNotEqualTo(user2);
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
    }
}
