import { createReducer, on } from '@ngrx/store';
import { TasksState } from './tasks.state';
import { TaskFilters } from '@models';
import * as TasksActions from './tasks.actions';

const initialFilters: TaskFilters = {
  status: 'all',
  priority: 'all',
  assignee: 'all',
  project: 'all'
};

export const initialState: TasksState = {
  entities: {},
  ids: [],
  loading: false,
  error: null,
  filters: initialFilters,
  sortBy: 'created'
};

export const tasksReducer = createReducer(
  initialState,

  // Load Tasks
  on(TasksActions.loadTasks, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(TasksActions.loadTasksSuccess, (state, { tasks }) => {
    const entities = tasks.reduce((acc, task) => ({
      ...acc,
      [task.id]: task
    }), {});
    const ids = tasks.map(task => task.id);

    return {
      ...state,
      entities,
      ids,
      loading: false,
      error: null
    };
  }),

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

  on(TasksActions.createTaskSuccess, (state, { task }) => ({
    ...state,
    entities: {
      ...state.entities,
      [task.id]: task
    },
    ids: [...state.ids, task.id],
    loading: false,
    error: null
  })),

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

  on(TasksActions.updateTaskSuccess, (state, { task }) => ({
    ...state,
    entities: {
      ...state.entities,
      [task.id]: task
    },
    loading: false,
    error: null
  })),

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

  on(TasksActions.deleteTaskSuccess, (state, { taskId }) => {
    const { [taskId]: removed, ...entities } = state.entities;
    const ids = state.ids.filter(id => id !== taskId);

    return {
      ...state,
      entities,
      ids,
      loading: false,
      error: null
    };
  }),

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
