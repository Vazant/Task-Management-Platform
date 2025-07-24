import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map } from 'rxjs';
import { ApiService } from './api.service';
import { User, LoginRequest, LoginResponse, RegisterRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private apiService: ApiService) {
    this.loadUserFromStorage();
  }

  private loadUserFromStorage(): void {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        this.currentUserSubject.next(user);
      } catch (error) {
        console.error('Error parsing user from storage:', error);
        this.logout();
      }
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.apiService.post<LoginResponse>('/auth/login', credentials).pipe(
      map(response => response.data)
    );
  }

  register(userData: RegisterRequest): Observable<LoginResponse> {
    return this.apiService.post<LoginResponse>('/auth/register', userData).pipe(
      map(response => response.data)
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  setUser(user: User, token: string, refreshToken?: string): void {
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('token', token);
    if (refreshToken) {
      localStorage.setItem('refreshToken', refreshToken);
    }
    this.currentUserSubject.next(user);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isAuthenticated(): boolean {
    return !!this.getCurrentUser() && !!localStorage.getItem('token');
  }

  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'admin';
  }

  refreshToken(): Observable<{ token: string; refreshToken: string }> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.apiService.post<{ token: string; refreshToken: string }>(
      '/auth/refresh',
      { refreshToken }
    ).pipe(
      map(response => response.data)
    );
  }

  forgotPassword(email: string): Observable<any> {
    return this.apiService.post('/auth/forgot-password', { email }).pipe(
      map(response => response.data)
    );
  }

  resetPassword(token: string, password: string): Observable<any> {
    return this.apiService.post('/auth/reset-password', { token, password }).pipe(
      map(response => response.data)
    );
  }
} 