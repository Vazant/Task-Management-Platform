package com.taskboard.userservice.performance;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Event Processing Performance Tests")
class EventProcessingPerformanceTest {
    
    @Autowired
    private IncomingEventProcessor eventProcessor;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserStatisticsRepository userStatisticsRepository;
    
    @Autowired
    private PasswordEncoderConfig passwordEncoderConfig;
    
    private List<User> testUsers;
    
    @BeforeEach
    void setUp() {
        testUsers = new ArrayList<>();
        
        // Create test users for performance tests
        for (int i = 0; i < 50; i++) {
            User user = User.builder()
                .username("perfuser" + i)
                .email("perfuser" + i + "@example.com")
                .firstName("Performance")
                .lastName("User" + i)
                .password(passwordEncoderConfig.passwordEncoder().encode("password123"))
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            testUsers.add(userRepository.save(user));
        }
    }
    
    @Nested
    @DisplayName("Single Event Processing Performance Tests")
    class SingleEventProcessingPerformanceTests {
        
        @Test
        @DisplayName("Should process single event efficiently")
        void shouldProcessSingleEventEfficiently() throws Exception {
            // Given
            User user = testUsers.get(0);
            UUID eventId = UUID.randomUUID();
            LocalDateTime timestamp = LocalDateTime.now();
            
            TaskData taskData = TaskData.builder()
                .taskId(1L)
                .title("Performance Test Task")
                .description("Task for performance testing")
                .userId(user.getId())
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
            long startTime = System.nanoTime();
            eventProcessor.processEvent(event);
            long endTime = System.nanoTime();
            
            long durationNanos = endTime - startTime;
            long durationMillis = durationNanos / 1_000_000;
            
            // Then
            // Should complete within 100ms for single event
            assertThat(durationMillis).isLessThan(100);
            
            // Verify event was processed
            var userStats = userStatisticsRepository.findByUserId(user.getId());
            assertThat(userStats).isPresent();
            assertThat(userStats.get().getTotalTasks()).isEqualTo(1);
        }
        
        @Test
        @DisplayName("Should process multiple single events efficiently")
        void shouldProcessMultipleSingleEventsEfficiently() throws Exception {
            // Given
            int eventCount = 100;
            LocalDateTime timestamp = LocalDateTime.now();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < eventCount; i++) {
                User user = testUsers.get(i % testUsers.size());
                
                TaskData taskData = TaskData.builder()
                    .taskId((long) i)
                    .title("Task " + i)
                    .description("Description for task " + i)
                    .userId(user.getId())
                    .projectId(100L)
                    .status("TODO")
                    .priority("MEDIUM")
                    .createdAt(timestamp)
                    .build();
                
                TaskCreatedEvent event = TaskCreatedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .eventType("task.created")
                    .sourceService("task-service")
                    .timestamp(timestamp)
                    .data(taskData)
                    .build();
                
                eventProcessor.processEvent(event);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            // Should complete within 5 seconds for 100 events
            assertThat(duration).isLessThan(5000);
            
            // Calculate throughput
            double throughput = (double) eventCount / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(20); // At least 20 events per second
            
            // Verify events were processed
            var userStats = userStatisticsRepository.findAll();
            assertThat(userStats).isNotEmpty();
        }
    }
    
    @Nested
    @DisplayName("Concurrent Event Processing Performance Tests")
    class ConcurrentEventProcessingPerformanceTests {
        
        @Test
        @DisplayName("Should handle concurrent event processing efficiently")
        void shouldHandleConcurrentEventProcessingEfficiently() throws Exception {
            // Given
            int eventCount = 100;
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            LocalDateTime timestamp = LocalDateTime.now();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < eventCount; i++) {
                final int eventIndex = i;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    User user = testUsers.get(eventIndex % testUsers.size());
                    
                    TaskData taskData = TaskData.builder()
                        .taskId((long) eventIndex)
                        .title("Concurrent Task " + eventIndex)
                        .description("Description for concurrent task " + eventIndex)
                        .userId(user.getId())
                        .projectId(100L)
                        .status("TODO")
                        .priority("MEDIUM")
                        .createdAt(timestamp)
                        .build();
                    
                    TaskCreatedEvent event = TaskCreatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("task.created")
                        .sourceService("task-service")
                        .timestamp(timestamp)
                        .data(taskData)
                        .build();
                    
                    eventProcessor.processEvent(event);
                }, executor);
                
