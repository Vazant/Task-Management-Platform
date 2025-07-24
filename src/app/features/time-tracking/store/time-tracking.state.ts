import { TimeEntry } from '../../../core/models';

export interface TimeTrackingState {
  entities: { [id: string]: TimeEntry };
  ids: string[];
  loading: boolean;
  error: string | null;
  activeTimer: {
    taskId: string;
    startTime: Date;
  } | null;
}

export const initialState: TimeTrackingState = {
  entities: {},
  ids: [],
  loading: false,
  error: null,
  activeTimer: null
}; 