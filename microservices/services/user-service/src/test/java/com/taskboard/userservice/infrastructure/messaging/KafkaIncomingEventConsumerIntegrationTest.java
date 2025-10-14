package com.taskboard.userservice.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.domain.event.IncomingEvent;
import com.taskboard.userservice.domain.event.IncomingEventProcessor;
import com.taskboard.userservice.domain.event.task.TaskCreatedEvent;
import com.taskboard.userservice.domain.event.task.TaskCreatedEvent.TaskData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaIncomingEventConsumer Integration Tests")
class KafkaIncomingEventConsumerIntegrationTest {
    
    @Mock
    private IncomingEventProcessor eventProcessor;
    
    @Mock
    private ObjectMapper objectMapper;
    
    private KafkaIncomingEventConsumer kafkaConsumer;
    
    @BeforeEach
    void setUp() {
        kafkaConsumer = new KafkaIncomingEventConsumer(eventProcessor, objectMapper);
    }
    
    @Nested
    @DisplayName("Event Consumption Tests")
    class EventConsumptionTests {
        
        @Test
        @DisplayName("Should process task created event successfully")
        void shouldProcessTaskCreatedEventSuccessfully() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            String eventType = "task.created";
            String sourceService = "task-service";
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Test Task")
                .description("Test Description")
                .userId(100L)
                .projectId(200L)
                .status("TODO")
                .priority("MEDIUM")
                .createdAt(timestamp)
                .build();
            
            TaskCreatedEvent eventData = TaskCreatedEvent.builder(
                .eventId(eventId)
                .eventType(eventType)
                .sourceService(sourceService)
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            String jsonPayload = """
                {
                    "eventId": "%s",
                    "eventType": "%s",
                    "sourceService": "%s",
                    "timestamp": "%s",
                    "data": {
                        "taskId": 1,
                        "title": "Test Task",
                        "description": "Test Description",
                        "userId": 100,
                        "projectId": 200,
                        "status": "TODO",
                        "priority": "MEDIUM",
                        "createdAt": "%s"
                    }
                }
                """.formatted(eventId, eventType, sourceService, timestamp, timestamp);
            
            when(objectMapper.readValue(jsonPayload, TaskCreatedEvent.class)).thenReturn(eventData);
            
            Message<String> message = MessageBuilder
                .withPayload(jsonPayload)
                .setHeader(KafkaHeaders.RECEIVED_TOPIC, "user-service-events")
                .setHeader(KafkaHeaders.RECEIVED_PARTITION, 0)
                .setHeader(KafkaHeaders.OFFSET, 123L)
                .build();
            
            // When
            kafkaConsumer.consume(message);
            
            // Then
            verify(objectMapper).readValue(jsonPayload, TaskCreatedEvent.class);
            verify(eventProcessor).processEvent(any(IncomingEvent.class));
        }
        
        @Test
        @DisplayName("Should handle malformed JSON gracefully")
        void shouldHandleMalformedJsonGracefully() throws Exception {
            // Given
            String malformedJson = "{ invalid json }";
            
            when(objectMapper.readValue(malformedJson, TaskCreatedEvent.class))
                .thenThrow(new RuntimeException("Invalid JSON"));
            
            Message<String> message = MessageBuilder
                .withPayload(malformedJson)
                .setHeader(KafkaHeaders.RECEIVED_TOPIC, "user-service-events")
                .build();
            
            // When & Then
            assertThatThrownBy(() -> kafkaConsumer.consume(message))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid JSON");
            
            verify(eventProcessor, never()).processEvent(any());
        }
        
        @Test
        @DisplayName("Should handle missing event headers")
        void shouldHandleMissingEventHeaders() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            String eventType = "task.created";
            String sourceService = "task-service";
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Test Task")
                .userId(100L)
                .build();
            
