# E2E тестирование Task Management Platform

## Обзор

End-to-End (E2E) тестирование обеспечивает проверку полного пользовательского workflow от начала до конца, гарантируя корректную работу всех компонентов системы в реальных условиях.

## Технологический стек

- **Cypress**: Основной инструмент для E2E тестирования
- **TypeScript**: Типизированные тесты для лучшей поддержки
- **Page Object Model**: Паттерн для организации тестов
- **Custom Commands**: Переиспользуемые команды Cypress

## Структура тестов

```
cypress/
├── e2e/
│   ├── api/                    # API тесты
│   │   └── tasks-api.cy.ts
│   ├── projects/              # Тесты управления проектами
│   │   └── project-management.cy.ts
│   ├── tasks/                 # Тесты управления задачами
│   │   └── task-management.cy.ts
│   └── user-flows/            # Полные пользовательские сценарии
│       └── complete-task-workflow.cy.ts
├── fixtures/                  # Тестовые данные
│   ├── tasks.json
│   ├── projects.json
│   └── users.json
├── support/
│   ├── commands.ts           # Кастомные команды
│   ├── helpers/              # Вспомогательные функции
│   │   └── test-data.ts
│   └── pages/                # Page Objects
│       └── tasks-page.ts
└── cypress.config.ts         # Конфигурация Cypress
```

## Основные тестовые сценарии

### 1. Полный workflow управления задачами

#### Создание задачи
```typescript
it('should create a new task', () => {
  const task = TestDataHelper.generateRandomTask();
  
  cy.visit('/tasks');
  cy.get('[data-cy=create-task-btn]').click();
  
  cy.get('[data-cy=task-title-input]').type(task.title);
  cy.get('[data-cy=task-description-input]').type(task.description);
  cy.get('[data-cy=task-priority]').select(task.priority);
  cy.get('[data-cy=save-task-btn]').click();
  
  cy.get('[data-cy=task-list]').should('contain', task.title);
  cy.get('[data-cy=success-message]').should('be.visible');
});
```

#### Редактирование задачи
```typescript
it('should edit an existing task', () => {
  const task = TestDataHelper.generateRandomTask();
  cy.createTask(task.title, task.description);
  
  cy.contains(task.title).click();
  cy.get('[data-cy=edit-task-btn]').click();
  
  cy.get('[data-cy=task-title-input]').clear().type('Updated Task Title');
  cy.get('[data-cy=save-task-btn]').click();
  
  cy.get('[data-cy=task-list]').should('contain', 'Updated Task Title');
});
```

#### Изменение статуса задачи
```typescript
it('should change task status', () => {
  const task = TestDataHelper.generateRandomTask();
  cy.createTask(task.title, task.description);
  
  cy.contains(task.title).click();
  cy.get('[data-cy=status-select]').select('IN_PROGRESS');
  
  cy.get('[data-cy=task-list]').should('contain', 'IN_PROGRESS');
});
```

### 2. Управление проектами

#### Создание проекта
```typescript
it('should create a new project', () => {
  const project = TestDataHelper.generateRandomProject();
  
  cy.visit('/projects');
  cy.get('[data-cy=create-project-btn]').click();
  
  cy.get('[data-cy=project-name-input]').type(project.name);
  cy.get('[data-cy=project-description-input]').type(project.description);
  cy.get('[data-cy=project-start-date]').type(project.startDate);
  cy.get('[data-cy=project-end-date]').type(project.endDate);
  cy.get('[data-cy=save-project-btn]').click();
  
  cy.get('[data-cy=project-list]').should('contain', project.name);
});
```

#### Управление командой проекта
```typescript
it('should manage project team', () => {
  const project = TestDataHelper.generateRandomProject();
  cy.createProject(project);
  
  // Добавление участника команды
  cy.get('[data-cy=project-team-tab]').click();
  cy.get('[data-cy=add-member-btn]').click();
  
  cy.get('[data-cy=member-email-input]').type('team@example.com');
  cy.get('[data-cy=member-role-select]').select('DEVELOPER');
  cy.get('[data-cy=add-member-confirm]').click();
  
  cy.get('[data-cy=team-list]').should('contain', 'team@example.com');
});
```

### 3. Аналитика и отчеты

#### Просмотр аналитики
```typescript
it('should display project analytics', () => {
  const project = TestDataHelper.generateRandomProject();
  cy.createProject(project);
  
  // Создание задач с разными статусами
  const tasks = ['TODO', 'IN_PROGRESS', 'DONE'];
  tasks.forEach(status => {
    const task = TestDataHelper.generateRandomTask();
    cy.createTask(task.title, task.description);
    cy.get('[data-cy=task-status-select]').select(status);
    cy.get('[data-cy=save-task-btn]').click();
  });
  
  // Переход к аналитике
  cy.get('[data-cy=project-analytics-tab]').click();
  
  // Проверка отображения аналитики
  cy.get('[data-cy=task-status-chart]').should('be.visible');
  cy.get('[data-cy=completion-rate]').should('be.visible');
  cy.get('[data-cy=team-productivity]').should('be.visible');
});
```

