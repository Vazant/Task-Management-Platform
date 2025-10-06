import { Injectable, signal } from '@angular/core';
import { Observable, of, delay } from 'rxjs';
import { UserProfile, UpdateProfileRequest, UpdateAvatarRequest } from '../models/user-profile.model';



@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  // Signal-based state для профиля
  private readonly _profile = signal<UserProfile | null>(null);
  private readonly _isLoading = signal(false);
  private readonly _error = signal<string | null>(null);

  // Public signals
  readonly profile = this._profile.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly error = this._error.asReadonly();

  constructor() {
    // Инициализация с моковыми данными
    this.loadProfile();
  }

  // Получить профиль пользователя
  getProfile(): Observable<UserProfile> {
    this._isLoading.set(true);
    this._error.set(null);

    // Моковые данные для демонстрации
    const mockProfile: UserProfile = {
      id: '1',
      username: 'john_doe',
      email: 'john.doe@example.com',
      firstName: 'John',
      lastName: 'Doe',
      avatar: 'https://via.placeholder.com/150/3498db/ffffff?text=JD',
      role: 'user',
      lastLogin: new Date('2024-01-15T10:30:00Z'),
      createdAt: new Date('2023-01-01T00:00:00Z'),
      updatedAt: new Date('2024-01-10T15:45:00Z')
    };

    return of(mockProfile).pipe(
      delay(800) // Имитация задержки сети
    );
  }

  // Обновить профиль
  updateProfile(request: UpdateProfileRequest): Observable<UserProfile | null> {
    this._isLoading.set(true);
    this._error.set(null);

    const currentProfile = this._profile();
    if (!currentProfile) {
      this._error.set('Профиль не найден');
      this._isLoading.set(false);
      return of(null);
    }

    const updatedProfile: UserProfile = {
      ...currentProfile,
      ...request,
      updatedAt: new Date()
    };

    return of(updatedProfile).pipe(
      delay(1000) // Имитация задержки сети
    );
  }

  // Обновить аватар
  updateAvatar(_request: UpdateAvatarRequest): Observable<{ avatarUrl: string }> {
    this._isLoading.set(true);
    this._error.set(null);

    // Имитация загрузки файла
    return of({ avatarUrl: 'https://via.placeholder.com/150/27ae60/ffffff?text=NEW' }).pipe(
      delay(1500)
    );
  }

  // Загрузить профиль в state
  private loadProfile(): void {
    this.getProfile().subscribe({
      next: (profile) => {
        this._profile.set(profile);
        this._isLoading.set(false);
      },
      error: (error) => {
        this._error.set(error.message ?? 'Ошибка загрузки профиля');
        this._isLoading.set(false);
      }
    });
  }

  // Обновить профиль в state
  updateProfileInState(request: UpdateProfileRequest): void {
    this.updateProfile(request).subscribe({
      next: (profile) => {
        this._profile.set(profile);
        this._isLoading.set(false);
      },
      error: (error) => {
        this._error.set(error.message ?? 'Ошибка обновления профиля');
        this._isLoading.set(false);
      }
    });
  }

  // Обновить аватар в state
  updateAvatarInState(request: UpdateAvatarRequest): void {
    this.updateAvatar(request).subscribe({
      next: (result) => {
        const currentProfile = this._profile();
        if (currentProfile) {
          this._profile.set({
            ...currentProfile,
            avatar: result.avatarUrl,
            updatedAt: new Date()
          });
        }
        this._isLoading.set(false);
      },
      error: (error) => {
        this._error.set(error.message ?? 'Ошибка обновления аватара');
        this._isLoading.set(false);
      }
    });
  }

  // Очистить ошибки
  clearError(): void {
    this._error.set(null);
  }
}