            TaskCreatedEvent eventData = TaskCreatedEvent.builder(
                .eventId(eventId)
                .eventType(eventType)
                .sourceService(sourceService)
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            String jsonPayload = """
                {
                    "eventId": "%s",
                    "eventType": "%s",
                    "sourceService": "%s",
                    "timestamp": "%s",
                    "data": {
                        "taskId": 1,
                        "title": "Test Task",
                        "userId": 100
                    }
                }
                """.formatted(eventId, eventType, sourceService, timestamp);
            
            when(objectMapper.readValue(jsonPayload, TaskCreatedEvent.class)).thenReturn(eventData);
            
            Message<String> message = MessageBuilder
                .withPayload(jsonPayload)
                .build(); // No headers
            
            // When
            kafkaConsumer.consume(message);
            
            // Then
            verify(objectMapper).readValue(jsonPayload, TaskCreatedEvent.class);
            verify(eventProcessor).processEvent(any(IncomingEvent.class));
        }
        
        @Test
        @DisplayName("Should handle event processing failure")
        void shouldHandleEventProcessingFailure() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            String eventType = "task.created";
            String sourceService = "task-service";
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Test Task")
                .userId(100L)
                .build();
            
            TaskCreatedEvent eventData = TaskCreatedEvent.builder(
                .eventId(eventId)
                .eventType(eventType)
                .sourceService(sourceService)
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            String jsonPayload = """
                {
                    "eventId": "%s",
                    "eventType": "%s",
                    "sourceService": "%s",
                    "timestamp": "%s",
                    "data": {
                        "taskId": 1,
                        "title": "Test Task",
                        "userId": 100
                    }
                }
                """.formatted(eventId, eventType, sourceService, timestamp);
            
            when(objectMapper.readValue(jsonPayload, TaskCreatedEvent.class)).thenReturn(eventData);
            doThrow(new RuntimeException("Processing failed")).when(eventProcessor).processEvent(any());
            
            Message<String> message = MessageBuilder
                .withPayload(jsonPayload)
                .setHeader(KafkaHeaders.RECEIVED_TOPIC, "user-service-events")
                .build();
            
            // When & Then
            assertThatThrownBy(() -> kafkaConsumer.consume(message))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Processing failed");
            
            verify(objectMapper).readValue(jsonPayload, TaskCreatedEvent.class);
            verify(eventProcessor).processEvent(any());
        }
    }
    
    @Nested
    @DisplayName("Message Header Processing Tests")
    class MessageHeaderProcessingTests {
        
        @Test
        @DisplayName("Should extract and use message headers")
        void shouldExtractAndUseMessageHeaders() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            String eventType = "task.created";
            String sourceService = "task-service";
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Test Task")
                .userId(100L)
                .build();
            
            TaskCreatedEvent eventData = TaskCreatedEvent.builder(
                .eventId(eventId)
                .eventType(eventType)
                .sourceService(sourceService)
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            String jsonPayload = """
                {
                    "eventId": "%s",
                    "eventType": "%s",
                    "sourceService": "%s",
                    "timestamp": "%s",
                    "data": {
                        "taskId": 1,
                        "title": "Test Task",
                        "userId": 100
                    }
                }
                """.formatted(eventId, eventType, sourceService, timestamp);
            
            when(objectMapper.readValue(jsonPayload, TaskCreatedEvent.class)).thenReturn(eventData);
            
            Map<String, Object> headers = new HashMap<>();
            headers.put(KafkaHeaders.RECEIVED_TOPIC, "user-service-events");
            headers.put(KafkaHeaders.RECEIVED_PARTITION, 0);
            headers.put(KafkaHeaders.OFFSET, 123L);
            headers.put("custom-header", "custom-value");
            
            Message<String> message = MessageBuilder
                .withPayload(jsonPayload)
                .copyHeaders(headers)
                .build();
            
            // When
            kafkaConsumer.consume(message);
            
            // Then
            verify(objectMapper).readValue(jsonPayload, TaskCreatedEvent.class);
            verify(eventProcessor).processEvent(any(IncomingEvent.class));
        }
        
        @Test
        @DisplayName("Should handle null headers gracefully")
        void shouldHandleNullHeadersGracefully() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            String eventType = "task.created";
            String sourceService = "task-service";
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Test Task")
                .userId(100L)
                .build();
            
