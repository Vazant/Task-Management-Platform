package com.taskboard.api.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Утилитарный класс для централизованного логирования. Обеспечивает единообразное логирование по
 * всему приложению.
 */
@Slf4j
public final class LoggingUtils {

  private LoggingUtils() {
    throw new UnsupportedOperationException("Utility class");
  }

  /** Логирует успешную аутентификацию пользователя. */
  public static void logUserLogin(final String username, final String ipAddress) {
    log.info("User login successful: username={}, ip={}", username, ipAddress);
  }

  /** Логирует неудачную попытку входа. */
  public static void logUserLoginFailed(final String username, final String ipAddress, final String reason) {
    log.warn("User login failed: username={}, ip={}, reason={}", username, ipAddress, reason);
  }

  /** Логирует регистрацию нового пользователя. */
  public static void logUserRegistration(final String email, final String username) {
    log.info("New user registered: email={}, username={}", email, username);
  }

  /** Логирует ошибки валидации. */
  public static void logValidationError(final String field, final String value, final String error) {
    log.warn("Validation error: field={}, value={}, error={}", field, value, error);
  }

  /** Логирует ошибки безопасности. */
  public static void logSecurityViolation(final String action, final String user, final String details) {
    log.error("Security violation: action={}, user={}, details={}", action, user, details);
  }

  /** Логирует ошибки файловых операций. */
  public static void logFileOperationError(final String operation, final String filename, final String error) {
    log.error(
        "File operation failed: operation={}, filename={}, error={}", operation, filename, error);
  }

  /** Логирует ошибки базы данных. */
  public static void logDatabaseError(final String operation, final String entity, final String error) {
    log.error("Database error: operation={}, entity={}, error={}", operation, entity, error);
  }

  /** Логирует критические ошибки системы. */
  public static void logCriticalError(final String component, final String operation, final String error) {
    log.error(
        "Critical system error: component={}, operation={}, error={}", component, operation, error);
  }

  /** Логирует производительность операций. */
  public static void logPerformance(final String operation, final long durationMs) {
    if (durationMs > 1000) {
      log.warn("Slow operation: operation={}, duration={}ms", operation, durationMs);
    } else {
      log.debug("Operation completed: operation={}, duration={}ms", operation, durationMs);
    }
  }
}
