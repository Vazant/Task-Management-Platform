import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, tap, switchMap } from 'rxjs';
import { ApiService, NotificationService } from '@services';
import { AvatarService } from '../../../core/services/avatar.service';
import { User } from '../../../core/models/user.model';
import { UserProfile, UpdateProfileRequest, UpdateAvatarRequest } from '../models/user-profile.model';

export interface ProfileState {
  profile: UserProfile | null;
  isLoading: boolean;
  error: string | null;
  hasError: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly apiService = inject(ApiService);
  private readonly notificationService = inject(NotificationService);
  private readonly avatarService = inject(AvatarService);

  // Сигналы для состояния
  private readonly _profile = signal<UserProfile | null>(null);
  private readonly _isLoading = signal(false);
  private readonly _error = signal<string | null>(null);
  private readonly _hasError = signal(false);

  // Публичные readonly сигналы
  readonly profile = this._profile.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly error = this._error.asReadonly();
  readonly hasError = this._hasError.asReadonly();

  constructor() {
    this.loadProfile();
  }

  /**
   * Загружает профиль пользователя с сервера
   */
  loadProfile(): Observable<UserProfile | null> {
    this._isLoading.set(true);
    this._error.set(null);
    this._hasError.set(false);

    return this.apiService.get<UserProfile>('/profile').pipe(
      map(response => response.data),
      tap(profile => {
        this._profile.set(profile);
        this._isLoading.set(false);
        this._hasError.set(false);
      }),
      catchError(error => {
        console.error('Ошибка загрузки профиля:', error);
        const errorMessage = this.getErrorMessage(error);
        this._error.set(errorMessage);
        this._isLoading.set(false);
        this._hasError.set(true);

        // Показываем уведомление с кнопкой retry
        this.notificationService.showError(
          'Ошибка загрузки профиля',
          'Ошибка загрузки профиля'
        );

        return of(null);
      })
    );
  }

  /**
   * Обновляет профиль пользователя
   */
  updateProfile(request: UpdateProfileRequest): Observable<UserProfile | null> {
    this._isLoading.set(true);
    this._error.set(null);
    this._hasError.set(false);

    return this.apiService.put<UserProfile>('/profile', request).pipe(
      map(response => response.data),
      tap(profile => {
        this._profile.set(profile);
        this._isLoading.set(false);
        this._hasError.set(false);
        this.notificationService.showSuccess('Успех', 'Профиль успешно обновлен');
      }),
      catchError(error => {
        console.error('Ошибка обновления профиля:', error);
        const errorMessage = this.getErrorMessage(error);
        this._error.set(errorMessage);
        this._isLoading.set(false);
        this._hasError.set(true);

        this.notificationService.showError('Ошибка обновления профиля', errorMessage);
        return of(null);
      })
    );
  }

  /**
   * Обновляет аватар пользователя через новый AvatarService
   */
  updateAvatar(request: UpdateAvatarRequest): Observable<UserProfile | null> {
    this._isLoading.set(true);
    this._error.set(null);
    this._hasError.set(false);

    // Получаем текущий профиль для userId
    const currentProfile = this._profile();
    if (!currentProfile) {
      this._isLoading.set(false);
      this._hasError.set(true);
      this._error.set('Профиль не загружен');
      return of(null);
    }

    // Генерируем upload URL
    const uploadRequest = {
      fileName: request.avatar.name,
      fileSize: request.avatar.size,
      contentType: request.avatar.type
    };

    return this.avatarService.upload(request.avatar).pipe(
      map((user: User) => {
        // Преобразуем User в UserProfile
        const updatedProfile: UserProfile = {
          id: user.id,
          email: user.email,
          username: user.username,
          displayName: user.displayName,
          avatar: user.avatarUrl,
          role: user.role,
          createdAt: user.createdAt,
          updatedAt: user.updatedAt || new Date()
        };
        
        this._profile.set(updatedProfile);
        this._isLoading.set(false);
        this._hasError.set(false);
        this.notificationService.showSuccess('Успех', 'Аватар успешно обновлен');
        return updatedProfile;
      }),
      catchError(error => {
        console.error('Ошибка обновления аватара:', error);
        const errorMessage = this.getErrorMessage(error);
        this._error.set(errorMessage);
        this._isLoading.set(false);
        this._hasError.set(true);
        this.notificationService.showError('Ошибка обновления аватара', errorMessage);
        return of(null);
      })
    );
  }

