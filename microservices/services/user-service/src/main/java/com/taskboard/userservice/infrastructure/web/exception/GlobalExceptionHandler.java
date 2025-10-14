package com.taskboard.userservice.infrastructure.web.exception;

import com.taskboard.userservice.application.exception.UserNotFoundException;
import com.taskboard.userservice.application.exception.UserServiceException;
import com.taskboard.userservice.domain.exception.UserDomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the User Service.
 * 
 * <p>This class provides centralized exception handling for all controllers in the application.
 * It follows the best practice of having a single point of exception handling and provides
 * consistent error responses across the entire application.
 * 
 * <p>Features:
 * <ul>
 *   <li>Centralized exception handling</li>
 *   <li>Consistent error response format</li>
 *   <li>Proper HTTP status codes</li>
 *   <li>Detailed logging for debugging</li>
 *   <li>Security-conscious error messages</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
   * Handles user not found exceptions.
   * 
   * @param ex the exception
   * @param request the web request
   * @return error response with 404 status
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(
      UserNotFoundException ex, 
      WebRequest request) {
    
    log.warn("User not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("User Not Found")
        .message(ex.getMessage())
        .path(getPath(request))
                .build();
        
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /**
   * Handles user service exceptions.
   * 
   * @param ex the exception
   * @param request the web request
   * @return error response with 400 status
   */
  @ExceptionHandler(UserServiceException.class)
  public ResponseEntity<ErrorResponse> handleUserServiceException(
      UserServiceException ex, 
      WebRequest request) {
    
    log.error("User service error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("User Service Error")
        .message(ex.getMessage())
        .path(getPath(request))
                .build();
        
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handles domain exceptions.
   * 
   * @param ex the exception
   * @param request the web request
   * @return error response with 422 status
   */
  @ExceptionHandler(UserDomainException.class)
  public ResponseEntity<ErrorResponse> handleUserDomainException(
      UserDomainException ex, 
      WebRequest request) {
    
    log.warn("Domain validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
        .error("Domain Validation Error")
                .message(ex.getMessage())
        .path(getPath(request))
                .build();
        
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
  }

  /**
   * Handles validation exceptions.
   * 
   * @param ex the exception
   * @param request the web request
   * @return error response with 400 status and validation details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, 
      WebRequest request) {
    
    log.warn("Validation error: {}", ex.getMessage());
    
    Map<String, String> validationErrors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      validationErrors.put(fieldName, errorMessage);
    });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Error")
        .message("Request validation failed")
        .path(getPath(request))
        .validationErrors(validationErrors)
                .build();
        
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handles illegal argument exceptions.
   * 
   * @param ex the exception
   * @param request the web request
   * @return error response with 400 status
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, 
      WebRequest request) {
    
    log.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Invalid Argument")
        .message(ex.getMessage())
        .path(getPath(request))
                .build();
        
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handles all other exceptions.
   * 
   * @param ex the exception
   * @param request the web request
   * @return error response with 500 status
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, 
      WebRequest request) {
    
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("An unexpected error occurred")
        .path(getPath(request))
                .build();
        
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  /**
   * Extracts the request path from the web request.
   * 
   * @param request the web request
   * @return the request path
   */
  private String getPath(WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}