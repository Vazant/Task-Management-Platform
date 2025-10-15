package com.taskboard.userservice.e2e;

import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.e2e.util.E2ETestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * E2E тесты для UserController.
 * 
 * <p>Тестирует полный цикл управления пользователями:
 * <ul>
 *   <li>Создание пользователей (админом)</li>
 *   <li>Получение списка пользователей</li>
 *   <li>Получение пользователя по ID</li>
 *   <li>Обновление пользователя</li>
 *   <li>Удаление пользователя</li>
 *   <li>Управление профилем</li>
 * </ul>
 * </p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@DisplayName("UserController E2E Tests")
class UserControllerE2ETest extends BaseE2ETest {
    
    private static final String USERS_BASE_URL = "/api/v1/users";
    private static final String AUTH_BASE_URL = "/api/v1/auth";
    
    private String adminToken;
    private String userToken;
    private Long testUserId;
    
    @BeforeEach
    void setUp() throws Exception {
        // Create admin user and get token
        String adminUsername = E2ETestDataFactory.createUniqueUsername("admin");
        String adminEmail = E2ETestDataFactory.createUniqueEmail("admin");
        
        // Register admin
        var adminRegisterRequest = E2ETestDataFactory.createRegisterRequest(adminUsername, adminEmail, E2ETestDataFactory.ADMIN_PASSWORD);
        mockMvc.perform(post(AUTH_BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(adminRegisterRequest)))
                .andExpect(status().isCreated());
        
        // Login as admin
        var adminLoginRequest = E2ETestDataFactory.createLoginRequest(adminUsername, E2ETestDataFactory.ADMIN_PASSWORD);
        ResultActions adminLoginResult = mockMvc.perform(post(AUTH_BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(adminLoginRequest)))
                .andExpect(status().isOk());
        
        adminToken = extractAccessToken(adminLoginResult.andReturn().getResponse().getContentAsString());
        
        // Create regular user and get token
        String userUsername = E2ETestDataFactory.createUniqueUsername("user");
        String userEmail = E2ETestDataFactory.createUniqueEmail("user");
        
        // Register user
        var userRegisterRequest = E2ETestDataFactory.createRegisterRequest(userUsername, userEmail, E2ETestDataFactory.TEST_PASSWORD);
        ResultActions userRegisterResult = mockMvc.perform(post(AUTH_BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userRegisterRequest)))
                .andExpect(status().isCreated());
        
        testUserId = extractUserId(userRegisterResult.andReturn().getResponse().getContentAsString());
        
