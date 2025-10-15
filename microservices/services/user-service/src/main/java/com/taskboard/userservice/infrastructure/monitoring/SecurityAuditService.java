package com.taskboard.userservice.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for auditing security events and monitoring security-related activities. Records security
 * events for compliance and monitoring purposes.
 */
@Service
@Slf4j
public class SecurityAuditService {

  private final Counter loginAttemptsCounter;
  private final Counter failedLoginAttemptsCounter;
  private final Counter successfulLoginsCounter;
  private final Counter passwordChangeCounter;
  private final Counter accountLockoutCounter;
  private final Counter suspiciousActivityCounter;
  private final Map<String, Integer> failedAttemptsByIp = new ConcurrentHashMap<>();
  private final Map<String, LocalDateTime> lastAttemptByIp = new ConcurrentHashMap<>();

  /**
   * Constructs a new SecurityAuditService.
   *
   * @param meterRegistry The meter registry for recording metrics.
   */
  public SecurityAuditService(MeterRegistry meterRegistry) {

    // Initialize security counters
    this.loginAttemptsCounter =
        Counter.builder("security.login.attempts.total")
            .description("Total number of login attempts")
            .register(meterRegistry);

    this.failedLoginAttemptsCounter =
        Counter.builder("security.login.failures.total")
            .description("Total number of failed login attempts")
            .register(meterRegistry);

    this.successfulLoginsCounter =
        Counter.builder("security.login.success.total")
            .description("Total number of successful logins")
            .register(meterRegistry);

    this.passwordChangeCounter =
        Counter.builder("security.password.changes.total")
            .description("Total number of password changes")
            .register(meterRegistry);

    this.accountLockoutCounter =
        Counter.builder("security.account.lockouts.total")
            .description("Total number of account lockouts")
            .register(meterRegistry);

    this.suspiciousActivityCounter =
        Counter.builder("security.suspicious.activity.total")
            .description("Total number of suspicious activities detected")
            .register(meterRegistry);
  }

  /**
   * Records a login attempt.
   *
   * @param username The username attempting to login.
   * @param ipAddress The IP address of the login attempt.
   * @param success Whether the login was successful.
   */
  public void recordLoginAttempt(String username, String ipAddress, boolean success) {
    loginAttemptsCounter.increment();

    if (success) {
      successfulLoginsCounter.increment();
      log.info("Successful login - Username: {}, IP: {}", username, ipAddress);
    } else {
      failedLoginAttemptsCounter.increment();
      recordFailedAttempt(ipAddress);
      log.warn("Failed login attempt - Username: {}, IP: {}", username, ipAddress);
    }
  }

  /**
   * Records a password change event.
   *
   * @param username The username whose password was changed.
   * @param ipAddress The IP address of the request.
   */
  public void recordPasswordChange(String username, String ipAddress) {
    passwordChangeCounter.increment();
    log.info("Password changed - Username: {}, IP: {}", username, ipAddress);
  }

  /**
   * Records an account lockout event.
   *
   * @param username The username that was locked.
   * @param ipAddress The IP address of the request.
   * @param reason The reason for the lockout.
   */
  public void recordAccountLockout(String username, String ipAddress, String reason) {
    accountLockoutCounter.increment();
    log.warn("Account locked - Username: {}, IP: {}, Reason: {}", username, ipAddress, reason);
  }

  /**
   * Records suspicious activity.
   *
   * @param activityType The type of suspicious activity.
   * @param username The username involved (if known).
   * @param ipAddress The IP address of the activity.
   * @param details Additional details about the activity.
   */
  public void recordSuspiciousActivity(
      String activityType, String username, String ipAddress, String details) {
    suspiciousActivityCounter.increment();
    log.error(
        "Suspicious activity detected - Type: {}, Username: {}, IP: {}, Details: {}",
        activityType,
        username,
        ipAddress,
        details);
  }

  /**
   * Records a JWT token validation event.
   *
   * @param tokenValid Whether the token was valid.
   * @param ipAddress The IP address of the request.
   */
  public void recordTokenValidation(boolean tokenValid, String ipAddress) {
    if (tokenValid) {
      log.debug("Valid JWT token - IP: {}", ipAddress);
    } else {
      log.warn("Invalid JWT token - IP: {}", ipAddress);
      recordSuspiciousActivity("INVALID_TOKEN", null, ipAddress, "Invalid JWT token provided");
    }
  }

  /**
   * Records an authorization failure.
   *
   * @param username The username that was denied access.
   * @param resource The resource that was accessed.
   * @param action The action that was attempted.
   * @param ipAddress The IP address of the request.
   */
  public void recordAuthorizationFailure(
      String username, String resource, String action, String ipAddress) {
    log.warn(
        "Authorization failed - Username: {}, Resource: {}, Action: {}, IP: {}",
        username,
        resource,
        action,
        ipAddress);

    recordSuspiciousActivity(
        "AUTHORIZATION_FAILURE",
        username,
        ipAddress,
        String.format("Attempted %s on %s", action, resource));
  }

