package com.taskboard.userservice.infrastructure.persistence.adapter;

import com.taskboard.userservice.domain.model.UserAudit;
import com.taskboard.userservice.domain.repository.UserAuditRepository;
import com.taskboard.userservice.infrastructure.persistence.entity.UserAuditEntity;
import com.taskboard.userservice.infrastructure.persistence.mapper.UserAuditEntityMapper;
import com.taskboard.userservice.infrastructure.persistence.repository.UserAuditJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter implementation of UserAuditRepository using JPA.
 *
 * <p>This adapter bridges the gap between the domain layer and the infrastructure layer by
 * implementing the domain repository interface using JPA repositories. It handles the conversion
 * between domain models and JPA entities using the UserAuditEntityMapper.
 *
 * <p>The adapter provides:
 *
 * <ul>
 *   <li>Domain-to-entity conversion using MapStruct
 *   <li>Transaction management for data consistency
 *   <li>Error handling and validation
 *   <li>Performance optimization for bulk operations
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Transactional
public class UserAuditRepositoryAdapter implements UserAuditRepository {

  private final UserAuditJpaRepository userAuditJpaRepository;
  private final UserAuditEntityMapper userAuditEntityMapper;

  /**
   * Constructs a new UserAuditRepositoryAdapter.
   *
   * @param userAuditJpaRepository the JPA repository for user audit entities
   * @param userAuditEntityMapper the mapper for converting between domain and entity models
   */
  public UserAuditRepositoryAdapter(UserAuditJpaRepository userAuditJpaRepository,
                                   UserAuditEntityMapper userAuditEntityMapper) {
    this.userAuditJpaRepository = userAuditJpaRepository;
    this.userAuditEntityMapper = userAuditEntityMapper;
  }

  @Override
  @Transactional
  public UserAudit save(UserAudit userAudit) {
    if (userAudit == null) {
      throw new IllegalArgumentException("UserAudit cannot be null");
    }

    userAudit.validate();
    UserAuditEntity entity = userAuditEntityMapper.toEntity(userAudit);
    UserAuditEntity savedEntity = userAuditJpaRepository.save(entity);
    return userAuditEntityMapper.toDomain(savedEntity);
  }

  @Override
  @Transactional
  public List<UserAudit> saveAll(List<UserAudit> userAudits) {
    if (userAudits == null) {
      throw new IllegalArgumentException("UserAudits list cannot be null");
    }

    // Validate all audit records
    userAudits.forEach(UserAudit::validate);

    List<UserAuditEntity> entities = userAuditEntityMapper.toEntityList(userAudits);
    List<UserAuditEntity> savedEntities = userAuditJpaRepository.saveAll(entities);
    return userAuditEntityMapper.toDomainList(savedEntities);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findByUserId(Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }

    List<UserAuditEntity> entities = userAuditJpaRepository.findByUserId(userId);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserAudit> findByUserId(Long userId, Pageable pageable) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable cannot be null");
    }

    Page<UserAuditEntity> entityPage = userAuditJpaRepository.findByUserId(userId, pageable);
    return entityPage.map(userAuditEntityMapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findByUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }

    List<UserAuditEntity> entities = userAuditJpaRepository.findByUsername(username);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findByActionType(UserAudit.AuditActionType actionType) {
    if (actionType == null) {
      throw new IllegalArgumentException("Action type cannot be null");
    }

    List<UserAuditEntity> entities = userAuditJpaRepository.findByActionType(actionType);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserAudit> findByActionType(UserAudit.AuditActionType actionType, Pageable pageable) {
    if (actionType == null) {
      throw new IllegalArgumentException("Action type cannot be null");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable cannot be null");
    }

    Page<UserAuditEntity> entityPage = userAuditJpaRepository.findByActionType(actionType, pageable);
    return entityPage.map(userAuditEntityMapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findByIpAddress(String ipAddress) {
    if (ipAddress == null || ipAddress.trim().isEmpty()) {
      throw new IllegalArgumentException("IP address cannot be null or empty");
    }

    List<UserAuditEntity> entities = userAuditJpaRepository.findByIpAddress(ipAddress);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findBySuccess(boolean success) {
    List<UserAuditEntity> entities = userAuditJpaRepository.findBySuccess(success);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findByCreatedAtAfter(LocalDateTime date) {
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be null");
    }

    List<UserAuditEntity> entities = userAuditJpaRepository.findByCreatedAtAfter(date);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate == null) {
      throw new IllegalArgumentException("Start date cannot be null");
    }
    if (endDate == null) {
      throw new IllegalArgumentException("End date cannot be null");
    }
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    }

    List<UserAuditEntity> entities = userAuditJpaRepository.findByCreatedAtBetween(startDate, endDate);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserAudit> findByCriteria(Long userId, UserAudit.AuditActionType actionType,
                                        Boolean success, LocalDateTime startDate, LocalDateTime endDate,
                                        Pageable pageable) {
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable cannot be null");
    }

    Page<UserAuditEntity> entityPage = userAuditJpaRepository.findByCriteria(
        userId, actionType, success, startDate, endDate, pageable);
    return entityPage.map(userAuditEntityMapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findFailedLoginAttemptsByIp(String ipAddress, LocalDateTime startDate) {
    if (ipAddress == null || ipAddress.trim().isEmpty()) {
      throw new IllegalArgumentException("IP address cannot be null or empty");
    }
    if (startDate == null) {
      throw new IllegalArgumentException("Start date cannot be null");
    }

    List<UserAuditEntity> entities = userAuditJpaRepository.findFailedLoginAttemptsByIp(ipAddress, startDate);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public long countByActionType(UserAudit.AuditActionType actionType) {
    if (actionType == null) {
      throw new IllegalArgumentException("Action type cannot be null");
    }

    return userAuditJpaRepository.countByActionType(actionType);
  }

  @Override
  @Transactional(readOnly = true)
  public long countBySuccess(boolean success) {
    return userAuditJpaRepository.countBySuccess(success);
  }

  @Override
  @Transactional(readOnly = true)
  public long countByUserId(Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }

    return userAuditJpaRepository.countByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findRecentByUserId(Long userId, Pageable pageable) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable cannot be null");
    }

    List<UserAuditEntity> entities = userAuditJpaRepository.findRecentByUserId(userId, pageable);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAudit> findSuspiciousActivities(String ipAddress, LocalDateTime startDate, long minAttempts) {
    if (ipAddress == null || ipAddress.trim().isEmpty()) {
      throw new IllegalArgumentException("IP address cannot be null or empty");
    }
    if (startDate == null) {
      throw new IllegalArgumentException("Start date cannot be null");
    }
    if (minAttempts < 1) {
      throw new IllegalArgumentException("Minimum attempts must be at least 1");
    }

    List<UserAuditEntity> entities = userAuditJpaRepository.findSuspiciousActivities(ipAddress, startDate, minAttempts);
    return userAuditEntityMapper.toDomainList(entities);
  }

  @Override
  @Transactional
  public long deleteByCreatedAtBefore(LocalDateTime date) {
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be null");
    }

    return userAuditJpaRepository.deleteByCreatedAtBefore(date);
  }

  @Override
  @Transactional(readOnly = true)
  public long count() {
    return userAuditJpaRepository.count();
  }
}
