package com.taskboard.userservice.application.service;

import com.taskboard.userservice.application.dto.UserDto;

import java.util.List;
import java.util.Optional;

/**
 * Interface for user query operations.
 * 
 * <p>This interface follows the Dependency Inversion Principle by defining
 * the contract for user query operations without depending on concrete
 * implementations. This allows for better testability and flexibility.
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserQueryServiceInterface {

  /**
   * Gets a user by ID.
   * 
   * @param userId the user ID
   * @return the user DTO if found
   */
  Optional<UserDto> getUserById(Long userId);

  /**
   * Gets a user by username.
   * 
   * @param username the username
   * @return the user DTO if found
   */
  Optional<UserDto> getUserByUsername(String username);

  /**
   * Gets a user by email.
   * 
   * @param email the email address
   * @return the user DTO if found
   */
  Optional<UserDto> getUserByEmail(String email);

  /**
   * Gets all users.
   * 
   * @return list of all user DTOs
   */
  List<UserDto> getAllUsers();

  /**
   * Gets users by role.
   * 
   * @param role the user role
   * @return list of users with the specified role
   */
  List<UserDto> getUsersByRole(String role);

  /**
   * Gets active users.
   * 
   * @return list of active user DTOs
   */
  List<UserDto> getActiveUsers();

  /**
   * Checks if a user exists by username.
   * 
   * @param username the username
   * @return true if user exists
   */
  boolean existsByUsername(String username);

  /**
   * Checks if a user exists by email.
   * 
   * @param email the email address
   * @return true if user exists
   */
  boolean existsByEmail(String email);

  /**
   * Gets the total number of users.
   * 
   * @return the total count of users
   */
  long getUserCount();
}

