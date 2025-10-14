package com.taskboard.userservice.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.application.event.ProjectMemberAddedEventHandler;
import com.taskboard.userservice.application.event.TaskCreatedEventHandler;
import com.taskboard.userservice.application.event.TaskUpdatedEventHandler;
import com.taskboard.userservice.domain.event.EventDeduplicationService;
import com.taskboard.userservice.domain.event.EventRetryService;
import com.taskboard.userservice.domain.event.EventValidationService;
import com.taskboard.userservice.domain.event.IncomingEventProcessor;

/**
 * Configuration for incoming event processing.
 * 
 * TODO: Add event validation configuration
 * TODO: Add retry policy configuration
 * TODO: Add dead letter queue configuration
 * TODO: Add event schema registry configuration
 */
@Configuration
public class IncomingEventConfig {
    
    /**
     * Creates ObjectMapper bean for event serialization/deserialization.
     * 
     * @return configured ObjectMapper
     */
    @Bean
    public ObjectMapper eventObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // TODO: Configure Jackson for event serialization
        // - Add custom serializers/deserializers
        // - Configure date/time handling
        // - Add schema validation
        
        return mapper;
    }
    
    /**
     * Creates IncomingEventProcessor with all event handlers and supporting services.
     * 
     * @param taskCreatedHandler the task created event handler
     * @param taskUpdatedHandler the task updated event handler
     * @param projectMemberAddedHandler the project member added event handler
     * @param deduplicationService the event deduplication service
     * @param validationService the event validation service
     * @param retryService the event retry service
     * @return configured IncomingEventProcessor
     */
    @Bean
    public IncomingEventProcessor incomingEventProcessor(
            TaskCreatedEventHandler taskCreatedHandler,
            TaskUpdatedEventHandler taskUpdatedHandler,
            ProjectMemberAddedEventHandler projectMemberAddedHandler,
            EventDeduplicationService deduplicationService,
            EventValidationService validationService,
            EventRetryService retryService) {
        
        List<com.taskboard.userservice.domain.event.IncomingEventHandler<?>> handlers = List.of(
            taskCreatedHandler,
            taskUpdatedHandler,
            projectMemberAddedHandler
        );
        
        return new IncomingEventProcessor(handlers, deduplicationService, validationService, retryService);
    }
}
