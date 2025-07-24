import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  duration?: number;
  timestamp: Date;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }

  private addNotification(notification: Omit<Notification, 'id' | 'timestamp'>): void {
    const newNotification: Notification = {
      ...notification,
      id: this.generateId(),
      timestamp: new Date()
    };

    const currentNotifications = this.notificationsSubject.value;
    this.notificationsSubject.next([...currentNotifications, newNotification]);

    // Автоматическое удаление уведомления
    if (notification.duration !== 0) {
      setTimeout(() => {
        this.removeNotification(newNotification.id);
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

  removeNotification(id: string): void {
    const currentNotifications = this.notificationsSubject.value;
    const filteredNotifications = currentNotifications.filter(
      notification => notification.id !== id
    );
    this.notificationsSubject.next(filteredNotifications);
  }

  clearAll(): void {
    this.notificationsSubject.next([]);
  }

  getNotifications(): Notification[] {
    return this.notificationsSubject.value;
  }
}
