package com.taskboard.userservice.application.service;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.application.dto.UserDto;

/**
 * Interface for user command operations.
 * 
 * <p>This interface follows the Dependency Inversion Principle by defining
 * the contract for user command operations without depending on concrete
 * implementations. This allows for better testability and flexibility.
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserCommandServiceInterface {

  /**
   * Creates a new user.
   * 
   * @param request the create user request
   * @return the created user DTO
   */
  UserDto createUser(CreateUserRequest request);

  /**
   * Updates an existing user.
   * 
   * @param userId the user ID
   * @param request the update user request
   * @return the updated user DTO
   */
  UserDto updateUser(Long userId, UpdateUserRequest request);

  /**
   * Deletes a user by ID.
   * 
   * @param userId the user ID
   */
  void deleteUser(Long userId);

  /**
   * Updates a user's password.
   * 
   * @param userId the user ID
   * @param newPassword the new password
   */
  void updatePassword(Long userId, String newPassword);

  /**
   * Activates a user account.
   * 
   * @param userId the user ID
   * @return the updated user DTO
   */
  UserDto activateUser(Long userId);

  /**
   * Deactivates a user account.
   * 
   * @param userId the user ID
   * @return the updated user DTO
   */
  UserDto deactivateUser(Long userId);
}

