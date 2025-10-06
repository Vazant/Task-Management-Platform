package com.taskboard.api.constants;

/** Константы для сообщений об ошибках. Используются для логирования и отображения пользователю. */
public final class ErrorMessages {

  private ErrorMessages() {
    throw new UnsupportedOperationException("Utility class");
  }

  // Общие ошибки
  public static final String USER_NOT_FOUND = "User not found: ";
  public static final String USER_ALREADY_EXISTS = "User already exists: ";
  public static final String INVALID_CREDENTIALS = "Invalid credentials for user: ";
  public static final String ACCESS_DENIED = "Access denied for user: ";
  public static final String VALIDATION_ERROR = "Validation error: ";
  public static final String INTERNAL_ERROR = "Internal server error: ";

  // Файловые операции
  public static final String FILE_UPLOAD_FAILED = "File upload failed: ";
  public static final String FILE_DELETE_FAILED = "File delete failed: ";
  public static final String FILE_SIZE_EXCEEDED = "File size exceeded: ";
  public static final String FILE_TYPE_NOT_ALLOWED = "File type not allowed: ";

  // База данных
  public static final String DATABASE_ERROR = "Database error: ";
  public static final String ENTITY_NOT_FOUND = "Entity not found: ";
  public static final String CONSTRAINT_VIOLATION = "Constraint violation: ";

  // Сеть и внешние сервисы
  public static final String NETWORK_ERROR = "Network error: ";
  public static final String EXTERNAL_SERVICE_ERROR = "External service error: ";
  public static final String CONNECTION_TIMEOUT = "Connection timeout: ";

  // Безопасность
  public static final String SECURITY_VIOLATION = "Security violation: ";
  public static final String TOKEN_EXPIRED = "Token expired: ";
  public static final String TOKEN_INVALID = "Token invalid: ";
  public static final String PERMISSION_DENIED = "Permission denied: ";
}
