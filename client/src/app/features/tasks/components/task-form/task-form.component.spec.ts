import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TaskFormComponent } from './task-form.component';
import { Task } from '@models';

describe('TaskFormComponent', () => {
  let component: TaskFormComponent;
  let fixture: ComponentFixture<TaskFormComponent>;

  const mockTask: Task = {
    id: '1',
    title: 'Test Task',
    description: 'Test Description',
    status: 'in-progress',
    priority: 'high',
    projectId: 'p1',
    creatorId: 'u1',
    assigneeId: 'u2',
    dueDate: new Date('2024-12-31'),
    timeSpent: 5,
    labels: [{ name: 'Feature', color: '#3f51b5' }],
    subtasks: [],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockProjects = [
    { id: 'p1', name: 'Project 1' },
    { id: 'p2', name: 'Project 2' }
  ];

  const mockUsers = [
    { id: 'u1', name: 'User 1' },
    { id: 'u2', name: 'User 2' }
  ];

  const mockLabels = [
    { name: 'Feature', color: '#3f51b5' },
    { name: 'Bug', color: '#f44336' },
    { name: 'Enhancement', color: '#4caf50' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TaskFormComponent,
        ReactiveFormsModule,
        NoopAnimationsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskFormComponent);
    component = fixture.componentInstance;
    component.projects = mockProjects;
    component.users = mockUsers;
    component.availableLabels = mockLabels;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    expect(component.taskForm.get('title')?.value).toBe('');
    expect(component.taskForm.get('status')?.value).toBe('backlog');
    expect(component.taskForm.get('priority')?.value).toBe('medium');
    expect(component.taskForm.get('projectId')?.value).toBe('');
  });

  it('should initialize form with task values when editing', () => {
    component.task = mockTask;
    component.ngOnInit();
    fixture.detectChanges();

    expect(component.taskForm.get('title')?.value).toBe('Test Task');
    expect(component.taskForm.get('status')?.value).toBe('in-progress');
    expect(component.taskForm.get('priority')?.value).toBe('high');
    expect(component.taskForm.get('projectId')?.value).toBe('p1');
  });

  it('should validate required fields', () => {
    const titleControl = component.taskForm.get('title');
    const projectControl = component.taskForm.get('projectId');

    titleControl?.setValue('');
    projectControl?.setValue('');

    expect(titleControl?.hasError('required')).toBeTruthy();
    expect(projectControl?.hasError('required')).toBeTruthy();
  });

  it('should validate title length', () => {
    const titleControl = component.taskForm.get('title');

    titleControl?.setValue('ab');
    expect(titleControl?.hasError('minlength')).toBeTruthy();

    titleControl?.setValue('a'.repeat(101));
    expect(titleControl?.hasError('maxlength')).toBeTruthy();
  });

  it('should validate estimated hours range', () => {
    const hoursControl = component.taskForm.get('estimatedHours');

    hoursControl?.setValue(-1);
    expect(hoursControl?.hasError('min')).toBeTruthy();

    hoursControl?.setValue(1001);
    expect(hoursControl?.hasError('max')).toBeTruthy();
  });

  it('should validate due date is not in the past', () => {
    const dueDateControl = component.taskForm.get('dueDate');
    const pastDate = new Date('2020-01-01');

    dueDateControl?.setValue(pastDate);
    component.taskForm.updateValueAndValidity();

    expect(component.taskForm.hasError('pastDueDate')).toBeTruthy();
  });

  it('should add label to form', () => {
    const label = { name: 'New Label', color: '#ff9800' };
    component.addLabel(label);

    const labels = component.taskForm.get('labels')?.value;
    expect(labels).toContain(label);
  });

  it('should not add duplicate labels', () => {
    const label = { name: 'Feature', color: '#3f51b5' };
    component.addLabel(label);
    component.addLabel(label);

    const labels = component.taskForm.get('labels')?.value;
    const featureLabels = labels.filter((l: any) => l.name === 'Feature');
    expect(featureLabels).toHaveLength(1);
  });

  it('should remove label from form', () => {
    component.addLabel({ name: 'Test Label', color: '#ff0000' });
    component.removeLabel('Test Label');

    const labels = component.taskForm.get('labels')?.value;
    expect(labels.find((l: any) => l.name === 'Test Label')).toBeUndefined();
  });

  it('should emit save event with form data when valid', () => {
    spyOn(component.save, 'emit');
    
    component.taskForm.patchValue({
      title: 'Valid Task',
      description: 'Valid Description',
      status: 'in-progress',
      priority: 'high',
      projectId: 'p1'
    });

    component.onSubmit();

    expect(component.save.emit).toHaveBeenCalledWith({
      title: 'Valid Task',
      description: 'Valid Description',
      status: 'in-progress',
      priority: 'high',
      projectId: 'p1',
      assigneeId: '',
      dueDate: null,
      estimatedHours: null,
      labels: []
    });
  });

  it('should not emit save event when form is invalid', () => {
    spyOn(component.save, 'emit');
    
    component.taskForm.patchValue({
      title: '', // Invalid - required
      projectId: '' // Invalid - required
    });

    component.onSubmit();

    expect(component.save.emit).not.toHaveBeenCalled();
  });

  it('should emit cancel event', () => {
    spyOn(component.cancel, 'emit');
    
    component.onCancel();

    expect(component.cancel.emit).toHaveBeenCalled();
  });

  it('should return correct error messages', () => {
    const titleControl = component.taskForm.get('title');
    titleControl?.setValue('');

    expect(component.getErrorMessage('title')).toBe('Title is required');
  });

  it('should return form validity status', () => {
    expect(component.isFormValid).toBeFalsy();

    component.taskForm.patchValue({
      title: 'Valid Task',
      projectId: 'p1'
    });

    expect(component.isFormValid).toBeTruthy();
  });

  it('should return form dirty status', () => {
    expect(component.isFormDirty).toBeFalsy();

    component.taskForm.patchValue({
      title: 'Changed Title'
    });

    expect(component.isFormDirty).toBeTruthy();
  });
});
