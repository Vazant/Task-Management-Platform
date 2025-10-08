package com.taskboard.api.repository;

import com.taskboard.api.entity.Task;
import com.taskboard.api.enums.TaskPriority;
import com.taskboard.api.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity.
 * Provides data access methods for task management operations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks by project ID.
     * 
     * @param projectId project identifier
     * @return list of tasks in the project
     */
    List<Task> findByProjectId(Long projectId);

    /**
     * Find all tasks by project ID with pagination.
     * 
     * @param projectId project identifier
     * @param pageable pagination information
     * @return page of tasks in the project
     */
    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    /**
     * Find all tasks assigned to a user.
     * 
     * @param assigneeId user identifier
     * @return list of tasks assigned to the user
     */
    List<Task> findByAssigneeId(Long assigneeId);

    /**
     * Find all tasks assigned to a user with pagination.
     * 
     * @param assigneeId user identifier
     * @param pageable pagination information
     * @return page of tasks assigned to the user
     */
    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    /**
     * Find all tasks by status.
     * 
     * @param status task status
     * @return list of tasks with the specified status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Find all tasks by status with pagination.
     * 
     * @param status task status
     * @param pageable pagination information
     * @return page of tasks with the specified status
     */
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    /**
     * Find all tasks by priority.
     * 
     * @param priority task priority
     * @return list of tasks with the specified priority
     */
    List<Task> findByPriority(TaskPriority priority);

    /**
     * Find all tasks by project and status.
     * 
     * @param projectId project identifier
     * @param status task status
     * @return list of tasks in the project with the specified status
     */
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    /**
     * Find all tasks by project and assignee.
     * 
     * @param projectId project identifier
     * @param assigneeId user identifier
     * @return list of tasks in the project assigned to the user
     */
    List<Task> findByProjectIdAndAssigneeId(Long projectId, Long assigneeId);

    /**
     * Find all tasks by assignee and status.
     * 
     * @param assigneeId user identifier
     * @param status task status
     * @return list of tasks assigned to the user with the specified status
     */
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    /**
     * Find overdue tasks.
     * 
     * @param currentTime current timestamp
     * @return list of overdue tasks
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentTime AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find tasks by title containing the search term.
     * 
     * @param title search term
     * @return list of tasks with matching title
     */
    List<Task> findByTitleContainingIgnoreCase(String title);

    /**
     * Find tasks by description containing the search term.
     * 
     * @param description search term
     * @return list of tasks with matching description
     */
    List<Task> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Find tasks by title or description containing the search term.
     * 
     * @param title search term for title
     * @param description search term for description
     * @return list of tasks with matching title or description
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<Task> findByTitleOrDescriptionContaining(@Param("title") String title, 
                                                  @Param("description") String description);

    /**
     * Count tasks by project.
     * 
     * @param projectId project identifier
     * @return number of tasks in the project
     */
    long countByProjectId(Long projectId);

    /**
     * Count tasks by status.
     * 
     * @param status task status
     * @return number of tasks with the specified status
     */
    long countByStatus(TaskStatus status);

    /**
     * Count tasks by assignee.
     * 
     * @param assigneeId user identifier
     * @return number of tasks assigned to the user
     */
    long countByAssigneeId(Long assigneeId);

    /**
     * Find task by ID and project ID for security validation.
     * 
     * @param id task identifier
     * @param projectId project identifier
     * @return optional task if found and belongs to the project
     */
    Optional<Task> findByIdAndProjectId(Long id, Long projectId);
}
