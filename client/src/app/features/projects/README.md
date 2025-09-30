# Projects Feature Module

Модуль управления проектами с полным функционалом CRUD операций, фильтрации, поиска и статистики.

## 🚀 Возможности

- ✅ **CRUD операции**: Создание, просмотр, редактирование и удаление проектов
- ✅ **Фильтрация и поиск**: По статусу, приоритету, названию и тегам
- ✅ **Статистика**: Общая статистика по проектам с метриками
- ✅ **NgRx Store**: Централизованное управление состоянием
- ✅ **Material Design**: Современный UI с адаптивным дизайном
- ✅ **Валидация форм**: Полная валидация с обработкой ошибок
- ✅ **Тестирование**: Unit и E2E тесты

## 📁 Структура модуля

```
projects/
├── components/
│   ├── project-list/              # Список проектов
│   ├── project-create-dialog/     # Диалог создания
│   └── project-edit-dialog/       # Диалог редактирования
├── store/                        # NgRx store
│   ├── projects.actions.ts       # Actions
│   ├── projects.effects.ts       # Effects
│   ├── projects.reducer.ts       # Reducer
│   └── projects.selectors.ts     # Selectors
├── services/
│   └── project.service.ts        # HTTP сервис
├── models/
│   └── project.model.ts          # TypeScript модели
└── projects.module.ts            # Главный модуль
```

## 🛠 Использование

### 1. Импорт модуля

```typescript
import { ProjectsModule } from './features/projects/projects.module';

@NgModule({
  imports: [
    ProjectsModule
  ]
})
export class AppModule { }
```

### 2. Использование в компонентах

```typescript
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Project } from './core/models/project.model';
import * as ProjectsActions from './features/projects/store';
import * as ProjectsSelectors from './features/projects/store/projects.selectors';

@Component({...})
export class MyComponent {
  projects$: Observable<Project[]>;
  loading$: Observable<boolean>;

  constructor(private store: Store) {
    this.projects$ = this.store.select(ProjectsSelectors.selectAllProjects);
    this.loading$ = this.store.select(ProjectsSelectors.selectProjectsLoading);
  }

  loadProjects() {
    this.store.dispatch(ProjectsActions.loadProjects());
  }

  createProject(project: CreateProjectRequest) {
    this.store.dispatch(ProjectsActions.createProject({ request: project }));
  }
}
```

### 3. Использование сервиса

```typescript
import { ProjectService } from './core/services/project.service';

constructor(private projectService: ProjectService) {}

// Получить все проекты
this.projectService.getProjects().subscribe(projects => {
  console.log(projects);
});

// Создать проект
const newProject: CreateProjectRequest = {
  name: 'My Project',
  description: 'Project description',
  priority: ProjectPriority.HIGH,
  color: '#1976d2'
};

this.projectService.createProject(newProject).subscribe(project => {
  console.log('Created:', project);
});
```

## 📊 Модели данных

### Project

```typescript
interface Project {
  id: string;
  name: string;
  description?: string;
  status: ProjectStatus;
  priority: ProjectPriority;
  startDate?: Date;
  endDate?: Date;
  tags?: string[];
  color?: string;
  ownerId: string;
  ownerName?: string;
  teamMembers?: ProjectMember[];
  tasksCount?: number;
  completedTasksCount?: number;
  progress?: number;
  createdAt: Date;
  updatedAt: Date;
}
```

### CreateProjectRequest

```typescript
interface CreateProjectRequest {
  name: string;
  description?: string;
  priority: ProjectPriority;
  startDate?: Date;
  endDate?: Date;
  tags?: string[];
  color?: string;
  teamMemberIds?: string[];
}
```

### UpdateProjectRequest

```typescript
interface UpdateProjectRequest {
  name?: string;
  description?: string;
  status?: ProjectStatus;
  priority?: ProjectPriority;
  startDate?: Date;
  endDate?: Date;
  tags?: string[];
  color?: string;
  teamMemberIds?: string[];
}
```

## 🎯 NgRx Store

### Actions

```typescript
// Загрузка проектов
loadProjects()
loadProjectsSuccess({ projects })
loadProjectsFailure({ error })

// CRUD операции
createProject({ request })
updateProject({ id, request })
deleteProject({ id })

// Фильтрация и поиск
filterProjects({ filter })
searchProjects({ query })
setSelectedProject({ project })
```

### Selectors

```typescript
// Основные селекторы
selectAllProjects
selectSelectedProject
selectProjectsLoading
selectProjectsError

// Вычисляемые селекторы
selectFilteredProjects
selectActiveProjects
selectArchivedProjects
selectProjectsStatistics
selectRecentProjects
```

### Effects

```typescript
// Автоматическая обработка HTTP запросов
loadProjects$ - загрузка списка проектов
createProject$ - создание проекта
updateProject$ - обновление проекта
deleteProject$ - удаление проекта
```

## 🎨 UI Компоненты

### ProjectListComponent

Основной компонент для отображения списка проектов.

**Функции:**
- Отображение проектов в виде карточек
- Фильтрация по статусу
- Поиск по названию
- Статистика проектов
- Действия с проектами (создание, редактирование, удаление)

