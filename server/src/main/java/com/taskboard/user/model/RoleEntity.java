package com.taskboard.user.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.*;
import static lombok.AccessLevel.PROTECTED;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entity representing a role in the system. This entity is for potential future expansion of
 * role-based access control.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RoleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(unique = true, nullable = false, length = 50)
  private String name;

  @Column(length = 255)
  private String description;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date createdAt;
}
