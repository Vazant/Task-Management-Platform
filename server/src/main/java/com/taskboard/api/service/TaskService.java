package com.taskboard.api.service;

import com.taskboard.api.constants.TaskConstants;
import com.taskboard.api.dto.AssignTaskRequest;
import com.taskboard.api.dto.ChangeTaskStatusRequest;
import com.taskboard.api.dto.TaskCreateRequest;
import com.taskboard.api.dto.TaskResponse;
import com.taskboard.api.dto.TaskUpdateRequest;
import com.taskboard.api.entity.Project;
import com.taskboard.api.entity.Task;
import com.taskboard.api.entity.User;
import com.taskboard.api.enums.TaskStatus;
import com.taskboard.api.mapper.TaskMapper;
import com.taskboard.api.repository.ProjectRepository;
import com.taskboard.api.repository.TaskRepository;
import com.taskboard.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for task management operations.
 * Handles business logic for task CRUD operations, assignments, and status changes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    /**
     * Get all tasks with pagination.
     * 
     * @param pageable pagination information
     * @return page of tasks
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        log.info("Retrieving all tasks with pagination: {}", pageable);
        Page<Task> tasks = taskRepository.findAll(pageable);
        return tasks.map(taskMapper::toResponse);
    }

    /**
     * Get all tasks by project ID.
     * 
     * @param projectId project identifier
     * @param pageable pagination information
     * @return page of tasks in the project
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByProject(Long projectId, Pageable pageable) {
        log.info("Retrieving tasks for project: {} with pagination: {}", projectId, pageable);
        Page<Task> tasks = taskRepository.findByProjectId(projectId, pageable);
        return tasks.map(taskMapper::toResponse);
    }

    /**
     * Get all tasks assigned to a user.
     * 
     * @param assigneeId user identifier
     * @param pageable pagination information
     * @return page of tasks assigned to the user
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByAssignee(Long assigneeId, Pageable pageable) {
        log.info("Retrieving tasks for assignee: {} with pagination: {}", assigneeId, pageable);
        Page<Task> tasks = taskRepository.findByAssigneeId(assigneeId, pageable);
        return tasks.map(taskMapper::toResponse);
    }

    /**
     * Get all tasks by status.
     * 
     * @param status task status
     * @param pageable pagination information
     * @return page of tasks with the specified status
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByStatus(TaskStatus status, Pageable pageable) {
        log.info("Retrieving tasks with status: {} with pagination: {}", status, pageable);
        Page<Task> tasks = taskRepository.findByStatus(status, pageable);
        return tasks.map(taskMapper::toResponse);
    }

    /**
     * Get task by ID.
     * 
     * @param id task identifier
     * @return task response DTO
     * @throws RuntimeException if task not found
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        log.info("Retrieving task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(TaskConstants.ERROR_TASK_NOT_FOUND));
        return taskMapper.toResponse(task);
    }

    /**
     * Create a new task.
     * 
     * @param request task creation request
     * @param creatorId task creator ID
     * @return created task response DTO
     * @throws RuntimeException if project or assignee not found
     */
    public TaskResponse createTask(TaskCreateRequest request, String creatorId) {
        log.info("Creating task: {} for creator: {}", request.getTitle(), creatorId);
        
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException(TaskConstants.ERROR_TASK_PROJECT_NOT_FOUND));
        
        Task task = taskMapper.toEntity(request, project, creatorId);
        
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException(TaskConstants.ERROR_TASK_ASSIGNEE_NOT_FOUND));
            task.setAssignee(assignee);
        }
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());
        return taskMapper.toResponse(savedTask);
    }

    /**
     * Update an existing task.
     * 
     * @param id task identifier
     * @param request task update request
     * @return updated task response DTO
     * @throws RuntimeException if task not found
     */
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        log.info("Updating task with ID: {}", id);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(TaskConstants.ERROR_TASK_NOT_FOUND));
        
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException(TaskConstants.ERROR_TASK_ASSIGNEE_NOT_FOUND));
        }
        
        taskMapper.updateEntity(task, request, assignee);
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully with ID: {}", updatedTask.getId());
        return taskMapper.toResponse(updatedTask);
    }

    /**
     * Delete a task.
     * 
     * @param id task identifier
     * @throws RuntimeException if task not found
     */
    public void deleteTask(Long id) {
        log.info("Deleting task with ID: {}", id);
        
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException(TaskConstants.ERROR_TASK_NOT_FOUND);
        }
        
        taskRepository.deleteById(id);
        log.info("Task deleted successfully with ID: {}", id);
    }

    /**
     * Assign a task to a user.
     * 
     * @param id task identifier
     * @param request assignment request
     * @return updated task response DTO
     * @throws RuntimeException if task or assignee not found
     */
    public TaskResponse assignTask(Long id, AssignTaskRequest request) {
        log.info("Assigning task {} to user {}", id, request.getAssigneeId());
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(TaskConstants.ERROR_TASK_NOT_FOUND));
        
        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new RuntimeException(TaskConstants.ERROR_TASK_ASSIGNEE_NOT_FOUND));
        
        task.setAssignee(assignee);
        Task updatedTask = taskRepository.save(task);
        
        log.info("Task {} assigned successfully to user {}", id, request.getAssigneeId());
        return taskMapper.toResponse(updatedTask);
    }

    /**
     * Change task status.
     * 
     * @param id task identifier
     * @param request status change request
     * @return updated task response DTO
     * @throws RuntimeException if task not found
     */
    public TaskResponse changeTaskStatus(Long id, ChangeTaskStatusRequest request) {
        log.info("Changing task {} status to {}", id, request.getStatus());
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(TaskConstants.ERROR_TASK_NOT_FOUND));
        
        task.setStatus(request.getStatus());
        Task updatedTask = taskRepository.save(task);
        
        log.info("Task {} status changed successfully to {}", id, request.getStatus());
        return taskMapper.toResponse(updatedTask);
    }

    /**
     * Search tasks by title or description.
     * 
     * @param searchTerm search term
     * @return list of matching tasks
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasks(String searchTerm) {
        log.info("Searching tasks with term: {}", searchTerm);
        List<Task> tasks = taskRepository.findByTitleOrDescriptionContaining(searchTerm, searchTerm);
        return taskMapper.toResponseList(tasks);
    }

    /**
     * Get overdue tasks.
     * 
     * @return list of overdue tasks
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        log.info("Retrieving overdue tasks");
        List<Task> tasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        return taskMapper.toResponseList(tasks);
    }

}
