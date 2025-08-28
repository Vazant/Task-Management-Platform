# Kanban Board — Конспект

## Обзор

Kanban Board — это визуальный компонент для управления задачами с drag & drop функциональностью. Позволяет пользователям перемещать задачи между колонками статусов и изменять их порядок внутри колонок.

## Архитектура

### Компоненты
- `KanbanBoardComponent` — основной компонент доски
- `TaskCardComponent` — карточка задачи (переиспользуется)
- Angular CDK Drag & Drop для интерактивности

### Состояние
- NgRx Entity Adapter для нормализованного состояния задач
- Колонки: Backlog, In Progress, Done, Blocked
- Поддержка переупорядочивания и изменения статуса

## Основные возможности

### 1. Drag & Drop между колонками
```typescript
onDrop(event: CdkDragDrop<Task[]>): void {
  if (event.previousContainer === event.container) {
    // Перемещение в пределах одной колонки
    moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    this.store.dispatch(TaskActions.reorderTasks({ tasks: reorderedTasks }));
  } else {
    // Перемещение между колонками
    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex
    );
    this.store.dispatch(TaskActions.updateTask({
      id: task.id,
      status: newStatus,
      updatedAt: new Date().toISOString()
    }));
  }
}
```

### 2. Структура колонок
```typescript
interface KanbanColumn {
  status: TaskStatus;
  title: string;
  tasks: Task[];
  color: string;
}

readonly columns: KanbanColumn[] = [
  { status: 'backlog', title: 'Backlog', tasks: [], color: 'accent' },
  { status: 'in-progress', title: 'In Progress', tasks: [], color: 'primary' },
  { status: 'done', title: 'Done', tasks: [], color: 'success' },
  { status: 'blocked', title: 'Blocked', tasks: [], color: 'warn' }
];
```

### 3. Фильтрация задач по статусу
```typescript
this.columns$ = combineLatest([
  this.store.select(TaskSelectors.selectAllTasks),
  this.loading$
]).pipe(
  map(([tasks, loading]) => {
    if (loading) return this.columns;
    
    return this.columns.map(column => ({
      ...column,
      tasks: tasks.filter(task => task.status === column.status)
    }));
  })
);
```

## NgRx интеграция

### Actions
```typescript
// Переупорядочивание задач
export const reorderTasks = createAction(
  '[Tasks] Reorder Tasks',
  props<{ tasks: Task[] }>()
);

// Обновление задачи
export const updateTask = createAction(
  '[Tasks] Update Task',
  props<{ task: Partial<Task> & { id: string } }>()
);
```

### Reducer
```typescript
on(TasksActions.reorderTasksSuccess, (state, { tasks }) =>
  tasksAdapter.setMany(tasks, {
    ...state,
    loading: false,
    error: null,
  })
),

on(TasksActions.updateTaskSuccess, (state, { task }) =>
  tasksAdapter.upsertOne(task, {
    ...state,
    loading: false,
    error: null,
  })
)
```

## Стилизация

### Основные стили
```scss
.kanban-board {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.kanban-columns {
  display: flex;
  gap: 16px;
  height: 100%;
  overflow-x: auto;
}

.kanban-column {
  min-width: 320px;
  max-width: 320px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
```

### Drag & Drop анимации
```scss
.task-item {
  &.cdk-drag-preview {
    transform: rotate(5deg);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  }

  &.cdk-drag-placeholder {
    opacity: 0.3;
    background: #f0f0f0;
    border: 2px dashed #ccc;
    border-radius: 4px;
    height: 60px;
  }
}

.cdk-drop-list-receiving {
  background: rgba(33, 150, 243, 0.05);
  border-radius: 8px;
  transition: background 0.2s ease;
}
```

## Доступность

### ARIA атрибуты
```html
<div 
  class="column-content"
  cdkDropList
  [id]="column.status"
  [cdkDropListData]="column.tasks"
  [cdkDropListConnectedTo]="getConnectedDropLists()"
  (cdkDropListDropped)="onDrop($event)"
  role="region"
  [attr.aria-label]="column.title + ' column'">
```

