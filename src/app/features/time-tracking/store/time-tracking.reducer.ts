import { createReducer, on } from '@ngrx/store';
import { TimeTrackingState } from './time-tracking.state';
import * as TimeTrackingActions from './time-tracking.actions';

export const initialState: TimeTrackingState = {
  entities: {},
  ids: [],
  loading: false,
  error: null,
  activeTimer: null
};

export const timeTrackingReducer = createReducer(
  initialState,

  // Load Time Entries
  on(TimeTrackingActions.loadTimeEntries, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(TimeTrackingActions.loadTimeEntriesSuccess, (state, { timeEntries }) => {
    const entities = timeEntries.reduce((acc, entry) => ({
      ...acc,
      [entry.id]: entry
    }), {});
    const ids = timeEntries.map(entry => entry.id);

    return {
      ...state,
      entities,
      ids,
      loading: false,
      error: null
    };
  }),

  on(TimeTrackingActions.loadTimeEntriesFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Start Timer
  on(TimeTrackingActions.startTimer, (state, { taskId }) => ({
    ...state,
    activeTimer: {
      taskId,
      startTime: new Date()
    }
  })),

  // Stop Timer
  on(TimeTrackingActions.stopTimer, (state) => ({
    ...state,
    activeTimer: null
  })),

  // Create Time Entry
  on(TimeTrackingActions.createTimeEntry, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(TimeTrackingActions.createTimeEntrySuccess, (state, { timeEntry }) => ({
    ...state,
    entities: {
      ...state.entities,
      [timeEntry.id]: timeEntry
    },
    ids: [...state.ids, timeEntry.id],
    loading: false,
    error: null
  })),

  on(TimeTrackingActions.createTimeEntryFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Update Time Entry
  on(TimeTrackingActions.updateTimeEntry, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(TimeTrackingActions.updateTimeEntrySuccess, (state, { timeEntry }) => ({
    ...state,
    entities: {
      ...state.entities,
      [timeEntry.id]: timeEntry
    },
    loading: false,
    error: null
  })),

  on(TimeTrackingActions.updateTimeEntryFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Delete Time Entry
  on(TimeTrackingActions.deleteTimeEntry, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(TimeTrackingActions.deleteTimeEntrySuccess, (state, { timeEntryId }) => {
    const entities = { ...state.entities };
    delete entities[timeEntryId];
    const ids = state.ids.filter(id => id !== timeEntryId);

    return {
      ...state,
      entities,
      ids,
      loading: false,
      error: null
    };
  }),

  on(TimeTrackingActions.deleteTimeEntryFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Clear Error
  on(TimeTrackingActions.clearTimeTrackingError, (state) => ({
    ...state,
    error: null
  }))
);
