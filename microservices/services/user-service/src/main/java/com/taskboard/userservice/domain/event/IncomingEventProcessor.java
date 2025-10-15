package com.taskboard.userservice.domain.event;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Processes incoming events by routing them to appropriate handlers.
 * 
 * <p>This processor integrates with event deduplication, validation, and retry services
 * to provide robust event processing with proper error handling and monitoring.</p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@Component
@Slf4j
public class IncomingEventProcessor {
    
    private final Map<String, IncomingEventHandler<?>> eventHandlers;
    private final EventDeduplicationService deduplicationService;
    private final EventValidationService validationService;
    private final EventRetryService retryService;
    
    public IncomingEventProcessor(List<IncomingEventHandler<?>> handlers,
                                EventDeduplicationService deduplicationService,
                                EventValidationService validationService,
                                EventRetryService retryService) {
        this.eventHandlers = handlers.stream()
            .collect(Collectors.toMap(
                this::createHandlerKey,
                Function.identity(),
                (existing, replacement) -> {
                    log.warn("Duplicate handler for key: {}, using existing handler", createHandlerKey(existing));
                    return existing;
                },
                ConcurrentHashMap::new
            ));
        
        this.deduplicationService = deduplicationService;
        this.validationService = validationService;
        this.retryService = retryService;
        
        log.info("Initialized IncomingEventProcessor with {} handlers", eventHandlers.size());
    }
    
    /**
     * Processes an incoming event by finding and invoking the appropriate handler.
     * 
     * <p>This method implements a complete event processing pipeline including:
     * <ul>
     *   <li>Event validation</li>
     *   <li>Deduplication checking</li>
     *   <li>Handler routing</li>
     *   <li>Retry mechanism for failures</li>
     * </ul>
     * </p>
     * 
     * @param event the incoming event to process
     * @param <T> the type of event data
     */
    public <T> void processEvent(IncomingEvent<T> event) {
        UUID eventId = event.getEventId();
        String eventType = event.getEventType();
        String sourceService = event.getSourceService();
        
        log.debug("Processing event: {} from service: {} with ID: {}", 
            eventType, sourceService, eventId);
        
        // Step 1: Validate event
        try {
            validationService.validateEvent(event);
        } catch (EventValidationException e) {
            log.error("Event validation failed for event {}: {}", eventId, e.getMessage());
            throw new EventProcessingException("Event validation failed", e);
        }
        
        // Step 2: Check for duplicates
        if (deduplicationService.isEventProcessed(eventId)) {
            log.debug("Event {} already processed, skipping", eventId);
            return;
        }
        
        // Step 3: Find appropriate handler
        String handlerKey = createEventKey(eventType, sourceService);
        @SuppressWarnings("unchecked")
        IncomingEventHandler<T> handler = (IncomingEventHandler<T>) eventHandlers.get(handlerKey);
        
        if (handler == null) {
            log.warn("No handler found for event type: {} from service: {}", 
                eventType, sourceService);
            return;
        }
        
        // Step 4: Process event with retry mechanism
        try {
            retryService.processEventWithRetry(eventId, eventType, () -> {
                handler.handle(event);
                deduplicationService.markEventAsProcessed(eventId);
                return null;
            }).get(); // Wait for completion
            
            log.info("Successfully processed event: {} from service: {} with ID: {}", 
                eventType, sourceService, eventId);
                
        } catch (Exception e) {
            log.error("Failed to process event: {} from service: {} with ID: {}", 
                eventType, sourceService, eventId, e);
            throw new EventProcessingException("Failed to process event", e);
        }
    }
    
    /**
     * Creates a unique key for event handlers.
     * 
     * @param eventType the event type
     * @param sourceService the source service
     * @return the handler key
     */
    private String createEventKey(String eventType, String sourceService) {
        return sourceService + ":" + eventType;
    }
    
    /**
     * Creates a unique key for event handlers.
     * 
     * @param handler the event handler
     * @return the handler key
     */
    private String createHandlerKey(IncomingEventHandler<?> handler) {
        return createEventKey(handler.getEventType(), handler.getSourceService());
    }
    
    /**
     * Gets the number of registered event handlers.
     * 
     * @return the number of handlers
     */
    public int getHandlerCount() {
        return eventHandlers.size();
    }
    
    /**
     * Checks if a handler exists for the given event type and source service.
     * 
     * @param eventType the event type
     * @param sourceService the source service
     * @return true if handler exists, false otherwise
     */
    public boolean hasHandler(String eventType, String sourceService) {
        return eventHandlers.containsKey(createEventKey(eventType, sourceService));
    }
}