            TaskCreatedEvent eventData = TaskCreatedEvent.builder(
                .eventId(eventId)
                .eventType(eventType)
                .sourceService(sourceService)
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            String jsonPayload = """
                {
                    "eventId": "%s",
                    "eventType": "%s",
                    "sourceService": "%s",
                    "timestamp": "%s",
                    "data": {
                        "taskId": 1,
                        "title": "Test Task",
                        "userId": 100
                    }
                }
                """.formatted(eventId, eventType, sourceService, timestamp);
            
            when(objectMapper.readValue(jsonPayload, TaskCreatedEvent.class)).thenReturn(eventData);
            
            Message<String> message = MessageBuilder
                .withPayload(jsonPayload)
                .setHeader("null-header", null)
                .build();
            
            // When
            kafkaConsumer.consume(message);
            
            // Then
            verify(objectMapper).readValue(jsonPayload, TaskCreatedEvent.class);
            verify(eventProcessor).processEvent(any(IncomingEvent.class));
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle ObjectMapper serialization errors")
        void shouldHandleObjectMapperSerializationErrors() throws Exception {
            // Given
            String jsonPayload = "valid json but wrong structure";
            
            when(objectMapper.readValue(jsonPayload, TaskCreatedEvent.class))
                .thenThrow(new RuntimeException("Deserialization failed"));
            
            Message<String> message = MessageBuilder
                .withPayload(jsonPayload)
                .build();
            
            // When & Then
            assertThatThrownBy(() -> kafkaConsumer.consume(message))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Deserialization failed");
            
            verify(eventProcessor, never()).processEvent(any());
        }
        
        @Test
        @DisplayName("Should handle null message payload")
        void shouldHandleNullMessagePayload() {
            // Given
            Message<String> message = MessageBuilder
                .withPayload((String) null)
                .build();
            
            // When & Then
            assertThatThrownBy(() -> kafkaConsumer.consume(message))
                .isInstanceOf(IllegalArgumentException.class);
            
            verify(objectMapper, never()).readValue(anyString(), any(Class.class));
            verify(eventProcessor, never()).processEvent(any());
        }
        
        @Test
        @DisplayName("Should handle empty message payload")
        void shouldHandleEmptyMessagePayload() throws Exception {
            // Given
            String emptyPayload = "";
            
            when(objectMapper.readValue(emptyPayload, TaskCreatedEvent.class))
                .thenThrow(new RuntimeException("Empty payload"));
            
            Message<String> message = MessageBuilder
                .withPayload(emptyPayload)
                .build();
            
            // When & Then
            assertThatThrownBy(() -> kafkaConsumer.consume(message))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Empty payload");
            
            verify(eventProcessor, never()).processEvent(any());
        }
    }
    
    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {
        
        @Test
        @DisplayName("Should process multiple events efficiently")
        void shouldProcessMultipleEventsEfficiently() throws Exception {
            // Given
            int eventCount = 100;
            UUID eventId = UUID.randomUUID();
            String eventType = "task.created";
            String sourceService = "task-service";
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Test Task")
                .userId(100L)
                .build();
            
            TaskCreatedEvent eventData = TaskCreatedEvent.builder(
                .eventId(eventId)
                .eventType(eventType)
                .sourceService(sourceService)
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            String jsonPayload = """
                {
                    "eventId": "%s",
                    "eventType": "%s",
                    "sourceService": "%s",
                    "timestamp": "%s",
                    "data": {
                        "taskId": 1,
                        "title": "Test Task",
                        "userId": 100
                    }
                }
                """.formatted(eventId, eventType, sourceService, timestamp);
            
            when(objectMapper.readValue(jsonPayload, TaskCreatedEvent.class)).thenReturn(eventData);
            
            Message<String> message = MessageBuilder
                .withPayload(jsonPayload)
                .build();
            
            // When
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < eventCount; i++) {
                kafkaConsumer.consume(message);
            }
            long endTime = System.currentTimeMillis();
            
            // Then
            assertThat(endTime - startTime).isLessThan(5000); // Should complete within 5 seconds
            verify(objectMapper, times(eventCount)).readValue(jsonPayload, TaskCreatedEvent.class);
            verify(eventProcessor, times(eventCount)).processEvent(any(IncomingEvent.class));
        }
    }
}
