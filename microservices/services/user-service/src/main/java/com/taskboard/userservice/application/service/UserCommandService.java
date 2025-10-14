package com.taskboard.userservice.application.service;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.application.dto.UserDto;
import com.taskboard.userservice.application.usecase.CreateUserUseCase;
import com.taskboard.userservice.application.usecase.UpdateUserUseCase;
import com.taskboard.userservice.application.usecase.DeleteUserUseCase;
import com.taskboard.userservice.domain.model.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for user command operations.
 * 
 * <p>This service follows the Single Responsibility Principle by handling only
 * write operations for users. It provides a clean separation between read and
 * write operations, making the code more maintainable and testable.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Creating new users</li>
 *   <li>Updating existing users</li>
 *   <li>Deleting users</li>
 *   <li>Password management</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserCommandService {

  private final CreateUserUseCase createUserUseCase;
  private final UpdateUserUseCase updateUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  /**
   * Creates a new user.
   * 
   * @param request the create user request
   * @return the created user DTO
   */
  public UserDto createUser(CreateUserRequest request) {
    log.debug("Creating user with username: {}", request.getUsername());
    return createUserUseCase.execute(request).getUser();
  }

  /**
   * Updates an existing user.
   * 
   * @param userId the user ID
   * @param request the update user request
   * @return the updated user DTO
   */
  public UserDto updateUser(Long userId, UpdateUserRequest request) {
    log.debug("Updating user with ID: {}", userId);
    return updateUserUseCase.execute(
        com.taskboard.userservice.application.dto.UpdateUserRequest.builder()
            .userId(userId)
            .username(request.getUsername())
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .role(request.getRole())
            .profileImageUrl(request.getProfileImageUrl())
            .build()
    ).getUser();
  }

  /**
   * Deletes a user by ID.
   * 
   * @param userId the user ID
   */
  public void deleteUser(Long userId) {
    log.debug("Deleting user with ID: {}", userId);
    deleteUserUseCase.execute(
        com.taskboard.userservice.application.dto.DeleteUserRequest.builder()
            .userId(userId)
            .build()
    );
  }

  /**
   * Updates a user's password.
   * 
   * @param userId the user ID
   * @param newPassword the new password
   */
  public void updatePassword(Long userId, String newPassword) {
    log.debug("Updating password for user ID: {}", userId);
    // This would typically involve hashing the password and updating the user
    // For now, we'll just log the operation
    log.info("Password update requested for user ID: {}", userId);
  }

  /**
   * Activates a user account.
   * 
   * @param userId the user ID
   * @return the updated user DTO
   */
  public UserDto activateUser(Long userId) {
    log.debug("Activating user with ID: {}", userId);
    UpdateUserRequest request = UpdateUserRequest.builder()
        .userId(userId)
        .status(UserStatus.ACTIVE)
        .build();
    return updateUser(userId, request);
  }

  /**
   * Deactivates a user account.
   * 
   * @param userId the user ID
   * @return the updated user DTO
   */
  public UserDto deactivateUser(Long userId) {
    log.debug("Deactivating user with ID: {}", userId);
    UpdateUserRequest request = UpdateUserRequest.builder()
        .userId(userId)
        .status(UserStatus.INACTIVE)
        .build();
    return updateUser(userId, request);
  }
}

