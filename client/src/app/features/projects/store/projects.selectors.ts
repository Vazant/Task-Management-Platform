import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ProjectsState } from './projects.state';
import { projectsAdapter } from './projects.reducer';
import type { Project } from '@models';



export const selectProjectsState = createFeatureSelector<ProjectsState>('projects');

const { selectAll, selectEntities, selectIds } = projectsAdapter.getSelectors();

export const selectAllProjects = createSelector(selectProjectsState, selectAll);
export const selectProjectsEntities = createSelector(selectProjectsState, selectEntities);
export const selectProjectsIds = createSelector(selectProjectsState, selectIds);

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
