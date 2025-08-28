import { createReducer, on } from '@ngrx/store';
import { ProjectsState } from './projects.state';
import * as ProjectsActions from './projects.actions';
import { createEntityAdapter } from '@ngrx/entity';
import type { Project } from '@models';

export const projectsAdapter = createEntityAdapter<Project>({
  selectId: (p) => p.id,
  sortComparer: false,
});

export const initialState: ProjectsState = projectsAdapter.getInitialState({
  loading: false,
  error: null,
  selectedProjectId: null,
});

export const projectsReducer = createReducer(
  initialState,

  // Load Projects
  on(ProjectsActions.loadProjects, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(ProjectsActions.loadProjectsSuccess, (state, { projects }) =>
    projectsAdapter.setAll(projects, {
      ...state,
      loading: false,
      error: null,
    })
  ),

  on(ProjectsActions.loadProjectsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Create Project
  on(ProjectsActions.createProject, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(ProjectsActions.createProjectSuccess, (state, { project }) =>
    projectsAdapter.addOne(project, {
      ...state,
      loading: false,
      error: null,
    })
  ),

  on(ProjectsActions.createProjectFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Update Project
  on(ProjectsActions.updateProject, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(ProjectsActions.updateProjectSuccess, (state, { project }) =>
    projectsAdapter.upsertOne(project, {
      ...state,
      loading: false,
      error: null,
    })
  ),

  on(ProjectsActions.updateProjectFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Delete Project
  on(ProjectsActions.deleteProject, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(ProjectsActions.deleteProjectSuccess, (state, { projectId }) =>
    projectsAdapter.removeOne(projectId, {
      ...state,
      loading: false,
      error: null,
    })
  ),

  on(ProjectsActions.deleteProjectFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Select Project
  on(ProjectsActions.selectProject, (state, { projectId }) => ({
    ...state,
    selectedProjectId: projectId
  })),

  // Clear Error
  on(ProjectsActions.clearProjectsError, (state) => ({
    ...state,
    error: null
  }))
);
