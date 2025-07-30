import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiResponse, PaginatedResponse } from '@models';



@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = 'http://localhost:3000/api'; // Замените на ваш API URL
  private readonly http = inject(HttpClient);

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  private getFileHeaders(): HttpHeaders {
    return new HttpHeaders({
      // 'Content-Type': 'multipart/form-data' // This header is often handled by the browser
    });
  }

  get<T>(endpoint: string, params?: Record<string, string | number | boolean>): Observable<ApiResponse<T>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach((key: string) => {
        httpParams = httpParams.set(key, params[key]);
      });
    }

    return this.http.get<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, {
      headers: this.getHeaders(),
      params: httpParams
    }).pipe(
      catchError(this.handleError)
    );
  }

  getPaginated<T>(endpoint: string, page = 1, limit = 10, params?: Record<string, string | number | boolean>): Observable<PaginatedResponse<T>> {
    let httpParams = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    if (params) {
      Object.keys(params).forEach((key: string) => {
        httpParams = httpParams.set(key, params[key]);
      });
    }

    return this.http.get<PaginatedResponse<T>>(`${this.baseUrl}${endpoint}`, {
      headers: this.getHeaders(),
      params: httpParams
    }).pipe(
      catchError(this.handleError)
    );
  }

  post<T>(endpoint: string, data: unknown): Observable<ApiResponse<T>> {
    console.log('API POST request:', `${this.baseUrl}${endpoint}`, data);
    return this.http.post<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  postFile<T>(endpoint: string, formData: FormData): Observable<ApiResponse<T>> {
    return this.http.post<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, formData, {
      headers: this.getFileHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  put<T>(endpoint: string, data: unknown): Observable<ApiResponse<T>> {
    return this.http.put<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  patch<T>(endpoint: string, data: unknown): Observable<ApiResponse<T>> {
    return this.http.patch<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  delete<T>(endpoint: string): Observable<ApiResponse<T>> {
    return this.http.delete<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: unknown): Observable<never> {
    console.error('API Error:', error);

    let errorMessage = 'Произошла ошибка при выполнении запроса';

        // Type guard для проверки типа ошибки
    if (error && typeof error === 'object') {
      const errorObj = error as Record<string, unknown>;

      if (errorObj['error'] && typeof errorObj['error'] === 'object') {
        const errorData = errorObj['error'] as Record<string, unknown>;
        if (typeof errorData['message'] === 'string') {
          errorMessage = errorData['message'];
        }
      } else if (typeof errorObj['message'] === 'string') {
        errorMessage = errorObj['message'];
      } else if (typeof errorObj['status'] === 'number') {
        const status = errorObj['status'];
        if (status === 0) {
          errorMessage = 'Нет соединения с сервером';
        } else if (status === 401) {
          errorMessage = 'Необходима авторизация';
        } else if (status === 403) {
          errorMessage = 'Доступ запрещен';
        } else if (status === 404) {
          errorMessage = 'Ресурс не найден';
        } else if (status === 500) {
          errorMessage = 'Внутренняя ошибка сервера';
        }
      }
    }

    return throwError(() => ({ message: errorMessage }));
  }
}
