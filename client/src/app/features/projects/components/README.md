# Компоненты проектов

Этот модуль содержит компоненты для работы с проектами в приложении TaskBoard.

## Компоненты

### ProjectCard

Презентационный компонент для отображения карточки проекта.

#### Особенности

- **Адаптивный дизайн** - автоматически адаптируется под размер экрана
- **Состояния** - поддержка выбора, наведения, загрузки и отключения
- **Настраиваемые элементы** - можно включать/отключать различные части интерфейса
- **Анимации** - плавные переходы и hover-эффекты
- **Доступность** - полная поддержка ARIA и клавиатурной навигации
- **Skeleton loader** - индикатор загрузки с анимацией

#### Использование

```html
<app-project-card
  [project]="project"
  [isSelected]="false"
  [isHovered]="false"
  [showActions]="true"
  [showSelection]="false"
  [showStatus]="true"
  [showMembers]="true"
  [showDates]="true"
  [loading]="false"
  [disabled]="false"
  (select)="onProjectSelect($event)"
  (action)="onProjectAction($event)"
  (click)="onProjectClick($event)"
  (hover)="onProjectHover($event)">
</app-project-card>
```

#### Входные параметры

| Параметр | Тип | По умолчанию | Описание |
|----------|-----|--------------|----------|
| `project` | `Project` | - | Объект проекта (обязательный) |
| `isSelected` | `boolean` | `false` | Выбран ли проект |
| `isHovered` | `boolean` | `false` | Наведен ли курсор |
| `showActions` | `boolean` | `true` | Показывать ли меню действий |
| `showSelection` | `boolean` | `false` | Показывать ли чекбокс выбора |
| `showStatus` | `boolean` | `true` | Показывать ли статус проекта |
| `showMembers` | `boolean` | `true` | Показывать ли количество участников |
| `showDates` | `boolean` | `true` | Показывать ли даты |
| `loading` | `boolean` | `false` | Состояние загрузки |
| `disabled` | `boolean` | `false` | Отключена ли карточка |

#### Выходные события

| Событие | Тип | Описание |
|---------|-----|----------|
| `select` | `{ projectId: string; selected: boolean }` | Выбор/отмена выбора проекта |
| `action` | `ProjectAction` | Действие с проектом (edit, delete, archive, etc.) |
| `click` | `Project` | Клик по карточке проекта |
| `hover` | `{ projectId: string; isHovered: boolean }` | Наведение курсора |

#### Примеры использования

**Базовая карточка:**
```html
<app-project-card [project]="project"></app-project-card>
```

**С поддержкой выбора:**
```html
<app-project-card
  [project]="project"
  [showSelection]="true"
  [isSelected]="selectedProjects.includes(project.id)"
  (select)="onProjectSelect($event)">
</app-project-card>
```

**Минимальная карточка:**
```html
<app-project-card
  [project]="project"
  [showActions]="false"
  [showStatus]="false"
  [showMembers]="false"
  [showDates]="false">
</app-project-card>
```

### ProjectList

Контейнерный компонент для отображения списка проектов с фильтрацией, сортировкой и пагинацией.

#### Особенности

- **Поиск с debounce** - поиск проектов с задержкой для оптимизации
- **Фильтрация** - по статусу, датам и другим параметрам
- **Сортировка** - по различным полям проекта
- **Бесконечная прокрутка** - автоматическая загрузка при прокрутке
- **Пагинация** - альтернатива бесконечной прокрутке
- **Массовые действия** - выбор нескольких проектов
- **Адаптивный дизайн** - два режима просмотра: сетка и список
- **Управление состоянием** - интеграция с NgRx

#### Использование

```html
<app-project-list
  [config]="config"
  [enableSelection]="true"
  [enableInfiniteScroll]="true"
  [enableFilters]="true"
  [enableSorting]="true"
  [enableSearch]="true"
  [enableActions]="true"
  [pageSize]="12"
  [debounceTime]="300"
  (projectSelected)="onProjectSelected($event)"
  (projectAction)="onProjectAction($event)"
  (selectionChanged)="onSelectionChanged($event)">
</app-project-list>
```

#### Входные параметры

| Параметр | Тип | По умолчанию | Описание |
|----------|-----|--------------|----------|
| `config` | `Partial<ProjectListConfig>` | `{}` | Конфигурация компонента |
| `enableSelection` | `boolean` | `false` | Включить выбор проектов |
| `enableInfiniteScroll` | `boolean` | `true` | Включить бесконечную прокрутку |
| `enableFilters` | `boolean` | `true` | Включить фильтры |
| `enableSorting` | `boolean` | `true` | Включить сортировку |
| `enableSearch` | `boolean` | `true` | Включить поиск |
| `enableActions` | `boolean` | `true` | Включить действия |
| `pageSize` | `number` | `12` | Размер страницы |
| `debounceTime` | `number` | `300` | Время debounce для поиска (мс) |

