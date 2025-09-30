package com.taskboard.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Сервис для получения локализованных сообщений.
 */
@Service
public class MessageService {

    @Autowired
    private MessageSource messageSource;

    /**
     * Получить сообщение по ключу с текущей локалью.
     *
     * @param key ключ сообщения
     * @return локализованное сообщение
     */
    public String getMessage(final String key) {
        System.out.println("DEBUG 1-param: key=" + key);
        String result = messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        System.out.println("DEBUG 1-param: result=" + result);
        return result;
    }

    /**
     * Получить сообщение по ключу с параметрами и текущей локалью.
     *
     * @param key ключ сообщения
     * @param args параметры для подстановки
     * @return локализованное сообщение
     */
    public String getMessage(final String key, final Object[] args) {
        System.out.println("DEBUG 2-param-array: key=" + key + ", args=" + java.util.Arrays.toString(args));
        String message = messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        System.out.println("DEBUG 2-param-array: message=" + message);
        String result = MessageFormat.format(message, args);
        System.out.println("DEBUG 2-param-array: result=" + result);
        return result;
    }

    /**
     * Получить сообщение по ключу с одним параметром и текущей локалью.
     *
     * @param key ключ сообщения
     * @param arg параметр для подстановки
     * @return локализованное сообщение
     */
    public String getMessage(final String key, final Object arg) {
        Object[] args = new Object[]{arg};
        String message = messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        System.out.println("DEBUG 2-param: key=" + key + ", message=" + message + ", arg=" + arg);
        return message;
    }

    /**
     * Получить сообщение по ключу с одним параметром и указанной локалью.
     *
     * @param key ключ сообщения
     * @param arg параметр для подстановки
     * @param locale локаль
     * @return локализованное сообщение
     */
    public String getMessage(final String key, final Object arg, final Locale locale) {
        Object[] args = new Object[]{arg};
        String message = messageSource.getMessage(key, args, locale);
        System.out.println("DEBUG 3-param: key=" + key + ", message=" + message + ", arg=" + arg + ", locale=" + locale);
        return message;
    }

    /**
     * Получить сообщение по ключу с дефолтным значением и текущей локалью.
     *
     * @param key ключ сообщения
     * @param defaultMessage дефолтное сообщение
     * @return локализованное сообщение или дефолтное
     */
    public String getMessage(final String key, final String defaultMessage) {
        System.out.println("DEBUG 2-param-default: key=" + key + ", defaultMessage=" + defaultMessage);
        String result = messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale());
        System.out.println("DEBUG 2-param-default: result=" + result);
        return result;
    }

    public String getMessageWithDefault(final String key, final Locale locale, final String defaultMessage) {
        System.out.println("DEBUG 3-param-default-locale: key=" + key + ", locale=" + locale + ", defaultMessage=" + defaultMessage);
        String result = messageSource.getMessage(key, null, defaultMessage, locale);
        System.out.println("DEBUG 3-param-default-locale: result=" + result);
        return result;
    }

    public String getMessage(final String key, final String defaultMessage, final Locale locale) {
        System.out.println("DEBUG 3-param-default-old: key=" + key + ", defaultMessage=" + defaultMessage + ", locale=" + locale);
        String result = messageSource.getMessage(key, null, defaultMessage, locale);
        System.out.println("DEBUG 3-param-default-old: result=" + result);
        return result;
    }

    /**
     * Получить сообщение по ключу с дефолтным значением и указанной локалью.
     *
     * @param key ключ сообщения
     * @param defaultMessage дефолтное сообщение
     * @param locale локаль
     * @return локализованное сообщение или дефолтное
     */
    public String getMessageWithDefault(final String key, final String defaultMessage, final Locale locale) {
        System.out.println("DEBUG 3-param-default: key=" + key + ", defaultMessage=" + defaultMessage + ", locale=" + locale);
        String result = messageSource.getMessage(key, null, defaultMessage, locale);
        System.out.println("DEBUG 3-param-default: result=" + result);
        return result;
    }

    /**
     * Получить сообщение по ключу с указанной локалью.
     *
     * @param key ключ сообщения
     * @param locale локаль
     * @return локализованное сообщение
     */
    public String getMessage(final String key, final Locale locale) {
        System.out.println("DEBUG 2-param-locale: key=" + key + ", locale=" + locale);
        String result = messageSource.getMessage(key, null, locale);
        System.out.println("DEBUG 2-param-locale: result=" + result);
        return result;
    }

    /**
     * Получить сообщение по ключу с параметрами и указанной локалью.
     *
     * @param key ключ сообщения
     * @param args параметры для подстановки
     * @param locale локаль
     * @return локализованное сообщение
     */
    public String getMessage(final String key, final Object[] args, final Locale locale) {
        String message = messageSource.getMessage(key, null, locale);
        return MessageFormat.format(message, args);
    }


    /**
     * Получить сообщение по ключу с дефолтным значением и указанной локалью.
     *
     * @param key ключ сообщения
     * @param args параметры для подстановки
     * @param defaultMessage дефолтное сообщение
     * @param locale локаль
     * @return локализованное сообщение или дефолтное
     */
    public String getMessage(final String key, final Object[] args, final String defaultMessage, final Locale locale) {
        return messageSource.getMessage(key, args, defaultMessage, locale);
    }
}
