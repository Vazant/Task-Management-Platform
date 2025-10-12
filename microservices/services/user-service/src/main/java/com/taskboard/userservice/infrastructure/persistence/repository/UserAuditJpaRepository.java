package com.taskboard.userservice.infrastructure.persistence.repository;

import com.taskboard.userservice.domain.model.UserAudit;
import com.taskboard.userservice.infrastructure.persistence.entity.UserAuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for UserAuditEntity operations.
 *
 * <p>This repository provides data access methods for user audit logs using Spring Data JPA.
 * It includes custom queries for audit analysis, security monitoring, and compliance reporting.
 *
 * <p>The UserAuditJpaRepository is responsible for:
 *
 * <ul>
 *   <li>Basic CRUD operations for audit logs
 *   <li>Custom queries for security analysis
 *   <li>Pagination support for large audit datasets
 *   <li>Performance-optimized queries for reporting
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface UserAuditJpaRepository extends JpaRepository<UserAuditEntity, Long> {

  /**
   * Finds audit logs by user ID.
   *
   * @param userId the user ID
   * @return list of audit logs for the user
   */
  List<UserAuditEntity> findByUserId(Long userId);

  /**
   * Finds audit logs by user ID with pagination.
   *
   * @param userId the user ID
   * @param pageable pagination information
   * @return page of audit logs for the user
   */
  Page<UserAuditEntity> findByUserId(Long userId, Pageable pageable);

  /**
   * Finds audit logs by username.
   *
   * @param username the username
   * @return list of audit logs for the username
   */
  List<UserAuditEntity> findByUsername(String username);

  /**
   * Finds audit logs by action type.
   *
   * @param actionType the action type
   * @return list of audit logs with the action type
   */
  List<UserAuditEntity> findByActionType(UserAudit.AuditActionType actionType);

  /**
   * Finds audit logs by action type with pagination.
   *
   * @param actionType the action type
   * @param pageable pagination information
   * @return page of audit logs with the action type
   */
  Page<UserAuditEntity> findByActionType(UserAudit.AuditActionType actionType, Pageable pageable);

  /**
   * Finds audit logs by IP address.
   *
   * @param ipAddress the IP address
   * @return list of audit logs from the IP address
   */
  List<UserAuditEntity> findByIpAddress(String ipAddress);

  /**
   * Finds audit logs by success status.
   *
   * @param success the success status
   * @return list of audit logs with the success status
   */
  List<UserAuditEntity> findBySuccess(boolean success);

  /**
   * Finds audit logs created after a specific date.
   *
   * @param date the date to search from
   * @return list of audit logs created after the date
   */
  List<UserAuditEntity> findByCreatedAtAfter(LocalDateTime date);

  /**
   * Finds audit logs created between two dates.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of audit logs created between the dates
   */
  List<UserAuditEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Finds audit logs by multiple criteria with pagination.
   *
   * @param userId the user ID (optional)
   * @param actionType the action type (optional)
   * @param success the success status (optional)
   * @param startDate the start date (optional)
   * @param endDate the end date (optional)
   * @param pageable pagination information
   * @return page of audit logs matching the criteria
   */
  @Query(
      "SELECT u FROM UserAuditEntity u WHERE "
          + "(:userId IS NULL OR u.userId = :userId) AND "
          + "(:actionType IS NULL OR u.actionType = :actionType) AND "
          + "(:success IS NULL OR u.success = :success) AND "
          + "(:startDate IS NULL OR u.createdAt >= :startDate) AND "
          + "(:endDate IS NULL OR u.createdAt <= :endDate)")
  Page<UserAuditEntity> findByCriteria(
      @Param("userId") Long userId,
      @Param("actionType") UserAudit.AuditActionType actionType,
      @Param("success") Boolean success,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable);

  /**
   * Finds failed login attempts by IP address.
   *
   * @param ipAddress the IP address
   * @param startDate the start date
   * @return list of failed login attempts from the IP address
   */
  @Query(
      "SELECT u FROM UserAuditEntity u WHERE "
          + "u.ipAddress = :ipAddress AND "
          + "u.actionType = 'LOGIN_FAILURE' AND "
          + "u.createdAt >= :startDate")
  List<UserAuditEntity> findFailedLoginAttemptsByIp(
      @Param("ipAddress") String ipAddress,
      @Param("startDate") LocalDateTime startDate);

  /**
   * Counts audit logs by action type.
   *
   * @param actionType the action type
   * @return number of audit logs with the action type
   */
  long countByActionType(UserAudit.AuditActionType actionType);

  /**
   * Counts audit logs by success status.
   *
   * @param success the success status
   * @return number of audit logs with the success status
   */
  long countBySuccess(boolean success);

  /**
   * Counts audit logs by user ID.
   *
   * @param userId the user ID
   * @return number of audit logs for the user
   */
  long countByUserId(Long userId);

  /**
   * Finds recent audit logs for a user.
   *
   * @param userId the user ID
   * @param limit the maximum number of logs to return
   * @return list of recent audit logs for the user
   */
  @Query(
      "SELECT u FROM UserAuditEntity u WHERE u.userId = :userId "
          + "ORDER BY u.createdAt DESC")
  List<UserAuditEntity> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);

  /**
   * Finds suspicious activities (multiple failed attempts from same IP).
   *
   * @param ipAddress the IP address
   * @param startDate the start date
   * @param minAttempts the minimum number of attempts
   * @return list of suspicious activities
   */
  @Query(
      "SELECT u FROM UserAuditEntity u WHERE "
          + "u.ipAddress = :ipAddress AND "
          + "u.actionType IN ('LOGIN_FAILURE', 'AUTHORIZATION_FAILURE', 'SUSPICIOUS_ACTIVITY') AND "
          + "u.createdAt >= :startDate "
          + "GROUP BY u.ipAddress "
          + "HAVING COUNT(u) >= :minAttempts")
  List<UserAuditEntity> findSuspiciousActivities(
      @Param("ipAddress") String ipAddress,
      @Param("startDate") LocalDateTime startDate,
      @Param("minAttempts") long minAttempts);

  /**
   * Deletes audit logs older than a specific date (for cleanup).
   *
   * @param date the date to delete logs before
   * @return number of deleted audit logs
   */
  long deleteByCreatedAtBefore(LocalDateTime date);
}
