import { createReducer, on } from '@ngrx/store';
import { ProjectsState } from './projects.state';
import * as ProjectsActions from './projects.actions';

export const initialState: ProjectsState = {
  entities: {},
  ids: [],
  loading: false,
  error: null,
  selectedProjectId: null
};

export const projectsReducer = createReducer(
  initialState,

  // Load Projects
  on(ProjectsActions.loadProjects, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(ProjectsActions.loadProjectsSuccess, (state, { projects }) => {
    const entities = projects.reduce((acc, project) => ({
      ...acc,
      [project.id]: project
    }), {});
    const ids = projects.map(project => project.id);

    return {
      ...state,
      entities,
      ids,
      loading: false,
      error: null
    };
  }),

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

  on(ProjectsActions.createProjectSuccess, (state, { project }) => ({
    ...state,
    entities: {
      ...state.entities,
      [project.id]: project
    },
    ids: [...state.ids, project.id],
    loading: false,
    error: null
  })),

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

  on(ProjectsActions.updateProjectSuccess, (state, { project }) => ({
    ...state,
    entities: {
      ...state.entities,
      [project.id]: project
    },
    loading: false,
    error: null
  })),

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

  on(ProjectsActions.deleteProjectSuccess, (state, { projectId }) => {
    const { [projectId]: removed, ...entities } = state.entities;
    const ids = state.ids.filter(id => id !== projectId);

    return {
      ...state,
      entities,
      ids,
      loading: false,
      error: null
    };
  }),

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
