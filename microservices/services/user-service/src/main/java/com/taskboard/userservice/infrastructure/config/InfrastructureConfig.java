package com.taskboard.userservice.infrastructure.config;

import com.taskboard.userservice.domain.event.UserEventPublisher;
import com.taskboard.userservice.domain.repository.UserAuditRepository;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.domain.service.UserDomainService;
import com.taskboard.userservice.infrastructure.persistence.adapter.UserAuditRepositoryAdapter;
import com.taskboard.userservice.infrastructure.persistence.adapter.UserRepositoryAdapter;
import com.taskboard.userservice.infrastructure.persistence.mapper.UserAuditEntityMapper;
import com.taskboard.userservice.infrastructure.persistence.mapper.UserEntityMapper;
import com.taskboard.userservice.infrastructure.persistence.repository.UserAuditJpaRepository;
import com.taskboard.userservice.infrastructure.persistence.repository.UserJpaRepository;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Infrastructure configuration for User Service.
 *
 * <p>This configuration class sets up the database connection, JPA entity manager, transaction
 * management, Flyway migrations, repository adapters, and domain services for the User Service.
 * It provides a clean separation between infrastructure concerns and business logic.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>PostgreSQL data source configuration
 *   <li>JPA entity manager factory setup
 *   <li>Transaction management configuration
 *   <li>Hibernate properties optimization
 *   <li>Flyway database migrations
 *   <li>Repository adapter beans configuration
 *   <li>Domain service beans configuration
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@Profile("!test")
@EnableJpaRepositories(
    basePackages = "com.taskboard.userservice.infrastructure.persistence",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager")
@EnableTransactionManagement
public class InfrastructureConfig {

  /**
   * Creates and configures the primary data source.
   *
   * <p>This data source is configured using Spring Boot's auto-configuration properties and
   * provides connection pooling through HikariCP.
   *
   * @return configured data source
   */
  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  /**
   * Creates and configures the JPA entity manager factory.
   *
   * <p>This factory is responsible for creating EntityManager instances and managing the
   * persistence context. It's configured with Hibernate as the JPA provider and optimized for
   * PostgreSQL.
   *
   * @param dataSource the data source to use
   * @return configured entity manager factory
   */
  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("com.taskboard.userservice.infrastructure.persistence.entity");

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setShowSql(false);
    vendorAdapter.setGenerateDdl(false);
    vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");

    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(hibernateProperties());

    return em;
  }

  /**
   * Creates and configures the transaction manager.
   *
   * <p>This transaction manager handles database transactions and ensures data consistency across
   * operations. It's configured to work with the JPA entity manager factory.
   *
   * @param entityManagerFactory the entity manager factory
   * @return configured transaction manager
   */
  @Bean
  @Primary
  public PlatformTransactionManager transactionManager(
      LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    return transactionManager;
  }

  /**
   * Configures Hibernate properties for optimal performance and behavior.
   *
   * <p>These properties optimize Hibernate for production use with PostgreSQL, including connection
   * pooling, batch processing, and query optimization.
   *
   * @return Hibernate properties
   */
  private Properties hibernateProperties() {
    Properties properties = new Properties();

    // Connection and pooling
    properties.setProperty("hibernate.connection.provider_disables_autocommit", "true");
    properties.setProperty("hibernate.connection.autocommit", "false");

    // Batch processing
    properties.setProperty("hibernate.jdbc.batch_size", "20");
    properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
    properties.setProperty("hibernate.order_inserts", "true");
    properties.setProperty("hibernate.order_updates", "true");

    // Query optimization
    properties.setProperty("hibernate.query.plan_cache_max_size", "2048");
    properties.setProperty("hibernate.query.plan_parameter_metadata_max_size", "128");

    // Second level cache (can be enabled later with Redis)
    properties.setProperty("hibernate.cache.use_second_level_cache", "false");
    properties.setProperty("hibernate.cache.use_query_cache", "false");

    // Statistics and monitoring
    properties.setProperty("hibernate.generate_statistics", "false");
    properties.setProperty("hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS", "100");

    // Schema validation
    properties.setProperty("hibernate.hbm2ddl.auto", "validate");

    // Naming strategy
    properties.setProperty(
        "hibernate.physical_naming_strategy",
        "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

    return properties;
  }

  /**
   * Creates a UserRepository bean using the JPA adapter.
   *
   * @param jpaRepository the JPA repository
   * @param mapper the entity mapper
   * @return the UserRepository implementation
   */
  @Bean
  public UserRepository userRepository(UserJpaRepository jpaRepository, UserEntityMapper mapper) {
    return new UserRepositoryAdapter(jpaRepository, mapper);
  }

  /**
   * Creates a UserAuditRepository bean using the JPA adapter.
   *
   * @param userAuditJpaRepository the JPA repository for user audit entities
   * @param userAuditEntityMapper the mapper for converting between domain and entity models
   * @return the UserAuditRepository implementation
   */
  @Bean
  public UserAuditRepository userAuditRepository(UserAuditJpaRepository userAuditJpaRepository,
                                                 UserAuditEntityMapper userAuditEntityMapper) {
    return new UserAuditRepositoryAdapter(userAuditJpaRepository, userAuditEntityMapper);
  }

  /**
   * Creates a UserDomainService bean.
   *
   * @param userRepository the user repository
   * @param eventPublisher the event publisher
   * @return the UserDomainService instance
   */
  @Bean
  public UserDomainService userDomainService(UserRepository userRepository, UserEventPublisher eventPublisher) {
    return new UserDomainService(userRepository, eventPublisher);
  }

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
