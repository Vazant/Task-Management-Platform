package com.taskboard.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for consistent API responses.
 * Contains data, message, success status, and optional errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  
  private T data;
  private String message;
  private boolean success;
  private String[] errors;

  // Static factory methods
  public static <T> ApiResponse<T> success(final T data) {
    return ApiResponse.<T>builder()
            .data(data)
            .message("Успешно")
            .success(true)
            .build();
  }

  public static <T> ApiResponse<T> success(final T data, final String message) {
    return ApiResponse.<T>builder()
            .data(data)
            .message(message)
            .success(true)
            .build();
  }

  public static <T> ApiResponse<T> error(final String message) {
    return ApiResponse.<T>builder()
            .message(message)
            .success(false)
            .build();
  }

  public static <T> ApiResponse<T> error(final String message, final String[] errors) {
    return ApiResponse.<T>builder()
            .message(message)
            .success(false)
            .errors(errors)
            .build();
  }
}
