import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '@services';
import { ApiResponse } from '@models';
import { environment } from '../../../environments/environment';

export interface AvatarResponse {
  avatarUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class AvatarService {
  private readonly apiService = inject(ApiService);

  /**
   * Генерирует новый аватар для пользователя
   */
  generateAvatar(userId: string, username: string): Observable<AvatarResponse> {
    return this.apiService.post<AvatarResponse>(`/avatars/generate/${userId}?username=${encodeURIComponent(username)}`, {})
      .pipe(map((response: ApiResponse<AvatarResponse>) => response.data));
  }

  /**
   * Получает дефолтный URL аватара
   */
  getDefaultAvatar(): Observable<AvatarResponse> {
    return this.apiService.get<AvatarResponse>('/avatars/default')
      .pipe(map((response: ApiResponse<AvatarResponse>) => response.data));
  }

  /**
   * Получает полный URL аватара с учетом сервера
   */
  getAvatarUrl(avatarPath: string | null | undefined): string {
    if (!avatarPath) {
      return '/images/default-avatar.png';
    }

    // Если это уже полный URL, возвращаем как есть
    if (avatarPath.startsWith('http')) {
      return avatarPath;
    }

    // Если это относительный путь к аватару, добавляем базовый URL сервера
    if (avatarPath.startsWith('/avatars/') || avatarPath.startsWith('/images/')) {
      return `${environment.apiUrl}${avatarPath}`;
    }

    // Если это путь к локальному аватару, возвращаем как есть
    if (avatarPath.startsWith('assets/')) {
      return avatarPath;
    }

    // По умолчанию возвращаем дефолтный аватар
    return '/images/default-avatar.png';
  }

  /**
   * Удаляет аватар пользователя
   */
  deleteAvatar(userId: string): Observable<void> {
    return this.apiService.delete<void>(`/avatars/${userId}`)
      .pipe(map((response: ApiResponse<void>) => response.data));
  }

  /**
   * Генерирует инициалы из имени пользователя
   */
  generateInitials(name: string): string {
    if (!name) return 'U';

    const parts = name.trim().split(/\s+/);
    if (parts.length >= 2) {
      return (parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
    } else if (name.length >= 2) {
      return name.substring(0, 2).toUpperCase();
    } else {
      return name.toUpperCase();
    }
  }

  /**
   * Генерирует цвет фона на основе имени пользователя
   */
  generateBackgroundColor(name: string): string {
    if (!name) return '#3B82F6';

    // Простая хеш-функция для генерации цвета
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }

    const colors = [
      '#3B82F6', // Blue
      '#10B981', // Green
      '#F59E0B', // Yellow
      '#EF4444', // Red
      '#8B5CF6', // Purple
      '#EC4899', // Pink
      '#06B6D4', // Cyan
      '#84CC16'  // Lime
    ];

    return colors[Math.abs(hash) % colors.length];
  }
}
