import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Task, TaskStatus, TaskPriority } from '@models';

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
    MatChipsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatAutocompleteModule
  ],
  templateUrl: './task-inline-edit.component.html',
  styleUrls: ['./task-inline-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskInlineEditComponent {
  @Input() task!: Task;
  @Output() save = new EventEmitter<Partial<Task>>();
  @Output() cancel = new EventEmitter<void>();

  taskForm!: FormGroup;
  isEditing = false;

  readonly statusOptions: { value: TaskStatus; label: string }[] = [
    { value: 'backlog', label: 'Backlog' },
    { value: 'in-progress', label: 'In Progress' },
    { value: 'done', label: 'Done' },
    { value: 'blocked', label: 'Blocked' }
  ];

  readonly priorityOptions: { value: TaskPriority; label: string; color: string }[] = [
    { value: 'low', label: 'Low', color: 'accent' },
    { value: 'medium', label: 'Medium', color: 'primary' },
    { value: 'high', label: 'High', color: 'warn' },
    { value: 'urgent', label: 'Urgent', color: 'warn' }
  ];

  readonly assigneeOptions = [
    'john.doe',
    'jane.smith',
    'bob.wilson',
    'alice.johnson'
  ];

  private readonly fb = inject(FormBuilder);

  startEdit(): void {
    this.isEditing = true;
    this.initForm();
  }

  onSave(): void {
    if (this.taskForm.valid) {
      const formValue = this.taskForm.value;
      const updatedTask: Partial<Task> = {
        title: formValue.title,
        description: formValue.description,
        status: formValue.status,
        priority: formValue.priority,
        assignee: formValue.assignee,
        dueDate: formValue.dueDate ? formValue.dueDate.toISOString().split('T')[0] : null,
        estimatedHours: formValue.estimatedHours,
        labels: formValue.labels || [],
        updatedAt: new Date().toISOString()
      };

      this.save.emit(updatedTask);
      this.isEditing = false;
    }
  }

  onCancel(): void {
    this.isEditing = false;
    this.cancel.emit();
  }

  addLabel(event: any): void {
    const value = (event.value || '').trim();
    if (value) {
      const labels = this.taskForm.get('labels')?.value || [];
      const newLabel = { name: value, color: this.getRandomColor() };
      this.taskForm.patchValue({ labels: [...labels, newLabel] });
      event.chipInput?.clear();
    }
  }

  removeLabel(label: any): void {
    const labels = this.taskForm.get('labels')?.value || [];
    const index = labels.findIndex((l: any) => l.name === label.name);
    if (index >= 0) {
      labels.splice(index, 1);
      this.taskForm.patchValue({ labels: [...labels] });
    }
  }

  private initForm(): void {
    this.taskForm = this.fb.group({
      title: [this.task.title, [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: [this.task.description, [Validators.maxLength(1000)]],
      status: [this.task.status, Validators.required],
      priority: [this.task.priority, Validators.required],
      assignee: [this.task.assignee],
      dueDate: [this.task.dueDate ? new Date(this.task.dueDate) : null],
      estimatedHours: [this.task.estimatedHours, [Validators.min(0.5), Validators.max(100)]],
      labels: [this.task.labels || []]
    });
  }

  private getRandomColor(): string {
    const colors = ['#f44336', '#e91e63', '#9c27b0', '#673ab7', '#3f51b5', '#2196f3', '#03a9f4', '#00bcd4', '#009688', '#4caf50', '#8bc34a', '#cddc39', '#ffeb3b', '#ffc107', '#ff9800', '#ff5722'];
    return colors[Math.floor(Math.random() * colors.length)];
  }
}
