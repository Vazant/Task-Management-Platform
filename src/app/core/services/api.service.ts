import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:3000/api'; // Замените на ваш API URL

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    });
  }

  get<T>(endpoint: string, params?: any): Observable<ApiResponse<T>> {
    const options = {
      headers: this.getHeaders(),
      params: new HttpParams({ fromObject: params || {} })
    };
    return this.http.get<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, options);
  }

  getPaginated<T>(endpoint: string, page: number = 1, limit: number = 10, params?: any): Observable<PaginatedResponse<T>> {
    const queryParams = {
      page: page.toString(),
      limit: limit.toString(),
      ...params
    };
    return this.http.get<PaginatedResponse<T>>(`${this.baseUrl}${endpoint}`, {
      headers: this.getHeaders(),
      params: new HttpParams({ fromObject: queryParams })
    });
  }

  post<T>(endpoint: string, data: any): Observable<ApiResponse<T>> {
    const options = { headers: this.getHeaders() };
    return this.http.post<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, data, options);
  }

  put<T>(endpoint: string, data: any): Observable<ApiResponse<T>> {
    const options = { headers: this.getHeaders() };
    return this.http.put<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, data, options);
  }

  patch<T>(endpoint: string, data: any): Observable<ApiResponse<T>> {
    const options = { headers: this.getHeaders() };
    return this.http.patch<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, data, options);
  }

  delete<T>(endpoint: string): Observable<ApiResponse<T>> {
    const options = { headers: this.getHeaders() };
    return this.http.delete<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, options);
  }
} 