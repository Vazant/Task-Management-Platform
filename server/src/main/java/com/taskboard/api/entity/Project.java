package com.taskboard.api.entity;

import com.taskboard.api.constants.ProjectConstants;
import com.taskboard.api.enums.ProjectPriority;
import com.taskboard.api.enums.ProjectStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import static lombok.AccessLevel.PROTECTED;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "projects")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"tags"})
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(nullable = false, length = ProjectConstants.MAX_NAME_LENGTH)
  private String name;

  @Column(length = ProjectConstants.MAX_DESCRIPTION_LENGTH)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProjectStatus status = ProjectStatus.ACTIVE;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProjectPriority priority = ProjectPriority.MEDIUM;

  @Column(length = ProjectConstants.COLOR_LENGTH)
  private String color = ProjectConstants.DEFAULT_COLOR;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @ElementCollection
  @CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
  @Column(name = "tag")
  private List<String> tags = new ArrayList<>();

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public static Project create(final String name, final String description, final ProjectStatus status, 
                              final ProjectPriority priority, final String color, final LocalDateTime startDate, 
                              final LocalDateTime endDate, final List<String> tags, final String createdBy) {
    return Project.builder()
            .name(name)
            .description(description)
            .status(status != null ? status : ProjectStatus.ACTIVE)
            .priority(priority != null ? priority : ProjectPriority.MEDIUM)
            .color(color != null ? color : ProjectConstants.DEFAULT_COLOR)
            .startDate(startDate)
            .endDate(endDate)
            .tags(tags != null ? tags : new ArrayList<>())
            .createdBy(createdBy)
            .build();
  }
}