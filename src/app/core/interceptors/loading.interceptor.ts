import { HttpInterceptorFn } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

export const loadingInterceptor: HttpInterceptorFn = (request, next) => {
  // Здесь можно добавить логику для показа/скрытия спиннера
  console.log('Loading started for:', request.url);

  return next(request).pipe(
    finalize(() => {
      console.log('Loading finished for:', request.url);
    })
  );
};
