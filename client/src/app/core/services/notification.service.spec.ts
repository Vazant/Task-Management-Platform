import { TestBed } from '@angular/core/testing';
import { NotificationService } from './notification.service';
import { Notification, NotificationType, NotificationPriority } from '../models/notification.model';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NotificationService]
    });
    service = TestBed.inject(NotificationService);
    
    // Clear localStorage before each test
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Notification Management', () => {
    it('should add a new notification', () => {
      const notification = {
        title: 'Test Notification',
        message: 'Test message',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority
      };

      service.addNotification(notification);

      service.notifications.subscribe(notifications => {
        expect(notifications.length).toBe(1);
        expect(notifications[0].title).toBe('Test Notification');
        expect(notifications[0].message).toBe('Test message');
        expect(notifications[0].type).toBe('info');
        expect(notifications[0].read).toBe(false);
        expect(notifications[0].id).toBeDefined();
        expect(notifications[0].createdAt).toBeInstanceOf(Date);
      });
    });

    it('should mark notification as read', () => {
      const notification = {
        title: 'Test Notification',
        message: 'Test message',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority
      };

      service.addNotification(notification);

      service.notifications.subscribe(notifications => {
        if (notifications.length > 0) {
          service.markAsRead(notifications[0].id);
          
          service.notifications.subscribe(updatedNotifications => {
            expect(updatedNotifications[0].read).toBe(true);
          });
        }
      });
    });

    it('should mark all notifications as read', () => {
      const notification1 = {
        title: 'Test Notification 1',
        message: 'Test message 1',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority
      };

      const notification2 = {
        title: 'Test Notification 2',
        message: 'Test message 2',
        type: 'success' as NotificationType,
        priority: 'medium' as NotificationPriority
      };

      service.addNotification(notification1);
      service.addNotification(notification2);

      service.notifications.subscribe(notifications => {
        if (notifications.length === 2) {
          service.markAllAsRead();
          
          service.notifications.subscribe(updatedNotifications => {
            expect(updatedNotifications[0].read).toBe(true);
            expect(updatedNotifications[1].read).toBe(true);
          });
        }
      });
    });

    it('should delete a notification', () => {
      const notification = {
        title: 'Test Notification',
        message: 'Test message',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority
      };

      service.addNotification(notification);

      service.notifications.subscribe(notifications => {
        if (notifications.length > 0) {
          service.deleteNotification(notifications[0].id);
          
          service.notifications.subscribe(updatedNotifications => {
            expect(updatedNotifications.length).toBe(0);
          });
        }
      });
    });

    it('should clear all notifications', () => {
      const notification1 = {
        title: 'Test Notification 1',
        message: 'Test message 1',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority
      };

      const notification2 = {
        title: 'Test Notification 2',
        message: 'Test message 2',
        type: 'success' as NotificationType,
        priority: 'medium' as NotificationPriority
      };

      service.addNotification(notification1);
      service.addNotification(notification2);

      service.clearAllNotifications();

      service.notifications.subscribe(notifications => {
        expect(notifications.length).toBe(0);
      });
    });
  });

  describe('Quick Notification Methods', () => {
    it('should show info notification', () => {
      service.showInfo('Info Title', 'Info message');

      service.notifications.subscribe(notifications => {
        expect(notifications.length).toBe(1);
        expect(notifications[0].title).toBe('Info Title');
        expect(notifications[0].message).toBe('Info message');
        expect(notifications[0].type).toBe('info');
        expect(notifications[0].priority).toBe('low');
      });
    });

    it('should show success notification', () => {
      service.showSuccess('Success Title', 'Success message');

      service.notifications.subscribe(notifications => {
        expect(notifications.length).toBe(1);
        expect(notifications[0].title).toBe('Success Title');
        expect(notifications[0].message).toBe('Success message');
        expect(notifications[0].type).toBe('success');
        expect(notifications[0].priority).toBe('medium');
      });
    });

    it('should show warning notification', () => {
      service.showWarning('Warning Title', 'Warning message');

      service.notifications.subscribe(notifications => {
        expect(notifications.length).toBe(1);
        expect(notifications[0].title).toBe('Warning Title');
        expect(notifications[0].message).toBe('Warning message');
        expect(notifications[0].type).toBe('warning');
        expect(notifications[0].priority).toBe('high');
      });
    });

    it('should show error notification', () => {
      service.showError('Error Title', 'Error message');

      service.notifications.subscribe(notifications => {
        expect(notifications.length).toBe(1);
        expect(notifications[0].title).toBe('Error Title');
        expect(notifications[0].message).toBe('Error message');
        expect(notifications[0].type).toBe('error');
        expect(notifications[0].priority).toBe('urgent');
      });
    });
  });

  describe('Unread Count', () => {
    it('should track unread count correctly', () => {
      const notification = {
        title: 'Test Notification',
        message: 'Test message',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority
      };

      service.addNotification(notification);

      service.unreadCount.subscribe(count => {
        expect(count).toBe(1);
      });

      service.notifications.subscribe(notifications => {
        if (notifications.length > 0) {
          service.markAsRead(notifications[0].id);
          
          service.unreadCount.subscribe(count => {
            expect(count).toBe(0);
          });
        }
      });
    });

    it('should update unread count when marking all as read', () => {
      const notification1 = {
        title: 'Test Notification 1',
        message: 'Test message 1',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority
      };

      const notification2 = {
        title: 'Test Notification 2',
        message: 'Test message 2',
        type: 'success' as NotificationType,
        priority: 'medium' as NotificationPriority
      };

      service.addNotification(notification1);
      service.addNotification(notification2);

      service.unreadCount.subscribe(count => {
        expect(count).toBe(2);
      });

      service.markAllAsRead();

      service.unreadCount.subscribe(count => {
        expect(count).toBe(0);
      });
    });
  });

  describe('Preferences Management', () => {
    it('should load default preferences', () => {
      service.preferences.subscribe(preferences => {
        expect(preferences.email).toBe(true);
        expect(preferences.push).toBe(true);
        expect(preferences.inApp).toBe(true);
        expect(preferences.types.info).toBe(true);
        expect(preferences.types.success).toBe(true);
        expect(preferences.types.warning).toBe(true);
        expect(preferences.types.error).toBe(true);
        expect(preferences.quietHours.enabled).toBe(false);
      });
    });

    it('should update preferences', () => {
      service.updatePreferences({
        email: false,
        types: { info: false, success: true, warning: true, error: true }
      });

      service.preferences.subscribe(preferences => {
        expect(preferences.email).toBe(false);
        expect(preferences.push).toBe(true); // Should remain unchanged
        expect(preferences.types.info).toBe(false);
        expect(preferences.types.success).toBe(true);
      });
    });
  });

  describe('Persistence', () => {
    it('should save and load notifications from localStorage', () => {
      const notification = {
        title: 'Test Notification',
        message: 'Test message',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority
      };

      service.addNotification(notification);

      // Create a new service instance to test loading
      const newService = TestBed.inject(NotificationService);

      newService.notifications.subscribe(notifications => {
        expect(notifications.length).toBe(1);
        expect(notifications[0].title).toBe('Test Notification');
        expect(notifications[0].message).toBe('Test message');
      });
    });

    it('should save and load preferences from localStorage', () => {
      service.updatePreferences({
        email: false,
        push: false
      });

      // Create a new service instance to test loading
      const newService = TestBed.inject(NotificationService);

      newService.preferences.subscribe(preferences => {
        expect(preferences.email).toBe(false);
        expect(preferences.push).toBe(false);
      });
    });
  });

  describe('Cleanup', () => {
    it('should cleanup expired notifications', () => {
      const expiredNotification = {
        title: 'Expired Notification',
        message: 'Expired message',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority,
        expiresAt: new Date(Date.now() - 1000) // Expired 1 second ago
      };

      const validNotification = {
        title: 'Valid Notification',
        message: 'Valid message',
        type: 'info' as NotificationType,
        priority: 'low' as NotificationPriority,
        expiresAt: new Date(Date.now() + 1000) // Expires in 1 second
      };

      service.addNotification(expiredNotification);
      service.addNotification(validNotification);

      service.cleanupExpiredNotifications();

      service.notifications.subscribe(notifications => {
        expect(notifications.length).toBe(1);
        expect(notifications[0].title).toBe('Valid Notification');
      });
    });
  });
});

