import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { LucideAngularModule } from 'lucide-angular';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-notification-bell',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatBadgeModule,
    MatButtonModule,
    MatMenuModule,
    MatTooltipModule,
    LucideAngularModule
  ],
  template: `
    <button
      mat-icon-button
      [matMenuTriggerFor]="notificationMenu"
      [matTooltip]="'Notifications'"
      class="notification-bell"
      (click)="$event.stopPropagation()">
      <mat-icon>notifications</mat-icon>
      <span 
        *ngIf="(unreadCount$ | async) as unreadCount"
        class="notification-badge"
        [style.display]="unreadCount > 0 ? 'flex' : 'none'">
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </button>

    <mat-menu #notificationMenu="matMenu" class="notification-menu">
      <div class="notification-header">
        <h3>Notifications</h3>
        <button
          mat-button
          color="primary"
          (click)="markAllAsRead()"
          [disabled]="(unreadCount$ | async) === 0">
          Mark all as read
        </button>
      </div>

      <div class="notification-list">
        <div
          *ngFor="let notification of notifications$ | async; trackBy: trackByNotification"
          class="notification-item"
          [class.unread]="!notification.read"
          (click)="markAsRead(notification.id)">
          <div class="notification-icon">
            <mat-icon [class]="'notification-type-' + notification.type">
              {{ getNotificationIcon(notification.type) }}
            </mat-icon>
          </div>
          <div class="notification-content">
            <div class="notification-title">{{ notification.title }}</div>
            <div class="notification-message">{{ notification.message }}</div>
            <div class="notification-time">
              {{ notification.createdAt | date:'short' }}
            </div>
          </div>
          <button
            mat-icon-button
            (click)="deleteNotification(notification.id, $event)"
            class="notification-delete">
            <mat-icon>close</mat-icon>
          </button>
        </div>

        <div
          *ngIf="(notifications$ | async)?.length === 0"
          class="notification-empty">
          <mat-icon>notifications_off</mat-icon>
          <p>No notifications</p>
        </div>
      </div>

      <div class="notification-footer">
        <button
          mat-button
          color="primary"
          (click)="viewAllNotifications()">
          View all notifications
        </button>
      </div>
    </mat-menu>
  `,
  styles: [`
    .notification-bell {
      position: relative;
    }

    .notification-badge {
      position: absolute;
      top: -8px;
      right: -8px;
      background-color: #f44336;
      color: white;
      border-radius: 50%;
      min-width: 18px;
      height: 18px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 11px;
      font-weight: 500;
      padding: 0 4px;
    }

    .notification-menu {
      min-width: 350px;
      max-width: 400px;
    }

    .notification-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid #e0e0e0;
    }

    .notification-header h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
    }

    .notification-list {
      max-height: 400px;
      overflow-y: auto;
    }

    .notification-item {
      display: flex;
      align-items: flex-start;
      padding: 12px 16px;
      border-bottom: 1px solid #f5f5f5;
      cursor: pointer;
      transition: background-color 0.2s;
    }

    .notification-item:hover {
      background-color: #f5f5f5;
    }

    .notification-item.unread {
      background-color: #f0f8ff;
    }

    .notification-item.unread:hover {
      background-color: #e6f3ff;
    }

    .notification-icon {
      margin-right: 12px;
      margin-top: 2px;
    }

    .notification-type-info {
      color: #2196f3;
    }

    .notification-type-success {
      color: #4caf50;
    }

    .notification-type-warning {
      color: #ff9800;
    }

    .notification-type-error {
      color: #f44336;
    }

    .notification-content {
      flex: 1;
      min-width: 0;
    }

    .notification-title {
      font-weight: 500;
      font-size: 14px;
      margin-bottom: 4px;
      color: #333;
    }

    .notification-message {
      font-size: 13px;
      color: #666;
      margin-bottom: 4px;
      line-height: 1.4;
    }

    .notification-time {
      font-size: 11px;
      color: #999;
    }

    .notification-delete {
      opacity: 0;
      transition: opacity 0.2s;
    }

    .notification-item:hover .notification-delete {
      opacity: 1;
    }

    .notification-empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 32px 16px;
      color: #999;
    }

    .notification-empty mat-icon {
      font-size: 48px;
      width: 48px;
      height: 48px;
      margin-bottom: 8px;
    }

    .notification-empty p {
      margin: 0;
      font-size: 14px;
    }

    .notification-footer {
      padding: 12px 16px;
      border-top: 1px solid #e0e0e0;
      text-align: center;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NotificationBellComponent {
  private readonly notificationService = inject(NotificationService);

  notifications$ = this.notificationService.notifications;
  unreadCount$ = this.notificationService.unreadCount;

  trackByNotification(index: number, notification: any): string {
    return notification.id;
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'info': return 'info';
      case 'success': return 'check_circle';
      case 'warning': return 'warning';
      case 'error': return 'error';
      default: return 'notifications';
    }
  }

  markAsRead(notificationId: string): void {
    this.notificationService.markAsRead(notificationId);
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead();
  }

  deleteNotification(notificationId: string, event: Event): void {
    event.stopPropagation();
    this.notificationService.deleteNotification(notificationId);
  }

  viewAllNotifications(): void {
    // Navigate to notifications page
  }
}

