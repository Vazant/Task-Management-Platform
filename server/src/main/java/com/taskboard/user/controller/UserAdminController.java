package com.taskboard.user.controller;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.user.dto.UserDto;
import com.taskboard.user.dto.UserProfileDto;
import com.taskboard.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for administrative user operations. 
 * Requires ADMIN role for all operations.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Administration", description = "APIs for administrative user management")
@SecurityRequirement(name = "bearerAuth")
public class UserAdminController {

  private final UserService userService;

  /**
   * Get all users with pagination and sorting.
   * 
   * @param page page number (default: 0)
   * @param size page size (default: 20)
   * @param sortBy sort field (default: createdAt)
   * @param sortDir sort direction (default: desc)
   * @return paginated list of users
   */
  @GetMapping
  @Operation(summary = "Get all users", description = "Retrieve all users with pagination (Admin only)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
  })
  public ResponseEntity<ApiResponse<Page<UserDto>>> getAllUsers(
      @RequestParam(defaultValue = "0") int page, 
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    log.info("Admin request to get all users: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
    
    Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);
    
    Page<UserDto> users = userService.getAllUsers(pageable);
    return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
  }

  /**
   * Get user by ID.
   * 
   * @param userId user identifier
   * @return user profile
   */
  @GetMapping("/{userId}")
  @Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID (Admin only)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
  })
  public ResponseEntity<ApiResponse<UserProfileDto>> getUserById(
      @Parameter(description = "User ID", required = true) @PathVariable String userId) {
    log.info("Admin request to get user by ID: {}", userId);
    UserProfileDto user = userService.getUserById(userId);
    return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
  }

  /**
   * Create new user.
   * 
   * @param userDto user data
   * @return created user profile
   */
  @PostMapping
  @Operation(summary = "Create user", description = "Create a new user (Admin only)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request or validation error"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
  })
  public ResponseEntity<ApiResponse<UserProfileDto>> createUser(@Valid @RequestBody UserDto userDto) {
    log.info("Admin request to create user: {}", userDto.getEmail());
    UserProfileDto createdUser = userService.createUser(userDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(createdUser, "User created successfully"));
  }

  /**
   * Update user.
   * 
   * @param userId user identifier
   * @param userDto user data
   * @return updated user profile
   */
  @PutMapping("/{userId}")
  @Operation(summary = "Update user", description = "Update an existing user (Admin only)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request or validation error"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
  })
  public ResponseEntity<ApiResponse<UserProfileDto>> updateUser(
      @Parameter(description = "User ID", required = true) @PathVariable String userId, 
      @Valid @RequestBody UserDto userDto) {
    log.info("Admin request to update user: {}", userId);
    UserProfileDto updatedUser = userService.updateUser(userId, userDto);
    return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
  }

  /**
   * Delete user.
   * 
   * @param userId user identifier
   * @return success response
   */
  @DeleteMapping("/{userId}")
  @Operation(summary = "Delete user", description = "Delete a user (Admin only)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
  })
  public ResponseEntity<ApiResponse<Void>> deleteUser(
      @Parameter(description = "User ID", required = true) @PathVariable String userId) {
    log.info("Admin request to delete user: {}", userId);
    userService.deleteUser(userId);
    return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
  }

  /**
   * Block/unblock user.
   * 
   * @param userId user identifier
   * @return updated user profile
   */
  @PutMapping("/{userId}/block")
  @Operation(summary = "Toggle user block", description = "Block or unblock a user (Admin only)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User block status updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
  })
  public ResponseEntity<ApiResponse<UserProfileDto>> toggleUserBlock(
      @Parameter(description = "User ID", required = true) @PathVariable String userId) {
    log.info("Admin request to toggle user block: {}", userId);
    UserProfileDto user = userService.toggleUserBlock(userId);
    return ResponseEntity.ok(ApiResponse.success(user, "User block status updated successfully"));
  }

  /**
   * Update user role.
   * 
   * @param userId user identifier
   * @param role new role
   * @return updated user profile
   */
  @PutMapping("/{userId}/role")
  @Operation(summary = "Update user role", description = "Update user role (Admin only)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User role updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid role"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
  })
  public ResponseEntity<ApiResponse<UserProfileDto>> updateUserRole(
      @Parameter(description = "User ID", required = true) @PathVariable String userId, 
      @Parameter(description = "New role", required = true) @RequestParam String role) {
    log.info("Admin request to update user role: userId={}, role={}", userId, role);
    UserProfileDto user = userService.updateUserRole(userId, role);
    return ResponseEntity.ok(ApiResponse.success(user, "User role updated successfully"));
  }

  /**
   * Search users by email or name.
   * 
   * @param query search query
   * @param page page number (default: 0)
   * @param size page size (default: 20)
   * @return paginated list of matching users
   */
  @GetMapping("/search")
  @Operation(summary = "Search users", description = "Search users by email or name (Admin only)")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
  })
  public ResponseEntity<ApiResponse<Page<UserDto>>> searchUsers(
      @Parameter(description = "Search query", required = true) @RequestParam String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    log.info("Admin request to search users: query={}, page={}, size={}", query, page, size);
    
    Pageable pageable = PageRequest.of(page, size);
    Page<UserDto> users = userService.searchUsers(query, pageable);
    return ResponseEntity.ok(ApiResponse.success(users, "Search completed successfully"));
  }
}
