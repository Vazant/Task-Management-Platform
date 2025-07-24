import { Task } from '../../../core/models';

export interface TaskFilters {
  status: 'all' | 'backlog' | 'in-progress' | 'done';
  priority: 'all' | 'low' | 'medium' | 'high' | 'urgent';
  assignee: 'all' | string;
  project: 'all' | string;
}

export type TaskSortOption = 'created' | 'updated' | 'priority' | 'dueDate' | 'title';

export interface TasksState {
  entities: { [id: string]: Task };
  ids: string[];
  loading: boolean;
  error: string | null;
  filters: TaskFilters;
  sortBy: TaskSortOption;
}

export const initialState: TasksState = {
  entities: {},
  ids: [],
  loading: false,
  error: null,
  filters: {
    status: 'all',
    priority: 'all',
    assignee: 'all',
    project: 'all'
  },
  sortBy: 'created'
}; 