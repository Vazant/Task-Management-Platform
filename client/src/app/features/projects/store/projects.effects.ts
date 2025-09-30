import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { of } from 'rxjs';
import { Store } from '@ngrx/store';
import { ProjectService } from '@services/project.service';
import * as ProjectsActions from './projects.actions';
import * as ProjectsSelectors from './projects.selectors';

@Injectable()
export class ProjectsEffects {

  constructor(
    private readonly actions$: Actions,
    private readonly store: Store,
    private readonly projectService: ProjectService
  ) {}

  // Load Projects Effect
  loadProjects$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.loadProjects),
      switchMap(() =>
        this.projectService.getProjects().pipe(
          map(projects => ProjectsActions.loadProjectsSuccess({ projects })),
          catchError(error => of(ProjectsActions.loadProjectsFailure({ 
            error: error.message || 'Failed to load projects' 
          })))
        )
      )
    )
  );

  // Load Project by ID Effect
  loadProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.loadProject),
      switchMap(({ id }) =>
        this.projectService.getProjectById(id).pipe(
          map(project => ProjectsActions.loadProjectSuccess({ project })),
          catchError(error => of(ProjectsActions.loadProjectFailure({ 
            error: error.message || 'Failed to load project' 
          })))
        )
      )
    )
  );

  // Create Project Effect
  createProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.createProject),
      switchMap(({ request }) =>
        this.projectService.createProject(request).pipe(
          map(project => ProjectsActions.createProjectSuccess({ project })),
          catchError(error => of(ProjectsActions.createProjectFailure({ 
            error: error.message || 'Failed to create project' 
          })))
        )
      )
    )
  );

  // Update Project Effect
  updateProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.updateProject),
      switchMap(({ id, request }) =>
        this.projectService.updateProject(id, request).pipe(
          map(project => ProjectsActions.updateProjectSuccess({ project })),
          catchError(error => of(ProjectsActions.updateProjectFailure({ 
            error: error.message || 'Failed to update project' 
          })))
        )
      )
    )
  );

  // Delete Project Effect
  deleteProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.deleteProject),
      switchMap(({ id }) =>
        this.projectService.deleteProject(id).pipe(
          map(() => ProjectsActions.deleteProjectSuccess({ id })),
          catchError(error => of(ProjectsActions.deleteProjectFailure({ 
            error: error.message || 'Failed to delete project' 
          })))
        )
      )
    )
  );

  // Load Projects on App Init
  loadProjectsOnInit$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.loadProjects),
      withLatestFrom(this.store.select(ProjectsSelectors.selectProjectsLoaded)),
      switchMap(([action, loaded]) => {
        if (loaded) {
          return of(ProjectsActions.loadProjectsSuccess({ projects: [] }));
        }
        return of(action);
      })
    )
  );
}