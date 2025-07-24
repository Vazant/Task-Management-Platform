import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificationService, Notification } from '../../../core/services/notification.service';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-notification-toast',
  template: `
    <div class="notification-container">
      <div
        *ngFor="let notification of notifications$ | async"
        class="notification-toast"
        [class]="'notification-' + notification.type"
        [@slideInOut]
      >
        <div class="notification-header">
          <span class="notification-title">{{ notification.title }}</span>
          <button
            class="notification-close"
            (click)="removeNotification(notification.id)"
            aria-label="Закрыть уведомление"
          >
            ×
          </button>
        </div>
        <div class="notification-message">{{ notification.message }}</div>
      </div>
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 1000;
      max-width: 400px;
    }

    .notification-toast {
      background: white;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      margin-bottom: 10px;
      padding: 16px;
      border-left: 4px solid;
      animation: slideIn 0.3s ease-out;
    }

    .notification-success {
      border-left-color: #4caf50;
    }

    .notification-error {
      border-left-color: #f44336;
    }

    .notification-warning {
      border-left-color: #ff9800;
    }

    .notification-info {
      border-left-color: #2196f3;
    }

    .notification-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
    }

    .notification-title {
      font-weight: 600;
      font-size: 14px;
      color: #333;
    }

    .notification-close {
      background: none;
      border: none;
      font-size: 18px;
      cursor: pointer;
      color: #666;
      padding: 0;
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      transition: background-color 0.2s;
    }

    .notification-close:hover {
      background-color: #f0f0f0;
      color: #333;
    }

    .notification-message {
      font-size: 13px;
      color: #666;
      line-height: 1.4;
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
  animations: [
    // Здесь можно добавить анимации если нужно
  ],
  standalone: false
})
export class NotificationToastComponent implements OnInit, OnDestroy {
  notifications$: Observable<Notification[]>;
  private destroy$ = new Subject<void>();

  constructor(private notificationService: NotificationService) {
    this.notifications$ = this.notificationService.notifications$;
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  removeNotification(id: string): void {
    this.notificationService.removeNotification(id);
  }
} 