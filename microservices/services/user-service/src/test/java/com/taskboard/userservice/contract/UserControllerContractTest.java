package com.taskboard.userservice.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("User Controller Contract Tests")
class UserControllerContractTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserRepository userRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .password("hashedPassword")
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    @Nested
    @DisplayName("User Creation Contract Tests")
    class UserCreationContractTests {
        
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create user with valid request")
        void shouldCreateUserWithValidRequest() throws Exception {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
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
            mockMvc.perform(post("/api/v1/users")
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
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 for invalid request")
        void shouldReturn400ForInvalidRequest() throws Exception {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                .username("") // Invalid: empty username
                .email("invalid-email") // Invalid: malformed email
                .password("123") // Invalid: too short password
                .build();
            
            // When & Then
            mockMvc.perform(post("/api/v1/users")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
        }
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 403 for non-admin user")
        void shouldReturn403ForNonAdminUser() throws Exception {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
                .build();
            
            // When & Then
            mockMvc.perform(post("/api/v1/users")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }
        
        @Test
        @DisplayName("Should return 401 for unauthenticated user")
        void shouldReturn401ForUnauthenticatedUser() throws Exception {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
                .build();
            
            // When & Then
            mockMvc.perform(post("/api/v1/users")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
        }
    }
    
    @Nested
    @DisplayName("User Retrieval Contract Tests")
    class UserRetrievalContractTests {
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should get user by ID")
        void shouldGetUserById() throws Exception {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(get("/api/v1/users/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 404 for non-existent user")
        void shouldReturn404ForNonExistentUser() throws Exception {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            
            // When & Then
            mockMvc.perform(get("/api/v1/users/999")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"));
        }
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should get current user profile")
        void shouldGetCurrentUserProfile() throws Exception {
            // Given
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(get("/api/v1/users/profile")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }
        
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get all users for admin")
        void shouldGetAllUsersForAdmin() throws Exception {
            // Given
            when(userRepository.findAll()).thenReturn(java.util.List.of(testUser));
            
            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].passwordHash").doesNotExist());
        }
    }
    
    @Nested
    @DisplayName("User Update Contract Tests")
    class UserUpdateContractTests {
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should update user profile")
        void shouldUpdateUserProfile() throws Exception {
            // Given
            UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .build();
            
            User updatedUser = testUser.toBuilder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .updatedAt(LocalDateTime.now())
                .build();
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmailAndIdNot("updated@example.com", 1L)).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            
            // When & Then
            mockMvc.perform(put("/api/v1/users/profile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.updatedAt").exists());
        }
        
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update user by ID for admin")
        void shouldUpdateUserByIdForAdmin() throws Exception {
            // Given
            UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Admin Updated")
                .lastName("Name")
                .status(UserStatus.INACTIVE)
                .build();
            
            User updatedUser = testUser.toBuilder()
                .firstName("Admin Updated")
                .lastName("Name")
                .status(UserStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();
            
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            
            // When & Then
            mockMvc.perform(put("/api/v1/users/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Admin Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.active").value(false));
        }
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 400 for invalid update request")
        void shouldReturn400ForInvalidUpdateRequest() throws Exception {
            // Given
            UpdateUserRequest request = UpdateUserRequest.builder()
                .email("invalid-email") // Invalid email format
                .build();
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(put("/api/v1/users/profile")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray());
        }
    }
    
    @Nested
    @DisplayName("User Deletion Contract Tests")
    class UserDeletionContractTests {
        
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete user by ID for admin")
        void shouldDeleteUserByIdForAdmin() throws Exception {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(delete("/api/v1/users/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        }
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 403 for non-admin deletion")
        void shouldReturn403ForNonAdminDeletion() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/v1/users/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        }
        
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 for non-existent user deletion")
        void shouldReturn404ForNonExistentUserDeletion() throws Exception {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            
            // When & Then
            mockMvc.perform(delete("/api/v1/users/999")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"));
        }
    }
    
    @Nested
    @DisplayName("Password Management Contract Tests")
    class PasswordManagementContractTests {
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should change password successfully")
        void shouldChangePasswordSuccessfully() throws Exception {
            // Given
            String changePasswordRequest = """
                {
                    "currentPassword": "oldPassword123",
                    "newPassword": "newPassword123"
                }
                """;
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(put("/api/v1/users/password")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(changePasswordRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
        }
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 400 for invalid password change request")
        void shouldReturn400ForInvalidPasswordChangeRequest() throws Exception {
            // Given
            String changePasswordRequest = """
                {
                    "currentPassword": "oldPassword123",
                    "newPassword": "123"
                }
                """;
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(put("/api/v1/users/password")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(changePasswordRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray());
        }
    }
    
    @Nested
    @DisplayName("Error Response Contract Tests")
    class ErrorResponseContractTests {
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return consistent error format for validation errors")
        void shouldReturnConsistentErrorFormatForValidationErrors() throws Exception {
            // Given
            CreateUserRequest request = CreateUserRequest.builder()
                .username("") // Invalid
                .email("invalid") // Invalid
                .password("123") // Invalid
                .build();
            
            // When & Then
            mockMvc.perform(post("/api/v1/users")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/v1/users"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
        }
        
        @Test
        @DisplayName("Should return consistent error format for unauthorized access")
        void shouldReturnConsistentErrorFormatForUnauthorizedAccess() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/users/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/api/v1/users/1"));
        }
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return consistent error format for forbidden access")
        void shouldReturnConsistentErrorFormatForForbiddenAccess() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/v1/users/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.path").value("/api/v1/users/1"));
        }
    }
    
    @Nested
    @DisplayName("Content Type Contract Tests")
    class ContentTypeContractTests {
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should accept JSON content type")
        void shouldAcceptJsonContentType() throws Exception {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            
            // When & Then
            mockMvc.perform(get("/api/v1/users/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
        
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should reject non-JSON content type")
        void shouldRejectNonJsonContentType() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/users")
                    .with(csrf())
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
        }
    }
}
