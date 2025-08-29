import { Component, Input, Output, EventEmitter, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

import { Task, TaskStatus } from '@models';

@Component({
  selector: 'app-task-inline-edit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  template: `
    <div class="inline-edit-container">
      <div *ngIf="!isEditing" class="display-mode">
        <div class="task-title">{{ task.title }}</div>
        <div class="task-description" *ngIf="task.description">{{ task.description }}</div>
        <div class="task-meta">
          <span class="status-badge" [class]="'status-' + task.status">
            {{ getStatusLabel(task.status) }}
          </span>
          <span class="priority-badge" [class]="'priority-' + task.priority">
            {{ getPriorityLabel(task.priority) }}
          </span>
          <span class="assignee" *ngIf="task.assigneeId">{{ task.assigneeId }}</span>
          <span class="due-date" *ngIf="task.dueDate">
            {{ task.dueDate | date:'shortDate' }}
          </span>
        </div>
        <button mat-icon-button (click)="startEdit()" class="edit-button">
          <mat-icon>edit</mat-icon>
        </button>
      </div>

      <form *ngIf="isEditing" [formGroup]="editForm" (ngSubmit)="saveChanges()" class="edit-mode">
        <div class="form-row">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Название</mat-label>
            <input matInput formControlName="title" placeholder="Введите название">
            <mat-error *ngIf="editForm.get('title')?.hasError('required')">
              Название обязательно
            </mat-error>
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Описание</mat-label>
            <textarea matInput formControlName="description" rows="2" placeholder="Введите описание"></textarea>
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
            <input matInput formControlName="assigneeId" placeholder="Введите исполнителя">
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Срок</mat-label>
            <input matInput [matDatepicker]="picker" formControlName="dueDate">
            <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            <mat-datepicker #picker></mat-datepicker>
          </mat-form-field>
        </div>

        <div class="form-actions">
          <button mat-button type="button" (click)="cancelEdit()">Отмена</button>
          <button mat-raised-button color="primary" type="submit" [disabled]="editForm.invalid">
            Сохранить
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .inline-edit-container {
      padding: 16px;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      background: white;
    }

    .display-mode {
      position: relative;
    }

    .task-title {
      font-weight: 600;
      font-size: 16px;
      margin-bottom: 8px;
    }

    .task-description {
      color: #666;
      margin-bottom: 12px;
      line-height: 1.4;
    }

    .task-meta {
      display: flex;
      gap: 8px;
      flex-wrap: wrap;
      align-items: center;
    }

    .status-badge, .priority-badge {
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
    }

    .status-backlog { background: #f0f0f0; color: #666; }
    .status-in-progress { background: #e3f2fd; color: #1976d2; }
    .status-done { background: #e8f5e8; color: #388e3c; }
    .status-blocked { background: #ffebee; color: #d32f2f; }

    .priority-low { background: #f3e5f5; color: #7b1fa2; }
    .priority-medium { background: #fff3e0; color: #f57c00; }
    .priority-high { background: #ffebee; color: #d32f2f; }
    .priority-urgent { background: #d32f2f; color: white; }

    .assignee, .due-date {
      font-size: 12px;
      color: #666;
    }

    .edit-button {
      position: absolute;
      top: 0;
      right: 0;
    }

    .edit-mode {
      padding: 16px 0;
    }

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

    .form-actions {
      display: flex;
      gap: 8px;
      justify-content: flex-end;
      margin-top: 16px;
    }
  `]
})
export class TaskInlineEditComponent implements OnInit {
  @Input() task!: Task;
  @Output() taskUpdated = new EventEmitter<Partial<Task>>();

  private readonly fb = inject(FormBuilder);

  editForm!: FormGroup;
  isEditing = false;

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.editForm = this.fb.group({
      title: [this.task.title, [Validators.required, Validators.minLength(3)]],
      description: [this.task.description || ''],
      status: [this.task.status, Validators.required],
      priority: [this.task.priority, Validators.required],
      assigneeId: [this.task.assigneeId || ''],
      dueDate: [this.task.dueDate || null],
      estimatedHours: [this.task.estimatedHours || null]
    });
  }

  startEdit(): void {
    this.isEditing = true;
    this.patchForm();
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.patchForm();
  }

  saveChanges(): void {
    if (this.editForm.valid) {
      const formValue = this.editForm.value;
      
      const updatedTask: Partial<Task> = {
        id: this.task.id,
        title: formValue.title,
        description: formValue.description,
        status: formValue.status,
        priority: formValue.priority,
        assigneeId: formValue.assigneeId,
        dueDate: formValue.dueDate,
        estimatedHours: formValue.estimatedHours,
        updatedAt: new Date()
      };

      this.taskUpdated.emit(updatedTask);
      this.isEditing = false;
    }
  }

  private patchForm(): void {
    this.editForm.patchValue({
      title: this.task.title,
      description: this.task.description,
      status: this.task.status,
      priority: this.task.priority,
      assigneeId: this.task.assigneeId,
      dueDate: this.task.dueDate,
      estimatedHours: this.task.estimatedHours
    });
  }

  getStatusLabel(status: TaskStatus): string {
    const labels: Record<TaskStatus, string> = {
      'backlog': 'Backlog',
      'in-progress': 'В работе',
      'done': 'Завершено',
      'blocked': 'Заблокировано'
    };
    return labels[status];
  }

  getPriorityLabel(priority: string): string {
    const labels: Record<string, string> = {
      'low': 'Низкий',
      'medium': 'Средний',
      'high': 'Высокий',
      'urgent': 'Срочный'
    };
    return labels[priority] || priority;
  }
}
