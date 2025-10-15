package com.taskboard.userservice.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.e2e.config.E2ETestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Базовый класс для E2E тестов.
 * 
 * <p>Предоставляет общую конфигурацию для всех E2E тестов:
 * <ul>
 *   <li>Spring Boot контекст с реальными зависимостями</li>
 *   <li>Testcontainers для внешних сервисов</li>
 *   <li>MockMvc для HTTP запросов</li>
 *   <li>ObjectMapper для JSON сериализации</li>
 *   <li>Транзакции с откатом</li>
 * </ul>
 * </p>
 * 
 * <p>Все E2E тесты должны наследоваться от этого класса.</p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@Import(E2ETestConfig.class)
@ActiveProfiles("e2e")
@Transactional
public abstract class BaseE2ETest {
    
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    /**
     * Создает JSON строку из объекта.
     * 
     * @param object объект для сериализации
     * @return JSON строка
     * @throws Exception если произошла ошибка сериализации
     */
    protected String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
    
    /**
     * Создает объект из JSON строки.
     * 
     * @param json JSON строка
     * @param clazz класс для десериализации
     * @param <T> тип объекта
     * @return десериализованный объект
     * @throws Exception если произошла ошибка десериализации
     */
    protected <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}
