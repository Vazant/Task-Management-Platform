package com.taskboard.userservice.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service for monitoring application performance and business metrics.
 * Provides methods to record various metrics for monitoring and alerting.
 */
@Service
public class PerformanceMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringService.class);

    private final MeterRegistry meterRegistry;
    private final Counter userCreationCounter;
    private final Counter userAuthenticationCounter;
    private final Counter userAuthenticationFailureCounter;
    private final Counter userUpdateCounter;
    private final Counter userDeletionCounter;
    private final Timer userCreationTimer;
    private final Timer userAuthenticationTimer;
    private final Timer userUpdateTimer;
    private final Timer userDeletionTimer;

    /**
     * Constructs a new PerformanceMonitoringService.
     *
     * @param meterRegistry The meter registry for recording metrics.
     */
    public PerformanceMonitoringService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.userCreationCounter = Counter.builder("user.creation.total")
                .description("Total number of users created")
                .register(meterRegistry);
        
        this.userAuthenticationCounter = Counter.builder("user.authentication.success.total")
                .description("Total number of successful user authentications")
                .register(meterRegistry);
        
        this.userAuthenticationFailureCounter = Counter.builder("user.authentication.failure.total")
                .description("Total number of failed user authentications")
                .register(meterRegistry);
        
        this.userUpdateCounter = Counter.builder("user.update.total")
                .description("Total number of user updates")
                .register(meterRegistry);
        
        this.userDeletionCounter = Counter.builder("user.deletion.total")
                .description("Total number of user deletions")
                .register(meterRegistry);
        
        // Initialize timers
        this.userCreationTimer = Timer.builder("user.creation.duration")
                .description("Time taken to create a user")
                .register(meterRegistry);
        
        this.userAuthenticationTimer = Timer.builder("user.authentication.duration")
                .description("Time taken to authenticate a user")
                .register(meterRegistry);
        
        this.userUpdateTimer = Timer.builder("user.update.duration")
                .description("Time taken to update a user")
                .register(meterRegistry);
        
        this.userDeletionTimer = Timer.builder("user.deletion.duration")
                .description("Time taken to delete a user")
                .register(meterRegistry);
    }

    /**
     * Records a user creation event.
     *
     * @param duration The time taken to create the user.
     * @param success Whether the creation was successful.
     */
    public void recordUserCreation(Duration duration, boolean success) {
        userCreationCounter.increment();
        userCreationTimer.record(duration);
        
        logger.info("User creation recorded - Duration: {}ms, Success: {}", 
                duration.toMillis(), success);
    }

    /**
     * Records a user authentication event.
     *
     * @param duration The time taken to authenticate the user.
     * @param success Whether the authentication was successful.
     */
    public void recordUserAuthentication(Duration duration, boolean success) {
        if (success) {
            userAuthenticationCounter.increment();
        } else {
            userAuthenticationFailureCounter.increment();
        }
        
        userAuthenticationTimer.record(duration);
        
        logger.info("User authentication recorded - Duration: {}ms, Success: {}", 
                duration.toMillis(), success);
    }

    /**
     * Records a user update event.
     *
     * @param duration The time taken to update the user.
     * @param success Whether the update was successful.
     */
    public void recordUserUpdate(Duration duration, boolean success) {
        userUpdateCounter.increment();
        userUpdateTimer.record(duration);
        
        logger.info("User update recorded - Duration: {}ms, Success: {}", 
                duration.toMillis(), success);
    }

    /**
     * Records a user deletion event.
     *
     * @param duration The time taken to delete the user.
     * @param success Whether the deletion was successful.
     */
    public void recordUserDeletion(Duration duration, boolean success) {
        userDeletionCounter.increment();
        userDeletionTimer.record(duration);
        
        logger.info("User deletion recorded - Duration: {}ms, Success: {}", 
                duration.toMillis(), success);
    }

    /**
     * Records a custom business metric.
     *
     * @param metricName The name of the metric.
     * @param value The value to record.
     * @param tags Additional tags for the metric.
     */
    public void recordCustomMetric(String metricName, double value, String... tags) {
        meterRegistry.gauge(metricName, value);
        if (logger.isDebugEnabled()) {
            logger.debug("Custom metric recorded - Name: {}, Value: {}, Tags: {}", 
                    metricName, value, String.join(",", tags));
        }
    }

    /**
     * Records an error event.
     *
     * @param errorType The type of error.
     * @param errorMessage The error message.
     */
    public void recordError(String errorType, String errorMessage) {
        Counter.builder("user.service.errors.total")
                .tag("error_type", errorType)
                .description("Total number of errors by type")
                .register(meterRegistry)
                .increment();
        
        logger.error("Error recorded - Type: {}, Message: {}", errorType, errorMessage);
    }
}
