import { createFeatureSelector, createSelector } from '@ngrx/store';
import { Project } from '@models';
import { ProjectListState } from '../models/project-list.model';

// Feature selector
export const selectProjectListState = createFeatureSelector<ProjectListState>('projectList');

// Базовые селекторы
export const selectAllProjects = createSelector(
  selectProjectListState,
  (state: ProjectListState) => state.projects
);

export const selectProjectListLoading = createSelector(
  selectProjectListState,
  (state: ProjectListState) => state.loading
);

export const selectProjectListError = createSelector(
  selectProjectListState,
  (state: ProjectListState) => state.error
);

export const selectProjectListFilters = createSelector(
  selectProjectListState,
  (state: ProjectListState) => state.filters
);

export const selectProjectListSort = createSelector(
  selectProjectListState,
  (state: ProjectListState) => state.sort
);

export const selectProjectListPagination = createSelector(
  selectProjectListState,
  (state: ProjectListState) => state.pagination
);

export const selectSelectedProjectIds = createSelector(
  selectProjectListState,
  (state: ProjectListState) => state.selectedProjectIds
);

// Вычисляемые селекторы
export const selectProjectsCount = createSelector(
  selectAllProjects,
  (projects: Project[]) => projects.length
);

export const selectTotalProjectsCount = createSelector(
  selectProjectListPagination,
  (pagination) => pagination.total
);

export const selectHasMoreProjects = createSelector(
  selectProjectListPagination,
  (pagination) => pagination.hasMore
);

export const selectCurrentPage = createSelector(
  selectProjectListPagination,
  (pagination) => pagination.page
);

export const selectPageSize = createSelector(
  selectProjectListPagination,
  (pagination) => pagination.pageSize
);

export const selectSelectedProjectsCount = createSelector(
  selectSelectedProjectIds,
  (selectedIds: string[]) => selectedIds.length
);

export const selectIsAllSelected = createSelector(
  selectAllProjects,
  selectSelectedProjectIds,
  (projects: Project[], selectedIds: string[]) => {
    if (projects.length === 0) return false;
    return projects.every(project => selectedIds.includes(project.id));
  }
);

export const selectIsPartiallySelected = createSelector(
  selectAllProjects,
  selectSelectedProjectIds,
  (projects: Project[], selectedIds: string[]) => {
    if (projects.length === 0 || selectedIds.length === 0) return false;
    return selectedIds.length > 0 && selectedIds.length < projects.length;
  }
);

// Селекторы для фильтрации
export const selectSearchTerm = createSelector(
  selectProjectListFilters,
  (filters) => filters.searchTerm
);

export const selectStatusFilter = createSelector(
  selectProjectListFilters,
  (filters) => filters.status
);

export const selectDateRangeFilter = createSelector(
  selectProjectListFilters,
  (filters) => filters.dateRange
);

// Селекторы для сортировки
export const selectSortField = createSelector(
  selectProjectListSort,
  (sort) => sort.field
);

export const selectSortDirection = createSelector(
  selectProjectListSort,
  (sort) => sort.direction
);

// Селекторы для проектов с дополнительной информацией
export const selectProjectsWithSelection = createSelector(
  selectAllProjects,
  selectSelectedProjectIds,
  (projects: Project[], selectedIds: string[]) =>
    projects.map(project => ({
      project,
      isSelected: selectedIds.includes(project.id),
      isHovered: false
    }))
);

export const selectSelectedProjects = createSelector(
  selectAllProjects,
  selectSelectedProjectIds,
  (projects: Project[], selectedIds: string[]) =>
    projects.filter(project => selectedIds.includes(project.id))
);

export const selectProjectById = (projectId: string) => createSelector(
  selectAllProjects,
  (projects: Project[]) => projects.find(project => project.id === projectId)
);

// Селекторы для UI состояния
export const selectIsLoading = createSelector(
  selectProjectListLoading,
  (loading) => loading
);

export const selectHasError = createSelector(
  selectProjectListError,
  (error) => error !== null
);

export const selectErrorMessage = createSelector(
  selectProjectListError,
  (error) => error
);

// Селекторы для пагинации
export const selectPaginationInfo = createSelector(
  selectProjectListPagination,
  selectProjectsCount,
  (pagination, currentCount) => ({
    currentPage: pagination.page,
    pageSize: pagination.pageSize,
    total: pagination.total,
    hasMore: pagination.hasMore,
    currentCount,
    totalPages: Math.ceil(pagination.total / pagination.pageSize)
  })
);

