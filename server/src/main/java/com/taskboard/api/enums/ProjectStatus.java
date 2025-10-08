package com.taskboard.api.enums;

/**
 * Статусы проектов в системе.
 */
public enum ProjectStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    ON_HOLD("ON_HOLD");

    private final String value;

    ProjectStatus(String value) {
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
     * Получить статус по строковому значению.
     * 
     * @param value строковое значение
     * @return соответствующий enum или null если не найден
     */
    public static ProjectStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}






