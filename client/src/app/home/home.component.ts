import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../core/services/auth.service';
import { User } from '../core/models/user.model';
import { LayoutComponent } from '../shared/components/layout';
import { LucideAngularModule, ChartColumn, CheckSquare, Clock, TrendingUp, Menu } from 'lucide-angular';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, CommonModule, LayoutComponent, LucideAngularModule],
  template: `
    <app-layout [showHeader]="true" [showFooter]="true">
      <div class="home-container">

      <!-- Hero Section -->
      <section class="hero">
        <div class="hero-content">
          <div class="hero-text">
            <h1 class="hero-title">Управляйте проектами эффективно</h1>
            <p class="hero-subtitle">
              Мощная платформа для командной работы, управления задачами и отслеживания прогресса
            </p>
            <div class="hero-buttons">
              <ng-container *ngIf="!isAuthenticated; else dashboardButton">
                <a routerLink="/auth/register" class="btn btn-primary">Начать бесплатно</a>
                <a routerLink="/auth/login" class="btn btn-outline">Войти в систему</a>
              </ng-container>
              <ng-template #dashboardButton>
                <a routerLink="/dashboard" class="btn btn-primary">Перейти к дашборду</a>
                <button (click)="logout()" class="btn btn-outline">Выйти</button>
              </ng-template>
            </div>
          </div>
          <div class="hero-image">
            <div class="mockup">
              <div class="mockup-header">
                <div class="mockup-dot red"></div>
                <div class="mockup-dot yellow"></div>
                <div class="mockup-dot green"></div>
              </div>
              <div class="mockup-content">
                <div class="mockup-card">
                  <div class="mockup-card-header">
                    <div class="mockup-avatar"></div>
                    <div class="mockup-text">
                      <div class="mockup-line short"></div>
                      <div class="mockup-line long"></div>
                    </div>
                  </div>
                </div>
                <div class="mockup-card">
                  <div class="mockup-card-header">
                    <div class="mockup-avatar"></div>
                    <div class="mockup-text">
                      <div class="mockup-line short"></div>
                      <div class="mockup-line long"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="features">
        <div class="container">
          <div class="section-header">
            <h2>Все что нужно для управления проектами</h2>
            <p>Полный набор инструментов для эффективной работы команды</p>
          </div>

          <div class="features-grid">
            <div class="feature-card">
              <div class="feature-icon">
                <i-lucide [img]="ChartColumn" [size]="32"></i-lucide>
              </div>
              <h3>Управление проектами</h3>
              <p>Создавайте проекты, добавляйте участников и отслеживайте прогресс в реальном времени</p>
            </div>

            <div class="feature-card">
              <div class="feature-icon">
                <i-lucide [img]="CheckSquare" [size]="32"></i-lucide>
              </div>
              <h3>Kanban доска</h3>
              <p>Визуальное управление задачами с drag-and-drop функционалом</p>
            </div>

            <div class="feature-card">
              <div class="feature-icon">
                <i-lucide [img]="Clock" [size]="32"></i-lucide>
              </div>
              <h3>Отслеживание времени</h3>
              <p>Встроенный таймер и детальная аналитика времени работы</p>
            </div>

            <div class="feature-card">
              <div class="feature-icon">
                <i-lucide [img]="TrendingUp" [size]="32"></i-lucide>
              </div>
              <h3>Аналитика и отчеты</h3>
              <p>Подробная статистика производительности и автоматические отчеты</p>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="cta">
        <div class="container">
          <div class="cta-content">
            <h2>Готовы начать?</h2>
            <p>Присоединяйтесь к тысячам команд, которые уже используют TaskBoard Pro</p>
            <ng-container *ngIf="!isAuthenticated; else dashboardCta">
              <a routerLink="/auth/register" class="btn btn-primary btn-large">Создать аккаунт</a>
            </ng-container>
            <ng-template #dashboardCta>
              <a routerLink="/dashboard" class="btn btn-primary btn-large">Перейти к дашборду</a>
            </ng-template>
          </div>
        </div>
      </section>

      </div>
    </app-layout>
  `,
  styles: [`
    .home-container {
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    }

    /* Hero Section */
    .hero {
      padding: 8rem 2rem 4rem;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }

    .hero-content {
      max-width: 1200px;
      margin: 0 auto;
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 4rem;
      align-items: center;
    }

    .hero-title {
      font-size: 3.5rem;
      font-weight: 700;
      margin-bottom: 1.5rem;
      line-height: 1.2;
    }

    .hero-subtitle {
      font-size: 1.25rem;
      margin-bottom: 2rem;
      opacity: 0.9;
      line-height: 1.6;
    }

    .hero-buttons {
      display: flex;
      gap: 1rem;
      flex-wrap: wrap;
    }

    .btn {
      padding: 1rem 2rem;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 600;
      transition: all 0.3s ease;
      display: inline-block;
      border: none;
      cursor: pointer;
    }

    .btn-primary {
      background: #2ecc71;
      color: white;
    }

    .btn-primary:hover {
      background: #27ae60;
      transform: translateY(-2px);
    }

    .btn-outline {
      background: transparent;
      color: white;
      border: 2px solid white;
    }

    .btn-outline:hover {
      background: white;
      color: #667eea;
    }

    .btn-large {
      padding: 1.25rem 2.5rem;
      font-size: 1.1rem;
    }

    /* Mockup */
    .hero-image {
      display: flex;
      justify-content: center;
    }

    .mockup {
      background: white;
      border-radius: 12px;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      width: 100%;
      max-width: 400px;
    }

    .mockup-header {
      background: #f8f9fa;
      padding: 1rem;
      display: flex;
      gap: 0.5rem;
    }

    .mockup-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
    }

    .mockup-dot.red { background: #ff5f56; }
    .mockup-dot.yellow { background: #ffbd2e; }
    .mockup-dot.green { background: #27ca3f; }

    .mockup-content {
      padding: 1.5rem;
    }

    .mockup-card {
      background: #f8f9fa;
      border-radius: 8px;
      padding: 1rem;
      margin-bottom: 1rem;
    }

    .mockup-card-header {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }

    .mockup-avatar {
      width: 32px;
      height: 32px;
      background: #3498db;
      border-radius: 50%;
    }

    .mockup-text {
      flex: 1;
    }

    .mockup-line {
      background: #dee2e6;
      border-radius: 2px;
      margin-bottom: 0.25rem;
    }

    .mockup-line.short {
      height: 8px;
      width: 60%;
    }

    .mockup-line.long {
      height: 8px;
      width: 100%;
    }

    /* Features Section */
    .features {
      padding: 5rem 2rem;
      background: white;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
    }

    .section-header {
      text-align: center;
      margin-bottom: 4rem;
    }

    .section-header h2 {
      font-size: 2.5rem;
      color: #2c3e50;
      margin-bottom: 1rem;
    }

    .section-header p {
      font-size: 1.1rem;
      color: #6c757d;
    }

    .features-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 2rem;
    }

    .feature-card {
      padding: 2rem;
      border-radius: 12px;
      background: #f8f9fa;
      text-align: center;
      transition: transform 0.3s ease, box-shadow 0.3s ease;
    }

    .feature-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    }

    .feature-icon {
      width: 64px;
      height: 64px;
      background: #3498db;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 1.5rem;
      color: white;
      position: relative;
    }

    .feature-icon i-lucide {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      color: white;
    }

    .feature-card h3 {
      font-size: 1.5rem;
      color: #2c3e50;
      margin-bottom: 1rem;
    }

    .feature-card p {
      color: #6c757d;
      line-height: 1.6;
    }

    /* CTA Section */
    .cta {
      padding: 5rem 2rem;
      background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
      color: white;
      text-align: center;
    }

    .cta-content h2 {
      font-size: 2.5rem;
      margin-bottom: 1rem;
    }

    .cta-content p {
      font-size: 1.1rem;
      margin-bottom: 2rem;
      opacity: 0.9;
    }



    /* Responsive */
    @media (max-width: 768px) {
      .hero-content {
        grid-template-columns: 1fr;
        text-align: center;
      }

      .hero-title {
        font-size: 2.5rem;
      }

      .hero-buttons {
        justify-content: center;
      }

      .features-grid {
        grid-template-columns: 1fr;
      }

      .footer-content {
        grid-template-columns: 1fr;
        text-align: center;
      }

      .footer-links {
        grid-template-columns: 1fr;
      }

      .nav {
        gap: 1rem;
      }

      .nav-desktop {
        display: none;
      }

      .mobile-menu-toggle {
        display: block;
      }

      .mobile-menu {
        display: block;
      }
    }

    @media (max-width: 480px) {
      .header-content {
        padding: 0.75rem;
      }

      .logo h1 {
        font-size: 1.25rem;
      }

      .hero-title {
        font-size: 2rem;
      }
    }
  `]
})
export class HomeComponent implements OnInit {
  private readonly authService = inject(AuthService);

  isAuthenticated = false;
  currentUser: User | null = null;

  // Lucide icons
  readonly ChartColumn = ChartColumn;
  readonly CheckSquare = CheckSquare;
  readonly Clock = Clock;
  readonly TrendingUp = TrendingUp;
  readonly Menu = Menu;



  ngOnInit(): void {
    // Подписываемся на изменения состояния аутентификации
    this.authService.isAuthenticated$.subscribe(isAuth => {
      this.isAuthenticated = isAuth;
    });

    // Подписываемся на изменения пользователя
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  logout(): void {
    this.authService.logout();
  }

  changePassword(): void {
    // Change password functionality will be implemented
  }
}
