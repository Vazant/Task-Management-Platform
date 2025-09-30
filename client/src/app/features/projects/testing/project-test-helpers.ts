import { TestBed } from '@angular/core/testing';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { Project } from '../../core/models/project.model';
import { MOCK_PROJECTS } from './project-test-data';

export class ProjectTestHelpers {
  static createMockStore(): jasmine.SpyObj<Store> {
    const storeSpy = jasmine.createSpyObj('Store', ['select', 'dispatch']);
    
    // Setup default selectors
    storeSpy.select.and.callFake((selector) => {
      if (selector.toString().includes('selectAllProjects')) {
        return of(MOCK_PROJECTS);
      }
      if (selector.toString().includes('selectProjectsLoading')) {
        return of(false);
      }
      if (selector.toString().includes('selectProjectsError')) {
        return of(null);
      }
      if (selector.toString().includes('selectSelectedProject')) {
        return of(null);
      }
      return of(null);
    });

    return storeSpy;
  }

  static createMockDialog(): jasmine.SpyObj<MatDialog> {
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    
    const mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['afterClosed']);
    mockDialogRef.afterClosed.and.returnValue(of(true));
    dialogSpy.open.and.returnValue(mockDialogRef);

    return dialogSpy;
  }

  static createMockProjectService() {
    return jasmine.createSpyObj('ProjectService', [
      'getProjects',
      'getProject',
      'createProject',
      'updateProject',
      'deleteProject',
      'searchProjects',
      'getProjectsStatistics',
      'getProjectsPaginated',
      'getProjectsByTags',
      'getProjectsByDateRange',
      'duplicateProject',
      'archiveProject',
      'unarchiveProject',
      'changeProjectStatus',
      'changeProjectPriority',
      'getProjectHistory',
      'exportProjectsToCSV',
      'importProjectsFromCSV'
    ]);
  }

  static setupTestBed(component: any, additionalProviders: any[] = []) {
    return TestBed.configureTestingModule({
      declarations: [component],
      imports: [
        // Add necessary imports
      ],
      providers: [
        { provide: Store, useValue: this.createMockStore() },
        { provide: MatDialog, useValue: this.createMockDialog() },
        ...additionalProviders
      ]
    });
  }

  static createProject(overrides: Partial<Project> = {}): Project {
    return {
      id: 'test-id',
      name: 'Test Project',
      description: 'Test Description',
      status: 'ACTIVE' as any,
      priority: 'MEDIUM' as any,
      startDate: new Date('2024-01-01'),
      endDate: new Date('2024-12-31'),
      tags: ['test'],
      color: '#1976d2',
      ownerId: 'user1',
      ownerName: 'Test User',
      teamMembers: [],
      tasksCount: 0,
      completedTasksCount: 0,
      progress: 0,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01'),
      ...overrides
    };
  }

  static createProjectFormData() {
    return {
      name: 'Test Project',
      description: 'Test Description',
      priority: 'MEDIUM',
      color: '#1976d2',
      tags: ['test', 'project'],
      startDate: new Date('2024-01-01'),
      endDate: new Date('2024-12-31')
    };
  }

  static simulateAsyncOperation<T>(data: T, delay: number = 100): Promise<T> {
    return new Promise((resolve) => {
      setTimeout(() => resolve(data), delay);
    });
  }

  static simulateError(errorMessage: string = 'Test error'): Promise<never> {
    return new Promise((_, reject) => {
      setTimeout(() => reject(new Error(errorMessage)), 100);
    });
  }

  static createMockHttpResponse<T>(data: T, status: number = 200) {
    return {
      body: data,
      status,
      statusText: 'OK',
      headers: new Map(),
      url: 'http://test.com'
    };
  }

  static createMockBlob(content: string = 'test content'): Blob {
    return new Blob([content], { type: 'text/csv' });
  }

  static createMockFile(name: string = 'test.csv', content: string = 'test content'): File {
    const blob = this.createMockBlob(content);
    return new File([blob], name, { type: 'text/csv' });
  }

  static createMockFormGroup() {
    return {
      value: this.createProjectFormData(),
      valid: true,
      invalid: false,
      touched: false,
      dirty: false,
      pristine: true,
      get: jasmine.createSpy('get').and.returnValue({
        value: 'test',
        hasError: jasmine.createSpy('hasError').and.returnValue(false),
        errors: null,
        touched: false,
        dirty: false,
        pristine: true
      }),
      patchValue: jasmine.createSpy('patchValue'),
      setValue: jasmine.createSpy('setValue'),
      reset: jasmine.createSpy('reset'),
      markAsTouched: jasmine.createSpy('markAsTouched'),
      markAsDirty: jasmine.createSpy('markAsDirty'),
      markAsPristine: jasmine.createSpy('markAsPristine')
    };
  }

  static createMockEvent(type: string = 'click', target?: any) {
    return {
      type,
      target: target || { value: 'test' },
      preventDefault: jasmine.createSpy('preventDefault'),
      stopPropagation: jasmine.createSpy('stopPropagation')
    };
  }

  static createMockKeyboardEvent(key: string = 'Enter') {
    return {
      key,
      preventDefault: jasmine.createSpy('preventDefault'),
      stopPropagation: jasmine.createSpy('stopPropagation')
    };
  }

  static createMockMouseEvent(button: number = 0) {
    return {
      button,
      preventDefault: jasmine.createSpy('preventDefault'),
      stopPropagation: jasmine.createSpy('stopPropagation')
    };
  }

  static waitForAsync(ms: number = 0): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  static createMockObservable<T>(data: T, error?: any) {
    if (error) {
      return of(data).pipe(
        // Simulate error after delay
        delay(100),
        switchMap(() => throwError(() => error))
      );
    }
    return of(data);
  }

  static createMockSubject<T>() {
    return new Subject<T>();
  }

  static createMockBehaviorSubject<T>(initialValue: T) {
    return new BehaviorSubject<T>(initialValue);
  }

  static createMockReplaySubject<T>(bufferSize: number = 1) {
    return new ReplaySubject<T>(bufferSize);
  }

  static createMockAsyncSubject<T>() {
    return new AsyncSubject<T>();
  }

  static createMockPublishSubject<T>() {
    return new PublishSubject<T>();
  }

  static createMockPublishBehaviorSubject<T>(initialValue: T) {
    return new PublishBehaviorSubject<T>(initialValue);
  }

  static createMockPublishReplaySubject<T>(bufferSize: number = 1) {
    return new PublishReplaySubject<T>(bufferSize);
  }

  static createMockPublishAsyncSubject<T>() {
    return new PublishAsyncSubject<T>();
  }

  static createMockPublishSubject<T>() {
    return new PublishSubject<T>();
  }

  static createMockPublishBehaviorSubject<T>(initialValue: T) {
    return new PublishBehaviorSubject<T>(initialValue);
  }

  static createMockPublishReplaySubject<T>(bufferSize: number = 1) {
    return new PublishReplaySubject<T>(bufferSize);
  }

  static createMockPublishAsyncSubject<T>() {
    return new PublishAsyncSubject<T>();
  }
}
