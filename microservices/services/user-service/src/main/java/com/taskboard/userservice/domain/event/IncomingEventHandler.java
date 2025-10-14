package com.taskboard.userservice.domain.event;

/**
 * Interface for handling incoming events from other services.
 * 
 * @param <T> The type of event data
 */
public interface IncomingEventHandler<T> {
    
    /**
     * Handles an incoming event.
     * 
     * @param event the incoming event to handle
     */
    void handle(IncomingEvent<T> event);
    
    /**
     * Gets the event type this handler can process.
     * 
     * @return the event type
     */
    String getEventType();
    
    /**
     * Gets the source service this handler listens to.
     * 
     * @return the source service name
     */
    String getSourceService();
}
