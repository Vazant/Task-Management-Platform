package com.taskboard.userservice.application.event;

import com.taskboard.userservice.application.service.UserNotificationService;
import com.taskboard.userservice.application.service.UserStatisticsService;
import com.taskboard.userservice.domain.event.EventDeduplicationService;
import com.taskboard.userservice.domain.event.EventRetryService;
import com.taskboard.userservice.domain.event.EventValidationService;
import com.taskboard.userservice.domain.event.IncomingEvent;
import com.taskboard.userservice.domain.event.IncomingEventProcessor;
import com.taskboard.userservice.domain.event.task.TaskCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventProcessingIntegrationTest {
    
    @Mock
    private UserStatisticsService userStatisticsService;
    
    @Mock
    private UserNotificationService userNotificationService;
    
    private EventDeduplicationService deduplicationService;
    private EventValidationService validationService;
    private EventRetryService retryService;
    private IncomingEventProcessor eventProcessor;
    private TaskCreatedEventHandler taskCreatedEventHandler;
    
    @BeforeEach
    void setUp() {
        deduplicationService = new EventDeduplicationService();
        validationService = new EventValidationService();
        retryService = new EventRetryService();
        
        taskCreatedEventHandler = new TaskCreatedEventHandler(userStatisticsService, userNotificationService);
        
        List<com.taskboard.userservice.domain.event.IncomingEventHandler<?>> handlers = List.of(
            taskCreatedEventHandler
        );
        
        eventProcessor = new IncomingEventProcessor(handlers, deduplicationService, validationService, retryService);
    }
    
    @Test
    void shouldProcessValidEventSuccessfully() {
        // Given
        UUID eventId = UUID.randomUUID();
        TaskCreatedEvent.TaskData taskData = TaskCreatedEvent.TaskData.builder()
            .taskId(1L)
            .title("Test Task")
            .description("Test Description")
            .userId(1L)
            .projectId(1L)
            .status("TODO")
            .priority("MEDIUM")
            .createdAt(LocalDateTime.now())
            .build();
        
        TestEvent event = new TestEvent(eventId, "task.created", "task-service", 
            LocalDateTime.now(), "1.0", taskData);
        
        when(userNotificationService.shouldNotifyUser(anyLong(), anyString())).thenReturn(true);
        
        // When
        eventProcessor.processEvent(event);
        
        // Then
        verify(userStatisticsService).updateTaskCreatedStatistics(1L);
        verify(userNotificationService).shouldNotifyUser(1L, "task_created");
        verify(userNotificationService).sendTaskCreatedNotification(1L, "Test Task", 1L);
        verify(userStatisticsService).updateUserActivity(1L, "task_created");
        
        // Verify event is marked as processed
        assertThat(deduplicationService.isEventProcessed(eventId)).isTrue();
    }
    
    @Test
    void shouldNotProcessDuplicateEvent() {
        // Given
        UUID eventId = UUID.randomUUID();
        TaskCreatedEvent.TaskData taskData = TaskCreatedEvent.TaskData.builder()
            .taskId(1L)
            .title("Test Task")
            .userId(1L)
            .build();
        
        TestEvent event = new TestEvent(eventId, "task.created", "task-service", 
            LocalDateTime.now(), "1.0", taskData);
        
        // Process event first time
        when(userNotificationService.shouldNotifyUser(anyLong(), anyString())).thenReturn(true);
        eventProcessor.processEvent(event);
        
        // Reset mocks
        reset(userStatisticsService, userNotificationService);
        
        // When - try to process same event again
        eventProcessor.processEvent(event);
        
        // Then - should not call any services
        verifyNoInteractions(userStatisticsService);
        verifyNoInteractions(userNotificationService);
    }
    
    @Test
    void shouldRetryFailedEventProcessing() {
        // Given
        UUID eventId = UUID.randomUUID();
        TaskCreatedEvent.TaskData taskData = TaskCreatedEvent.TaskData.builder()
            .taskId(1L)
            .title("Test Task")
            .userId(1L)
            .build();
        
        TestEvent event = new TestEvent(eventId, "task.created", "task-service", 
            LocalDateTime.now(), "1.0", taskData);
        
        // Make first call fail, second call succeed
        doThrow(new RuntimeException("Temporary failure"))
            .doNothing()
            .when(userStatisticsService).updateTaskCreatedStatistics(anyLong());
        
        when(userNotificationService.shouldNotifyUser(anyLong(), anyString())).thenReturn(true);
        
        // When
        eventProcessor.processEvent(event);
        
        // Then - should retry and eventually succeed
        verify(userStatisticsService, atLeast(2)).updateTaskCreatedStatistics(1L);
        verify(userNotificationService).shouldNotifyUser(1L, "task_created");
        verify(userNotificationService).sendTaskCreatedNotification(1L, "Test Task", 1L);
        verify(userStatisticsService).updateUserActivity(1L, "task_created");
    }
    
    @Test
    void shouldRejectInvalidEvent() {
        // Given
        UUID eventId = UUID.randomUUID();
        TaskCreatedEvent.TaskData taskData = TaskCreatedEvent.TaskData.builder()
            .taskId(1L)
            .title("Test Task")
            .userId(1L)
            .build();
        
        // Invalid event with wrong event type
        TestEvent event = new TestEvent(eventId, "invalid.event", "task-service", 
            LocalDateTime.now(), "1.0", taskData);
        
        // When & Then
        assertThatThrownBy(() -> eventProcessor.processEvent(event))
            .isInstanceOf(com.taskboard.userservice.domain.event.EventProcessingException.class)
            .hasMessageContaining("Event validation failed");
        
        // Verify no services were called
        verifyNoInteractions(userStatisticsService);
        verifyNoInteractions(userNotificationService);
    }
    
    /**
     * Test implementation of IncomingEvent for testing purposes.
     */
    private static class TestEvent implements IncomingEvent<TaskCreatedEvent.TaskData> {
        private final UUID eventId;
        private final String eventType;
        private final String sourceService;
        private final LocalDateTime timestamp;
        private final String version;
        private final TaskCreatedEvent.TaskData data;
        
        public TestEvent(UUID eventId, String eventType, String sourceService, 
                        LocalDateTime timestamp, String version, TaskCreatedEvent.TaskData data) {
            this.eventId = eventId;
            this.eventType = eventType;
            this.sourceService = sourceService;
            this.timestamp = timestamp;
            this.version = version;
            this.data = data;
        }
        
        @Override
        public UUID getEventId() {
            return eventId;
        }
        
        @Override
        public String getEventType() {
            return eventType;
        }
        
        @Override
        public String getSourceService() {
            return sourceService;
        }
        
        @Override
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String getVersion() {
            return version;
        }
        
        @Override
        public TaskCreatedEvent.TaskData getData() {
            return data;
        }
    }
}
