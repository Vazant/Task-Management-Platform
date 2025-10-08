package com.taskboard.api.config;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.exception.EmailAlreadyExistsException;
import com.taskboard.api.exception.RegistrationException;
import com.taskboard.api.exception.UsernameAlreadyExistsException;
import com.taskboard.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling and consistent error responses.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  private final MessageService messageService;

  /**
   * Handle validation exceptions.
   * 
   * @param ex validation exception
   * @return validation error response
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.warn("Validation error: {}", ex.getMessage());
    
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    String firstError = errors.values().iterator().next();
    return ResponseEntity.badRequest()
        .body(ApiResponse.error("Validation failed", errors.values().toArray(new String[0])));
  }

  /**
   * Handle authentication exceptions.
   * 
   * @param ex authentication exception
   * @return unauthorized response
   */
  @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
  public ResponseEntity<ApiResponse<String>> handleAuthenticationException(Exception ex) {
    log.warn("Authentication error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("Authentication failed"));
  }

  /**
   * Handle access denied exceptions.
   * 
   * @param ex access denied exception
   * @return forbidden response
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error("Access denied"));
  }

  /**
   * Handle email already exists exceptions.
   * 
   * @param ex email already exists exception
   * @return conflict response
   */
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<String>> handleEmailAlreadyExists(
      EmailAlreadyExistsException ex) {
    log.warn("Email already exists: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(ex.getMessage()));
  }

  /**
   * Handle username already exists exceptions.
   * 
   * @param ex username already exists exception
   * @return conflict response
   */
  @ExceptionHandler(UsernameAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<String>> handleUsernameAlreadyExists(
      UsernameAlreadyExistsException ex) {
    log.warn("Username already exists: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(ex.getMessage()));
  }

  /**
   * Handle registration exceptions.
   * 
   * @param ex registration exception
   * @return bad request response
   */
  @ExceptionHandler(RegistrationException.class)
  public ResponseEntity<ApiResponse<String>> handleRegistrationException(RegistrationException ex) {
    log.warn("Registration error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
  }

  /**
   * Handle data integrity violation exceptions.
   * 
   * @param ex data integrity violation exception
   * @return conflict response
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    log.warn("Data integrity violation: {}", ex.getMessage());
    
    String message = "Data integrity violation";
    if (ex.getMessage() != null) {
      if (ex.getMessage().contains("email") || ex.getMessage().contains("EMAIL")) {
        message = "Email already exists";
      } else if (ex.getMessage().contains("username") || ex.getMessage().contains("USERNAME")) {
        message = "Username already exists";
      }
    }

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(message));
  }

  /**
   * Handle generic exceptions.
   * 
   * @param ex generic exception
   * @param request web request
   * @return internal server error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex, WebRequest request) {
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("An unexpected error occurred"));
  }
}
