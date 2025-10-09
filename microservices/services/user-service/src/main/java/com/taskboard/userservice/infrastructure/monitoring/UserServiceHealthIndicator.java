package com.taskboard.userservice.infrastructure.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom health indicator for the User Service.
 * Provides detailed health information about the service and its dependencies.
 */
@Component
public class UserServiceHealthIndicator implements HealthIndicator {

    private static final String STATUS_KEY = "status";
    private static final String LAST_CHECK_KEY = "lastCheck";
    private static final String HEALTHY_STATUS = "healthy";

    private final SecurityAuditService securityAuditService;

    /**
     * Constructs a new UserServiceHealthIndicator.
     *
     * @param securityAuditService The security audit service.
     */
    public UserServiceHealthIndicator(SecurityAuditService securityAuditService) {
        this.securityAuditService = securityAuditService;
    }

    /**
     * Performs a health check and returns the health status.
     *
     * @return The health status of the service.
     */
    @Override
    public Health health() {
        try {
            Map<String, Object> details = new HashMap<>();
            
            // Service information
            details.put("service", "user-service");
            details.put("version", "1.0.0");
            details.put("timestamp", LocalDateTime.now());
            
            // Security status
            details.put("security", getSecurityStatus());
            
            // Performance status
            details.put("performance", getPerformanceStatus());
            
            // System status
            details.put("system", getSystemStatus());
            
            return Health.up()
                    .withDetails(details)
                    .build();
                    
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        }
    }

    /**
     * Gets the security status information.
     *
     * @return A map containing security status details.
     */
    private Map<String, Object> getSecurityStatus() {
        Map<String, Object> securityStatus = new HashMap<>();
        securityStatus.put(STATUS_KEY, HEALTHY_STATUS);
        securityStatus.put("failedAttempts", securityAuditService.getFailedAttemptsForIp("127.0.0.1"));
        securityStatus.put(LAST_CHECK_KEY, LocalDateTime.now());
        return securityStatus;
    }

    /**
     * Gets the performance status information.
     *
     * @return A map containing performance status details.
     */
    private Map<String, Object> getPerformanceStatus() {
        Map<String, Object> performanceStatus = new HashMap<>();
        performanceStatus.put(STATUS_KEY, HEALTHY_STATUS);
        performanceStatus.put("memoryUsage", getMemoryUsage());
        performanceStatus.put(LAST_CHECK_KEY, LocalDateTime.now());
        return performanceStatus;
    }

    /**
     * Gets the system status information.
     *
     * @return A map containing system status details.
     */
    private Map<String, Object> getSystemStatus() {
        Map<String, Object> systemStatus = new HashMap<>();
        systemStatus.put(STATUS_KEY, HEALTHY_STATUS);
        systemStatus.put("uptime", getUptime());
        systemStatus.put(LAST_CHECK_KEY, LocalDateTime.now());
        return systemStatus;
    }

    /**
     * Gets the current memory usage percentage.
     *
     * @return The memory usage percentage.
     */
    private double getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return (double) usedMemory / totalMemory * 100;
    }

    /**
     * Gets the service uptime in milliseconds.
     *
     * @return The uptime in milliseconds.
     */
    private long getUptime() {
        return System.currentTimeMillis() - getStartTime();
    }

    /**
     * Gets the service start time.
     * This is a simplified implementation - in a real scenario,
     * you might want to track this more accurately.
     *
     * @return The start time in milliseconds.
     */
    private long getStartTime() {
        // This is a simplified implementation
        // In a real scenario, you would track the actual start time
        return System.currentTimeMillis() - 60000; // Assume 1 minute uptime for demo
    }
}
