package com.taskboard.userservice.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserStatistics Domain Model Tests")
class UserStatisticsTest {
    
    private UserStatistics.UserStatisticsBuilder statisticsBuilder;
    
    @BeforeEach
    void setUp() {
        statisticsBuilder = UserStatistics.builder()
            .userId(1L)
            .totalTasks(10)
            .completedTasks(5)
            .inProgressTasks(3)
            .todoTasks(2)
            .activeProjects(2)
            .totalProjects(3)
            .lastActivity(LocalDateTime.now().minusHours(1))
            .lastTaskCreated(LocalDateTime.now().minusMinutes(30))
            .lastTaskCompleted(LocalDateTime.now().minusMinutes(15));
    }
    
    @Nested
    @DisplayName("UserStatistics Creation Tests")
    class UserStatisticsCreationTests {
        
        @Test
        @DisplayName("Should create user statistics with valid data")
        void shouldCreateUserStatisticsWithValidData() {
            // When
            UserStatistics statistics = statisticsBuilder.build();
            
            // Then
            assertThat(statistics.getUserId()).isEqualTo(1L);
            assertThat(statistics.getTotalTasks()).isEqualTo(10);
            assertThat(statistics.getCompletedTasks()).isEqualTo(5);
            assertThat(statistics.getInProgressTasks()).isEqualTo(3);
            assertThat(statistics.getTodoTasks()).isEqualTo(2);
            assertThat(statistics.getActiveProjects()).isEqualTo(2);
            assertThat(statistics.getTotalProjects()).isEqualTo(3);
            assertThat(statistics.getLastActivity()).isNotNull();
            assertThat(statistics.getLastTaskCreated()).isNotNull();
            assertThat(statistics.getLastTaskCompleted()).isNotNull();
            assertThat(statistics.getCreatedAt()).isNotNull();
            assertThat(statistics.getUpdatedAt()).isNotNull();
        }
        
        @Test
        @DisplayName("Should create user statistics with minimal data")
        void shouldCreateUserStatisticsWithMinimalData() {
            // When
            UserStatistics statistics = UserStatistics.builder()
                .userId(1L)
                .build();
            
            // Then
            assertThat(statistics.getUserId()).isEqualTo(1L);
            assertThat(statistics.getTotalTasks()).isEqualTo(0);
            assertThat(statistics.getCompletedTasks()).isEqualTo(0);
            assertThat(statistics.getInProgressTasks()).isEqualTo(0);
            assertThat(statistics.getTodoTasks()).isEqualTo(0);
            assertThat(statistics.getActiveProjects()).isEqualTo(0);
            assertThat(statistics.getTotalProjects()).isEqualTo(0);
        }
        
        @Test
        @DisplayName("Should throw exception when userId is null")
        void shouldThrowExceptionWhenUserIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> statisticsBuilder.userId(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID is required");
        }
        