                futures.add(future);
            }
            
            // Wait for all events to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            
            // Then
            // Should complete within 10 seconds for 100 concurrent events
            assertThat(duration).isLessThan(10000);
            
            // Calculate throughput
            double throughput = (double) eventCount / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(10); // At least 10 events per second
            
            // Verify events were processed
            var userStats = userStatisticsRepository.findAll();
            assertThat(userStats).isNotEmpty();
        }
        
        @Test
        @DisplayName("Should handle high concurrency event processing")
        void shouldHandleHighConcurrencyEventProcessing() throws Exception {
            // Given
            int eventCount = 200;
            int threadCount = 20;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            LocalDateTime timestamp = LocalDateTime.now();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < eventCount; i++) {
                final int eventIndex = i;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    User user = testUsers.get(eventIndex % testUsers.size());
                    
                    TaskData taskData = TaskData.builder()
                        .taskId((long) eventIndex)
                        .title("High Concurrency Task " + eventIndex)
                        .description("Description for high concurrency task " + eventIndex)
                        .userId(user.getId())
                        .projectId(100L)
                        .status("TODO")
                        .priority("MEDIUM")
                        .createdAt(timestamp)
                        .build();
                    
                    TaskCreatedEvent event = TaskCreatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("task.created")
                        .sourceService("task-service")
                        .timestamp(timestamp)
                        .data(taskData)
                        .build();
                    
                    eventProcessor.processEvent(event);
                }, executor);
                
                futures.add(future);
            }
            
            // Wait for all events to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
            
            // Then
            // Should complete within 20 seconds for 200 high concurrency events
            assertThat(duration).isLessThan(20000);
            
            // Calculate throughput
            double throughput = (double) eventCount / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(10); // At least 10 events per second
            
            // Verify events were processed
            var userStats = userStatisticsRepository.findAll();
            assertThat(userStats).isNotEmpty();
        }
    }
    
    @Nested
    @DisplayName("Event Processing Load Tests")
    class EventProcessingLoadTests {
        
        @Test
        @DisplayName("Should handle sustained load efficiently")
        void shouldHandleSustainedLoadEfficiently() throws Exception {
            // Given
            int batchCount = 10;
            int eventsPerBatch = 50;
            LocalDateTime timestamp = LocalDateTime.now();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int batch = 0; batch < batchCount; batch++) {
                for (int i = 0; i < eventsPerBatch; i++) {
                    User user = testUsers.get(i % testUsers.size());
                    
                    TaskData taskData = TaskData.builder()
                        .taskId((long) (batch * eventsPerBatch + i))
                        .title("Load Test Task " + (batch * eventsPerBatch + i))
                        .description("Description for load test task " + (batch * eventsPerBatch + i))
                        .userId(user.getId())
                        .projectId(100L)
                        .status("TODO")
                        .priority("MEDIUM")
                        .createdAt(timestamp)
                        .build();
                    
                    TaskCreatedEvent event = TaskCreatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("task.created")
                        .sourceService("task-service")
                        .timestamp(timestamp)
                        .data(taskData)
                        .build();
                    
                    eventProcessor.processEvent(event);
                }
                
                // Small delay between batches to simulate sustained load
                Thread.sleep(100);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            int totalEvents = batchCount * eventsPerBatch;
            
            // Should complete within 15 seconds for 500 events
            assertThat(duration).isLessThan(15000);
            
            // Calculate throughput
            double throughput = (double) totalEvents / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(30); // At least 30 events per second
            
            // Verify events were processed
            var userStats = userStatisticsRepository.findAll();
            assertThat(userStats).isNotEmpty();
        }
        
        @Test
        @DisplayName("Should handle burst load efficiently")
        void shouldHandleBurstLoadEfficiently() throws Exception {
            // Given
            int burstCount = 5;
            int eventsPerBurst = 100;
            LocalDateTime timestamp = LocalDateTime.now();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int burst = 0; burst < burstCount; burst++) {
                // Process burst of events
                for (int i = 0; i < eventsPerBurst; i++) {
                    User user = testUsers.get(i % testUsers.size());
                    
                    TaskData taskData = TaskData.builder()
                        .taskId((long) (burst * eventsPerBurst + i))
                        .title("Burst Test Task " + (burst * eventsPerBurst + i))
                        .description("Description for burst test task " + (burst * eventsPerBurst + i))
                        .userId(user.getId())
                        .projectId(100L)
                        .status("TODO")
                        .priority("MEDIUM")
                        .createdAt(timestamp)
                        .build();
                    
                    TaskCreatedEvent event = TaskCreatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("task.created")
                        .sourceService("task-service")
                        .timestamp(timestamp)
                        .data(taskData)
                        .build();
                    
                    eventProcessor.processEvent(event);
                }
                
                // Longer delay between bursts
                Thread.sleep(1000);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            int totalEvents = burstCount * eventsPerBurst;
            
            // Should complete within 20 seconds for 500 events with delays
            assertThat(duration).isLessThan(20000);
            
            // Calculate throughput (excluding delays)
            double processingTime = duration - (burstCount * 1000); // Subtract delay time
            double throughput = (double) totalEvents / (processingTime / 1000.0);
            assertThat(throughput).isGreaterThan(25); // At least 25 events per second
            
            // Verify events were processed
            var userStats = userStatisticsRepository.findAll();
            assertThat(userStats).isNotEmpty();
        }
    }
    
    @Nested
    @DisplayName("Memory Usage Performance Tests")
    class MemoryUsagePerformanceTests {
        
        @Test
        @DisplayName("Should handle memory efficiently during bulk event processing")
        void shouldHandleMemoryEfficientlyDuringBulkEventProcessing() throws Exception {
            // Given
            int eventCount = 200;
            LocalDateTime timestamp = LocalDateTime.now();
            Runtime runtime = Runtime.getRuntime();
            
            // Force garbage collection before test
            System.gc();
            long initialMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < eventCount; i++) {
                User user = testUsers.get(i % testUsers.size());
                
                TaskData taskData = TaskData.builder()
                    .taskId((long) i)
                    .title("Memory Test Task " + i)
                    .description("Description for memory test task " + i)
                    .userId(user.getId())
                    .projectId(100L)
                    .status("TODO")
                    .priority("MEDIUM")
                    .createdAt(timestamp)
                    .build();
                
                TaskCreatedEvent event = TaskCreatedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .eventType("task.created")
                    .sourceService("task-service")
                    .timestamp(timestamp)
                    .data(taskData)
                    .build();
                
                eventProcessor.processEvent(event);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Force garbage collection after test
            System.gc();
            long finalMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = finalMemory - initialMemory;
            
            // Then
            // Should complete within reasonable time
            assertThat(duration).isLessThan(10000);
            
            // Memory usage should be reasonable (less than 50MB for 200 events)
            assertThat(memoryUsed).isLessThan(50 * 1024 * 1024); // 50MB
            
            // Verify events were processed
            var userStats = userStatisticsRepository.findAll();
            assertThat(userStats).isNotEmpty();
        }
        
        @Test
        @DisplayName("Should handle memory efficiently during concurrent event processing")
        void shouldHandleMemoryEfficientlyDuringConcurrentEventProcessing() throws Exception {
            // Given
            int eventCount = 100;
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            LocalDateTime timestamp = LocalDateTime.now();
            Runtime runtime = Runtime.getRuntime();
            
            // Force garbage collection before test
            System.gc();
            long initialMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < eventCount; i++) {
                final int eventIndex = i;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    User user = testUsers.get(eventIndex % testUsers.size());
                    
                    TaskData taskData = TaskData.builder()
                        .taskId((long) eventIndex)
                        .title("Concurrent Memory Task " + eventIndex)
                        .description("Description for concurrent memory task " + eventIndex)
                        .userId(user.getId())
                        .projectId(100L)
                        .status("TODO")
                        .priority("MEDIUM")
                        .createdAt(timestamp)
                        .build();
                    
                    TaskCreatedEvent event = TaskCreatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("task.created")
                        .sourceService("task-service")
                        .timestamp(timestamp)
                        .data(taskData)
                        .build();
                    
                    eventProcessor.processEvent(event);
                }, executor);
                
                futures.add(future);
            }
            
            // Wait for all events to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            
            // Force garbage collection after test
            System.gc();
            long finalMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = finalMemory - initialMemory;
            
            // Then
            // Should complete within reasonable time
            assertThat(duration).isLessThan(10000);
            
            // Memory usage should be reasonable (less than 30MB for 100 concurrent events)
            assertThat(memoryUsed).isLessThan(30 * 1024 * 1024); // 30MB
            
            // Verify events were processed
            var userStats = userStatisticsRepository.findAll();
            assertThat(userStats).isNotEmpty();
        }
    }
}
