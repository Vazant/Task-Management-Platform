import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AuthState } from './auth.state';

export const selectAuthState = createFeatureSelector<AuthState>('auth');

export const selectUser = createSelector(
  selectAuthState,
  (state) => state.user
);

export const selectToken = createSelector(
  selectAuthState,
  (state) => state.token
);

export const selectRefreshToken = createSelector(
  selectAuthState,
  (state) => state.refreshToken
);

export const selectIsLoading = createSelector(
  selectAuthState,
  (state) => state.loading
);

export const selectError = createSelector(
  selectAuthState,
  (state) => state.error
);

export const selectIsAuthenticated = createSelector(
  selectAuthState,
  (state) => state.isAuthenticated
);

export const selectUsername = createSelector(
  selectUser,
  (user) => user?.username
);

export const selectUserRole = createSelector(
  selectUser,
  (user) => user?.role
);

export const selectUserEmail = createSelector(
  selectUser,
  (user) => user?.email
);

// Временный селектор для success сообщений (можно расширить state позже)
export const selectSuccess = createSelector(
  selectAuthState,
  (state) => state.error // Временно используем error для success
);
