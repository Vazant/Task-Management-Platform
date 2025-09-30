import { createAction, props } from '@ngrx/store';
import { Project, CreateProjectRequest, UpdateProjectRequest, ProjectFilters, ProjectStatus, ProjectPriority, ProjectHistoryEntry } from '../../core/models/project.model';

// Load Projects
export const loadProjects = createAction(
  '[Projects] Load Projects'
);

export const loadProjectsSuccess = createAction(
  '[Projects] Load Projects Success',
  props<{ projects: Project[] }>()
);

export const loadProjectsFailure = createAction(
  '[Projects] Load Projects Failure',
  props<{ error: string }>()
);

// Load Project by ID
export const loadProject = createAction(
  '[Projects] Load Project',
  props<{ id: string }>()
);

export const loadProjectSuccess = createAction(
  '[Projects] Load Project Success',
  props<{ project: Project }>()
);

export const loadProjectFailure = createAction(
  '[Projects] Load Project Failure',
  props<{ error: string }>()
);

// Create Project
export const createProject = createAction(
  '[Projects] Create Project',
  props<{ request: CreateProjectRequest }>()
);

export const createProjectSuccess = createAction(
  '[Projects] Create Project Success',
  props<{ project: Project }>()
);

export const createProjectFailure = createAction(
  '[Projects] Create Project Failure',
  props<{ error: string }>()
);

// Update Project
export const updateProject = createAction(
  '[Projects] Update Project',
  props<{ id: string; request: UpdateProjectRequest }>()
);

export const updateProjectSuccess = createAction(
  '[Projects] Update Project Success',
  props<{ project: Project }>()
);

export const updateProjectFailure = createAction(
  '[Projects] Update Project Failure',
  props<{ error: string }>()
);

// Delete Project
export const deleteProject = createAction(
  '[Projects] Delete Project',
  props<{ id: string }>()
);

export const deleteProjectSuccess = createAction(
  '[Projects] Delete Project Success',
  props<{ id: string }>()
);

export const deleteProjectFailure = createAction(
  '[Projects] Delete Project Failure',
  props<{ error: string }>()
);

// Clear Projects
export const clearProjects = createAction(
  '[Projects] Clear Projects'
);

// Set Selected Project
export const setSelectedProject = createAction(
  '[Projects] Set Selected Project',
  props<{ project: Project | null }>()
);

// Filter Projects
export const filterProjects = createAction(
  '[Projects] Filter Projects',
  props<{ filter: string }>()
);

// Search Projects
export const searchProjects = createAction(
  '[Projects] Search Projects',
  props<{ query: string }>()
);

// Advanced CRUD operations
export const loadProjectsPaginated = createAction(
  '[Projects] Load Projects Paginated',
  props<{ page: number; size: number; filters?: ProjectFilters }>()
);

export const loadProjectsPaginatedSuccess = createAction(
  '[Projects] Load Projects Paginated Success',
  props<{ 
    projects: Project[]; 
    totalElements: number; 
    totalPages: number; 
    currentPage: number 
  }>()
);

export const loadProjectsPaginatedFailure = createAction(
  '[Projects] Load Projects Paginated Failure',
  props<{ error: string }>()
);

export const loadProjectsByTags = createAction(
  '[Projects] Load Projects By Tags',
  props<{ tags: string[] }>()
);

export const loadProjectsByTagsSuccess = createAction(
  '[Projects] Load Projects By Tags Success',
  props<{ projects: Project[] }>()
);

export const loadProjectsByDateRange = createAction(
  '[Projects] Load Projects By Date Range',
  props<{ startDate: Date; endDate: Date }>()
);

export const loadProjectsByDateRangeSuccess = createAction(
  '[Projects] Load Projects By Date Range Success',
  props<{ projects: Project[] }>()
);

export const duplicateProject = createAction(
  '[Projects] Duplicate Project',
  props<{ projectId: string; newName?: string }>()
);

export const duplicateProjectSuccess = createAction(
  '[Projects] Duplicate Project Success',
  props<{ project: Project }>()
);

export const archiveProject = createAction(
  '[Projects] Archive Project',
  props<{ projectId: string }>()
);

export const archiveProjectSuccess = createAction(
  '[Projects] Archive Project Success',
  props<{ project: Project }>()
);

export const unarchiveProject = createAction(
  '[Projects] Unarchive Project',
  props<{ projectId: string }>()
);

export const unarchiveProjectSuccess = createAction(
  '[Projects] Unarchive Project Success',
  props<{ project: Project }>()
);

export const changeProjectStatus = createAction(
  '[Projects] Change Project Status',
  props<{ projectId: string; status: ProjectStatus }>()
);

export const changeProjectStatusSuccess = createAction(
  '[Projects] Change Project Status Success',
  props<{ project: Project }>()
);

export const changeProjectPriority = createAction(
  '[Projects] Change Project Priority',
  props<{ projectId: string; priority: ProjectPriority }>()
);

export const changeProjectPrioritySuccess = createAction(
  '[Projects] Change Project Priority Success',
  props<{ project: Project }>()
);

export const loadProjectHistory = createAction(
  '[Projects] Load Project History',
  props<{ projectId: string }>()
);

export const loadProjectHistorySuccess = createAction(
  '[Projects] Load Project History Success',
  props<{ projectId: string; history: ProjectHistoryEntry[] }>()
);

export const exportProjects = createAction(
  '[Projects] Export Projects',
  props<{ format: string; filters?: ProjectFilters }>()
);

export const exportProjectsSuccess = createAction(
  '[Projects] Export Projects Success',
  props<{ blob: Blob; filename: string }>()
);

export const importProjects = createAction(
  '[Projects] Import Projects',
  props<{ file: File }>()
);

export const importProjectsSuccess = createAction(
  '[Projects] Import Projects Success',
  props<{ success: number; errors: any[] }>()
);

export const bulkDeleteProjects = createAction(
  '[Projects] Bulk Delete Projects',
  props<{ projectIds: string[] }>()
);

export const bulkDeleteProjectsSuccess = createAction(
  '[Projects] Bulk Delete Projects Success',
  props<{ deletedIds: string[] }>()
);

export const bulkUpdateProjects = createAction(
  '[Projects] Bulk Update Projects',
  props<{ projectIds: string[]; updates: Partial<UpdateProjectRequest> }>()
);

export const bulkUpdateProjectsSuccess = createAction(
  '[Projects] Bulk Update Projects Success',
  props<{ projects: Project[] }>()
);