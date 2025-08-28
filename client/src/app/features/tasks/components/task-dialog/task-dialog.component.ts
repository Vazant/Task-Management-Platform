import { ChangeDetectionStrategy, Component, Inject, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Store } from '@ngrx/store';
import { Observable, map, startWith } from 'rxjs';
import { Task, TaskStatus, TaskPriority } from '@models';
import * as TaskActions from '../../store/tasks.actions';

export interface TaskDialogData {
  task?: Task;
  mode: 'create' | 'edit';
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
    MatIconModule,
    MatChipsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatAutocompleteModule,
    MatSlideToggleModule
  ],
  templateUrl: './task-dialog.component.html',
  styleUrls: ['./task-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskDialogComponent implements OnInit {
  taskForm!: FormGroup;
  loading = false;
  error: string | null = null;

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

  filteredAssignees$!: Observable<string[]>;

  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);
  private readonly dialogRef = inject(MatDialogRef<TaskDialogComponent>);
  private readonly data = inject(MAT_DIALOG_DATA);

  ngOnInit(): void {
    this.initForm();
    this.setupAssigneeFilter();
    
    if (this.data.mode === 'edit' && this.data.task) {
      this.patchForm(this.data.task);
    }
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      this.loading = true;
      this.error = null;

      const formValue = this.taskForm.value;
      const taskData: Partial<Task> = {
        title: formValue.title,
        description: formValue.description,
        status: formValue.status,
        priority: formValue.priority,
        assignee: formValue.assignee,
        dueDate: formValue.dueDate ? formValue.dueDate.toISOString().split('T')[0] : null,
        estimatedHours: formValue.estimatedHours,
        labels: formValue.labels || [],
        project: formValue.project || 'default'
      };

      if (this.data.mode === 'create') {
        this.store.dispatch(TaskActions.createTask({ task: taskData }));
      } else if (this.data.task) {
        this.store.dispatch(TaskActions.updateTask({
          id: this.data.task.id,
          ...taskData
        }));
      }

      // Подписываемся на результат операции
      this.store.select(state => state.tasks).subscribe(tasksState => {
        if (!tasksState.loading && this.loading) {
          this.loading = false;
          
          if (tasksState.error) {
            this.error = tasksState.error;
          } else {
            this.dialogRef.close(true);
          }
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
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

  getDialogTitle(): string {
    return this.data.mode === 'create' ? 'Create New Task' : 'Edit Task';
  }

  getSubmitButtonText(): string {
    return this.data.mode === 'create' ? 'Create' : 'Update';
  }

  private initForm(): void {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(1000)]],
      status: [this.data.initialStatus || 'backlog', Validators.required],
      priority: ['medium', Validators.required],
      assignee: [''],
      dueDate: [null],
      estimatedHours: [null, [Validators.min(0.5), Validators.max(100)]],
      labels: [[]],
      project: ['default']
    });
  }

  private setupAssigneeFilter(): void {
    this.filteredAssignees$ = this.taskForm.get('assignee')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterAssignees(value))
    );
  }

  private filterAssignees(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.assigneeOptions.filter(assignee => 
      assignee.toLowerCase().includes(filterValue)
    );
  }

  private patchForm(task: Task): void {
    this.taskForm.patchValue({
      title: task.title,
      description: task.description,
      status: task.status,
      priority: task.priority,
      assignee: task.assignee,
      dueDate: task.dueDate ? new Date(task.dueDate) : null,
      estimatedHours: task.estimatedHours,
      labels: task.labels || [],
      project: task.project
    });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.taskForm.controls).forEach(key => {
      const control = this.taskForm.get(key);
      control?.markAsTouched();
    });
  }

  private getRandomColor(): string {
    const colors = ['#f44336', '#e91e63', '#9c27b0', '#673ab7', '#3f51b5', '#2196f3', '#03a9f4', '#00bcd4', '#009688', '#4caf50', '#8bc34a', '#cddc39', '#ffeb3b', '#ffc107', '#ff9800', '#ff5722'];
    return colors[Math.floor(Math.random() * colors.length)];
  }
}
