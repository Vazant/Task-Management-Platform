import { User } from '@models';

export interface AuthState {
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  loading: boolean;
  error: string | null;
  success: string | null;
  isAuthenticated: boolean;
}

