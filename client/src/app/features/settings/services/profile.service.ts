import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { ApiService, NotificationService } from '@services';
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
          () => this.loadProfile()
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
        this.notificationService.success('Успех', 'Профиль успешно обновлен');
      }),
      catchError(error => {
        console.error('Ошибка обновления профиля:', error);
        const errorMessage = this.getErrorMessage(error);
        this._error.set(errorMessage);
        this._isLoading.set(false);
        this._hasError.set(true);

        this.notificationService.error('Ошибка обновления профиля', errorMessage);
        return of(null);
      })
    );
  }

  /**
   * Обновляет аватар пользователя
   */
  updateAvatar(request: UpdateAvatarRequest): Observable<UserProfile | null> {
    this._isLoading.set(true);
    this._error.set(null);
    this._hasError.set(false);

    // Создаем FormData для отправки файла
    const formData = new FormData();
    formData.append('avatar', request.avatar);

    return this.apiService.postFile<UserProfile>('/profile/avatar', formData).pipe(
      map(response => response.data),
      tap(profile => {
        this._profile.set(profile);
        this._isLoading.set(false);
        this._hasError.set(false);
        this.notificationService.success('Успех', 'Аватар успешно обновлен');
      }),
      catchError(error => {
        console.error('Ошибка обновления аватара:', error);
        const errorMessage = this.getErrorMessage(error);
        this._error.set(errorMessage);
        this._isLoading.set(false);
        this._hasError.set(true);

        this.notificationService.error('Ошибка обновления аватара', errorMessage);
        return of(null);
      })
    );
  }

  /**
   * Удаляет аватар пользователя
   */
  deleteAvatar(): Observable<UserProfile | null> {
    this._isLoading.set(true);
    this._error.set(null);
    this._hasError.set(false);

    return this.apiService.delete<UserProfile>('/profile/avatar').pipe(
      map(response => response.data),
      tap(profile => {
        this._profile.set(profile);
        this._isLoading.set(false);
        this._hasError.set(false);
        this.notificationService.success('Успех', 'Аватар успешно удален');
      }),
      catchError(error => {
        console.error('Ошибка удаления аватара:', error);
        const errorMessage = this.getErrorMessage(error);
        this._error.set(errorMessage);
        this._isLoading.set(false);
        this._hasError.set(true);

        this.notificationService.error('Ошибка удаления аватара', errorMessage);
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
        this.notificationService.success('Успех', 'Пароль успешно изменен');
      }),
      catchError(error => {
        console.error('Ошибка смены пароля:', error);
        const errorMessage = this.getErrorMessage(error);
        this._error.set(errorMessage);
        this._isLoading.set(false);
        this._hasError.set(true);

        this.notificationService.error('Ошибка смены пароля', errorMessage);
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
