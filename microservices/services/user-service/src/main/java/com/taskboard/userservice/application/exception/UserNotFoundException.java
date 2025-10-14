package com.taskboard.userservice.application.exception;

/**
 * Exception thrown when a user is not found.
 *
 * <p>This exception is thrown when an operation is attempted on a user that does not exist
 * in the system. It provides a clear indication that the requested user resource is not available.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserNotFoundException extends UserServiceException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new UserNotFoundException with the specified user ID.
   *
   * @param userId the user ID that was not found
   */
  public UserNotFoundException(Long userId) {
    super(String.format("User not found with ID: %d", userId));
  }

  /**
   * Constructs a new UserNotFoundException with the specified username.
   *
   * @param username the username that was not found
   */
  public UserNotFoundException(String username) {
    super(String.format("User not found with username: %s", username));
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

