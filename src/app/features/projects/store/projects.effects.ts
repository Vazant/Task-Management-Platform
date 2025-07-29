import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { map, mergeMap, catchError } from 'rxjs/operators';

import * as ProjectsActions from './projects.actions';
import { NotificationService } from '@services';
import { Project } from '@models';



@Injectable()
export class ProjectsEffects {
  private readonly actions$ = inject(Actions);
  private readonly store = inject(Store);
  private readonly notificationService = inject(NotificationService);

  // Загрузка проектов
  loadProjects$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.loadProjects),
      mergeMap(() => {
        // Здесь будет реальный API вызов
        // Пока используем моковые данные
        const mockProjects = [
          {
            id: '1',
            name: 'Веб-сайт компании',
            description: 'Разработка корпоративного сайта',
            ownerId: 'user1',
            members: ['user1', 'user2'],
            settings: {
              allowGuestAccess: false,
              defaultTaskPriority: 'medium' as const,
              autoAssignTasks: true,
              requireTimeTracking: true
            },
            createdAt: new Date(),
            updatedAt: new Date()
          },
          {
            id: '2',
            name: 'Мобильное приложение',
            description: 'iOS и Android приложение',
            ownerId: 'user1',
            members: ['user1', 'user3'],
            settings: {
              allowGuestAccess: true,
              defaultTaskPriority: 'high' as const,
              autoAssignTasks: false,
              requireTimeTracking: true
            },
            createdAt: new Date(),
            updatedAt: new Date()
          }
        ];

        return of(mockProjects).pipe(
          map(projects => ProjectsActions.loadProjectsSuccess({ projects })),
          catchError(error => {
            // Показываем уведомление только для 4xx ошибок (кроме 0 и 5xx)
            if (error.status && error.status >= 400 && error.status < 500) {
              this.notificationService.error('Ошибка', 'Не удалось загрузить проекты');
            }
            return of(ProjectsActions.loadProjectsFailure({ error: error.message }));
          })
        );
      })
    )
  );

  // Создание проекта
  createProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.createProject),
      mergeMap(({ project }) => {
        // Здесь будет реальный API вызов
        const newProject: Project = {
          ...project,
          id: Date.now().toString(),
          name: project.name ?? '',
          description: project.description ?? '',
          ownerId: project.ownerId ?? '',
          members: project.members ?? [],
          settings: project.settings ?? {
            allowGuestAccess: false,
            defaultTaskPriority: 'medium',
            autoAssignTasks: false,
            requireTimeTracking: false
          },
          createdAt: new Date(),
          updatedAt: new Date()
        };

        return of(newProject).pipe(
          map(createdProject => {
            this.notificationService.success('Успех', 'Проект создан');
            return ProjectsActions.createProjectSuccess({ project: createdProject });
          }),
          catchError(error => {
            // Показываем уведомление только для 4xx ошибок (кроме 0 и 5xx)
            if (error.status && error.status >= 400 && error.status < 500) {
              this.notificationService.error('Ошибка', 'Не удалось создать проект');
            }
            return of(ProjectsActions.createProjectFailure({ error: error.message }));
          })
        );
      })
    )
  );

  // Обновление проекта
  updateProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.updateProject),
      mergeMap(({ project }) => {
        // Здесь будет реальный API вызов
        const updatedProject: Project = {
          ...project,
          name: project.name ?? '',
          description: project.description ?? '',
          ownerId: project.ownerId ?? '',
          members: project.members ?? [],
          settings: project.settings ?? {
            allowGuestAccess: false,
            defaultTaskPriority: 'medium',
            autoAssignTasks: false,
            requireTimeTracking: false
          },
          createdAt: project.createdAt ?? new Date(),
          updatedAt: new Date()
        };

        return of(updatedProject).pipe(
          map(project => {
            this.notificationService.success('Успех', 'Проект обновлен');
            return ProjectsActions.updateProjectSuccess({ project });
          }),
          catchError(error => {
            // Показываем уведомление только для 4xx ошибок (кроме 0 и 5xx)
            if (error.status && error.status >= 400 && error.status < 500) {
              this.notificationService.error('Ошибка', 'Не удалось обновить проект');
            }
            return of(ProjectsActions.updateProjectFailure({ error: error.message }));
          })
        );
      })
    )
  );

  // Удаление проекта
  deleteProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.deleteProject),
      mergeMap(({ projectId }) => {
        // Здесь будет реальный API вызов
        return of(projectId).pipe(
          map(id => {
            this.notificationService.success('Успех', 'Проект удален');
            return ProjectsActions.deleteProjectSuccess({ projectId: id });
          }),
          catchError(error => {
            // Показываем уведомление только для 4xx ошибок (кроме 0 и 5xx)
            if (error.status && error.status >= 400 && error.status < 500) {
              this.notificationService.error('Ошибка', 'Не удалось удалить проект');
            }
            return of(ProjectsActions.deleteProjectFailure({ error: error.message }));
          })
        );
      })
    )
  );
}
