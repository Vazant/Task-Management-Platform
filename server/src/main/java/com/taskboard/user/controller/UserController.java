package com.taskboard.user.controller;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.user.dto.UserProfileDto;
import com.taskboard.user.dto.UserUpdateRequest;
import com.taskboard.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user profile operations.
 * Provides endpoints for users to manage their own profiles.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
@Tag(name = "User Profile", description = "APIs for user profile management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile.
     * 
     * @param authentication user authentication
     * @return user profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current user's profile information")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(Authentication authentication) {
        log.info("Getting profile for user: {}", authentication.getName());
        UserProfileDto profile = userService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved successfully"));
    }

    /**
     * Update current user profile.
     * 
     * @param request profile update request
     * @param authentication user authentication
     * @return updated user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update current user's profile information")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request or validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {
        log.info("Updating profile for user: {}", authentication.getName());
        UserProfileDto updatedProfile = userService.updateUserProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(updatedProfile, "Profile updated successfully"));
    }

    /**
     * Change user password.
     * 
     * @param request password change request
     * @param authentication user authentication
     * @return success response
     */
    @PutMapping("/password")
    @Operation(summary = "Change password", description = "Change current user's password")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request or invalid current password"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication) {
        log.info("Changing password for user: {}", authentication.getName());
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    /**
     * Upload user avatar.
     * 
     * @param avatarUrl avatar URL
     * @param authentication user authentication
     * @return updated user profile
     */
    @PutMapping("/avatar")
    @Operation(summary = "Update avatar", description = "Update current user's avatar")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Avatar updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<UserProfileDto>> updateAvatar(
            @Parameter(description = "Avatar URL", required = true) @RequestParam String avatarUrl,
            Authentication authentication) {
        log.info("Updating avatar for user: {}", authentication.getName());
        UserProfileDto updatedProfile = userService.updateAvatar(authentication.getName(), avatarUrl);
        return ResponseEntity.ok(ApiResponse.success(updatedProfile, "Avatar updated successfully"));
    }

    /**
     * Delete user account.
     * 
     * @param authentication user authentication
     * @return success response
     */
    @DeleteMapping("/account")
    @Operation(summary = "Delete account", description = "Delete current user's account")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Void>> deleteAccount(Authentication authentication) {
        log.info("Deleting account for user: {}", authentication.getName());
        userService.deleteAccount(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Account deleted successfully"));
    }
}
