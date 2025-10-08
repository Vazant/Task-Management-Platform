package com.taskboard.api.mapper;

import com.taskboard.api.dto.ProjectCreateRequest;
import com.taskboard.api.dto.ProjectDto;
import com.taskboard.api.dto.ProjectUpdateRequest;
import com.taskboard.api.entity.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Project entities and DTOs.
 * Handles all mapping operations for project-related data transfer objects.
 */
@Component
public class ProjectMapper {

    /**
     * Convert Project entity to ProjectDto.
     * 
     * @param project project entity
     * @return project DTO
     */
    public ProjectDto toDto(Project project) {
        if (project == null) {
            return null;
        }

        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .priority(project.getPriority())
                .color(project.getColor())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .tags(project.getTags())
                .createdBy(project.getCreatedBy())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    /**
     * Convert list of Project entities to list of ProjectDto.
     * 
     * @param projects list of project entities
     * @return list of project DTOs
     */
    public List<ProjectDto> toDtoList(List<Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert ProjectCreateRequest to Project entity.
     * 
     * @param request project creation request
     * @param createdBy project creator ID
     * @return project entity
     */
    public Project toEntity(ProjectCreateRequest request, String createdBy) {
        if (request == null) {
            return null;
        }

        return Project.create(
                request.getName(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                request.getColor(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTags(),
                createdBy
        );
    }

    /**
     * Update Project entity with data from ProjectUpdateRequest.
     * 
     * @param project existing project entity
     * @param request project update request
     */
    public void updateEntity(Project project, ProjectUpdateRequest request) {
        if (project == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            project.setPriority(request.getPriority());
        }
        if (request.getColor() != null) {
            project.setColor(request.getColor());
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }
        if (request.getTags() != null) {
            project.setTags(request.getTags());
        }
    }
}
