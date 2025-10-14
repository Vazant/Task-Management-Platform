package com.taskboard.userservice.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.application.dto.LoginRequest;
import com.taskboard.userservice.application.dto.RegisterRequest;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.infrastructure.config.PasswordEncoderConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Auth Controller Contract Tests")
class AuthControllerContractTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .password(passwordEncoder.encode("password123"))
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    @Nested
    @DisplayName("User Registration Contract Tests")
    class UserRegistrationContractTests {
        
        @Test
        @DisplayName("Should register user with valid request")
        void shouldRegisterUserWithValidRequest() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();
            
            User createdUser = testUser.toBuilder()
                .id(2L)
                .username("newuser")
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .build();
            
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(createdUser);
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }
        
        @Test
        @DisplayName("Should return 400 for invalid registration request")
        void shouldReturn400ForInvalidRegistrationRequest() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                .username("") // Invalid: empty username
                .email("invalid-email") // Invalid: malformed email
                .password("123") // Invalid: too short password
                .build();
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
        }
        
        @Test
        @DisplayName("Should return 409 for duplicate username")
        void shouldReturn409ForDuplicateUsername() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();
            
            when(userRepository.existsByUsername("testuser")).thenReturn(true);
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Username already exists"));
        }
        
        @Test
        @DisplayName("Should return 409 for duplicate email")
        void shouldReturn409ForDuplicateEmail() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("test@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();
            
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email already exists"));
        }
    }
    
    @Nested
    @DisplayName("User Login Contract Tests")
    class UserLoginContractTests {
        
        @Test
        @DisplayName("Should login user with valid credentials")
        void shouldLoginUserWithValidCredentials() throws Exception {
            // Given
            LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());
        }
        
        @Test
        @DisplayName("Should return 400 for invalid login request")
        void shouldReturn400ForInvalidLoginRequest() throws Exception {
            // Given
            LoginRequest request = LoginRequest.builder()
                .username("") // Invalid: empty username
                .password("") // Invalid: empty password
                .build();
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
        }
        
        @Test
        @DisplayName("Should return 401 for invalid credentials")
        void shouldReturn401ForInvalidCredentials() throws Exception {
            // Given
            LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }
        
        @Test
        @DisplayName("Should return 401 for non-existent user")
        void shouldReturn401ForNonExistentUser() throws Exception {
            // Given
            LoginRequest request = LoginRequest.builder()
                .username("nonexistent")
                .password("password123")
                .build();
            
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }
        
        @Test
        @DisplayName("Should return 401 for inactive user")
        void shouldReturn401ForInactiveUser() throws Exception {
            // Given
            User inactiveUser = testUser.toBuilder()
                .status(UserStatus.INACTIVE)
                .build();
            
            LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(inactiveUser));
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account is inactive"));
        }
    }
    
    @Nested
    @DisplayName("Token Refresh Contract Tests")
    class TokenRefreshContractTests {
        
        @Test
        @DisplayName("Should refresh token successfully")
        void shouldRefreshTokenSuccessfully() throws Exception {
            // Given
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/refresh")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").exists());
        }
        
        @Test
        @DisplayName("Should return 401 for invalid refresh token")
        void shouldReturn401ForInvalidRefreshToken() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/auth/refresh")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid token"));
        }
        
        @Test
        @DisplayName("Should return 401 for missing refresh token")
        void shouldReturn401ForMissingRefreshToken() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/auth/refresh")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Token is required"));
        }
    }
    
    @Nested
    @DisplayName("Logout Contract Tests")
    class LogoutContractTests {
        
        @Test
        @DisplayName("Should logout successfully")
        void shouldLogoutSuccessfully() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/auth/logout")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
        }
        
        @Test
        @DisplayName("Should logout without token")
        void shouldLogoutWithoutToken() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/auth/logout")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
        }
    }
    
    @Nested
    @DisplayName("Error Response Contract Tests")
    class ErrorResponseContractTests {
        
        @Test
        @DisplayName("Should return consistent error format for validation errors")
        void shouldReturnConsistentErrorFormatForValidationErrors() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                .username("") // Invalid
                .email("invalid") // Invalid
                .password("123") // Invalid
                .build();
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/register"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
        }
        
        @Test
        @DisplayName("Should return consistent error format for authentication errors")
        void shouldReturnConsistentErrorFormatForAuthenticationErrors() throws Exception {
            // Given
            LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/login"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }
        
        @Test
        @DisplayName("Should return consistent error format for conflict errors")
        void shouldReturnConsistentErrorFormatForConflictErrors() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();
            
            when(userRepository.existsByUsername("testuser")).thenReturn(true);
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/register"))
                .andExpect(jsonPath("$.message").value("Username already exists"));
        }
    }
    
    @Nested
    @DisplayName("Content Type Contract Tests")
    class ContentTypeContractTests {
        
        @Test
        @DisplayName("Should accept JSON content type")
        void shouldAcceptJsonContentType() throws Exception {
            // Given
            LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
        
        @Test
        @DisplayName("Should reject non-JSON content type")
        void shouldRejectNonJsonContentType() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
        }
    }
    
    @Nested
    @DisplayName("Security Contract Tests")
    class SecurityContractTests {
        
        @Test
        @DisplayName("Should require CSRF token for state-changing operations")
        void shouldRequireCsrfTokenForStateChangingOperations() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }
        
        @Test
        @DisplayName("Should not require CSRF token for read operations")
        void shouldNotRequireCsrfTokenForReadOperations() throws Exception {
            // Given
            LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        }
    }
}
