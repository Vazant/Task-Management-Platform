package com.taskboard.userservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base interface for all incoming events from other services.
 * 
 * @param <T> The type of event data
 */
public interface IncomingEvent<T> {
    
    /**
     * Gets the unique event identifier.
     * 
     * @return the event ID
     */
    UUID getEventId();
    
    /**
     * Gets the event type identifier.
     * 
     * @return the event type
     */
    String getEventType();
    
    /**
     * Gets the source service that published this event.
     * 
     * @return the source service name
     */
    String getSourceService();
    
    /**
     * Gets the timestamp when the event was created.
     * 
     * @return the event timestamp
     */
    LocalDateTime getTimestamp();
    
    /**
     * Gets the event data payload.
     * 
     * @return the event data
     */
    T getData();
    
    /**
     * Gets the event version for schema evolution.
     * 
     * @return the event version
     */
    String getVersion();
}
