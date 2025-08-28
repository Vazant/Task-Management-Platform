import { ComponentFixture } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { Store } from '@ngrx/store';
import { MockStore } from '@ngrx/store/testing';
import { of } from 'rxjs';

import { User, Task, Project } from '@core/models';

/**
 * Test utilities for common testing operations
 */
export class TestUtils {
  /**
   * Get element by CSS selector
   */
  static getElement<T>(fixture: ComponentFixture<T>, selector: string): DebugElement | null {
    return fixture.debugElement.query(By.css(selector));
  }

  /**
   * Get all elements by CSS selector
   */
  static getAllElements<T>(fixture: ComponentFixture<T>, selector: string): DebugElement[] {
    return fixture.debugElement.queryAll(By.css(selector));
  }

  /**
   * Get element by test ID
   */
  static getElementByTestId<T>(fixture: ComponentFixture<T>, testId: string): DebugElement | null {
    return fixture.debugElement.query(By.css(`[data-testid="${testId}"]`));
  }

  /**
   * Trigger event on element
   */
  static triggerEvent(element: DebugElement, eventName: string, eventObj: any = {}): void {
    element.triggerEventHandler(eventName, eventObj);
  }

  /**
   * Set input value
   */
  static setInputValue(element: DebugElement, value: string): void {
    element.triggerEventHandler('input', { target: { value } });
  }

  /**
   * Click element
   */
  static clickElement(element: DebugElement): void {
    element.triggerEventHandler('click', null);
  }

  /**
   * Get text content of element
   */
  static getTextContent(element: DebugElement): string {
    return element.nativeElement.textContent.trim();
  }

  /**
   * Check if element has class
   */
  static hasClass(element: DebugElement, className: string): boolean {
    return element.nativeElement.classList.contains(className);
  }

  /**
   * Check if element is visible
   */
  static isVisible(element: DebugElement): boolean {
    const style = window.getComputedStyle(element.nativeElement);
    return style.display !== 'none' && style.visibility !== 'hidden' && style.opacity !== '0';
  }

  /**
   * Wait for async operations
   */
  static async waitForAsync(fixture: ComponentFixture<any>): Promise<void> {
    fixture.detectChanges();
    await fixture.whenStable();
  }

  /**
   * Dispatch store action
   */
  static dispatchAction(store: MockStore, action: any): void {
    store.dispatch(action);
  }

  /**
   * Set store state
   */
  static setStoreState(store: MockStore, state: any): void {
    store.setState(state);
    store.refreshState();
  }

  /**
   * Mock HTTP response
   */
  static mockHttpResponse(mockService: any, method: string, response: any): void {
    mockService[method].and.returnValue(of(response));
  }

  /**
   * Mock HTTP error
   */
  static mockHttpError(mockService: any, method: string, error: any): void {
    mockService[method].and.returnValue(throwError(() => error));
  }
}

/**
 * Test data factories for creating mock objects
 */
export class TestDataFactory {
  /**
   * Create mock user
   */
  static createUser(overrides: Partial<User> = {}): User {
    return {
      id: '1',
      email: 'test@example.com',
      profile: {
        firstName: 'John',
        lastName: 'Doe',
        phone: '+1234567890',
        bio: 'Test bio',
        location: 'New York',
        website: 'https://example.com',
        dateOfBirth: '1990-01-01',
        avatarUrl: 'https://example.com/avatar.jpg'
      },
      preferences: {
        language: 'en',
        timezone: 'UTC',
        dateFormat: 'MM/DD/YYYY',
        timeFormat: '12h',
        theme: 'light',
        notifications: {
          email: true,
          push: true,
          sms: false,
          taskReminders: true,
          projectUpdates: true,
          teamMessages: true
        }
      },
      settings: {
        privacy: {
          profileVisibility: 'public',
          showEmail: true,
          showPhone: false,
          showLocation: true,
          allowMessages: true
        },
        security: {
          twoFactorAuth: false,
          sessionTimeout: 30,
          passwordExpiry: 90,
          loginNotifications: true
        },
        display: {
          compactMode: false,
          showAvatars: true,
          showStatus: true,
          autoRefresh: true,
          refreshInterval: 30
        }
      },
      ...overrides
    };
  }

