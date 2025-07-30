import { Injectable, inject } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { BehaviorSubject } from 'rxjs';

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  duration?: number;
  timestamp: Date;
  action?: {
    label: string;
    callback: () => void;
  };
}

export interface ErrorNotification {
  title: string;
  message: string;
  retryAction?: () => void;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly snackBar = inject(MatSnackBar);
  private readonly notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  // Отслеживаем активные уведомления для предотвращения дублирования
  private readonly activeNotifications = new Set<string>();
  private readonly lastNotificationTime = new Map<string, number>();

  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }

  private generateNotificationKey(type: string, title: string, message: string): string {
    return `${type}:${title}:${message}`;
  }

  private addNotification(notification: Omit<Notification, 'id' | 'timestamp'>): void {
    const notificationKey = this.generateNotificationKey(notification.type, notification.title, notification.message);
    const now = Date.now();
    const lastTime = this.lastNotificationTime.get(notificationKey) ?? 0;

    // Проверяем, не показываем ли мы уже такое уведомление или не показывали его недавно (в течение 3 секунд)
    if (this.activeNotifications.has(notificationKey) || (now - lastTime) < 3000) {
      return; // Пропускаем дублирующееся уведомление
    }

    const newNotification: Notification = {
      ...notification,
      id: this.generateId(),
      timestamp: new Date()
    };

    // Добавляем ключ в активные уведомления и запоминаем время
    this.activeNotifications.add(notificationKey);
    this.lastNotificationTime.set(notificationKey, now);

    const currentNotifications = this.notificationsSubject.value;
    this.notificationsSubject.next([...currentNotifications, newNotification]);

    // Автоматическое удаление уведомления
    if (notification.duration !== 0) {
      setTimeout(() => {
        this.removeNotification(newNotification.id);
        this.activeNotifications.delete(notificationKey);
      }, notification.duration ?? 5000);
    }
  }

  success(title: string, message: string, duration?: number): void {
    this.addNotification({ type: 'success', title, message, duration });
  }

  error(title: string, message: string, duration?: number): void {
    this.addNotification({ type: 'error', title, message, duration });
  }

  warning(title: string, message: string, duration?: number): void {
    this.addNotification({ type: 'warning', title, message, duration });
  }

  info(title: string, message: string, duration?: number): void {
    this.addNotification({ type: 'info', title, message, duration });
  }

  /**
   * Показывает ошибку с кнопкой "Повторить"
   */
  showErrorWithRetry(error: ErrorNotification): void {
    const config: MatSnackBarConfig = {
      duration: 0, // Не закрывается автоматически
      panelClass: ['error-snackbar'],
      data: {
        title: error.title,
        message: error.message,
        retryAction: error.retryAction
      }
    };

    this.snackBar.open(error.message, 'Повторить', config);
  }

  /**
   * Упрощенный метод для показа ошибок API
   */
  showError(message: string, retryAction?: () => void): void {
    if (retryAction) {
      this.showErrorWithRetry({
        title: 'Ошибка',
        message,
        retryAction
      });
    } else {
      this.error('Ошибка', message);
    }
  }

  /**
   * Показывает уведомление через MatSnackBar
   */
  private showSnackBar(message: string, type: 'success' | 'error' | 'warning' | 'info', duration?: number): void {
    const config: MatSnackBarConfig = {
      duration: duration ?? 5000,
      panelClass: [`${type}-snackbar`],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    };

    this.snackBar.open(message, 'Закрыть', config);
  }

  removeNotification(id: string): void {
    const currentNotifications = this.notificationsSubject.value;
    const notificationToRemove = currentNotifications.find(n => n.id === id);

    if (notificationToRemove) {
      const notificationKey = this.generateNotificationKey(
        notificationToRemove.type,
        notificationToRemove.title,
        notificationToRemove.message
      );
      this.activeNotifications.delete(notificationKey);
      this.lastNotificationTime.delete(notificationKey);
    }

    const filteredNotifications = currentNotifications.filter(
      notification => notification.id !== id
    );
    this.notificationsSubject.next(filteredNotifications);
  }

  clearAll(): void {
    this.notificationsSubject.next([]);
    this.activeNotifications.clear();
    this.lastNotificationTime.clear();
    this.snackBar.dismiss();
  }

  getNotifications(): Notification[] {
    return this.notificationsSubject.value;
  }

  /**
   * Очищает только уведомления определенного типа
   */
  clearByType(type: 'success' | 'error' | 'warning' | 'info'): void {
    const currentNotifications = this.notificationsSubject.value;
    const filteredNotifications = currentNotifications.filter(n => n.type !== type);

    // Очищаем активные уведомления этого типа
    currentNotifications.forEach(notification => {
      if (notification.type === type) {
        const notificationKey = this.generateNotificationKey(
          notification.type,
          notification.title,
          notification.message
        );
        this.activeNotifications.delete(notificationKey);
        this.lastNotificationTime.delete(notificationKey);
      }
    });

    this.notificationsSubject.next(filteredNotifications);
  }
}
