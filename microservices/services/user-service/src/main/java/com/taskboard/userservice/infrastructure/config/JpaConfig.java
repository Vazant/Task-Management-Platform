package com.taskboard.userservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration for the user service.
 *
 * <p>This configuration enables JPA repositories and transaction management for the
 * user service infrastructure layer.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.taskboard.userservice.infrastructure.persistence.repository")
@EnableTransactionManagement
public class JpaConfig {
  // JPA configuration is handled by Spring Boot auto-configuration
  // This class serves as a marker for JPA repository scanning
}
