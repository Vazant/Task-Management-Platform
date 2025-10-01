import { Component, OnInit, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';
import { UserMenuComponent } from '../user-menu/user-menu.component';
import { LucideAngularModule, Menu } from 'lucide-angular';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, CommonModule, UserMenuComponent, LucideAngularModule],
  template: `
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <h1>TaskBoard Pro</h1>
        </div>
        <nav class="nav">
          <div class="nav-desktop">
            <a routerLink="/" class="nav-link" [class.active]="isActiveRoute('/')">Главная</a>
            <ng-container *ngIf="isAuthenticated">
              <a routerLink="/dashboard" class="nav-link" [class.active]="isActiveRoute('/dashboard')">Дашборд</a>
              <a routerLink="/projects" class="nav-link" [class.active]="isActiveRoute('/projects')">Проекты</a>
              <a routerLink="/tasks" class="nav-link" [class.active]="isActiveRoute('/tasks')">Задачи</a>
              <a routerLink="/time-tracking" class="nav-link" [class.active]="isActiveRoute('/time-tracking')">Время</a>
              <a routerLink="/analytics" class="nav-link" [class.active]="isActiveRoute('/analytics')">Аналитика</a>
            </ng-container>
          </div>

          <!-- Мобильное меню -->
          <button class="mobile-menu-toggle" (click)="toggleMobileMenu()" type="button" *ngIf="isAuthenticated">
            <i-lucide [img]="Menu" [size]="24"></i-lucide>
          </button>

          <div class="mobile-menu" [class.open]="isMobileMenuOpen" *ngIf="isAuthenticated">
            <a routerLink="/" class="mobile-nav-link" [class.active]="isActiveRoute('/')" (click)="closeMobileMenu()">Главная</a>
            <a routerLink="/dashboard" class="mobile-nav-link" [class.active]="isActiveRoute('/dashboard')" (click)="closeMobileMenu()">Дашборд</a>
            <a routerLink="/projects" class="mobile-nav-link" [class.active]="isActiveRoute('/projects')" (click)="closeMobileMenu()">Проекты</a>
            <a routerLink="/tasks" class="mobile-nav-link" [class.active]="isActiveRoute('/tasks')" (click)="closeMobileMenu()">Задачи</a>
            <a routerLink="/time-tracking" class="mobile-nav-link" [class.active]="isActiveRoute('/time-tracking')" (click)="closeMobileMenu()">Время</a>
            <a routerLink="/analytics" class="mobile-nav-link" [class.active]="isActiveRoute('/analytics')" (click)="closeMobileMenu()">Аналитика</a>
          </div>

          <ng-container *ngIf="!isAuthenticated; else authenticatedNav">
            <a routerLink="/auth/login" class="nav-link">Войти</a>
            <a routerLink="/auth/register" class="nav-btn">Регистрация</a>
          </ng-container>
          <ng-template #authenticatedNav>
            <app-user-menu
              [user]="currentUser"
              (logoutClick)="logout()"
              (changePasswordClick)="changePassword()">
            </app-user-menu>
          </ng-template>
        </nav>
      </div>
    </header>
  `,
  styles: [`
    /* Header */
    .header {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-bottom: 1px solid rgba(0, 0, 0, 0.1);
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 1000;
    }

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 1rem 2rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .logo h1 {
      font-size: 1.5rem;
      font-weight: 700;
      color: #2c3e50;
      margin: 0;
    }

    .nav {
      display: flex;
      align-items: center;
      gap: 2rem;
      position: relative;
    }

    .nav-desktop {
      display: flex;
      align-items: center;
      gap: 2rem;
    }

    .nav-link {
      text-decoration: none;
      color: #6c757d;
      font-weight: 500;
      transition: color 0.3s ease;
      padding: 0.5rem 0;
      position: relative;
    }

    .nav-link:hover,
    .nav-link.active {
      color: #3498db;
    }

    .nav-link.active::after {
      content: '';
      position: absolute;
      bottom: -1rem;
      left: 0;
      right: 0;
      height: 2px;
      background: #3498db;
    }

    .nav-btn {
      background: #3498db;
      color: white;
      padding: 0.5rem 1.5rem;
      border-radius: 6px;
      text-decoration: none;
      font-weight: 500;
      transition: background 0.3s ease;
      border: none;
      cursor: pointer;
    }

    .nav-btn:hover {
      background: #2980b9;
    }

    // Mobile menu toggle
    .mobile-menu-toggle {
      display: none;
      background: none;
      border: none;
      color: #6c757d;
      cursor: pointer;
      padding: 0.5rem;
      border-radius: 6px;
      transition: background 0.3s ease;
    }

    .mobile-menu-toggle:hover {
      background: rgba(0, 0, 0, 0.05);
    }

    // Mobile menu
    .mobile-menu {
      display: none;
      position: absolute;
      top: 100%;
      right: 0;
      background: white;
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      min-width: 200px;
      z-index: 1000;
      margin-top: 8px;
      border: 1px solid rgba(0, 0, 0, 0.08);
      overflow: hidden;
      opacity: 0;
      transform: translateY(-10px);
      transition: all 0.3s ease;
    }

    .mobile-menu.open {
      opacity: 1;
      transform: translateY(0);
    }

    .mobile-nav-link {
      display: block;
      padding: 1rem 1.5rem;
      color: #6c757d;
      text-decoration: none;
      font-weight: 500;
      transition: background 0.3s ease;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    }

    .mobile-nav-link:hover,
    .mobile-nav-link.active {
      background: #f8f9fa;
      color: #3498db;
    }

    .mobile-nav-link:last-child {
      border-bottom: none;
    }

    /* User menu styles */
    app-user-menu {
      display: inline-block;
    }

    app-user-menu .user-button {
      background: #3498db;
      color: white;
      padding: 0.5rem 1rem;
      border-radius: 6px;
      transition: background 0.3s ease;
    }

    app-user-menu .user-button:hover {
      background: #2980b9;
    }

    app-user-menu .user-name {
      color: white;
    }

    app-user-menu .menu-arrow {
      color: rgba(255, 255, 255, 0.8);
    }

    app-user-menu .dropdown-menu {
      margin-top: 8px;
    }

    /* Responsive */
    @media (max-width: 768px) {
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
    }
  `]
})
export class HeaderComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  isAuthenticated = false;
  currentUser: User | null = null;
  isMobileMenuOpen = false;

  // Lucide icons
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

  isActiveRoute(route: string): boolean {
    return this.router.url === route;
  }

  logout(): void {
    this.authService.logout();
  }

  changePassword(): void {
    // Change password functionality will be implemented
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }
}
