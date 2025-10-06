import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, map, of, delay } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AvatarService {
  private readonly baseUrl = '/api/users/me';
  private currentUser: User | null = null;

  constructor(private http: HttpClient) {}

  /**
   * Загрузить аватар: multipart/form-data -> сервер возвращает обновлённого пользователя
   */
  upload(file: File): Observable<User> {
    // Для демо - создаем mock пользователя с аватаром
    const mockUser: User = {
      id: '1',
      email: 'demo@example.com',
      username: 'demo_user',
      displayName: 'Demo User',
      avatarUrl: URL.createObjectURL(file), // Создаем URL из файла
      role: 'user',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    this.currentUser = mockUser;

    // Имитируем задержку сети
    return of(mockUser).pipe(delay(500));
  }

  /**
   * Удалить текущий аватар
   */
  deleteAvatar(): Observable<User> {
    const mockUser: User = {
      id: '1',
      email: 'demo@example.com',
      username: 'demo_user',
      displayName: 'Demo User',
      avatarUrl: undefined, // Убираем аватар
      role: 'user',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    this.currentUser = mockUser;

    // Имитируем задержку сети
    return of(mockUser).pipe(delay(300));
  }

  /**
   * Утилита для плейсхолдера
   */
  getAvatarUrl(user: User | null, fallback: string = '/assets/images/avatar-placeholder.svg'): string {
    return user?.avatarUrl ?? fallback;
  }
}