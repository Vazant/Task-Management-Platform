package com.taskboard.userservice.domain.repository;

import com.taskboard.userservice.domain.model.UserAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for UserAudit domain entities.
 *
 * <p>This interface defines the contract for user audit data access operations from a domain perspective.
 * It follows the Repository pattern and provides a clean abstraction over audit data persistence
 * concerns.
 *
 * <p>The repository is responsible for:
 *
 * <ul>
 *   <li>Persisting user audit records
 *   <li>Retrieving audit logs by various criteria
 *   <li>Managing audit lifecycle operations
 *   <li>Providing domain-specific query methods for security analysis
 * </ul>
 *
 * <p>This interface is part of the domain layer and should not contain any infrastructure-specific
 * details. The actual implementation will be provided by the infrastructure layer.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserAuditRepository {

  /**
   * Saves a user audit record.
   *
   * @param userAudit the user audit record to save
   * @return the saved user audit record
   * @throws IllegalArgumentException if userAudit is null
   */
  UserAudit save(UserAudit userAudit);

  /**
   * Saves multiple user audit records.
   *
   * @param userAudits the list of user audit records to save
   * @return the list of saved user audit records
   */
  List<UserAudit> saveAll(List<UserAudit> userAudits);

  /**
   * Finds audit logs by user ID.
   *
   * @param userId the user ID
   * @return list of audit logs for the user
   */
  List<UserAudit> findByUserId(Long userId);

  /**
   * Finds audit logs by user ID with pagination.
   *
   * @param userId the user ID
   * @param pageable pagination information
   * @return page of audit logs for the user
   */
  Page<UserAudit> findByUserId(Long userId, Pageable pageable);

  /**
   * Finds audit logs by username.
   *
   * @param username the username
   * @return list of audit logs for the username
   */
  List<UserAudit> findByUsername(String username);

  /**
   * Finds audit logs by action type.
   *
   * @param actionType the action type
   * @return list of audit logs with the action type
   */
  List<UserAudit> findByActionType(UserAudit.AuditActionType actionType);

  /**
   * Finds audit logs by action type with pagination.
   *
   * @param actionType the action type
   * @param pageable pagination information
   * @return page of audit logs with the action type
   */
  Page<UserAudit> findByActionType(UserAudit.AuditActionType actionType, Pageable pageable);

  /**
   * Finds audit logs by IP address.
   *
   * @param ipAddress the IP address
   * @return list of audit logs from the IP address
   */
  List<UserAudit> findByIpAddress(String ipAddress);

  /**
   * Finds audit logs by success status.
   *
   * @param success the success status
   * @return list of audit logs with the success status
   */
  List<UserAudit> findBySuccess(boolean success);

  /**
   * Finds audit logs created after a specific date.
   *
   * @param date the date to search from
   * @return list of audit logs created after the date
   */
  List<UserAudit> findByCreatedAtAfter(LocalDateTime date);

  /**
   * Finds audit logs created between two dates.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of audit logs created between the dates
   */
  List<UserAudit> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

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
  Page<UserAudit> findByCriteria(Long userId, UserAudit.AuditActionType actionType, 
                                 Boolean success, LocalDateTime startDate, LocalDateTime endDate, 
                                 Pageable pageable);

  /**
   * Finds failed login attempts by IP address.
   *
   * @param ipAddress the IP address
   * @param startDate the start date
   * @return list of failed login attempts from the IP address
   */
  List<UserAudit> findFailedLoginAttemptsByIp(String ipAddress, LocalDateTime startDate);

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
   * @param pageable pagination information
   * @return list of recent audit logs for the user
   */
  List<UserAudit> findRecentByUserId(Long userId, Pageable pageable);

  /**
   * Finds suspicious activities (multiple failed attempts from same IP).
   *
   * @param ipAddress the IP address
   * @param startDate the start date
   * @param minAttempts the minimum number of attempts
   * @return list of suspicious activities
   */
  List<UserAudit> findSuspiciousActivities(String ipAddress, LocalDateTime startDate, long minAttempts);

  /**
   * Deletes audit logs older than a specific date (for cleanup).
   *
   * @param date the date to delete logs before
   * @return number of deleted audit logs
   */
  long deleteByCreatedAtBefore(LocalDateTime date);

  /**
   * Counts the total number of audit logs.
   *
   * @return the total number of audit logs
   */
  long count();
}
