import { createAction, props } from '@ngrx/store';
import { TimeEntry } from '@models';

// Load Time Entries
export const loadTimeEntries = createAction('[Time Tracking] Load Time Entries');

export const loadTimeEntriesSuccess = createAction(
  '[Time Tracking] Load Time Entries Success',
  props<{ timeEntries: TimeEntry[] }>()
);

export const loadTimeEntriesFailure = createAction(
  '[Time Tracking] Load Time Entries Failure',
  props<{ error: string }>()
);

// Timer Actions
export const startTimer = createAction(
  '[Time Tracking] Start Timer',
  props<{ taskId: string }>()
);

export const stopTimer = createAction('[Time Tracking] Stop Timer');

// Create Time Entry
export const createTimeEntry = createAction(
  '[Time Tracking] Create Time Entry',
  props<{ timeEntry: Partial<TimeEntry> }>()
);

export const createTimeEntrySuccess = createAction(
  '[Time Tracking] Create Time Entry Success',
  props<{ timeEntry: TimeEntry }>()
);

export const createTimeEntryFailure = createAction(
  '[Time Tracking] Create Time Entry Failure',
  props<{ error: string }>()
);

// Update Time Entry
export const updateTimeEntry = createAction(
  '[Time Tracking] Update Time Entry',
  props<{ timeEntry: Partial<TimeEntry> & { id: string } }>()
);

export const updateTimeEntrySuccess = createAction(
  '[Time Tracking] Update Time Entry Success',
  props<{ timeEntry: TimeEntry }>()
);

export const updateTimeEntryFailure = createAction(
  '[Time Tracking] Update Time Entry Failure',
  props<{ error: string }>()
);

// Delete Time Entry
export const deleteTimeEntry = createAction(
  '[Time Tracking] Delete Time Entry',
  props<{ timeEntryId: string }>()
);

export const deleteTimeEntrySuccess = createAction(
  '[Time Tracking] Delete Time Entry Success',
  props<{ timeEntryId: string }>()
);

export const deleteTimeEntryFailure = createAction(
  '[Time Tracking] Delete Time Entry Failure',
  props<{ error: string }>()
);

// Clear Error
export const clearTimeTrackingError = createAction('[Time Tracking] Clear Error');
