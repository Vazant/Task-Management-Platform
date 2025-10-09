package com.taskboard.userservice.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for auditing security events and monitoring security-related activities.
 * Records security events for compliance and monitoring purposes.
 */
@Service
public class SecurityAuditService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditService.class);

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
        this.loginAttemptsCounter = Counter.builder("security.login.attempts.total")
                .description("Total number of login attempts")
                .register(meterRegistry);
        
        this.failedLoginAttemptsCounter = Counter.builder("security.login.failures.total")
                .description("Total number of failed login attempts")
                .register(meterRegistry);
        
        this.successfulLoginsCounter = Counter.builder("security.login.success.total")
                .description("Total number of successful logins")
                .register(meterRegistry);
        
        this.passwordChangeCounter = Counter.builder("security.password.changes.total")
                .description("Total number of password changes")
                .register(meterRegistry);
        
        this.accountLockoutCounter = Counter.builder("security.account.lockouts.total")
                .description("Total number of account lockouts")
                .register(meterRegistry);
        
        this.suspiciousActivityCounter = Counter.builder("security.suspicious.activity.total")
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
            logger.info("Successful login - Username: {}, IP: {}", username, ipAddress);
        } else {
            failedLoginAttemptsCounter.increment();
            recordFailedAttempt(ipAddress);
            logger.warn("Failed login attempt - Username: {}, IP: {}", username, ipAddress);
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
        logger.info("Password changed - Username: {}, IP: {}", username, ipAddress);
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
        logger.warn("Account locked - Username: {}, IP: {}, Reason: {}", username, ipAddress, reason);
    }

    /**
     * Records suspicious activity.
     *
     * @param activityType The type of suspicious activity.
     * @param username The username involved (if known).
     * @param ipAddress The IP address of the activity.
     * @param details Additional details about the activity.
     */
    public void recordSuspiciousActivity(String activityType, String username, String ipAddress, String details) {
        suspiciousActivityCounter.increment();
        logger.error("Suspicious activity detected - Type: {}, Username: {}, IP: {}, Details: {}", 
                activityType, username, ipAddress, details);
    }

    /**
     * Records a JWT token validation event.
     *
     * @param tokenValid Whether the token was valid.
     * @param ipAddress The IP address of the request.
     */
    public void recordTokenValidation(boolean tokenValid, String ipAddress) {
        if (tokenValid) {
            logger.debug("Valid JWT token - IP: {}", ipAddress);
        } else {
            logger.warn("Invalid JWT token - IP: {}", ipAddress);
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
    public void recordAuthorizationFailure(String username, String resource, String action, String ipAddress) {
        logger.warn("Authorization failed - Username: {}, Resource: {}, Action: {}, IP: {}", 
                username, resource, action, ipAddress);
        
        recordSuspiciousActivity("AUTHORIZATION_FAILURE", username, ipAddress, 
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
                recordSuspiciousActivity("BRUTE_FORCE_ATTEMPT", null, ipAddress, 
                        String.format("Multiple failed attempts: %d", attempts));
            }
        }
        
        lastAttemptByIp.put(ipAddress, now);
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
}
