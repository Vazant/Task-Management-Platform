package com.taskboard.api.controller;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.CreateProjectRequest;
import com.taskboard.api.dto.ProjectDto;
import com.taskboard.api.dto.UpdateProjectRequest;
import com.taskboard.api.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProjectController {
    
    private final ProjectService projectService;
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ProjectDto>>> getProjects(Authentication authentication) {
        try {
            log.info("Getting projects for user: {}", authentication.getName());
            List<ProjectDto> projects = projectService.getProjectsByUser(authentication.getName());
            return ResponseEntity.ok(new ApiResponse<>(projects, "Projects retrieved successfully", true));
        } catch (Exception e) {
            log.error("Error getting projects for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving projects: " + e.getMessage(), false));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProjectDto>> getProject(@PathVariable Long id, Authentication authentication) {
        try {
            log.info("Getting project {} for user: {}", id, authentication.getName());
            ProjectDto project = projectService.getProjectById(id, authentication.getName());
            return ResponseEntity.ok(new ApiResponse<>(project, "Project retrieved successfully", true));
        } catch (Exception e) {
            log.error("Error getting project {} for user: {}", id, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving project: " + e.getMessage(), false));
        }
    }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProjectDto>> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication) {
        try {
            log.info("Creating project for user: {}", authentication.getName());
            ProjectDto project = projectService.createProject(request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(project, "Project created successfully", true));
        } catch (Exception e) {
            log.error("Error creating project for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error creating project: " + e.getMessage(), false));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProjectDto>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request,
            Authentication authentication) {
        try {
            log.info("Updating project {} for user: {}", id, authentication.getName());
            ProjectDto project = projectService.updateProject(id, request, authentication.getName());
            return ResponseEntity.ok(new ApiResponse<>(project, "Project updated successfully", true));
        } catch (Exception e) {
            log.error("Error updating project {} for user: {}", id, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error updating project: " + e.getMessage(), false));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id, Authentication authentication) {
        try {
            log.info("Deleting project {} for user: {}", id, authentication.getName());
            projectService.deleteProject(id, authentication.getName());
            return ResponseEntity.ok(new ApiResponse<>(null, "Project deleted successfully", true));
        } catch (Exception e) {
            log.error("Error deleting project {} for user: {}", id, authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error deleting project: " + e.getMessage(), false));
        }
    }
}

