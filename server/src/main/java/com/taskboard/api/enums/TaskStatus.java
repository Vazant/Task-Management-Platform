package com.taskboard.api.enums;

/**
 * Task status enumeration for the task management system.
 * Represents the current state of a task in its lifecycle.
 */
public enum TaskStatus {
    TODO("TODO"),
    IN_PROGRESS("IN_PROGRESS"),
    IN_REVIEW("IN_REVIEW"),
    DONE("DONE"),
    CANCELLED("CANCELLED");

    private final String value;

    TaskStatus(String value) {
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
     * Get task status by string value.
     * 
     * @param value string value to match
     * @return corresponding enum or null if not found
     */
    public static TaskStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (TaskStatus status : TaskStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}
