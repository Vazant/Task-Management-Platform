package com.taskboard.api.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Конфигурация для работы с локализованными сообщениями.
 */
@Configuration
public class MessageConfig implements WebMvcConfigurer {

    @Bean
    @Primary
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setCacheSeconds(3600); // Cache for 1 hour
        messageSource.setAlwaysUseMessageFormat(false);
        
        return messageSource;
    }

    @Bean
    public MessageSource validationMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:ValidationMessages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource validationMessageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(validationMessageSource);
        return bean;
    }

    @Bean
    public LocaleResolver localeResolver() {
        // Создаем кастомный LocaleResolver, который поддерживает оба способа:
        // 1. Accept-Language заголовок (по умолчанию)
        // 2. Параметр lang (через LocaleChangeInterceptor)
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setSupportedLocales(Arrays.asList(
            new Locale("en"),
            new Locale("ru")
        ));
        localeResolver.setDefaultLocale(new Locale("en"));
        return localeResolver;
    }

    // LocaleChangeInterceptor не используется с AcceptHeaderLocaleResolver,
    // так как AcceptHeaderLocaleResolver не поддерживает setLocale()
    // Если нужна поддержка параметра lang, используйте SessionLocaleResolver
}