  /**
   * Загружает файл на presigned URL
   */
  private uploadFileToPresignedUrl(presignedUrl: string, file: File): Observable<any> {
    return new Observable(observer => {
      const xhr = new XMLHttpRequest();
      
      xhr.open('PUT', presignedUrl);
      xhr.setRequestHeader('Content-Type', file.type);
      
      xhr.onload = () => {
        if (xhr.status >= 200 && xhr.status < 300) {
          observer.next(xhr.response);
          observer.complete();
        } else {
          observer.error(new Error(`Upload failed with status: ${xhr.status}`));
        }
      };
      
      xhr.onerror = () => {
        observer.error(new Error('Upload failed'));
      };
      
      xhr.send(file);
    });
  }

  /**
   * Удаляет аватар пользователя через новый AvatarService
   */
  deleteAvatar(): Observable<UserProfile | null> {
    this._isLoading.set(true);
    this._error.set(null);
    this._hasError.set(false);

    // Получаем текущий профиль для userId
    const currentProfile = this._profile();
    if (!currentProfile) {
      this._isLoading.set(false);
      this._hasError.set(true);
      this._error.set('Профиль не загружен');
      return of(null);
    }

    return this.avatarService.deleteAvatar().pipe(
      map(() => {
        // Обновляем профиль, убирая аватар
        const updatedProfile = {
          ...currentProfile,
          avatar: undefined
        };
        this._profile.set(updatedProfile);
        this._isLoading.set(false);
        this._hasError.set(false);
        this.notificationService.showSuccess('Успех', 'Аватар успешно удален');
        return updatedProfile;
      }),
      catchError(error => {
        console.error('Ошибка удаления аватара:', error);
        const errorMessage = this.getErrorMessage(error);
        this._error.set(errorMessage);
        this._isLoading.set(false);
        this._hasError.set(true);

        this.notificationService.showError('Ошибка удаления аватара', errorMessage);
        return of(null);
      })
    );
  }

  /**
   * Изменяет пароль пользователя
   */
  changePassword(currentPassword: string, newPassword: string): Observable<{ message: string } | null> {
    this._isLoading.set(true);
    this._error.set(null);
    this._hasError.set(false);

    const request = {
      currentPassword,
      newPassword
    };

    return this.apiService.post<{ message: string }>('/profile/change-password', request).pipe(
      map(response => response.data),
      tap(() => {
        this._isLoading.set(false);
        this._hasError.set(false);
        this.notificationService.showSuccess('Успех', 'Пароль успешно изменен');
      }),
      catchError(error => {
        console.error('Ошибка смены пароля:', error);
        const errorMessage = this.getErrorMessage(error);
        this._error.set(errorMessage);
        this._isLoading.set(false);
        this._hasError.set(true);

        this.notificationService.showError('Ошибка смены пароля', errorMessage);
        return of(null);
      })
    );
  }

  /**
   * Получает текущий профиль из состояния
   */
  getCurrentProfile(): UserProfile | null {
    return this._profile();
  }

  /**
   * Очищает ошибку
   */
  clearError(): void {
    this._error.set(null);
    this._hasError.set(false);
  }

  /**
   * Сбрасывает состояние загрузки
   */
  resetLoading(): void {
    this._isLoading.set(false);
  }

  /**
   * Повторная попытка загрузки профиля
   */
  retryLoadProfile(): void {
    this.loadProfile().subscribe();
  }

  /**
   * Обрабатывает ошибки и возвращает понятное сообщение
   */
  private getErrorMessage(error: unknown): string {
    if (error && typeof error === 'object' && 'error' in error && error.error && typeof error.error === 'object' && 'message' in error.error) {
      return String(error.error.message);
    }

    if (error && typeof error === 'object' && 'message' in error) {
      return String(error.message);
    }

    if (error && typeof error === 'object' && 'status' in error) {
      const status = Number(error.status);
      switch (status) {
        case 0:
          return 'Сервер недоступен. Проверьте подключение к интернету.';
        case 400:
          return 'Неверные данные запроса';
        case 401:
          return 'Необходима авторизация';
        case 403:
          return 'Доступ запрещен';
        case 404:
          return 'Профиль не найден';
        case 409:
          return 'Пользователь с таким email уже существует';
        case 413:
          return 'Файл слишком большой';
        case 415:
          return 'Неподдерживаемый тип файла';
        case 500:
          return 'Внутренняя ошибка сервера';
        default:
          return 'Произошла ошибка при выполнении запроса';
      }
    }

    return 'Произошла неизвестная ошибка';
  }
}
