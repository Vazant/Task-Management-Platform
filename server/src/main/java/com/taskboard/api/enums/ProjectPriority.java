package com.taskboard.api.enums;

/**
 * Приоритеты проектов в системе.
 */
public enum ProjectPriority {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    CRITICAL("CRITICAL");

    private final String value;

    ProjectPriority(String value) {
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
     * Получить приоритет по строковому значению.
     * 
     * @param value строковое значение
     * @return соответствующий enum или null если не найден
     */
    public static ProjectPriority fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ProjectPriority priority : ProjectPriority.values()) {
            if (priority.value.equals(value)) {
                return priority;
            }
        }
        return null;
    }
}

















