package com.taskboard.api.constants;

/**
 * Constants for task management functionality.
 * Contains validation constraints, default values, and success/error messages.
 */
public final class TaskConstants {

    private TaskConstants() {
        // Utility class
    }

    // Task validation constraints
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MIN_TITLE_LENGTH = 1;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_ESTIMATED_HOURS = 9999;
    public static final int MIN_ESTIMATED_HOURS = 0;

    // Default values
    public static final String DEFAULT_STATUS = "TODO";
    public static final String DEFAULT_PRIORITY = "MEDIUM";

    // Success messages
    public static final String SUCCESS_TASKS_RETRIEVED = "Tasks retrieved successfully";
    public static final String SUCCESS_TASK_RETRIEVED = "Task retrieved successfully";
    public static final String SUCCESS_TASK_CREATED = "Task created successfully";
    public static final String SUCCESS_TASK_UPDATED = "Task updated successfully";
    public static final String SUCCESS_TASK_DELETED = "Task deleted successfully";
    public static final String SUCCESS_TASK_ASSIGNED = "Task assigned successfully";
    public static final String SUCCESS_TASK_STATUS_CHANGED = "Task status changed successfully";

    // Error messages
    public static final String ERROR_TASK_NOT_FOUND = "Task not found";
    public static final String ERROR_TASK_ACCESS_DENIED = "Access denied to task";
    public static final String ERROR_TASK_INVALID_STATUS = "Invalid task status";
    public static final String ERROR_TASK_INVALID_PRIORITY = "Invalid task priority";
    public static final String ERROR_TASK_ASSIGNEE_NOT_FOUND = "Assignee not found";
    public static final String ERROR_TASK_PROJECT_NOT_FOUND = "Project not found";
    public static final String ERROR_TASK_CREATION_FAILED = "Failed to create task";
    public static final String ERROR_TASK_UPDATE_FAILED = "Failed to update task";
    public static final String ERROR_TASK_DELETE_FAILED = "Failed to delete task";
}
