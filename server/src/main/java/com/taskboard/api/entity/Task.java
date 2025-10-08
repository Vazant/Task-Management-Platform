package com.taskboard.api.entity;

import com.taskboard.api.constants.TaskConstants;
import com.taskboard.api.enums.TaskPriority;
import com.taskboard.api.enums.TaskStatus;
import com.taskboard.user.model.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

/**
 * Task entity representing a task in the project management system.
 * Contains all necessary fields for task management including status, priority, and relationships.
 */
@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"project", "assignee"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Task title is required")
    @Size(min = TaskConstants.MIN_TITLE_LENGTH, max = TaskConstants.MAX_TITLE_LENGTH, 
          message = "Task title must be between 1 and 200 characters")
    @Column(nullable = false, length = TaskConstants.MAX_TITLE_LENGTH)
    private String title;

    @Size(max = TaskConstants.MAX_DESCRIPTION_LENGTH, 
          message = "Task description cannot exceed 1000 characters")
    @Column(length = TaskConstants.MAX_DESCRIPTION_LENGTH)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @NotNull(message = "Project is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @NotBlank(message = "Creator is required")
    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Min(value = TaskConstants.MIN_ESTIMATED_HOURS, message = "Estimated hours cannot be negative")
    @Max(value = TaskConstants.MAX_ESTIMATED_HOURS, message = "Estimated hours cannot exceed 9999")
    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Min(value = 0, message = "Actual hours cannot be negative")
    @Column(name = "actual_hours")
    private Integer actualHours = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Create a new task with basic information.
     * 
     * @param title task title
     * @param description task description
     * @param project associated project
     * @param creatorId task creator ID
     * @return new Task instance
     */
    public static Task create(String title, String description, Project project, String creatorId) {
        return Task.builder()
                .title(title)
                .description(description)
                .project(project)
                .creatorId(creatorId)
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .actualHours(0)
                .build();
    }

    /**
     * Check if task is assigned to a user.
     * 
     * @return true if task has an assignee
     */
    public boolean isAssigned() {
        return assignee != null;
    }

    /**
     * Check if task is overdue.
     * 
     * @return true if task has due date and it's in the past
     */
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDateTime.now()) && status != TaskStatus.DONE;
    }

    /**
     * Check if task is completed.
     * 
     * @return true if task status is DONE
     */
    public boolean isCompleted() {
        return status == TaskStatus.DONE;
    }
}
