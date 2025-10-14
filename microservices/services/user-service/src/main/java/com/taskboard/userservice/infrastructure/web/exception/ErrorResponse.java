package com.taskboard.userservice.infrastructure.web.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response format for the User Service.
 * 
 * <p>This class provides a consistent error response format across all endpoints.
 * It follows REST API best practices for error handling and provides detailed
 * information for debugging while maintaining security.
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
public class ErrorResponse {

  /**
   * Timestamp when the error occurred.
   */
  private LocalDateTime timestamp;

  /**
   * HTTP status code.
   */
  private int status;

  /**
   * Error type or category.
   */
  private String error;

  /**
   * Human-readable error message.
   */
  private String message;

  /**
   * Request path where the error occurred.
   */
  private String path;

  /**
   * Additional validation errors (for validation failures).
   */
  private Map<String, String> validationErrors;
}