## Page Object Model

### TasksPage
```typescript
export class TasksPage {
  static visit() {
    cy.visit('/tasks');
  }
  
  static getCreateTaskButton() {
    return cy.get('[data-cy=create-task-btn]');
  }
  
  static getTaskList() {
    return cy.get('[data-cy=task-list]');
  }
  
  static getTaskItem(title: string) {
    return cy.get('[data-cy=task-item]').contains(title);
  }
  
  static createTask(task: { title: string; description: string; priority: string }) {
    this.getCreateTaskButton().click();
    cy.get('[data-cy=task-title-input]').type(task.title);
    cy.get('[data-cy=task-description-input]').type(task.description);
    cy.get('[data-cy=task-priority]').select(task.priority);
    cy.get('[data-cy=save-task-btn]').click();
  }
  
  static editTask(title: string, newTitle: string) {
    this.getTaskItem(title).click();
    cy.get('[data-cy=edit-task-btn]').click();
    cy.get('[data-cy=task-title-input]').clear().type(newTitle);
    cy.get('[data-cy=save-task-btn]').click();
  }
  
  static changeTaskStatus(title: string, status: string) {
    this.getTaskItem(title).click();
    cy.get('[data-cy=status-select]').select(status);
  }
  
  static deleteTask(title: string) {
    this.getTaskItem(title).click();
    cy.get('[data-cy=delete-task-btn]').click();
    cy.get('[data-cy=confirm-delete-btn]').click();
  }
}
```

### ProjectsPage
```typescript
export class ProjectsPage {
  static visit() {
    cy.visit('/projects');
  }
  
  static getCreateProjectButton() {
    return cy.get('[data-cy=create-project-btn]');
  }
  
  static getProjectList() {
    return cy.get('[data-cy=project-list]');
  }
  
  static createProject(project: {
    name: string;
    description: string;
    startDate: string;
    endDate: string;
    priority: string;
  }) {
    this.getCreateProjectButton().click();
    cy.get('[data-cy=project-name-input]').type(project.name);
    cy.get('[data-cy=project-description-input]').type(project.description);
    cy.get('[data-cy=project-start-date]').type(project.startDate);
    cy.get('[data-cy=project-end-date]').type(project.endDate);
    cy.get('[data-cy=project-priority]').select(project.priority);
    cy.get('[data-cy=save-project-btn]').click();
  }
  
  static addTeamMember(email: string, role: string) {
    cy.get('[data-cy=project-team-tab]').click();
    cy.get('[data-cy=add-member-btn]').click();
    cy.get('[data-cy=member-email-input]').type(email);
    cy.get('[data-cy=member-role-select]').select(role);
    cy.get('[data-cy=add-member-confirm]').click();
  }
}
```

## Кастомные команды Cypress

### commands.ts
```typescript
declare global {
  namespace Cypress {
    interface Chainable {
      login(email: string, password: string): Chainable<void>;
      createTask(title: string, description: string): Chainable<void>;
      createProject(project: any): Chainable<void>;
      deleteTask(title: string): Chainable<void>;
      deleteProject(projectId: string): Chainable<void>;
    }
  }
}

Cypress.Commands.add('login', (email: string, password: string) => {
  cy.visit('/auth/login');
  cy.get('[data-cy=email-input]').type(email);
  cy.get('[data-cy=password-input]').type(password);
  cy.get('[data-cy=login-btn]').click();
  cy.url().should('include', '/dashboard');
});

Cypress.Commands.add('createTask', (title: string, description: string) => {
  cy.visit('/tasks');
  cy.get('[data-cy=create-task-btn]').click();
  cy.get('[data-cy=task-title-input]').type(title);
  cy.get('[data-cy=task-description-input]').type(description);
  cy.get('[data-cy=save-task-btn]').click();
  cy.get('[data-cy=success-message]').should('be.visible');
});

Cypress.Commands.add('createProject', (project: any) => {
  cy.visit('/projects');
  cy.get('[data-cy=create-project-btn]').click();
  cy.get('[data-cy=project-name-input]').type(project.name);
  cy.get('[data-cy=project-description-input]').type(project.description);
  cy.get('[data-cy=project-start-date]').type(project.startDate);
  cy.get('[data-cy=project-end-date]').type(project.endDate);
  cy.get('[data-cy=save-project-btn]').click();
  cy.get('[data-cy=success-message]').should('be.visible');
});

Cypress.Commands.add('deleteTask', (title: string) => {
  cy.contains(title).click();
  cy.get('[data-cy=delete-task-btn]').click();
  cy.get('[data-cy=confirm-delete-btn]').click();
  cy.get('[data-cy=success-message]').should('be.visible');
});

Cypress.Commands.add('deleteProject', (projectId: string) => {
  cy.visit(`/projects/${projectId}`);
  cy.get('[data-cy=delete-project-btn]').click();
  cy.get('[data-cy=confirm-delete-btn]').click();
  cy.get('[data-cy=success-message]').should('be.visible');
});
```

