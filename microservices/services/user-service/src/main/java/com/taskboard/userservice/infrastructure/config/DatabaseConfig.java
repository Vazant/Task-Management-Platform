package com.taskboard.userservice.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Database configuration for User Service.
 * 
 * <p>This configuration class sets up the database connection, JPA entity manager,
 * and transaction management for the User Service. It provides a clean separation
 * between infrastructure concerns and business logic.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>PostgreSQL data source configuration</li>
 *   <li>JPA entity manager factory setup</li>
 *   <li>Transaction management configuration</li>
 *   <li>Hibernate properties optimization</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.taskboard.userservice.infrastructure.persistence",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
@EnableTransactionManagement
public class DatabaseConfig {
    
    /**
     * Creates and configures the primary data source.
     * 
     * <p>This data source is configured using Spring Boot's auto-configuration
     * properties and provides connection pooling through HikariCP.</p>
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
     * <p>This factory is responsible for creating EntityManager instances
     * and managing the persistence context. It's configured with Hibernate
     * as the JPA provider and optimized for PostgreSQL.</p>
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
     * <p>This transaction manager handles database transactions and ensures
     * data consistency across operations. It's configured to work with
     * the JPA entity manager factory.</p>
     * 
     * @param entityManagerFactory the entity manager factory
     * @return configured transaction manager
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
    
    /**
     * Configures Hibernate properties for optimal performance and behavior.
     * 
     * <p>These properties optimize Hibernate for production use with PostgreSQL,
     * including connection pooling, batch processing, and query optimization.</p>
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
        properties.setProperty("hibernate.physical_naming_strategy", 
            "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        
        return properties;
    }
}
