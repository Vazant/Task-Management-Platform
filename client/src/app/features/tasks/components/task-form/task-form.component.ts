import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Task, TaskStatus, TaskPriority } from '@models';

export interface TaskFormData {
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  projectId: string;
  assigneeId?: string;
  dueDate?: Date;
  estimatedHours?: number;
  labels: Array<{ name: string; color: string }>;
}

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatAutocompleteModule,
    MatSlideToggleModule
  ],
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskFormComponent implements OnInit {
  @Input() task?: Task;
  @Input() projects: Array<{ id: string; name: string }> = [];
  @Input() users: Array<{ id: string; name: string }> = [];
  @Input() availableLabels: Array<{ name: string; color: string }> = [];
  @Output() save = new EventEmitter<TaskFormData>();
  @Output() cancel = new EventEmitter<void>();

  taskForm!: FormGroup;
  isEditMode = false;

  statuses: TaskStatus[] = ['backlog', 'in-progress', 'done', 'blocked'];
  priorities: TaskPriority[] = ['low', 'medium', 'high', 'urgent'];

  constructor(private readonly fb: FormBuilder) {}

  ngOnInit(): void {
    this.isEditMode = !!this.task;
    this.initializeForm();
  }

  private initializeForm(): void {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(1000)]],
      status: [this.task?.status || 'backlog', Validators.required],
      priority: [this.task?.priority || 'medium', Validators.required],
      projectId: [this.task?.projectId || '', Validators.required],
      assigneeId: [this.task?.assigneeId || ''],
      dueDate: [this.task?.dueDate || null],
      estimatedHours: [this.task?.estimatedHours || null, [Validators.min(0), Validators.max(1000)]],
      labels: [this.task?.labels || []]
    });

    // Add custom validators
    this.taskForm.setValidators([this.dueDateValidator.bind(this)]);
  }

  private dueDateValidator(group: AbstractControl): { [key: string]: any } | null {
    const dueDate = group.get('dueDate')?.value;
    if (!dueDate) return null;

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const selectedDate = new Date(dueDate);
    selectedDate.setHours(0, 0, 0, 0);

    if (selectedDate < today) {
      return { pastDueDate: true };
    }

    return null;
  }

  getErrorMessage(controlName: string): string {
    const control = this.taskForm.get(controlName);
    if (!control?.errors) return '';

    if (control.hasError('required')) {
      return `${this.getFieldLabel(controlName)} is required`;
    }
    if (control.hasError('minlength')) {
      const requiredLength = control.errors['minlength'].requiredLength;
      return `${this.getFieldLabel(controlName)} must be at least ${requiredLength} characters`;
    }
    if (control.hasError('maxlength')) {
      const requiredLength = control.errors['maxlength'].requiredLength;
      return `${this.getFieldLabel(controlName)} must be no more than ${requiredLength} characters`;
    }
    if (control.hasError('min')) {
      return `${this.getFieldLabel(controlName)} must be at least ${control.errors['min'].min}`;
    }
    if (control.hasError('max')) {
      return `${this.getFieldLabel(controlName)} must be no more than ${control.errors['max'].max}`;
    }
    if (control.hasError('pastDueDate')) {
      return 'Due date cannot be in the past';
    }

    return `${this.getFieldLabel(controlName)} is invalid`;
  }

  private getFieldLabel(controlName: string): string {
    const labels: { [key: string]: string } = {
      title: 'Title',
      description: 'Description',
      status: 'Status',
      priority: 'Priority',
      projectId: 'Project',
      assigneeId: 'Assignee',
      dueDate: 'Due date',
      estimatedHours: 'Estimated hours'
    };
    return labels[controlName] || controlName;
  }

  addLabel(label: { name: string; color: string }): void {
    const currentLabels = this.taskForm.get('labels')?.value || [];
    if (!currentLabels.find((l: any) => l.name === label.name)) {
      this.taskForm.patchValue({
        labels: [...currentLabels, label]
      });
    }
  }

  removeLabel(labelName: string): void {
    const currentLabels = this.taskForm.get('labels')?.value || [];
    this.taskForm.patchValue({
      labels: currentLabels.filter((l: any) => l.name !== labelName)
    });
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      const formData: TaskFormData = this.taskForm.value;
      this.save.emit(formData);
    } else {
      this.markFormGroupTouched(this.taskForm);
    }
  }

  onCancel(): void {
    this.cancel.emit();
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

  get isFormValid(): boolean {
    return this.taskForm.valid;
  }

  get isFormDirty(): boolean {
    return this.taskForm.dirty;
  }
}
