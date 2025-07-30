import { Component, OnDestroy, inject } from '@angular/core';
import { NotificationService, Notification } from '@services';
import { Observable, Subject } from 'rxjs';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { AsyncPipe, NgFor, NgClass } from '@angular/common';



@Component({
  selector: 'app-notification-toast',
  template: `
    <div class="notification-container">
      <div
        *ngFor="let notification of notifications$ | async; trackBy: trackByNotification"
        class="notification-toast"
        [ngClass]="'notification-' + notification.type"
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
  `],
  animations: [
    trigger('slideInOut', [
      state('void', style({
        transform: 'translateX(100%)',
        opacity: 0
      })),
      state('*', style({
        transform: 'translateX(0)',
        opacity: 1
      })),
      transition('void => *', [
        animate('300ms ease-out')
      ]),
      transition('* => void', [
        animate('300ms ease-in')
      ])
    ])
  ],
  imports: [AsyncPipe, NgFor, NgClass],
  standalone: true
})
export class NotificationToastComponent implements OnDestroy {
  private readonly notificationService = inject(NotificationService);
  notifications$: Observable<Notification[]> = this.notificationService.notifications$;
  private readonly destroy$ = new Subject<void>();

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  removeNotification(id: string): void {
    this.notificationService.removeNotification(id);
  }

  trackByNotification(index: number, notification: Notification): string {
    return notification.id;
  }
}
