import { createReducer, on } from '@ngrx/store';
import {
  ProjectListState,
  ProjectStatus
} from '../models/project-list.model';
import * as ProjectListActions from './project-list.actions';

// Начальное состояние
export const initialState: ProjectListState = {
  projects: [],
  loading: false,
  error: null,
  filters: {
    searchTerm: '',
    status: 'all',
    ownerId: null,
    dateRange: {
      start: null,
      end: null
    }
  },
  sort: {
    field: 'createdAt',
    direction: 'desc'
  },
  pagination: {
    page: 1,
    pageSize: 12,
    total: 0,
    hasMore: true
  },
  selectedProjectIds: []
};

export const projectListReducer = createReducer(
  initialState,

  // Загрузка проектов
  on(ProjectListActions.loadProjects, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(ProjectListActions.loadProjectsSuccess, (state, { response, append }) => ({
    ...state,
    projects: append ? [...state.projects, ...response.projects] : response.projects,
    pagination: {
      ...state.pagination,
      total: response.total,
      hasMore: response.hasMore
    },
    loading: false,
    error: null
  })),

  on(ProjectListActions.loadProjectsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Загрузка следующей страницы
  on(ProjectListActions.loadNextPage, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(ProjectListActions.loadNextPageSuccess, (state, { response }) => ({
    ...state,
    projects: [...state.projects, ...response.projects],
    pagination: {
      ...state.pagination,
      page: state.pagination.page + 1,
      total: response.total,
      hasMore: response.hasMore
    },
    loading: false,
    error: null
  })),

  on(ProjectListActions.loadNextPageFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Обновление фильтров
  on(ProjectListActions.updateFilters, (state, { filters }) => ({
    ...state,
    filters: { ...state.filters, ...filters },
    pagination: {
      ...state.pagination,
      page: 1 // Сбрасываем на первую страницу при изменении фильтров
    },
    selectedProjectIds: [] // Сбрасываем выбор при изменении фильтров
  })),

  on(ProjectListActions.resetFilters, (state) => ({
    ...state,
    filters: initialState.filters,
    pagination: {
      ...state.pagination,
      page: 1
    },
    selectedProjectIds: []
  })),

  // Обновление сортировки
  on(ProjectListActions.updateSort, (state, { sort }) => ({
    ...state,
    sort,
    pagination: {
      ...state.pagination,
      page: 1
    },
    selectedProjectIds: []
  })),

  on(ProjectListActions.resetSort, (state) => ({
    ...state,
    sort: initialState.sort,
    pagination: {
      ...state.pagination,
      page: 1
    },
    selectedProjectIds: []
  })),

  // Обновление пагинации
  on(ProjectListActions.updatePagination, (state, { pagination }) => ({
    ...state,
    pagination: { ...state.pagination, ...pagination }
  })),

  on(ProjectListActions.resetPagination, (state) => ({
    ...state,
    pagination: initialState.pagination
  })),

  // Выбор проектов
  on(ProjectListActions.selectProject, (state, { projectId }) => ({
    ...state,
    selectedProjectIds: state.selectedProjectIds.includes(projectId)
      ? state.selectedProjectIds
      : [...state.selectedProjectIds, projectId]
  })),

  on(ProjectListActions.deselectProject, (state, { projectId }) => ({
    ...state,
    selectedProjectIds: state.selectedProjectIds.filter(id => id !== projectId)
  })),

  on(ProjectListActions.selectAllProjectsAction, (state) => ({
    ...state,
    selectedProjectIds: state.projects.map(project => project.id)
  })),

  on(ProjectListActions.deselectAllProjectsAction, (state) => ({
    ...state,
    selectedProjectIds: []
  })),

  on(ProjectListActions.toggleProjectSelection, (state, { projectId }) => ({
    ...state,
    selectedProjectIds: state.selectedProjectIds.includes(projectId)
      ? state.selectedProjectIds.filter(id => id !== projectId)
      : [...state.selectedProjectIds, projectId]
  })),

  // Создание проекта
  on(ProjectListActions.createProjectSuccess, (state, { project }) => ({
    ...state,
    projects: [project, ...state.projects],
    pagination: {
      ...state.pagination,
      total: state.pagination.total + 1
    }
  })),

  // Обновление проекта
  on(ProjectListActions.updateProjectSuccess, (state, { project }) => ({
    ...state,
    projects: state.projects.map(p => p.id === project.id ? project : p)
  })),

  // Удаление проекта
  on(ProjectListActions.deleteProjectSuccess, (state, { id }) => ({
    ...state,
    projects: state.projects.filter(p => p.id !== id),
    selectedProjectIds: state.selectedProjectIds.filter(projectId => projectId !== id),
    pagination: {
      ...state.pagination,
      total: Math.max(0, state.pagination.total - 1)
    }
  })),

  // Архивирование проекта
  on(ProjectListActions.archiveProjectSuccess, (state, { project }) => ({
    ...state,
    projects: state.projects.map(p => p.id === project.id ? project : p)
  })),

  // Дублирование проекта
  on(ProjectListActions.duplicateProjectSuccess, (state, { project }) => ({
    ...state,
    projects: [project, ...state.projects],
    pagination: {
      ...state.pagination,
      total: state.pagination.total + 1
    }
  })),

  // Установка состояния загрузки
  on(ProjectListActions.setLoading, (state, { loading }) => ({
    ...state,
    loading
  })),

  // Очистка ошибок
  on(ProjectListActions.clearError, (state) => ({
    ...state,
    error: null
  })),

  // Сброс состояния
  on(ProjectListActions.resetProjectList, () => initialState),

  // Обновление поиска
  on(ProjectListActions.updateSearchTerm, (state, { searchTerm }) => ({
    ...state,
    filters: {
      ...state.filters,
      searchTerm
    },
    pagination: {
      ...state.pagination,
      page: 1
    },
    selectedProjectIds: []
  })),

  // Обновление фильтра статуса
  on(ProjectListActions.updateStatusFilter, (state, { status }) => ({
    ...state,
    filters: {
      ...state.filters,
      status: status as ProjectStatus | 'all'
    },
    pagination: {
      ...state.pagination,
      page: 1
    },
    selectedProjectIds: []
  })),

  // Обновление диапазона дат
  on(ProjectListActions.updateDateRange, (state, { start, end }) => ({
    ...state,
    filters: {
      ...state.filters,
      dateRange: { start, end }
    },
    pagination: {
      ...state.pagination,
      page: 1
    },
    selectedProjectIds: []
  }))
);
