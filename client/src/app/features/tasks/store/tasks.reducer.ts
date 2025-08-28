import { createReducer, on } from '@ngrx/store';
import { TasksState } from './tasks.state';
import { createEntityAdapter } from '@ngrx/entity';
import type { Task } from '@models';
import { TaskFilters } from '@models';
import * as TasksActions from './tasks.actions';



const initialFilters: TaskFilters = {
  status: 'all',
  priority: 'all',
  assignee: 'all',
  project: 'all'
};

export const tasksAdapter = createEntityAdapter<Task>({
  selectId: (task) => task.id,
  sortComparer: false,
});

export const initialState: TasksState = tasksAdapter.getInitialState({
  loading: false,
  error: null,
  filters: initialFilters,
  sortBy: 'created',
});

export const tasksReducer = createReducer(
  initialState,

  // Load Tasks
  on(TasksActions.loadTasks, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(TasksActions.loadTasksSuccess, (state, { tasks }) =>
    tasksAdapter.setAll(tasks, {
      ...state,
      loading: false,
      error: null,
    })
  ),

  on(TasksActions.loadTasksFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Create Task
  on(TasksActions.createTask, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(TasksActions.createTaskSuccess, (state, { task }) =>
    tasksAdapter.addOne(task, {
      ...state,
      loading: false,
      error: null,
    })
  ),

  on(TasksActions.createTaskFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Update Task
  on(TasksActions.updateTask, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(TasksActions.updateTaskSuccess, (state, { task }) =>
    tasksAdapter.upsertOne(task, {
      ...state,
      loading: false,
      error: null,
    })
  ),

  on(TasksActions.updateTaskFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Delete Task
  on(TasksActions.deleteTask, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(TasksActions.deleteTaskSuccess, (state, { taskId }) =>
    tasksAdapter.removeOne(taskId, {
      ...state,
      loading: false,
      error: null,
    })
  ),

  on(TasksActions.deleteTaskFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Update Filters
  on(TasksActions.updateFilters, (state, { filters }) => ({
    ...state,
    filters: { ...state.filters, ...filters }
  })),

  // Update Sort
  on(TasksActions.updateSort, (state, { sortBy }) => ({
    ...state,
    sortBy
  })),

  // Clear Filters
  on(TasksActions.clearFilters, (state) => ({
    ...state,
    filters: initialFilters
  })),

  // Clear Error
  on(TasksActions.clearTasksError, (state) => ({
    ...state,
    error: null
  }))
);
