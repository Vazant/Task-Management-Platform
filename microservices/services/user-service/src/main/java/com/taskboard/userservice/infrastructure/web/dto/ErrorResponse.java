package com.taskboard.userservice.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Error information in API responses.
 * Contains detailed error information for API clients.
 * 
 * <p>This class provides comprehensive error details including:
 * <ul>
 *   <li>Error code for programmatic handling</li>
 *   <li>Human-readable error message</li>
 *   <li>Additional error details</li>
 *   <li>Field validation errors (when applicable)</li>
 *   <li>Additional context information</li>
 * </ul>
 * 
 * <p>Usage examples:
 * <pre>{@code
 * // Simple error
 * ErrorResponse error = ErrorResponse.builder()
 *     .code("USER_NOT_FOUND")
 *     .message("User with ID 123 not found")
 *     .build();
 * 
 * // Validation error with field details
 * ErrorResponse validationError = ErrorResponse.builder()
 *     .code("VALIDATION_ERROR")
 *     .message("Validation failed")
 *     .validationErrors(List.of(
 *         ValidationError.builder()
 *             .field("email")
 *             .message("Invalid email format")
 *             .rejectedValue("invalid-email")
 *             .build()
 *     ))
 *     .build();
 * }</pre>
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see ValidationError
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * Error code for programmatic error handling.
     */
    private String code;
    
    /**
     * Human-readable error message.
     */
    private String message;
    
    /**
     * Additional error details and context.
     */
    private String details;
    
    /**
     * List of field validation errors (when applicable).
     */
    private List<ValidationError> validationErrors;
    
    /**
     * Additional error context information.
     */
    private Map<String, Object> additionalInfo;
    
    /**
     * Field validation error details.
     * Contains information about validation failures for specific fields.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        
        /**
         * The name of the field that failed validation.
         */
        private String field;
        
        /**
         * The validation error message.
         */
        private String message;
        
        /**
         * The rejected value that caused the validation error.
         */
        private Object rejectedValue;
    }
    
    /**
     * Creates a validation error for a specific field.
     *
     * @param field the field name that failed validation
     * @param message the validation error message
     * @param rejectedValue the value that was rejected
     * @return validation error instance
     */
    public static ValidationError validationError(String field, String message, Object rejectedValue) {
        return ValidationError.builder()
                .field(field)
                .message(message)
                .rejectedValue(rejectedValue)
                .build();
    }
}
