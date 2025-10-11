package com.taskboard.api.constants;

/**
 * Константы для проектов.
 */
public final class ProjectConstants {

    private ProjectConstants() {
        // Utility class
    }

    // Project constraints
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    public static final int COLOR_LENGTH = 7;
    public static final int MAX_TAGS_COUNT = 10;
    public static final int MAX_TAG_LENGTH = 50;

    // Default values
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_PRIORITY = "MEDIUM";
    public static final String DEFAULT_COLOR = "#1976d2";

    // Error messages
    public static final String ERROR_PROJECT_NOT_FOUND = "Project not found";
    public static final String ERROR_ACCESS_DENIED = "Access denied";
    public static final String ERROR_PROJECT_CREATION_FAILED = "Failed to create project";
    public static final String ERROR_PROJECT_UPDATE_FAILED = "Failed to update project";
    public static final String ERROR_PROJECT_DELETE_FAILED = "Failed to delete project";

    // Success messages
    public static final String SUCCESS_PROJECT_CREATED = "Project created successfully";
    public static final String SUCCESS_PROJECT_UPDATED = "Project updated successfully";
    public static final String SUCCESS_PROJECT_DELETED = "Project deleted successfully";
    public static final String SUCCESS_PROJECT_RETRIEVED = "Project retrieved successfully";
    public static final String SUCCESS_PROJECTS_RETRIEVED = "Projects retrieved successfully";
}

















