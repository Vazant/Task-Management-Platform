import { Project } from '../../../core/models';

export interface ProjectsState {
  entities: { [id: string]: Project };
  ids: string[];
  loading: boolean;
  error: string | null;
  selectedProjectId: string | null;
}

export const initialState: ProjectsState = {
  entities: {},
  ids: [],
  loading: false,
  error: null,
  selectedProjectId: null
}; 