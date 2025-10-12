package com.taskboard.userservice.infrastructure.web.exception;

import com.taskboard.userservice.infrastructure.web.dto.ApiResponse;
import com.taskboard.userservice.infrastructure.web.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for REST API endpoints.
 * Provides consistent error handling and response formatting across the entire application.
 * 
 * <p>This handler catches and processes various types of exceptions including:
 * <ul>
 *   <li>Validation errors (Bean Validation, Constraint Violations)</li>
 *   <li>Authentication and authorization errors</li>
 *   <li>Resource not found errors</li>
 *   <li>HTTP method and parameter errors</li>
 *   <li>Generic application errors</li>
 * </ul>
 * 
 * <p>All exceptions are converted to standardized {@link ApiResponse} objects
 * with appropriate HTTP status codes and error details.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see ApiResponse
 * @see ErrorResponse
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles field validation errors from Bean Validation.
     * 
     * @param ex the validation exception containing field errors
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            ErrorResponse.ValidationError validationError = ErrorResponse.ValidationError.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .rejectedValue(error.getRejectedValue())
                    .build();
            validationErrors.add(validationError);
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .validationErrors(validationErrors)
                .build();
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка ошибок валидации ограничений.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            ErrorResponse.ValidationError validationError = ErrorResponse.ValidationError.builder()
                    .field(fieldName)
                    .message(violation.getMessage())
                    .rejectedValue(violation.getInvalidValue())
                    .build();
            validationErrors.add(validationError);
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("CONSTRAINT_VIOLATION")
                .message("Constraint validation failed")
                .validationErrors(validationErrors)
                .build();
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка ошибок аутентификации.
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(Exception ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("AUTHENTICATION_ERROR")
                .message("Authentication failed")
                .details(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка ошибок авторизации.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ACCESS_DENIED")
                .message("Access denied")
                .details(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка ошибок "не найдено".
     */
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(IllegalArgumentException ex) {
        log.warn("Not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка ошибок неверного HTTP метода.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not supported: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("METHOD_NOT_SUPPORTED")
                .message("HTTP method not supported")
                .details(String.format("Method '%s' is not supported for this endpoint", ex.getMethod()))
                .build();
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка ошибок неверного типа параметра.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch: {}", ex.getMessage());
        
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("TYPE_MISMATCH")
                .message("Invalid parameter type")
                .details(message)
                .build();
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка ошибок отсутствующих параметров запроса.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameterException(MissingServletRequestParameterException ex) {
        log.warn("Missing parameter: {}", ex.getMessage());
        
        String message = String.format("Required parameter '%s' of type '%s' is missing",
                ex.getParameterName(), ex.getParameterType());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("MISSING_PARAMETER")
                .message("Required parameter missing")
                .details(message)
                .build();
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка ошибок нечитаемого JSON.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Message not readable: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("MALFORMED_JSON")
                .message("Invalid JSON format")
                .details("Request body contains invalid JSON")
                .build();
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка ошибок "endpoint не найден".
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("No handler found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ENDPOINT_NOT_FOUND")
                .message("Endpoint not found")
                .details(String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()))
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }

    /**
     * Обработка всех остальных исключений.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .details("Please contact support if the problem persists")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .error(errorResponse)
                        .build());
    }
}
