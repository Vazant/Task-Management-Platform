import { createAction, props } from '@ngrx/store';
import { User, LoginRequest, RegisterRequest } from '@models';

// Login Actions
export const login = createAction(
  '[Auth] Login',
  props<{ credentials: LoginRequest }>()
);

export const loginSuccess = createAction(
  '[Auth] Login Success',
  props<{ user: User; token: string; refreshToken: string }>()
);

export const loginFailure = createAction(
  '[Auth] Login Failure',
  props<{ error: string }>()
);

// Register Actions
export const register = createAction(
  '[Auth] Register',
  props<{ userData: RegisterRequest }>()
);

export const registerSuccess = createAction(
  '[Auth] Register Success',
  props<{ user: User; token: string; refreshToken: string }>()
);

export const registerFailure = createAction(
  '[Auth] Register Failure',
  props<{ error: string }>()
);

// Logout Actions
export const logout = createAction('[Auth] Logout');

export const logoutSuccess = createAction('[Auth] Logout Success');

// Token Refresh Actions
export const refreshToken = createAction('[Auth] Refresh Token');

export const refreshTokenSuccess = createAction(
  '[Auth] Refresh Token Success',
  props<{ token: string; refreshToken: string }>()
);

export const refreshTokenFailure = createAction(
  '[Auth] Refresh Token Failure',
  props<{ error: string }>()
);

// Forgot Password Actions
export const forgotPassword = createAction(
  '[Auth] Forgot Password',
  props<{ email: string }>()
);

export const forgotPasswordSuccess = createAction(
  '[Auth] Forgot Password Success',
  props<{ message: string }>()
);

export const forgotPasswordFailure = createAction(
  '[Auth] Forgot Password Failure',
  props<{ error: string }>()
);

// Reset Password Actions
export const resetPassword = createAction(
  '[Auth] Reset Password',
  props<{ token: string; password: string }>()
);

export const resetPasswordSuccess = createAction(
  '[Auth] Reset Password Success',
  props<{ message: string }>()
);

export const resetPasswordFailure = createAction(
  '[Auth] Reset Password Failure',
  props<{ error: string }>()
);

// Clear Error Action
export const clearAuthError = createAction('[Auth] Clear Error');
