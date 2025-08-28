import { EntityState } from '@ngrx/entity';
import { Project } from '@models';

export interface ProjectsState extends EntityState<Project> {
  loading: boolean;
  error: string | null;
  selectedProjectId: string | null;
}

