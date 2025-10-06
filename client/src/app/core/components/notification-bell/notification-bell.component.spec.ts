import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NotificationBellComponent } from './notification-bell.component';
import { NotificationService } from '../../services/notification.service';
import { BehaviorSubject, of } from 'rxjs';
import { Notification, NotificationType } from '../../models/notification.model';

describe('NotificationBellComponent', () => {
  let component: NotificationBellComponent;
  let fixture: ComponentFixture<NotificationBellComponent>;
  let notificationService: jasmine.SpyObj<NotificationService>;

  const mockNotifications: Notification[] = [
    {
      id: '1',
      title: 'Test Notification 1',
      message: 'Test message 1',
      type: 'info',
      priority: 'low',
      read: false,
      createdAt: new Date()
    },
    {
      id: '2',
      title: 'Test Notification 2',
      message: 'Test message 2',
      type: 'success',
      priority: 'medium',
      read: true,
      createdAt: new Date()
    }
  ];

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('NotificationService', [
      'markAsRead',
      'markAllAsRead',
      'deleteNotification'
    ], {
      notifications: of(mockNotifications),
      unreadCount: of(1)
    });

    await TestBed.configureTestingModule({
      imports: [NotificationBellComponent, NoopAnimationsModule],
      providers: [
        { provide: NotificationService, useValue: spy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationBellComponent);
    component = fixture.componentInstance;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display notification bell with badge', () => {
    const bellElement = fixture.nativeElement.querySelector('.notification-bell');
    const badgeElement = fixture.nativeElement.querySelector('mat-badge');
    
    expect(bellElement).toBeTruthy();
    expect(badgeElement).toBeTruthy();
  });

  it('should display correct unread count', () => {
    const badgeElement = fixture.nativeElement.querySelector('mat-badge');
    expect(badgeElement.textContent).toContain('1');
  });

  it('should display notifications in menu', () => {
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const notificationItems = fixture.nativeElement.querySelectorAll('.notification-item');
    expect(notificationItems.length).toBe(2);
  });

  it('should display notification title and message', () => {
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const firstNotification = fixture.nativeElement.querySelector('.notification-item');
    const titleElement = firstNotification.querySelector('.notification-title');
    const messageElement = firstNotification.querySelector('.notification-message');

    expect(titleElement.textContent).toContain('Test Notification 1');
    expect(messageElement.textContent).toContain('Test message 1');
  });

  it('should mark notification as read when clicked', () => {
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const firstNotification = fixture.nativeElement.querySelector('.notification-item');
    firstNotification.click();

    expect(notificationService.markAsRead).toHaveBeenCalledWith('1');
  });

  it('should mark all notifications as read when button clicked', () => {
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const markAllButton = fixture.nativeElement.querySelector('button[color="primary"]');
    markAllButton.click();

    expect(notificationService.markAllAsRead).toHaveBeenCalled();
  });

  it('should delete notification when delete button clicked', () => {
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const deleteButton = fixture.nativeElement.querySelector('.notification-delete');
    deleteButton.click();

    expect(notificationService.deleteNotification).toHaveBeenCalledWith('1', jasmine.any(Object));
  });

  it('should display correct notification icons', () => {
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const icons = fixture.nativeElement.querySelectorAll('.notification-icon mat-icon');
    expect(icons[0].textContent.trim()).toBe('info');
    expect(icons[1].textContent.trim()).toBe('check_circle');
  });

  it('should apply correct CSS classes for notification types', () => {
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const notificationItems = fixture.nativeElement.querySelectorAll('.notification-item');
    const firstIcon = notificationItems[0].querySelector('.notification-icon mat-icon');
    
    expect(firstIcon.classList).toContain('notification-type-info');
  });

  it('should display empty state when no notifications', () => {
    // Update the service to return empty notifications
    (notificationService.notifications as any) = of([]);
    (notificationService.unreadCount as any) = of(0);
    
    fixture.detectChanges();
    
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const emptyState = fixture.nativeElement.querySelector('.notification-empty');
    expect(emptyState).toBeTruthy();
    expect(emptyState.textContent).toContain('No notifications');
  });

  it('should disable mark all as read button when no unread notifications', () => {
    // Update the service to return 0 unread count
    (notificationService.unreadCount as any) = of(0);
    
    fixture.detectChanges();
    
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const markAllButton = fixture.nativeElement.querySelector('button[color="primary"]');
    expect(markAllButton.disabled).toBe(true);
  });

  it('should call trackByNotification correctly', () => {
    const result = component.trackByNotification(0, mockNotifications[0]);
    expect(result).toBe('1');
  });

  it('should return correct icon for notification types', () => {
    expect(component.getNotificationIcon('info')).toBe('info');
    expect(component.getNotificationIcon('success')).toBe('check_circle');
    expect(component.getNotificationIcon('warning')).toBe('warning');
    expect(component.getNotificationIcon('error')).toBe('error');
    expect(component.getNotificationIcon('unknown' as any)).toBe('notifications');
  });

  it('should handle view all notifications click', () => {
    spyOn(console, 'log');
    
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const viewAllButton = fixture.nativeElement.querySelector('.notification-footer button');
    viewAllButton.click();

    expect(console.log).toHaveBeenCalledWith('Navigate to notifications page');
  });

  it('should prevent event propagation on bell click', () => {
    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');
    
    const bellElement = fixture.nativeElement.querySelector('.notification-bell');
    bellElement.dispatchEvent(event);

    expect(event.stopPropagation).toHaveBeenCalled();
  });

  it('should prevent event propagation on delete button click', () => {
    const menuTrigger = fixture.nativeElement.querySelector('.notification-bell');
    menuTrigger.click();
    fixture.detectChanges();

    const deleteButton = fixture.nativeElement.querySelector('.notification-delete');
    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');
    
    deleteButton.dispatchEvent(event);

    expect(event.stopPropagation).toHaveBeenCalled();
  });
});

