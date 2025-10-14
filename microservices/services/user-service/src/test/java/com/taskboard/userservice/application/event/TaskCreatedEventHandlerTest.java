package com.taskboard.userservice.application.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taskboard.userservice.application.service.UserNotificationService;
import com.taskboard.userservice.application.service.UserStatisticsService;
import com.taskboard.userservice.domain.event.IncomingEvent;
import com.taskboard.userservice.domain.event.task.TaskCreatedEvent;

/**
 * Unit tests for TaskCreatedEventHandler.
 * 
 * TODO: Add more comprehensive test scenarios
 * TODO: Test error handling and edge cases
 * TODO: Add integration tests with real services
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskCreatedEventHandler Tests")
class TaskCreatedEventHandlerTest {
    
    @Mock
    private UserStatisticsService userStatisticsService;
    
    @Mock
    private UserNotificationService userNotificationService;
    
    private TaskCreatedEventHandler eventHandler;
    
    @BeforeEach
    void setUp() {
        eventHandler = new TaskCreatedEventHandler(userStatisticsService, userNotificationService);
    }
    
    @Test
    @DisplayName("Should handle task created event successfully")
    void shouldHandleTaskCreatedEventSuccessfully() {
        // Given
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
        
        IncomingEvent<TaskCreatedEvent.TaskData> event = TaskCreatedEvent.builder(
            UUID.randomUUID(), "task.created", "task-service", LocalDateTime.now(), "1.0", taskData
        );
        
        when(userNotificationService.shouldNotifyUser(anyLong(), anyString())).thenReturn(true);
        
        // When
        eventHandler.handle(event);
        
        // Then
        verify(userStatisticsService).updateTaskCreatedStatistics(1L);
        verify(userNotificationService).shouldNotifyUser(1L, "task_created");
        verify(userNotificationService).sendTaskCreatedNotification(1L, "Test Task", 1L);
        verify(userStatisticsService).updateUserActivity(1L, "task_created");
    }
    
    @Test
    @DisplayName("Should not send notification when user should not be notified")
    void shouldNotSendNotificationWhenUserShouldNotBeNotified() {
        // Given
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
        
        IncomingEvent<TaskCreatedEvent.TaskData> event = TaskCreatedEvent.builder(
            UUID.randomUUID(), "task.created", "task-service", LocalDateTime.now(), "1.0", taskData
        );
        
        when(userNotificationService.shouldNotifyUser(anyLong(), anyString())).thenReturn(false);
        
        // When
        eventHandler.handle(event);
        
        // Then
        verify(userStatisticsService).updateTaskCreatedStatistics(1L);
        verify(userNotificationService).shouldNotifyUser(1L, "task_created");
        verify(userNotificationService, never()).sendTaskCreatedNotification(anyLong(), anyString(), anyLong());
        verify(userStatisticsService).updateUserActivity(1L, "task_created");
    }
    
    @Test
    @DisplayName("Should return correct event type")
    void shouldReturnCorrectEventType() {
        // When & Then
        assert eventHandler.getEventType().equals("task.created");
    }
    
    @Test
    @DisplayName("Should return correct source service")
    void shouldReturnCorrectSourceService() {
        // When & Then
        assert eventHandler.getSourceService().equals("task-service");
    }
}
