import { EntityState } from '@ngrx/entity';
import { Task, TaskFilters, TaskSortOption } from '@models';

export interface TasksState extends EntityState<Task> {
  loading: boolean;
  error: string | null;
  filters: TaskFilters;
  sortBy: TaskSortOption;
}


