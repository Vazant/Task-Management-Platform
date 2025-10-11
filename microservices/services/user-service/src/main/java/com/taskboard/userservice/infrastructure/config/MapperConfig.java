package com.taskboard.userservice.infrastructure.config;

import com.taskboard.userservice.infrastructure.persistence.mapper.UserEntityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for entity mappers.
 *
 * <p>This configuration provides beans for mapping between domain models and JPA entities.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class MapperConfig {

  /**
   * Creates a UserEntityMapper bean.
   *
   * @return the UserEntityMapper instance
   */
  @Bean
  public UserEntityMapper userEntityMapper() {
    return new UserEntityMapper();
  }
}
