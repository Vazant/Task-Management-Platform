import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, mergeMap, catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

import * as AuthActions from './auth.actions';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable()
export class AuthEffects {
  constructor(
    private actions$: Actions,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

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
            return AuthActions.loginSuccess({
              user: response.user,
              token: response.token,
              refreshToken: response.refreshToken
            });
          }),
          catchError((error) =>
            of(AuthActions.loginFailure({ error: error.message }))
          )
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
            return AuthActions.registerSuccess({
              user: response.user,
              token: response.token,
              refreshToken: response.refreshToken
            });
          }),
          catchError((error) =>
            of(AuthActions.registerFailure({ error: error.message }))
          )
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
          catchError((error) =>
            of(AuthActions.refreshTokenFailure({ error: error.message }))
          )
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
          catchError((error) =>
            of(AuthActions.forgotPasswordFailure({ error: error.message }))
          )
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
          catchError((error) =>
            of(AuthActions.resetPasswordFailure({ error: error.message }))
          )
        )
      )
    );
  });

  // Navigation effects
  loginSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.loginSuccess),
      tap(() => {
        this.notificationService.success('Успех', 'Вы успешно вошли в систему');
        this.router.navigate(['/dashboard']);
      })
    );
  }, { dispatch: false });

  registerSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.registerSuccess),
      tap(() => {
        this.notificationService.success('Успех', 'Регистрация прошла успешно');
        this.router.navigate(['/dashboard']);
      })
    );
  }, { dispatch: false });

  // Error handling effects
  authFailure$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AuthActions.loginFailure,
        AuthActions.registerFailure,
        AuthActions.refreshTokenFailure
      ),
      tap(({ error }) => {
        this.notificationService.error('Ошибка', error);
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
