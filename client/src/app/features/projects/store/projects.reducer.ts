import { createReducer, on } from '@ngrx/store';
import { Project } from '../../core/models/project.model';
import * as ProjectsActions from './projects.actions';

export interface ProjectsState {
  projects: Project[];
  selectedProject: Project | null;
  loading: boolean;
  error: string | null;
  filter: string;
  searchQuery: string;
  loaded: boolean;
}

export const initialState: ProjectsState = {
  projects: [],
  selectedProject: null,
  loading: false,
  error: null,
  filter: '',
  searchQuery: '',
  loaded: false
};

export const projectsReducer = createReducer(
  initialState,

  // Load Projects
  on(ProjectsActions.loadProjects, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(ProjectsActions.loadProjectsSuccess, (state, { projects }) => ({
    ...state,
    projects,
    loading: false,
    loaded: true,
    error: null
  })),

  on(ProjectsActions.loadProjectsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Load Project by ID
  on(ProjectsActions.loadProject, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(ProjectsActions.loadProjectSuccess, (state, { project }) => ({
    ...state,
    selectedProject: project,
    loading: false,
    error: null
  })),

  on(ProjectsActions.loadProjectFailure, (state, { error }) => ({
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
    projects: [...state.projects, project],
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
    projects: state.projects.map(p => p.id === project.id ? project : p),
    selectedProject: state.selectedProject?.id === project.id ? project : state.selectedProject,
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

  on(ProjectsActions.deleteProjectSuccess, (state, { id }) => ({
    ...state,
    projects: state.projects.filter(p => p.id !== id),
    selectedProject: state.selectedProject?.id === id ? null : state.selectedProject,
    loading: false,
    error: null
  })),

  on(ProjectsActions.deleteProjectFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Clear Projects
  on(ProjectsActions.clearProjects, (state) => ({
    ...state,
    projects: [],
    selectedProject: null,
    loaded: false
  })),

  // Set Selected Project
  on(ProjectsActions.setSelectedProject, (state, { project }) => ({
    ...state,
    selectedProject: project
  })),

  // Filter Projects
  on(ProjectsActions.filterProjects, (state, { filter }) => ({
    ...state,
    filter
  })),

  // Search Projects
  on(ProjectsActions.searchProjects, (state, { query }) => ({
    ...state,
    searchQuery: query
  }))
);