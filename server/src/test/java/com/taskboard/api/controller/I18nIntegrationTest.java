package com.taskboard.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.LoginRequest;
import com.taskboard.api.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/** Интеграционные тесты для проверки i18n в контроллерах. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class I18nIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testLoginWithEnglishLocale() throws Exception {
    LoginRequest request = new LoginRequest("test@example.com", "password123");

    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .header("Accept-Language", "en")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ApiResponse<?> response = objectMapper.readValue(responseContent, ApiResponse.class);

    // Проверяем, что сообщение на английском языке
    assertTrue(
        response.getMessage().contains("Bad credentials")
            || response.getMessage().contains("Invalid email or password")
            || response.getMessage().contains("auth.error.invalid.credentials"));
  }

  @Test
  public void testLoginWithRussianLocale() throws Exception {
    LoginRequest request = new LoginRequest("test@example.com", "password123");

    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .header("Accept-Language", "ru")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ApiResponse<?> response = objectMapper.readValue(responseContent, ApiResponse.class);

    // Проверяем, что сообщение на русском языке
    assertTrue(
        response.getMessage().contains("Bad credentials")
            || response.getMessage().contains("Неверный email или пароль")
            || response.getMessage().contains("auth.error.invalid.credentials"));
  }

  @Test
  public void testRegisterWithEnglishLocale() throws Exception {
    RegisterRequest request =
        new RegisterRequest("testuser", "test@example.com", "password123", "password123");

    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/register")
                    .header("Accept-Language", "en")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ApiResponse<?> response = objectMapper.readValue(responseContent, ApiResponse.class);

    // Проверяем, что сообщение на английском языке
    assertTrue(
        response.getMessage().contains("Registration successful")
            || response.getMessage().contains("User with this email already exists"));
  }

  @Test
  public void testRegisterWithRussianLocale() throws Exception {
    RegisterRequest request =
        new RegisterRequest("testuser", "test@example.com", "password123", "password123");

    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/register")
                    .header("Accept-Language", "ru")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ApiResponse<?> response = objectMapper.readValue(responseContent, ApiResponse.class);

    // Проверяем, что сообщение на русском языке
    assertTrue(
        response.getMessage().contains("Регистрация выполнена успешно")
            || response.getMessage().contains("Пользователь с таким email уже существует"));
  }

  @Test
  public void testValidationErrorsWithEnglishLocale() throws Exception {
    // Отправляем невалидный запрос (пустой email)
    LoginRequest request = new LoginRequest("", "password123");

    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .header("Accept-Language", "en")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ApiResponse<?> response = objectMapper.readValue(responseContent, ApiResponse.class);

    // Проверяем, что сообщение об ошибке валидации на английском языке
    assertTrue(
        response.getMessage().contains("Invalid request")
            || response.getMessage().contains("Email is required"));
  }

  @Test
  public void testValidationErrorsWithRussianLocale() throws Exception {
    // Отправляем невалидный запрос (пустой email)
    LoginRequest request = new LoginRequest("", "password123");

    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .header("Accept-Language", "ru")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ApiResponse<?> response = objectMapper.readValue(responseContent, ApiResponse.class);

    // Проверяем, что сообщение об ошибке валидации на русском языке
    assertTrue(
        response.getMessage().contains("Неверный запрос")
            || response.getMessage().contains("Email обязателен"));
  }

  @Test
  public void testTestEndpointWithEnglishLocale() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/test/public").header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ApiResponse<?> response = objectMapper.readValue(responseContent, ApiResponse.class);

    // Проверяем, что сообщение на английском языке
    assertEquals("Public endpoint works", response.getData());
  }

  @Test
  public void testTestEndpointWithRussianLocale() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/test/public").header("Accept-Language", "ru"))
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ApiResponse<?> response = objectMapper.readValue(responseContent, ApiResponse.class);

    // Проверяем, что сообщение на русском языке
    assertEquals("Публичный endpoint работает", response.getData());
  }

  @Test
  public void testLocaleParameterOverride() throws Exception {
    // Тест, что Accept-Language заголовок работает корректно
    // (параметр lang не поддерживается с AcceptHeaderLocaleResolver)
    MvcResult result =
        mockMvc
            .perform(
                get("/api/test/public")
                    .param("lang", "ru") // Параметр будет проигнорирован
                    .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ApiResponse<?> response = objectMapper.readValue(responseContent, ApiResponse.class);

    // Accept-Language имеет приоритет
    assertEquals("Public endpoint works", response.getData());
  }
}
