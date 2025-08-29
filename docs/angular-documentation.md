# Angular Documentation Patterns

## Overview

This document outlines the documentation patterns and best practices used throughout the Task Management Platform Angular application.

## Table of Contents

1. [JSDoc Comments](#jsdoc-comments)
2. [Component Documentation](#component-documentation)
3. [Service Documentation](#service-documentation)
4. [NgRx Documentation](#ngrx-documentation)
5. [API Documentation](#api-documentation)
6. [README Best Practices](#readme-best-practices)
7. [Storybook Documentation](#storybook-documentation)
8. [Testing Documentation](#testing-documentation)

## JSDoc Comments

### Component Documentation

```typescript
/**
 * TaskListComponent displays a list of tasks with filtering and sorting capabilities.
 * 
 * @description
 * This component provides a comprehensive task management interface with:
 * - Task filtering by status, priority, and assignee
 * - Sorting by various criteria
 * - Bulk operations
 * - Real-time updates
 * 
 * @example
 * ```html
 * <app-task-list
 *   [tasks]="tasks$ | async"
 *   [loading]="loading$ | async"
 *   (taskSelected)="onTaskSelected($event)"
 *   (taskUpdated)="onTaskUpdated($event)">
 * </app-task-list>
 * ```
 * 
 * @since 1.0.0
 * @author Development Team
 */
@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskListComponent implements OnInit, OnDestroy {
  /**
   * Observable of tasks to display
   */
  @Input() tasks$!: Observable<Task[]>;

  /**
   * Observable indicating loading state
   */
  @Input() loading$!: Observable<boolean>;

  /**
   * Event emitted when a task is selected
   */
  @Output() taskSelected = new EventEmitter<Task>();

  /**
   * Event emitted when a task is updated
   */
  @Output() taskUpdated = new EventEmitter<Task>();

  /**
   * Initializes the component
   */
  ngOnInit(): void {
    this.initializeFilters();
    this.subscribeToTasks();
  }

  /**
   * Handles task selection
   * @param task - The selected task
   */
  onTaskSelect(task: Task): void {
    this.taskSelected.emit(task);
  }
}
```

### Service Documentation

```typescript
/**
 * TaskService provides CRUD operations for tasks and manages task-related business logic.
 * 
 * @description
 * This service handles:
 * - Task creation, reading, updating, and deletion
 * - Task filtering and searching
 * - Task validation and business rules
 * - Integration with backend APIs
 * 
 * @example
 * ```typescript
 * constructor(private taskService: TaskService) {}
 * 
 * createTask(task: Task): void {
 *   this.taskService.createTask(task).subscribe({
 *     next: (createdTask) => console.log('Task created:', createdTask),
 *     error: (error) => console.error('Error creating task:', error)
 *   });
 * }
 * ```
 * 
 * @since 1.0.0
 * @author Development Team
 */
@Injectable({
  providedIn: 'root'
})
export class TaskService {
  /**
   * Creates a new task
   * @param task - The task to create
   * @returns Observable of the created task
   * @throws {ValidationError} When task data is invalid
   * @throws {NetworkError} When network request fails
   */
  createTask(task: CreateTaskRequest): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  /**
   * Retrieves a task by its ID
   * @param id - The task ID
   * @returns Observable of the task
   * @throws {NotFoundError} When task is not found
   */
  getTaskById(id: string): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError.bind(this))
    );
  }
}
```

## API Documentation

### Interface Documentation

```typescript
/**
 * Represents a task in the system
 * 
 * @description
 * A task is a unit of work that can be assigned to users,
 * tracked for progress, and managed through various workflows.
 * 
 * @example
 * ```typescript
 * const task: Task = {
 *   id: 'task-123',
 *   title: 'Implement user authentication',
 *   description: 'Add JWT-based authentication to the application',
 *   status: 'in-progress',
 *   priority: 'high',
 *   assigneeId: 'user-456',
 *   projectId: 'project-789',
 *   dueDate: new Date('2024-01-15'),
 *   createdAt: new Date('2024-01-01'),
 *   updatedAt: new Date('2024-01-10')
 * };
 * ```
 */
export interface Task {
  /** Unique identifier for the task */
  id: string;

  /** Human-readable title of the task */
  title: string;

  /** Detailed description of the task */
  description?: string;

  /** Current status of the task */
  status: TaskStatus;

  /** Priority level of the task */
  priority: TaskPriority;

  /** ID of the user assigned to the task */
  assigneeId?: string;

  /** ID of the project this task belongs to */
  projectId: string;

  /** Due date for task completion */
  dueDate?: Date;

  /** Date when the task was created */
  createdAt: Date;

  /** Date when the task was last updated */
  updatedAt: Date;
}

/**
 * Available task statuses
 */
export type TaskStatus = 
  | 'pending'     // Task is created but not started
  | 'in-progress' // Task is currently being worked on
  | 'review'      // Task is completed and awaiting review
  | 'completed'   // Task is completed and approved
  | 'cancelled';  // Task has been cancelled

/**
 * Available task priorities
 */
export type TaskPriority = 
  | 'low'     // Low priority task
  | 'medium'  // Medium priority task
  | 'high'    // High priority task
  | 'urgent'; // Urgent task requiring immediate attention
```

## Testing Documentation

### Test Documentation Patterns

```typescript
/**
 * TaskService unit tests
 * 
 * @description
 * Tests for the TaskService which handles CRUD operations for tasks.
 * Covers all public methods and error scenarios.
 * 
 * @group services
 * @group tasks
 */
describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;
  let notificationService: jasmine.SpyObj<NotificationService>;

  beforeEach(() => {
    const notificationSpy = jasmine.createSpyObj('NotificationService', [
      'showSuccess',
      'showError',
      'showInfo'
    ]);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        TaskService,
        { provide: NotificationService, useValue: notificationSpy }
      ]
    });

    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  /**
   * Tests task creation functionality
   * 
   * @description
   * Verifies that the service correctly creates tasks and handles
   * both success and error scenarios.
   */
  describe('createTask', () => {
    const mockTask: CreateTaskRequest = {
      title: 'Test Task',
      description: 'Test Description',
      projectId: 'project-1'
    };

    it('should create a task successfully', (done) => {
      service.createTask(mockTask).subscribe({
        next: (task) => {
          expect(task).toBeDefined();
          expect(notificationService.showSuccess).toHaveBeenCalledWith('Task created successfully');
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne('/api/tasks');
      expect(req.request.method).toBe('POST');
      req.flush(mockTask);
    });
  });
});
```

## Best Practices Summary

### Documentation Standards

1. **JSDoc Comments**
   - Use `@description` for detailed explanations
   - Include `@example` for usage examples
   - Document all public methods and properties
   - Use `@since` for version tracking
   - Include `@author` for attribution

2. **Component Documentation**
   - Document inputs, outputs, and events
   - Include usage examples
   - Explain component behavior and features
   - Document accessibility features

3. **Service Documentation**
   - Document all public methods
   - Include error scenarios
   - Provide usage examples
   - Document dependencies and side effects

4. **API Documentation**
   - Use TypeScript interfaces for type safety
   - Document all properties and methods
   - Include validation rules
   - Provide examples for complex types

5. **Testing Documentation**
   - Group related tests with `describe`
   - Use descriptive test names
   - Document test scenarios
   - Include setup and teardown explanations

### Documentation Maintenance

1. **Keep Documentation Updated**
   - Update docs when code changes
   - Review documentation regularly
   - Remove outdated information

2. **Use Consistent Formatting**
   - Follow established patterns
   - Use consistent terminology
   - Maintain consistent structure

3. **Include Examples**
   - Provide real-world usage examples
   - Show common patterns and best practices
   - Include error handling examples
