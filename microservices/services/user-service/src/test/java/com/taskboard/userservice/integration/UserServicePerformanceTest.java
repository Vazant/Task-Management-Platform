package com.taskboard.userservice.integration;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.usecase.CreateUserUseCase;
import com.taskboard.userservice.application.usecase.GetUserUseCase;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты производительности для User Service.
 * Тестирует производительность операций с реальной базой данных.
 */
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServicePerformanceTest extends BaseIntegrationTest {

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private GetUserUseCase getUserUseCase;

    @Test
    @DisplayName("Should handle bulk user creation efficiently")
    void shouldHandleBulkUserCreationEfficiently() {
        // Given
        List<CreateUserRequest> requests = IntStream.range(0, 100)
                .mapToObj(i -> CreateUserRequest.builder()
                        .username("perfuser" + i)
                        .email("perfuser" + i + "@example.com")
                        .password("password123")
                        .firstName("Performance")
                        .lastName("User" + i)
                        .role(UserRole.USER)
                        .build())
                .toList();

        // When
        Instant startTime = Instant.now();
        List<Long> createdUserIds = new ArrayList<>();
        
        for (CreateUserRequest request : requests) {
            var response = createUserUseCase.execute(request);
            createdUserIds.add(response.getUserId());
        }
        
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);

        // Then
        assertThat(createdUserIds).hasSize(100);
        assertThat(duration).isLessThan(Duration.ofSeconds(10)); // Should complete within 10 seconds
        
        // Verify all users were created successfully
        for (Long userId : createdUserIds) {
            var user = getUserUseCase.execute(com.taskboard.userservice.application.dto.GetUserRequest.builder()
                    .userId(userId)
                    .build());
            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isEqualTo(userId);
        }
    }

    @Test
    @DisplayName("Should handle concurrent user creation efficiently")
    void shouldHandleConcurrentUserCreationEfficiently() throws Exception {
        // Given
        int numberOfUsers = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        List<CompletableFuture<Long>> futures = IntStream.range(0, numberOfUsers)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    CreateUserRequest request = CreateUserRequest.builder()
                            .username("concurrentuser" + i)
                            .email("concurrentuser" + i + "@example.com")
                            .password("password123")
                            .firstName("Concurrent")
                            .lastName("User" + i)
                            .role(UserRole.USER)
                            .build();
                    
                    var response = createUserUseCase.execute(request);
                    return response.getUserId();
                }, executor))
                .toList();

        // When
        Instant startTime = Instant.now();
        List<Long> createdUserIds = futures.stream()
                .map(CompletableFuture::join)
                .toList();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);

        // Then
        assertThat(createdUserIds).hasSize(numberOfUsers);
        assertThat(duration).isLessThan(Duration.ofSeconds(15)); // Should complete within 15 seconds
        
        // Verify all users were created successfully
        for (Long userId : createdUserIds) {
            var user = getUserUseCase.execute(com.taskboard.userservice.application.dto.GetUserRequest.builder()
                    .userId(userId)
                    .build());
            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isEqualTo(userId);
        }
        
        executor.shutdown();
    }

    @Test
    @DisplayName("Should handle rapid sequential user retrieval efficiently")
    void shouldHandleRapidSequentialUserRetrievalEfficiently() {
        // Given
        Long userId = 1L; // Existing user from test data
        int numberOfRetrievals = 1000;

        // When
        Instant startTime = Instant.now();
        
        for (int i = 0; i < numberOfRetrievals; i++) {
            var user = getUserUseCase.execute(com.taskboard.userservice.application.dto.GetUserRequest.builder()
                    .userId(userId)
                    .build());
            assertThat(user).isNotNull();
        }
        
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);

        // Then
        assertThat(duration).isLessThan(Duration.ofSeconds(5)); // Should complete within 5 seconds
        
        // Calculate average response time
        double averageResponseTimeMs = duration.toMillis() / (double) numberOfRetrievals;
        assertThat(averageResponseTimeMs).isLessThan(10.0); // Average response time should be less than 10ms
    }

    @Test
    @DisplayName("Should handle concurrent user retrieval efficiently")
    void shouldHandleConcurrentUserRetrievalEfficiently() throws Exception {
        // Given
        Long userId = 1L; // Existing user from test data
        int numberOfRetrievals = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        
        List<CompletableFuture<Void>> futures = IntStream.range(0, numberOfRetrievals)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    var user = getUserUseCase.execute(com.taskboard.userservice.application.dto.GetUserRequest.builder()
                            .userId(userId)
                            .build());
                    assertThat(user).isNotNull();
                    assertThat(user.getUserId()).isEqualTo(userId);
                }, executor))
                .toList();

        // When
        Instant startTime = Instant.now();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);

        // Then
        assertThat(duration).isLessThan(Duration.ofSeconds(10)); // Should complete within 10 seconds
        
        // Calculate average response time
        double averageResponseTimeMs = duration.toMillis() / (double) numberOfRetrievals;
        assertThat(averageResponseTimeMs).isLessThan(100.0); // Average response time should be less than 100ms
        
        executor.shutdown();
    }

    @Test
    @DisplayName("Should handle mixed operations efficiently")
    void shouldHandleMixedOperationsEfficiently() throws Exception {
        // Given
        int numberOfOperations = 200;
        ExecutorService executor = Executors.newFixedThreadPool(15);
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        // Mix of create and read operations
        for (int i = 0; i < numberOfOperations; i++) {
            if (i % 3 == 0) {
                // Create operation
                int userIndex = i;
                futures.add(CompletableFuture.runAsync(() -> {
                    CreateUserRequest request = CreateUserRequest.builder()
                            .username("mixeduser" + userIndex)
                            .email("mixeduser" + userIndex + "@example.com")
                            .password("password123")
                            .firstName("Mixed")
                            .lastName("User" + userIndex)
                            .role(UserRole.USER)
                            .build();
                    
                    var response = createUserUseCase.execute(request);
                    assertThat(response).isNotNull();
                    assertThat(response.getUserId()).isNotNull();
                }, executor));
            } else {
                // Read operation
                Long userId = (long) (i % 3 + 1); // Cycle through existing users
                futures.add(CompletableFuture.runAsync(() -> {
                    var user = getUserUseCase.execute(com.taskboard.userservice.application.dto.GetUserRequest.builder()
                            .userId(userId)
                            .build());
                    assertThat(user).isNotNull();
                }, executor));
            }
        }

        // When
        Instant startTime = Instant.now();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);

        // Then
        assertThat(duration).isLessThan(Duration.ofSeconds(20)); // Should complete within 20 seconds
        
        // Calculate average response time
        double averageResponseTimeMs = duration.toMillis() / (double) numberOfOperations;
        assertThat(averageResponseTimeMs).isLessThan(100.0); // Average response time should be less than 100ms
        
        executor.shutdown();
    }

    @Test
    @DisplayName("Should maintain performance under load")
    void shouldMaintainPerformanceUnderLoad() throws Exception {
        // Given
        int numberOfUsers = 200;
        ExecutorService executor = Executors.newFixedThreadPool(25);
        
        // Create users first
        List<CompletableFuture<Long>> createFutures = IntStream.range(0, numberOfUsers)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    CreateUserRequest request = CreateUserRequest.builder()
                            .username("loaduser" + i)
                            .email("loaduser" + i + "@example.com")
                            .password("password123")
                            .firstName("Load")
                            .lastName("User" + i)
                            .role(UserRole.USER)
                            .build();
                    
                    var response = createUserUseCase.execute(request);
                    return response.getUserId();
                }, executor))
                .toList();
        
        List<Long> userIds = createFutures.stream()
                .map(CompletableFuture::join)
                .toList();

        // When - Perform read operations under load
        Instant startTime = Instant.now();
        
        List<CompletableFuture<Void>> readFutures = userIds.stream()
                .map(userId -> CompletableFuture.runAsync(() -> {
                    var user = getUserUseCase.execute(com.taskboard.userservice.application.dto.GetUserRequest.builder()
                            .userId(userId)
                            .build());
                    assertThat(user).isNotNull();
                    assertThat(user.getUserId()).isEqualTo(userId);
                }, executor))
                .toList();
        
        CompletableFuture.allOf(readFutures.toArray(new CompletableFuture[0])).join();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);

        // Then
        assertThat(duration).isLessThan(Duration.ofSeconds(15)); // Should complete within 15 seconds
        
        // Calculate throughput
        double throughput = numberOfUsers / (duration.toMillis() / 1000.0);
        assertThat(throughput).isGreaterThan(10.0); // Should handle at least 10 operations per second
        
        executor.shutdown();
    }
}
