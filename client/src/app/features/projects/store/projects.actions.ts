import { createAction, props } from '@ngrx/store';
import { Project, CreateProjectRequest, UpdateProjectRequest } from '../../core/models/project.model';

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