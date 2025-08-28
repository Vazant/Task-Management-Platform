# Task Forms — Конспект

## Обзор

Task Forms — это система компонентов для создания и редактирования задач с использованием Angular Reactive Forms. Включает диалоговые окна, inline редактирование и валидацию.

## Архитектура

### Компоненты
- `TaskDialogComponent` — диалог создания/редактирования задач
- `TaskInlineEditComponent` — inline редактирование задач
- `TaskDialogService` — сервис для управления диалогами

### Состояние
- NgRx для управления состоянием задач
- Reactive Forms для валидации и управления данными
- Material Design компоненты для UI

## Основные возможности

### 1. Диалог создания/редактирования задач
```typescript
@Component({
  selector: 'app-task-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    // ... другие модули
  ],
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

  onSubmit(): void {
    if (this.taskForm.valid) {
      const formValue = this.taskForm.value;
      const taskData: Partial<Task> = {
        title: formValue.title,
        description: formValue.description,
        status: formValue.status,
        priority: formValue.priority,
        assignee: formValue.assignee,
        dueDate: formValue.dueDate ? formValue.dueDate.toISOString().split('T')[0] : null,
        estimatedHours: formValue.estimatedHours,
        labels: formValue.labels || []
      };

      if (this.data.mode === 'create') {
        this.store.dispatch(TaskActions.createTask({ task: taskData }));
      } else {
        this.store.dispatch(TaskActions.updateTask({
          id: this.data.task.id,
          ...taskData
        }));
      }
    }
  }
}
```

### 2. Сервис управления диалогами
```typescript
@Injectable({
  providedIn: 'root'
})
export class TaskDialogService {
  private readonly dialog = inject(MatDialog);

  openCreateDialog(initialStatus?: TaskStatus): Observable<boolean> {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      width: '600px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      disableClose: true,
      data: {
        mode: 'create',
        initialStatus
      } as TaskDialogData
    });

    return dialogRef.afterClosed();
  }

  openEditDialog(task: Task): Observable<boolean> {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      width: '600px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      disableClose: true,
      data: {
        mode: 'edit',
        task
      } as TaskDialogData
    });

    return dialogRef.afterClosed();
  }
}
```

### 3. Inline редактирование
```typescript
@Component({
  selector: 'app-task-inline-edit',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskInlineEditComponent {
  @Input() task!: Task;
  @Output() save = new EventEmitter<Partial<Task>>();
  @Output() cancel = new EventEmitter<void>();

  taskForm!: FormGroup;
  isEditing = false;

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
}
```

## Валидация форм

### 1. Валидаторы
```typescript
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
```

### 2. Отображение ошибок
```html
<mat-form-field appearance="outline" class="full-width">
  <mat-label>Title</mat-label>
  <input matInput formControlName="title" placeholder="Enter task title">
  <mat-error *ngIf="taskForm.get('title')?.hasError('required')">
    Title is required
  </mat-error>
  <mat-error *ngIf="taskForm.get('title')?.hasError('minlength')">
    Title must be at least 3 characters
  </mat-error>
  <mat-error *ngIf="taskForm.get('title')?.hasError('maxlength')">
    Title cannot exceed 100 characters
  </mat-error>
</mat-form-field>
```

### 3. Кастомная валидация
```typescript
// Валидация даты
private validateDueDate(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const dueDate = control.value;
    if (!dueDate) return null;
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (dueDate < today) {
      return { pastDate: true };
    }
    
    return null;
  };
}

// Валидация часов
private validateEstimatedHours(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const hours = control.value;
    if (!hours) return null;
    
    if (hours < 0.5) {
      return { minHours: { min: 0.5, actual: hours } };
    }
    
    if (hours > 100) {
      return { maxHours: { max: 100, actual: hours } };
    }
    
    return null;
  };
}
```

## Управление метками (Labels)

### 1. Добавление меток
```typescript
addLabel(event: any): void {
  const value = (event.value || '').trim();
  if (value) {
    const labels = this.taskForm.get('labels')?.value || [];
    const newLabel = { name: value, color: this.getRandomColor() };
    this.taskForm.patchValue({ labels: [...labels, newLabel] });
    event.chipInput?.clear();
  }
}
```

### 2. Удаление меток
```typescript
removeLabel(label: any): void {
  const labels = this.taskForm.get('labels')?.value || [];
  const index = labels.findIndex((l: any) => l.name === label.name);
  if (index >= 0) {
    labels.splice(index, 1);
    this.taskForm.patchValue({ labels: [...labels] });
  }
}
```