// Селекторы для фильтров
export const selectActiveFilters = createSelector(
  selectProjectListFilters,
  (filters) => {
    const activeFilters: string[] = [];

    if (filters.searchTerm) {
      activeFilters.push(`Поиск: "${filters.searchTerm}"`);
    }

    if (filters.status && filters.status !== 'all') {
      activeFilters.push(`Статус: ${filters.status}`);
    }

    if (filters.dateRange.start || filters.dateRange.end) {
      const dateRange = [];
      if (filters.dateRange.start) {
        dateRange.push(`с ${filters.dateRange.start.toLocaleDateString()}`);
      }
      if (filters.dateRange.end) {
        dateRange.push(`по ${filters.dateRange.end.toLocaleDateString()}`);
      }
      activeFilters.push(`Дата: ${dateRange.join(' ')}`);
    }

    return activeFilters;
  }
);

export const selectHasActiveFilters = createSelector(
  selectActiveFilters,
  (activeFilters) => activeFilters.length > 0
);

// Селекторы для статистики
export const selectProjectsStats = createSelector(
  selectAllProjects,
  (projects: Project[]) => {
    const stats = {
      total: projects.length,
      active: 0,
      archived: 0,
      completed: 0,
      onHold: 0
    };

    projects.forEach(project => {
      const status = project.status ?? 'active';
      switch (status) {
        case 'active':
          stats.active++;
          break;
        case 'archived':
          stats.archived++;
          break;
        case 'completed':
          stats.completed++;
          break;
        case 'on-hold':
          stats.onHold++;
          break;
        default:
          stats.active++;
      }
    });

    return stats;
  }
);

// Селекторы для группировки проектов
export const selectProjectsByStatus = createSelector(
  selectAllProjects,
  (projects: Project[]) => {
    const grouped: { [key: string]: Project[] } = {
      active: [],
      archived: [],
      completed: [],
      'on-hold': []
    };

    projects.forEach(project => {
      const status = project.status ?? 'active';
      if (grouped[status]) {
        grouped[status].push(project);
      } else {
        grouped['active'].push(project);
      }
    });

    return grouped;
  }
);

// Селекторы для поиска
export const selectFilteredProjects = createSelector(
  selectAllProjects,
  selectProjectListFilters,
  (projects: Project[], filters) => {
    return projects.filter(project => {
      // Фильтр по поисковому запросу
      if (filters.searchTerm) {
        const searchLower = filters.searchTerm.toLowerCase();
        const matchesSearch =
          project.name.toLowerCase().includes(searchLower) ||
          project.description.toLowerCase().includes(searchLower);

        if (!matchesSearch) return false;
      }

      // Фильтр по статусу
      if (filters.status && filters.status !== 'all') {
        const projectStatus = project.status ?? 'active';
        if (projectStatus !== filters.status) return false;
      }

      // Фильтр по дате
      if (filters.dateRange.start || filters.dateRange.end) {
        const projectDate = new Date(project.createdAt);

        if (filters.dateRange.start && projectDate < filters.dateRange.start) {
          return false;
        }

        if (filters.dateRange.end && projectDate > filters.dateRange.end) {
          return false;
        }
      }

      return true;
    });
  }
);

// Селекторы для сортировки
export const selectSortedProjects = createSelector(
  selectFilteredProjects,
  selectProjectListSort,
  (projects: Project[], sort) => {
    return [...projects].sort((a, b) => {
      // Безопасное получение значений с проверкой типов
      const getValue = (project: Project, field: string): unknown => {
        switch (field) {
          case 'name':
            return project.name;
          case 'description':
            return project.description;
          case 'status':
            return project.status;
          case 'createdAt':
            return project.createdAt;
          case 'updatedAt':
            return project.updatedAt;
          case 'id':
            return project.id;
          default:
            return null;
        }
      };

      let aValue = getValue(a, sort.field);
      let bValue = getValue(b, sort.field);

      // Обработка null/undefined значений
      aValue ??= '';
      bValue ??= '';

      // Обработка дат
      if (sort.field === 'createdAt' || sort.field === 'updatedAt') {
        aValue = new Date(aValue as string | number | Date).getTime();
        bValue = new Date(bValue as string | number | Date).getTime();
      }

      // Обработка строк
      if (typeof aValue === 'string' && typeof bValue === 'string') {
        aValue = aValue.toLowerCase();
        bValue = bValue.toLowerCase();
      }

      // Сравнение значений
      if ((aValue as string | number) < (bValue as string | number)) {
        return sort.direction === 'asc' ? -1 : 1;
      }
      if ((aValue as string | number) > (bValue as string | number)) {
        return sort.direction === 'asc' ? 1 : -1;
      }
      return 0;
    });
  }
);
