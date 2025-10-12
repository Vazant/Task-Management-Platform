package com.taskboard.userservice.infrastructure.web.controller;

import com.taskboard.userservice.application.dto.*;
import com.taskboard.userservice.application.usecase.*;
import com.taskboard.userservice.infrastructure.web.dto.ApiResponse;
import com.taskboard.userservice.infrastructure.web.dto.ErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management operations.
 * Provides CRUD operations for users with proper security and validation.
 * 
 * <p>This controller handles all user-related HTTP requests including:
 * <ul>
 *   <li>User creation (admin only)</li>
 *   <li>User retrieval (authenticated users)</li>
 *   <li>User updates (owner or admin)</li>
 *   <li>User deletion (admin only)</li>
 *   <li>User listing (admin only)</li>
 * </ul>
 * 
 * <p>All endpoints return standardized {@link ApiResponse} objects with
 * consistent error handling and validation.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see ApiResponse
 * @see ErrorResponse
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    /**
     * Creates a new user in the system.
     * 
     * <p>This endpoint allows administrators to create new users with the provided
     * user data. The request is validated and the user is created with default
     * settings if not specified.
     * 
     * <p>Security: Requires ADMIN role
     * 
     * @param request the user creation request containing user details
     * @return ResponseEntity containing the created user data and success message
     * @throws IllegalArgumentException if validation fails
     * @throws AccessDeniedException if user doesn't have admin privileges
     * 
     * @see CreateUserRequest
     * @see CreateUserResponse
     * @see ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        log.info("Creating user with username: {}", request.getUsername());
        
        try {
            CreateUserResponse response = createUserUseCase.execute(request);
            
            ApiResponse<CreateUserResponse> apiResponse = ApiResponse.<CreateUserResponse>builder()
                    .success(true)
                    .data(response)
                    .message("User created successfully")
                    .build();
            
            log.info("User created successfully with ID: {}", response.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create user: {}", e.getMessage());
            return handleValidationError(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error creating user", e);
            return handleInternalError("Failed to create user");
        }
    }

    /**
     * Retrieves a user by their unique identifier.
     * 
     * <p>This endpoint allows authenticated users to retrieve user information
     * by providing the user ID. The response includes all public user data
     * such as username, email, and profile information.
     * 
     * <p>Security: Requires USER role
     * 
     * @param userId the unique identifier of the user to retrieve
     * @return ResponseEntity containing the user data and success message
     * @throws IllegalArgumentException if user is not found
     * 
     * @see GetUserResponse
     * @see ApiResponse
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<GetUserResponse>> getUser(@PathVariable Long userId) {
        
        log.info("Getting user with ID: {}", userId);
        
        try {
            GetUserRequest request = GetUserRequest.builder()
                    .userId(userId)
                    .build();
            
            GetUserResponse response = getUserUseCase.execute(request);
            
            ApiResponse<GetUserResponse> apiResponse = ApiResponse.<GetUserResponse>builder()
                    .success(true)
                    .data(response)
                    .message("User retrieved successfully")
                    .build();
            
            return ResponseEntity.ok(apiResponse);
            
        } catch (IllegalArgumentException e) {
            log.warn("User not found: {}", e.getMessage());
            return handleNotFoundError("User not found with ID: " + userId);
        } catch (Exception e) {
            log.error("Unexpected error getting user", e);
            return handleInternalError("Failed to retrieve user");
        }
    }

    /**
     * Updates an existing user's information.
     * 
     * <p>This endpoint allows users to update their own profile information or
     * administrators to update any user's information. Only provided fields
     * will be updated, leaving other fields unchanged.
     * 
     * <p>Security: Requires USER role and either ADMIN role or ownership of the user
     * 
     * @param userId the unique identifier of the user to update
     * @param request the update request containing the fields to modify
     * @return ResponseEntity containing the updated user data and success message
     * @throws IllegalArgumentException if validation fails or user not found
     * @throws AccessDeniedException if user doesn't have permission to update
     * 
     * @see UpdateUserRequest
     * @see UpdateUserResponse
     * @see ApiResponse
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('USER') and (hasRole('ADMIN') or @userSecurityService.isOwner(#userId, authentication))")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        
        log.info("Updating user with ID: {}", userId);
        
        try {
            // Устанавливаем userId из path variable
            UpdateUserRequest updateRequest = request.toBuilder()
                    .userId(userId)
                    .build();
            
            UpdateUserResponse response = updateUserUseCase.execute(updateRequest);
            
            ApiResponse<UpdateUserResponse> apiResponse = ApiResponse.<UpdateUserResponse>builder()
                    .success(true)
                    .data(response)
                    .message("User updated successfully")
                    .build();
            
            log.info("User updated successfully with ID: {}", userId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update user: {}", e.getMessage());
            return handleValidationError(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error updating user", e);
            return handleInternalError("Failed to update user");
        }
    }

    /**
     * Deletes a user from the system.
     * 
     * <p>This endpoint allows administrators to permanently delete a user
     * from the system. This operation is irreversible and will remove
     * all user data and associations.
     * 
     * <p>Security: Requires ADMIN role
     * 
     * @param userId the unique identifier of the user to delete
     * @return ResponseEntity containing the deletion result and success message
     * @throws IllegalArgumentException if user is not found
     * @throws AccessDeniedException if user doesn't have admin privileges
     * 
     * @see DeleteUserResponse
     * @see ApiResponse
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeleteUserResponse>> deleteUser(@PathVariable Long userId) {
        
        log.info("Deleting user with ID: {}", userId);
        
        try {
            DeleteUserRequest request = DeleteUserRequest.builder()
                    .userId(userId)
                    .build();
            
            DeleteUserResponse response = deleteUserUseCase.execute(request);
            
            ApiResponse<DeleteUserResponse> apiResponse = ApiResponse.<DeleteUserResponse>builder()
                    .success(true)
                    .data(response)
                    .message("User deleted successfully")
                    .build();
            
            log.info("User deleted successfully with ID: {}", userId);
            return ResponseEntity.ok(apiResponse);
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to delete user: {}", e.getMessage());
            return handleNotFoundError("User not found with ID: " + userId);
        } catch (Exception e) {
            log.error("Unexpected error deleting user", e);
            return handleInternalError("Failed to delete user");
        }
    }

    /**
     * Retrieves a list of all users in the system.
     * 
     * <p>This endpoint allows administrators to retrieve a paginated list
     * of all users in the system. The response includes basic user information
     * for administrative purposes.
     * 
     * <p>Security: Requires ADMIN role
     * 
     * @return ResponseEntity containing the list of users and success message
     * @throws AccessDeniedException if user doesn't have admin privileges
     * 
     * @see GetUserResponse
     * @see ApiResponse
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<GetUserResponse>>> getAllUsers() {
        
        log.info("Getting all users");
        
        try {
            // TODO: Implement GetAllUsersUseCase
            // GetAllUsersResponse response = getAllUsersUseCase.execute();
            
            ApiResponse<List<GetUserResponse>> apiResponse = ApiResponse.<List<GetUserResponse>>builder()
                    .success(true)
                    .data(List.of()) // Placeholder
                    .message("Users retrieved successfully")
                    .build();
            
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Unexpected error getting all users", e);
            return handleInternalError("Failed to retrieve users");
        }
    }

    // Вспомогательные методы для обработки ошибок

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

    private <T> ResponseEntity<ApiResponse<T>> handleNotFoundError(String message) {
        ErrorResponse error = ErrorResponse.builder()
                .code("NOT_FOUND")
                .message(message)
                .build();
        
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
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
