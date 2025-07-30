import { AuthState } from '../features/auth/store/auth.state';
import { ProjectsState } from '../features/projects/store/projects.state';
import { TasksState } from '../features/tasks/store/tasks.state';
import { TimeTrackingState } from '../features/time-tracking/store/time-tracking.state';

export interface AppState {
  auth: AuthState;
  projects: ProjectsState;
  tasks: TasksState;
  timeTracking: TimeTrackingState;
}
