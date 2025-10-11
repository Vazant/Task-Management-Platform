package com.taskboard.userservice.infrastructure.config;

import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.infrastructure.persistence.adapter.UserRepositoryAdapter;
import com.taskboard.userservice.infrastructure.persistence.mapper.UserEntityMapper;
import com.taskboard.userservice.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for repository adapters.
 *
 * <p>This configuration provides beans for repository adapters that implement domain
 * repository interfaces using JPA repositories.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class RepositoryConfig {

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
}
