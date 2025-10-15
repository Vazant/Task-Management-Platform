package com.taskboard.userservice.config;

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
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Test infrastructure configuration for User Service.
 *
 * <p>This configuration class overrides the main infrastructure configuration for testing purposes.
 * It uses H2 in-memory database and Hibernate's create-drop strategy for automatic schema management.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@Profile("test")
@EnableJpaRepositories(
    basePackages = "com.taskboard.userservice.infrastructure.persistence",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager")
@EnableTransactionManagement
public class TestInfrastructureConfig {

  /**
   * Creates and configures the test data source.
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
   * Creates and configures the JPA entity manager factory for testing.
   *
   * <p>This factory is responsible for creating EntityManager instances and managing the
   * persistence context. It's configured with Hibernate as the JPA provider and optimized for
   * H2 in-memory database.
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
    vendorAdapter.setGenerateDdl(true);
    vendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");

    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(hibernateProperties());

    return em;
  }

  /**
   * Creates and configures the transaction manager for testing.
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
   * Configures Hibernate properties for testing.
   *
   * <p>These properties optimize Hibernate for testing with H2 in-memory database,
   * including automatic schema creation and dropping.
   *
   * @return Hibernate properties
   */
  private Properties hibernateProperties() {
    Properties properties = new Properties();

    // Schema management for testing
    properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

    // Naming strategy
    properties.setProperty(
        "hibernate.physical_naming_strategy",
        "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

    // Disable statistics for testing
    properties.setProperty("hibernate.generate_statistics", "false");

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
   * Creates a test UserDetailsService bean.
   *
   * <p>This is a simple implementation for testing purposes that returns a test user.
   *
   * @return the UserDetailsService instance
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      if ("testuser".equals(username)) {
        return User.builder()
            .username("testuser")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();
      }
      throw new UsernameNotFoundException("User not found: " + username);
    };
  }

         /**
          * Creates a PasswordEncoder bean for testing.
          *
          * @return the PasswordEncoder instance
          */
         @Bean
         public PasswordEncoder passwordEncoder() {
           return new BCryptPasswordEncoder();
         }

         /**
          * Creates an AuthenticationManager bean for testing.
          *
          * <p>This is a simple implementation for testing purposes that uses
          * DaoAuthenticationProvider with the test UserDetailsService.
          *
          * @param userDetailsService the UserDetailsService
          * @param passwordEncoder the PasswordEncoder
          * @return the AuthenticationManager instance
          */
         @Bean
         public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
           DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
           authProvider.setUserDetailsService(userDetailsService);
           authProvider.setPasswordEncoder(passwordEncoder);
           return new ProviderManager(authProvider);
         }
       }

