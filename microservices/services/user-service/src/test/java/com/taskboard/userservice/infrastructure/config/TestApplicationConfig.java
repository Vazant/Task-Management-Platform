package com.taskboard.userservice.infrastructure.config;

import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.domain.repository.UserStatisticsRepository;
import com.taskboard.userservice.infrastructure.repository.UserRepositoryStub;
import com.taskboard.userservice.infrastructure.repository.UserStatisticsRepositoryStub;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration for Spring Boot tests.
 * Provides mock beans and test-specific configurations.
 */
@TestConfiguration
@Profile("test")
@Import(TestSecurityConfig.class)
public class TestApplicationConfig {

    @Bean
    @Primary
    public UserRepository testUserRepository() {
        return new UserRepositoryStub();
    }

    @Bean
    @Primary
    public UserStatisticsRepository testUserStatisticsRepository() {
        return new UserStatisticsRepositoryStub();
    }
}
