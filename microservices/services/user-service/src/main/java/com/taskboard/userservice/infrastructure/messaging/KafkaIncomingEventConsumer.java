package com.taskboard.userservice.infrastructure.messaging;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.domain.event.IncomingEvent;
import com.taskboard.userservice.domain.event.IncomingEventProcessor;
import com.taskboard.userservice.domain.event.task.TaskCreatedEvent;
import com.taskboard.userservice.domain.event.task.TaskUpdatedEvent;
import com.taskboard.userservice.domain.event.project.ProjectMemberAddedEvent;

/**
 * Kafka consumer for incoming events from other services.
 * 
 * TODO: Add event deduplication based on event ID
 * TODO: Implement dead letter queue for failed events
 * TODO: Add event validation and schema evolution
 * TODO: Implement retry mechanism with exponential backoff
 * TODO: Add metrics and monitoring for event processing
 */
@Component
@Profile("!test") // Exclude this component from the "test" profile
@RequiredArgsConstructor
@Slf4j
public class KafkaIncomingEventConsumer {
    
    private final IncomingEventProcessor eventProcessor;
    private final ObjectMapper objectMapper;
    
    /**
     * Consumes task.created events from Task Service.
     * 
     * @param payload the event payload
     * @param eventId the event ID from Kafka headers
     * @param timestamp the event timestamp
     */
    @KafkaListener(topics = "task.created", groupId = "user-service")
    public void handleTaskCreatedEvent(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = "eventId", required = false) String eventId,
            @Header(value = "timestamp", required = false) String timestamp) {
        
        log.debug("Received task.created event: {}", payload);
        
        try {
            // TODO: Parse event payload and create TaskCreatedEvent
            TaskCreatedEvent.TaskData taskData = parseTaskCreatedData(payload);
            
            TaskCreatedEvent event = new TaskCreatedEvent(
                eventId != null ? UUID.fromString(eventId) : UUID.randomUUID(),
                "task.created",
                "task-service",
                parseTimestamp(timestamp),
                "1.0",
                taskData
            );
            
            eventProcessor.processEvent(event);
            
        } catch (Exception e) {
            log.error("Failed to process task.created event: {}", payload, e);
            // TODO: Send to dead letter queue
            throw e;
        }
    }
    
    /**
     * Consumes task.updated events from Task Service.
     * 
     * @param payload the event payload
     * @param eventId the event ID from Kafka headers
     * @param timestamp the event timestamp
     */
    @KafkaListener(topics = "task.updated", groupId = "user-service")
    public void handleTaskUpdatedEvent(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = "eventId", required = false) String eventId,
            @Header(value = "timestamp", required = false) String timestamp) {
        
        log.debug("Received task.updated event: {}", payload);
        
        try {
            // TODO: Parse event payload and create TaskUpdatedEvent
            TaskUpdatedEvent.TaskData taskData = parseTaskUpdatedData(payload);
            
            TaskUpdatedEvent event = new TaskUpdatedEvent(
                eventId != null ? UUID.fromString(eventId) : UUID.randomUUID(),
                "task.updated",
                "task-service",
                parseTimestamp(timestamp),
                "1.0",
                taskData
            );
            
            eventProcessor.processEvent(event);
            
        } catch (Exception e) {
            log.error("Failed to process task.updated event: {}", payload, e);
            // TODO: Send to dead letter queue
            throw e;
        }
    }
    
    /**
     * Consumes project.member.added events from Project Service.
     * 
     * @param payload the event payload
     * @param eventId the event ID from Kafka headers
     * @param timestamp the event timestamp
     */
    @KafkaListener(topics = "project.member.added", groupId = "user-service")
    public void handleProjectMemberAddedEvent(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = "eventId", required = false) String eventId,
            @Header(value = "timestamp", required = false) String timestamp) {
        
        log.debug("Received project.member.added event: {}", payload);
        
        try {
            // TODO: Parse event payload and create ProjectMemberAddedEvent
            ProjectMemberAddedEvent.ProjectMemberData memberData = parseProjectMemberData(payload);
            
            ProjectMemberAddedEvent event = new ProjectMemberAddedEvent(
                eventId != null ? UUID.fromString(eventId) : UUID.randomUUID(),
                "project.member.added",
                "project-service",
                parseTimestamp(timestamp),
                "1.0",
                memberData
            );
            
            eventProcessor.processEvent(event);
            
        } catch (Exception e) {
            log.error("Failed to process project.member.added event: {}", payload, e);
            // TODO: Send to dead letter queue
            throw e;
        }
    }
    
    /**
     * Parses task created data from JSON payload.
     * 
     * @param payload the JSON payload
     * @return the parsed task data
     */
    private TaskCreatedEvent.TaskData parseTaskCreatedData(String payload) {
        // TODO: Implement JSON parsing for TaskCreatedEvent.TaskData
        log.debug("Parsing task created data from payload: {}", payload);
        
        // Placeholder implementation
        return TaskCreatedEvent.TaskData.builder()
            .taskId(1L)
            .title("Sample Task")
            .description("Sample Description")
            .userId(1L)
            .projectId(1L)
            .status("TODO")
            .priority("MEDIUM")
            .createdAt(java.time.LocalDateTime.now())
            .build();
    }
    
    /**
     * Parses task updated data from JSON payload.
     * 
     * @param payload the JSON payload
     * @return the parsed task data
     */
    private TaskUpdatedEvent.TaskData parseTaskUpdatedData(String payload) {
        // TODO: Implement JSON parsing for TaskUpdatedEvent.TaskData
        log.debug("Parsing task updated data from payload: {}", payload);
        
        // Placeholder implementation
        return new TaskUpdatedEvent.TaskData(
            1L, "Sample Task", "Sample Description", 1L, 1L, 
            "IN_PROGRESS", "HIGH", java.time.LocalDateTime.now(), "admin"
        );
    }
    
    /**
     * Parses project member data from JSON payload.
     * 
     * @param payload the JSON payload
     * @return the parsed project member data
     */
    private ProjectMemberAddedEvent.ProjectMemberData parseProjectMemberData(String payload) {
        // TODO: Implement JSON parsing for ProjectMemberAddedEvent.ProjectMemberData
        log.debug("Parsing project member data from payload: {}", payload);
        
        // Placeholder implementation
        return new ProjectMemberAddedEvent.ProjectMemberData(
            1L, "Sample Project", 1L, "MEMBER", "admin", 
            java.time.LocalDateTime.now(), "invitation-token"
        );
    }
    
    /**
     * Parses timestamp from string.
     * 
     * @param timestamp the timestamp string
     * @return the parsed timestamp
     */
    private java.time.LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null) {
            return java.time.LocalDateTime.now();
        }
        
        try {
            return java.time.LocalDateTime.parse(timestamp);
        } catch (Exception e) {
            log.warn("Failed to parse timestamp: {}, using current time", timestamp);
            return java.time.LocalDateTime.now();
        }
    }
}
