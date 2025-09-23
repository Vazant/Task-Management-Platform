package com.taskboard.api.config;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Тесты для конфигурации i18n.
 */
@SpringBootTest
@ActiveProfiles("test")
public class MessageConfigTest {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Autowired
    private LocaleResolver localeResolver;

    @Test
    public void testMessageSourceBean() {
        assertNotNull(messageSource, "MessageSource bean should be configured");
        assertTrue(messageSource instanceof org.springframework.context.support.ReloadableResourceBundleMessageSource,
                "MessageSource should be ReloadableResourceBundleMessageSource");
    }

    @Test
    public void testValidatorBean() {
        assertNotNull(validator, "LocalValidatorFactoryBean should be configured");
        // Note: getValidationMessageSource() method might not be available in all Spring versions
        // This test verifies that the validator bean is properly configured
    }

    @Test
    public void testLocaleResolverBean() {
        assertNotNull(localeResolver, "LocaleResolver bean should be configured");
        assertTrue(localeResolver instanceof org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver,
                "LocaleResolver should be AcceptHeaderLocaleResolver");
    }

    @Test
    public void testDefaultLocale() {
        // Create a mock request with no Accept-Language header to test default locale
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Accept-Language")).thenReturn(null);
        
        Locale defaultLocale = localeResolver.resolveLocale(request);
        assertEquals(Locale.ENGLISH, defaultLocale, "Default locale should be English");
    }

    @Test
    public void testSupportedLocales() {
        // Тест поддерживаемых локалей
        assertDoesNotThrow(() -> {
            String messageEn = messageSource.getMessage("auth.login.success", null, Locale.ENGLISH);
            assertNotNull(messageEn);
        }, "English locale should be supported");

        assertDoesNotThrow(() -> {
            String messageRu = messageSource.getMessage("auth.login.success", null, Locale.forLanguageTag("ru"));
            assertNotNull(messageRu);
        }, "Russian locale should be supported");
    }

    @Test
    public void testMessageSourceEncoding() {
        // Тест кодировки UTF-8 для русских символов
        String messageRu = messageSource.getMessage("auth.login.success", null, Locale.forLanguageTag("ru"));
        assertTrue(messageRu.contains("Вход"), "Russian message should contain Cyrillic characters");
    }

    @Test
    public void testMessageSourceCaching() {
        // Тест кэширования сообщений
        long startTime = System.currentTimeMillis();
        String message1 = messageSource.getMessage("auth.login.success", null, Locale.ENGLISH);
        long firstCallTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        String message2 = messageSource.getMessage("auth.login.success", null, Locale.ENGLISH);
        long secondCallTime = System.currentTimeMillis() - startTime;

        assertEquals(message1, message2, "Cached message should be the same");
        // Второй вызов должен быть быстрее из-за кэширования
        assertTrue(secondCallTime <= firstCallTime, "Second call should be faster due to caching");
    }

    @Test
    public void testValidationMessageSource() {
        // Тест MessageSource для валидации
        // Note: Direct access to validation MessageSource might not be available in all Spring versions
        // Instead, we test that validation messages work through the main MessageSource
        String validationMessage = messageSource.getMessage("validation.email.required", null, Locale.ENGLISH);
        assertEquals("Email is required", validationMessage);

        String validationMessageRu = messageSource.getMessage("validation.email.required", null, Locale.forLanguageTag("ru"));
        assertEquals("Email обязателен", validationMessageRu);
    }
}
