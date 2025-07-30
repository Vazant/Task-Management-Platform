import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User } from '@models';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div
      class="avatar"
      [class.avatar--small]="size === 'small'"
      [class.avatar--medium]="size === 'medium'"
      [class.avatar--large]="size === 'large'"
      [style.background-color]="backgroundColor"
      [style.color]="textColor">

      <img
        *ngIf="avatarUrl && !isDefaultAvatar"
        [src]="avatarUrl"
        [alt]="altText"
        (error)="onImageError()"
        class="avatar__image">

      <div
        *ngIf="!avatarUrl || isDefaultAvatar"
        class="avatar__initials">
        {{ initials }}
      </div>
    </div>
  `,
  styles: [`
    .avatar {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      font-weight: 600;
      overflow: hidden;
      background: linear-gradient(135deg, #3B82F6, #8B5CF6);
      color: white;
    }

    .avatar--small {
      width: 32px;
      height: 32px;
      font-size: 12px;
    }

    .avatar--medium {
      width: 48px;
      height: 48px;
      font-size: 16px;
    }

    .avatar--large {
      width: 64px;
      height: 64px;
      font-size: 20px;
    }

    .avatar__image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .avatar__initials {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
      text-transform: uppercase;
    }
  `]
})
export class AvatarComponent implements OnInit {
  @Input() user?: User;
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  @Input() avatarUrl?: string;
  @Input() username?: string;
  @Input() altText?: string;

  initials = '';
  backgroundColor = '';
  textColor = '#ffffff';
  isDefaultAvatar = false;

  ngOnInit() {
    this.initializeAvatar();
  }

  private initializeAvatar() {
    // Определяем URL аватара
    if (this.avatarUrl) {
      this.avatarUrl = this.avatarUrl;
    } else if (this.user?.avatar) {
      this.avatarUrl = this.user.avatar;
    }

    // Определяем имя пользователя
    const name = this.username || this.user?.username || '';

    // Генерируем инициалы
    this.initials = this.generateInitials(name);

    // Генерируем цвет фона на основе имени
    this.backgroundColor = this.generateBackgroundColor(name);

    // Проверяем, является ли это дефолтным аватаром
    this.isDefaultAvatar = this.avatarUrl?.includes('default-avatar') || false;
  }

  private generateInitials(name: string): string {
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

  private generateBackgroundColor(name: string): string {
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

  onImageError() {
    // Если изображение не загрузилось, показываем инициалы
    this.isDefaultAvatar = true;
  }
}
