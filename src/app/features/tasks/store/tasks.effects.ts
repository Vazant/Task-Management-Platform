import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, mergeMap, catchError } from 'rxjs/operators';

import * as TasksActions from './tasks.actions';
import { NotificationService } from '../../../core/services/notification.service';
import { Task } from '../../../core/models';

@Injectable()
export class TasksEffects {
  private actions$ = inject(Actions);
  private notificationService = inject(NotificationService);

  // Загрузка задач
  loadTasks$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TasksActions.loadTasks),
      mergeMap(() => {
        // Здесь будет реальный API вызов
        // Пока используем моковые данные
        const mockTasks: Task[] = [
          {
            id: '1',
            title: 'Создать дизайн',
            description: 'Разработать макет главной страницы',
            status: 'in-progress',
            priority: 'high',
            projectId: '1',
            assigneeId: 'user1',
            creatorId: 'user1',
            labels: ['design', 'frontend'],
            subtasks: [],
            timeSpent: 120,
            estimatedTime: 240,
            dueDate: new Date('2024-02-15'),
            createdAt: new Date(),
            updatedAt: new Date()
          },
          {
            id: '2',
            title: 'Настроить API',
            description: 'Создать REST API endpoints',
            status: 'backlog',
            priority: 'medium',
            projectId: '1',
            assigneeId: 'user2',
            creatorId: 'user1',
            labels: ['backend', 'api'],
            subtasks: [],
            timeSpent: 0,
            estimatedTime: 480,
            dueDate: new Date('2024-02-20'),
            createdAt: new Date(),
            updatedAt: new Date()
          }
        ];

        return of(mockTasks).pipe(
          map(tasks => TasksActions.loadTasksSuccess({ tasks })),
          catchError(error => {
            this.notificationService.error('Ошибка', 'Не удалось загрузить задачи');
            return of(TasksActions.loadTasksFailure({ error: error.message }));
          })
        );
      })
    )
  );

  // Создание задачи
  createTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TasksActions.createTask),
      mergeMap(({ task }) => {
        // Здесь будет реальный API вызов
        const newTask: Task = {
          ...task,
          id: Date.now().toString(),
          title: task.title || '',
          description: task.description || '',
          status: task.status || 'backlog',
          priority: task.priority || 'medium',
          projectId: task.projectId || '',
          assigneeId: task.assigneeId,
          creatorId: task.creatorId || '',
          labels: task.labels || [],
          subtasks: task.subtasks || [],
          timeSpent: task.timeSpent || 0,
          estimatedTime: task.estimatedTime,
          dueDate: task.dueDate,
          createdAt: new Date(),
          updatedAt: new Date()
        };

        return of(newTask).pipe(
          map(createdTask => {
            this.notificationService.success('Успех', 'Задача создана');
            return TasksActions.createTaskSuccess({ task: createdTask });
          }),
          catchError(error => {
            this.notificationService.error('Ошибка', 'Не удалось создать задачу');
            return of(TasksActions.createTaskFailure({ error: error.message }));
          })
        );
      })
    )
  );

  // Обновление задачи
  updateTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TasksActions.updateTask),
      mergeMap(({ task }) => {
        // Здесь будет реальный API вызов
        const updatedTask: Task = {
          ...task,
          title: task.title || '',
          description: task.description || '',
          status: task.status || 'backlog',
          priority: task.priority || 'medium',
          projectId: task.projectId || '',
          assigneeId: task.assigneeId,
          creatorId: task.creatorId || '',
          labels: task.labels || [],
          subtasks: task.subtasks || [],
          timeSpent: task.timeSpent || 0,
          estimatedTime: task.estimatedTime,
          dueDate: task.dueDate,
          createdAt: task.createdAt || new Date(),
          updatedAt: new Date()
        };

        return of(updatedTask).pipe(
          map(task => {
            this.notificationService.success('Успех', 'Задача обновлена');
            return TasksActions.updateTaskSuccess({ task });
          }),
          catchError(error => {
            this.notificationService.error('Ошибка', 'Не удалось обновить задачу');
            return of(TasksActions.updateTaskFailure({ error: error.message }));
          })
        );
      })
    )
  );

  // Удаление задачи
  deleteTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TasksActions.deleteTask),
      mergeMap(({ taskId }) => {
        // Здесь будет реальный API вызов
        return of(taskId).pipe(
          map(id => {
            this.notificationService.success('Успех', 'Задача удалена');
            return TasksActions.deleteTaskSuccess({ taskId: id });
          }),
          catchError(error => {
            this.notificationService.error('Ошибка', 'Не удалось удалить задачу');
            return of(TasksActions.deleteTaskFailure({ error: error.message }));
          })
        );
      })
    )
  );
}
