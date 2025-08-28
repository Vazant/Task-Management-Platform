import { createFeatureSelector, createSelector } from '@ngrx/store';
import { TasksState } from './tasks.state';
import { tasksAdapter } from './tasks.reducer';
import type { Task } from '@models';



export const selectTasksState = createFeatureSelector<TasksState>('tasks');

const { selectAll, selectEntities, selectIds, selectTotal } = tasksAdapter.getSelectors();

export const selectAllTasks = createSelector(selectTasksState, selectAll);
export const selectTasksEntities = createSelector(selectTasksState, selectEntities);
export const selectTasksIds = createSelector(selectTasksState, selectIds);
export const selectTasksTotal = createSelector(selectTasksState, selectTotal);

export const selectTasksLoading = createSelector(
  selectTasksState,
  (state) => state.loading
);

export const selectTasksError = createSelector(
  selectTasksState,
  (state) => state.error
);

export const selectTasksFilters = createSelector(
  selectTasksState,
  (state) => state.filters
);

export const selectTasksSortBy = createSelector(
  selectTasksState,
  (state) => state.sortBy
);

// Фильтрованные задачи
export const selectFilteredTasks = createSelector(
  selectAllTasks,
  selectTasksFilters,
  (tasks: Task[], filters) => {
    return tasks.filter((task: Task) => {
      if (filters.status !== 'all' && task.status !== filters.status) {
        return false;
      }
      if (filters.priority !== 'all' && task.priority !== filters.priority) {
        return false;
      }
      if (filters.assignee !== 'all' && task.assigneeId !== filters.assignee) {
        return false;
      }
      if (filters.project !== 'all' && task.projectId !== filters.project) {
        return false;
      }
      if (filters.query && filters.query.trim()) {
        const q = filters.query.toLowerCase();
        const inTitle = task.title.toLowerCase().includes(q);
        const inDesc = task.description.toLowerCase().includes(q);
        if (!inTitle && !inDesc) return false;
      }
      return true;
    });
  }
);

// Отсортированные задачи
export const selectSortedTasks = createSelector(
  selectFilteredTasks,
  selectTasksSortBy,
  (tasks: Task[], sortBy) => {
    return [...tasks].sort((a: Task, b: Task) => {
      switch (sortBy) {
        case 'created':
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        case 'updated':
          return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime();
        case 'priority': {
          const priorityOrder: Record<Task['priority'], number> = { urgent: 4, high: 3, medium: 2, low: 1 };
          return priorityOrder[b.priority] - priorityOrder[a.priority];
        }
        case 'dueDate':
          if (!a.dueDate && !b.dueDate) return 0;
          if (!a.dueDate) return 1;
          if (!b.dueDate) return -1;
          return new Date(a.dueDate!).getTime() - new Date(b.dueDate!).getTime();
        case 'title':
          return a.title.localeCompare(b.title);
        default:
          return 0;
      }
    });
  }
);

// Задачи по статусу
export const selectTasksByStatus = (status: string) => createSelector(
  selectAllTasks,
  (tasks: Task[]) => tasks.filter((task: Task) => task.status === status)
);

// Задачи по проекту
export const selectTasksByProject = (projectId: string) => createSelector(
  selectAllTasks,
  (tasks: Task[]) => tasks.filter((task: Task) => task.projectId === projectId)
);

// Задачи по исполнителю
export const selectTasksByAssignee = (assigneeId: string) => createSelector(
  selectAllTasks,
  (tasks: Task[]) => tasks.filter((task: Task) => task.assigneeId === assigneeId)
);

// Статистика задач
export const selectTasksStats = createSelector(
  selectAllTasks,
  (tasks: Task[]) => ({
    total: tasks.length,
    backlog: tasks.filter((t: Task) => t.status === 'backlog').length,
    inProgress: tasks.filter((t: Task) => t.status === 'in-progress').length,
    done: tasks.filter((t: Task) => t.status === 'done').length,
    urgent: tasks.filter((t: Task) => t.priority === 'urgent').length,
    high: tasks.filter((t: Task) => t.priority === 'high').length,
    medium: tasks.filter((t: Task) => t.priority === 'medium').length,
    low: tasks.filter((t: Task) => t.priority === 'low').length,
  })
);
