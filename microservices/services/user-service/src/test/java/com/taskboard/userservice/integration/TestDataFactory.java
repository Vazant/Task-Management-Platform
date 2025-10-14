package com.taskboard.userservice.integration;

import com.taskboard.userservice.application.dto.*;
import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Фабрика для создания тестовых данных в интеграционных тестах.
 */
public class TestDataFactory {
    
    private static final String DEFAULT_PASSWORD = "password123";
    private static final String DEFAULT_PASSWORD_HASH = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi";
    private static final String EMAIL_DOMAIN = "@example.com";
    
    private TestDataFactory() {
        // Utility class
    }

    /**
     * Создает CreateUserRequest с тестовыми данными.
     */
    public static CreateUserRequest createUserRequest() {
        return createUserRequest("testuser", "test@example.com");
    }

    /**
     * Создает CreateUserRequest с указанными username и email.
     */
    public static CreateUserRequest createUserRequest(String username, String email) {
        return CreateUserRequest.builder()
                .username(username)
                .email(email)
                .password(DEFAULT_PASSWORD)
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .build();
    }

    /**
     * Создает CreateUserRequest для администратора.
     */
    public static CreateUserRequest createAdminUserRequest() {
        return CreateUserRequest.builder()
                .username("adminuser")
                .email("admin@example.com")
                .password(DEFAULT_PASSWORD)
                .firstName("Admin")
                .lastName("User")
                .role(UserRole.ADMIN)
                .build();
    }

    /**
     * Создает GetUserRequest с указанным ID.
     */
    public static GetUserRequest getUserRequest(Long userId) {
        return GetUserRequest.builder()
                .userId(userId)
                .build();
    }

    /**
     * Создает UpdateUserRequest с указанным ID.
     */
    public static UpdateUserRequest updateUserRequest(Long userId) {
        return UpdateUserRequest.builder()
                .userId(userId)
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .build();
    }

    /**
     * Создает UpdateUserRequest с частичными данными.
     */
    public static UpdateUserRequest partialUpdateUserRequest(Long userId) {
        return UpdateUserRequest.builder()
                .userId(userId)
                .firstName("Partial")
                .build();
    }

    /**
     * Создает DeleteUserRequest с указанным ID.
     */
    public static DeleteUserRequest deleteUserRequest(Long userId) {
        return DeleteUserRequest.builder()
                .userId(userId)
                .build();
    }

    /**
     * Создает AuthenticateUserRequest с указанными credentials.
     */
    public static AuthenticateUserRequest authenticateUserRequest(String username, String password) {
        return AuthenticateUserRequest.builder()
                .username(username)
                .password(password)
                .build();
    }

    /**
     * Создает AuthenticateUserRequest с тестовыми credentials.
     */
    public static AuthenticateUserRequest authenticateUserRequest() {
        return authenticateUserRequest("testuser1", "password123");
    }

    /**
     * Создает User доменную модель с тестовыми данными.
     */
    public static User createUser() {
        return createUser(1L, "testuser", "test@example.com");
    }

    /**
     * Создает User доменную модель с указанными параметрами.
     */
    public static User createUser(Long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(DEFAULT_PASSWORD_HASH)
                .firstName("Test")
                .lastName("User")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Создает User доменную модель для администратора.
     */
    public static User createAdminUser() {
        return User.builder()
                .id(2L)
                .username("adminuser")
                .email("admin@example.com")
                .password(DEFAULT_PASSWORD_HASH)
                .firstName("Admin")
                .lastName("User")
                .status(UserStatus.ACTIVE)
                .role(UserRole.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Создает User доменную модель с неактивным статусом.
     */
    public static User createInactiveUser() {
        return User.builder()
                .id(3L)
                .username("inactiveuser")
                .email("inactive@example.com")
                .password(DEFAULT_PASSWORD_HASH)
                .firstName("Inactive")
                .lastName("User")
                .status(UserStatus.INACTIVE)
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Создает список CreateUserRequest для bulk операций.
     */
    public static List<CreateUserRequest> createBulkUserRequests(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createUserRequest("bulkuser" + i, "bulkuser" + i + EMAIL_DOMAIN))
                .toList();
    }

    /**
     * Создает список CreateUserRequest для performance тестов.
     */
    public static List<CreateUserRequest> createPerformanceUserRequests(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createUserRequest("perfuser" + i, "perfuser" + i + EMAIL_DOMAIN))
                .toList();
    }

    /**
     * Создает список CreateUserRequest для concurrent тестов.
     */
    public static List<CreateUserRequest> createConcurrentUserRequests(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createUserRequest("concurrentuser" + i, "concurrentuser" + i + EMAIL_DOMAIN))
                .toList();
    }

    /**
     * Создает CreateUserRequest с невалидными данными.
     */
    public static CreateUserRequest createInvalidUserRequest() {
        return CreateUserRequest.builder()
                .username("") // Invalid: empty username
                .email("invalid-email") // Invalid: malformed email
                .password("123") // Invalid: too short password
                .firstName("")
                .lastName("")
                .build();
    }

    /**
     * Создает CreateUserRequest с дублирующимся username.
     */
    public static CreateUserRequest createDuplicateUsernameRequest() {
        return CreateUserRequest.builder()
                .username("testuser1") // Already exists in test data
                .email("newemail@example.com")
                .password(DEFAULT_PASSWORD)
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
                .build();
    }

    /**
     * Создает CreateUserRequest с дублирующимся email.
     */
    public static CreateUserRequest createDuplicateEmailRequest() {
        return CreateUserRequest.builder()
                .username("newusername")
                .email("test1@example.com") // Already exists in test data
                .password(DEFAULT_PASSWORD)
                .firstName("New")
                .lastName("User")
                .role(UserRole.USER)
                .build();
    }

    /**
     * Создает AuthenticateUserRequest с неверными credentials.
     */
    public static AuthenticateUserRequest createInvalidAuthRequest() {
        return AuthenticateUserRequest.builder()
                .username("nonexistent")
                .password("wrongpassword")
                .build();
    }

    /**
     * Создает AuthenticateUserRequest с пустыми полями.
     */
    public static AuthenticateUserRequest createEmptyAuthRequest() {
        return AuthenticateUserRequest.builder()
                .username("")
                .password("")
                .build();
    }
}
