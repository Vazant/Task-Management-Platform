import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '@services';
import { User } from '@models';
import { LayoutComponent } from '../../shared/components/layout';
import { LucideAngularModule, User as UserIcon, Mail, Shield, Calendar, Settings, Lock, Loader2 } from 'lucide-angular';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink, LayoutComponent, LucideAngularModule],
  template: `
    <app-layout [showHeader]="true" [showFooter]="true">
      <div class="profile-container">
        <div class="profile-header">
          <h1>Мой профиль</h1>
          <p>Управление личной информацией и настройками</p>
        </div>

        <!-- Состояние загрузки с анимированным спиннером -->
        <div class="profile-content" *ngIf="loading">
          <div class="loading-card">
            <div class="loading-spinner">
              <i-lucide [img]="Loader2" [size]="48" class="spinner-icon"></i-lucide>
            </div>
            <h3>Загрузка профиля...</h3>
            <p>Пожалуйста, подождите, мы загружаем ваши данные</p>
          </div>
        </div>

        <!-- Состояние ошибки -->
        <div class="profile-content" *ngIf="error">
          <div class="error-card">
            <h3>Ошибка загрузки</h3>
            <p>{{ error }}</p>
            <button class="btn btn-primary" (click)="retryLoad()">
              <i-lucide [img]="Loader2" [size]="20"></i-lucide>
              Попробовать снова
            </button>
          </div>
        </div>

        <!-- Основной контент профиля -->
        <div class="profile-content" *ngIf="currentUser && !loading">
          <div class="profile-card">
            <div class="profile-avatar">
              <i-lucide [img]="UserIcon" [size]="48" color="white"></i-lucide>
            </div>

            <div class="profile-info">
              <h2>{{ currentUser.username }}</h2>
              <div class="info-item">
                <i-lucide [img]="Mail" [size]="20" color="#3498db"></i-lucide>
                <span>{{ currentUser.email }}</span>
              </div>
              <div class="info-item">
                <i-lucide [img]="Shield" [size]="20" color="#27ae60"></i-lucide>
                <span>Роль: {{ currentUser.role === 'admin' ? 'Администратор' : 'Пользователь' }}</span>
              </div>
              <div class="info-item">
                <i-lucide [img]="Calendar" [size]="20" color="#6c757d"></i-lucide>
                <span>Участник с: {{ currentUser.createdAt | date:'dd.MM.yyyy' }}</span>
              </div>
            </div>
          </div>

          <div class="profile-actions">
            <a routerLink="/settings" class="btn btn-primary">
              <i-lucide [img]="Settings" [size]="20"></i-lucide>
              Настройки
            </a>
            <button class="btn btn-outline" (click)="changePassword()">
              <i-lucide [img]="Lock" [size]="20"></i-lucide>
              Сменить пароль
            </button>
          </div>
        </div>
      </div>
    </app-layout>
  `,
  styles: [`
    .profile-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      padding: 2rem;
    }

    .profile-header {
      max-width: 800px;
      margin: 0 auto 2rem;
      text-align: center;
    }

    .profile-header h1 {
      font-size: 2.5rem;
      color: #2c3e50;
      margin-bottom: 0.5rem;
    }

    .profile-header p {
      color: #6c757d;
      font-size: 1.1rem;
    }

    .profile-content {
      max-width: 800px;
      margin: 0 auto;
    }

    /* Стили для состояния загрузки */
    .loading-card {
      background: white;
      border-radius: 12px;
      padding: 3rem 2rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      text-align: center;
    }

    .loading-spinner {
      margin-bottom: 1.5rem;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .spinner-icon {
      animation: spin 1s linear infinite;
      color: #3498db;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    @keyframes spin {
      0% {
        transform: rotate(0deg);
      }
      100% {
        transform: rotate(360deg);
      }
    }

    .loading-card h3 {
      color: #2c3e50;
      margin-bottom: 0.5rem;
      font-size: 1.5rem;
    }

    .loading-card p {
      color: #6c757d;
      font-size: 1rem;
    }

    /* Стили для состояния ошибки */
    .error-card {
      background: white;
      border-radius: 12px;
      padding: 2rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      text-align: center;
      border-left: 4px solid #e74c3c;
    }

    .error-card h3 {
      color: #e74c3c;
      margin-bottom: 1rem;
    }

    .error-card p {
      color: #6c757d;
      margin-bottom: 1.5rem;
    }

    .profile-card {
      background: white;
      border-radius: 12px;
      padding: 2rem;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      display: flex;
      align-items: center;
      gap: 2rem;
      margin-bottom: 2rem;
    }

    .profile-avatar {
      width: 100px;
      height: 100px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      flex-shrink: 0;
    }

    .profile-avatar i-lucide {
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .profile-info h2 {
      font-size: 1.8rem;
      color: #2c3e50;
      margin-bottom: 1rem;
    }

    .info-item {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      margin-bottom: 0.75rem;
      color: #6c757d;
    }

    .info-item i-lucide {
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .info-item span {
      font-weight: 500;
      line-height: 1.4;
    }

    .profile-actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
    }

    .btn {
      padding: 0.75rem 1.5rem;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 500;
      transition: all 0.3s ease;
      border: none;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 1rem;
    }

    .btn i-lucide {
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .btn-primary {
      background: #3498db;
      color: white;
    }

    .btn-primary:hover {
      background: #2980b9;
      transform: translateY(-2px);
    }

    .btn-outline {
      background: transparent;
      color: #3498db;
      border: 2px solid #3498db;
    }

    .btn-outline:hover {
      background: #3498db;
      color: white;
      transform: translateY(-2px);
    }

    @media (max-width: 768px) {
      .profile-container {
        padding: 1rem;
      }

      .profile-card {
        flex-direction: column;
        text-align: center;
        gap: 1.5rem;
      }

      .profile-info {
        text-align: center;
      }

      .info-item {
        justify-content: center;
      }

      .profile-actions {
        flex-direction: column;
      }
    }
  `]
})
export class ProfileComponent implements OnInit {
  private readonly authService = inject(AuthService);

  // Lucide icons для standalone компонентов
  readonly UserIcon = UserIcon;
  readonly Mail = Mail;
  readonly Shield = Shield;
  readonly Calendar = Calendar;
  readonly Settings = Settings;
  readonly Lock = Lock;
  readonly Loader2 = Loader2;

  currentUser: User | null = null;
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.loadProfile();
  }

  private loadProfile(): void {
    this.loading = true;
    this.error = null;

    // Имитируем загрузку профиля с задержкой для лучшего UX
    setTimeout(() => {
      this.authService.currentUser$.subscribe({
        next: (user) => {
          this.currentUser = user;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Не удалось загрузить профиль. Попробуйте обновить страницу.';
          this.loading = false;
          console.error('Error loading profile:', err);
        }
      });
    }, 500); // Небольшая задержка для показа спиннера
  }

  retryLoad(): void {
    this.loadProfile();
  }

  changePassword(): void {
    // Change password functionality will be implemented
  }
}
