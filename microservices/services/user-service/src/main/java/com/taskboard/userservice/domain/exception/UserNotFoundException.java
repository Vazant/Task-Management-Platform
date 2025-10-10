package com.taskboard.userservice.domain.exception;

/**
 * Exception thrown when a requested user cannot be found.
 *
 * <p>This exception is thrown when operations are attempted on a user that does not exist in the
 * system. It provides clear indication that the requested user entity was not found and helps
 * distinguish between different types of user-related errors.
 *
 * <p>This exception should be thrown when:
 *
 * <ul>
 *   <li>Attempting to retrieve a user by ID that doesn't exist
 *   <li>Attempting to update a non-existent user
 *   <li>Attempting to delete a non-existent user
 *   <li>Any operation that requires an existing user
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserNotFoundException extends UserDomainException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new UserNotFoundException with the specified user ID.
   *
   * @param userId the ID of the user that was not found
   */
  public UserNotFoundException(Long userId) {
    super("User not found with ID: " + userId);
  }

  /**
   * Constructs a new UserNotFoundException with the specified username.
   *
   * @param username the username that was not found
   */
  public UserNotFoundException(String username) {
    super("User not found with username: " + username);
  }

  /**
   * Constructs a new UserNotFoundException with the specified detail message.
   *
   * @param message the detail message
   */
  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
