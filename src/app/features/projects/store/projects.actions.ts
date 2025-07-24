import { createAction, props } from '@ngrx/store';
import { Project } from '../../../core/models';

// Load Projects
export const loadProjects = createAction('[Projects] Load Projects');

export const loadProjectsSuccess = createAction(
  '[Projects] Load Projects Success',
  props<{ projects: Project[] }>()
);

export const loadProjectsFailure = createAction(
  '[Projects] Load Projects Failure',
  props<{ error: string }>()
);

// Create Project
export const createProject = createAction(
  '[Projects] Create Project',
  props<{ project: Partial<Project> }>()
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
  props<{ project: Partial<Project> & { id: string } }>()
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
  props<{ projectId: string }>()
);

export const deleteProjectSuccess = createAction(
  '[Projects] Delete Project Success',
  props<{ projectId: string }>()
);

export const deleteProjectFailure = createAction(
  '[Projects] Delete Project Failure',
  props<{ error: string }>()
);

// Select Project
export const selectProject = createAction(
  '[Projects] Select Project',
  props<{ projectId: string }>()
);

// Clear Error
export const clearProjectsError = createAction('[Projects] Clear Error');
