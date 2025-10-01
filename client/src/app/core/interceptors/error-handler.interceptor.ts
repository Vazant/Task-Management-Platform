import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, timer } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { inject } from '@angular/core';
import { NotificationService } from '@services';

export interface ApiError {
  message: string;
  status: number;
  statusText: string;
  url: string;
  timestamp: Date;
}

// Функция-интерцептор для Angular 16+ с современными RxJS операторами
export const errorHandlerInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    // Заменяем retryWhen на retry с delayWhen для экспоненциальной задержки
    retry({
      count: 3,
      delay: (error: HttpErrorResponse, retryCount: number) => {
        // Не повторяем для определенных ошибок
        if (shouldNotRetry(error, retryCount)) {
          return throwError(() => error);
        }

        // Экспоненциальная задержка: 1s, 2s, 4s, 8s...
        const delayMs = Math.min(1000 * Math.pow(2, retryCount), 10000);

        console.warn(`Retry attempt ${retryCount + 1} for ${req.url} after ${delayMs}ms`);

        return timer(delayMs);
      }
    }),
    catchError((error: HttpErrorResponse) => {
      const apiError = createApiError(error, req);

      // Логируем ошибку для разработчиков
      console.error('API Error:', apiError);

      // Показываем уведомление только для критичных ошибок (0, 5xx)
      // Для 4xx ошибок уведомления будут показываться в effects
      if (error.status === 0 || (error.status >= 500)) {
        notificationService.error('Ошибка сервера', apiError.message);
      }

      return throwError(() => apiError);
    })
  );
};

function shouldNotRetry(error: HttpErrorResponse, attemptIndex: number): boolean {
  // Не повторяем для клиентских ошибок (4xx), кроме 408, 429
  if (error.status >= 400 && error.status < 500 && ![408, 429].includes(error.status)) {
    return true;
  }

  // Не повторяем больше 3 раз
  if (attemptIndex >= 2) {
    return true;
  }

  // Не повторяем для определенных эндпоинтов
  const noRetryEndpoints = ['/auth/logout', '/profile/avatar'];
  if (noRetryEndpoints.some(endpoint => error.url?.includes(endpoint))) {
    return true;
  }

  return false;
}

function createApiError(error: HttpErrorResponse, request: HttpRequest<unknown>): ApiError {
  return {
    message: getUserFriendlyMessage(error),
    status: error.status,
    statusText: error.statusText,
    url: request.url,
    timestamp: new Date()
  };
}

function getUserFriendlyMessage(error: HttpErrorResponse): string {
  const status = error.status;

  switch (status) {
    case 0:
      return 'Сервер недоступен. Проверьте подключение к интернету.';
    case 400:
      return 'Неверный запрос. Проверьте введенные данные.';
    case 401:
      // Для эндпоинта логина показываем специальное сообщение
      if (error.url?.includes('/auth/login')) {
        return 'Неверный email или пароль';
      }
      return 'Необходима авторизация. Войдите в систему.';
    case 403:
      return 'Доступ запрещен. У вас недостаточно прав.';
    case 404:
      return 'Запрашиваемый ресурс не найден.';
    case 408:
      return 'Превышено время ожидания ответа сервера.';
    case 409:
      return 'Конфликт данных. Возможно, запись уже существует.';
    case 413:
      return 'Файл слишком большой. Уменьшите размер файла.';
    case 415:
      return 'Неподдерживаемый тип файла.';
    case 429:
      return 'Слишком много запросов. Попробуйте позже.';
    case 500:
      return 'Внутренняя ошибка сервера. Попробуйте позже.';
    case 502:
      return 'Ошибка шлюза. Сервер временно недоступен.';
    case 503:
      return 'Сервис временно недоступен. Попробуйте позже.';
    case 504:
      return 'Превышено время ожидания шлюза.';
    default:
      return 'Произошла неизвестная ошибка. Попробуйте позже.';
  }
}
