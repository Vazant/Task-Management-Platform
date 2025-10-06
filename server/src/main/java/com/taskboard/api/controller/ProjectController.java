package com.taskboard.api.controller;

import com.taskboard.api.constants.AppConstants;
import com.taskboard.api.constants.ProjectConstants;
import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.CreateProjectRequest;
import com.taskboard.api.dto.ProjectDto;
import com.taskboard.api.dto.UpdateProjectRequest;
import com.taskboard.api.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.API_PROJECTS_PATH)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@SuppressWarnings("checkstyle:DesignForExtension")
public class ProjectController {

  private final ProjectService projectService;

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<ProjectDto>>> getProjects(final Authentication authentication) {
    try {
      log.info("Getting projects for user: {}", authentication.getName());
      final List<ProjectDto> projects = projectService.getProjectsByUser(authentication.getName());
      return ResponseEntity.ok(
          new ApiResponse<>(projects, ProjectConstants.SUCCESS_PROJECTS_RETRIEVED, true));
    } catch (Exception e) {
      log.error("Error getting projects for user: {}", authentication.getName(), e);
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(null, "Error retrieving projects: " + e.getMessage(), false));
    }
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<ProjectDto>> getProject(
      @PathVariable final Long id, final Authentication authentication) {
    try {
      log.info("Getting project {} for user: {}", id, authentication.getName());
      final ProjectDto project = projectService.getProjectById(id, authentication.getName());
      return ResponseEntity.ok(new ApiResponse<>(project, ProjectConstants.SUCCESS_PROJECT_RETRIEVED, true));
    } catch (Exception e) {
      log.error("Error getting project {} for user: {}", id, authentication.getName(), e);
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(null, "Error retrieving project: " + e.getMessage(), false));
    }
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<ProjectDto>> createProject(
      @Valid @RequestBody final CreateProjectRequest request, final Authentication authentication) {
    try {
      log.info("Creating project for user: {}", authentication.getName());
      final ProjectDto project = projectService.createProject(request, authentication.getName());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse<>(project, ProjectConstants.SUCCESS_PROJECT_CREATED, true));
    } catch (Exception e) {
      log.error("Error creating project for user: {}", authentication.getName(), e);
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(null, "Error creating project: " + e.getMessage(), false));
    }
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<ProjectDto>> updateProject(
      @PathVariable final Long id,
      @Valid @RequestBody final UpdateProjectRequest request,
      final Authentication authentication) {
    try {
      log.info("Updating project {} for user: {}", id, authentication.getName());
      final ProjectDto project = projectService.updateProject(id, request, authentication.getName());
      return ResponseEntity.ok(new ApiResponse<>(project, ProjectConstants.SUCCESS_PROJECT_UPDATED, true));
    } catch (Exception e) {
      log.error("Error updating project {} for user: {}", id, authentication.getName(), e);
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(null, "Error updating project: " + e.getMessage(), false));
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> deleteProject(
      @PathVariable final Long id, final Authentication authentication) {
    try {
      log.info("Deleting project {} for user: {}", id, authentication.getName());
      projectService.deleteProject(id, authentication.getName());
      return ResponseEntity.ok(new ApiResponse<>(null, ProjectConstants.SUCCESS_PROJECT_DELETED, true));
    } catch (Exception e) {
      log.error("Error deleting project {} for user: {}", id, authentication.getName(), e);
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(null, "Error deleting project: " + e.getMessage(), false));
    }
  }
}