## Тестовые данные

### TestDataHelper
```typescript
export class TestDataHelper {
  static generateRandomTask() {
    return {
      title: `Test Task ${Math.random().toString(36).substr(2, 9)}`,
      description: 'Test task description',
      priority: 'MEDIUM',
      status: 'TODO',
      assignee: null
    };
  }
  
  static generateRandomProject() {
    const startDate = new Date();
    const endDate = new Date(startDate.getTime() + 30 * 24 * 60 * 60 * 1000); // 30 days later
    
    return {
      name: `Test Project ${Math.random().toString(36).substr(2, 9)}`,
      description: 'Test project description',
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      priority: 'MEDIUM'
    };
  }
  
  static generateRandomTasks(count: number) {
    return Array.from({ length: count }, () => this.generateRandomTask());
  }
  
  static generateRandomUser() {
    return {
      email: `test${Math.random().toString(36).substr(2, 9)}@example.com`,
      password: 'password123',
      firstName: 'Test',
      lastName: 'User'
    };
  }
}
```

## Конфигурация Cypress

### cypress.config.ts
```typescript
import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    supportFile: 'cypress/support/e2e.ts',
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    viewportWidth: 1280,
    viewportHeight: 720,
    video: true,
    screenshotOnRunFailure: true,
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,
    setupNodeEvents(on, config) {
      // Настройка для генерации отчетов
      on('task', {
        log(message) {
          console.log(message);
          return null;
        }
      });
    }
  },
  component: {
    devServer: {
      framework: 'angular',
      bundler: 'webpack'
    }
  }
});
```

## Запуск тестов

### Локальный запуск
```bash
# Установка зависимостей
npm install

# Запуск Cypress в интерактивном режиме
npx cypress open

# Запуск всех тестов в headless режиме
npx cypress run

# Запуск конкретного теста
npx cypress run --spec "cypress/e2e/tasks/task-management.cy.ts"

# Запуск с видео
npx cypress run --record --key YOUR_RECORD_KEY
```

### CI/CD интеграция

#### GitHub Actions
```yaml
name: E2E Tests
on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  e2e-tests:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
        cache: 'npm'
    
    - name: Install dependencies
      run: npm ci
    
    - name: Start backend server
      run: |
        cd server
        ./mvnw spring-boot:run &
        sleep 30
    
    - name: Start frontend server
      run: |
        npm start &
        sleep 30
    
    - name: Run E2E tests
      run: npx cypress run --record --key ${{ secrets.CYPRESS_RECORD_KEY }}
    
    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: failure()
      with:
        name: cypress-screenshots
        path: cypress/screenshots
```

## Отчеты и мониторинг

### HTML отчеты
```bash
# Генерация HTML отчета
npx cypress run --reporter html --reporter-options output=cypress/reports/report.html
```

### JUnit отчеты
```bash
# Генерация JUnit отчета для CI/CD
npx cypress run --reporter junit --reporter-options output=cypress/reports/junit.xml
```

### Интеграция с Allure
```typescript
// cypress/support/e2e.ts
import 'cypress-allure-plugin';

// В тестах
it('should create task', { tags: '@smoke' }, () => {
  // Test implementation
});
```

## Лучшие практики

### 1. Селекторы
```typescript
// ✅ Хорошо - data-cy атрибуты
cy.get('[data-cy=create-task-btn]')

// ❌ Плохо - CSS селекторы
cy.get('.btn.btn-primary')
```

### 2. Ожидания
```typescript
// ✅ Хорошо - явные ожидания
cy.get('[data-cy=task-list]').should('contain', 'New Task')

// ❌ Плохо - неявные ожидания
cy.wait(1000)
```

### 3. Очистка данных
```typescript
beforeEach(() => {
  cy.clearLocalStorage();
  cy.clearCookies();
  // Очистка тестовых данных
});
```

### 4. Изоляция тестов
```typescript
// Каждый тест должен быть независимым
it('should create task', () => {
  // Тест не должен зависеть от других тестов
});
```

## Troubleshooting

### Частые проблемы

#### 1. Timeout ошибки
```typescript
// Увеличьте timeout для медленных операций
cy.get('[data-cy=slow-element]', { timeout: 10000 })
```

#### 2. Flaky тесты
```typescript
// Используйте retry для нестабильных элементов
cy.get('[data-cy=dynamic-element]').should('be.visible')
```

#### 3. Проблемы с аутентификацией
```typescript
// Используйте cy.session для кэширования сессий
beforeEach(() => {
  cy.session('user', () => {
    cy.login('test@example.com', 'password');
  });
});
```

## Заключение

E2E тестирование является критически важным для обеспечения качества Task Management Platform. Регулярное выполнение тестов помогает:

1. **Выявить регрессии** в функциональности
2. **Обеспечить стабильность** пользовательского опыта
3. **Автоматизировать проверки** критических путей
4. **Повысить уверенность** в релизах

Рекомендуется интегрировать E2E тесты в CI/CD pipeline и выполнять их при каждом изменении кода.
