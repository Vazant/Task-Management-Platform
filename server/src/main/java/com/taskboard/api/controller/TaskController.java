package com.taskboard.api.controller;

import com.taskboard.api.constants.AppConstants;
import com.taskboard.api.constants.TaskConstants;
import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.AssignTaskRequest;
import com.taskboard.api.dto.ChangeTaskStatusRequest;
import com.taskboard.api.dto.TaskCreateRequest;
import com.taskboard.api.dto.TaskResponse;
import com.taskboard.api.dto.TaskUpdateRequest;
import com.taskboard.api.enums.TaskStatus;
import com.taskboard.api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for task management operations.
 * Provides endpoints for CRUD operations, task assignment, and status management.
 */
@RestController
@RequestMapping(AppConstants.API_TASKS_PATH)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@SuppressWarnings("checkstyle:DesignForExtension")
@Tag(name = "Task Management", description = "APIs for managing tasks, assignments, and status updates")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    /**
     * Get all tasks with pagination and sorting.
     * 
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param sortBy sort field (default: createdAt)
     * @param sortDir sort direction (default: desc)
     * @param authentication user authentication
     * @return paginated list of tasks
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all tasks", description = "Retrieve all tasks with pagination and sorting options")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            final Authentication authentication) {
        try {
            log.info("Getting all tasks for user: {}", authentication.getName());
            
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<TaskDto> tasks = taskService.getAllTasks(pageable);
            return ResponseEntity.ok(
                    new ApiResponse<>(tasks, TaskConstants.SUCCESS_TASKS_RETRIEVED, true));
        } catch (Exception e) {
            log.error("Error getting tasks for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving tasks: " + e.getMessage(), false));
        }
    }

    /**
     * Get tasks by project ID.
     * 
     * @param projectId project identifier
     * @param page page number
     * @param size page size
     * @param authentication user authentication
     * @return paginated list of tasks in the project
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByProject(
            @PathVariable final Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            final Authentication authentication) {
        try {
            log.info("Getting tasks for project {} by user: {}", projectId, authentication.getName());
            
            Pageable pageable = PageRequest.of(page, size);
            Page<TaskDto> tasks = taskService.getTasksByProject(projectId, pageable);
            
            return ResponseEntity.ok(
                    new ApiResponse<>(tasks, TaskConstants.SUCCESS_TASKS_RETRIEVED, true));
        } catch (Exception e) {
            log.error("Error getting tasks for project {} by user: {}", projectId, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving project tasks: " + e.getMessage(), false));
        }
    }

    /**
     * Get tasks by status.
     * 
     * @param status task status
     * @param page page number
     * @param size page size
     * @param authentication user authentication
     * @return paginated list of tasks with the specified status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByStatus(
            @PathVariable final TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            final Authentication authentication) {
        try {
            log.info("Getting tasks with status {} for user: {}", status, authentication.getName());
            
            Pageable pageable = PageRequest.of(page, size);
            Page<TaskDto> tasks = taskService.getTasksByStatus(status, pageable);
            
            return ResponseEntity.ok(
                    new ApiResponse<>(tasks, TaskConstants.SUCCESS_TASKS_RETRIEVED, true));
        } catch (Exception e) {
            log.error("Error getting tasks with status {} for user: {}", status, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving tasks by status: " + e.getMessage(), false));
        }
    }

    /**
     * Get task by ID.
     * 
     * @param id task identifier
     * @param authentication user authentication
     * @return task details
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @PathVariable final Long id,
            final Authentication authentication) {
        try {
            log.info("Getting task {} for user: {}", id, authentication.getName());
            
            TaskDto task = taskService.getTaskById(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(task, TaskConstants.SUCCESS_TASK_RETRIEVED, true));
        } catch (Exception e) {
            log.error("Error getting task {} for user: {}", id, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving task: " + e.getMessage(), false));
        }
    }

    /**
     * Create a new task.
     * 
     * @param request task creation request
     * @param authentication user authentication
     * @return created task
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new task", description = "Create a new task with the provided details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Task created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request or validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody final TaskCreateRequest request,
            final Authentication authentication) {
        try {
            log.info("Creating task for user: {}", authentication.getName());
            
            // Get current user ID from authentication
            Long userId = getCurrentUserId(authentication);
            TaskDto task = taskService.createTask(request, userId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(task, TaskConstants.SUCCESS_TASK_CREATED, true));
        } catch (Exception e) {
            log.error("Error creating task for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error creating task: " + e.getMessage(), false));
        }
    }

    /**
     * Update an existing task.
     * 
     * @param id task identifier
     * @param request task update request
     * @param authentication user authentication
     * @return updated task
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable final Long id,
            @Valid @RequestBody final TaskUpdateRequest request,
            final Authentication authentication) {
        try {
            log.info("Updating task {} for user: {}", id, authentication.getName());
            
            TaskResponse task = taskService.updateTask(id, request);
            return ResponseEntity.ok(
                    new ApiResponse<>(task, TaskConstants.SUCCESS_TASK_UPDATED, true));
        } catch (Exception e) {
            log.error("Error updating task {} for user: {}", id, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error updating task: " + e.getMessage(), false));
        }
    }

    /**
     * Delete a task.
     * 
     * @param id task identifier
     * @param authentication user authentication
     * @return success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable final Long id,
            final Authentication authentication) {
        try {
            log.info("Deleting task {} for user: {}", id, authentication.getName());
            
            taskService.deleteTask(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(null, TaskConstants.SUCCESS_TASK_DELETED, true));
        } catch (Exception e) {
            log.error("Error deleting task {} for user: {}", id, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error deleting task: " + e.getMessage(), false));
        }
    }

    /**
     * Assign a task to a user.
     * 
     * @param id task identifier
     * @param request assignment request
     * @param authentication user authentication
     * @return updated task
     */
    @PutMapping("/{id}/assign")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(
            @PathVariable final Long id,
            @Valid @RequestBody final AssignTaskRequest request,
            final Authentication authentication) {
        try {
            log.info("Assigning task {} to user {} by: {}", id, request.getAssigneeId(), authentication.getName());
            
            TaskResponse task = taskService.assignTask(id, request);
            return ResponseEntity.ok(
                    new ApiResponse<>(task, TaskConstants.SUCCESS_TASK_ASSIGNED, true));
        } catch (Exception e) {
            log.error("Error assigning task {} for user: {}", id, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error assigning task: " + e.getMessage(), false));
        }
    }

    /**
     * Change task status.
     * 
     * @param id task identifier
     * @param request status change request
     * @param authentication user authentication
     * @return updated task
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TaskResponse>> changeTaskStatus(
            @PathVariable final Long id,
            @Valid @RequestBody final ChangeTaskStatusRequest request,
            final Authentication authentication) {
        try {
            log.info("Changing task {} status to {} by user: {}", id, request.getStatus(), authentication.getName());
            
            TaskResponse task = taskService.changeTaskStatus(id, request);
            return ResponseEntity.ok(
                    new ApiResponse<>(task, TaskConstants.SUCCESS_TASK_STATUS_CHANGED, true));
        } catch (Exception e) {
            log.error("Error changing task {} status for user: {}", id, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error changing task status: " + e.getMessage(), false));
        }
    }

    /**
     * Search tasks by title or description.
     * 
     * @param q search query
     * @param authentication user authentication
     * @return list of matching tasks
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> searchTasks(
            @RequestParam final String q,
            final Authentication authentication) {
        try {
            log.info("Searching tasks with query '{}' for user: {}", q, authentication.getName());
            
            List<TaskResponse> tasks = taskService.searchTasks(q);
            return ResponseEntity.ok(
                    new ApiResponse<>(tasks, "Search completed successfully", true));
        } catch (Exception e) {
            log.error("Error searching tasks for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error searching tasks: " + e.getMessage(), false));
        }
    }

    /**
     * Get overdue tasks.
     * 
     * @param authentication user authentication
     * @return list of overdue tasks
     */
    @GetMapping("/overdue")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks(
            final Authentication authentication) {
        try {
            log.info("Getting overdue tasks for user: {}", authentication.getName());
            
            List<TaskResponse> tasks = taskService.getOverdueTasks();
            return ResponseEntity.ok(
                    new ApiResponse<>(tasks, "Overdue tasks retrieved successfully", true));
        } catch (Exception e) {
            log.error("Error getting overdue tasks for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving overdue tasks: " + e.getMessage(), false));
        }
    }

    /**
     * Get current user ID from authentication.
     * 
     * @param authentication user authentication
     * @return user ID
     */
    private String getCurrentUserId(Authentication authentication) {
        // Extract username from authentication
        return authentication.getName();
    }
}
