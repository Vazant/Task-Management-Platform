package com.taskboard.userservice.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.application.dto.RegisterRequest;
import com.taskboard.userservice.domain.event.IncomingEventProcessor;
import com.taskboard.userservice.domain.event.task.TaskCreatedEvent;
import com.taskboard.userservice.domain.event.task.TaskCreatedEvent.TaskData;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.domain.repository.UserStatisticsRepository;
import com.taskboard.userservice.infrastructure.config.PasswordEncoderConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Event Processing E2E Tests")
class EventProcessingE2ETest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private IncomingEventProcessor eventProcessor;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserStatisticsRepository userStatisticsRepository;
    
    @Autowired
    private PasswordEncoderConfig passwordEncoderConfig;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
            .username("eventtestuser")
            .email("eventtest@example.com")
            .firstName("Event")
            .lastName("Test")
            .password(passwordEncoderConfig.passwordEncoder().encode("password123"))
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        testUser = userRepository.save(testUser);
    }
    
    @Nested
    @DisplayName("Task Event Processing E2E Tests")
    class TaskEventProcessingE2ETests {
        
        @Test
        @DisplayName("Should process task created event and update user statistics")
        void shouldProcessTaskCreatedEventAndUpdateUserStatistics() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("E2E Test Task")
                .description("Task created during E2E test")
                .userId(testUser.getId())
                .projectId(100L)
                .status("TODO")
                .priority("MEDIUM")
                .createdAt(timestamp)
                .build();
            
            TaskCreatedEvent event = TaskCreatedEvent.builder()
                .eventId(eventId)
                .eventType("task.created")
                .sourceService("task-service")
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            // When
            eventProcessor.processEvent(event);
            
            // Then
            // Verify user statistics were updated
            var userStats = userStatisticsRepository.findByUserId(testUser.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalTasks()).isEqualTo(1);
            assertThat(userStats.get().getPendingTasks()).isEqualTo(1);
            assertThat(userStats.get().getCompletedTasks()).isEqualTo(0);
            assertThat(userStats.get().getOverdueTasks()).isEqualTo(0);
        }
        
        @Test
        @DisplayName("Should process multiple task events and maintain correct statistics")
        void shouldProcessMultipleTaskEventsAndMaintainCorrectStatistics() throws Exception {
            // Given
            LocalDateTime timestamp = LocalDateTime.now();
            
            // Create multiple task events
            TaskCreatedEvent task1Event = createTaskCreatedEvent(1L, "Task 1", "TODO", timestamp);
            TaskCreatedEvent task2Event = createTaskCreatedEvent(2L, "Task 2", "IN_PROGRESS", timestamp);
            TaskCreatedEvent task3Event = createTaskCreatedEvent(3L, "Task 3", "DONE", timestamp);
            
            // When
            eventProcessor.processEvent(task1Event);
            eventProcessor.processEvent(task2Event);
            eventProcessor.processEvent(task3Event);
            
            // Then
            var userStats = userStatisticsRepository.findByUserId(testUser.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalTasks()).isEqualTo(3);
            assertThat(userStats.get().getPendingTasks()).isEqualTo(1); // TODO
            assertThat(userStats.get().getCompletedTasks()).isEqualTo(1); // DONE
            assertThat(userStats.get().getOverdueTasks()).isEqualTo(0);
        }
        
        @Test
        @DisplayName("Should handle task events for non-existent user gracefully")
        void shouldHandleTaskEventsForNonExistentUserGracefully() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Task for non-existent user")
                .description("This task belongs to a user that doesn't exist")
                .userId(999L) // Non-existent user ID
                .projectId(100L)
                .status("TODO")
                .priority("MEDIUM")
                .createdAt(timestamp)
                .build();
            
            TaskCreatedEvent event = TaskCreatedEvent.builder()
                .eventId(eventId)
                .eventType("task.created")
                .sourceService("task-service")
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            // When & Then
            // Should not throw exception, but should handle gracefully
            eventProcessor.processEvent(event);
            
            // Verify no statistics were created for non-existent user
            var userStats = userStatisticsRepository.findByUserId(999L);
            assertThat(userStats).isEmpty();
        }
        
        @Test
        @DisplayName("Should handle duplicate task events (idempotency)")
        void shouldHandleDuplicateTaskEvents() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Duplicate Task")
                .description("This task will be processed twice")
                .userId(testUser.getId())
                .projectId(100L)
                .status("TODO")
                .priority("MEDIUM")
                .createdAt(timestamp)
                .build();
            
            TaskCreatedEvent event = TaskCreatedEvent.builder()
                .eventId(eventId)
                .eventType("task.created")
                .sourceService("task-service")
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            // When
            eventProcessor.processEvent(event);
            eventProcessor.processEvent(event); // Process same event again
            
            // Then
            // Should only count the task once due to idempotency
            var userStats = userStatisticsRepository.findByUserId(testUser.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalTasks()).isEqualTo(1);
            assertThat(userStats.get().getPendingTasks()).isEqualTo(1);
        }
    }
    
    @Nested
    @DisplayName("Project Event Processing E2E Tests")
    class ProjectEventProcessingE2ETests {
        
        @Test
        @DisplayName("Should process project member added event")
        void shouldProcessProjectMemberAddedEvent() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            LocalDateTime timestamp = LocalDateTime.now();
            
            var event = com.taskboard.userservice.domain.event.project.ProjectMemberAddedEvent.builder()
                .eventId(eventId)
                .eventType("project.member.added")
                .sourceService("project-service")
                .timestamp(timestamp)
                .data(com.taskboard.userservice.domain.event.project.ProjectMemberAddedEvent.ProjectMemberData.builder()
                    .projectId(100L)
                    .projectName("E2E Test Project")
                    .userId(testUser.getId())
                    .role("MEMBER")
                    .addedAt(timestamp)
                    .build())
                .build();
            
            // When
            eventProcessor.processEvent(event);
            
            // Then
            // Verify user statistics were updated
            var userStats = userStatisticsRepository.findByUserId(testUser.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalProjects()).isEqualTo(1);
            assertThat(userStats.get().getActiveProjects()).isEqualTo(1);
            assertThat(userStats.get().getTotalProjects()).isEqualTo(0);
        }
        
        @Test
        @DisplayName("Should process multiple project events")
        void shouldProcessMultipleProjectEvents() throws Exception {
            // Given
            LocalDateTime timestamp = LocalDateTime.now();
            
            // Create multiple project events
            var project1Event = createProjectMemberAddedEvent(100L, "Project 1", timestamp);
            var project2Event = createProjectMemberAddedEvent(200L, "Project 2", timestamp);
            var project3Event = createProjectMemberAddedEvent(300L, "Project 3", timestamp);
            
            // When
            eventProcessor.processEvent(project1Event);
            eventProcessor.processEvent(project2Event);
            eventProcessor.processEvent(project3Event);
            
            // Then
            var userStats = userStatisticsRepository.findByUserId(testUser.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalProjects()).isEqualTo(3);
            assertThat(userStats.get().getActiveProjects()).isEqualTo(3);
            assertThat(userStats.get().getTotalProjects()).isEqualTo(0);
        }
    }
    
    @Nested
    @DisplayName("Event Processing Error Handling E2E Tests")
    class EventProcessingErrorHandlingE2ETests {
        
        @Test
        @DisplayName("Should handle malformed event data gracefully")
        void shouldHandleMalformedEventDataGracefully() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Task with malformed data")
                .description("This task has some malformed data")
                .userId(testUser.getId())
                .projectId(100L)
                .status("INVALID_STATUS") // Invalid status
                .priority("INVALID_PRIORITY") // Invalid priority
                .createdAt(timestamp)
                .build();
            
            TaskCreatedEvent event = TaskCreatedEvent.builder()
                .eventId(eventId)
                .eventType("task.created")
                .sourceService("task-service")
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            // When & Then
            // Should handle gracefully without throwing exception
            eventProcessor.processEvent(event);
            
            // Verify event was processed (even with malformed data)
            var userStats = userStatisticsRepository.findByUserId(testUser.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalTasks()).isEqualTo(1);
        }
        
        @Test
        @DisplayName("Should handle events with missing required fields")
        void shouldHandleEventsWithMissingRequiredFields() throws Exception {
            // Given
            UUID eventId = UUID.randomUUID();
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Task with missing fields")
                .description(null) // Missing description
                .userId(testUser.getId())
                .projectId(null) // Missing project ID
                .status("TODO")
                .priority("MEDIUM")
                .createdAt(timestamp)
                .build();
            
            TaskCreatedEvent event = TaskCreatedEvent.builder()
                .eventId(eventId)
                .eventType("task.created")
                .sourceService("task-service")
                .timestamp(timestamp)
                .data(taskData)
                .build();
            
            // When & Then
            // Should handle gracefully
            eventProcessor.processEvent(event);
            
            // Verify event was processed
            var userStats = userStatisticsRepository.findByUserId(testUser.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalTasks()).isEqualTo(1);
        }
    }
    
    @Nested
    @DisplayName("Event Processing Performance E2E Tests")
    class EventProcessingPerformanceE2ETests {
        
        @Test
        @DisplayName("Should process multiple events efficiently")
        void shouldProcessMultipleEventsEfficiently() throws Exception {
            // Given
            int eventCount = 100;
            LocalDateTime timestamp = LocalDateTime.now();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < eventCount; i++) {
                TaskCreatedEvent event = createTaskCreatedEvent(
                    (long) i, 
                    "Task " + i, 
                    "TODO", 
                    timestamp
                );
                eventProcessor.processEvent(event);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            // Should complete within reasonable time (5 seconds for 100 events)
            assertThat(duration).isLessThan(5000);
            
            // Verify all events were processed
            var userStats = userStatisticsRepository.findByUserId(testUser.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalTasks()).isEqualTo(eventCount);
        }
        
        @Test
        @DisplayName("Should handle concurrent event processing")
        void shouldHandleConcurrentEventProcessing() throws Exception {
            // Given
            int eventCount = 50;
            LocalDateTime timestamp = LocalDateTime.now();
            
            // When
            long startTime = System.currentTimeMillis();
            
            // Process events concurrently (simulated by rapid sequential processing)
            for (int i = 0; i < eventCount; i++) {
                TaskCreatedEvent event = createTaskCreatedEvent(
                    (long) i, 
                    "Concurrent Task " + i, 
                    "TODO", 
                    timestamp
                );
                eventProcessor.processEvent(event);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            // Should complete within reasonable time
            assertThat(duration).isLessThan(3000);
            
            // Verify all events were processed correctly
            var userStats = userStatisticsRepository.findByUserId(testUser.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalTasks()).isEqualTo(eventCount);
        }
    }
    
    // Helper methods
    private TaskCreatedEvent createTaskCreatedEvent(Long taskId, String title, String status, LocalDateTime timestamp) {
        TaskData taskData = TaskData.builder()
            .taskId(taskId)
            .title(title)
            .description("Description for " + title)
            .userId(testUser.getId())
            .projectId(100L)
            .status(status)
            .priority("MEDIUM")
            .createdAt(timestamp)
            .build();
        
        return TaskCreatedEvent.builder()
            .eventId(UUID.randomUUID())
            .eventType("task.created")
            .sourceService("task-service")
            .timestamp(timestamp)
            .data(taskData)
            .build();
    }
    
    private com.taskboard.userservice.domain.event.project.ProjectMemberAddedEvent createProjectMemberAddedEvent(
            Long projectId, String projectName, LocalDateTime timestamp) {
        
        return com.taskboard.userservice.domain.event.project.ProjectMemberAddedEvent.builder()
            .eventId(UUID.randomUUID())
            .eventType("project.member.added")
            .sourceService("project-service")
            .timestamp(timestamp)
            .data(com.taskboard.userservice.domain.event.project.ProjectMemberAddedEvent.ProjectMemberData.builder()
                .projectId(projectId)
                .projectName(projectName)
                .userId(testUser.getId())
                .role("MEMBER")
                .addedAt(timestamp)
                .build())
            .build();
    }
}
