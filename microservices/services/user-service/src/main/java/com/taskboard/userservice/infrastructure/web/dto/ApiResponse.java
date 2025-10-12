package com.taskboard.userservice.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for all endpoints.
 * Provides a consistent response format across the entire API.
 * 
 * <p>This class ensures that all API responses follow the same structure:
 * <ul>
 *   <li>Success flag indicating operation result</li>
 *   <li>Data payload (when successful)</li>
 *   <li>Human-readable message</li>
 *   <li>Error details (when failed)</li>
 *   <li>Timestamp for request tracking</li>
 * </ul>
 * 
 * <p>Usage examples:
 * <pre>{@code
 * // Success response
 * ApiResponse<UserDto> response = ApiResponse.success(user, "User created successfully");
 * 
 * // Error response
 * ApiResponse<Void> error = ApiResponse.error("VALIDATION_ERROR", "Invalid input data");
 * }</pre>
 *
 * @param <T> the type of data contained in the response
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see ErrorResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * Flag indicating whether the operation was successful.
     */
    private boolean success;
    
    /**
     * The response data payload.
     */
    private T data;
    
    /**
     * Human-readable message describing the operation result.
     */
    private String message;
    
    /**
     * Error information (present only when operation fails).
     */
    private ErrorResponse error;
    
    /**
     * Timestamp when the response was generated.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Creates a successful response with data.
     *
     * @param data the response data
     * @param <T> the data type
     * @return successful response
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates a successful response with data and message.
     *
     * @param data the response data
     * @param message the success message
     * @param <T> the data type
     * @return successful response
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates an error response.
     *
     * @param error the error information
     * @param <T> the data type
     * @return error response
     */
    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates an error response with error code and message.
     *
     * @param code the error code
     * @param message the error message
     * @param <T> the data type
     * @return error response
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        ErrorResponse error = ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
        
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