  /**
   * Create mock task
   */
  static createTask(overrides: Partial<Task> = {}): Task {
    return {
      id: '1',
      title: 'Test Task',
      description: 'Test task description',
      status: 'todo',
      priority: 'medium',
      assignee: 'user1',
      dueDate: new Date(),
      createdAt: new Date(),
      updatedAt: new Date(),
      estimatedHours: 4,
      labels: [
        { name: 'bug', color: '#ff0000' },
        { name: 'feature', color: '#00ff00' }
      ],
      subtasks: [
        { id: '1', title: 'Subtask 1', completed: false },
        { id: '2', title: 'Subtask 2', completed: true }
      ],
      ...overrides
    };
  }

  /**
   * Create mock project
   */
  static createProject(overrides: Partial<Project> = {}): Project {
    return {
      id: '1',
      name: 'Test Project',
      description: 'Test project description',
      status: 'active',
      owner: 'user1',
      members: ['user1', 'user2'],
      createdAt: new Date(),
      updatedAt: new Date(),
      ...overrides
    };
  }

  /**
   * Create list of mock tasks
   */
  static createTaskList(count: number = 3, overrides: Partial<Task> = {}): Task[] {
    return Array.from({ length: count }, (_, i) => 
      this.createTask({ 
        id: `${i + 1}`, 
        title: `Task ${i + 1}`,
        ...overrides 
      })
    );
  }

  /**
   * Create list of mock projects
   */
  static createProjectList(count: number = 3, overrides: Partial<Project> = {}): Project[] {
    return Array.from({ length: count }, (_, i) => 
      this.createProject({ 
        id: `${i + 1}`, 
        name: `Project ${i + 1}`,
        ...overrides 
      })
    );
  }

  /**
   * Create list of mock users
   */
  static createUserList(count: number = 3, overrides: Partial<User> = {}): User[] {
    return Array.from({ length: count }, (_, i) => 
      this.createUser({ 
        id: `${i + 1}`, 
        email: `user${i + 1}@example.com`,
        profile: {
          firstName: `User${i + 1}`,
          lastName: 'Test'
        },
        ...overrides 
      })
    );
  }
}

/**
 * Mock services for testing
 */
export class MockServices {
  /**
   * Create mock user service
   */
  static createUserService() {
    return jasmine.createSpyObj('UserService', [
      'getCurrentUser',
      'updateUser',
      'updateUserProfile',
      'exportUserData',
      'deleteUser',
      'changePassword',
      'enableTwoFactor',
      'disableTwoFactor'
    ]);
  }

  /**
   * Create mock auth service
   */
  static createAuthService() {
    return jasmine.createSpyObj('AuthService', [
      'login',
      'logout',
      'register',
      'forgotPassword',
      'resetPassword',
      'refreshToken',
      'isAuthenticated',
      'getCurrentUser'
    ]);
  }

  /**
   * Create mock task service
   */
  static createTaskService() {
    return jasmine.createSpyObj('TaskService', [
      'getTasks',
      'getTask',
      'createTask',
      'updateTask',
      'deleteTask',
      'getTasksByProject',
      'getTasksByAssignee',
      'getTasksByStatus'
    ]);
  }

  /**
   * Create mock project service
   */
  static createProjectService() {
    return jasmine.createSpyObj('ProjectService', [
      'getProjects',
      'getProject',
      'createProject',
      'updateProject',
      'deleteProject',
      'addMember',
      'removeMember',
      'getProjectMembers'
    ]);
  }

  /**
   * Create mock notification service
   */
  static createNotificationService() {
    return jasmine.createSpyObj('NotificationService', [
      'showSuccess',
      'showError',
      'showWarning',
      'showInfo',
      'showConfirm',
      'showPrompt'
    ]);
  }
}

/**
 * Test helpers for common assertions
 */
