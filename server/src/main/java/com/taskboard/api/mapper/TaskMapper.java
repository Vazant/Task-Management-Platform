package com.taskboard.api.mapper;

import com.taskboard.api.dto.TaskCreateRequest;
import com.taskboard.api.dto.TaskResponse;
import com.taskboard.api.dto.TaskUpdateRequest;
import com.taskboard.api.entity.Task;
import com.taskboard.api.entity.Project;
import com.taskboard.api.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Task entities and DTOs.
 * Handles all mapping operations for task-related data transfer objects.
 */
@Component
public class TaskMapper {

    /**
     * Convert Task entity to TaskResponse DTO.
     * 
     * @param task task entity
     * @return task response DTO
     */
    public TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .assigneeName(task.getAssignee() != null ? task.getAssignee().getUsername() : null)
                .assigneeEmail(task.getAssignee() != null ? task.getAssignee().getEmail() : null)
                .creatorId(task.getCreatorId())
                .dueDate(task.getDueDate())
                .estimatedHours(task.getEstimatedHours())
                .actualHours(task.getActualHours())
                .isAssigned(task.isAssigned())
                .isOverdue(task.isOverdue())
                .isCompleted(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    /**
     * Convert list of Task entities to list of TaskResponse DTOs.
     * 
     * @param tasks list of task entities
     * @return list of task response DTOs
     */
    public List<TaskResponse> toResponseList(List<Task> tasks) {
        if (tasks == null) {
            return null;
        }
        return tasks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert TaskCreateRequest to Task entity.
     * 
     * @param request task creation request
     * @param project associated project
     * @param creatorId task creator ID
     * @return task entity
     */
    public Task toEntity(TaskCreateRequest request, Project project, String creatorId) {
        if (request == null) {
            return null;
        }

        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .project(project)
                .creatorId(creatorId)
                .status(request.getStatus() != null ? request.getStatus() : com.taskboard.api.enums.TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : com.taskboard.api.enums.TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .estimatedHours(request.getEstimatedHours())
                .actualHours(0)
                .build();
    }

    /**
     * Update Task entity with data from TaskUpdateRequest.
     * 
     * @param task existing task entity
     * @param request task update request
     * @param assignee assignee user (if provided)
     */
    public void updateEntity(Task task, TaskUpdateRequest request, User assignee) {
        if (task == null || request == null) {
            return;
        }

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (assignee != null) {
            task.setAssignee(assignee);
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getEstimatedHours() != null) {
            task.setEstimatedHours(request.getEstimatedHours());
        }
        if (request.getActualHours() != null) {
            task.setActualHours(request.getActualHours());
        }
    }
}
