package com.taskboard.userservice.infrastructure.web.controller;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.application.dto.UserDto;
import com.taskboard.userservice.application.service.UserService;
import com.taskboard.userservice.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for user management operations.
 * 
 * <p>This controller provides endpoints for:
 * <ul>
 *   <li>User creation (admin only)</li>
 *   <li>User retrieval and listing</li>
 *   <li>User profile management</li>
 *   <li>User updates and deletion</li>
 *   <li>Password management</li>
 * </ul>
 * </p>
 * 
 * <p>All endpoints require authentication and appropriate authorization.</p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Operations for managing users in the system")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Creates a new user in the system.
     * 
     * <p>This endpoint allows administrators to create new users with specified roles and permissions.
     * The user will be created with the provided information and will be able to authenticate
     * using the provided credentials.</p>
     * 
     * @param request the user creation request containing user details
     * @return the created user information (without password)
     * 
     * @throws com.taskboard.userservice.domain.exception.ValidationException if the request data is invalid
     * @throws com.taskboard.userservice.domain.exception.ConflictException if username or email already exists
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user in the system. Only administrators can create users. " +
            "The user will be created with the specified role and will be able to authenticate " +
            "using the provided credentials."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class),
                examples = @ExampleObject(
                    name = "Created User",
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
                            "path": "/api/v1/users",
                            "errors": [
                                "Username is required",
                                "Email must be valid"
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
                            "path": "/api/v1/users",
                            "message": "Username already exists"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Forbidden Error",
                    value = """
                        {
                            "timestamp": "2024-01-01T10:00:00",
                            "status": 403,
                            "error": "Forbidden",
                            "path": "/api/v1/users",
                            "message": "Access denied"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "User creation request", required = true)
            @Valid @RequestBody CreateUserRequest request) {
        
        UserDto createdUser = userService.createUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    /**
     * Retrieves all users in the system.
     * 
     * <p>This endpoint returns a list of all users in the system. Only administrators
     * can access this endpoint.</p>
     * 
     * @return list of all users (without passwords)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users in the system. Only administrators can access this endpoint."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "array", implementation = UserDto.class),
                examples = @ExampleObject(
                    name = "Users List",
                    value = """
                        [
                            {
                                "id": 1,
                                "username": "user1",
                                "email": "user1@example.com",
                                "firstName": "User",
                                "lastName": "One",
                                "role": "USER",
                                "active": true,
                                "createdAt": "2024-01-01T10:00:00",
                                "updatedAt": "2024-01-01T10:00:00"
                            },
                            {
                                "id": 2,
                                "username": "admin",
                                "email": "admin@example.com",
                                "firstName": "Admin",
                                "lastName": "User",
                                "role": "ADMIN",
                                "active": true,
                                "createdAt": "2024-01-01T10:00:00",
                                "updatedAt": "2024-01-01T10:00:00"
                            }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions"
        )
    })
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAll();
        
        return ResponseEntity.ok(users);
    }
    
    /**
     * Retrieves a user by their ID.
     * 
     * <p>This endpoint returns the user information for the specified user ID.
     * Users can only access their own information unless they are administrators.</p>
     * 
     * @param id the user ID
     * @return the user information (without password)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication, #id)")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves user information by user ID. Users can only access their own information " +
            "unless they are administrators."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Not Found Error",
                    value = """
                        {
                            "timestamp": "2024-01-01T10:00:00",
                            "status": 404,
                            "error": "Not Found",
                            "path": "/api/v1/users/999",
                            "message": "User not found"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions"
        )
    })
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        
        Optional<UserDto> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(user.get());
    }
    
    /**
     * Retrieves the current user's profile.
     * 
     * <p>This endpoint returns the profile information of the currently authenticated user.</p>
     * 
     * @param authentication the current authentication context
     * @return the current user's profile information
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Get current user profile",
        description = "Retrieves the profile information of the currently authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    public ResponseEntity<UserDto> getCurrentUserProfile(Authentication authentication) {
        String username = authentication.getName();
        Optional<UserDto> user = userService.findByUsername(username);
        
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(user.get());
    }
    
    /**
     * Updates the current user's profile.
     * 
     * <p>This endpoint allows users to update their own profile information.
     * Users cannot change their role or active status through this endpoint.</p>
     * 
     * @param request the profile update request
     * @param authentication the current authentication context
     * @return the updated user profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Update current user profile",
        description = "Updates the profile information of the currently authenticated user. " +
            "Users cannot change their role or active status through this endpoint."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    public ResponseEntity<UserDto> updateCurrentUserProfile(
            @Parameter(description = "Profile update request", required = true)
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        Optional<UserDto> user = userService.findByUsername(username);
        
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        UserDto updatedUser = userService.updateUser(user.get().getId(), request);
        
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Updates a user by their ID.
     * 
     * <p>This endpoint allows administrators to update any user's information,
     * including role and active status.</p>
     * 
     * @param id the user ID
     * @param request the user update request
     * @return the updated user information
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update user by ID",
        description = "Updates user information by user ID. Only administrators can update users. " +
            "This endpoint allows updating role and active status."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions"
        )
    })
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "User update request", required = true)
            @Valid @RequestBody UpdateUserRequest request) {
        
        UserDto updatedUser = userService.updateUser(id, request);
        
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Deletes a user by their ID.
     * 
     * <p>This endpoint allows administrators to delete users from the system.
     * This action is irreversible.</p>
     * 
     * @param id the user ID
     * @return no content on successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete user by ID",
        description = "Deletes a user from the system by user ID. Only administrators can delete users. " +
            "This action is irreversible."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "User deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions"
        )
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Changes the current user's password.
     * 
     * <p>This endpoint allows users to change their own password by providing
     * the current password and the new password.</p>
     * 
     * @param request the password change request
     * @param authentication the current authentication context
     * @return success message
     */
    @PutMapping("/password")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Change password",
        description = "Changes the password of the currently authenticated user. " +
            "Requires the current password for verification."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Password changed successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Message",
                    value = """
                        {
                            "message": "Password changed successfully"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data or current password is incorrect"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    public ResponseEntity<PasswordChangeResponse> changePassword(
            @Parameter(description = "Password change request", required = true)
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        Optional<UserDto> user = userService.findByUsername(username);
        
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        userService.updatePassword(user.get().getId(), request.newPassword());
        
        return ResponseEntity.ok(new PasswordChangeResponse("Password changed successfully"));
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
     * Response DTO for password change operations.
     */
    public record PasswordChangeResponse(String message) {}
    
    /**
     * Request DTO for password change operations.
     */
    public record PasswordChangeRequest(
        @Schema(description = "Current password", example = "currentPassword123")
        String currentPassword,
        
        @Schema(description = "New password", example = "newPassword123")
        String newPassword
    ) {}
}