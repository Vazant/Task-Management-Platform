package com.taskboard.userservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for validating incoming events and handling schema evolution.
 * 
 * <p>This service provides comprehensive validation for incoming events,
 * including structure validation, data validation, and schema version compatibility.</p>
 * 
 * <p>Features:
 * <ul>
 *   <li>Event structure validation</li>
 *   <li>Data type and format validation</li>
 *   <li>Schema version compatibility checking</li>
 *   <li>Custom validation rules for different event types</li>
 *   <li>Validation error reporting with detailed messages</li>
 * </ul>
 * </p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@Service
public class EventValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventValidationService.class);
    
    /**
     * Supported schema versions for events.
     */
    private static final String[] SUPPORTED_VERSIONS = {"1.0", "1.1", "2.0"};
    
    /**
     * Pattern for validating event types.
     */
    private static final Pattern EVENT_TYPE_PATTERN = Pattern.compile("^[a-z]+\\.[a-z]+$");
    
    /**
     * Pattern for validating service names.
     */
    private static final Pattern SERVICE_NAME_PATTERN = Pattern.compile("^[a-z-]+-service$");
    
    /**
     * Pattern for validating UUIDs.
     */
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Validates an incoming event.
     * 
     * @param event the event to validate
     * @param <T> the type of event data
     * @throws EventValidationException if validation fails
     */
    public <T> void validateEvent(IncomingEvent<T> event) {
        logger.debug("Validating event: {} from service: {}", 
            event.getEventType(), event.getSourceService());
        
        ValidationResult result = new ValidationResult();
        
        // Validate basic event structure
        validateEventId(event.getEventId(), result);
        validateEventType(event.getEventType(), result);
        validateSourceService(event.getSourceService(), result);
        validateTimestamp(event.getTimestamp(), result);
        validateVersion(event.getVersion(), result);
        validateEventData(event.getData(), result);
        
        if (!result.isValid()) {
            logger.warn("Event validation failed for event {}: {}", 
                event.getEventId(), result.getErrorMessage());
            throw new EventValidationException("Event validation failed: " + result.getErrorMessage());
        }
        
        logger.debug("Event validation successful for event: {}", event.getEventId());
    }
    
    /**
     * Validates event ID.
     * 
     * @param eventId the event ID to validate
     * @param result the validation result
     */
    private void validateEventId(UUID eventId, ValidationResult result) {
        if (eventId == null) {
            result.addError("Event ID cannot be null");
            return;
        }
        
        if (!UUID_PATTERN.matcher(eventId.toString()).matches()) {
            result.addError("Event ID must be a valid UUID");
        }
    }
    
    /**
     * Validates event type.
     * 
     * @param eventType the event type to validate
     * @param result the validation result
     */
    private void validateEventType(String eventType, ValidationResult result) {
        if (eventType == null || eventType.trim().isEmpty()) {
            result.addError("Event type cannot be null or empty");
            return;
        }
        
        if (!EVENT_TYPE_PATTERN.matcher(eventType).matches()) {
            result.addError("Event type must follow pattern 'service.action' (e.g., 'task.created')");
        }
    }
    
    /**
     * Validates source service name.
     * 
     * @param sourceService the source service name to validate
     * @param result the validation result
     */
    private void validateSourceService(String sourceService, ValidationResult result) {
        if (sourceService == null || sourceService.trim().isEmpty()) {
            result.addError("Source service cannot be null or empty");
            return;
        }
        
        if (!SERVICE_NAME_PATTERN.matcher(sourceService).matches()) {
            result.addError("Source service must follow pattern 'service-name-service'");
        }
    }
    
    /**
     * Validates event timestamp.
     * 
     * @param timestamp the timestamp to validate
     * @param result the validation result
     */
    private void validateTimestamp(LocalDateTime timestamp, ValidationResult result) {
        if (timestamp == null) {
            result.addError("Event timestamp cannot be null");
            return;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxPastTime = now.minusHours(24);
        LocalDateTime maxFutureTime = now.plusMinutes(5);
        
        if (timestamp.isBefore(maxPastTime)) {
            result.addError("Event timestamp cannot be more than 24 hours in the past");
        }
        
        if (timestamp.isAfter(maxFutureTime)) {
            result.addError("Event timestamp cannot be more than 5 minutes in the future");
        }
    }
    
    /**
     * Validates event version.
     * 
     * @param version the version to validate
     * @param result the validation result
     */
    private void validateVersion(String version, ValidationResult result) {
        if (version == null || version.trim().isEmpty()) {
            result.addError("Event version cannot be null or empty");
            return;
        }
        
        boolean isSupported = false;
        for (String supportedVersion : SUPPORTED_VERSIONS) {
            if (supportedVersion.equals(version)) {
                isSupported = true;
                break;
            }
        }
        
        if (!isSupported) {
            result.addError("Unsupported event version: " + version + 
                ". Supported versions: " + String.join(", ", SUPPORTED_VERSIONS));
        }
    }
    
    /**
     * Validates event data based on event type.
     * 
     * @param data the event data to validate
     * @param result the validation result
     * @param <T> the type of event data
     */
    private <T> void validateEventData(T data, ValidationResult result) {
        if (data == null) {
            result.addError("Event data cannot be null");
            return;
        }
        
        // TODO: Implement specific validation rules for different event types
        // This would involve checking specific fields based on the event type
        logger.debug("Event data validation completed for type: {}", data.getClass().getSimpleName());
    }
    
    /**
     * Checks if a schema version is supported.
     * 
     * @param version the version to check
     * @return true if version is supported, false otherwise
     */
    public boolean isVersionSupported(String version) {
        if (version == null) {
            return false;
        }
        
        for (String supportedVersion : SUPPORTED_VERSIONS) {
            if (supportedVersion.equals(version)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets the latest supported schema version.
     * 
     * @return the latest supported version
     */
    public String getLatestSupportedVersion() {
        return SUPPORTED_VERSIONS[SUPPORTED_VERSIONS.length - 1];
    }
    
    /**
     * Gets all supported schema versions.
     * 
     * @return array of supported versions
     */
    public String[] getSupportedVersions() {
        return SUPPORTED_VERSIONS.clone();
    }
    
    /**
     * Validation result container.
     */
    private static class ValidationResult {
        private final StringBuilder errorMessage = new StringBuilder();
        private boolean valid = true;
        
        public void addError(String error) {
            if (!valid) {
                errorMessage.append("; ");
            }
            errorMessage.append(error);
            valid = false;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage.toString();
        }
    }
}
