package com.taskboard.userservice.domain.service;

import com.taskboard.userservice.domain.exception.UserAlreadyExistsException;
import com.taskboard.userservice.domain.exception.UserDomainException;
import com.taskboard.userservice.domain.exception.UserNotFoundException;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.repository.UserRepository;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * Domain service for User business logic.
 *
 * <p>This service encapsulates complex business logic that doesn't naturally fit within a single
 * User entity. It coordinates between multiple entities and enforces business rules that span
 * across the domain.
 *
 * <p>The service is responsible for:
 *
 * <ul>
 *   <li>Complex user creation and validation
 *   <li>User lifecycle management
 *   <li>Business rule enforcement
 *   <li>Cross-entity operations
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class UserDomainService {

  private final UserRepository userRepository;

  /**
   * Constructs a new UserDomainService with the specified repository.
   *
   * @param userRepository the user repository
   * @throws IllegalArgumentException if userRepository is null
   */
  public UserDomainService(UserRepository userRepository) {
    this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
  }

  /**
   * Creates a new user with business rule validation.
   *
   * <p>This method enforces business rules for user creation:
   *
   * <ul>
   *   <li>Username must be unique
   *   <li>Email must be unique
   *   <li>Password must meet security requirements
   *   <li>Default role assignment
   * </ul>
   *
   * @param username unique username
   * @param email unique email address
   * @param passwordHash hashed password
   * @param firstName user's first name
   * @param lastName user's last name
   * @param role user's role (optional, defaults to USER)
   * @return the created user
   * @throws UserAlreadyExistsException if username or email already exists
   * @throws UserDomainException if business rules are violated
   */
  public User createUser(
      String username,
      String email,
      String passwordHash,
      String firstName,
      String lastName,
      UserRole role) {

    // Validate business rules
    validateUserCreation(username, email, passwordHash, firstName, lastName);

    // Check for existing user with same username
    if (userRepository.existsByUsername(username)) {
      throw new UserAlreadyExistsException(username);
    }

    // Check for existing user with same email
    if (userRepository.existsByEmail(email)) {
      throw new UserAlreadyExistsException(email, true);
    }

    // Set default role if not provided
    UserRole userRole = role != null ? role : UserRole.USER;

    // Create new user
    User user = new User(username, email, passwordHash, firstName, lastName, userRole);

    // Save user
    return userRepository.save(user);
  }

  /**
   * Updates user profile information.
   *
   * @param userId the ID of the user to update
   * @param firstName new first name
   * @param lastName new last name
   * @param email new email address
   * @return the updated user
   * @throws UserNotFoundException if user is not found
   * @throws UserDomainException if business rules are violated
   */
  public User updateUserProfile(Long userId, String firstName, String lastName, String email) {
    User user = findUserById(userId);

    // Validate email uniqueness if changed
    if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
      throw new UserAlreadyExistsException(email, true);
    }

    // Update profile
    user.updateProfile(firstName, lastName, email);

    return userRepository.save(user);
  }

  /**
   * Changes user password.
   *
   * @param userId the ID of the user
   * @param newPasswordHash new hashed password
   * @return the updated user
   * @throws UserNotFoundException if user is not found
   * @throws UserDomainException if password validation fails
   */
  public User changeUserPassword(Long userId, String newPasswordHash) {
    User user = findUserById(userId);

    // Validate password
    validatePassword(newPasswordHash);

    // Update password
    user.changePassword(newPasswordHash);

    return userRepository.save(user);
  }

  /**
   * Updates user role.
   *
   * @param userId the ID of the user
   * @param newRole new user role
   * @param updatedBy the user performing the update
   * @return the updated user
   * @throws UserNotFoundException if user is not found
   * @throws UserDomainException if role update is not allowed
   */
  public User updateUserRole(Long userId, UserRole newRole, User updatedBy) {
    User user = findUserById(userId);

    // Validate role update permissions
    validateRoleUpdate(user, newRole, updatedBy);

    // Update role
    user.updateRole(newRole);

    return userRepository.save(user);
  }

  /**
   * Activates a user account.
   *
   * @param userId the ID of the user to activate
   * @return the activated user
   * @throws UserNotFoundException if user is not found
   */
  public User activateUser(Long userId) {
    User user = findUserById(userId);
    user.activate();
    return userRepository.save(user);
  }

  /**
   * Deactivates a user account.
   *
   * @param userId the ID of the user to deactivate
   * @return the deactivated user
   * @throws UserNotFoundException if user is not found
   */
  public User deactivateUser(Long userId) {
    User user = findUserById(userId);
    user.deactivate();
    return userRepository.save(user);
  }

  /**
   * Blocks a user account.
   *
   * @param userId the ID of the user to block
   * @param blockedBy the user performing the block action
   * @return the blocked user
   * @throws UserNotFoundException if user is not found
   * @throws UserDomainException if blocking is not allowed
   */
  public User blockUser(Long userId, User blockedBy) {
    User user = findUserById(userId);

    // Validate blocking permissions
    validateBlockingPermissions(user, blockedBy);

    user.block();
    return userRepository.save(user);
  }

  /**
   * Records user login.
   *
   * @param userId the ID of the user
   * @return the updated user
   * @throws UserNotFoundException if user is not found
   */
  public User recordUserLogin(Long userId) {
    User user = findUserById(userId);
    user.recordLogin();
    return userRepository.save(user);
  }

  /**
   * Verifies user email.
   *
   * @param userId the ID of the user
   * @return the updated user
   * @throws UserNotFoundException if user is not found
   */
  public User verifyUserEmail(Long userId) {
    User user = findUserById(userId);
    user.verifyEmail();
    return userRepository.save(user);
  }

  /**
   * Finds a user by ID with validation.
   *
   * @param userId the user ID
   * @return the user
   * @throws UserNotFoundException if user is not found
   */
  public User findUserById(Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
  }

  /**
   * Finds a user by username with validation.
   *
   * @param username the username
   * @return the user
   * @throws UserNotFoundException if user is not found
   */
  public User findUserByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));
  }

  /**
   * Validates user creation parameters.
   *
   * @param username username to validate
   * @param email email to validate
   * @param passwordHash password hash to validate
   * @param firstName first name to validate
   * @param lastName last name to validate
   * @throws UserDomainException if validation fails
   */
  private void validateUserCreation(
      String username, String email, String passwordHash, String firstName, String lastName) {
    if (username == null || username.trim().isEmpty()) {
      throw new UserDomainException("Username cannot be null or empty");
    }

    if (username.length() < 3 || username.length() > 50) {
      throw new UserDomainException("Username must be between 3 and 50 characters");
    }

    if (email == null || email.trim().isEmpty()) {
      throw new UserDomainException("Email cannot be null or empty");
    }

    if (!isValidEmail(email)) {
      throw new UserDomainException("Invalid email format");
    }

    if (passwordHash == null || passwordHash.trim().isEmpty()) {
      throw new UserDomainException("Password hash cannot be null or empty");
    }

    if (firstName == null || firstName.trim().isEmpty()) {
      throw new UserDomainException("First name cannot be null or empty");
    }

    if (lastName == null || lastName.trim().isEmpty()) {
      throw new UserDomainException("Last name cannot be null or empty");
    }
  }

  /**
   * Validates password requirements.
   *
   * @param passwordHash password hash to validate
   * @throws UserDomainException if password validation fails
   */
  private void validatePassword(String passwordHash) {
    if (passwordHash == null || passwordHash.trim().isEmpty()) {
      throw new UserDomainException("Password hash cannot be null or empty");
    }

    // Additional password validation can be added here
    // For example, checking password strength, length, etc.
  }

  /**
   * Validates role update permissions.
   *
   * @param user the user being updated
   * @param newRole the new role
   * @param updatedBy the user performing the update
   * @throws UserDomainException if role update is not allowed
   */
  private void validateRoleUpdate(User user, UserRole newRole, User updatedBy) {
    // Users cannot change their own role
    if (user.getId().equals(updatedBy.getId())) {
      throw new UserDomainException("Users cannot change their own role");
    }

    // Only users with higher priority can change roles
    if (!updatedBy.getRole().canManage(newRole)) {
      throw new UserDomainException("Insufficient permissions to assign role: " + newRole);
    }
  }

  /**
   * Validates blocking permissions.
   *
   * @param user the user being blocked
   * @param blockedBy the user performing the block
   * @throws UserDomainException if blocking is not allowed
   */
  private void validateBlockingPermissions(User user, User blockedBy) {
    // Users cannot block themselves
    if (user.getId().equals(blockedBy.getId())) {
      throw new UserDomainException("Users cannot block themselves");
    }

    // Only admins can block users
    if (!blockedBy.isAdmin()) {
      throw new UserDomainException("Only administrators can block users");
    }

    // Cannot block super admins
    if (user.getRole() == UserRole.SUPER_ADMIN) {
      throw new UserDomainException("Cannot block super administrators");
    }
  }

  /**
   * Validates email format.
   *
   * @param email email to validate
   * @return true if email is valid
   */
  private boolean isValidEmail(String email) {
    return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
  }

  /**
   * Validates user update request against business rules.
   *
   * @param existingUser the existing user to be updated
   * @param request the update request
   * @throws IllegalArgumentException if validation fails
   */
  public void validateUserUpdate(User existingUser, com.taskboard.userservice.application.dto.UpdateUserRequest request) {
    if (existingUser == null) {
      throw new IllegalArgumentException("Existing user cannot be null");
    }

    if (request == null) {
      throw new IllegalArgumentException("Update request cannot be null");
    }

    // Validate email uniqueness if email is being updated
    if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
      if (userRepository.existsByEmail(request.getEmail())) {
        throw new IllegalArgumentException("Email already exists: " + request.getEmail());
      }
    }

    // Validate role change restrictions (business rule example)
    if (request.getRole() != null && !request.getRole().equals(existingUser.getRole())) {
      // Example: Only admin users can change roles
      // This is a business rule that can be customized based on requirements
      log.info("User role change requested from {} to {} for user {}", 
          existingUser.getRole(), request.getRole(), existingUser.getId());
    }
  }
}
