import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Store } from '@ngrx/store';
import { Subject, takeUntil } from 'rxjs';

import { Task, TaskStatus, TaskPriority } from '@models';
import * as TaskActions from '../../store/tasks.actions';

export interface TaskDialogData {
  mode: 'create' | 'edit';
  task?: Task;
  initialStatus?: TaskStatus;
}

@Component({
  selector: 'app-task-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatAutocompleteModule
  ],
  template: `
    <h2 mat-dialog-title>{{ getDialogTitle() }}</h2>
    <form [formGroup]="taskForm" (ngSubmit)="onSubmit()">
      <mat-dialog-content>
        <div class="form-row">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Название задачи</mat-label>
            <input matInput formControlName="title" placeholder="Введите название задачи">
            <mat-error *ngIf="taskForm.get('title')?.hasError('required')">
              Название обязательно
            </mat-error>
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Описание</mat-label>
            <textarea matInput formControlName="description" rows="3" placeholder="Введите описание задачи"></textarea>
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline">
            <mat-label>Статус</mat-label>
            <mat-select formControlName="status">
              <mat-option value="backlog">Backlog</mat-option>
              <mat-option value="in-progress">В работе</mat-option>
              <mat-option value="done">Завершено</mat-option>
              <mat-option value="blocked">Заблокировано</mat-option>
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Приоритет</mat-label>
            <mat-select formControlName="priority">
              <mat-option value="low">Низкий</mat-option>
              <mat-option value="medium">Средний</mat-option>
              <mat-option value="high">Высокий</mat-option>
              <mat-option value="urgent">Срочный</mat-option>
            </mat-select>
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline">
            <mat-label>Исполнитель</mat-label>
            <input matInput formControlName="assigneeId" placeholder="Введите имя исполнителя">
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Срок выполнения</mat-label>
            <input matInput [matDatepicker]="picker" formControlName="dueDate">
            <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            <mat-datepicker #picker></mat-datepicker>
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Метки</mat-label>
            <mat-chip-grid #chipGrid>
              <mat-chip-row *ngFor="let label of selectedLabels" 
                           (removed)="removeLabel(label)">
                {{ label }}
                <button matChipRemove>
                  <mat-icon>cancel</mat-icon>
                </button>
              </mat-chip-row>
            </mat-chip-grid>
            <input placeholder="Добавить метку..."
                   [matChipInputFor]="chipGrid"
                   (matChipInputTokenEnd)="addLabel($event)">
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline">
            <mat-label>Оценка времени (часы)</mat-label>
            <input matInput type="number" formControlName="estimatedHours" min="0">
          </mat-form-field>
        </div>
      </mat-dialog-content>

      <mat-dialog-actions align="end">
        <button mat-button type="button" (click)="onCancel()">Отмена</button>
        <button mat-raised-button color="primary" type="submit" [disabled]="taskForm.invalid">
          {{ getSubmitButtonText() }}
        </button>
      </mat-dialog-actions>
    </form>
  `,
  styles: [`
    .form-row {
      display: flex;
      gap: 16px;
      margin-bottom: 16px;
    }

    .full-width {
      width: 100%;
    }

    mat-form-field {
      flex: 1;
    }

    mat-dialog-content {
      min-width: 500px;
    }

    mat-dialog-actions {
      padding: 16px 0;
    }
  `]
})
export class TaskDialogComponent implements OnInit, OnDestroy {
  private readonly dialogRef = inject(MatDialogRef<TaskDialogComponent>);
  private readonly data: TaskDialogData = inject(MAT_DIALOG_DATA) as TaskDialogData;
  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);
  private readonly destroy$ = new Subject<void>();

  taskForm!: FormGroup;
  selectedLabels: string[] = [];

  ngOnInit(): void {
    this.initForm();
    
    if (this.data.mode === 'edit' && this.data.task) {
      this.patchForm(this.data.task);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      status: [this.data.initialStatus || 'backlog', Validators.required],
      priority: ['medium', Validators.required],
      assigneeId: [''],
      dueDate: [null],
      estimatedHours: [null, [Validators.min(0)]],
      labels: [[]]
    });

    this.taskForm.get('labels')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(labels => {
        this.selectedLabels = labels || [];
      });
  }

  private patchForm(task: Task): void {
    this.taskForm.patchValue({
      title: task.title,
      description: task.description,
      status: task.status,
      priority: task.priority,
      assigneeId: task.assigneeId,
      dueDate: task.dueDate,
      estimatedHours: task.estimatedHours,
      labels: task.labels
    });
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      const formValue = this.taskForm.value;
      
      const taskData: Partial<Task> = {
        title: formValue.title,
        description: formValue.description,
        status: formValue.status,
        priority: formValue.priority,
        assigneeId: formValue.assigneeId,
        dueDate: formValue.dueDate,
        estimatedHours: formValue.estimatedHours,
        labels: formValue.labels,
        projectId: '1', // TODO: Get from current project
        creatorId: 'user1', // TODO: Get from auth service
        timeSpent: 0,
        subtasks: []
      };

      if (this.data.mode === 'create') {
        this.store.dispatch(TaskActions.createTask({ task: taskData }));
      } else {
        this.store.dispatch(TaskActions.updateTask({
          task: {
            id: this.data.task!.id,
            ...taskData,
            updatedAt: new Date()
          }
        }));
      }

      this.dialogRef.close();
    } else {
      // Mark all form controls as touched to show validation errors
      this.markFormGroupTouched(this.taskForm);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  addLabel(event: any): void {
    const value = (event.value || '').trim();
    if (value && !this.selectedLabels.includes(value)) {
      this.selectedLabels.push(value);
      this.taskForm.patchValue({ labels: this.selectedLabels });
    }
    event.chipInput!.clear();
  }

  removeLabel(label: string): void {
    const index = this.selectedLabels.indexOf(label);
    if (index >= 0) {
      this.selectedLabels.splice(index, 1);
      this.taskForm.patchValue({ labels: this.selectedLabels });
    }
  }

  getDialogTitle(): string {
    return this.data.mode === 'create' ? 'Create New Task' : 'Edit Task';
  }

  getSubmitButtonText(): string {
    return this.data.mode === 'create' ? 'Create' : 'Update';
  }
}
