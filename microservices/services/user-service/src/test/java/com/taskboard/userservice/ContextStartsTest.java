package com.taskboard.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Дымовой тест для диагностики проблем с загрузкой Spring контекста.
 * 
 * <p>Этот тест просто проверяет, что Spring ApplicationContext может быть загружен
 * без ошибок. Если этот тест падает, мы увидим реальную причину проблемы
 * вместо "ApplicationContext failure threshold exceeded".</p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ContextStartsTest {
    
    @Test
    void contextLoads() {
        // Этот тест просто проверяет, что Spring контекст загружается
        // Если здесь есть ошибка, мы увидим реальную причину
    }
}
