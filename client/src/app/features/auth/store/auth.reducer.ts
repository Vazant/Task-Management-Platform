import { createReducer, on } from '@ngrx/store';
import { AuthState } from './auth.state';
import * as AuthActions from './auth.actions';

export const initialState: AuthState = {
  user: null,
  token: null,
  refreshToken: null,
  loading: false,
  error: null,
  isAuthenticated: false
};

export const authReducer = createReducer(
  initialState,
  
  // Login
  on(AuthActions.login, (state) => ({
    ...state,
    loading: true,
    error: null
  })),
  
  on(AuthActions.loginSuccess, (state, { user, token, refreshToken }) => ({
    ...state,
    user,
    token,
    refreshToken,
    loading: false,
    error: null,
    isAuthenticated: true
  })),
  
  on(AuthActions.loginFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
    isAuthenticated: false
  })),
  
  // Register
  on(AuthActions.register, (state) => ({
    ...state,
    loading: true,
    error: null
  })),
  
  on(AuthActions.registerSuccess, (state, { user, token, refreshToken }) => ({
    ...state,
    user,
    token,
    refreshToken,
    loading: false,
    error: null,
    isAuthenticated: true
  })),
  
  on(AuthActions.registerFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
    isAuthenticated: false
  })),
  
  // Logout
  on(AuthActions.logout, (state) => ({
    ...state,
    loading: true
  })),
  
  on(AuthActions.logoutSuccess, () => ({
    ...initialState
  })),
  
  // Refresh Token
  on(AuthActions.refreshToken, (state) => ({
    ...state,
    loading: true,
    error: null
  })),
  
  on(AuthActions.refreshTokenSuccess, (state, { token, refreshToken }) => ({
    ...state,
    token,
    refreshToken,
    loading: false,
    error: null
  })),
  
  on(AuthActions.refreshTokenFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
    isAuthenticated: false
  })),
  
  // Forgot Password
  on(AuthActions.forgotPassword, (state) => ({
    ...state,
    loading: true,
    error: null
  })),
  
  on(AuthActions.forgotPasswordSuccess, (state) => ({
    ...state,
    loading: false,
    error: null
  })),
  
  on(AuthActions.forgotPasswordFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  
  // Reset Password
  on(AuthActions.resetPassword, (state) => ({
    ...state,
    loading: true,
    error: null
  })),
  
  on(AuthActions.resetPasswordSuccess, (state) => ({
    ...state,
    loading: false,
    error: null
  })),
  
  on(AuthActions.resetPasswordFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  
  // Clear Error
  on(AuthActions.clearAuthError, (state) => ({
    ...state,
    error: null
  }))
); 