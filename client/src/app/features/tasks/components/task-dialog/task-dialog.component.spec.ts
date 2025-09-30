import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import { TaskDialogComponent, TaskDialogData } from './task-dialog.component';
import { Task } from '@models';

describe('TaskDialogComponent', () => {
  let component: TaskDialogComponent;
  let fixture: ComponentFixture<TaskDialogComponent>;
  let mockStore: MockStore;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<TaskDialogComponent>>;

  const mockTask: Task = {
    id: '1',
    title: 'Test Task',
    description: 'Test Description',
    status: 'in-progress',
    priority: 'medium',
    assigneeId: 'john.doe',
    projectId: 'test-project',
    creatorId: 'creator-1',
    subtasks: [],
    timeSpent: 0,
    dueDate: new Date('2025-08-15'),
    estimatedHours: 4,
    labels: ['bug'],
    createdAt: new Date('2025-08-01T10:00:00Z'),
    updatedAt: new Date('2025-08-01T10:00:00Z')
  };

  const mockDialogData: TaskDialogData = {
    mode: 'create',
    initialStatus: 'backlog'
  };

  beforeEach(async () => {
    const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [
        TaskDialogComponent,
        NoopAnimationsModule
      ],
      providers: [
        provideMockStore({
          initialState: {
            tasks: {
              loading: false,
              error: null,
              entities: {},
              ids: []
            }
          }
        }),
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: mockDialogData }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskDialogComponent);
    component = fixture.componentInstance;
    mockStore = TestBed.inject(MockStore);
    mockDialogRef = TestBed.inject(MatDialogRef) as jasmine.SpyObj<MatDialogRef<TaskDialogComponent>>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values for create mode', () => {
    component.ngOnInit();
    
    expect(component.taskForm.get('status')?.value).toBe('backlog');
    expect(component.taskForm.get('priority')?.value).toBe('medium');
    expect(component.taskForm.get('title')?.value).toBe('');
    expect(component.taskForm.get('description')?.value).toBe('');
  });

  it('should patch form with task data for edit mode', () => {
    const editData: TaskDialogData = {
      mode: 'edit',
      task: mockTask
    };

    // Create new TestBed for this test
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [TaskDialogComponent, ReactiveFormsModule, MatDialogModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: editData },
        { provide: MatDialogRef, useValue: mockDialogRef },
        provideMockStore({
          initialState: {
            tasks: {
              tasks: [],
              loading: false,
              error: null
            }
          }
        })
      ]
    });
    
    fixture = TestBed.createComponent(TaskDialogComponent);
    component = fixture.componentInstance;
    
    component.ngOnInit();
    
    expect(component.taskForm.get('title')?.value).toBe('Test Task');
    expect(component.taskForm.get('description')?.value).toBe('Test Description');
    expect(component.taskForm.get('status')?.value).toBe('in-progress');
    expect(component.taskForm.get('priority')?.value).toBe('medium');
    expect(component.taskForm.get('assigneeId')?.value).toBe('john.doe');
  });

  it('should validate required fields', () => {
    component.ngOnInit();
    
    const titleControl = component.taskForm.get('title');
    const statusControl = component.taskForm.get('status');
    
    expect(titleControl?.valid).toBeFalsy();
    expect(statusControl?.valid).toBeTruthy(); // status has default value
    
    titleControl?.setValue('Test Task');
    expect(titleControl?.valid).toBeTruthy();
  });

  it('should validate title length', () => {
    component.ngOnInit();
    
    const titleControl = component.taskForm.get('title');
    
    titleControl?.setValue('ab'); // Too short
    titleControl?.markAsTouched();
    titleControl?.updateValueAndValidity();
    expect(titleControl?.hasError('minlength')).toBeTruthy();
    
    titleControl?.setValue('a'.repeat(101)); // Too long
    titleControl?.markAsTouched();
    titleControl?.updateValueAndValidity();
    expect(titleControl?.hasError('maxlength')).toBeTruthy();
    
    titleControl?.setValue('Valid Title');
    titleControl?.markAsTouched();
    titleControl?.updateValueAndValidity();
    expect(titleControl?.valid).toBeTruthy();
  });

  it('should validate estimated hours', () => {
    component.ngOnInit();
    
    const hoursControl = component.taskForm.get('estimatedHours');
    
    hoursControl?.setValue(0.3); // Too low (min is 0.5)
    hoursControl?.markAsTouched();
    hoursControl?.updateValueAndValidity();
    expect(hoursControl?.hasError('min')).toBeTruthy();
    
    hoursControl?.setValue(1500); // Too high (max is 1000)
    hoursControl?.markAsTouched();
    hoursControl?.updateValueAndValidity();
    expect(hoursControl?.hasError('max')).toBeTruthy();
    
    hoursControl?.setValue(4);
    hoursControl?.updateValueAndValidity();
    expect(hoursControl?.valid).toBeTruthy();
  });

  it('should add label when addLabel is called', () => {
    component.ngOnInit();
    
    const event = {
      value: 'new-label',
      chipInput: { clear: jasmine.createSpy() }
    };
    
    component.addLabel(event);
    
    const labels = component.taskForm.get('labels')?.value;
    expect(labels).toContain('new-label');
    expect(event.chipInput.clear).toHaveBeenCalled();
  });

  it('should remove label when removeLabel is called', () => {
    component.ngOnInit();
    
    const label = 'test-label';
    component.taskForm.patchValue({ labels: [label] });
    
    component.removeLabel('test-label');
    
    const labels = component.taskForm.get('labels')?.value;
    expect(labels).not.toContain('test-label');
  });

  it('should dispatch createTask action when form is valid in create mode', () => {
    spyOn(component['store'], 'dispatch');
    component.ngOnInit();
    
    component.taskForm.patchValue({
      title: 'New Task',
      description: 'New Description',
      status: 'backlog',
      priority: 'high'
    });
    
    component.onSubmit();
    
    expect(component['store'].dispatch).toHaveBeenCalledWith(
      jasmine.objectContaining({
        type: '[Tasks] Create Task',
        task: jasmine.objectContaining({
          title: 'New Task',
          description: 'New Description',
          status: 'backlog',
          priority: 'high'
        })
      })
    );
  });

  it('should dispatch updateTask action when form is valid in edit mode', () => {
    const editData: TaskDialogData = {
      mode: 'edit',
      task: mockTask
    };

    // Create new TestBed for this test
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [TaskDialogComponent, ReactiveFormsModule, MatDialogModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: editData },
        { provide: MatDialogRef, useValue: mockDialogRef },
        provideMockStore({
          initialState: {
            tasks: {
              tasks: [],
              loading: false,
              error: null
            }
          }
        })
      ]
    });
    
    fixture = TestBed.createComponent(TaskDialogComponent);
    component = fixture.componentInstance;
    
    const mockStore = TestBed.inject(Store);
    spyOn(mockStore, 'dispatch');
    component.ngOnInit();
    
    component.taskForm.patchValue({
      title: 'Updated Task',
      description: 'Updated Description'
    });
    
    component.onSubmit();
    
    expect(mockStore.dispatch).toHaveBeenCalledWith(
      jasmine.objectContaining({
        type: '[Tasks] Update Task',
        task: jasmine.objectContaining({
          id: '1',
          title: 'Updated Task',
          description: 'Updated Description'
        })
      })
    );
  });

  it('should not dispatch action when form is invalid', () => {
    spyOn(component['store'], 'dispatch');
    component.ngOnInit();
    
    // Form is invalid without title
    component.onSubmit();
    
    expect(component['store'].dispatch).not.toHaveBeenCalled();
  });

  it('should mark form as touched when submit is called with invalid form', () => {
    component.ngOnInit();
    
    // Make form invalid by clearing title
    component.taskForm.patchValue({ title: '' });
    
    spyOn(component as any, 'markFormGroupTouched');
    
    component.onSubmit();
    
    expect(component['markFormGroupTouched']).toHaveBeenCalledWith(component.taskForm);
  });

  it('should close dialog with false when cancel is called', () => {
    component.onCancel();
    
    expect(mockDialogRef.close).toHaveBeenCalledWith();
  });

  it('should return correct dialog title for create mode', () => {
    expect(component.getDialogTitle()).toBe('Create New Task');
  });

  it('should return correct dialog title for edit mode', () => {
    const editData: TaskDialogData = {
      mode: 'edit',
      task: mockTask
    };

    // Create new TestBed for this test
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [TaskDialogComponent, ReactiveFormsModule, MatDialogModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: editData },
        { provide: MatDialogRef, useValue: mockDialogRef },
        provideMockStore({
          initialState: {
            tasks: {
              tasks: [],
              loading: false,
              error: null
            }
          }
        })
      ]
    });
    
    fixture = TestBed.createComponent(TaskDialogComponent);
    component = fixture.componentInstance;
    
    expect(component.getDialogTitle()).toBe('Edit Task');
  });

  it('should return correct submit button text for create mode', () => {
    expect(component.getSubmitButtonText()).toBe('Create');
  });

  it('should return correct submit button text for edit mode', () => {
    const editData: TaskDialogData = {
      mode: 'edit',
      task: mockTask
    };

    // Create new TestBed for this test
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [TaskDialogComponent, ReactiveFormsModule, MatDialogModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: editData },
        { provide: MatDialogRef, useValue: mockDialogRef },
        provideMockStore({
          initialState: {
            tasks: {
              tasks: [],
              loading: false,
              error: null
            }
          }
        })
      ]
    });
    
    fixture = TestBed.createComponent(TaskDialogComponent);
    component = fixture.componentInstance;
    
    expect(component.getSubmitButtonText()).toBe('Update');
  });

  it('should have correct form structure', () => {
    component.ngOnInit();
    expect(component.taskForm).toBeTruthy();
    expect(component.taskForm.get('title')).toBeTruthy();
    expect(component.taskForm.get('description')).toBeTruthy();
    expect(component.taskForm.get('status')).toBeTruthy();
    expect(component.taskForm.get('priority')).toBeTruthy();
    expect(component.taskForm.get('assigneeId')).toBeTruthy();
  });
});
