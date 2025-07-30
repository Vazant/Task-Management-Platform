import { AuthState } from '../features/auth/store';
import { ProjectsState } from '../features/projects/store';
import { TasksState } from '../features/tasks/store';
import { TimeTrackingState } from '../features/time-tracking/store';

export interface AppState {
  auth: AuthState;
  projects: ProjectsState;
  tasks: TasksState;
  timeTracking: TimeTrackingState;
}
