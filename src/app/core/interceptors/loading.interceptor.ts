import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { NotificationService } from '@services';

export const loadingInterceptor: HttpInterceptorFn = (request, next) => {
  const notificationService = inject(NotificationService);

  // Показываем индикатор загрузки
  notificationService.info('Загрузка', 'Выполняется запрос...', 0);

  return next(request).pipe(
    finalize(() => {
      // Скрываем индикатор загрузки
      notificationService.clearAll();
    })
  );
};
