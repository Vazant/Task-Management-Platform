package com.taskboard.userservice.infrastructure.web.controller;

import com.taskboard.userservice.application.dto.LoginRequest;
import com.taskboard.userservice.application.dto.RegisterRequest;
import com.taskboard.userservice.application.dto.UserDto;
import com.taskboard.userservice.application.service.UserService;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for authentication operations.
 * 
 * <p>This controller provides endpoints for:
 * <ul>
 *   <li>User registration</li>
 *   <li>User login and authentication</li>
 *   <li>Token refresh</li>
 *   <li>User logout</li>
 * </ul>
 * </p>
 * 
 * <p>Authentication endpoints do not require prior authentication.</p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Operations for user authentication and registration")
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    public AuthController(UserService userService, 
                         AuthenticationManager authenticationManager,
                         JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    /**
     * Registers a new user in the system.
     * 
     * <p>This endpoint allows new users to register in the system. The user will be created
     * with the USER role and will be able to authenticate using the provided credentials.</p>
     * 
     * @param request the user registration request
     * @return the created user information (without password)
     * 
     * @throws com.taskboard.userservice.domain.exception.ValidationException if the request data is invalid
     * @throws com.taskboard.userservice.domain.exception.ConflictException if username or email already exists
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Registers a new user in the system. The user will be created with the USER role " +
            "and will be able to authenticate using the provided credentials."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class),
                examples = @ExampleObject(
                    name = "Registered User",
                    value = """
                        {
                            "id": 1,
                            "username": "newuser",
                            "email": "newuser@example.com",
                            "firstName": "New",
                            "lastName": "User",
                            "role": "USER",
                            "active": true,
                            "createdAt": "2024-01-01T10:00:00",
                            "updatedAt": "2024-01-01T10:00:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                            "timestamp": "2024-01-01T10:00:00",
                            "status": 400,
                            "error": "Bad Request",
                            "path": "/api/v1/auth/register",
                            "errors": [
                                "Username is required",
                                "Email must be valid",
                                "Password must be at least 8 characters"
                            ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username or email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Conflict Error",
                    value = """
                        {
                            "timestamp": "2024-01-01T10:00:00",
                            "status": 409,
                            "error": "Conflict",
                            "path": "/api/v1/auth/register",
                            "message": "Username already exists"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<UserDto> register(
            @Parameter(description = "User registration request", required = true)
            @Valid @RequestBody RegisterRequest request) {
        
        // Convert RegisterRequest to CreateUserRequest
        com.taskboard.userservice.application.dto.CreateUserRequest createRequest = 
            com.taskboard.userservice.application.dto.CreateUserRequest.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .profileImageUrl(request.getProfileImageUrl())
                .build();
        
        UserDto userDto = userService.createUser(createRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }
    
    /**
     * Authenticates a user and returns a JWT token.
     * 
     * <p>This endpoint authenticates a user using their username and password.
     * Upon successful authentication, a JWT token is returned that can be used
     * for subsequent API calls.</p>
     * 
     * @param request the login request containing credentials
     * @return authentication response with JWT token and user information
     * 
     * @throws com.taskboard.userservice.domain.exception.AuthenticationException if credentials are invalid
     * @throws com.taskboard.userservice.domain.exception.AccountInactiveException if user account is inactive
     */
    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user using their username and password. " +
            "Returns a JWT token that can be used for subsequent API calls."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Authentication successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Authentication Success",
                    value = """
                        {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "tokenType": "Bearer",
                            "expiresIn": 3600,
                            "user": {
                                "id": 1,
                                "username": "testuser",
                                "email": "test@example.com",
                                "firstName": "Test",
                                "lastName": "User",
                                "role": "USER",
                                "active": true,
                                "createdAt": "2024-01-01T10:00:00",
                                "updatedAt": "2024-01-01T10:00:00"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                            "timestamp": "2024-01-01T10:00:00",
                            "status": 400,
                            "error": "Bad Request",
                            "path": "/api/v1/auth/login",
                            "errors": [
                                "Username is required",
                                "Password is required"
                            ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials or inactive account",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Authentication Error",
                    value = """
                        {
                            "timestamp": "2024-01-01T10:00:00",
                            "status": 401,
                            "error": "Unauthorized",
                            "path": "/api/v1/auth/login",
                            "message": "Invalid credentials"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Login request", required = true)
            @Valid @RequestBody LoginRequest request) {
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        // Get user details
        Optional<UserDto> user = userService.getUserByUsername(request.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);
        long expiresIn = jwtTokenProvider.getExpiration();
        
        // Create response
        AuthResponse response = new AuthResponse(
            token,
            "Bearer",
            expiresIn,
            user.get()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refreshes the JWT token for the current user.
     * 
     * <p>This endpoint allows users to refresh their JWT token without re-authenticating.
     * The token must be valid and not expired.</p>
     * 
     * @param authentication the current authentication context
     * @return new JWT token
     * 
     * @throws com.taskboard.userservice.domain.exception.TokenExpiredException if the token is expired
     * @throws com.taskboard.userservice.domain.exception.InvalidTokenException if the token is invalid
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh JWT token",
        description = "Refreshes the JWT token for the current user without re-authenticating. " +
            "The current token must be valid and not expired."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenRefreshResponse.class),
                examples = @ExampleObject(
                    name = "Token Refresh Success",
                    value = """
                        {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "tokenType": "Bearer",
                            "expiresIn": 3600
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Token Error",
                    value = """
                        {
                            "timestamp": "2024-01-01T10:00:00",
                            "status": 401,
                            "error": "Unauthorized",
                            "path": "/api/v1/auth/refresh",
                            "message": "Invalid token"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<TokenRefreshResponse> refreshToken(Authentication authentication) {
        // Generate new token
        String token = jwtTokenProvider.generateToken(authentication);
        long expiresIn = jwtTokenProvider.getExpirationTime();
        
        // Create response
        TokenRefreshResponse response = new TokenRefreshResponse(
            token,
            "Bearer",
            expiresIn
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Logs out the current user.
     * 
     * <p>This endpoint logs out the current user. In a stateless JWT implementation,
     * this primarily serves as a client-side signal to discard the token.</p>
     * 
     * @return success message
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Logout user",
        description = "Logs out the current user. In a stateless JWT implementation, " +
            "this primarily serves as a client-side signal to discard the token."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Logout Success",
                    value = """
                        {
                            "message": "Logged out successfully"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    public ResponseEntity<LogoutResponse> logout() {
        // Clear security context
        SecurityContextHolder.clearContext();
        
        return ResponseEntity.ok(new LogoutResponse("Logged out successfully"));
    }
    
    /**
     * Maps a User entity to a UserDto.
     * 
     * @param user the user entity
     * @return the user DTO
     */
    private UserDto mapToDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .status(user.isActive() ? com.taskboard.userservice.domain.model.UserStatus.ACTIVE : com.taskboard.userservice.domain.model.UserStatus.INACTIVE)
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
    
    /**
     * Response DTO for authentication operations.
     */
    public record AuthResponse(
        @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        
        @Schema(description = "Token expiration time in seconds", example = "3600")
        long expiresIn,
        
        @Schema(description = "User information")
        UserDto user
    ) {}
    
    /**
     * Response DTO for token refresh operations.
     */
    public record TokenRefreshResponse(
        @Schema(description = "New JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        
        @Schema(description = "Token expiration time in seconds", example = "3600")
        long expiresIn
    ) {}
    
    /**
     * Response DTO for logout operations.
     */
    public record LogoutResponse(
        @Schema(description = "Logout message", example = "Logged out successfully")
        String message
    ) {}
}