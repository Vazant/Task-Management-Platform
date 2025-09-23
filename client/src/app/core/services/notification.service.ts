import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, timer } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { Notification, NotificationType, NotificationPriority, NotificationPreferences } from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  readonly notifications$ = new BehaviorSubject<Notification[]>([]);
  readonly unreadCount$ = new BehaviorSubject<number>(0);
  readonly preferences$ = new BehaviorSubject<NotificationPreferences>(this.getDefaultPreferences());

  constructor() {
    this.loadNotifications();
    this.loadPreferences();
    this.startNotificationPolling();
  }

  // Observable streams
  get notifications(): Observable<Notification[]> {
    return this.notifications$.asObservable();
  }

  get unreadCount(): Observable<number> {
    return this.unreadCount$.asObservable();
  }

  get preferences(): Observable<NotificationPreferences> {
    return this.preferences$.asObservable();
  }

  // Notification management
  addNotification(notification: Omit<Notification, 'id' | 'createdAt' | 'read'>): void {
    const newNotification: Notification = {
      ...notification,
      id: this.generateId(),
      createdAt: new Date(),
      read: false
    };

    const currentNotifications = this.notifications$.value;
    this.notifications$.next([newNotification, ...currentNotifications]);
    this.updateUnreadCount();
    this.saveNotifications();
  }

  markAsRead(notificationId: string): void {
    const notifications = this.notifications$.value.map(notification =>
      notification.id === notificationId ? { ...notification, read: true } : notification
    );
    this.notifications$.next(notifications);
    this.updateUnreadCount();
    this.saveNotifications();
  }

  markAllAsRead(): void {
    const notifications = this.notifications$.value.map(notification => ({
      ...notification,
      read: true
    }));
    this.notifications$.next(notifications);
    this.updateUnreadCount();
    this.saveNotifications();
  }

  deleteNotification(notificationId: string): void {
    const notifications = this.notifications$.value.filter(
      notification => notification.id !== notificationId
    );
    this.notifications$.next(notifications);
    this.updateUnreadCount();
    this.saveNotifications();
  }

  clearAllNotifications(): void {
    this.notifications$.next([]);
    this.updateUnreadCount();
    this.saveNotifications();
  }

  // Quick notification methods
  showInfo(title: string, message: string, options?: Partial<Notification>): void {
    this.addNotification({
      title,
      message,
      type: 'info',
      priority: 'low',
      ...options
    });
  }

  showSuccess(title: string, message: string, options?: Partial<Notification>): void {
    this.addNotification({
      title,
      message,
      type: 'success',
      priority: 'medium',
      ...options
    });
  }

  showError(title: string, message: string, options?: Partial<Notification>): void {
    this.addNotification({
      title,
      message,
      type: 'error',
      priority: 'high',
      ...options
    });
  }

  showWarning(title: string, message: string, options?: Partial<Notification>): void {
    this.addNotification({
      title,
      message,
      type: 'warning',
      priority: 'medium',
      ...options
    });
  }

  info(title: string, message: string): void {
    this.showInfo(title, message);
  }

  error(title: string, message: string): void {
    this.showError(title, message);
  }

  warning(title: string, message: string): void {
    this.showWarning(title, message);
  }

  success(title: string, message: string): void {
    this.showSuccess(title, message);
  }

  clearAll(): void {
    this.clearAllNotifications();
  }

  // Preferences management
  updatePreferences(preferences: Partial<NotificationPreferences>): void {
    const currentPreferences = this.preferences$.value;
    const updatedPreferences = { ...currentPreferences, ...preferences };
    this.preferences$.next(updatedPreferences);
    this.savePreferences();
  }

  // Utility methods
  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }

  private updateUnreadCount(): void {
    const unreadCount = this.notifications$.value.filter(n => !n.read).length;
    this.unreadCount$.next(unreadCount);
  }

  private getDefaultPreferences(): NotificationPreferences {
    return {
      email: true,
      push: true,
      inApp: true,
      types: {
        info: true,
        success: true,
        warning: true,
        error: true
      },
      quietHours: {
        enabled: false,
        start: '22:00',
        end: '08:00'
      }
    };
  }

  // Persistence
  private loadNotifications(): void {
    try {
      const stored = localStorage.getItem('notifications');
      if (stored) {
        const notifications = JSON.parse(stored).map((n: any) => ({
          ...n,
          createdAt: new Date(n.createdAt),
          expiresAt: n.expiresAt ? new Date(n.expiresAt) : undefined
        }));
        this.notifications$.next(notifications);
        this.updateUnreadCount();
      }
    } catch (error) {
      console.error('Error loading notifications:', error);
    }
  }

  private saveNotifications(): void {
    try {
      localStorage.setItem('notifications', JSON.stringify(this.notifications$.value));
    } catch (error) {
      console.error('Error saving notifications:', error);
    }
  }

  private loadPreferences(): void {
    try {
      const stored = localStorage.getItem('notificationPreferences');
      if (stored) {
        this.preferences$.next(JSON.parse(stored));
      }
    } catch (error) {
      console.error('Error loading notification preferences:', error);
    }
  }

  private savePreferences(): void {
    try {
      localStorage.setItem('notificationPreferences', JSON.stringify(this.preferences$.value));
    } catch (error) {
      console.error('Error saving notification preferences:', error);
    }
  }

  // Polling for new notifications
  private startNotificationPolling(): void {
    // Poll every 30 seconds for new notifications
    timer(0, 30000).pipe(
      switchMap(() => this.checkForNewNotifications())
    ).subscribe();
  }

  private checkForNewNotifications(): Observable<void> {
    // In a real app, this would make an API call
    // For now, we'll just return an empty observable
    return of(void 0);
  }

  // Cleanup expired notifications
  cleanupExpiredNotifications(): void {
    const now = new Date();
    const notifications = this.notifications$.value.filter(notification => {
      if (!notification.expiresAt) return true;
      return notification.expiresAt > now;
    });
    this.notifications$.next(notifications);
    this.saveNotifications();
  }
}
