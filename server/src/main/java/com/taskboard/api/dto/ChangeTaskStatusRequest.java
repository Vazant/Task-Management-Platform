package com.taskboard.api.dto;

import com.taskboard.api.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for changing task status.
 * Contains the new status for the task.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeTaskStatusRequest {

    @NotNull(message = "Task status is required")
    private TaskStatus status;
}
