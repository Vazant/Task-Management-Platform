import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
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
    assignee: 'john.doe',
    project: 'test-project',
    dueDate: '2025-08-15',
    estimatedHours: 4,
    labels: [{ name: 'bug', color: '#f44336' }],
    createdAt: '2025-08-01T10:00:00Z',
    updatedAt: '2025-08-01T10:00:00Z'
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

    TestBed.overrideProvider(MAT_DIALOG_DATA, { useValue: editData });
    fixture = TestBed.createComponent(TaskDialogComponent);
    component = fixture.componentInstance;
    
    component.ngOnInit();
    
    expect(component.taskForm.get('title')?.value).toBe('Test Task');
    expect(component.taskForm.get('description')?.value).toBe('Test Description');
    expect(component.taskForm.get('status')?.value).toBe('in-progress');
    expect(component.taskForm.get('priority')?.value).toBe('medium');
    expect(component.taskForm.get('assignee')?.value).toBe('john.doe');
  });

  it('should validate required fields', () => {
    component.ngOnInit();
    
    const titleControl = component.taskForm.get('title');
    const statusControl = component.taskForm.get('status');
    
    expect(titleControl?.valid).toBeFalsy();
    expect(statusControl?.valid).toBeTruthy();
    
    titleControl?.setValue('Test Task');
    expect(titleControl?.valid).toBeTruthy();
  });

  it('should validate title length', () => {
    component.ngOnInit();
    
    const titleControl = component.taskForm.get('title');
    
    titleControl?.setValue('ab'); // Too short
    expect(titleControl?.hasError('minlength')).toBeTruthy();
    
    titleControl?.setValue('a'.repeat(101)); // Too long
    expect(titleControl?.hasError('maxlength')).toBeTruthy();
    
    titleControl?.setValue('Valid Title');
    expect(titleControl?.valid).toBeTruthy();
  });

  it('should validate estimated hours', () => {
    component.ngOnInit();
    
    const hoursControl = component.taskForm.get('estimatedHours');
    
    hoursControl?.setValue(0.3); // Too low
    expect(hoursControl?.hasError('min')).toBeTruthy();
    
    hoursControl?.setValue(150); // Too high
    expect(hoursControl?.hasError('max')).toBeTruthy();
    
    hoursControl?.setValue(4);
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
    expect(labels).toContain(jasmine.objectContaining({
      name: 'new-label',
      color: jasmine.any(String)
    }));
    expect(event.chipInput.clear).toHaveBeenCalled();
  });

  it('should remove label when removeLabel is called', () => {
    component.ngOnInit();
    
    const label = { name: 'test-label', color: '#f44336' };
    component.taskForm.patchValue({ labels: [label] });
    
    component.removeLabel(label);
    
    const labels = component.taskForm.get('labels')?.value;
    expect(labels).not.toContain(label);
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

    TestBed.overrideProvider(MAT_DIALOG_DATA, { useValue: editData });
    fixture = TestBed.createComponent(TaskDialogComponent);
    component = fixture.componentInstance;
    
    spyOn(component['store'], 'dispatch');
    component.ngOnInit();
    
    component.taskForm.patchValue({
      title: 'Updated Task',
      description: 'Updated Description'
    });
    
    component.onSubmit();
    
    expect(component['store'].dispatch).toHaveBeenCalledWith(
      jasmine.objectContaining({
        type: '[Tasks] Update Task',
        id: '1',
        title: 'Updated Task',
        description: 'Updated Description'
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
    
    const titleControl = component.taskForm.get('title');
    spyOn(titleControl!, 'markAsTouched');
    
    component.onSubmit();
    
    expect(titleControl?.markAsTouched).toHaveBeenCalled();
  });

  it('should close dialog with false when cancel is called', () => {
    component.onCancel();
    
    expect(mockDialogRef.close).toHaveBeenCalledWith(false);
  });

  it('should return correct dialog title for create mode', () => {
    expect(component.getDialogTitle()).toBe('Create New Task');
  });

  it('should return correct dialog title for edit mode', () => {
    const editData: TaskDialogData = {
      mode: 'edit',
      task: mockTask
    };

    TestBed.overrideProvider(MAT_DIALOG_DATA, { useValue: editData });
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

    TestBed.overrideProvider(MAT_DIALOG_DATA, { useValue: editData });
    fixture = TestBed.createComponent(TaskDialogComponent);
    component = fixture.componentInstance;
    
    expect(component.getSubmitButtonText()).toBe('Update');
  });

  it('should have correct status options', () => {
    expect(component.statusOptions).toEqual([
      { value: 'backlog', label: 'Backlog' },
      { value: 'in-progress', label: 'In Progress' },
      { value: 'done', label: 'Done' },
      { value: 'blocked', label: 'Blocked' }
    ]);
  });

  it('should have correct priority options', () => {
    expect(component.priorityOptions).toEqual([
      { value: 'low', label: 'Low', color: 'accent' },
      { value: 'medium', label: 'Medium', color: 'primary' },
      { value: 'high', label: 'High', color: 'warn' },
      { value: 'urgent', label: 'Urgent', color: 'warn' }
    ]);
  });

  it('should have correct assignee options', () => {
    expect(component.assigneeOptions).toEqual([
      'john.doe',
      'jane.smith',
      'bob.wilson',
      'alice.johnson'
    ]);
  });
});
