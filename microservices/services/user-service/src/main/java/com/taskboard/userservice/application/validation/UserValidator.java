package com.taskboard.userservice.application.validation;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.application.exception.UserServiceException;
import com.taskboard.userservice.application.service.UserQueryServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Validator for user-related operations.
 * 
 * <p>This class provides comprehensive validation for user data, ensuring data integrity
 * and business rule compliance. It follows the Single Responsibility Principle by
 * focusing solely on validation logic.
 * 
 * <p>Validation includes:
 * <ul>
 *   <li>Username format and uniqueness</li>
 *   <li>Email format and uniqueness</li>
 *   <li>Password strength requirements</li>
 *   <li>Name format validation</li>
 *   <li>Business rule enforcement</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {

  private final UserQueryServiceInterface userQueryService;

  // Validation patterns
  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
  private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,50}$");
  private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

  // Validation constants
  private static final int MIN_USERNAME_LENGTH = 3;
  private static final int MAX_USERNAME_LENGTH = 20;
  private static final int MIN_PASSWORD_LENGTH = 8;
  private static final int MAX_PASSWORD_LENGTH = 128;
  private static final int MIN_NAME_LENGTH = 2;
  private static final int MAX_NAME_LENGTH = 50;

  /**
   * Validates a create user request.
   * 
   * @param request the create user request
   * @throws UserServiceException if validation fails
   */
  public void validateCreateUserRequest(CreateUserRequest request) {
    log.debug("Validating create user request for username: {}", request.getUsername());
    
    validateUsername(request.getUsername());
    validateEmail(request.getEmail());
    validatePassword(request.getPassword());
    validateFirstName(request.getFirstName());
    validateLastName(request.getLastName());
    
    // Check uniqueness
    if (userQueryService.existsByUsername(request.getUsername())) {
      throw new UserServiceException("Username already exists: " + request.getUsername());
    }
    
    if (userQueryService.existsByEmail(request.getEmail())) {
      throw new UserServiceException("Email already exists: " + request.getEmail());
    }
    
    log.debug("Create user request validation passed");
  }

  /**
   * Validates an update user request.
   * 
   * @param request the update user request
   * @param currentUsername the current username
   * @param currentEmail the current email
   * @throws UserServiceException if validation fails
   */
  public void validateUpdateUserRequest(UpdateUserRequest request, String currentUsername, String currentEmail) {
    log.debug("Validating update user request for user ID: {}", request.getUserId());
    
    if (StringUtils.hasText(request.getUsername())) {
      validateUsername(request.getUsername());
      
      // Check uniqueness only if username changed
      if (!request.getUsername().equals(currentUsername) && 
          userQueryService.existsByUsername(request.getUsername())) {
        throw new UserServiceException("Username already exists: " + request.getUsername());
      }
    }
    
    if (StringUtils.hasText(request.getEmail())) {
      validateEmail(request.getEmail());
      
      // Check uniqueness only if email changed
      if (!request.getEmail().equals(currentEmail) && 
          userQueryService.existsByEmail(request.getEmail())) {
        throw new UserServiceException("Email already exists: " + request.getEmail());
      }
    }
    
    if (StringUtils.hasText(request.getFirstName())) {
      validateFirstName(request.getFirstName());
    }
    
    if (StringUtils.hasText(request.getLastName())) {
      validateLastName(request.getLastName());
    }
    
    log.debug("Update user request validation passed");
  }

  /**
   * Validates username format and length.
   * 
   * @param username the username to validate
   * @throws UserServiceException if validation fails
   */
  private void validateUsername(String username) {
    if (!StringUtils.hasText(username)) {
      throw new UserServiceException("Username cannot be empty");
    }
    
    if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
      throw new UserServiceException(
          String.format("Username must be between %d and %d characters", 
              MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH));
    }
    
    if (!USERNAME_PATTERN.matcher(username).matches()) {
      throw new UserServiceException(
          "Username can only contain letters, numbers, and underscores");
    }
  }

  /**
   * Validates email format.
   * 
   * @param email the email to validate
   * @throws UserServiceException if validation fails
   */
  private void validateEmail(String email) {
    if (!StringUtils.hasText(email)) {
      throw new UserServiceException("Email cannot be empty");
    }
    
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new UserServiceException("Invalid email format");
    }
  }

  /**
   * Validates password strength.
   * 
   * @param password the password to validate
   * @throws UserServiceException if validation fails
   */
  private void validatePassword(String password) {
    if (!StringUtils.hasText(password)) {
      throw new UserServiceException("Password cannot be empty");
    }
    
    if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
      throw new UserServiceException(
          String.format("Password must be between %d and %d characters", 
              MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
    }
    
    if (!PASSWORD_PATTERN.matcher(password).matches()) {
      throw new UserServiceException(
          "Password must contain at least one lowercase letter, one uppercase letter, " +
          "one digit, and one special character");
    }
  }

  /**
   * Validates first name format.
   * 
   * @param firstName the first name to validate
   * @throws UserServiceException if validation fails
   */
  private void validateFirstName(String firstName) {
    if (!StringUtils.hasText(firstName)) {
      throw new UserServiceException("First name cannot be empty");
    }
    
    if (firstName.length() < MIN_NAME_LENGTH || firstName.length() > MAX_NAME_LENGTH) {
      throw new UserServiceException(
          String.format("First name must be between %d and %d characters", 
              MIN_NAME_LENGTH, MAX_NAME_LENGTH));
    }
    
    if (!NAME_PATTERN.matcher(firstName).matches()) {
      throw new UserServiceException(
          "First name can only contain letters and spaces");
    }
  }

  /**
   * Validates last name format.
   * 
   * @param lastName the last name to validate
   * @throws UserServiceException if validation fails
   */
  private void validateLastName(String lastName) {
    if (!StringUtils.hasText(lastName)) {
      throw new UserServiceException("Last name cannot be empty");
    }
    
    if (lastName.length() < MIN_NAME_LENGTH || lastName.length() > MAX_NAME_LENGTH) {
      throw new UserServiceException(
          String.format("Last name must be between %d and %d characters", 
              MIN_NAME_LENGTH, MAX_NAME_LENGTH));
    }
    
    if (!NAME_PATTERN.matcher(lastName).matches()) {
      throw new UserServiceException(
          "Last name can only contain letters and spaces");
    }
  }
}

