import { HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (request, next) => {
  return next(request).pipe(
    catchError(error => {
      console.error('HTTP Error:', error);
      return throwError(() => error);
    })
  );
};
