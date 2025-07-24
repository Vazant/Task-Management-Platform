import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ProjectsState } from './projects.state';

export const selectProjectsState = createFeatureSelector<ProjectsState>('projects');

export const selectAllProjects = createSelector(
  selectProjectsState,
  (state) => state.ids.map(id => state.entities[id]).filter(Boolean)
);

export const selectProjectsEntities = createSelector(
  selectProjectsState,
  (state) => state.entities
);

export const selectProjectsIds = createSelector(
  selectProjectsState,
  (state) => state.ids
);

export const selectProjectsLoading = createSelector(
  selectProjectsState,
  (state) => state.loading
);

export const selectProjectsError = createSelector(
  selectProjectsState,
  (state) => state.error
);

export const selectSelectedProjectId = createSelector(
  selectProjectsState,
  (state) => state.selectedProjectId
);

export const selectSelectedProject = createSelector(
  selectProjectsEntities,
  selectSelectedProjectId,
  (entities, selectedId) => selectedId ? entities[selectedId] : null
);

export const selectProjectById = (projectId: string) => createSelector(
  selectProjectsEntities,
  (entities) => entities[projectId] || null
);

export const selectProjectsByOwner = (ownerId: string) => createSelector(
  selectAllProjects,
  (projects) => projects.filter(project => project.ownerId === ownerId)
);

export const selectProjectsByMember = (memberId: string) => createSelector(
  selectAllProjects,
  (projects) => projects.filter(project =>
    project.ownerId === memberId || project.members.includes(memberId)
  )
);