export class TestAssertions {
  /**
   * Assert element exists
   */
  static assertElementExists<T>(fixture: ComponentFixture<T>, selector: string): void {
    const element = TestUtils.getElement(fixture, selector);
    expect(element).toBeTruthy();
  }

  /**
   * Assert element does not exist
   */
  static assertElementNotExists<T>(fixture: ComponentFixture<T>, selector: string): void {
    const element = TestUtils.getElement(fixture, selector);
    expect(element).toBeFalsy();
  }

  /**
   * Assert element has text content
   */
  static assertElementHasText<T>(fixture: ComponentFixture<T>, selector: string, expectedText: string): void {
    const element = TestUtils.getElement(fixture, selector);
    expect(element).toBeTruthy();
    expect(TestUtils.getTextContent(element!)).toContain(expectedText);
  }

  /**
   * Assert element has class
   */
  static assertElementHasClass<T>(fixture: ComponentFixture<T>, selector: string, className: string): void {
    const element = TestUtils.getElement(fixture, selector);
    expect(element).toBeTruthy();
    expect(TestUtils.hasClass(element!, className)).toBe(true);
  }

  /**
   * Assert element is visible
   */
  static assertElementIsVisible<T>(fixture: ComponentFixture<T>, selector: string): void {
    const element = TestUtils.getElement(fixture, selector);
    expect(element).toBeTruthy();
    expect(TestUtils.isVisible(element!)).toBe(true);
  }

  /**
   * Assert element is not visible
   */
  static assertElementIsNotVisible<T>(fixture: ComponentFixture<T>, selector: string): void {
    const element = TestUtils.getElement(fixture, selector);
    expect(element).toBeTruthy();
    expect(TestUtils.isVisible(element!)).toBe(false);
  }

  /**
   * Assert form control has error
   */
  static assertFormControlHasError(form: any, controlName: string, errorType: string): void {
    const control = form.get(controlName);
    expect(control).toBeTruthy();
    expect(control.errors?.[errorType]).toBeTruthy();
  }

  /**
   * Assert form control is valid
   */
  static assertFormControlIsValid(form: any, controlName: string): void {
    const control = form.get(controlName);
    expect(control).toBeTruthy();
    expect(control.valid).toBe(true);
  }

  /**
   * Assert form is valid
   */
  static assertFormIsValid(form: any): void {
    expect(form.valid).toBe(true);
  }

  /**
   * Assert form is invalid
   */
  static assertFormIsInvalid(form: any): void {
    expect(form.valid).toBe(false);
  }

  /**
   * Assert service method was called
   */
  static assertServiceMethodCalled(service: any, methodName: string): void {
    expect(service[methodName]).toHaveBeenCalled();
  }

  /**
   * Assert service method was called with arguments
   */
  static assertServiceMethodCalledWith(service: any, methodName: string, args: any[]): void {
    expect(service[methodName]).toHaveBeenCalledWith(...args);
  }

  /**
   * Assert store action was dispatched
   */
  static assertActionDispatched(store: MockStore, actionType: string): void {
    const spy = spyOn(store, 'dispatch').and.callThrough();
    expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ type: actionType }));
  }
}

/**
 * Test configuration helpers
 */
export class TestConfig {
  /**
   * Create basic test module configuration
   */
  static createBasicTestConfig(component: any, providers: any[] = []) {
    return {
      imports: [
        component,
        // Add common imports here
      ],
      providers: [
        ...providers
      ]
    };
  }

  /**
   * Create test module with NgRx store
   */
  static createTestConfigWithStore(component: any, initialState: any = {}, providers: any[] = []) {
    return {
      imports: [
        component,
        // Add common imports here
      ],
      providers: [
        provideMockStore({ initialState }),
        ...providers
      ]
    };
  }

  /**
   * Create test module with HTTP testing
   */
  static createTestConfigWithHttp(component: any, providers: any[] = []) {
    return {
      imports: [
        component,
        HttpClientTestingModule,
        // Add common imports here
      ],
      providers: [
        ...providers
      ]
    };
  }
}

// Import missing dependencies
import { provideMockStore } from '@ngrx/store/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { throwError } from 'rxjs';
