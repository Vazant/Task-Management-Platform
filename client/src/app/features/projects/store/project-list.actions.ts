import { createAction, props } from '@ngrx/store';
import { Project } from '@models';
import {
  ProjectListRequest,
  ProjectListResponse,
  ProjectFilters,
  ProjectSort,
  ProjectPagination,
  ProjectAction
} from '../models/project-list.model';

// Загрузка проектов
export const loadProjects = createAction(
  '[Project List] Load Projects',
  props<{ request: ProjectListRequest }>()
);

export const loadProjectsSuccess = createAction(
  '[Project List] Load Projects Success',
  props<{ response: ProjectListResponse; append: boolean }>()
);

export const loadProjectsFailure = createAction(
  '[Project List] Load Projects Failure',
  props<{ error: string }>()
);

// Загрузка следующей страницы (для infinite scroll)
export const loadNextPage = createAction(
  '[Project List] Load Next Page'
);

export const loadNextPageSuccess = createAction(
  '[Project List] Load Next Page Success',
  props<{ response: ProjectListResponse }>()
);

export const loadNextPageFailure = createAction(
  '[Project List] Load Next Page Failure',
  props<{ error: string }>()
);

// Фильтрация проектов
export const updateFilters = createAction(
  '[Project List] Update Filters',
  props<{ filters: Partial<ProjectFilters> }>()
);

export const resetFilters = createAction(
  '[Project List] Reset Filters'
);

// Сортировка проектов
export const updateSort = createAction(
  '[Project List] Update Sort',
  props<{ sort: ProjectSort }>()
);

export const resetSort = createAction(
  '[Project List] Reset Sort'
);

// Пагинация
export const updatePagination = createAction(
  '[Project List] Update Pagination',
  props<{ pagination: Partial<ProjectPagination> }>()
);

export const resetPagination = createAction(
  '[Project List] Reset Pagination'
);

// Выбор проектов
export const selectProject = createAction(
  '[Project List] Select Project',
  props<{ projectId: string }>()
);

export const deselectProject = createAction(
  '[Project List] Deselect Project',
  props<{ projectId: string }>()
);

export const selectAllProjectsAction = createAction(
  '[Project List] Select All Projects'
);

export const deselectAllProjectsAction = createAction(
  '[Project List] Deselect All Projects'
);

export const toggleProjectSelection = createAction(
  '[Project List] Toggle Project Selection',
  props<{ projectId: string }>()
);

// Действия с проектами
export const performProjectAction = createAction(
  '[Project List] Perform Project Action',
  props<{ action: ProjectAction }>()
);

export const performProjectActionSuccess = createAction(
  '[Project List] Perform Project Action Success',
  props<{ action: ProjectAction; result: any }>()
);

export const performProjectActionFailure = createAction(
  '[Project List] Perform Project Action Failure',
  props<{ action: ProjectAction; error: string }>()
);

// Создание проекта
export const createProject = createAction(
  '[Project List] Create Project',
  props<{ project: Partial<Project> }>()
);

export const createProjectSuccess = createAction(
  '[Project List] Create Project Success',
  props<{ project: Project }>()
);

export const createProjectFailure = createAction(
  '[Project List] Create Project Failure',
  props<{ error: string }>()
);

// Обновление проекта
export const updateProject = createAction(
  '[Project List] Update Project',
  props<{ id: string; project: Partial<Project> }>()
);

export const updateProjectSuccess = createAction(
  '[Project List] Update Project Success',
  props<{ project: Project }>()
);

export const updateProjectFailure = createAction(
  '[Project List] Update Project Failure',
  props<{ error: string }>()
);

// Удаление проекта
export const deleteProject = createAction(
  '[Project List] Delete Project',
  props<{ id: string }>()
);

export const deleteProjectSuccess = createAction(
  '[Project List] Delete Project Success',
  props<{ id: string }>()
);

export const deleteProjectFailure = createAction(
  '[Project List] Delete Project Failure',
  props<{ error: string }>()
);

// Архивирование проекта
export const archiveProject = createAction(
  '[Project List] Archive Project',
  props<{ id: string }>()
);

export const archiveProjectSuccess = createAction(
  '[Project List] Archive Project Success',
  props<{ project: Project }>()
);

export const archiveProjectFailure = createAction(
  '[Project List] Archive Project Failure',
  props<{ error: string }>()
);

// Дублирование проекта
export const duplicateProject = createAction(
  '[Project List] Duplicate Project',
  props<{ id: string }>()
);

export const duplicateProjectSuccess = createAction(
  '[Project List] Duplicate Project Success',
  props<{ project: Project }>()
);

export const duplicateProjectFailure = createAction(
  '[Project List] Duplicate Project Failure',
  props<{ error: string }>()
);

// Состояние загрузки
export const setLoading = createAction(
  '[Project List] Set Loading',
  props<{ loading: boolean }>()
);

// Очистка ошибок
export const clearError = createAction(
  '[Project List] Clear Error'
);

// Сброс состояния
export const resetProjectList = createAction(
  '[Project List] Reset State'
);

// Предварительная загрузка
export const prefetchNextPage = createAction(
  '[Project List] Prefetch Next Page'
);

export const prefetchNextPageSuccess = createAction(
  '[Project List] Prefetch Next Page Success',
  props<{ response: ProjectListResponse }>()
);

export const prefetchNextPageFailure = createAction(
  '[Project List] Prefetch Next Page Failure',
  props<{ error: string }>()
);

// Обновление поиска
export const updateSearchTerm = createAction(
  '[Project List] Update Search Term',
  props<{ searchTerm: string }>()
);

// Обновление статуса
export const updateStatusFilter = createAction(
  '[Project List] Update Status Filter',
  props<{ status: string }>()
);

// Обновление диапазона дат
export const updateDateRange = createAction(
  '[Project List] Update Date Range',
  props<{ start: Date | null; end: Date | null }>()
);

// Загрузка дополнительных проектов
export const loadMoreProjects = createAction(
  '[Project List] Load More Projects'
);

// Обновление проектов
export const refreshProjects = createAction(
  '[Project List] Refresh Projects'
);

// Очистка фильтров
export const clearFilters = createAction(
  '[Project List] Clear Filters'
);
