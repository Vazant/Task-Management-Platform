import { Project } from '@models';

// Интерфейс для фильтрации проектов
export interface ProjectFilters {
  searchTerm: string;
  status: ProjectStatus | 'all';
  ownerId: string | null;
  dateRange: {
    start: Date | null;
    end: Date | null;
  };
}

// Интерфейс для сортировки проектов
export interface ProjectSort {
  field: keyof Project;
  direction: 'asc' | 'desc';
}

// Интерфейс для пагинации
export interface ProjectPagination {
  page: number;
  pageSize: number;
  total: number;
  hasMore: boolean;
}

// Интерфейс для состояния списка проектов
export interface ProjectListState {
  projects: Project[];
  loading: boolean;
  error: string | null;
  filters: ProjectFilters;
  sort: ProjectSort;
  pagination: ProjectPagination;
  selectedProjectIds: string[];
}

// Интерфейс для запроса проектов
export interface ProjectListRequest {
  filters: ProjectFilters;
  sort: ProjectSort;
  pagination: {
    page: number;
    pageSize: number;
  };
}

// Интерфейс для ответа API
export interface ProjectListResponse {
  projects: Project[];
  total: number;
  hasMore: boolean;
}

// Типы статусов проектов
export type ProjectStatus = 'active' | 'archived' | 'completed' | 'on-hold';

// Интерфейс для карточки проекта
export interface ProjectCard {
  project: Project;
  isSelected: boolean;
  isHovered: boolean;
}

// Интерфейс для действий с проектами
export interface ProjectAction {
  type: 'edit' | 'delete' | 'archive' | 'duplicate' | 'share' | 'create';
  projectId: string;
  payload?: any;
}

// Интерфейс для конфигурации компонента
export interface ProjectListConfig {
  enableSelection: boolean;
  enableInfiniteScroll: boolean;
  enableFilters: boolean;
  enableSorting: boolean;
  enableSearch: boolean;
  enableActions: boolean;
  pageSize: number;
  debounceTime: number;
}
