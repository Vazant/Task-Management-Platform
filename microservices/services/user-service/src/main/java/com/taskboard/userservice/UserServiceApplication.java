package com.taskboard.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for User Service.
 * 
 * <p>This service is responsible for user management, authentication, and authorization.
 * It follows hexagonal architecture principles with clear separation of concerns.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>User CRUD operations</li>
 *   <li>Authentication and authorization</li>
 *   <li>JWT token management</li>
 *   <li>Event publishing for user lifecycle events</li>
 *   <li>Integration with other microservices</li>
 * </ul>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.taskboard.userservice.infrastructure.persistence")
@EnableTransactionManagement
@EnableKafka
@EnableFeignClients(basePackages = "com.taskboard.userservice.infrastructure.external")
public class UserServiceApplication {

    /**
     * Main method to start the User Service application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