        // Login as user
        var userLoginRequest = E2ETestDataFactory.createLoginRequest(userUsername, E2ETestDataFactory.TEST_PASSWORD);
        ResultActions userLoginResult = mockMvc.perform(post(AUTH_BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userLoginRequest)))
                .andExpect(status().isOk());
        
        userToken = extractAccessToken(userLoginResult.andReturn().getResponse().getContentAsString());
    }
    
    @Nested
    @DisplayName("User Creation Tests (Admin Only)")
    class UserCreationTests {
        
        @Test
        @DisplayName("Should create user successfully as admin")
        void shouldCreateUserSuccessfullyAsAdmin() throws Exception {
            // Given
            String username = E2ETestDataFactory.createUniqueUsername("newuser");
            String email = E2ETestDataFactory.createUniqueEmail("newuser");
            
            CreateUserRequest request = E2ETestDataFactory.createUserRequest(username, email, UserRole.USER);
            
            // When & Then
            mockMvc.perform(post(USERS_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken)
                    .content(toJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.username").value(username))
                    .andExpect(jsonPath("$.data.email").value(email))
                    .andExpect(jsonPath("$.data.role").value("USER"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.data.id").exists())
                    .andExpect(jsonPath("$.data.createdAt").exists());
        }
        
        @Test
        @DisplayName("Should fail to create user without admin privileges")
        void shouldFailToCreateUserWithoutAdminPrivileges() throws Exception {
            // Given
            String username = E2ETestDataFactory.createUniqueUsername("unauthorized");
            String email = E2ETestDataFactory.createUniqueEmail("unauthorized");
            
            CreateUserRequest request = E2ETestDataFactory.createUserRequest(username, email, UserRole.USER);
            
            // When & Then
            mockMvc.perform(post(USERS_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userToken)
                    .content(toJson(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
        
        @Test
        @DisplayName("Should fail to create user without authentication")
        void shouldFailToCreateUserWithoutAuthentication() throws Exception {
            // Given
            CreateUserRequest request = E2ETestDataFactory.createUserRequest();
            
            // When & Then
            mockMvc.perform(post(USERS_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isUnauthorized());
        }
    }
    
    @Nested
    @DisplayName("User Retrieval Tests")
    class UserRetrievalTests {
        
        @Test
        @DisplayName("Should get all users as admin")
        void shouldGetAllUsersAsAdmin() throws Exception {
            // When & Then
            mockMvc.perform(get(USERS_BASE_URL)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2))); // At least admin and user
        }
        
        @Test
        @DisplayName("Should get user by ID as admin")
        void shouldGetUserByIdAsAdmin() throws Exception {
            // When & Then
            mockMvc.perform(get(USERS_BASE_URL + "/" + testUserId)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(testUserId))
                    .andExpect(jsonPath("$.data.username").exists())
                    .andExpect(jsonPath("$.data.email").exists())
                    .andExpect(jsonPath("$.data.role").exists())
                    .andExpect(jsonPath("$.data.status").exists());
        }
        
        @Test
        @DisplayName("Should get own profile as user")
        void shouldGetOwnProfileAsUser() throws Exception {
            // When & Then
            mockMvc.perform(get(USERS_BASE_URL + "/me")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").exists())
                    .andExpect(jsonPath("$.data.username").exists())
                    .andExpect(jsonPath("$.data.email").exists())
                    .andExpect(jsonPath("$.data.role").exists())
                    .andExpect(jsonPath("$.data.status").exists());
        }
        
        @Test
        @DisplayName("Should fail to get user by ID without authentication")
        void shouldFailToGetUserByIdWithoutAuthentication() throws Exception {
            // When & Then
            mockMvc.perform(get(USERS_BASE_URL + "/" + testUserId))
                    .andExpect(status().isUnauthorized());
        }
    }
    
    @Nested
    @DisplayName("User Update Tests")
    class UserUpdateTests {
        
        @Test
        @DisplayName("Should update user as admin")
        void shouldUpdateUserAsAdmin() throws Exception {
            // Given
            UpdateUserRequest request = E2ETestDataFactory.createUpdateUserRequest(
                "UpdatedFirstName", "UpdatedLastName", "updated@example.com"
            );
            
            // When & Then
            mockMvc.perform(put(USERS_BASE_URL + "/" + testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken)
                    .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.firstName").value("UpdatedFirstName"))
                    .andExpect(jsonPath("$.data.lastName").value("UpdatedLastName"))
                    .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                    .andExpect(jsonPath("$.data.updatedAt").exists());
        }
        
        @Test
        @DisplayName("Should update own profile as user")
        void shouldUpdateOwnProfileAsUser() throws Exception {
            // Given
            UpdateUserRequest request = E2ETestDataFactory.createUpdateUserRequest(
                "MyUpdatedFirstName", "MyUpdatedLastName", "myupdated@example.com"
            );
            
            // When & Then
            mockMvc.perform(put(USERS_BASE_URL + "/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userToken)
                    .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.firstName").value("MyUpdatedFirstName"))
                    .andExpect(jsonPath("$.data.lastName").value("MyUpdatedLastName"))
                    .andExpect(jsonPath("$.data.email").value("myupdated@example.com"))
                    .andExpect(jsonPath("$.data.updatedAt").exists());
        }
        
        @Test
        @DisplayName("Should fail to update other user as regular user")
        void shouldFailToUpdateOtherUserAsRegularUser() throws Exception {
            // Given
            UpdateUserRequest request = E2ETestDataFactory.createUpdateUserRequest();
            
            // When & Then
            mockMvc.perform(put(USERS_BASE_URL + "/" + testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userToken)
                    .content(toJson(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
    }
    
    @Nested
    @DisplayName("User Deletion Tests")
    class UserDeletionTests {
        
        @Test
        @DisplayName("Should delete user as admin")
        void shouldDeleteUserAsAdmin() throws Exception {
            // Given - create a user to delete
            String username = E2ETestDataFactory.createUniqueUsername("todelete");
            String email = E2ETestDataFactory.createUniqueEmail("todelete");
            
            CreateUserRequest createRequest = E2ETestDataFactory.createUserRequest(username, email, UserRole.USER);
            ResultActions createResult = mockMvc.perform(post(USERS_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken)
                    .content(toJson(createRequest)))
                    .andExpect(status().isCreated());
            
            Long userIdToDelete = extractUserId(createResult.andReturn().getResponse().getContentAsString());
            
            // When & Then
            mockMvc.perform(delete(USERS_BASE_URL + "/" + userIdToDelete)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User deleted successfully"));
        }
        
        @Test
        @DisplayName("Should fail to delete user without admin privileges")
        void shouldFailToDeleteUserWithoutAdminPrivileges() throws Exception {
            // When & Then
            mockMvc.perform(delete(USERS_BASE_URL + "/" + testUserId)
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
    }
    
    @Nested
    @DisplayName("User Statistics Tests")
    class UserStatisticsTests {
        
        @Test
        @DisplayName("Should get user statistics as admin")
        void shouldGetUserStatisticsAsAdmin() throws Exception {
            // When & Then
            mockMvc.perform(get(USERS_BASE_URL + "/statistics")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.totalUsers").exists())
                    .andExpect(jsonPath("$.data.activeUsers").exists())
                    .andExpect(jsonPath("$.data.inactiveUsers").exists())
                    .andExpect(jsonPath("$.data.adminUsers").exists())
                    .andExpect(jsonPath("$.data.regularUsers").exists());
        }
        
        @Test
        @DisplayName("Should fail to get user statistics without admin privileges")
        void shouldFailToGetUserStatisticsWithoutAdminPrivileges() throws Exception {
            // When & Then
            mockMvc.perform(get(USERS_BASE_URL + "/statistics")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
    }
    
    /**
     * Извлекает access token из JSON ответа.
     * 
     * @param jsonResponse JSON ответ
     * @return access token
     */
    private String extractAccessToken(String jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse)
                    .path("data")
                    .path("accessToken")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract access token", e);
        }
    }
    
    /**
     * Извлекает user ID из JSON ответа.
     * 
     * @param jsonResponse JSON ответ
     * @return user ID
     */
    private Long extractUserId(String jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse)
                    .path("data")
                    .path("id")
                    .asLong();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract user ID", e);
        }
    }
}
