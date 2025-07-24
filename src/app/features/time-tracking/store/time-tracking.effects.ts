import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, mergeMap, catchError } from 'rxjs/operators';

import * as TimeTrackingActions from './time-tracking.actions';
import { NotificationService } from '../../../core/services/notification.service';
import { TimeEntry } from '../../../core/models';

@Injectable()
export class TimeTrackingEffects {
  private actions$ = inject(Actions);
  private notificationService = inject(NotificationService);

  // Загрузка записей времени
  loadTimeEntries$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TimeTrackingActions.loadTimeEntries),
      mergeMap(() => {
        // Здесь будет реальный API вызов
        // Пока используем моковые данные
        const mockTimeEntries: TimeEntry[] = [
          {
            id: '1',
            taskId: '1',
            userId: 'user1',
            startTime: new Date('2024-02-10T09:00:00'),
            endTime: new Date('2024-02-10T11:00:00'),
            duration: 120,
            description: 'Работа над дизайном'
          },
          {
            id: '2',
            taskId: '1',
            userId: 'user1',
            startTime: new Date('2024-02-11T14:00:00'),
            endTime: new Date('2024-02-11T16:30:00'),
            duration: 150,
            description: 'Доработка макета'
          }
        ];

        return of(mockTimeEntries).pipe(
          map(timeEntries => TimeTrackingActions.loadTimeEntriesSuccess({ timeEntries })),
          catchError(error => {
            this.notificationService.error('Ошибка', 'Не удалось загрузить записи времени');
            return of(TimeTrackingActions.loadTimeEntriesFailure({ error: error.message }));
          })
        );
      })
    )
  );

  // Создание записи времени
  createTimeEntry$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TimeTrackingActions.createTimeEntry),
      mergeMap(({ timeEntry }) => {
        // Здесь будет реальный API вызов
        const newTimeEntry: TimeEntry = {
          ...timeEntry,
          id: Date.now().toString(),
          taskId: timeEntry.taskId || '',
          userId: timeEntry.userId || '',
          startTime: timeEntry.startTime || new Date(),
          endTime: timeEntry.endTime,
          duration: timeEntry.duration || 0,
          description: timeEntry.description
        };

        return of(newTimeEntry).pipe(
          map(createdEntry => {
            this.notificationService.success('Успех', 'Запись времени создана');
            return TimeTrackingActions.createTimeEntrySuccess({ timeEntry: createdEntry });
          }),
          catchError(error => {
            this.notificationService.error('Ошибка', 'Не удалось создать запись времени');
            return of(TimeTrackingActions.createTimeEntryFailure({ error: error.message }));
          })
        );
      })
    )
  );

  // Обновление записи времени
  updateTimeEntry$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TimeTrackingActions.updateTimeEntry),
      mergeMap(({ timeEntry }) => {
        // Здесь будет реальный API вызов
        const updatedTimeEntry: TimeEntry = {
          ...timeEntry,
          taskId: timeEntry.taskId || '',
          userId: timeEntry.userId || '',
          startTime: timeEntry.startTime || new Date(),
          endTime: timeEntry.endTime,
          duration: timeEntry.duration || 0,
          description: timeEntry.description
        };

        return of(updatedTimeEntry).pipe(
          map(entry => {
            this.notificationService.success('Успех', 'Запись времени обновлена');
            return TimeTrackingActions.updateTimeEntrySuccess({ timeEntry: entry });
          }),
          catchError(error => {
            this.notificationService.error('Ошибка', 'Не удалось обновить запись времени');
            return of(TimeTrackingActions.updateTimeEntryFailure({ error: error.message }));
          })
        );
      })
    )
  );

  // Удаление записи времени
  deleteTimeEntry$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TimeTrackingActions.deleteTimeEntry),
      mergeMap(({ timeEntryId }) => {
        // Здесь будет реальный API вызов
        return of(timeEntryId).pipe(
          map(id => {
            this.notificationService.success('Успех', 'Запись времени удалена');
            return TimeTrackingActions.deleteTimeEntrySuccess({ timeEntryId: id });
          }),
          catchError(error => {
            this.notificationService.error('Ошибка', 'Не удалось удалить запись времени');
            return of(TimeTrackingActions.deleteTimeEntryFailure({ error: error.message }));
          })
        );
      })
    )
  );
}
