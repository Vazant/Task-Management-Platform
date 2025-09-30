import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { of, Subject } from 'rxjs';
import { ProjectCreateDialogComponent } from './project-create-dialog.component';
import { ProjectPriority } from '../../../../core/models/project.model';
import * as ProjectsActions from '../../store/projects.actions';
import * as ProjectsSelectors from '../../store/projects.selectors';

describe('ProjectCreateDialogComponent', () => {
  let component: ProjectCreateDialogComponent;
  let fixture: ComponentFixture<ProjectCreateDialogComponent>;
  let mockStore: jasmine.SpyObj<Store>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<ProjectCreateDialogComponent>>;

  beforeEach(async () => {
    const storeSpy = jasmine.createSpyObj('Store', ['select', 'dispatch']);
    const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      declarations: [ProjectCreateDialogComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: Store, useValue: storeSpy },
        { provide: MatDialogRef, useValue: dialogRefSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectCreateDialogComponent);
    component = fixture.componentInstance;
    mockStore = TestBed.inject(Store) as jasmine.SpyObj<Store>;
    mockDialogRef = TestBed.inject(MatDialogRef) as jasmine.SpyObj<MatDialogRef<ProjectCreateDialogComponent>>;

    // Setup store selector
    mockStore.select.and.returnValue(of(false));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    expect(component.projectForm.get('name')?.value).toBe('');
    expect(component.projectForm.get('description')?.value).toBe('');
    expect(component.projectForm.get('priority')?.value).toBe(ProjectPriority.MEDIUM);
    expect(component.projectForm.get('color')?.value).toBe('#1976d2');
  });

  it('should validate required name field', () => {
    const nameControl = component.projectForm.get('name');
    nameControl?.setValue('');
    expect(nameControl?.hasError('required')).toBeTruthy();
  });

  it('should validate name minimum length', () => {
    const nameControl = component.projectForm.get('name');
    nameControl?.setValue('ab');
    expect(nameControl?.hasError('minlength')).toBeTruthy();
  });

  it('should validate name maximum length', () => {
    const nameControl = component.projectForm.get('name');
    nameControl?.setValue('a'.repeat(101));
    expect(nameControl?.hasError('maxlength')).toBeTruthy();
  });

  it('should validate description maximum length', () => {
    const descriptionControl = component.projectForm.get('description');
    descriptionControl?.setValue('a'.repeat(501));
    expect(descriptionControl?.hasError('maxlength')).toBeTruthy();
  });

  it('should submit form when valid', () => {
    component.projectForm.patchValue({
      name: 'Test Project',
      description: 'Test Description',
      priority: ProjectPriority.HIGH,
      color: '#ff5722'
    });

    component.onSubmit();

    expect(mockStore.dispatch).toHaveBeenCalledWith(
      jasmine.objectContaining({
        type: ProjectsActions.createProject.type
      })
    );
  });

  it('should not submit form when invalid', () => {
    component.projectForm.patchValue({
      name: '', // Invalid: required field
      description: 'Test Description'
    });

    component.onSubmit();

    expect(mockStore.dispatch).not.toHaveBeenCalled();
  });

  it('should mark form as touched when invalid on submit', () => {
    component.projectForm.patchValue({
      name: '' // Invalid
    });

    spyOn(component as any, 'markFormGroupTouched');
    component.onSubmit();

    expect((component as any).markFormGroupTouched).toHaveBeenCalled();
  });

  it('should close dialog on cancel', () => {
    component.onCancel();
    expect(mockDialogRef.close).toHaveBeenCalledWith(false);
  });

  it('should add tag', () => {
    const event = {
      value: 'new tag',
      chipInput: { clear: jasmine.createSpy() }
    };

    component.addTag(event);

    expect(component.projectForm.get('tags')?.value).toContain('new tag');
    expect(event.chipInput.clear).toHaveBeenCalled();
  });

  it('should not add duplicate tag', () => {
    component.projectForm.patchValue({ tags: ['existing'] });
    
    const event = {
      value: 'existing',
      chipInput: { clear: jasmine.createSpy() }
    };

    component.addTag(event);

    expect(component.projectForm.get('tags')?.value).toEqual(['existing']);
  });

  it('should remove tag', () => {
    component.projectForm.patchValue({ tags: ['tag1', 'tag2', 'tag3'] });
    
    component.removeTag(1);

    expect(component.projectForm.get('tags')?.value).toEqual(['tag1', 'tag3']);
  });

  it('should get field error for required field', () => {
    const nameControl = component.projectForm.get('name');
    nameControl?.setValue('');
    nameControl?.markAsTouched();

    const error = component.getFieldError('name');
    expect(error).toBe('Это поле обязательно');
  });

  it('should get field error for minlength', () => {
    const nameControl = component.projectForm.get('name');
    nameControl?.setValue('ab');
    nameControl?.markAsTouched();

    const error = component.getFieldError('name');
    expect(error).toBe('Минимум 3 символов');
  });

  it('should get field error for maxlength', () => {
    const nameControl = component.projectForm.get('name');
    nameControl?.setValue('a'.repeat(101));
    nameControl?.markAsTouched();

    const error = component.getFieldError('name');
    expect(error).toBe('Максимум 100 символов');
  });

  it('should check if field is invalid', () => {
    const nameControl = component.projectForm.get('name');
    nameControl?.setValue('');
    nameControl?.markAsTouched();

    expect(component.isFieldInvalid('name')).toBeTruthy();
  });

  it('should handle loading state', () => {
    mockStore.select.and.returnValue(of(true));
    component.ngOnInit();

    expect(component.loading).toBe(true);
    expect(component.projectForm.disabled).toBe(true);
  });

  it('should close dialog after successful creation', () => {
    const loadingSubject = new Subject<boolean>();
    mockStore.select.and.returnValue(loadingSubject);

    component.ngOnInit();
    component.onSubmit();

    // Simulate loading completion
    loadingSubject.next(false);

    expect(mockDialogRef.close).toHaveBeenCalledWith(true);
  });
});
