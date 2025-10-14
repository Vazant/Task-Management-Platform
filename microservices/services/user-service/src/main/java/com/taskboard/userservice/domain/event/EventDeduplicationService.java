package com.taskboard.userservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for event deduplication based on event ID.
 * 
 * <p>This service ensures that events with the same ID are processed only once,
 * preventing duplicate processing and maintaining data consistency.</p>
 * 
 * <p>The service maintains an in-memory cache of processed event IDs with TTL
 * to prevent memory leaks while ensuring deduplication within a reasonable time window.</p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@Service
public class EventDeduplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventDeduplicationService.class);
    
    /**
     * Cache for storing processed event IDs with their processing timestamps.
     * Key: eventId, Value: processing timestamp
     */
    private final ConcurrentHashMap<UUID, LocalDateTime> processedEvents = new ConcurrentHashMap<>();
    
    /**
     * TTL for event deduplication cache in minutes.
     * Events older than this will be removed from cache.
     */
    private static final int CACHE_TTL_MINUTES = 60;
    
    /**
     * Cleanup interval for expired events in minutes.
     */
    private static final int CLEANUP_INTERVAL_MINUTES = 10;
    
    /**
     * Scheduled executor for cache cleanup.
     */
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(
        r -> {
            Thread t = new Thread(r, "event-deduplication-cleanup");
            t.setDaemon(true);
            return t;
        }
    );
    
    /**
     * Constructs EventDeduplicationService and starts cleanup scheduler.
     */
    public EventDeduplicationService() {
        startCleanupScheduler();
    }
    
    /**
     * Checks if an event has already been processed.
     * 
     * @param eventId the event ID to check
     * @return true if event was already processed, false otherwise
     */
    public boolean isEventProcessed(UUID eventId) {
        boolean isProcessed = processedEvents.containsKey(eventId);
        
        if (isProcessed) {
            logger.debug("Event {} already processed, skipping", eventId);
        }
        
        return isProcessed;
    }
    
    /**
     * Marks an event as processed.
     * 
     * @param eventId the event ID to mark as processed
     */
    public void markEventAsProcessed(UUID eventId) {
        LocalDateTime now = LocalDateTime.now();
        processedEvents.put(eventId, now);
        
        logger.debug("Marked event {} as processed at {}", eventId, now);
    }
    
    /**
     * Processes an event if it hasn't been processed before.
     * 
     * @param eventId the event ID
     * @param eventProcessor the event processing logic
     * @return true if event was processed, false if it was already processed
     */
    public boolean processEventIfNotDuplicate(UUID eventId, Runnable eventProcessor) {
        if (isEventProcessed(eventId)) {
            return false;
        }
        
        try {
            eventProcessor.run();
            markEventAsProcessed(eventId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to process event {}, will not mark as processed", eventId, e);
            throw e;
        }
    }
    
    /**
     * Gets the number of events in the deduplication cache.
     * 
     * @return the number of cached events
     */
    public int getCacheSize() {
        return processedEvents.size();
    }
    
    /**
     * Clears all processed events from cache.
     * This method is primarily for testing purposes.
     */
    public void clearCache() {
        int clearedCount = processedEvents.size();
        processedEvents.clear();
        logger.info("Cleared {} events from deduplication cache", clearedCount);
    }
    
    /**
     * Starts the cleanup scheduler for expired events.
     */
    private void startCleanupScheduler() {
        cleanupExecutor.scheduleAtFixedRate(
            this::cleanupExpiredEvents,
            CLEANUP_INTERVAL_MINUTES,
            CLEANUP_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        );
        
        logger.info("Started event deduplication cleanup scheduler with {} minute intervals", 
            CLEANUP_INTERVAL_MINUTES);
    }
    
    /**
     * Removes expired events from the cache.
     */
    private void cleanupExpiredEvents() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(CACHE_TTL_MINUTES);
        int initialSize = processedEvents.size();
        
        processedEvents.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoffTime));
        
        int finalSize = processedEvents.size();
        int removedCount = initialSize - finalSize;
        
        if (removedCount > 0) {
            logger.debug("Cleaned up {} expired events from deduplication cache", removedCount);
        }
    }
    
    /**
     * Shuts down the cleanup scheduler.
     * This method should be called during application shutdown.
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("Event deduplication service shutdown completed");
    }
}
