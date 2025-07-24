import { Project } from '@models';

export interface ProjectsState {
  entities: { [id: string]: Project };
  ids: string[];
  loading: boolean;
  error: string | null;
  selectedProjectId: string | null;
}

