package com.taskboard.api.controller;

import com.taskboard.api.constants.AppConstants;
import com.taskboard.api.constants.ProjectConstants;
import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.ProjectCreateRequest;
import com.taskboard.api.dto.ProjectDto;
import com.taskboard.api.dto.ProjectUpdateRequest;
import com.taskboard.api.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for project management operations.
 * Provides endpoints for CRUD operations and project management.
 */
@RestController
@RequestMapping(AppConstants.API_PROJECTS_PATH)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@SuppressWarnings("checkstyle:DesignForExtension")
@Tag(name = "Project Management", description = "APIs for managing projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

  private final ProjectService projectService;

  /**
   * Get all projects for the authenticated user with pagination and sorting.
   * 
   * @param page page number (default: 0)
   * @param size page size (default: 10)
   * @param sortBy sort field (default: createdAt)
   * @param sortDir sort direction (default: desc)
   * @param authentication user authentication
   * @return paginated list of projects
   */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Get all projects", description = "Retrieve all projects for the authenticated user with pagination")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ResponseEntity<ApiResponse<Page<ProjectDto>>> getProjects(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir,
      final Authentication authentication) {
    try {
      log.info("Getting projects for user: {} with pagination: page={}, size={}", authentication.getName(), page, size);
      
      Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
      Pageable pageable = PageRequest.of(page, size, sort);
      
      final Page<ProjectDto> projects = projectService.getProjectsByUser(authentication.getName(), pageable);
      return ResponseEntity.ok(
          new ApiResponse<>(projects, ProjectConstants.SUCCESS_PROJECTS_RETRIEVED, true));
    } catch (Exception e) {
      log.error("Error getting projects for user: {}", authentication.getName(), e);
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(null, "Error retrieving projects: " + e.getMessage(), false));
    }
  }

  /**
   * Get a specific project by ID.
   * 
   * @param id project identifier
   * @param authentication user authentication
   * @return project details
   */
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Get project by ID", description = "Retrieve a specific project by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ResponseEntity<ApiResponse<ProjectDto>> getProject(
      @Parameter(description = "Project ID", required = true) @PathVariable final Long id, 
      final Authentication authentication) {
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

  /**
   * Create a new project.
   * 
   * @param request project creation request
   * @param authentication user authentication
   * @return created project
   */
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Create project", description = "Create a new project")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Project created successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request or validation error"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ResponseEntity<ApiResponse<ProjectDto>> createProject(
      @Valid @RequestBody final ProjectCreateRequest request, final Authentication authentication) {
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

  /**
   * Update an existing project.
   * 
   * @param id project identifier
   * @param request project update request
   * @param authentication user authentication
   * @return updated project
   */
  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Update project", description = "Update an existing project")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request or validation error"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ResponseEntity<ApiResponse<ProjectDto>> updateProject(
      @Parameter(description = "Project ID", required = true) @PathVariable final Long id,
      @Valid @RequestBody final ProjectUpdateRequest request,
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

  /**
   * Delete a project.
   * 
   * @param id project identifier
   * @param authentication user authentication
   * @return success response
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Delete project", description = "Delete a project")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project deleted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ResponseEntity<ApiResponse<Void>> deleteProject(
      @Parameter(description = "Project ID", required = true) @PathVariable final Long id, 
      final Authentication authentication) {
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
