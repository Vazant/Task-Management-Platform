package com.taskboard.api.enums;

/**
 * Task priority enumeration for the task management system.
 * Defines the importance level of tasks for better organization and workflow.
 */
public enum TaskPriority {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    URGENT("URGENT");

    private final String value;

    TaskPriority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Get task priority by string value.
     * 
     * @param value string value to match
     * @return corresponding enum or null if not found
     */
    public static TaskPriority fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (TaskPriority priority : TaskPriority.values()) {
            if (priority.value.equals(value)) {
                return priority;
            }
        }
        return null;
    }
}