### 3. Генерация цветов
```typescript
private getRandomColor(): string {
  const colors = [
    '#f44336', '#e91e63', '#9c27b0', '#673ab7', '#3f51b5', 
    '#2196f3', '#03a9f4', '#00bcd4', '#009688', '#4caf50', 
    '#8bc34a', '#cddc39', '#ffeb3b', '#ffc107', '#ff9800', '#ff5722'
  ];
  return colors[Math.floor(Math.random() * colors.length)];
}
```

## Интеграция с NgRx

### 1. Диспатч действий
```typescript
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
  }
}
```

### 2. Обработка результатов
```typescript
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
```

## Стилизация

### 1. Основные стили диалога
```scss
.task-dialog {
  min-width: 500px;
  max-width: 600px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px 0;
  border-bottom: 1px solid #e0e0e0;
}

.dialog-content {
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.form-row {
  margin-bottom: 16px;

  &.two-columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
}
```

### 2. Стили inline редактирования
```scss
.task-inline-edit {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.view-mode {
  padding: 16px;

  .task-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
  }

  .task-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 12px;
  }
}

.edit-mode {
  padding: 16px;
  background: #fafafa;
}
```

### 3. Адаптивный дизайн
```scss
@media (max-width: 768px) {
  .task-dialog {
    min-width: 90vw;
    max-width: 90vw;
  }

  .form-row.two-columns {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .dialog-content {
    padding: 16px;
  }
}
```

## Доступность

### 1. ARIA атрибуты
```html
<mat-form-field appearance="outline" class="full-width">
  <mat-label>Title</mat-label>
  <input matInput formControlName="title" 
         placeholder="Enter task title"
         aria-describedby="title-error">
  <mat-error id="title-error" *ngIf="taskForm.get('title')?.hasError('required')">
    Title is required
  </mat-error>
</mat-form-field>
```

### 2. Клавиатурная навигация
```typescript
// Обработка клавиши Escape для отмены
@HostListener('document:keydown.escape')
onEscapeKey(): void {
  if (this.isEditing) {
    this.onCancel();
  }
}

// Обработка Enter для сохранения
@HostListener('document:keydown.enter', ['$event'])
onEnterKey(event: KeyboardEvent): void {
  if (this.isEditing && this.taskForm.valid) {
    event.preventDefault();
    this.onSave();
  }
}
```

### 3. Фокус-менеджмент
```typescript
// Автофокус на первое поле при открытии диалога
ngAfterViewInit(): void {
  if (this.data.mode === 'create') {
    setTimeout(() => {
      this.titleInput?.nativeElement.focus();
    }, 100);
  }
}
```

## Тестирование

### 1. Unit тесты для компонента
```typescript
describe('TaskDialogComponent', () => {
  let component: TaskDialogComponent;
  let fixture: ComponentFixture<TaskDialogComponent>;
  let mockStore: jasmine.SpyObj<Store>;

  beforeEach(async () => {
    const storeSpy = jasmine.createSpyObj('Store', ['dispatch', 'select']);
    
    await TestBed.configureTestingModule({
      imports: [TaskDialogComponent],
      providers: [
        { provide: Store, useValue: storeSpy },
        { provide: MAT_DIALOG_DATA, useValue: { mode: 'create' } },
        { provide: MatDialogRef, useValue: { close: jasmine.createSpy() } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskDialogComponent);
    component = fixture.componentInstance;
    mockStore = TestBed.inject(Store) as jasmine.SpyObj<Store>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    component.ngOnInit();
    expect(component.taskForm.get('status')?.value).toBe('backlog');
    expect(component.taskForm.get('priority')?.value).toBe('medium');
  });

  it('should validate required fields', () => {
    component.ngOnInit();
    const titleControl = component.taskForm.get('title');
    
    expect(titleControl?.valid).toBeFalsy();
    
    titleControl?.setValue('Test Task');
    expect(titleControl?.valid).toBeTruthy();
  });
});
```

