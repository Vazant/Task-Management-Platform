package com.taskboard.api.service;

import com.taskboard.api.constants.ProjectConstants;
import com.taskboard.api.dto.CreateProjectRequest;
import com.taskboard.api.dto.ProjectDto;
import com.taskboard.api.dto.UpdateProjectRequest;
import com.taskboard.api.entity.Project;
import com.taskboard.api.repository.ProjectRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
public class ProjectService {

  private final ProjectRepository projectRepository;

  public List<ProjectDto> getProjectsByUser(final String username) {
    log.info("Getting projects for user: {}", username);
    final List<Project> projects = projectRepository.findByCreatedBy(username);
    return projects.stream().map(this::convertToDto).collect(Collectors.toList());
  }

  public ProjectDto getProjectById(final Long id, final String username) {
    log.info("Getting project by id: {} for user: {}", id, username);
    final Project project =
        projectRepository.findById(id).orElseThrow(() -> new RuntimeException(ProjectConstants.ERROR_PROJECT_NOT_FOUND));

    if (!project.getCreatedBy().equals(username)) {
      throw new RuntimeException(ProjectConstants.ERROR_ACCESS_DENIED);
    }

    return convertToDto(project);
  }

  @Transactional
  public ProjectDto createProject(final CreateProjectRequest request, final String username) {
    log.info("Creating project: {} for user: {}", request.getName(), username);

    final Project project = Project.create(
        request.getName(),
        request.getDescription(),
        request.getStatus(),
        request.getPriority(),
        request.getColor(),
        request.getStartDate(),
        request.getEndDate(),
        request.getTags() != null ? request.getTags() : List.of(),
        username
    );

    final Project savedProject = projectRepository.save(project);
    log.info("Project created successfully with id: {}", savedProject.getId());

    return convertToDto(savedProject);
  }

  @Transactional
  public ProjectDto updateProject(final Long id, final UpdateProjectRequest request, final String username) {
    log.info("Updating project: {} for user: {}", id, username);

    final Project project =
        projectRepository.findById(id).orElseThrow(() -> new RuntimeException(ProjectConstants.ERROR_PROJECT_NOT_FOUND));

    if (!project.getCreatedBy().equals(username)) {
      throw new RuntimeException(ProjectConstants.ERROR_ACCESS_DENIED);
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

    final Project savedProject = projectRepository.save(project);
    log.info("Project updated successfully");

    return convertToDto(savedProject);
  }

  @Transactional
  public void deleteProject(final Long id, final String username) {
    log.info("Deleting project: {} for user: {}", id, username);

    final Project project =
        projectRepository.findById(id).orElseThrow(() -> new RuntimeException(ProjectConstants.ERROR_PROJECT_NOT_FOUND));

    if (!project.getCreatedBy().equals(username)) {
      throw new RuntimeException(ProjectConstants.ERROR_ACCESS_DENIED);
    }

    projectRepository.delete(project);
    log.info("Project deleted successfully");
  }

  private ProjectDto convertToDto(final Project project) {
    final ProjectDto dto = new ProjectDto();
    dto.setId(project.getId().toString());
    dto.setName(project.getName());
    dto.setDescription(project.getDescription());
    dto.setStatus(project.getStatus());
    dto.setPriority(project.getPriority());
    dto.setColor(project.getColor());
    dto.setStartDate(project.getStartDate());
    dto.setEndDate(project.getEndDate());
    dto.setTags(project.getTags());
    dto.setCreatedAt(project.getCreatedAt());
    dto.setUpdatedAt(project.getUpdatedAt());
    dto.setCreatedBy(project.getCreatedBy());
    return dto;
  }
}