#### Выходные события

| Событие | Тип | Описание |
|---------|-----|----------|
| `projectSelected` | `Project` | Выбор проекта |
| `projectAction` | `ProjectAction` | Действие с проектом |
| `selectionChanged` | `string[]` | Изменение выбранных проектов |

#### Конфигурация

```typescript
interface ProjectListConfig {
  enableSelection: boolean;
  enableInfiniteScroll: boolean;
  enableFilters: boolean;
  enableSorting: boolean;
  enableSearch: boolean;
  enableActions: boolean;
  pageSize: number;
  debounceTime: number;
}
```

#### Примеры использования

**Базовый список:**
```html
<app-project-list></app-project-list>
```

**С поддержкой выбора:**
```html
<app-project-list
  [enableSelection]="true"
  (selectionChanged)="onSelectionChanged($event)">
</app-project-list>
```

**С пагинацией вместо бесконечной прокрутки:**
```html
<app-project-list
  [enableInfiniteScroll]="false"
  [pageSize]="6">
</app-project-list>
```

**Минимальная конфигурация:**
```html
<app-project-list
  [enableFilters]="false"
  [enableSorting]="false"
  [enableSearch]="false"
  [enableActions]="false">
</app-project-list>
```

**Полная конфигурация:**
```typescript
const config: ProjectListConfig = {
  enableSelection: true,
  enableInfiniteScroll: true,
  enableFilters: true,
  enableSorting: true,
  enableSearch: true,
  enableActions: true,
  pageSize: 12,
  debounceTime: 300
};
```

```html
<app-project-list
  [config]="config"
  (projectSelected)="onProjectSelected($event)"
  (projectAction)="onProjectAction($event)"
  (selectionChanged)="onSelectionChanged($event)">
</app-project-list>
```

## Модели данных

### Project

```typescript
interface Project {
  id: string;
  name: string;
  description: string;
  status: 'active' | 'archived' | 'completed' | 'on-hold';
  ownerId: string;
  members: string[];
  settings: ProjectSettings;
  createdAt: Date;
  updatedAt: Date;
}

interface ProjectSettings {
  allowGuestAccess: boolean;
  defaultTaskPriority: 'low' | 'medium' | 'high' | 'urgent';
  autoAssignTasks: boolean;
  requireTimeTracking: boolean;
}
```

### ProjectAction

```typescript
interface ProjectAction {
  type: 'edit' | 'delete' | 'archive' | 'duplicate' | 'share' | 'create';
  projectId: string;
  payload?: any;
}
```

## Стилизация

### CSS переменные

Компоненты используют CSS переменные для настройки внешнего вида:

```scss
:root {
  --project-card-border-radius: 12px;
  --project-card-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  --project-card-shadow-hover: 0 4px 16px rgba(0, 0, 0, 0.15);
  --project-card-transition-duration: 0.2s;
}
```

### Темы

Компоненты поддерживают:
- **Светлую тему** (по умолчанию)
- **Темную тему** (автоматически при `prefers-color-scheme: dark`)
- **Высокий контраст** (при `prefers-contrast: high`)
- **Уменьшенное движение** (при `prefers-reduced-motion: reduce`)

### Адаптивность

Компоненты адаптируются под различные размеры экрана:

- **Desktop** (1200px+) - полная функциональность
- **Tablet** (768px-1199px) - упрощенная панель инструментов
- **Mobile** (до 767px) - вертикальное расположение элементов

## Тестирование

### Unit тесты

Компоненты покрыты unit тестами:

```bash
npm run test -- --include="**/project-card/**/*.spec.ts"
npm run test -- --include="**/project-list/**/*.spec.ts"
```

### Storybook

Компоненты документированы в Storybook:

```bash
npm run storybook
```

Доступные истории:
- Различные состояния компонентов
- Разные конфигурации
- Адаптивность
- Темы оформления

## Производительность

### Оптимизации

- **OnPush ChangeDetection** - для улучшения производительности
- **TrackBy функции** - для оптимизации ngFor
- **Debounce** - для поиска и фильтрации
- **Lazy loading** - для больших списков
- **Virtual scrolling** - для очень больших списков (планируется)

### Мониторинг

Рекомендуется мониторить:
- Время отклика интерфейса
- Количество перерисовок
- Использование памяти
- Размер бандла

## Доступность

### ARIA

Компоненты включают:
- Правильные ARIA-атрибуты
- Семантическую разметку
- Поддержку скринридеров

### Клавиатурная навигация

- Tab для навигации
- Enter/Space для активации
- Escape для закрытия модальных окон
- Стрелки для навигации в списках

### Фокус

- Видимые индикаторы фокуса
- Логический порядок табуляции
- Управление фокусом в модальных окнах

## Браузерная поддержка

- **Chrome** 90+
- **Firefox** 88+
- **Safari** 14+
- **Edge** 90+

## Лицензия

MIT License 