### Клавиатурная навигация
- Поддержка Tab для навигации между элементами
- Enter/Space для активации кнопок
- Escape для отмены drag операции

### Уменьшение движения
```scss
@media (prefers-reduced-motion: reduce) {
  .task-item,
  .kanban-column,
  .cdk-drop-list-dragging .task-item {
    transition: none;
  }
}
```

## Тестирование

### Unit тесты
```typescript
describe('KanbanBoardComponent', () => {
  it('should filter tasks by status', () => {
    // Тест фильтрации задач по статусу
  });

  it('should handle drag and drop within column', () => {
    // Тест переупорядочивания в пределах колонки
  });

  it('should handle drag and drop between columns', () => {
    // Тест перемещения между колонками
  });
});
```

### Storybook stories
```typescript
export const Default: Story = {
  args: {},
};

export const Loading: Story = {
  decorators: [provideMockStore({ initialState: { tasks: { loading: true } } })]
};

export const Empty: Story = {
  decorators: [provideMockStore({ initialState: { tasks: { entities: {}, ids: [] } } })]
};
```

## Производительность

### OnPush стратегия
```typescript
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class KanbanBoardComponent {
  // Компонент обновляется только при изменении входных данных
}
```

### TrackBy функции
```typescript
trackByTaskId(index: number, task: Task): string {
  return task.id;
}

trackByColumnStatus(index: number, column: KanbanColumn): string {
  return column.status;
}
```

### Виртуализация (для больших списков)
```typescript
// Для больших списков задач можно использовать CDK Virtual Scroll
import { ScrollingModule } from '@angular/cdk/scrolling';

@Component({
  imports: [ScrollingModule],
})
export class KanbanBoardComponent {
  // Виртуализация для колонок с большим количеством задач
}
```

## Лучшие практики

### 1. Обработка ошибок
```typescript
onDrop(event: CdkDragDrop<Task[]>) {
  try {
    // Логика drag & drop
  } catch (error) {
    console.error('Drag & drop error:', error);
    // Показать уведомление пользователю
  }
}
```

### 2. Оптимизация перерисовок
```typescript
// Используем мемоизированные селекторы
this.columns$ = combineLatest([
  this.store.select(TaskSelectors.selectAllTasks),
  this.loading$
]).pipe(
  map(([tasks, loading]) => {
    // Мемоизация результата
    return this.memoizedColumns(tasks, loading);
  })
);
```

### 3. Валидация операций
```typescript
private validateDropOperation(task: Task, newStatus: TaskStatus): boolean {
  // Проверка бизнес-правил
  if (task.status === 'done' && newStatus === 'backlog') {
    return false; // Нельзя вернуть выполненную задачу в бэклог
  }
  return true;
}
```

### 4. Логирование действий
```typescript
onDrop(event: CdkDragDrop<Task[]>) {
  const task = event.previousContainer.data[event.previousIndex];
  const newStatus = this.getStatusFromContainerId(event.container.id);
  
  console.log(`Task "${task.title}" moved from ${task.status} to ${newStatus}`);
  
  // Диспатч действий
}
```

## Расширения

### 1. WIP лимиты
```typescript
interface KanbanColumn {
  status: TaskStatus;
  title: string;
  tasks: Task[];
  color: string;
  wipLimit?: number; // Work In Progress limit
}
```

### 2. Автоматическое сохранение
```typescript
// Автоматическое сохранение при изменении
private autoSave$ = new Subject<void>();

ngOnInit() {
  this.autoSave$.pipe(
    debounceTime(1000),
    switchMap(() => this.saveChanges())
  ).subscribe();
}
```

### 3. Undo/Redo
```typescript
// История изменений для отмены операций
private changeHistory: Array<{
  action: 'move' | 'reorder';
  taskId: string;
  fromStatus: TaskStatus;
  toStatus: TaskStatus;
  fromIndex: number;
  toIndex: number;
}> = [];
```

## Заключение

Kanban Board предоставляет интуитивный интерфейс для управления задачами с поддержкой drag & drop. Компонент интегрирован с NgRx для централизованного управления состоянием и обеспечивает хорошую производительность и доступность.
