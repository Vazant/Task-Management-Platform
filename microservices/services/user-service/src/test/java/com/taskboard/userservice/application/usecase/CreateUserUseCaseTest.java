package com.taskboard.userservice.application.usecase;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.CreateUserResponse;
import com.taskboard.userservice.application.service.UserService;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase Tests")
class CreateUserUseCaseTest {
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private CreateUserUseCase createUserUseCase;
    
    private CreateUserRequest validRequest;
    private User createdUser;
    
    @BeforeEach
    void setUp() {
        validRequest = CreateUserRequest.builder()
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .password("password123")
            .role(UserRole.USER)
            .build();
        
        createdUser = User.builder()
            .id(1L)
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
    @DisplayName("Successful User Creation Tests")
    class SuccessfulUserCreationTests {
        
        @Test
        @DisplayName("Should create user successfully with valid request")
        void shouldCreateUserSuccessfullyWithValidRequest() {
            // Given
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createdUser);
            
            // When
            CreateUserResponse response = createUserUseCase.execute(validRequest);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getFirstName()).isEqualTo("Test");
            assertThat(response.getLastName()).isEqualTo("User");
            assertThat(response.getRole()).isEqualTo(UserRole.USER);
            assertThat(response.isActive()).isTrue();
            assertThat(response.getCreatedAt()).isNotNull();
            
            verify(userService).createUser(validRequest);
        }
        
        @Test
        @DisplayName("Should create admin user successfully")
        void shouldCreateAdminUserSuccessfully() {
            // Given
            CreateUserRequest adminRequest = validRequest.toBuilder()
                .role(UserRole.ADMIN)
                .build();
            
            User adminUser = createdUser.toBuilder()
                .role(UserRole.ADMIN)
                .build();
            
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(adminUser);
            
            // When
            CreateUserResponse response = createUserUseCase.execute(adminRequest);
            
            // Then
            assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
            verify(userService).createUser(adminRequest);
        }
        
        @Test
        @DisplayName("Should create user with minimal required fields")
        void shouldCreateUserWithMinimalRequiredFields() {
            // Given
            CreateUserRequest minimalRequest = CreateUserRequest.builder()
                .username("minimaluser")
                .email("minimal@example.com")
                .password("password123")
                .role(UserRole.USER)
                .build();
            
            User minimalUser = User.builder()
                .id(2L)
                .username("minimaluser")
                .email("minimal@example.com")
                .password("hashedPassword")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
            
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(minimalUser);
            
            // When
            CreateUserResponse response = createUserUseCase.execute(minimalRequest);
            
            // Then
            assertThat(response.getUserId()).isEqualTo(2L);
            assertThat(response.getUsername()).isEqualTo("minimaluser");
            assertThat(response.getEmail()).isEqualTo("minimal@example.com");
            assertThat(response.getFirstName()).isNull();
            assertThat(response.getLastName()).isNull();
            assertThat(response.getRole()).isEqualTo(UserRole.USER);
            
            verify(userService).createUser(minimalRequest);
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should throw exception when request is null")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Request cannot be null");
            
            verifyNoInteractions(userService);
        }
        
        @Test
        @DisplayName("Should propagate service exception")
        void shouldPropagateServiceException() {
            // Given
            when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new RuntimeException("Service error"));
            
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(validRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service error");
            
            verify(userService).createUser(validRequest);
        }
        
        @Test
        @DisplayName("Should handle validation errors from service")
        void shouldHandleValidationErrorsFromService() {
            // Given
            when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid username"));
            
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid username");
            
            verify(userService).createUser(validRequest);
        }
    }
    
    @Nested
    @DisplayName("Request Validation Tests")
    class RequestValidationTests {
        
        @Test
        @DisplayName("Should validate username is not null")
        void shouldValidateUsernameIsNotNull() {
            // Given
            CreateUserRequest invalidRequest = validRequest.toBuilder()
                .username(null)
                .build();
            
            when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Username cannot be null"));
            
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be null");
        }
        
        @Test
        @DisplayName("Should validate email is not null")
        void shouldValidateEmailIsNotNull() {
            // Given
            CreateUserRequest invalidRequest = validRequest.toBuilder()
                .email(null)
                .build();
            
            when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Email cannot be null"));
            
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null");
        }
        
        @Test
        @DisplayName("Should validate password is not null")
        void shouldValidatePasswordIsNotNull() {
            // Given
            CreateUserRequest invalidRequest = validRequest.toBuilder()
                .password(null)
                .build();
            
            when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Password cannot be null"));
            
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password cannot be null");
        }
        
        @Test
        @DisplayName("Should validate role is not null")
        void shouldValidateRoleIsNotNull() {
            // Given
            CreateUserRequest invalidRequest = validRequest.toBuilder()
                .role(null)
                .build();
            
            when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Role cannot be null"));
            
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role cannot be null");
        }
    }
    
    @Nested
    @DisplayName("Response Mapping Tests")
    class ResponseMappingTests {
        
        @Test
        @DisplayName("Should map all user fields to response correctly")
        void shouldMapAllUserFieldsToResponseCorrectly() {
            // Given
            User userWithAllFields = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("hashedPassword")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
            
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userWithAllFields);
            
            // When
            CreateUserResponse response = createUserUseCase.execute(validRequest);
            
            // Then
            assertThat(response.getUserId()).isEqualTo(userWithAllFields.getId());
            assertThat(response.getUsername()).isEqualTo(userWithAllFields.getUsername());
            assertThat(response.getEmail()).isEqualTo(userWithAllFields.getEmail());
            assertThat(response.getFirstName()).isEqualTo(userWithAllFields.getFirstName());
            assertThat(response.getLastName()).isEqualTo(userWithAllFields.getLastName());
            assertThat(response.getRole()).isEqualTo(userWithAllFields.getRole());
            assertThat(response.isActive()).isEqualTo(userWithAllFields.isActive());
            assertThat(response.getCreatedAt()).isEqualTo(userWithAllFields.getCreatedAt());
        }
        
        @Test
        @DisplayName("Should handle null optional fields in response")
        void shouldHandleNullOptionalFieldsInResponse() {
            // Given
            User userWithNullFields = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName(null)
                .lastName(null)
                .password("hashedPassword")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
            
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userWithNullFields);
            
            // When
            CreateUserResponse response = createUserUseCase.execute(validRequest);
            
            // Then
            assertThat(response.getFirstName()).isNull();
            assertThat(response.getLastName()).isNull();
            assertThat(response.getUserId()).isNotNull();
            assertThat(response.getUsername()).isNotNull();
            assertThat(response.getEmail()).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Service Integration Tests")
    class ServiceIntegrationTests {
        
        @Test
        @DisplayName("Should call user service with correct request")
        void shouldCallUserServiceWithCorrectRequest() {
            // Given
            when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createdUser);
            
            // When
            createUserUseCase.execute(validRequest);
            
            // Then
            verify(userService, times(1)).createUser(validRequest);
            verifyNoMoreInteractions(userService);
        }
        
        @Test
        @DisplayName("Should not call user service when request is null")
        void shouldNotCallUserServiceWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class);
            
            verifyNoInteractions(userService);
        }
        
        @Test
        @DisplayName("Should handle service timeout gracefully")
        void shouldHandleServiceTimeoutGracefully() {
            // Given
            when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new RuntimeException("Service timeout"));
            
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(validRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service timeout");
            
            verify(userService).createUser(validRequest);
        }
    }
}
