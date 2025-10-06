package com.taskboard.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> {
  private T data;
  private String message;
  private boolean success;
  private String[] errors;

  public ApiResponse() {}

  public ApiResponse(final T data, final String message, final boolean success) {
    this.data = data;
    this.message = message;
    this.success = success;
  }

  public ApiResponse(final T data, final boolean success) {
    this.data = data;
    this.success = success;
  }

  public ApiResponse(final String message, final boolean success) {
    this.message = message;
    this.success = success;
  }

  public ApiResponse(final String message, final boolean success, final String[] errors) {
    this.message = message;
    this.success = success;
    this.errors = errors;
  }

  // Static factory methods
  public static <T> ApiResponse<T> success(final T data) {
    return new ApiResponse<>(data, "Успешно", true);
  }

  public static <T> ApiResponse<T> success(final T data, final String message) {
    return new ApiResponse<>(data, message, true);
  }

  public static <T> ApiResponse<T> error(final String message) {
    return new ApiResponse<>(message, false);
  }

  public static <T> ApiResponse<T> error(final String message, final String[] errors) {
    return new ApiResponse<>(message, false, errors);
  }

  // Getters and Setters
  public T getData() {
    return data;
  }

  public void setData(final T data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(final boolean success) {
    this.success = success;
  }

  public String[] getErrors() {
    return errors;
  }

  public void setErrors(final String[] errors) {
    this.errors = errors;
  }
}
