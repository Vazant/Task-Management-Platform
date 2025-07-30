import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { NotificationService } from '@services';

export const loadingInterceptor: HttpInterceptorFn = (request, next) => {
  const notificationService = inject(NotificationService);

  // Показываем индикатор загрузки только для определенных запросов
  // Исключаем запросы к профилю и другим критичным данным
  const shouldShowLoading = !request.url.includes('/auth/refresh') &&
                           !request.url.includes('/profile') &&
                           !request.url.includes('/user/current');

  if (shouldShowLoading) {
    notificationService.info('Загрузка', 'Выполняется запрос...', 0);
  }

  return next(request).pipe(
    finalize(() => {
      // Скрываем индикатор загрузки
      if (shouldShowLoading) {
        notificationService.clearAll();
      }
    })
  );
};
