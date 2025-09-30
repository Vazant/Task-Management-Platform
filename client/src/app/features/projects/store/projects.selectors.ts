import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ProjectsState } from './projects.reducer';
import { Project, ProjectStatus } from '@models/project.model';

export const selectProjectsState = createFeatureSelector<ProjectsState>('projects');

// Basic Selectors
export const selectAllProjects = createSelector(
  selectProjectsState,
  (state: ProjectsState) => state.projects
);

export const selectSelectedProject = createSelector(
  selectProjectsState,
  (state: ProjectsState) => state.selectedProject
);

export const selectProjectsLoading = createSelector(
  selectProjectsState,
  (state: ProjectsState) => state.loading
);

export const selectProjectsError = createSelector(
  selectProjectsState,
  (state: ProjectsState) => state.error
);

export const selectProjectsLoaded = createSelector(
  selectProjectsState,
  (state: ProjectsState) => state.loaded
);

export const selectProjectsFilter = createSelector(
  selectProjectsState,
  (state: ProjectsState) => state.filter
);

export const selectProjectsSearchQuery = createSelector(
  selectProjectsState,
  (state: ProjectsState) => state.searchQuery
);

// Computed Selectors
export const selectFilteredProjects = createSelector(
  selectAllProjects,
  selectProjectsFilter,
  selectProjectsSearchQuery,
  (projects, filter, searchQuery) => {
    let filteredProjects = [...projects];

    // Apply filter
    if (filter) {
      filteredProjects = filteredProjects.filter(project => 
        project.status === filter
      );
    }

    // Apply search
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      filteredProjects = filteredProjects.filter(project =>
        project.name.toLowerCase().includes(query) ||
        project.description?.toLowerCase().includes(query) ||
        project.tags?.some(tag => tag.toLowerCase().includes(query))
      );
    }

    return filteredProjects;
  }
);

export const selectProjectsByStatus = createSelector(
  selectAllProjects,
  (projects: Project[]) => {
    return projects.reduce((acc, project) => {
      const status = project.status;
      if (!acc[status]) {
        acc[status] = [];
      }
      acc[status].push(project);
      return acc;
    }, {} as Record<string, Project[]>);
  }
);

export const selectActiveProjects = createSelector(
  selectAllProjects,
  (projects: Project[]) => projects.filter(project => project.status === ProjectStatus.ACTIVE)
);

export const selectArchivedProjects = createSelector(
  selectAllProjects,
  (projects: Project[]) => projects.filter(project => project.status === ProjectStatus.ARCHIVED)
);

export const selectProjectById = (id: string) => createSelector(
  selectAllProjects,
  (projects: Project[]) => projects.find(project => project.id === id)
);

export const selectProjectsCount = createSelector(
  selectAllProjects,
  (projects: Project[]) => projects.length
);

export const selectActiveProjectsCount = createSelector(
  selectActiveProjects,
  (projects: Project[]) => projects.length
);

export const selectArchivedProjectsCount = createSelector(
  selectArchivedProjects,
  (projects: Project[]) => projects.length
);

// Statistics Selectors
export const selectProjectsStatistics = createSelector(
  selectAllProjects,
  (projects: Project[]) => {
    const total = projects.length;
    const active = projects.filter(p => p.status === ProjectStatus.ACTIVE).length;
    const archived = projects.filter(p => p.status === ProjectStatus.ARCHIVED).length;
    const completed = projects.filter(p => p.status === ProjectStatus.COMPLETED).length;

    return {
      total,
      active,
      archived,
      completed,
      completionRate: total > 0 ? (completed / total) * 100 : 0
    };
  }
);

// Recent Projects
export const selectRecentProjects = createSelector(
  selectAllProjects,
  (projects: Project[]) => {
    const sortedProjects = [...projects].sort((a, b) => 
      new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
    );
    return sortedProjects.slice(0, 5);
  }
);

// Projects by Priority
export const selectProjectsByPriority = createSelector(
  selectAllProjects,
  (projects: Project[]) => {
    return projects.reduce((acc, project) => {
      const priority = project.priority || 'MEDIUM';
      if (!acc[priority]) {
        acc[priority] = [];
      }
      acc[priority].push(project);
      return acc;
    }, {} as Record<string, Project[]>);
  }
);