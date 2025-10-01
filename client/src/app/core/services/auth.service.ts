import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { ApiService } from '@services';
import { User, LoginRequest, LoginResponse, RegisterRequest } from '@models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly currentUserSubject = new BehaviorSubject<User | null>(null);
  public readonly currentUser$ = this.currentUserSubject.asObservable();
  private readonly isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public readonly isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private readonly apiService = inject(ApiService);

  constructor() {
    this.loadUserFromStorage();
  }

  private loadUserFromStorage(): void {
    const userStr = localStorage.getItem('user');
    const token = localStorage.getItem('token');

    if (userStr && token) {
      try {
        const user = JSON.parse(userStr);
        this.currentUserSubject.next(user);
        this.isAuthenticatedSubject.next(true);
      } catch (error) {
        console.error('Error parsing user from storage:', error);
        this.logout();
      }
    } else {
      this.isAuthenticatedSubject.next(false);
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    console.log('AuthService.login called with credentials:', credentials);
    return this.apiService.post<LoginResponse>('/auth/login', credentials).pipe(
      map(response => {
        console.log('Login response received:', response);
        return response.data;
      }),
      tap(response => {
        console.log('Setting user after login:', response.user);
        this.setUser(response.user, response.token, response.refreshToken);
      })
    );
  }

  register(userData: RegisterRequest): Observable<LoginResponse> {
    console.log('AuthService.register called with userData:', userData);
    return this.apiService.post<LoginResponse>('/auth/register', userData).pipe(
      map(response => {
        console.log('Register response received:', response);
        return response.data;
      }),
      tap(response => {
        console.log('Setting user after register:', response.user);
        this.setUser(response.user, response.token, response.refreshToken);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }

  setUser(user: User, token: string, refreshToken?: string): void {
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('token', token);
    if (refreshToken) {
      localStorage.setItem('refreshToken', refreshToken);
    }
    this.currentUserSubject.next(user);
    this.isAuthenticatedSubject.next(true);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  get isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }



  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'admin';
  }

  checkTokenValidity(): boolean {
    const token = localStorage.getItem('token');
    if (!token) {
      return false;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;

      if (payload.exp < currentTime) {
        this.logout();
        return false;
      }

      return true;
    } catch (error) {
      console.error('Error checking token validity:', error);
      this.logout();
      return false;
    }
  }

  refreshToken(): Observable<{ token: string; refreshToken: string }> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.apiService.post<{ token: string; refreshToken: string }>(
      '/auth/refresh',
      { refreshToken }
    ).pipe(
      map(response => response.data),
      tap(response => {
        localStorage.setItem('token', response.token);
        if (response.refreshToken) {
          localStorage.setItem('refreshToken', response.refreshToken);
        }
      })
    );
  }

  forgotPassword(email: string): Observable<{ message: string }> {
    return this.apiService.post<{ message: string }>('/auth/forgot-password', { email }).pipe(
      map(response => response.data)
    );
  }

  resetPassword(token: string, password: string): Observable<{ message: string }> {
    return this.apiService.post<{ message: string }>('/auth/reset-password', { token, password }).pipe(
      map(response => response.data)
    );
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  updateProfile(profileData: Partial<User>): Observable<User> {
    return this.apiService.put<User>('/auth/profile', profileData).pipe(
      map(response => response.data),
      tap(user => {
        this.currentUserSubject.next(user);
        localStorage.setItem('user', JSON.stringify(user));
      })
    );
  }
}
