import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import {
  map,
  mergeMap,
  catchError,
  withLatestFrom,
  debounceTime,
  distinctUntilChanged,
  filter,
  tap
} from 'rxjs/operators';
import { ProjectListService } from '../services/project-list.service';
import * as ProjectListActions from './project-list.actions';
import * as ProjectListSelectors from './project-list.selectors';
import { ProjectListConfig } from '../models/project-list.model';

@Injectable()
export class ProjectListEffects {
  // Конфигурация по умолчанию
  private readonly defaultConfig: ProjectListConfig = {
    enableSelection: true,
    enableInfiniteScroll: true,
    enableFilters: true,
    enableSorting: true,
    enableSearch: true,
    enableActions: true,
    pageSize: 12,
    debounceTime: 300
  };

  private readonly actions$ = inject(Actions);
  private readonly store = inject(Store);
  private readonly projectListService = inject(ProjectListService);

  // Эффект для загрузки проектов
  loadProjects$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectListActions.loadProjects),
      mergeMap(({ request }) =>
        this.projectListService.getProjects(request).pipe(
          map(response => ProjectListActions.loadProjectsSuccess({
            response,
            append: false
          })),
          catchError(error => of(ProjectListActions.loadProjectsFailure({
            error: error.message ?? 'Ошибка загрузки проектов'
          })))
        )
      )
    )
  );

  // Эффект для загрузки следующей страницы
  loadNextPage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectListActions.loadNextPage),
      withLatestFrom(
        this.store.select(ProjectListSelectors.selectProjectListFilters),
        this.store.select(ProjectListSelectors.selectProjectListSort),
        this.store.select(ProjectListSelectors.selectProjectListPagination)
      ),
      filter(([_action, _filters, _sort, pagination]) => pagination.hasMore),
      mergeMap(([_action, filters, sort, pagination]) => {
        const request = {
          filters,
          sort,
          pagination: {
            page: pagination.page + 1,
            pageSize: pagination.pageSize
          }
        };

        return this.projectListService.getProjects(request).pipe(
          map(response => ProjectListActions.loadNextPageSuccess({ response })),
          catchError(error => of(ProjectListActions.loadNextPageFailure({
            error: error.message ?? 'Ошибка загрузки следующей страницы'
          })))
        );
      })
    )
  );

  // Эффект для автоматической загрузки при изменении фильтров
  loadProjectsOnFilterChange$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        ProjectListActions.updateFilters,
        ProjectListActions.resetFilters,
        ProjectListActions.updateSort,
        ProjectListActions.resetSort,
        ProjectListActions.updateSearchTerm,
        ProjectListActions.updateStatusFilter,
        ProjectListActions.updateDateRange
      ),
      withLatestFrom(
        this.store.select(ProjectListSelectors.selectProjectListFilters),
        this.store.select(ProjectListSelectors.selectProjectListSort),
        this.store.select(ProjectListSelectors.selectProjectListPagination)
      ),
      debounceTime(this.defaultConfig.debounceTime),
      distinctUntilChanged((prev, curr) =>
        JSON.stringify(prev) === JSON.stringify(curr)
      ),
      mergeMap(([_action, filters, sort, pagination]) => {
        const request = {
          filters,
          sort,
          pagination: {
            page: 1,
            pageSize: pagination.pageSize
          }
        };

        return of(ProjectListActions.loadProjects({ request }));
      })
    )
  );

  // Эффект для создания проекта
  createProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectListActions.createProject),
      mergeMap(({ project }) =>
        this.projectListService.createProject(project).pipe(
          map(createdProject => ProjectListActions.createProjectSuccess({
            project: createdProject
          })),
          catchError(error => of(ProjectListActions.createProjectFailure({
            error: error.message ?? 'Ошибка создания проекта'
          })))
        )
      )
    )
  );

  // Эффект для обновления проекта
  updateProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectListActions.updateProject),
      mergeMap(({ id, project }) =>
        this.projectListService.updateProject(id, project).pipe(
          map(updatedProject => ProjectListActions.updateProjectSuccess({
            project: updatedProject
          })),
          catchError(error => of(ProjectListActions.updateProjectFailure({
            error: error.message ?? 'Ошибка обновления проекта'
          })))
        )
      )
    )
  );

  // Эффект для удаления проекта
  deleteProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectListActions.deleteProject),
      mergeMap(({ id }) =>
        this.projectListService.deleteProject(id).pipe(
          map(() => ProjectListActions.deleteProjectSuccess({ id })),
          catchError(error => of(ProjectListActions.deleteProjectFailure({
            error: error.message ?? 'Ошибка удаления проекта'
          })))
        )
      )
    )
  );

  // Эффект для архивирования проекта
  archiveProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectListActions.archiveProject),
      mergeMap(({ id }) =>
        this.projectListService.archiveProject(id).pipe(
          map(archivedProject => ProjectListActions.archiveProjectSuccess({
            project: archivedProject
          })),
          catchError(error => of(ProjectListActions.archiveProjectFailure({
            error: error.message ?? 'Ошибка архивирования проекта'
          })))
        )
      )
    )
  );

  // Эффект для дублирования проекта
  duplicateProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectListActions.duplicateProject),
      mergeMap(({ id }) =>
        this.projectListService.duplicateProject(id).pipe(
          map(duplicatedProject => ProjectListActions.duplicateProjectSuccess({
            project: duplicatedProject
          })),
          catchError(error => of(ProjectListActions.duplicateProjectFailure({
            error: error.message ?? 'Ошибка дублирования проекта'
          })))
        )
      )
    )
  );

  // Эффект для предварительной загрузки следующей страницы
  prefetchNextPage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectListActions.prefetchNextPage),
      withLatestFrom(
        this.store.select(ProjectListSelectors.selectProjectListFilters),
        this.store.select(ProjectListSelectors.selectProjectListSort),
        this.store.select(ProjectListSelectors.selectProjectListPagination)
      ),
      filter(([_action, _filters, _sort, pagination]) => pagination.hasMore),
      mergeMap(([_action, filters, sort, pagination]) => {
        const request = {
          filters,
          sort,
          pagination: {
            page: pagination.page + 1,
            pageSize: pagination.pageSize
          }
        };

        return this.projectListService.prefetchNextPage(request).pipe(
          map(response => ProjectListActions.prefetchNextPageSuccess({ response })),
          catchError(error => of(ProjectListActions.prefetchNextPageFailure({
            error: error.message ?? 'Ошибка предварительной загрузки'
          })))
        );
      })
    )
  );

  // Эффект для обработки ошибок
  handleError$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        ProjectListActions.loadProjectsFailure,
        ProjectListActions.loadNextPageFailure,
        ProjectListActions.createProjectFailure,
        ProjectListActions.updateProjectFailure,
        ProjectListActions.deleteProjectFailure,
        ProjectListActions.archiveProjectFailure,
        ProjectListActions.duplicateProjectFailure
      ),
      tap(({ error }) => {
        console.error('Project List Error:', error);
        // Здесь можно добавить логику для показа уведомлений
        // this.notificationService.showError(error);
      })
    ),
    { dispatch: false }
  );

  // Эффект для очистки ошибок при успешных операциях
  clearErrorOnSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        ProjectListActions.loadProjectsSuccess,
        ProjectListActions.loadNextPageSuccess,
        ProjectListActions.createProjectSuccess,
        ProjectListActions.updateProjectSuccess,
        ProjectListActions.deleteProjectSuccess,
        ProjectListActions.archiveProjectSuccess,
        ProjectListActions.duplicateProjectSuccess
      ),
      map(() => ProjectListActions.clearError())
    )
  );

  // Эффект для логирования действий (для отладки)
  logActions$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        ProjectListActions.loadProjects,
        ProjectListActions.loadProjectsSuccess,
        ProjectListActions.loadProjectsFailure,
        ProjectListActions.updateFilters,
        ProjectListActions.updateSort,
        ProjectListActions.selectProject,
        ProjectListActions.deselectProject
      ),
      tap(action => {
        // Проверка на development окружение через Angular
        if (typeof window !== 'undefined' && window.location.hostname === 'localhost') {
          // eslint-disable-next-line no-console
        }
      })
    ),
    { dispatch: false }
  );

  // Эффект для автоматической предварительной загрузки
  autoPrefetch$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectListActions.loadProjectsSuccess),
      withLatestFrom(
        this.store.select(ProjectListSelectors.selectHasMoreProjects),
        this.store.select(ProjectListSelectors.selectProjectListPagination)
      ),
      filter(([_action, hasMore, pagination]) =>
        hasMore && pagination.page === 1
      ),
      debounceTime(1000), // Задержка перед предварительной загрузкой
      map(() => ProjectListActions.prefetchNextPage())
    )
  );
}
