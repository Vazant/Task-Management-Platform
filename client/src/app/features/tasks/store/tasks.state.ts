import { Task, TaskFilters, TaskSortOption } from '@models';

export interface TasksState {
  entities: { [id: string]: Task };
  ids: string[];
  loading: boolean;
  error: string | null;
  filters: TaskFilters;
  sortBy: TaskSortOption;
}


