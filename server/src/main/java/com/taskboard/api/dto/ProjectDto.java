package com.taskboard.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taskboard.api.constants.ProjectConstants;
import com.taskboard.api.enums.ProjectPriority;
import com.taskboard.api.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
  private String id;

  @NotBlank(message = "Project name is required")
  @Size(min = 3, max = ProjectConstants.MAX_NAME_LENGTH, message = "Project name must be between 3 and 100 characters")
  private String name;

  @Size(max = ProjectConstants.MAX_DESCRIPTION_LENGTH, message = "Description cannot exceed 500 characters")
  private String description;

  private ProjectStatus status;
  private ProjectPriority priority;
  private String color;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime endDate;

  private List<String> tags;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;
}