### 2. Тесты для сервиса
```typescript
describe('TaskDialogService', () => {
  let service: TaskDialogService;
  let mockDialog: jasmine.SpyObj<MatDialog>;

  beforeEach(() => {
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    
    TestBed.configureTestingModule({
      providers: [
        TaskDialogService,
        { provide: MatDialog, useValue: dialogSpy }
      ]
    });
    
    service = TestBed.inject(TaskDialogService);
    mockDialog = TestBed.inject(MatDialog) as jasmine.SpyObj<MatDialog>;
  });

  it('should open create dialog', () => {
    const mockDialogRef = { afterClosed: () => of(true) };
    mockDialog.open.and.returnValue(mockDialogRef as any);

    service.openCreateDialog('backlog');

    expect(mockDialog.open).toHaveBeenCalledWith(
      TaskDialogComponent,
      jasmine.objectContaining({
        data: { mode: 'create', initialStatus: 'backlog' }
      })
    );
  });
});
```

## Лучшие практики

### 1. Управление состоянием загрузки
```typescript
onSubmit(): void {
  if (this.taskForm.valid) {
    this.loading = true;
    this.error = null;

    // Диспатч действия
    this.store.dispatch(TaskActions.createTask({ task: taskData }));

    // Подписка на результат
    this.store.select(state => state.tasks).pipe(
      takeUntil(this.destroy$),
      filter(tasksState => !tasksState.loading && this.loading)
    ).subscribe(tasksState => {
      this.loading = false;
      
      if (tasksState.error) {
        this.error = tasksState.error;
      } else {
        this.dialogRef.close(true);
      }
    });
  }
}
```

### 2. Обработка ошибок
```typescript
// Глобальная обработка ошибок форм
private handleFormErrors(): void {
  this.taskForm.valueChanges.pipe(
    takeUntil(this.destroy$),
    debounceTime(300)
  ).subscribe(() => {
    if (this.taskForm.invalid) {
      this.markFormGroupTouched();
    }
  });
}

private markFormGroupTouched(): void {
  Object.keys(this.taskForm.controls).forEach(key => {
    const control = this.taskForm.get(key);
    control?.markAsTouched();
  });
}
```

### 3. Оптимизация производительности
```typescript
// Мемоизация опций
readonly statusOptions = memoize(() => [
  { value: 'backlog', label: 'Backlog' },
  { value: 'in-progress', label: 'In Progress' },
  { value: 'done', label: 'Done' },
  { value: 'blocked', label: 'Blocked' }
]);

// Отписка от подписок
ngOnDestroy(): void {
  this.destroy$.next();
  this.destroy$.complete();
}
```

### 4. Валидация на уровне компонента
```typescript
// Кастомная валидация для бизнес-правил
private validateBusinessRules(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const formGroup = control as FormGroup;
    const status = formGroup.get('status')?.value;
    const priority = formGroup.get('priority')?.value;
    
    // Нельзя назначать высокий приоритет задачам в бэклоге
    if (status === 'backlog' && priority === 'urgent') {
      return { invalidPriorityForStatus: true };
    }
    
    return null;
  };
}
```

## Расширения

### 1. Rich Text Editor
```typescript
// Интеграция с Angular CKEditor
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

@Component({
  imports: [CKEditorModule],
})
export class TaskDialogComponent {
  public Editor = ClassicEditor;
  
  private initForm(): void {
    this.taskForm = this.fb.group({
      description: ['', [Validators.maxLength(5000)]], // Увеличиваем лимит для rich text
      // ... другие поля
    });
  }
}
```

### 2. Drag & Drop для меток
```typescript
// Drag & Drop для переупорядочивания меток
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';

onLabelDrop(event: CdkDragDrop<any[]>): void {
  const labels = this.taskForm.get('labels')?.value || [];
  moveItemInArray(labels, event.previousIndex, event.currentIndex);
  this.taskForm.patchValue({ labels: [...labels] });
}
```

### 3. Автосохранение
```typescript
// Автосохранение черновиков
private setupAutoSave(): void {
  this.taskForm.valueChanges.pipe(
    takeUntil(this.destroy$),
    debounceTime(2000),
    filter(() => this.taskForm.valid && this.taskForm.dirty)
  ).subscribe(() => {
    this.saveDraft();
  });
}

private saveDraft(): void {
  const formValue = this.taskForm.value;
  localStorage.setItem('task-draft', JSON.stringify(formValue));
}
```

## Заключение

Task Forms предоставляет полную систему для создания и редактирования задач с использованием Angular Reactive Forms. Компоненты интегрированы с NgRx для управления состоянием и обеспечивают хорошую производительность, доступность и пользовательский опыт.
