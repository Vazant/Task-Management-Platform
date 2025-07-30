import { Component, Input, Output, EventEmitter, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { User } from '@models';
import { AvatarComponent } from '../avatar/avatar.component';
import {
  LucideAngularModule,
  User as UserIcon,
  Settings,
  KeyRound,
  LogOut,
  ChevronDown
} from 'lucide-angular';

@Component({
  selector: 'app-user-menu',
  standalone: true,
  imports: [CommonModule, RouterLink, LucideAngularModule, AvatarComponent],
  template: `
    <div class="user-menu" [class.open]="isOpen">
      <button class="user-button" (click)="toggleMenu()" type="button">
        <app-avatar [user]="user || undefined" size="small" class="user-avatar"></app-avatar>
        <span class="user-name">{{ user?.username || 'Пользователь' }}</span>
        <i-lucide [img]="ChevronDown" class="menu-arrow" [class.rotated]="isOpen" [size]="16"></i-lucide>
      </button>

      <div class="dropdown-menu" *ngIf="isOpen">
        <a class="menu-item" routerLink="/profile" (click)="closeMenu()" tabindex="0" (keydown.enter)="closeMenu()">
          <i-lucide [img]="UserIcon" class="menu-icon" [size]="20"></i-lucide>
          <span class="menu-text">Мой профиль</span>
        </a>
        <a class="menu-item" routerLink="/settings" (click)="closeMenu()" tabindex="0" (keydown.enter)="closeMenu()">
          <i-lucide [img]="Settings" class="menu-icon" [size]="20"></i-lucide>
          <span class="menu-text">Настройки</span>
        </a>
        <button class="menu-item" (click)="changePassword()" (keydown.enter)="changePassword()" tabindex="0" type="button">
          <i-lucide [img]="KeyRound" class="menu-icon" [size]="20"></i-lucide>
          <span class="menu-text">Сменить пароль</span>
        </button>
        <div class="menu-divider"></div>
        <button class="menu-item logout" (click)="logout()" (keydown.enter)="logout()" tabindex="0" type="button">
          <i-lucide [img]="LogOut" class="menu-icon" [size]="20"></i-lucide>
          <span class="menu-text">Выйти</span>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .user-menu {
      position: relative;
      display: inline-block;
    }

    .user-button {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      background: none;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s ease;
      font-size: 14px;
      color: #374151;
    }

    .user-button:hover {
      background-color: #f9fafb;
      border-color: #d1d5db;
    }

    .user-avatar {
      flex-shrink: 0;
    }

    .user-name {
      font-weight: 500;
      max-width: 120px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .menu-arrow {
      transition: transform 0.2s ease;
      color: #6b7280;
    }

    .menu-arrow.rotated {
      transform: rotate(180deg);
    }

    .dropdown-menu {
      position: absolute;
      top: 100%;
      right: 0;
      margin-top: 4px;
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
      min-width: 200px;
      z-index: 1000;
      overflow: hidden;
    }

    .menu-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 16px;
      background: none;
      border: none;
      width: 100%;
      text-align: left;
      cursor: pointer;
      transition: background-color 0.2s ease;
      color: #374151;
      text-decoration: none;
      font-size: 14px;
    }

    .menu-item:hover {
      background-color: #f9fafb;
    }

    .menu-item:focus {
      outline: none;
      background-color: #f3f4f6;
    }

    .menu-item.logout {
      color: #dc2626;
    }

    .menu-item.logout:hover {
      background-color: #fef2f2;
    }

    .menu-icon {
      color: #6b7280;
      flex-shrink: 0;
    }

    .menu-text {
      font-weight: 500;
    }

    .menu-divider {
      height: 1px;
      background-color: #e5e7eb;
      margin: 4px 0;
    }

    /* Анимация появления */
    .dropdown-menu {
      animation: slideDown 0.2s ease-out;
    }

    @keyframes slideDown {
      from {
        opacity: 0;
        transform: translateY(-8px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    /* Адаптивность */
    @media (max-width: 768px) {
      .user-name {
        display: none;
      }

      .dropdown-menu {
        right: -8px;
        min-width: 180px;
      }
    }
  `]
})
export class UserMenuComponent {
  @Input() user: User | null = null;
  @Output() logoutClick = new EventEmitter<void>();
  @Output() changePasswordClick = new EventEmitter<void>();

  // Lucide icons для standalone компонентов
  readonly UserIcon = UserIcon;
  readonly Settings = Settings;
  readonly KeyRound = KeyRound;
  readonly LogOut = LogOut;
  readonly ChevronDown = ChevronDown;

  isOpen = false;

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    if (!this.isOpen) return;

    const menuElement = target.closest('.user-menu');
    if (!menuElement) {
      this.closeMenu();
    }
  }

  @HostListener('document:keydown.escape')
  onEscapeKey() {
    if (this.isOpen) {
      this.closeMenu();
    }
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  closeMenu() {
    this.isOpen = false;
  }

  logout() {
    this.logoutClick.emit();
    this.closeMenu();
  }

  changePassword() {
    this.changePasswordClick.emit();
    this.closeMenu();
  }
}