  /**
   * Records a failed attempt and tracks IP-based patterns.
   *
   * @param ipAddress The IP address of the failed attempt.
   */
  private void recordFailedAttempt(String ipAddress) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime lastAttempt = lastAttemptByIp.get(ipAddress);

    // Reset counter if more than 1 hour has passed
    if (lastAttempt == null || now.isAfter(lastAttempt.plusHours(1))) {
      failedAttemptsByIp.put(ipAddress, 1);
    } else {
      int attempts = failedAttemptsByIp.getOrDefault(ipAddress, 0) + 1;
      failedAttemptsByIp.put(ipAddress, attempts);

      // Alert if more than 5 failed attempts in 1 hour
      if (attempts > 5) {
        recordSuspiciousActivity(
            "BRUTE_FORCE_ATTEMPT",
            null,
            ipAddress,
            String.format("Multiple failed attempts: %d", attempts));
      }
    }

    lastAttemptByIp.put(ipAddress, now);
  }

  /**
   * Records a user creation event.
   *
   * @param userId The ID of the created user.
   * @param username The username of the created user.
   * @param email The email of the created user.
   * @param role The role of the created user.
   */
  public void logUserCreated(Long userId, String username, String email, com.taskboard.userservice.domain.model.UserRole role) {
    log.info("User created - ID: {}, Username: {}, Email: {}, Role: {}", userId, username, email, role);
  }

  /**
   * Records a failed user creation attempt.
   *
   * @param username The username that failed to be created.
   * @param email The email that failed to be created.
   * @param reason The reason for the failure.
   */
  public void logUserCreationFailed(String username, String email, String reason) {
    log.warn("User creation failed - Username: {}, Email: {}, Reason: {}", username, email, reason);
  }

  /**
   * Gets the number of failed attempts for an IP address.
   *
   * @param ipAddress The IP address to check.
   * @return The number of failed attempts in the last hour.
   */
  public int getFailedAttemptsForIp(String ipAddress) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime lastAttempt = lastAttemptByIp.get(ipAddress);

    if (lastAttempt == null || now.isAfter(lastAttempt.plusHours(1))) {
      return 0;
    }

    return failedAttemptsByIp.getOrDefault(ipAddress, 0);
  }

  /**
   * Logs successful user retrieval.
   *
   * @param userId the user ID
   * @param username the username
   */
  public void logUserRetrieved(Long userId, String username) {
    log.info("User retrieved - User ID: {}, Username: {}", userId, username);
  }

  /**
   * Logs failed user retrieval attempt.
   *
   * @param userId the user ID
   * @param reason the reason for failure
   */
  public void logUserRetrievalFailed(Long userId, String reason) {
    log.warn("User retrieval failed - User ID: {}, Reason: {}", userId, reason);
  }

  /**
   * Logs successful user update.
   *
   * @param userId the user ID
   * @param username the username
   */
  public void logUserUpdated(Long userId, String username) {
    log.info("User updated - User ID: {}, Username: {}", userId, username);
  }

  /**
   * Logs failed user update attempt.
   *
   * @param userId the user ID
   * @param reason the reason for failure
   */
  public void logUserUpdateFailed(Long userId, String reason) {
    log.warn("User update failed - User ID: {}, Reason: {}", userId, reason);
  }

  /**
   * Logs user deletion attempt.
   *
   * @param userId the user ID
   * @param username the username
   */
  public void logUserDeletionAttempt(Long userId, String username) {
    log.info("User deletion attempted - User ID: {}, Username: {}", userId, username);
  }

  /**
   * Logs successful user deletion.
   *
   * @param userId the user ID
   * @param username the username
   */
  public void logUserDeleted(Long userId, String username) {
    log.info("User deleted - User ID: {}, Username: {}", userId, username);
  }

  /**
   * Logs failed user deletion attempt.
   *
   * @param userId the user ID
   * @param reason the reason for failure
   */
  public void logUserDeletionFailed(Long userId, String reason) {
    log.warn("User deletion failed - User ID: {}, Reason: {}", userId, reason);
  }

  /**
   * Logs successful user authentication.
   *
   * @param userId the user ID
   * @param username the username
   */
  public void logAuthenticationSuccess(Long userId, String username) {
    log.info("Authentication successful - User ID: {}, Username: {}", userId, username);
    successfulLoginsCounter.increment();
  }

  /**
   * Logs failed user authentication attempt.
   *
   * @param username the username
   * @param reason the reason for failure
   */
  public void logAuthenticationFailed(String username, String reason) {
    log.warn("Authentication failed - Username: {}, Reason: {}", username, reason);
    failedLoginAttemptsCounter.increment();
  }
}
