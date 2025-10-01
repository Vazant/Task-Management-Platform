package com.taskboard.user.service;

import com.taskboard.api.dto.LoginRequest;
import com.taskboard.api.dto.LoginResponse;
import com.taskboard.api.dto.RegisterRequest;
import com.taskboard.api.dto.UpdateProfileRequest;
import com.taskboard.api.dto.ChangePasswordRequest;
import com.taskboard.user.dto.MessageResponse;
import com.taskboard.user.dto.RefreshTokenRequest;
import com.taskboard.user.dto.RefreshTokenResponse;
import com.taskboard.user.dto.ForgotPasswordRequest;
import com.taskboard.user.dto.ResetPasswordRequest;
import com.taskboard.user.dto.UserDto;
import com.taskboard.user.dto.UserProfileDto;

import java.util.List;

/**
 * Service interface for user operations.
 */
public interface UserService {

    /**
     * Authenticate user and generate JWT tokens.
     *
     * @param request the login request
     * @return the login response with user data and tokens
     */
    LoginResponse login(LoginRequest request);

    /**
     * Register a new user.
     *
     * @param request the register request
     * @return the login response with user data and tokens
     */
    LoginResponse register(RegisterRequest request);

    /**
     * Get user profile by user ID.
     *
     * @param userId the user ID
     * @return the user profile
     */
    UserProfileDto getProfile(String userId);

    /**
     * Get current user's profile.
     *
     * @param username the username from authentication
     * @return the user profile
     */
    UserProfileDto getCurrentUserProfile(String username);

    /**
     * Update user profile.
     *
     * @param userId the user ID
     * @param request the update profile request
     * @return the updated user profile
     */
    UserProfileDto updateProfile(String userId, UpdateProfileRequest request);

    /**
     * Change user password.
     *
     * @param userId the user ID
     * @param request the change password request
     */
    void changePassword(String userId, ChangePasswordRequest request);

    /**
     * Refresh JWT tokens.
     *
     * @param request the refresh token request
     * @return the new tokens
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    /**
     * Initiate password reset process.
     *
     * @param request the forgot password request
     * @return the response message
     */
    MessageResponse forgotPassword(ForgotPasswordRequest request);

    /**
     * Reset password with token.
     *
     * @param request the reset password request
     * @return the response message
     */
    MessageResponse resetPassword(ResetPasswordRequest request);

    /**
     * Update user avatar.
     *
     * @param userId the user ID
     * @param avatarUrl the avatar URL
     * @return the updated user profile
     */
    UserProfileDto updateAvatar(String userId, String avatarUrl);

    /**
     * Delete user avatar.
     *
     * @param userId the user ID
     * @return the updated user profile
     */
    UserProfileDto deleteAvatar(String userId);

    /**
     * Find user by username.
     *
     * @param username the username
     * @return the user DTO
     */
    UserDto findByUsername(String username);

    /**
     * Check if a user exists by email.
     *
     * @param email the email
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists by username.
     *
     * @param username the username
     * @return true if user exists
     */
    boolean existsByUsername(String username);

    // Admin operations

    /**
     * Get all users with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return list of users
     */
    List<UserDto> getAllUsers(int page, int size);

    /**
     * Get user by ID.
     *
     * @param userId the user ID
     * @return the user profile
     */
    UserProfileDto getUserById(String userId);

    /**
     * Create new user.
     *
     * @param userDto the user data
     * @return the created user profile
     */
    UserProfileDto createUser(UserDto userDto);

    /**
     * Update user.
     *
     * @param userId the user ID
     * @param userDto the user data
     * @return the updated user profile
     */
    UserProfileDto updateUser(String userId, UserDto userDto);

    /**
     * Delete user.
     *
     * @param userId the user ID
     */
    void deleteUser(String userId);

    /**
     * Toggle user block status.
     *
     * @param userId the user ID
     * @return the updated user profile
     */
    UserProfileDto toggleUserBlock(String userId);

    /**
     * Update user role.
     *
     * @param userId the user ID
     * @param role the new role
     * @return the updated user profile
     */
    UserProfileDto updateUserRole(String userId, String role);

    /**
     * Search users by email or name.
     *
     * @param query the search query
     * @return list of matching users
     */
    List<UserDto> searchUsers(String query);

    /**
     * Update user's last login timestamp.
     *
     * @param userId the user ID
     */
    void updateLastLogin(String userId);
}
