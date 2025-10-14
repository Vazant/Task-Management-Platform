package com.taskboard.userservice.infrastructure.repository;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.model.UserStatistics;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.domain.repository.UserStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("UserStatisticsRepository Integration Tests")
class UserStatisticsRepositoryIntegrationTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserStatisticsRepository userStatisticsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    private UserStatistics testStatistics;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .password("hashedPassword")
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .build();
        
        testStatistics = UserStatistics.builder()
            .totalTasks(10)
            .completedTasks(5)
            .todoTasks(2)
            .inProgressTasks(1)
            .todoTasks(0).inProgressTasks(2)
            .totalProjects(3)
            .activeProjects(2)
            .totalProjects(1)
            .lastActivityAt(LocalDateTime.now())
            .build();
    }
    
    @Nested
    @DisplayName("UserStatistics CRUD Operations Tests")
    class UserStatisticsCrudOperationsTests {
        
        @Test
        @DisplayName("Should save user statistics successfully")
        void shouldSaveUserStatisticsSuccessfully() {
            // Given
            User savedUser = userRepository.save(testUser);
            testStatistics = testStatistics.toBuilder().userId(savedUser.getId()).build();
            
            // When
            UserStatistics savedStatistics = userStatisticsRepository.save(testStatistics);
            entityManager.flush();
            entityManager.clear();
            
            // Then
            assertThat(savedStatistics.getId()).isNotNull();
            assertThat(savedStatistics.getUserId()).isEqualTo(savedUser.getId());
            assertThat(savedStatistics.getTotalTasks()).isEqualTo(10);
            assertThat(savedStatistics.getCompletedTasks()).isEqualTo(5);
            assertThat(savedStatistics.getPendingTasks()).isEqualTo(3);
            assertThat(savedStatistics.getOverdueTasks()).isEqualTo(2);
            assertThat(savedStatistics.getTotalProjects()).isEqualTo(3);
            assertThat(savedStatistics.getActiveProjects()).isEqualTo(2);
            assertThat(savedStatistics.getTotalProjects()).isEqualTo(1);
            assertThat(savedStatistics.getCreatedAt()).isNotNull();
            assertThat(savedStatistics.getUpdatedAt()).isNotNull();
        }
        
        @Test
        @DisplayName("Should find user statistics by user ID")
        void shouldFindUserStatisticsByUserId() {
            // Given
            User savedUser = userRepository.save(testUser);
            testStatistics = testStatistics.toBuilder().userId(savedUser.getId()).build();
            userStatisticsRepository.save(testStatistics);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<UserStatistics> foundStatistics = userStatisticsRepository.findByUserId(savedUser.getId());
            
            // Then
            assertThat(foundStatistics).isPresent();
            assertThat(foundStatistics.get().getUserId()).isEqualTo(savedUser.getId());
            assertThat(foundStatistics.get().getTotalTasks()).isEqualTo(10);
        }
        
        @Test
        @DisplayName("Should update user statistics successfully")
        void shouldUpdateUserStatisticsSuccessfully() {
            // Given
            User savedUser = userRepository.save(testUser);
            testStatistics = testStatistics.toBuilder().userId(savedUser.getId()).build();
            UserStatistics savedStatistics = userStatisticsRepository.save(testStatistics);
            entityManager.flush();
            entityManager.clear();
            
            // When
            UserStatistics statisticsToUpdate = savedStatistics.toBuilder()
                .totalTasks(15)
                .completedTasks(8)
                .todoTasks(3)
                .inProgressTasks(2)
                .todoTasks(0).inProgressTasks(2)
                .build();
            
            UserStatistics updatedStatistics = userStatisticsRepository.save(statisticsToUpdate);
            entityManager.flush();
            entityManager.clear();
            
            // Then
            Optional<UserStatistics> foundStatistics = userStatisticsRepository.findByUserId(savedUser.getId());
            assertThat(foundStatistics).isPresent();
            assertThat(foundStatistics.get().getTotalTasks()).isEqualTo(15);
            assertThat(foundStatistics.get().getCompletedTasks()).isEqualTo(8);
            assertThat(foundStatistics.get().getPendingTasks()).isEqualTo(5);
        }
        
        @Test
        @DisplayName("Should delete user statistics successfully")
        void shouldDeleteUserStatisticsSuccessfully() {
            // Given
            User savedUser = userRepository.save(testUser);
            testStatistics = testStatistics.toBuilder().userId(savedUser.getId()).build();
            UserStatistics savedStatistics = userStatisticsRepository.save(testStatistics);
            entityManager.flush();
            entityManager.clear();
            
            // When
            userStatisticsRepository.delete(savedStatistics.getId());
            entityManager.flush();
            entityManager.clear();
            
            // Then
            Optional<UserStatistics> foundStatistics = userStatisticsRepository.findByUserId(savedUser.getId());
            assertThat(foundStatistics).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("UserStatistics Query Methods Tests")
    class UserStatisticsQueryMethodsTests {
        
        @Test
        @DisplayName("Should find users with high task completion rate")
        void shouldFindUsersWithHighTaskCompletionRate() {
            // Given
            User user1 = userRepository.save(testUser.toBuilder().username("user1").email("user1@example.com").build());
            User user2 = userRepository.save(testUser.toBuilder().username("user2").email("user2@example.com").build());
            User user3 = userRepository.save(testUser.toBuilder().username("user3").email("user3@example.com").build());
            
            UserStatistics highCompletionStats = testStatistics.toBuilder()
                .userId(user1.getId())
                .totalTasks(10)
                .completedTasks(9) // 90% completion rate
                .build();
            
            UserStatistics mediumCompletionStats = testStatistics.toBuilder()
                .userId(user2.getId())
                .totalTasks(10)
                .completedTasks(5) // 50% completion rate
                .build();
            
            UserStatistics lowCompletionStats = testStatistics.toBuilder()
                .userId(user3.getId())
                .totalTasks(10)
                .completedTasks(2) // 20% completion rate
                .build();
            
            userStatisticsRepository.save(List.of(highCompletionStats, mediumCompletionStats, lowCompletionStats));
            entityManager.flush();
            entityManager.clear();
            
            // When
            List<UserStatistics> highCompletionUsers = userStatisticsRepository.findByCompletedTasksGreaterThan(0.8);
            
            // Then
            assertThat(highCompletionUsers).hasSize(1);
            assertThat(highCompletionUsers.get(0).getUserId()).isEqualTo(user1.getId());
        }
        
        @Test
        @DisplayName("Should find users with overdue tasks")
        void shouldFindUsersWithOverdueTasks() {
            // Given
            User user1 = userRepository.save(testUser.toBuilder().username("user1").email("user1@example.com").build());
            User user2 = userRepository.save(testUser.toBuilder().username("user2").email("user2@example.com").build());
            
            UserStatistics overdueStats = testStatistics.toBuilder()
                .userId(user1.getId())
                .todoTasks(0).inProgressTasks(3)
                .build();
            
            UserStatistics noOverdueStats = testStatistics.toBuilder()
                .userId(user2.getId())
                .todoTasks(0).inProgressTasks(0)
                .build();
            
            userStatisticsRepository.save(List.of(overdueStats, noOverdueStats));
            entityManager.flush();
            entityManager.clear();
            
            // When
            List<UserStatistics> usersWithOverdue = userStatisticsRepository.findByTodoTasksGreaterThan(0);
            
            // Then
            assertThat(usersWithOverdue).hasSize(1);
            assertThat(usersWithOverdue.get(0).getUserId()).isEqualTo(user1.getId());
            assertThat(usersWithOverdue.get(0).getOverdueTasks()).isEqualTo(3);
        }
        
        @Test
        @DisplayName("Should find users with many active projects")
        void shouldFindUsersWithManyActiveProjects() {
            // Given
            User user1 = userRepository.save(testUser.toBuilder().username("user1").email("user1@example.com").build());
            User user2 = userRepository.save(testUser.toBuilder().username("user2").email("user2@example.com").build());
            
            UserStatistics manyProjectsStats = testStatistics.toBuilder()
                .userId(user1.getId())
                .activeProjects(5)
                .build();
            
            UserStatistics fewProjectsStats = testStatistics.toBuilder()
                .userId(user2.getId())
                .activeProjects(1)
                .build();
            
            userStatisticsRepository.save(List.of(manyProjectsStats, fewProjectsStats));
            entityManager.flush();
            entityManager.clear();
            
            // When
            List<UserStatistics> activeProjectUsers = userStatisticsRepository.findByActiveProjectsGreaterThanEqual(3);
            
            // Then
            assertThat(activeProjectUsers).hasSize(1);
            assertThat(activeProjectUsers.get(0).getUserId()).isEqualTo(user1.getId());
            assertThat(activeProjectUsers.get(0).getActiveProjects()).isEqualTo(5);
        }
        
        @Test
        @DisplayName("Should find users with recent activity")
        void shouldFindUsersWithRecentActivity() {
            // Given
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(1);
            
            User user1 = userRepository.save(testUser.toBuilder().username("user1").email("user1@example.com").build());
            User user2 = userRepository.save(testUser.toBuilder().username("user2").email("user2@example.com").build());
            
            UserStatistics recentActivityStats = testStatistics.toBuilder()
                .userId(user1.getId())
                .lastActivityAt(LocalDateTime.now())
                .build();
            
            UserStatistics oldActivityStats = testStatistics.toBuilder()
                .userId(user2.getId())
                .lastActivityAt(LocalDateTime.now().minusDays(3))
                .build();
            
            userStatisticsRepository.save(List.of(recentActivityStats, oldActivityStats));
            entityManager.flush();
            entityManager.clear();
            
            // When
            List<UserStatistics> recentUsers = userStatisticsRepository.findByLastActivityAtAfter(cutoffDate);
            
            // Then
            assertThat(recentUsers).hasSize(1);
            assertThat(recentUsers.get(0).getUserId()).isEqualTo(user1.getId());
        }
        
        @Test
        @DisplayName("Should find top users by task count")
        void shouldFindTopUsersByTaskCount() {
            // Given
            User user1 = userRepository.save(testUser.toBuilder().username("user1").email("user1@example.com").build());
            User user2 = userRepository.save(testUser.toBuilder().username("user2").email("user2@example.com").build());
            User user3 = userRepository.save(testUser.toBuilder().username("user3").email("user3@example.com").build());
            
            UserStatistics highTaskStats = testStatistics.toBuilder()
                .userId(user1.getId())
                .totalTasks(100)
                .build();
            
            UserStatistics mediumTaskStats = testStatistics.toBuilder()
                .userId(user2.getId())
                .totalTasks(50)
                .build();
            
            UserStatistics lowTaskStats = testStatistics.toBuilder()
                .userId(user3.getId())
                .totalTasks(10)
                .build();
            
            userStatisticsRepository.save(List.of(highTaskStats, mediumTaskStats, lowTaskStats));
            entityManager.flush();
            entityManager.clear();
            
            // When
            List<UserStatistics> topUsers = userStatisticsRepository.findTop10ByOrderByTotalTasksDesc();
            
            // Then
            assertThat(topUsers).hasSize(3);
            assertThat(topUsers.get(0).getTotalTasks()).isEqualTo(100);
            assertThat(topUsers.get(1).getTotalTasks()).isEqualTo(50);
            assertThat(topUsers.get(2).getTotalTasks()).isEqualTo(10);
        }
    }
    
    @Nested
    @DisplayName("UserStatistics Business Logic Tests")
    class UserStatisticsBusinessLogicTests {
        
        @Test
        @DisplayName("Should calculate completion rate correctly")
        void shouldCalculateCompletionRateCorrectly() {
            // Given
            User savedUser = userRepository.save(testUser);
            UserStatistics stats = testStatistics.toBuilder()
                .userId(savedUser.getId())
                .totalTasks(10)
                .completedTasks(7)
                .build();
            
            userStatisticsRepository.save(stats);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<UserStatistics> foundStats = userStatisticsRepository.findByUserId(savedUser.getId());
            
            // Then
            assertThat(foundStats).isPresent();
            assertThat(foundStats.get().getCompletionRate()).isEqualTo(0.7);
        }
        
        @Test
        @DisplayName("Should handle zero total tasks")
        void shouldHandleZeroTotalTasks() {
            // Given
            User savedUser = userRepository.save(testUser);
            UserStatistics stats = testStatistics.toBuilder()
                .userId(savedUser.getId())
                .totalTasks(0)
                .completedTasks(0)
                .build();
            
            userStatisticsRepository.save(stats);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<UserStatistics> foundStats = userStatisticsRepository.findByUserId(savedUser.getId());
            
            // Then
            assertThat(foundStats).isPresent();
            assertThat(foundStats.get().getCompletionRate()).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("Should maintain data consistency")
        void shouldMaintainDataConsistency() {
            // Given
            User savedUser = userRepository.save(testUser);
            UserStatistics stats = testStatistics.toBuilder()
                .userId(savedUser.getId())
                .totalTasks(10)
                .completedTasks(5)
                .todoTasks(2)
            .inProgressTasks(1)
                .todoTasks(0).inProgressTasks(2)
                .build();
            
            userStatisticsRepository.save(stats);
            entityManager.flush();
            entityManager.clear();
            
            // When
            Optional<UserStatistics> foundStats = userStatisticsRepository.findByUserId(savedUser.getId());
            
            // Then
            assertThat(foundStats).isPresent();
            UserStatistics retrievedStats = foundStats.get();
            
            // Verify that completed + pending + overdue = total (allowing for some flexibility)
            int accountedTasks = retrievedStats.getCompletedTasks() + 
                               retrievedStats.getPendingTasks() + 
                               retrievedStats.getOverdueTasks();
            assertThat(accountedTasks).isLessThanOrEqualTo(retrievedStats.getTotalTasks());
        }
    }
    
    @Nested
    @DisplayName("UserStatistics Performance Tests")
    class UserStatisticsPerformanceTests {
        
        @Test
        @DisplayName("Should handle bulk statistics creation efficiently")
        void shouldHandleBulkStatisticsCreationEfficiently() {
            // Given
            List<User> users = List.of();
            List<UserStatistics> statistics = List.of();
            
            for (int i = 0; i < 50; i++) {
                User user = userRepository.save(testUser.toBuilder()
                    .username("user" + i)
                    .email("user" + i + "@example.com")
                    .build());
                users.add(user);
                
                statistics.add(testStatistics.toBuilder()
                    .userId(user.getId())
                    .totalTasks(i + 1)
                    .completedTasks(i)
                    .build());
            }
            
            // When
            long startTime = System.currentTimeMillis();
            userStatisticsRepository.save(statistics);
            entityManager.flush();
            long endTime = System.currentTimeMillis();
            
            // Then
            assertThat(endTime - startTime).isLessThan(2000); // Should complete within 2 seconds
            
            List<UserStatistics> allStats = userStatisticsRepository.findAll();
            assertThat(allStats).hasSize(50);
        }
        
        @Test
        @DisplayName("Should handle complex queries efficiently")
        void shouldHandleComplexQueriesEfficiently() {
            // Given
            List<User> users = List.of();
            List<UserStatistics> statistics = List.of();
            
            for (int i = 0; i < 100; i++) {
                User user = userRepository.save(testUser.toBuilder()
                    .username("user" + i)
                    .email("user" + i + "@example.com")
                    .build());
                users.add(user);
                
                statistics.add(testStatistics.toBuilder()
                    .userId(user.getId())
                    .totalTasks(i + 1)
                    .completedTasks(i)
                    .todoTasks(0).inProgressTasks(i % 3)
                    .activeProjects(i % 5)
                    .build());
            }
            
            userStatisticsRepository.save(statistics);
            entityManager.flush();
            entityManager.clear();
            
            // When
            long startTime = System.currentTimeMillis();
            List<UserStatistics> highCompletionUsers = userStatisticsRepository.findByCompletedTasksGreaterThan(0.8);
            List<UserStatistics> overdueUsers = userStatisticsRepository.findByTodoTasksGreaterThan(0);
            List<UserStatistics> topUsers = userStatisticsRepository.findTop10ByOrderByTotalTasksDesc();
            long endTime = System.currentTimeMillis();
            
            // Then
            assertThat(endTime - startTime).isLessThan(3000); // Should complete within 3 seconds
            assertThat(highCompletionUsers).isNotEmpty();
            assertThat(overdueUsers).isNotEmpty();
            assertThat(topUsers).hasSize(10);
        }
    }
}
