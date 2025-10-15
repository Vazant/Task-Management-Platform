package com.taskboard.userservice.e2e.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

/**
 * Конфигурация для E2E тестов.
 *
 * <p>Эта конфигурация настраивает тестовую среду для E2E тестов.
 * Использует H2 in-memory базу данных для упрощения тестирования.</p>
 *
 * <p>Используется только в профиле "e2e".</p>
 *
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@TestConfiguration
@Profile("e2e")
public class E2ETestConfig {
    // Простая конфигурация без Testcontainers
    // Используем H2 in-memory базу данных
}
