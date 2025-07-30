package com.taskboard.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Сервис для получения локализованных сообщений.
 */
@Service
public class MessageService {

    @Autowired
    private MessageSource messageSource;

    /**
     * Получить сообщение по ключу.
     *
     * @param key ключ сообщения
     * @return локализованное сообщение
     */
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }

    /**
     * Получить сообщение по ключу с параметрами.
     *
     * @param key ключ сообщения
     * @param args параметры для подстановки
     * @return локализованное сообщение
     */
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, Locale.getDefault());
    }

    /**
     * Получить сообщение по ключу с дефолтным значением.
     *
     * @param key ключ сообщения
     * @param defaultMessage дефолтное сообщение
     * @return локализованное сообщение или дефолтное
     */
    public String getMessage(String key, String defaultMessage) {
        return messageSource.getMessage(key, null, defaultMessage, Locale.getDefault());
    }
}
