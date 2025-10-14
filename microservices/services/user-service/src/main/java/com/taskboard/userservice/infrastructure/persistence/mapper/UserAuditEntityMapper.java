package com.taskboard.userservice.infrastructure.persistence.mapper;

import com.taskboard.userservice.domain.model.UserAudit;
import com.taskboard.userservice.infrastructure.persistence.entity.UserAuditEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.ArrayList;

/**
 * Mapper interface for converting between UserAudit domain model and UserAuditEntity JPA entity.
 *
 * <p>This mapper uses MapStruct to provide automatic mapping between domain and infrastructure
 * layers, ensuring clean separation of concerns and reducing boilerplate code.
 *
 * <p>The mapper handles:
 *
 * <ul>
 *   <li>Bidirectional conversion between domain and entity models
 *   <li>Enum mapping for audit action types
 *   <li>Null safety and validation
 *   <li>Collection mapping for bulk operations
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserAuditEntityMapper {

  UserAuditEntityMapper INSTANCE = Mappers.getMapper(UserAuditEntityMapper.class);

  /**
   * Converts UserAuditEntity to UserAudit domain model.
   *
   * @param userAuditEntity the JPA entity
   * @return the domain model
   */
  UserAudit toDomain(UserAuditEntity userAuditEntity);

  /**
   * Converts UserAudit domain model to UserAuditEntity.
   *
   * @param userAudit the domain model
   * @return the JPA entity
   */
  UserAuditEntity toEntity(UserAudit userAudit);

  /**
   * Converts a list of UserAuditEntity to a list of UserAudit domain models.
   *
   * @param userAuditEntities the list of JPA entities
   * @return the list of domain models
   */
  List<UserAudit> toDomainList(List<UserAuditEntity> userAuditEntities);

  /**
   * Converts a list of UserAudit domain models to a list of UserAuditEntity.
   *
   * @param userAudits the list of domain models
   * @return the list of JPA entities
   */
  List<UserAuditEntity> toEntityList(List<UserAudit> userAudits);

  /**
   * Updates UserAuditEntity from UserAudit domain model.
   *
   * @param userAudit the domain model
   * @param userAuditEntity the JPA entity to update
   */
  void updateEntityFromDomain(UserAudit userAudit, @MappingTarget UserAuditEntity userAuditEntity);
}
