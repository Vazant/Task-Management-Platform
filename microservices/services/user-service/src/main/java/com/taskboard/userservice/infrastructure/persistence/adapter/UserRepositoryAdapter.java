package com.taskboard.userservice.infrastructure.persistence.adapter;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.repository.UserRepository;
import com.taskboard.userservice.infrastructure.persistence.entity.UserEntity;
import com.taskboard.userservice.infrastructure.persistence.mapper.UserEntityMapper;
import com.taskboard.userservice.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adapter for integrating JPA repository with domain repository interface.
 *
 * <p>This adapter implements the domain UserRepository interface using the JPA repository
 * and entity mapper. It provides a clean separation between the domain layer and
 * infrastructure layer while maintaining the domain repository contract.
 *
 * <p>The UserRepositoryAdapter is responsible for:
 *
 * <ul>
 *   <li>Implementing domain repository interface
 *   <li>Converting between domain models and JPA entities
 *   <li>Handling transactions and persistence operations
 *   <li>Providing domain-specific query methods
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Transactional
public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository jpaRepository;
  private final UserEntityMapper mapper;

  /**
   * Constructs a new UserRepositoryAdapter.
   *
   * @param jpaRepository the JPA repository
   * @param mapper the entity mapper
   */
  public UserRepositoryAdapter(UserJpaRepository jpaRepository, UserEntityMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findById(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return jpaRepository.findById(id)
        .map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findByUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      return Optional.empty();
    }

    return jpaRepository.findByUsername(username)
        .map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findByEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return Optional.empty();
    }

    return jpaRepository.findByEmail(email)
        .map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    List<UserEntity> entities = jpaRepository.findAll();
    return mapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<User> findAll(Pageable pageable) {
    Page<UserEntity> entityPage = jpaRepository.findAll(pageable);
    return entityPage.map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> findByStatus(UserStatus status) {
    if (status == null) {
      return List.of();
    }

    List<UserEntity> entities = jpaRepository.findByStatus(status);
    return mapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<User> findByStatus(UserStatus status, Pageable pageable) {
    if (status == null) {
      return Page.empty(pageable);
    }

    Page<UserEntity> entityPage = jpaRepository.findByStatus(status, pageable);
    return entityPage.map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> findByRole(UserRole role) {
    if (role == null) {
      return List.of();
    }

    List<UserEntity> entities = jpaRepository.findByRole(role);
    return mapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<User> findByRole(UserRole role, Pageable pageable) {
    if (role == null) {
      return Page.empty(pageable);
    }

    Page<UserEntity> entityPage = jpaRepository.findByRole(role, pageable);
    return entityPage.map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> findByFullNameContaining(String searchText) {
    if (searchText == null || searchText.trim().isEmpty()) {
      return List.of();
    }

    List<UserEntity> entities = jpaRepository.findByFullNameContainingIgnoreCase(searchText);
    return mapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<User> findByCriteria(UserStatus status, UserRole role, String searchText, Pageable pageable) {
    Page<UserEntity> entityPage = jpaRepository.findByCriteria(status, role, searchText, pageable);
    return entityPage.map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> findUsersNotLoggedInSince(LocalDateTime date) {
    if (date == null) {
      return List.of();
    }

    List<UserEntity> entities = jpaRepository.findUsersNotLoggedInSince(date);
    return mapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> findByEmailVerified(boolean verified) {
    List<UserEntity> entities = verified 
        ? jpaRepository.findByEmailVerifiedTrue()
        : jpaRepository.findByEmailVerifiedFalse();
    return mapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public long countByStatus(UserStatus status) {
    if (status == null) {
      return 0;
    }

    return jpaRepository.countByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public long countByRole(UserRole role) {
    if (role == null) {
      return 0;
    }

    return jpaRepository.countByRole(role);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      return false;
    }

    return jpaRepository.existsByUsername(username);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return false;
    }

    return jpaRepository.existsByEmail(email);
  }

  @Override
  public User save(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    if (!mapper.isValidForEntityConversion(user)) {
      throw new IllegalArgumentException("User is not valid for persistence");
    }

    UserEntity entity = mapper.toEntity(user);
    UserEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public List<User> saveAll(List<User> users) {
    if (users == null) {
      return List.of();
    }

    List<UserEntity> entities = mapper.toEntityList(users);
    List<UserEntity> savedEntities = jpaRepository.saveAll(entities);
    return mapper.toDomainList(savedEntities);
  }

  @Override
  public void deleteById(Long id) {
    if (id != null) {
      jpaRepository.deleteById(id);
    }
  }

  @Override
  public void delete(User user) {
    if (user != null && user.getId() != null) {
      jpaRepository.deleteById(user.getId());
    }
  }

  @Override
  public void deleteAll(List<User> users) {
    if (users != null && !users.isEmpty()) {
      List<Long> ids = users.stream()
          .map(User::getId)
          .filter(id -> id != null)
          .toList();
      
      if (!ids.isEmpty()) {
        jpaRepository.deleteAllById(ids);
      }
    }
  }

  @Override
  public long deleteByStatus(UserStatus status) {
    if (status == null) {
      return 0;
    }

    return jpaRepository.deleteByStatus(status);
  }

  @Override
  public void updateLastLoginTime(Long userId, LocalDateTime loginTime) {
    if (userId != null && loginTime != null) {
      jpaRepository.updateLastLoginTime(userId, loginTime);
    }
  }

  @Override
  public void updateEmailVerificationStatus(Long userId, boolean verified) {
    if (userId != null) {
      jpaRepository.updateEmailVerificationStatus(userId, verified);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public long count() {
    return jpaRepository.count();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsById(Long id) {
    if (id == null) {
      return false;
    }

    return jpaRepository.existsById(id);
  }
}
