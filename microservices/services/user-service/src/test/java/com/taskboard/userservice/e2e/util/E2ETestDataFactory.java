package com.taskboard.userservice.e2e.util;

import com.taskboard.userservice.application.dto.LoginRequest;
import com.taskboard.userservice.application.dto.RegisterRequest;
import com.taskboard.userservice.application.dto.CreateUserRequest;
import com.taskboard.userservice.application.dto.UpdateUserRequest;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;

import java.time.LocalDateTime;

/**
 * Фабрика для создания тестовых данных в E2E тестах.
 * 
 * <p>Предоставляет методы для создания различных DTO и объектов,
 * используемых в E2E тестах.</p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
public class E2ETestDataFactory {
    
    // Константы для тестовых данных
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "TestPassword123!";
    public static final String TEST_FIRST_NAME = "Test";
    public static final String TEST_LAST_NAME = "User";
    
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final String ADMIN_PASSWORD = "AdminPassword123!";
    
    /**
     * Создает запрос на регистрацию пользователя.
     * 
     * @return RegisterRequest с тестовыми данными
     */
    public static RegisterRequest createRegisterRequest() {
        return RegisterRequest.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .build();
    }
    
    /**
     * Создает запрос на регистрацию пользователя с кастомными данными.
     * 
     * @param username имя пользователя
     * @param email email
     * @param password пароль
     * @return RegisterRequest с указанными данными
     */
    public static RegisterRequest createRegisterRequest(String username, String email, String password) {
        return RegisterRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .build();
    }
    
    /**
     * Создает запрос на вход пользователя.
     * 
     * @return LoginRequest с тестовыми данными
     */
    public static LoginRequest createLoginRequest() {
        return LoginRequest.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build();
    }
    
    /**
     * Создает запрос на вход пользователя с кастомными данными.
     * 
     * @param username имя пользователя
     * @param password пароль
     * @return LoginRequest с указанными данными
     */
    public static LoginRequest createLoginRequest(String username, String password) {
        return LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }
    
    /**
     * Создает запрос на создание пользователя (для админа).
     * 
     * @return CreateUserRequest с тестовыми данными
     */
    public static CreateUserRequest createUserRequest() {
        return CreateUserRequest.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .role(UserRole.USER)
                .build();
    }
    
    /**
     * Создает запрос на создание пользователя с кастомными данными.
     * 
     * @param username имя пользователя
     * @param email email
     * @param role роль пользователя
     * @return CreateUserRequest с указанными данными
     */
    public static CreateUserRequest createUserRequest(String username, String email, UserRole role) {
        return CreateUserRequest.builder()
                .username(username)
                .email(email)
                .password(TEST_PASSWORD)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .role(role)
                .build();
    }
    
    /**
     * Создает запрос на обновление пользователя.
     * 
     * @return UpdateUserRequest с тестовыми данными
     */
    public static UpdateUserRequest createUpdateUserRequest() {
        return UpdateUserRequest.builder()
                .firstName("Updated" + TEST_FIRST_NAME)
                .lastName("Updated" + TEST_LAST_NAME)
                .email("updated" + TEST_EMAIL)
                .build();
    }
    
    /**
     * Создает запрос на обновление пользователя с кастомными данными.
     * 
     * @param firstName имя
     * @param lastName фамилия
     * @param email email
     * @return UpdateUserRequest с указанными данными
     */
    public static UpdateUserRequest createUpdateUserRequest(String firstName, String lastName, String email) {
        return UpdateUserRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }
    
    /**
     * Создает уникальное имя пользователя для тестов.
     * 
     * @param prefix префикс
     * @return уникальное имя пользователя
     */
    public static String createUniqueUsername(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }
    
    /**
     * Создает уникальный email для тестов.
     * 
     * @param prefix префикс
     * @return уникальный email
     */
    public static String createUniqueEmail(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "@example.com";
    }
}
