package com.taskboard.userservice.application.service;

import com.taskboard.userservice.application.dto.UserDto;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for user query operations.
 * 
 * <p>This service follows the Single Responsibility Principle by handling only
 * read operations for users. It provides a clean separation between read and
 * write operations, making the code more maintainable and testable.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Finding users by various criteria</li>
 *   <li>Retrieving user lists</li>
 *   <li>Checking user existence</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserQueryService {

  private final UserRepository userRepository;

  /**
   * Gets a user by ID.
   * 
   * @param userId the user ID
   * @return the user DTO if found
   */
  public Optional<UserDto> getUserById(Long userId) {
    log.debug("Getting user by ID: {}", userId);
    return userRepository.findById(userId)
        .map(UserDto::fromUser);
  }

  /**
   * Gets a user by username.
   * 
   * @param username the username
   * @return the user DTO if found
   */
  public Optional<UserDto> getUserByUsername(String username) {
    log.debug("Getting user by username: {}", username);
    return userRepository.findByUsername(username)
        .map(UserDto::fromUser);
  }

  /**
   * Gets a user by email.
   * 
   * @param email the email address
   * @return the user DTO if found
   */
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
  public List<UserDto> getAllUsers() {
    log.debug("Getting all users");
    return userRepository.findAll().stream()
        .map(UserDto::fromUser)
        .toList();
  }

  /**
   * Gets users by role.
   * 
   * @param role the user role
   * @return list of users with the specified role
   */
  public List<UserDto> getUsersByRole(String role) {
    log.debug("Getting users by role: {}", role);
    return userRepository.findByRole(UserRole.valueOf(role.toUpperCase())).stream()
        .map(UserDto::fromUser)
        .toList();
  }

  /**
   * Gets active users.
   * 
   * @return list of active user DTOs
   */
  public List<UserDto> getActiveUsers() {
    log.debug("Getting active users");
    return userRepository.findByStatus(UserStatus.ACTIVE).stream()
        .map(UserDto::fromUser)
        .toList();
  }

  /**
   * Checks if a user exists by username.
   * 
   * @param username the username
   * @return true if user exists
   */
  public boolean existsByUsername(String username) {
    log.debug("Checking if user exists by username: {}", username);
    return userRepository.existsByUsername(username);
  }

  /**
   * Checks if a user exists by email.
   * 
   * @param email the email address
   * @return true if user exists
   */
  public boolean existsByEmail(String email) {
    log.debug("Checking if user exists by email: {}", email);
    return userRepository.existsByEmail(email);
  }

  /**
   * Gets the total number of users.
   * 
   * @return the total count of users
   */
  public long getUserCount() {
    log.debug("Getting user count");
    return userRepository.count();
  }
}

