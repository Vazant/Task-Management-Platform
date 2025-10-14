package com.taskboard.userservice.application.exception;

/**
 * Base exception for all application service errors in the User Service.
 *
 * <p>This exception serves as the root of the application exception hierarchy and should be used for all
 * application service-related errors. It provides a common base for all application exceptions and ensures
 * consistent error handling across the application layer.
 *
 * <p>Application exceptions should be thrown when:
 *
 * <ul>
 *   <li>Application service operations fail</li>
 *   <li>Use case execution fails</li>
 *   <li>Application-level validation fails</li>
 *   <li>External service integration fails</li>
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserServiceException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new UserServiceException with the specified detail message.
   *
   * @param message the detail message
   */
  public UserServiceException(String message) {
    super(message);
  }

  /**
   * Constructs a new UserServiceException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public UserServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new UserServiceException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public UserServiceException(Throwable cause) {
    super(cause);
  }
}

