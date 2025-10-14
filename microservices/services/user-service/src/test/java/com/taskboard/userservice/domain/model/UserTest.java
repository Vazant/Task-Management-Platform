package com.taskboard.userservice.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User Domain Model Tests")
class UserTest {
    
    private User.UserBuilder userBuilder;
    
    @BeforeEach
    void setUp() {
        userBuilder = User.builder()
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .password("hashedPassword")
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE);
    }
    
    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {
        
        @Test
        @DisplayName("Should create user with valid data")
        void shouldCreateUserWithValidData() {
            // When
            User user = userBuilder.build();
            
            // Then
            assertThat(user.getUsername()).isEqualTo("testuser");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getFirstName()).isEqualTo("Test");
            assertThat(user.getLastName()).isEqualTo("User");
            assertThat(user.getPassword()).isEqualTo("hashedPassword");
            assertThat(user.getRole()).isEqualTo(UserRole.USER);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNotNull();
        }
        
        @Test
        @DisplayName("Should create user with minimal required data")
        void shouldCreateUserWithMinimalRequiredData() {
            // When
            User user = User.builder()
                .username("minimaluser")
                .email("minimal@example.com")
                .password("hash")
                .role(UserRole.USER)
                .build();
            
            // Then
            assertThat(user.getUsername()).isEqualTo("minimaluser");
            assertThat(user.getEmail()).isEqualTo("minimal@example.com");
            assertThat(user.getPassword()).isEqualTo("hash");
            assertThat(user.getRole()).isEqualTo(UserRole.USER);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE); // Default value
        }
        
        @Test
        @DisplayName("Should throw exception when username is null")
        void shouldThrowExceptionWhenUsernameIsNull() {
            // When & Then
            assertThatThrownBy(() -> userBuilder.username(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be null or empty");
        }
        
        @Test
        @DisplayName("Should throw exception when username is empty")
        void shouldThrowExceptionWhenUsernameIsEmpty() {
            // When & Then
            assertThatThrownBy(() -> userBuilder.username("").build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be null or empty");
        }
        
        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // When & Then
            assertThatThrownBy(() -> userBuilder.email(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null or empty");
        }
        
        @Test
        @DisplayName("Should throw exception when email is invalid")
        void shouldThrowExceptionWhenEmailIsInvalid() {
            // When & Then
            assertThatThrownBy(() -> userBuilder.email("invalid-email").build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");
        }
        
        @Test
        @DisplayName("Should throw exception when password is null")
        void shouldThrowExceptionWhenPasswordIsNull() {
            // When & Then
            assertThatThrownBy(() -> userBuilder.password(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when role is null")
        void shouldThrowExceptionWhenRoleIsNull() {
            // When & Then
            assertThatThrownBy(() -> userBuilder.role(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role cannot be null");
        }
    }
    
    @Nested
    @DisplayName("User Validation Tests")
    class UserValidationTests {
        
        @ParameterizedTest
        @ValueSource(strings = {"user", "test_user", "user123", "user-name", "user.name"})
        @DisplayName("Should accept valid usernames")
        void shouldAcceptValidUsernames(String username) {
            // When
            User user = userBuilder.username(username).build();
            
            // Then
            assertThat(user.getUsername()).isEqualTo(username);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "a", "ab", "user@", "user#", "user$", "user%"})
        @DisplayName("Should reject invalid usernames")
        void shouldRejectInvalidUsernames(String username) {
            // When & Then
            assertThatThrownBy(() -> userBuilder.username(username).build())
                .isInstanceOf(IllegalArgumentException.class);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@test.com"
        })
        @DisplayName("Should accept valid email addresses")
        void shouldAcceptValidEmailAddresses(String email) {
            // When
            User user = userBuilder.email(email).build();
            
            // Then
            assertThat(user.getEmail()).isEqualTo(email);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {
            "invalid-email",
            "@example.com",
            "user@",
            "user@.com",
            "user..name@example.com"
        })
        @DisplayName("Should reject invalid email addresses")
        void shouldRejectInvalidEmailAddresses(String email) {
            // When & Then
            assertThatThrownBy(() -> userBuilder.email(email).build())
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
    
    @Nested
    @DisplayName("User Business Logic Tests")
    class UserBusinessLogicTests {
        
        @Test
        @DisplayName("Should return correct full name")
        void shouldReturnCorrectFullName() {
            // Given
            User user = userBuilder
                .firstName("John")
                .lastName("Doe")
                .build();
            
            // When
            String fullName = user.getFullName();
            
            // Then
            assertThat(fullName).isEqualTo("John Doe");
        }
        
        @Test
        @DisplayName("Should return username when first name is null")
        void shouldReturnUsernameWhenFirstNameIsNull() {
            // Given
            User user = userBuilder
                .firstName(null)
                .lastName("Doe")
                .build();
            
            // When
            String fullName = user.getFullName();
            
            // Then
            assertThat(fullName).isEqualTo("testuser");
        }
        
        @Test
        @DisplayName("Should return username when last name is null")
        void shouldReturnUsernameWhenLastNameIsNull() {
            // Given
            User user = userBuilder
                .firstName("John")
                .lastName(null)
                .build();
            
            // When
            String fullName = user.getFullName();
            
            // Then
            assertThat(fullName).isEqualTo("testuser");
        }
        
        @Test
        @DisplayName("Should return username when both names are null")
        void shouldReturnUsernameWhenBothNamesAreNull() {
            // Given
            User user = userBuilder
                .firstName(null)
                .lastName(null)
                .build();
            
            // When
            String fullName = user.getFullName();
            
            // Then
            assertThat(fullName).isEqualTo("testuser");
        }
        
        @Test
        @DisplayName("Should check if user is admin")
        void shouldCheckIfUserIsAdmin() {
            // Given
            User adminUser = userBuilder.role(UserRole.ADMIN).build();
            User regularUser = userBuilder.role(UserRole.USER).build();
            
            // When & Then
            assertThat(adminUser.isAdmin()).isTrue();
            assertThat(regularUser.isAdmin()).isFalse();
        }
        
        @Test
        @DisplayName("Should check if user is active")
        void shouldCheckIfUserIsActive() {
            // Given
            User activeUser = userBuilder.status(UserStatus.ACTIVE).build();
            User inactiveUser = userBuilder.status(UserStatus.INACTIVE).build();
            
            // When & Then
            assertThat(activeUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(inactiveUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }
        
        @Test
        @DisplayName("Should update last login time")
        void shouldUpdateLastLoginTime() {
            // Given
            User user = userBuilder.build();
            LocalDateTime loginTime = LocalDateTime.now();
            
            // When
            user.updateLastLogin(loginTime);
            
            // Then
            assertThat(user.getLastLoginAt()).isEqualTo(loginTime);
        }
        
        @Test
        @DisplayName("Should activate user")
        void shouldActivateUser() {
            // Given
            User user = userBuilder.status(UserStatus.INACTIVE).build();
            
            // When
            user.activate();
            
            // Then
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }
        
        @Test
        @DisplayName("Should deactivate user")
        void shouldDeactivateUser() {
            // Given
            User user = userBuilder.status(UserStatus.ACTIVE).build();
            
            // When
            user.deactivate();
            
            // Then
            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }
    }
    
    @Nested
    @DisplayName("User Equality and HashCode Tests")
    class UserEqualityTests {
        
        @Test
        @DisplayName("Should be equal when usernames are same")
        void shouldBeEqualWhenUsernamesAreSame() {
            // Given
            User user1 = userBuilder.username("sameuser").build();
            User user2 = userBuilder.username("sameuser").build();
            
            // When & Then
            assertThat(user1).isEqualTo(user2);
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }
        
        @Test
        @DisplayName("Should not be equal when usernames are different")
        void shouldNotBeEqualWhenUsernamesAreDifferent() {
            // Given
            User user1 = userBuilder.username("user1").build();
            User user2 = userBuilder.username("user2").build();
            
            // When & Then
            assertThat(user1).isNotEqualTo(user2);
        }
        
        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            User user = userBuilder.build();
            
            // When & Then
            assertThat(user).isNotEqualTo(null);
        }
        
        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToString() {
            // Given
            User user = userBuilder.build();
            String string = "not a user";
            
            // When & Then
            assertThat(user).isNotEqualTo(string);
        }
    }
    
    @Nested
    @DisplayName("User Builder Tests")
    class UserBuilderTests {
        
        @Test
        @DisplayName("Should create user with all fields using builder")
        void shouldCreateUserWithAllFieldsUsingBuilder() {
            // Given
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();
            LocalDateTime lastLoginAt = LocalDateTime.now().minusHours(1);
            
            // When
            User user = userBuilder
                .id(1L)
                .username("builderuser")
                .email("builder@example.com")
                .firstName("Builder")
                .lastName("User")
                .password("builderHash")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastActivity(lastLoginAt)
                .build();
            
            // Then
            assertThat(user.getId()).isEqualTo(1L);
            assertThat(user.getUsername()).isEqualTo("builderuser");
            assertThat(user.getEmail()).isEqualTo("builder@example.com");
            assertThat(user.getFirstName()).isEqualTo("Builder");
            assertThat(user.getLastName()).isEqualTo("User");
            assertThat(user.getPassword()).isEqualTo("builderHash");
            assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.getCreatedAt()).isEqualTo(createdAt);
            assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(user.getLastLoginAt()).isEqualTo(lastLoginAt);
        }
        
        @Test
        @DisplayName("Should create copy of user using toBuilder")
        void shouldCreateCopyOfUserUsingToBuilder() {
            // Given
            User originalUser = userBuilder.build();
            
            // When
            User copiedUser = originalUser.toBuilder()
                .username("copieduser")
                .email("copied@example.com")
                .build();
            
            // Then
            assertThat(copiedUser.getUsername()).isEqualTo("copieduser");
            assertThat(copiedUser.getEmail()).isEqualTo("copied@example.com");
            assertThat(copiedUser.getFirstName()).isEqualTo(originalUser.getFirstName());
            assertThat(copiedUser.getLastName()).isEqualTo(originalUser.getLastName());
            assertThat(copiedUser.getRole()).isEqualTo(originalUser.getRole());
        }
    }
}