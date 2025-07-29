import { createFeatureSelector, createSelector } from '@ngrx/store';
import { TimeTrackingState } from './time-tracking.state';



export const selectTimeTrackingState = createFeatureSelector<TimeTrackingState>('timeTracking');

export const selectAllTimeEntries = createSelector(
  selectTimeTrackingState,
  (state) => state.ids.map(id => state.entities[id]).filter(Boolean)
);

export const selectTimeEntriesEntities = createSelector(
  selectTimeTrackingState,
  (state) => state.entities
);

export const selectTimeEntriesIds = createSelector(
  selectTimeTrackingState,
  (state) => state.ids
);

export const selectTimeTrackingLoading = createSelector(
  selectTimeTrackingState,
  (state) => state.loading
);

export const selectTimeTrackingError = createSelector(
  selectTimeTrackingState,
  (state) => state.error
);

export const selectActiveTimer = createSelector(
  selectTimeTrackingState,
  (state) => state.activeTimer
);

export const selectIsTimerActive = createSelector(
  selectActiveTimer,
  (activeTimer) => activeTimer !== null
);

export const selectActiveTaskId = createSelector(
  selectActiveTimer,
  (activeTimer) => activeTimer?.taskId ?? null
);

// Время по задаче
export const selectTimeEntriesByTask = (taskId: string) => createSelector(
  selectAllTimeEntries,
  (timeEntries) => timeEntries.filter(entry => entry.taskId === taskId)
);

// Время по пользователю
export const selectTimeEntriesByUser = (userId: string) => createSelector(
  selectAllTimeEntries,
  (timeEntries) => timeEntries.filter(entry => entry.userId === userId)
);

// Общее время по задаче
export const selectTotalTimeByTask = (taskId: string) => createSelector(
  selectTimeEntriesByTask(taskId),
  (timeEntries) => timeEntries.reduce((total, entry) => total + entry.duration, 0)
);

// Общее время по пользователю
export const selectTotalTimeByUser = (userId: string) => createSelector(
  selectTimeEntriesByUser(userId),
  (timeEntries) => timeEntries.reduce((total, entry) => total + entry.duration, 0)
);

// Статистика времени
export const selectTimeTrackingStats = createSelector(
  selectAllTimeEntries,
  (timeEntries) => {
    const today = new Date();
    const startOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    const startOfWeek = new Date(today.getFullYear(), today.getMonth(), today.getDate() - today.getDay());
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    return {
      total: timeEntries.reduce((sum, entry) => sum + entry.duration, 0),
      today: timeEntries
        .filter(entry => new Date(entry.startTime) >= startOfDay)
        .reduce((sum, entry) => sum + entry.duration, 0),
      thisWeek: timeEntries
        .filter(entry => new Date(entry.startTime) >= startOfWeek)
        .reduce((sum, entry) => sum + entry.duration, 0),
      thisMonth: timeEntries
        .filter(entry => new Date(entry.startTime) >= startOfMonth)
        .reduce((sum, entry) => sum + entry.duration, 0),
      totalEntries: timeEntries.length
    };
  }
);

// Активное время (если таймер запущен)
export const selectActiveTime = createSelector(
  selectActiveTimer,
  (activeTimer) => {
    if (!activeTimer) return 0;
    const now = new Date();
    const startTime = new Date(activeTimer.startTime);
    return Math.floor((now.getTime() - startTime.getTime()) / 1000 / 60); // в минутах
  }
);
