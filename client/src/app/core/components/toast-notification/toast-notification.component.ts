import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Notification, NotificationType } from '../../models/notification.model';

@Component({
  selector: 'app-toast-notification',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  template: `
    <div
      class="toast-notification"
      [class]="'toast-' + notification.type"
      [@slideInOut]>
      <div class="toast-icon">
        <mat-icon>{{ getNotificationIcon(notification.type) }}</mat-icon>
      </div>
      <div class="toast-content">
        <div class="toast-title">{{ notification.title }}</div>
        <div class="toast-message">{{ notification.message }}</div>
      </div>
      <div class="toast-actions">
        <button
          *ngIf="notification.actionText"
          mat-button
          color="primary"
          (click)="handleAction()">
          {{ notification.actionText }}
        </button>
        <button
          mat-icon-button
          (click)="dismiss()"
          class="toast-close">
          <mat-icon>close</mat-icon>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .toast-notification {
      display: flex;
      align-items: flex-start;
      padding: 16px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      margin-bottom: 8px;
      min-width: 300px;
      max-width: 400px;
      animation: slideIn 0.3s ease-out;
    }

    .toast-info {
      background-color: #e3f2fd;
      border-left: 4px solid #2196f3;
    }

    .toast-success {
      background-color: #e8f5e8;
      border-left: 4px solid #4caf50;
    }

    .toast-warning {
      background-color: #fff3e0;
      border-left: 4px solid #ff9800;
    }

    .toast-error {
      background-color: #ffebee;
      border-left: 4px solid #f44336;
    }

    .toast-icon {
      margin-right: 12px;
      margin-top: 2px;
    }

    .toast-info .toast-icon mat-icon {
      color: #2196f3;
    }

    .toast-success .toast-icon mat-icon {
      color: #4caf50;
    }

    .toast-warning .toast-icon mat-icon {
      color: #ff9800;
    }

    .toast-error .toast-icon mat-icon {
      color: #f44336;
    }

    .toast-content {
      flex: 1;
      min-width: 0;
    }

    .toast-title {
      font-weight: 500;
      font-size: 14px;
      margin-bottom: 4px;
      color: #333;
    }

    .toast-message {
      font-size: 13px;
      color: #666;
      line-height: 1.4;
    }

    .toast-actions {
      display: flex;
      align-items: center;
      margin-left: 12px;
    }

    .toast-close {
      opacity: 0.7;
      transition: opacity 0.2s;
    }

    .toast-close:hover {
      opacity: 1;
    }

    @keyframes slideIn {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    @keyframes slideOut {
      from {
        transform: translateX(0);
        opacity: 1;
      }
      to {
        transform: translateX(100%);
        opacity: 0;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ToastNotificationComponent implements OnDestroy {
  @Input() notification!: Notification;
  @Input() onDismiss?: () => void;
  @Input() onAction?: () => void;

  private readonly autoDismissTimeout?: number;

  constructor() {
    // Auto-dismiss after 5 seconds for non-error notifications
    if (this.notification.type !== 'error') {
      this.autoDismissTimeout = window.setTimeout(() => {
        this.dismiss();
      }, 5000);
    }
  }

  ngOnDestroy(): void {
    if (this.autoDismissTimeout) {
      clearTimeout(this.autoDismissTimeout);
    }
  }

  getNotificationIcon(type: NotificationType): string {
    switch (type) {
      case 'info': return 'info';
      case 'success': return 'check_circle';
      case 'warning': return 'warning';
      case 'error': return 'error';
      default: return 'notifications';
    }
  }

  dismiss(): void {
    if (this.onDismiss) {
      this.onDismiss();
    }
  }

  handleAction(): void {
    if (this.onAction) {
      this.onAction();
    }
  }
}

