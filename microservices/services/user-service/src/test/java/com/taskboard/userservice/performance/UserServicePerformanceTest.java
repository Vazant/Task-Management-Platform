package com.taskboard.userservice.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.LoginRequest;
import com.taskboard.userservice.application.dto.RegisterRequest;
import com.taskboard.userservice.domain.model.UserRole;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("User Service Performance Tests")
class UserServicePerformanceTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private String authToken;
    
    @BeforeEach
    void setUp() throws Exception {
        // Register and login as admin user for performance tests
        authToken = registerAndLoginAdmin("perfadmin", "perfadmin@example.com", "password123");
    }
    
    @Nested
    @DisplayName("User Registration Performance Tests")
    class UserRegistrationPerformanceTests {
        
        @Test
        @DisplayName("Should handle bulk user registration efficiently")
        void shouldHandleBulkUserRegistrationEfficiently() throws Exception {
            // Given
            int userCount = 100;
            List<RegisterRequest> requests = new ArrayList<>();
            
            for (int i = 0; i < userCount; i++) {
                requests.add(RegisterRequest.builder()
                    .username("perfuser" + i)
                    .email("perfuser" + i + "@example.com")
                    .password("password123")
                    .firstName("Performance")
                    .lastName("User" + i)
                    .build());
            }
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (RegisterRequest request : requests) {
                mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            // Should complete within 10 seconds for 100 users
            assertThat(duration).isLessThan(10000);
            
            // Calculate throughput
            double throughput = (double) userCount / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(10); // At least 10 users per second
        }
        
        @Test
        @DisplayName("Should handle concurrent user registration")
        void shouldHandleConcurrentUserRegistration() throws Exception {
            // Given
            int userCount = 50;
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < userCount; i++) {
                final int userId = i;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        RegisterRequest request = RegisterRequest.builder()
                            .username("concurrentuser" + userId)
                            .email("concurrentuser" + userId + "@example.com")
                            .password("password123")
                            .firstName("Concurrent")
                            .lastName("User" + userId)
                            .build();
                        
                        mockMvc.perform(post("/api/v1/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isCreated());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);
                
                futures.add(future);
            }
            
            // Wait for all registrations to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            
            // Then
            // Should complete within 15 seconds for 50 concurrent users
            assertThat(duration).isLessThan(15000);
            
            // Calculate throughput
            double throughput = (double) userCount / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(3); // At least 3 users per second
        }
    }
    
    @Nested
    @DisplayName("User Authentication Performance Tests")
    class UserAuthenticationPerformanceTests {
        
        @Test
        @DisplayName("Should handle bulk user login efficiently")
        void shouldHandleBulkUserLoginEfficiently() throws Exception {
            // Given
            int userCount = 50;
            List<String> usernames = new ArrayList<>();
            
            // Register users first
            for (int i = 0; i < userCount; i++) {
                String username = "logintest" + i;
                usernames.add(username);
                
                RegisterRequest registerRequest = RegisterRequest.builder()
                    .username(username)
                    .email(username + "@example.com")
                    .password("password123")
                    .firstName("Login")
                    .lastName("Test" + i)
                    .build();
                
                mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());
            }
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (String username : usernames) {
                LoginRequest loginRequest = LoginRequest.builder()
                    .username(username)
                    .password("password123")
                    .build();
                
                mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk());
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            // Should complete within 5 seconds for 50 logins
            assertThat(duration).isLessThan(5000);
            
            // Calculate throughput
            double throughput = (double) userCount / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(10); // At least 10 logins per second
        }
        
        @Test
        @DisplayName("Should handle concurrent user login")
        void shouldHandleConcurrentUserLogin() throws Exception {
            // Given
            int userCount = 30;
            int threadCount = 5;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            List<String> usernames = new ArrayList<>();
            
            // Register users first
            for (int i = 0; i < userCount; i++) {
                String username = "concurrentlogin" + i;
                usernames.add(username);
                
                RegisterRequest registerRequest = RegisterRequest.builder()
                    .username(username)
                    .email(username + "@example.com")
                    .password("password123")
                    .firstName("Concurrent")
                    .lastName("Login" + i)
                    .build();
                
                mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());
            }
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (String username : usernames) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        LoginRequest loginRequest = LoginRequest.builder()
                            .username(username)
                            .password("password123")
                            .build();
                        
                        mockMvc.perform(post("/api/v1/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                            .andExpect(status().isOk());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);
                
                futures.add(future);
            }
            
            // Wait for all logins to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            
            // Then
            // Should complete within 10 seconds for 30 concurrent logins
            assertThat(duration).isLessThan(10000);
            
            // Calculate throughput
            double throughput = (double) userCount / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(3); // At least 3 logins per second
        }
    }
    
    @Nested
    @DisplayName("User Management Performance Tests")
    class UserManagementPerformanceTests {
        
        @Test
        @DisplayName("Should handle bulk user creation efficiently")
        void shouldHandleBulkUserCreationEfficiently() throws Exception {
            // Given
            int userCount = 100;
            List<CreateUserRequest> requests = new ArrayList<>();
            
            for (int i = 0; i < userCount; i++) {
                requests.add(CreateUserRequest.builder()
                    .username("bulkuser" + i)
                    .email("bulkuser" + i + "@example.com")
                    .password("password123")
                    .firstName("Bulk")
                    .lastName("User" + i)
                    .role(UserRole.USER)
                    .build());
            }
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (CreateUserRequest request : requests) {
                mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            // Should complete within 15 seconds for 100 users
            assertThat(duration).isLessThan(15000);
            
            // Calculate throughput
            double throughput = (double) userCount / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(6); // At least 6 users per second
        }
        
        @Test
        @DisplayName("Should handle bulk user retrieval efficiently")
        void shouldHandleBulkUserRetrievalEfficiently() throws Exception {
            // Given
            int userCount = 50;
            
            // Create users first
            for (int i = 0; i < userCount; i++) {
                CreateUserRequest request = CreateUserRequest.builder()
                    .username("retrievaluser" + i)
                    .email("retrievaluser" + i + "@example.com")
                    .password("password123")
                    .firstName("Retrieval")
                    .lastName("User" + i)
                    .role(UserRole.USER)
                    .build();
                
                mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
            }
            
            // When
            long startTime = System.currentTimeMillis();
            
            // Retrieve all users multiple times
            for (int i = 0; i < 10; i++) {
                mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            // Should complete within 5 seconds for 10 retrievals
            assertThat(duration).isLessThan(5000);
            
            // Calculate throughput
            double throughput = 10.0 / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(2); // At least 2 retrievals per second
        }
    }
    
    @Nested
    @DisplayName("Database Performance Tests")
    class DatabasePerformanceTests {
        
        @Test
        @DisplayName("Should handle large dataset queries efficiently")
        void shouldHandleLargeDatasetQueriesEfficiently() throws Exception {
            // Given
            int userCount = 200;
            
            // Create large dataset
            for (int i = 0; i < userCount; i++) {
                CreateUserRequest request = CreateUserRequest.builder()
                    .username("largeuser" + i)
                    .email("largeuser" + i + "@example.com")
                    .password("password123")
                    .firstName("Large")
                    .lastName("User" + i)
                    .role(UserRole.USER)
                    .build();
                
                mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
            }
            
            // When
            long startTime = System.currentTimeMillis();
            
            // Perform multiple queries
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            // Should complete within 10 seconds for 5 queries on 200 users
            assertThat(duration).isLessThan(10000);
            
            // Calculate throughput
            double throughput = 5.0 / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(0.5); // At least 0.5 queries per second
        }
        
        @Test
        @DisplayName("Should handle complex queries efficiently")
        void shouldHandleComplexQueriesEfficiently() throws Exception {
            // Given
            int userCount = 100;
            
            // Create users with different roles
            for (int i = 0; i < userCount; i++) {
                UserRole role = (i % 2 == 0) ? UserRole.USER : UserRole.ADMIN;
                
                CreateUserRequest request = CreateUserRequest.builder()
                    .username("complexuser" + i)
                    .email("complexuser" + i + "@example.com")
                    .password("password123")
                    .firstName("Complex")
                    .lastName("User" + i)
                    .role(role)
                    .build();
                
                mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
            }
            
            // When
            long startTime = System.currentTimeMillis();
            
            // Perform complex queries (simulated by multiple simple queries)
            for (int i = 0; i < 20; i++) {
                mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Then
            // Should complete within 15 seconds for 20 queries on 100 users
            assertThat(duration).isLessThan(15000);
            
            // Calculate throughput
            double throughput = 20.0 / (duration / 1000.0);
            assertThat(throughput).isGreaterThan(1); // At least 1 query per second
        }
    }
    
    @Nested
    @DisplayName("Memory Usage Performance Tests")
    class MemoryUsagePerformanceTests {
        
        @Test
        @DisplayName("Should handle memory efficiently during bulk operations")
        void shouldHandleMemoryEfficientlyDuringBulkOperations() throws Exception {
            // Given
            int userCount = 100;
            Runtime runtime = Runtime.getRuntime();
            
            // Force garbage collection before test
            System.gc();
            long initialMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // When
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < userCount; i++) {
                CreateUserRequest request = CreateUserRequest.builder()
                    .username("memoryuser" + i)
                    .email("memoryuser" + i + "@example.com")
                    .password("password123")
                    .firstName("Memory")
                    .lastName("User" + i)
                    .role(UserRole.USER)
                    .build();
                
                mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Force garbage collection after test
            System.gc();
            long finalMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = finalMemory - initialMemory;
            
            // Then
            // Should complete within reasonable time
            assertThat(duration).isLessThan(15000);
            
            // Memory usage should be reasonable (less than 100MB for 100 users)
            assertThat(memoryUsed).isLessThan(100 * 1024 * 1024); // 100MB
        }
    }
    
    // Helper methods
    private String registerAndLoginAdmin(String username, String email, String password) throws Exception {
        // Register as admin
        CreateUserRequest createRequest = CreateUserRequest.builder()
            .username(username)
            .email(email)
            .password(password)
            .firstName("Admin")
            .lastName("User")
            .role(UserRole.ADMIN)
            .build();
        
        mockMvc.perform(post("/api/v1/users")
                .with(csrf())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated());
        
        // Login
        LoginRequest loginRequest = LoginRequest.builder()
            .username(username)
            .password(password)
            .build();
        
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();
        
        return extractTokenFromResponse(result);
    }
    
    private String extractTokenFromResponse(MvcResult result) throws Exception {
        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseContent).get("token").asText();
    }
}
