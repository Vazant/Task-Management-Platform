import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, mergeMap, catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

import * as AuthActions from './auth.actions';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { User } from '../../../core/models';

@Injectable()
export class AuthEffects {
  private actions$ = inject(Actions);
  private authService = inject(AuthService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  login$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.login),
      mergeMap(({ credentials }) =>
        this.authService.login(credentials).pipe(
          map((response) => {
            this.authService.setUser(
              response.user,
              response.token,
              response.refreshToken
            );
            this.notificationService.success('Успех', 'Вы успешно вошли в систему');
            return AuthActions.loginSuccess({
              user: response.user,
              token: response.token,
              refreshToken: response.refreshToken
            });
          }),
          catchError((error) => {
            const errorMessage = error.message || 'Ошибка входа в систему';
            this.notificationService.error('Ошибка', errorMessage);
            return of(AuthActions.loginFailure({ error: errorMessage }));
          })
        )
      )
    );
  });

  register$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.register),
      mergeMap(({ userData }) =>
        this.authService.register(userData).pipe(
          map((response) => {
            this.authService.setUser(
              response.user,
              response.token,
              response.refreshToken
            );
            this.notificationService.success('Успех', 'Регистрация прошла успешно');
            return AuthActions.registerSuccess({
              user: response.user,
              token: response.token,
              refreshToken: response.refreshToken
            });
          }),
          catchError((error) => {
            const errorMessage = error.message || 'Ошибка регистрации';
            this.notificationService.error('Ошибка', errorMessage);
            return of(AuthActions.registerFailure({ error: errorMessage }));
          })
        )
      )
    );
  });

  logout$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.logout),
      tap(() => {
        this.authService.logout();
        this.router.navigate(['/auth/login']);
      }),
      map(() => AuthActions.logoutSuccess())
    );
  });

  refreshToken$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.refreshToken),
      mergeMap(() =>
        this.authService.refreshToken().pipe(
          map(({ token, refreshToken }) => {
            localStorage.setItem('token', token);
            localStorage.setItem('refreshToken', refreshToken);
            return AuthActions.refreshTokenSuccess({ token, refreshToken });
          }),
          catchError((error) => {
            const errorMessage = error.message || 'Ошибка обновления токена';
            this.notificationService.error('Ошибка', errorMessage);
            return of(AuthActions.refreshTokenFailure({ error: errorMessage }));
          })
        )
      )
    );
  });

  forgotPassword$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.forgotPassword),
      mergeMap(({ email }) =>
        this.authService.forgotPassword(email).pipe(
          map(() => AuthActions.forgotPasswordSuccess({
            message: 'Инструкции отправлены на email'
          })),
          catchError((error) => {
            const errorMessage = error.message || 'Ошибка восстановления пароля';
            this.notificationService.error('Ошибка', errorMessage);
            return of(AuthActions.forgotPasswordFailure({ error: errorMessage }));
          })
        )
      )
    );
  });

  resetPassword$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.resetPassword),
      mergeMap(({ token, password }) =>
        this.authService.resetPassword(token, password).pipe(
          map(() => AuthActions.resetPasswordSuccess({
            message: 'Пароль успешно изменен'
          })),
          catchError((error) => {
            const errorMessage = error.message || 'Ошибка сброса пароля';
            this.notificationService.error('Ошибка', errorMessage);
            return of(AuthActions.resetPasswordFailure({ error: errorMessage }));
          })
        )
      )
    );
  });

  // Navigation effects
  loginSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.loginSuccess),
      tap(() => {
        this.router.navigate(['/dashboard']);
      })
    );
  }, { dispatch: false });

  registerSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.registerSuccess),
      tap(() => {
        this.router.navigate(['/dashboard']);
      })
    );
  }, { dispatch: false });

  forgotPasswordSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.forgotPasswordSuccess),
      tap(({ message }) => {
        this.notificationService.success('Успех', message);
      })
    );
  }, { dispatch: false });

  resetPasswordSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.resetPasswordSuccess),
      tap(({ message }) => {
        this.notificationService.success('Успех', message);
        this.router.navigate(['/auth/login']);
      })
    );
  }, { dispatch: false });
}
