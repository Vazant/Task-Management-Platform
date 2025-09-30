import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Project, UpdateProjectRequest, ProjectPriority, ProjectStatus } from '../../../../core/models/project.model';
import { updateProject } from '../../store';
import { selectProjectsLoading } from '../../store/projects.selectors';

@Component({
  selector: 'app-project-edit-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatChipsModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './project-edit-dialog.component.html',
  styleUrls: ['./project-edit-dialog.component.scss']
})
export class ProjectEditDialogComponent implements OnInit, OnDestroy {
  projectForm: FormGroup;
  loading = false;
  project: Project;
  
  priorities = Object.values(ProjectPriority);
  statuses = Object.values(ProjectStatus);
  
  priorityLabels = {
    [ProjectPriority.LOW]: 'Низкий',
    [ProjectPriority.MEDIUM]: 'Средний',
    [ProjectPriority.HIGH]: 'Высокий',
    [ProjectPriority.URGENT]: 'Срочный'
  };
  
  statusLabels = {
    [ProjectStatus.ACTIVE]: 'Активный',
    [ProjectStatus.COMPLETED]: 'Завершен',
    [ProjectStatus.ARCHIVED]: 'Архивный',
    [ProjectStatus.ON_HOLD]: 'Приостановлен'
  };
  
  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialogRef: MatDialogRef<ProjectEditDialogComponent>,
    private readonly store: Store,
    @Inject(MAT_DIALOG_DATA) public data: { project: Project }
  ) {
    this.project = data.project;
    this.projectForm = this.createForm();
  }

  ngOnInit(): void {
    this.populateForm();
    
    this.store.select(selectProjectsLoading)
      .pipe(takeUntil(this.destroy$))
      .subscribe(loading => {
        this.loading = loading;
        if (loading) {
          this.projectForm.disable();
        } else {
          this.projectForm.enable();
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      status: [ProjectStatus.ACTIVE, Validators.required],
      priority: [ProjectPriority.MEDIUM, Validators.required],
      startDate: [null],
      endDate: [null],
      tags: [[]],
      color: ['#1976d2']
    });
  }

  private populateForm(): void {
    this.projectForm.patchValue({
      name: this.project.name,
      description: this.project.description || '',
      status: this.project.status,
      priority: this.project.priority,
      startDate: this.project.startDate ? new Date(this.project.startDate) : null,
      endDate: this.project.endDate ? new Date(this.project.endDate) : null,
      tags: this.project.tags || [],
      color: this.project.color || '#1976d2'
    });
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      const formValue = this.projectForm.value;
      
      const request: UpdateProjectRequest = {
        id: this.project.id,
        name: formValue.name,
        description: formValue.description || undefined,
        status: formValue.status,
        priority: formValue.priority,
        startDate: formValue.startDate ? new Date(formValue.startDate) : undefined,
        endDate: formValue.endDate ? new Date(formValue.endDate) : undefined,
        tags: formValue.tags || [],
        color: formValue.color
      };

      this.store.dispatch(updateProject({ id: this.project.id, request }));
      
      // Close dialog after successful update
      this.store.select(selectProjectsLoading)
        .pipe(takeUntil(this.destroy$))
        .subscribe(loading => {
          if (!loading) {
            this.dialogRef.close(true);
          }
        });
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

  private markFormGroupTouched(): void {
    Object.keys(this.projectForm.controls).forEach(key => {
      const control = this.projectForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string {
    const control = this.projectForm.get(fieldName);
    if (control?.errors && control.touched) {
      if (control.errors['required']) {
        return 'Это поле обязательно';
      }
      if (control.errors['minlength']) {
        return `Минимум ${control.errors['minlength'].requiredLength} символов`;
      }
      if (control.errors['maxlength']) {
        return `Максимум ${control.errors['maxlength'].requiredLength} символов`;
      }
    }
    return '';
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.projectForm.get(fieldName);
    return !!(control?.invalid && control.touched);
  }

  addTag(event: any): void {
    const value = event.value?.trim();
    if (value) {
      const tags = this.projectForm.get('tags')?.value || [];
      if (!tags.includes(value)) {
        this.projectForm.patchValue({
          tags: [...tags, value]
        });
      }
      event.chipInput.clear();
    }
  }

  removeTag(index: number): void {
    const tags = this.projectForm.get('tags')?.value || [];
    tags.splice(index, 1);
    this.projectForm.patchValue({ tags });
  }

  separatorKeysCodes = [13, 188]; // Enter and comma
}
