export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
  errors?: string[];
}

export interface PaginatedResponse<T> extends ApiResponse<T[]> {
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  user: any;
  token: string;
  refreshToken: string;
}

export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  confirmPassword: string;
} 