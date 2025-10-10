package com.taskboard.userservice.infrastructure.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Flyway configuration for database migrations.
 *
 * <p>This configuration class sets up Flyway for database schema management and provides custom
 * migration strategies for different environments.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Automatic migration on application startup
 *   <li>Environment-specific migration strategies
 *   <li>Validation of applied migrations
 *   <li>Baseline support for existing databases
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class FlywayConfig {

  /**
   * Custom Flyway migration strategy for development environment.
   *
   * <p>This strategy is used in development to ensure clean migrations and proper handling of
   * schema changes during development.
   *
   * @return custom migration strategy for development
   */
  @Bean
  @Profile("dev")
  public FlywayMigrationStrategy devMigrationStrategy() {
    return flyway -> {
      // Clean and migrate for development
      flyway.clean();
      flyway.migrate();
    };
  }

  /**
   * Custom Flyway migration strategy for test environment.
   *
   * <p>This strategy is used in testing to ensure clean database state for each test run and proper
   * migration handling.
   *
   * @return custom migration strategy for testing
   */
  @Bean
  @Profile("test")
  public FlywayMigrationStrategy testMigrationStrategy() {
    return flyway -> {
      // Clean and migrate for testing
      flyway.clean();
      flyway.migrate();
    };
  }

  /**
   * Custom Flyway migration strategy for production environment.
   *
   * <p>This strategy is used in production to ensure safe migrations without data loss and proper
   * validation of schema changes.
   *
   * @return custom migration strategy for production
   */
  @Bean
  @Profile("prod")
  public FlywayMigrationStrategy prodMigrationStrategy() {
    return flyway -> {
      // Validate existing migrations first
      flyway.validate();

      // Apply new migrations
      flyway.migrate();
    };
  }
}
