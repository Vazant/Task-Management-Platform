import { createAction, props } from '@ngrx/store';
import { Task, TaskFilters, TaskSortOption } from '@models';

// Load Tasks
export const loadTasks = createAction('[Tasks] Load Tasks');

export const loadTasksSuccess = createAction(
  '[Tasks] Load Tasks Success',
  props<{ tasks: Task[] }>()
);

export const loadTasksFailure = createAction(
  '[Tasks] Load Tasks Failure',
  props<{ error: string }>()
);

// Create Task
export const createTask = createAction(
  '[Tasks] Create Task',
  props<{ task: Partial<Task> }>()
);

export const createTaskSuccess = createAction(
  '[Tasks] Create Task Success',
  props<{ task: Task }>()
);

export const createTaskFailure = createAction(
  '[Tasks] Create Task Failure',
  props<{ error: string }>()
);

// Update Task
export const updateTask = createAction(
  '[Tasks] Update Task',
  props<{ task: Partial<Task> & { id: string } }>()
);

export const updateTaskSuccess = createAction(
  '[Tasks] Update Task Success',
  props<{ task: Task }>()
);

export const updateTaskFailure = createAction(
  '[Tasks] Update Task Failure',
  props<{ error: string }>()
);

// Delete Task
export const deleteTask = createAction(
  '[Tasks] Delete Task',
  props<{ taskId: string }>()
);

export const deleteTaskSuccess = createAction(
  '[Tasks] Delete Task Success',
  props<{ taskId: string }>()
);

export const deleteTaskFailure = createAction(
  '[Tasks] Delete Task Failure',
  props<{ error: string }>()
);

// Update Filters
export const updateFilters = createAction(
  '[Tasks] Update Filters',
  props<{ filters: Partial<TaskFilters> }>()
);

// Update Sort
export const updateSort = createAction(
  '[Tasks] Update Sort',
  props<{ sortBy: TaskSortOption }>()
);

// Clear Filters
export const clearFilters = createAction('[Tasks] Clear Filters');

// Reorder Tasks
export const reorderTasks = createAction(
  '[Tasks] Reorder Tasks',
  props<{ tasks: Task[] }>()
);

export const reorderTasksSuccess = createAction(
  '[Tasks] Reorder Tasks Success',
  props<{ tasks: Task[] }>()
);

export const reorderTasksFailure = createAction(
  '[Tasks] Reorder Tasks Failure',
  props<{ error: string }>()
);

// Clear Error
export const clearTasksError = createAction('[Tasks] Clear Error');