**Использование:**
```html
<app-project-list></app-project-list>
```

### ProjectCreateDialogComponent

Диалог для создания нового проекта.

**Функции:**
- Форма создания проекта
- Валидация полей
- Выбор приоритета и цвета
- Управление тегами
- Выбор дат

**Использование:**
```typescript
const dialogRef = this.dialog.open(ProjectCreateDialogComponent, {
  width: '600px'
});

dialogRef.afterClosed().subscribe(result => {
  if (result) {
    // Проект создан
  }
});
```

### ProjectEditDialogComponent

Диалог для редактирования существующего проекта.

**Функции:**
- Предзаполненная форма
- Редактирование всех полей
- Изменение статуса
- Обновление тегов

**Использование:**
```typescript
const dialogRef = this.dialog.open(ProjectEditDialogComponent, {
  width: '600px',
  data: { project: existingProject }
});
```

## 🔧 API Интеграция

### Endpoints

```typescript
GET    /api/projects              # Получить все проекты
GET    /api/projects/:id          # Получить проект по ID
POST   /api/projects              # Создать проект
PUT    /api/projects/:id          # Обновить проект
DELETE /api/projects/:id          # Удалить проект
GET    /api/projects/statistics   # Статистика проектов
GET    /api/projects/search       # Поиск проектов
```

### Фильтрация

```typescript
// Параметры запроса
interface ProjectFilters {
  status?: ProjectStatus;
  priority?: ProjectPriority;
  ownerId?: string;
  tags?: string[];
  dateRange?: {
    start: Date;
    end: Date;
  };
}
```

## 🧪 Тестирование

### Unit тесты

```bash
# Запуск unit тестов
npm run test

# Запуск тестов с покрытием
npm run test:coverage
```

**Покрытие тестами:**
- ✅ ProjectService (100%)
- ✅ ProjectListComponent (95%)
- ✅ ProjectCreateDialogComponent (90%)
- ✅ ProjectEditDialogComponent (90%)
- ✅ NgRx Store (100%)

### E2E тесты

```bash
# Запуск E2E тестов
npm run e2e

# Запуск в headless режиме
npm run e2e:headless
```

**Тестируемые сценарии:**
- ✅ Создание проекта
- ✅ Редактирование проекта
- ✅ Удаление проекта
- ✅ Фильтрация и поиск
- ✅ Обработка ошибок
- ✅ Состояния загрузки

## 🎨 Стилизация

### CSS классы

```scss
// Основные контейнеры
.project-list-container
.project-statistics
.project-filters
.projects-grid

// Карточки проектов
.project-card
.project-avatar
.project-status
.project-priority
.project-progress

// Статусы проектов
.project-status--active
.project-status--completed
.project-status--archived
.project-status--on_hold

// Приоритеты
.project-priority--low
.project-priority--medium
.project-priority--high
.project-priority--urgent
```

### Адаптивность

```scss
// Мобильные устройства
@media (max-width: 768px) {
  .projects-grid {
    grid-template-columns: 1fr;
  }
  
  .project-filters {
    flex-direction: column;
  }
}
```

## 🚀 Развертывание

### Переменные окружения

```typescript
// environment.ts
export const environment = {
  apiUrl: 'http://localhost:8080/api',
  production: false
};
```

### Конфигурация

```typescript
// app.config.ts
import { ProjectsModule } from './features/projects/projects.module';

export const appConfig: ApplicationConfig = {
  providers: [
    // ... другие провайдеры
    importProvidersFrom(ProjectsModule)
  ]
};
```

## 📝 Логирование

### Уровни логирования

```typescript
// Успешные операции
console.log('Project created successfully:', project);

// Предупреждения
console.warn('Project not found:', projectId);

// Ошибки
console.error('Failed to create project:', error);
```

### Метрики

```typescript
// Отслеживание производительности
const startTime = performance.now();
// ... операция
const endTime = performance.now();
console.log(`Operation took ${endTime - startTime} milliseconds`);
```

## 🔒 Безопасность

### Валидация

```typescript
// Валидация на клиенте
const validators = {
  name: [Validators.required, Validators.minLength(3)],
  description: [Validators.maxLength(500)],
  priority: [Validators.required]
};
```

### Санитизация

```typescript
// Очистка пользовательского ввода
const sanitizedInput = DOMPurify.sanitize(userInput);
```

## 🐛 Отладка

### Redux DevTools

```typescript
// Включение Redux DevTools
provideStoreDevtools({
  maxAge: 25,
  logOnly: environment.production
})
```

### Логирование действий

```typescript
// Логирование всех действий
this.actions$.subscribe(action => {
  console.log('Action dispatched:', action);
});
```

## 📚 Дополнительные ресурсы

- [Angular Material Documentation](https://material.angular.io/)
- [NgRx Documentation](https://ngrx.io/)
- [RxJS Documentation](https://rxjs.dev/)
- [Cypress Documentation](https://docs.cypress.io/)

## 🤝 Поддержка

При возникновении проблем:

1. Проверьте консоль браузера на наличие ошибок
2. Убедитесь, что API сервер запущен
3. Проверьте правильность конфигурации
4. Обратитесь к команде разработки

---

*Последнее обновление: Февраль 2024*
