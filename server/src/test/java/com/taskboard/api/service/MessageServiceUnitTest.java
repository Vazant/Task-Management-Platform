package com.taskboard.api.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/** Unit тесты для MessageService без Spring контекста. */
public class MessageServiceUnitTest {

  private MessageService messageService;
  private MessageSource messageSource;

  @BeforeEach
  public void setUp() {
    ReloadableResourceBundleMessageSource reloadableMessageSource =
        new ReloadableResourceBundleMessageSource();
    reloadableMessageSource.setBasename("classpath:messages");
    reloadableMessageSource.setDefaultEncoding("UTF-8");
    reloadableMessageSource.setUseCodeAsDefaultMessage(false);
    messageSource = reloadableMessageSource;

    messageService = new MessageService();
    // Используем рефлексию для установки messageSource
    try {
      java.lang.reflect.Field field = MessageService.class.getDeclaredField("messageSource");
      field.setAccessible(true);
      field.set(messageService, messageSource);
    } catch (Exception e) {
      fail("Failed to inject messageSource: " + e.getMessage());
    }
  }

  @Test
  public void testGetMessageWithDefaultLocale() {
    // Тест получения сообщения с локалью по умолчанию (EN)
    String message = messageService.getMessage("auth.login.success");
    // По умолчанию может быть русская локаль, поэтому проверяем любой из вариантов
    assertTrue(message.equals("Login successful") || message.equals("Вход выполнен успешно"));
  }

  @Test
  public void testGetMessageWithEnglishLocale() {
    // Тест получения сообщения на английском языке
    String message = messageService.getMessage("auth.login.success", Locale.ENGLISH);
    assertEquals("Login successful", message);
  }

  @Test
  public void testGetMessageWithRussianLocale() {
    // Тест получения сообщения на русском языке
    String message = messageService.getMessage("auth.login.success", Locale.forLanguageTag("ru"));
    assertEquals("Вход выполнен успешно", message);
  }

  @Test
  public void testGetMessageWithParameters() {
    // Тест получения сообщения с параметрами
    String message =
        messageService.getMessage("info.test.auth", new Object[] {"john.doe"}, Locale.ENGLISH);
    assertEquals("Authenticated as: john.doe", message);

    String messageRu =
        messageService.getMessage(
            "info.test.auth", new Object[] {"john.doe"}, Locale.forLanguageTag("ru"));
    assertEquals("Аутентифицирован как: john.doe", messageRu);
  }

  @Test
  public void testGetMessageWithDefaultMessage() {
    // Тест получения сообщения с дефолтным значением
    String message =
        messageService.getMessage(
            "nonexistent.key", new Object[] {}, "Default message", Locale.ENGLISH);
    assertEquals("Default message", message);
  }

  @Test
  public void testValidationMessages() {
    // Тест сообщений валидации
    String message = messageService.getMessage("validation.email.required", Locale.ENGLISH);
    assertEquals("Email is required", message);

    String messageRu =
        messageService.getMessage("validation.email.required", Locale.forLanguageTag("ru"));
    assertEquals("Email обязателен", messageRu);
  }

  @Test
  public void testErrorMessages() {
    // Тест сообщений об ошибках
    String message =
        messageService.getMessage(
            "error.global.unexpected", new Object[] {"Test error"}, Locale.ENGLISH);
    assertEquals("Internal server error: Test error", message);

    String messageRu =
        messageService.getMessage(
            "error.global.unexpected", new Object[] {"Test error"}, Locale.forLanguageTag("ru"));
    assertEquals("Внутренняя ошибка сервера: Test error", messageRu);
  }

  @Test
  public void testMessageSourceDirectly() {
    // Тест прямого использования MessageSource
    String message = messageSource.getMessage("auth.register.success", null, Locale.ENGLISH);
    assertEquals("Registration successful", message);

    String messageRu =
        messageSource.getMessage("auth.register.success", null, Locale.forLanguageTag("ru"));
    assertEquals("Регистрация выполнена успешно", messageRu);
  }

  @Test
  public void testSpringPluralization() {
    // Тест Spring MessageFormat для множественного числа
    String message1 =
        messageService.getMessage("cart.items.count", new Object[] {1}, Locale.ENGLISH);
    assertEquals("1 item", message1);

    String message2 =
        messageService.getMessage("cart.items.count", new Object[] {2}, Locale.ENGLISH);
    assertEquals("2 items", message2);

    String messageRu1 =
        messageService.getMessage(
            "cart.items.count", new Object[] {1}, Locale.forLanguageTag("ru"));
    assertEquals("1 товар", messageRu1);

    String messageRu2 =
        messageService.getMessage(
            "cart.items.count", new Object[] {2}, Locale.forLanguageTag("ru"));
    assertEquals("2 товара", messageRu2);

    String messageRu5 =
        messageService.getMessage(
            "cart.items.count", new Object[] {5}, Locale.forLanguageTag("ru"));
    assertEquals("5 товаров", messageRu5);
  }

  @Test
  public void testKeyExistence() {
    // Тест существования ключей сообщений
    String[] testKeys = {
      "auth.login.success",
      "auth.login.failure.credentials",
      "auth.register.success",
      "validation.email.required",
      "validation.email.invalid",
      "error.global.unexpected",
      "success.auth.login",
      "info.test.public"
    };

    for (String key : testKeys) {
      // Проверяем, что ключ существует для английского языка
      assertDoesNotThrow(
          () -> {
            String message = messageSource.getMessage(key, null, Locale.ENGLISH);
            assertNotNull(message, "Message for key " + key + " should not be null");
            assertFalse(
                message.trim().isEmpty(), "Message for key " + key + " should not be empty");
          },
          "Key " + key + " should exist in English messages");

      // Проверяем, что ключ существует для русского языка
      assertDoesNotThrow(
          () -> {
            String message = messageSource.getMessage(key, null, Locale.forLanguageTag("ru"));
            assertNotNull(message, "Message for key " + key + " should not be null");
            assertFalse(
                message.trim().isEmpty(), "Message for key " + key + " should not be empty");
          },
          "Key " + key + " should exist in Russian messages");
    }
  }
}
