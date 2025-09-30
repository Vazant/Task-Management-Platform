import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CreateProjectRequest, ProjectPriority } from '../../core/models/project.model';
import { createProject } from '../../store';
import { selectProjectsLoading } from '../../store/projects.selectors';

@Component({
  selector: 'app-project-create-dialog',
  templateUrl: './project-create-dialog.component.html',
  styleUrls: ['./project-create-dialog.component.scss']
})
export class ProjectCreateDialogComponent implements OnInit, OnDestroy {
  projectForm: FormGroup;
  loading = false;
  
  priorities = Object.values(ProjectPriority);
  priorityLabels = {
    [ProjectPriority.LOW]: 'Низкий',
    [ProjectPriority.MEDIUM]: 'Средний',
    [ProjectPriority.HIGH]: 'Высокий',
    [ProjectPriority.URGENT]: 'Срочный'
  };
  
  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialogRef: MatDialogRef<ProjectCreateDialogComponent>,
    private readonly store: Store
  ) {
    this.projectForm = this.createForm();
  }

  ngOnInit(): void {
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
      priority: [ProjectPriority.MEDIUM, Validators.required],
      startDate: [null],
      endDate: [null],
      tags: [[]],
      color: ['#1976d2']
    });
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      const formValue = this.projectForm.value;
      
      const request: CreateProjectRequest = {
        name: formValue.name,
        description: formValue.description || undefined,
        priority: formValue.priority,
        startDate: formValue.startDate ? new Date(formValue.startDate) : undefined,
        endDate: formValue.endDate ? new Date(formValue.endDate) : undefined,
        tags: formValue.tags || [],
        color: formValue.color
      };

      this.store.dispatch(createProject({ request }));
      
      // Close dialog after successful creation
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
