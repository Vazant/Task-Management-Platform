package com.taskboard.userservice.application.service;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.application.dto.UserDto;
import com.taskboard.userservice.application.usecase.CreateUserUseCase;
import com.taskboard.userservice.application.usecase.GetUserUseCase;
import com.taskboard.userservice.application.usecase.UpdateUserUseCase;
import com.taskboard.userservice.application.usecase.DeleteUserUseCase;
import com.taskboard.userservice.application.usecase.AuthenticateUserUseCase;
import com.taskboard.userservice.application.validation.UserValidator;
import com.taskboard.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application service for user operations.
 * 
 * <p>This service acts as a facade for user-related operations and coordinates
 * between different use cases. It provides a high-level interface for user
 * management operations.
 * 
 * <p>Example usage:
 * 
 * <pre>{@code
 * UserService userService = new UserService(createUserUseCase, getUserUseCase, ...);
 * 
 * // Create a new user
 * UserDto user = userService.createUser(createUserRequest);
 * 
 * // Get user by ID
 * UserDto user = userService.getUserById(1L);
 * }</pre>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see CreateUserUseCase
 * @see GetUserUseCase
 * @see UpdateUserUseCase
 * @see DeleteUserUseCase
 * @see AuthenticateUserUseCase
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserQueryServiceInterface, UserCommandServiceInterface {

  private final CreateUserUseCase createUserUseCase;
  private final GetUserUseCase getUserUseCase;
  private final UpdateUserUseCase updateUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;
  private final AuthenticateUserUseCase authenticateUserUseCase;
  private final UserQueryService userQueryService;
  private final UserCommandService userCommandService;
  private final UserValidator userValidator;
  private final UserRepository userRepository;

  /**
   * Creates a new user.
   * 
   * @param request the create user request
   * @return the created user DTO
   */
  @Override
  public UserDto createUser(CreateUserRequest request) {
    log.debug("Creating user with username: {}", request.getUsername());
    userValidator.validateCreateUserRequest(request);
    return userCommandService.createUser(request);
  }

  /**
   * Gets a user by ID.
   * 
   * @param userId the user ID
   * @return the user DTO if found
   */
  @Override
  @Transactional(readOnly = true)
  public Optional<UserDto> getUserById(Long userId) {
    log.debug("Getting user by ID: {}", userId);
    return userQueryService.getUserById(userId);
  }

  /**
   * Gets a user by username.
   * 
   * @param username the username
   * @return the user DTO if found
   */
  @Override
  @Transactional(readOnly = true)
  public Optional<UserDto> getUserByUsername(String username) {
    log.debug("Getting user by username: {}", username);
    return userQueryService.getUserByUsername(username);
  }

  /**
   * Gets a user by email.
   * 
   * @param email the email address
   * @return the user DTO if found
   */
  @Transactional(readOnly = true)
  public Optional<UserDto> getUserByEmail(String email) {
    log.debug("Getting user by email: {}", email);
    return userRepository.findByEmail(email)
        .map(UserDto::fromUser);
  }

  /**
   * Gets all users.
   * 
   * @return list of all user DTOs
   */
  @Transactional(readOnly = true)
  public List<UserDto> getAllUsers() {
    log.debug("Getting all users");
    return userRepository.findAll().stream()
        .map(UserDto::fromUser)
        .collect(Collectors.toList());
  }

  /**
   * Updates a user.
   * 
   * @param userId the user ID
   * @param request the update user request
   * @return the updated user DTO
   */
  public UserDto updateUser(Long userId, com.taskboard.userservice.application.dto.UpdateUserRequest request) {
    log.debug("Updating user with ID: {}", userId);
    return updateUserUseCase.execute(request).getUser();
  }

  /**
   * Deletes a user.
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
   * Authenticates a user.
   * 
   * @param username the username
   * @param password the password
   * @return the authentication response
   */
  @Transactional(readOnly = true)
  public com.taskboard.userservice.application.dto.AuthenticateUserResponse authenticateUser(String username, String password) {
    log.debug("Authenticating user: {}", username);
    return authenticateUserUseCase.execute(
        com.taskboard.userservice.application.dto.AuthenticateUserRequest.builder()
            .username(username)
            .password(password)
            .build()
    );
  }

  /**
   * Checks if a user exists by username.
   * 
   * @param username the username
   * @return true if user exists
   */
  @Transactional(readOnly = true)
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  /**
   * Checks if a user exists by email.
   * 
   * @param email the email address
   * @return true if user exists
   */
  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * Gets all users (alias for getAllUsers).
   * 
   * @return list of all user DTOs
   */
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return getAllUsers();
  }

  /**
   * Gets a user by ID (alias for getUserById).
   * 
   * @param userId the user ID
   * @return the user DTO if found
   */
  @Transactional(readOnly = true)
  public Optional<UserDto> findById(Long userId) {
    return getUserById(userId);
  }

  /**
   * Gets the total number of users.
   * 
   * @return the total count of users
   */
  @Override
  @Transactional(readOnly = true)
  public long getUserCount() {
    return userRepository.count();
  }

  /**
   * Gets users by role.
   * 
   * @param role the user role
   * @return list of users with the specified role
   */
  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getUsersByRole(String role) {
    return userQueryService.getUsersByRole(role);
  }

  /**
   * Gets active users.
   * 
   * @return list of active user DTOs
   */
  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getActiveUsers() {
    return userQueryService.getActiveUsers();
  }

  /**
   * Updates a user's password.
   * 
   * @param userId the user ID
   * @param newPassword the new password
   */
  @Override
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
  @Override
  public UserDto activateUser(Long userId) {
    return userCommandService.activateUser(userId);
  }

  /**
   * Deactivates a user account.
   * 
   * @param userId the user ID
   * @return the updated user DTO
   */
  @Override
  public UserDto deactivateUser(Long userId) {
    return userCommandService.deactivateUser(userId);
  }

  /**
   * Finds a user by username (alias for getUserByUsername).
   * 
   * @param username the username
   * @return the user DTO if found
   */
  @Transactional(readOnly = true)
  public Optional<UserDto> findByUsername(String username) {
    return getUserByUsername(username);
  }
}
