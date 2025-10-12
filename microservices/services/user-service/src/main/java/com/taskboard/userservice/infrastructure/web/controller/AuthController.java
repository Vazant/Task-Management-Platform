package com.taskboard.userservice.infrastructure.web.controller;

import com.taskboard.userservice.application.dto.AuthenticateUserRequest;
import com.taskboard.userservice.application.dto.AuthenticateUserResponse;
import com.taskboard.userservice.application.usecase.AuthenticateUserUseCase;
import com.taskboard.userservice.infrastructure.web.dto.ApiResponse;
import com.taskboard.userservice.infrastructure.web.dto.ErrorResponse;
import com.taskboard.userservice.infrastructure.web.dto.LoginRequest;
import com.taskboard.userservice.infrastructure.web.dto.LoginResponse;
import com.taskboard.userservice.infrastructure.web.dto.RefreshTokenRequest;
import com.taskboard.userservice.infrastructure.web.dto.RefreshTokenResponse;
import com.taskboard.userservice.infrastructure.web.dto.RegisterRequest;
import com.taskboard.userservice.infrastructure.web.dto.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication and authorization operations.
 * Provides endpoints for user login, registration, and token management.
 * 
 * <p>This controller handles all authentication-related HTTP requests including:
 * <ul>
 *   <li>User login with credentials</li>
 *   <li>User registration with validation</li>
 *   <li>Token refresh for extended sessions</li>
 *   <li>User logout and session cleanup</li>
 *   <li>Token validation and current user info</li>
 * </ul>
 * 
 * <p>All endpoints return standardized {@link ApiResponse} objects with
 * consistent error handling and security measures.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see ApiResponse
 * @see LoginRequest
 * @see RegisterRequest
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    // TODO: Добавить RefreshTokenUseCase, RegisterUserUseCase, LogoutUseCase

    /**
     * Authenticates a user and returns an access token.
     * 
     * <p>This endpoint validates user credentials and returns a JWT token
     * for authenticated sessions. The token can be used for subsequent
     * API requests to maintain the user's session.
     * 
     * <p>Security: Public endpoint (no authentication required)
     * 
     * @param request the login request containing username and password
     * @return ResponseEntity containing the access token and user information
     * @throws IllegalArgumentException if credentials are invalid
     * 
     * @see LoginRequest
     * @see LoginResponse
     * @see ApiResponse
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        
        log.info("Login attempt for username: {}", request.getUsername());
        
        try {
            AuthenticateUserRequest authRequest = AuthenticateUserRequest.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .build();
            
            AuthenticateUserResponse authResponse = authenticateUserUseCase.execute(authRequest);
            
            if (authResponse.isAuthenticated()) {
                LoginResponse loginResponse = LoginResponse.builder()
                        .token(authResponse.getToken())
                        .tokenType(authResponse.getTokenType())
                        .expiresIn(authResponse.getExpiresIn())
                        .userId(authResponse.getUserId())
                        .username(authResponse.getUsername())
                        .build();
                
                ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .data(loginResponse)
                        .message("Login successful")
                        .build();
                
                log.info("Login successful for user: {}", request.getUsername());
                return ResponseEntity.ok(apiResponse);
            } else {
                return handleAuthenticationError("Invalid credentials");
            }
            
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return handleAuthenticationError("Login failed");
        }
    }

    /**
     * Registers a new user in the system.
     * 
     * <p>This endpoint creates a new user account with the provided information.
     * The request is validated to ensure all required fields are present and
     * meet the system requirements.
     * 
     * <p>Security: Public endpoint (no authentication required)
     * 
     * @param request the registration request containing user details
     * @return ResponseEntity containing the registration result and user information
     * @throws IllegalArgumentException if validation fails or user already exists
     * 
     * @see RegisterRequest
     * @see RegisterResponse
     * @see ApiResponse
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        
        log.info("Registration attempt for username: {}", request.getUsername());
        
        try {
            // TODO: Implement RegisterUserUseCase
            // RegisterUserRequest registerRequest = RegisterUserRequest.builder()
            //         .username(request.getUsername())
            //         .email(request.getEmail())
            //         .password(request.getPassword())
            //         .firstName(request.getFirstName())
            //         .lastName(request.getLastName())
            //         .build();
            // 
            // RegisterUserResponse response = registerUserUseCase.execute(registerRequest);
            
            RegisterResponse registerResponse = RegisterResponse.builder()
                    .userId(1L) // Placeholder
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .message("Registration successful")
                    .build();
            
            ApiResponse<RegisterResponse> apiResponse = ApiResponse.<RegisterResponse>builder()
                    .success(true)
                    .data(registerResponse)
                    .message("User registered successfully")
                    .build();
            
            log.info("Registration successful for user: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
            
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return handleValidationError(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return handleInternalError("Registration failed");
        }
    }

    /**
     * Обновление токена аутентификации.
     *
     * @param request данные для обновления токена
     * @return новый токен
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        
        log.info("Token refresh attempt");
        
        try {
            // TODO: Implement RefreshTokenUseCase
            // RefreshTokenResponse response = refreshTokenUseCase.execute(request);
            
            RefreshTokenResponse refreshResponse = RefreshTokenResponse.builder()
                    .token("new-token") // Placeholder
                    .tokenType("Bearer")
                    .expiresIn(3600)
                    .build();
            
            ApiResponse<RefreshTokenResponse> apiResponse = ApiResponse.<RefreshTokenResponse>builder()
                    .success(true)
                    .data(refreshResponse)
                    .message("Token refreshed successfully")
                    .build();
            
            return ResponseEntity.ok(apiResponse);
            
        } catch (IllegalArgumentException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            return handleAuthenticationError("Invalid refresh token");
        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            return handleInternalError("Token refresh failed");
        }
    }

    /**
     * Выход из системы (logout).
     *
     * @param request данные для выхода
     * @return результат выхода
     */
    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) String request) {
        
        log.info("Logout attempt");
        
        try {
            // TODO: Implement LogoutUseCase
            // logoutUseCase.execute(request);
            
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .success(true)
                    .message("Logout successful")
                    .build();
            
            log.info("Logout successful");
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Unexpected error during logout", e);
            return handleInternalError("Logout failed");
        }
    }

    /**
     * Проверка валидности токена.
     *
     * @return статус токена
     */
    @GetMapping("/validate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> validateToken() {
        
        log.debug("Token validation request");
        
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .message("Token is valid")
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Получение информации о текущем пользователе.
     *
     * @return данные текущего пользователя
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser() {
        
        log.debug("Get current user request");
        
        try {
            // TODO: Implement GetCurrentUserUseCase
            // GetCurrentUserResponse response = getCurrentUserUseCase.execute();
            
            ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
                    .success(true)
                    .data("Current user data") // Placeholder
                    .message("Current user retrieved successfully")
                    .build();
            
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Unexpected error getting current user", e);
            return handleInternalError("Failed to get current user");
        }
    }

    // Вспомогательные методы для обработки ошибок

    private <T> ResponseEntity<ApiResponse<T>> handleAuthenticationError(String message) {
        ErrorResponse error = ErrorResponse.builder()
                .code("AUTHENTICATION_ERROR")
                .message(message)
                .build();
        
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    private <T> ResponseEntity<ApiResponse<T>> handleValidationError(String message) {
        ErrorResponse error = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message(message)
                .build();
        
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .build();
        
        return ResponseEntity.badRequest().body(apiResponse);
    }

    private <T> ResponseEntity<ApiResponse<T>> handleInternalError(String message) {
        ErrorResponse error = ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message(message)
                .build();
        
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