        @Test
        @DisplayName("Should throw exception when task counts are inconsistent")
        void shouldThrowExceptionWhenTaskCountsAreInconsistent() {
            // When & Then
            assertThatThrownBy(() -> statisticsBuilder
                .totalTasks(10)
                .completedTasks(3)
                .inProgressTasks(3)
                .todoTasks(3)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total tasks must equal sum of completed, in-progress, and TODO tasks");
        }
        
        @Test
        @DisplayName("Should throw exception when active projects exceed total projects")
        void shouldThrowExceptionWhenActiveProjectsExceedTotalProjects() {
            // When & Then
            assertThatThrownBy(() -> statisticsBuilder
                .activeProjects(5)
                .totalProjects(3)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Active projects cannot exceed total projects");
        }
    }
    
    @Nested
    @DisplayName("UserStatistics Business Logic Tests")
    class UserStatisticsBusinessLogicTests {
        
        @Test
        @DisplayName("Should calculate correct task completion rate")
        void shouldCalculateCorrectTaskCompletionRate() {
            // Given
            UserStatistics statistics = statisticsBuilder
                .totalTasks(10)
                .completedTasks(7)
                .build();
            
            // When
            double completionRate = statistics.getTaskCompletionRate();
            
            // Then
            assertThat(completionRate).isEqualTo(70.0);
        }
        
        @Test
        @DisplayName("Should return zero completion rate when no tasks")
        void shouldReturnZeroCompletionRateWhenNoTasks() {
            // Given
            UserStatistics statistics = statisticsBuilder
                .totalTasks(0)
                .completedTasks(0)
                .build();
            
            // When
            double completionRate = statistics.getTaskCompletionRate();
            
            // Then
            assertThat(completionRate).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("Should calculate correct project participation rate")
        void shouldCalculateCorrectProjectParticipationRate() {
            // Given
            UserStatistics statistics = statisticsBuilder
                .totalProjects(5)
                .activeProjects(3)
                .build();
            
            // When
            double participationRate = statistics.getProjectParticipationRate();
            
            // Then
            assertThat(participationRate).isEqualTo(60.0);
        }
        
        @Test
        @DisplayName("Should return zero participation rate when no projects")
        void shouldReturnZeroParticipationRateWhenNoProjects() {
            // Given
            UserStatistics statistics = statisticsBuilder
                .totalProjects(0)
                .activeProjects(0)
                .build();
            
            // When
            double participationRate = statistics.getProjectParticipationRate();
            
            // Then
            assertThat(participationRate).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("Should identify active user correctly")
        void shouldIdentifyActiveUserCorrectly() {
            // Given
            UserStatistics activeUser = statisticsBuilder
                .lastActivity(LocalDateTime.now().minusDays(1))
                .build();
            
            UserStatistics inactiveUser = statisticsBuilder
                .lastActivity(LocalDateTime.now().minusDays(10))
                .build();
            
            UserStatistics neverActiveUser = statisticsBuilder
                .lastActivity(null)
                .build();
            
            // When & Then
            assertThat(activeUser.isActive()).isTrue();
            assertThat(inactiveUser.isActive()).isFalse();
            assertThat(neverActiveUser.isActive()).isFalse();
        }
        
        @Test
        @DisplayName("Should calculate days since last activity correctly")
        void shouldCalculateDaysSinceLastActivityCorrectly() {
            // Given
            LocalDateTime lastActivity = LocalDateTime.now().minusDays(5);
            UserStatistics statistics = statisticsBuilder
                .lastActivity(lastActivity)
                .build();
            
            // When
            long daysSinceActivity = statistics.getDaysSinceLastActivity();
            
            // Then
            assertThat(daysSinceActivity).isEqualTo(5);
        }
        
        @Test
        @DisplayName("Should return -1 when never active")
        void shouldReturnMinusOneWhenNeverActive() {
            // Given
            UserStatistics statistics = statisticsBuilder
                .lastActivity(null)
                .build();
            
            // When
            long daysSinceActivity = statistics.getDaysSinceLastActivity();
            
            // Then
            assertThat(daysSinceActivity).isEqualTo(-1);
        }
    }
    
    @Nested
    @DisplayName("UserStatistics Builder Tests")
    class UserStatisticsBuilderTests {
        
        @Test
        @DisplayName("Should create user statistics with all fields using builder")
        void shouldCreateUserStatisticsWithAllFieldsUsingBuilder() {
            // Given
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();
            LocalDateTime lastActivity = LocalDateTime.now().minusHours(2);
            LocalDateTime lastTaskCreated = LocalDateTime.now().minusMinutes(30);
            LocalDateTime lastTaskCompleted = LocalDateTime.now().minusMinutes(15);
            
            // When
            UserStatistics statistics = statisticsBuilder
                .userId(1L)
                .totalTasks(20)
                .completedTasks(10)
                .inProgressTasks(5)
                .todoTasks(5)
                .activeProjects(3)
                .totalProjects(4)
                .lastActivity(lastActivity)
                .lastTaskCreated(lastTaskCreated)
                .lastTaskCompleted(lastTaskCompleted)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
            
            // Then
            assertThat(statistics.getUserId()).isEqualTo(1L);
            assertThat(statistics.getTotalTasks()).isEqualTo(20);
            assertThat(statistics.getCompletedTasks()).isEqualTo(10);
            assertThat(statistics.getInProgressTasks()).isEqualTo(5);
            assertThat(statistics.getTodoTasks()).isEqualTo(5);
            assertThat(statistics.getActiveProjects()).isEqualTo(3);
            assertThat(statistics.getTotalProjects()).isEqualTo(4);
            assertThat(statistics.getLastActivity()).isEqualTo(lastActivity);
            assertThat(statistics.getLastTaskCreated()).isEqualTo(lastTaskCreated);
            assertThat(statistics.getLastTaskCompleted()).isEqualTo(lastTaskCompleted);
            assertThat(statistics.getCreatedAt()).isEqualTo(createdAt);
            assertThat(statistics.getUpdatedAt()).isEqualTo(updatedAt);
        }
        
        @Test
        @DisplayName("Should create copy of user statistics using toBuilder")
        void shouldCreateCopyOfUserStatisticsUsingToBuilder() {
            // Given
            UserStatistics originalStatistics = statisticsBuilder.build();
            
            // When
            UserStatistics copiedStatistics = originalStatistics.toBuilder()
                .totalTasks(50)
                .completedTasks(25)
                .build();
            
            // Then
            assertThat(copiedStatistics.getUserId()).isEqualTo(originalStatistics.getUserId());
            assertThat(copiedStatistics.getTotalTasks()).isEqualTo(50);
            assertThat(copiedStatistics.getCompletedTasks()).isEqualTo(25);
            assertThat(copiedStatistics.getInProgressTasks()).isEqualTo(originalStatistics.getInProgressTasks());
            assertThat(copiedStatistics.getTodoTasks()).isEqualTo(originalStatistics.getTodoTasks());
            assertThat(copiedStatistics.getActiveProjects()).isEqualTo(originalStatistics.getActiveProjects());
            assertThat(copiedStatistics.getTotalProjects()).isEqualTo(originalStatistics.getTotalProjects());
        }
    }
    
    @Nested
    @DisplayName("UserStatistics Edge Cases Tests")
    class UserStatisticsEdgeCasesTests {
        
        @ParameterizedTest
        @CsvSource({
            "0, 0, 0, 0, 0.0",
            "10, 5, 3, 2, 50.0",
            "100, 75, 20, 5, 75.0",
            "1, 1, 0, 0, 100.0"
        })
        @DisplayName("Should handle various task completion scenarios")
        void shouldHandleVariousTaskCompletionScenarios(int totalTasks, int completedTasks, 
                                                       int inProgressTasks, int todoTasks, 
                                                       double expectedCompletionRate) {
            // Given
            UserStatistics statistics = statisticsBuilder
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .todoTasks(todoTasks)
                .build();
            
            // When
            double completionRate = statistics.getTaskCompletionRate();
            
            // Then
            assertThat(completionRate).isEqualTo(expectedCompletionRate);
        }
        
        @ParameterizedTest
        @CsvSource({
            "0, 0, 0.0",
            "5, 3, 60.0",
            "10, 10, 100.0",
            "1, 0, 0.0"
        })
        @DisplayName("Should handle various project participation scenarios")
        void shouldHandleVariousProjectParticipationScenarios(int totalProjects, int activeProjects, 
                                                             double expectedParticipationRate) {
            // Given
            UserStatistics statistics = statisticsBuilder
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .build();
            
            // When
            double participationRate = statistics.getProjectParticipationRate();
            
            // Then
            assertThat(participationRate).isEqualTo(expectedParticipationRate);
        }
        
        @Test
        @DisplayName("Should handle null timestamps gracefully")
        void shouldHandleNullTimestampsGracefully() {
            // Given
            UserStatistics statistics = statisticsBuilder
                .lastActivity(null)
                .lastTaskCreated(null)
                .lastTaskCompleted(null)
                .build();
            
            // When & Then
            assertThat(statistics.getLastActivity()).isNull();
            assertThat(statistics.getLastTaskCreated()).isNull();
            assertThat(statistics.getLastTaskCompleted()).isNull();
            assertThat(statistics.isActive()).isFalse();
            assertThat(statistics.getDaysSinceLastActivity()).isEqualTo(-1);
        }
    }
}
