# Cypress E2E Testing

## Overview

Cypress is a modern web testing framework that provides end-to-end testing capabilities for web applications. It offers real-time reload, automatic waiting, time travel debugging, and network traffic control.

## Key Concepts

### 1. Test Structure
- **describe()**: Groups related tests
- **it()**: Individual test cases
- **beforeEach()**: Setup before each test
- **afterEach()**: Cleanup after each test
- **before()**: One-time setup
- **after()**: One-time cleanup

### 2. Commands and Actions
- **cy.visit()**: Navigate to URLs
- **cy.get()**: Query DOM elements
- **cy.click()**: Click elements
- **cy.type()**: Type text
- **cy.select()**: Select options
- **cy.check()**: Check checkboxes
- **cy.uncheck()**: Uncheck checkboxes

### 3. Assertions
- **cy.should()**: Make assertions
- **cy.expect()**: BDD/TDD assertions
- **cy.contains()**: Find elements by text
- **cy.url()**: Assert current URL
- **cy.title()**: Assert page title

## Implementation Patterns

### 1. Page Object Model
```typescript
// cypress/support/pages/tasks-page.ts
export class TasksPage {
  // Selectors
  private readonly taskList = '[data-testid="task-list"]';
  private readonly addTaskButton = '[data-testid="add-task-button"]';
  private readonly taskTitleInput = '[data-testid="task-title-input"]';
  private readonly taskDescriptionInput = '[data-testid="task-description-input"]';
  private readonly saveTaskButton = '[data-testid="save-task-button"]';

  // Actions
  visit() {
    cy.visit('/tasks');
    return this;
  }

  clickAddTask() {
    cy.get(this.addTaskButton).click();
    return this;
  }

  fillTaskForm(title: string, description: string) {
    cy.get(this.taskTitleInput).type(title);
    cy.get(this.taskDescriptionInput).type(description);
    return this;
  }

  saveTask() {
    cy.get(this.saveTaskButton).click();
    return this;
  }

  // Assertions
  shouldShowTask(title: string) {
    cy.get(this.taskList).should('contain', title);
    return this;
  }

  shouldShowEmptyState() {
    cy.get(this.taskList).should('contain', 'No tasks found');
    return this;
  }
}
```

### 2. Custom Commands
```typescript
// cypress/support/commands.ts
declare global {
  namespace Cypress {
    interface Chainable {
      login(email: string, password: string): Chainable<void>;
      createTask(title: string, description?: string): Chainable<void>;
      deleteTask(title: string): Chainable<void>;
      selectOption(selector: string, option: string): Chainable<void>;
      waitForApi(alias: string): Chainable<void>;
    }
  }
}

Cypress.Commands.add('login', (email: string, password: string) => {
  cy.visit('/login');
  cy.get('[data-testid="email-input"]').type(email);
  cy.get('[data-testid="password-input"]').type(password);
  cy.get('[data-testid="login-button"]').click();
  cy.url().should('include', '/dashboard');
});

Cypress.Commands.add('createTask', (title: string, description = '') => {
  cy.get('[data-testid="add-task-button"]').click();
  cy.get('[data-testid="task-title-input"]').type(title);
  if (description) {
    cy.get('[data-testid="task-description-input"]').type(description);
  }
  cy.get('[data-testid="save-task-button"]').click();
  cy.get('[data-testid="task-list"]').should('contain', title);
});

Cypress.Commands.add('deleteTask', (title: string) => {
  cy.contains(title).parent().find('[data-testid="delete-task-button"]').click();
  cy.get('[data-testid="confirm-delete-button"]').click();
  cy.get('[data-testid="task-list"]').should('not.contain', title);
});

Cypress.Commands.add('selectOption', (selector: string, option: string) => {
  cy.get(selector).click();
  cy.get(`[data-value="${option}"]`).click();
});

Cypress.Commands.add('waitForApi', (alias: string) => {
  cy.wait(`@${alias}`);
});
```

### 3. Test Data Management
```typescript
// cypress/fixtures/tasks.json
{
  "validTask": {
    "title": "Test Task",
    "description": "Test Description",
    "priority": "medium",
    "status": "todo"
  },
  "invalidTask": {
    "title": "",
    "description": "Test Description"
  },
  "tasks": [
    {
      "id": "1",
      "title": "Task 1",
      "description": "Description 1",
      "priority": "high",
      "status": "in-progress"
    },
    {
      "id": "2",
      "title": "Task 2",
      "description": "Description 2",
      "priority": "medium",
      "status": "done"
    }
  ]
}

// cypress/support/helpers/test-data.ts
export class TestDataHelper {
  static getValidTask() {
    return cy.fixture('tasks').then((data) => data.validTask);
  }

  static getInvalidTask() {
    return cy.fixture('tasks').then((data) => data.invalidTask);
  }

  static getTasks() {
    return cy.fixture('tasks').then((data) => data.tasks);
  }

  static generateRandomTask() {
    const timestamp = Date.now();
    return {
      title: `Task ${timestamp}`,
      description: `Description ${timestamp}`,
      priority: 'medium',
      status: 'todo'
    };
  }
}
```

## Test Examples

### 1. Basic Task Management Tests
```typescript
// cypress/e2e/tasks/task-management.cy.ts
import { TasksPage } from '../../support/pages/tasks-page';

describe('Task Management', () => {
  const tasksPage = new TasksPage();

  beforeEach(() => {
    cy.login('test@example.com', 'password');
    tasksPage.visit();
  });

  it('should create a new task', () => {
    const task = TestDataHelper.generateRandomTask();
    
    tasksPage
      .clickAddTask()
      .fillTaskForm(task.title, task.description)
      .saveTask()
      .shouldShowTask(task.title);
  });

  it('should display validation errors for invalid task', () => {
    const invalidTask = TestDataHelper.getInvalidTask();
    
    tasksPage.clickAddTask();
    cy.get('[data-testid="task-title-input"]').type(invalidTask.title);
    cy.get('[data-testid="save-task-button"]').click();
    
    cy.get('[data-testid="title-error"]').should('be.visible');
    cy.get('[data-testid="title-error"]').should('contain', 'Title is required');
  });

  it('should delete a task', () => {
    const task = TestDataHelper.generateRandomTask();
    
    cy.createTask(task.title, task.description);
    cy.deleteTask(task.title);
  });

  it('should filter tasks by status', () => {
    cy.get('[data-testid="status-filter"]').click();
    cy.get('[data-value="in-progress"]').click();
    
    cy.get('[data-testid="task-list"] .task-item').each(($task) => {
      cy.wrap($task).should('contain', 'In Progress');
    });
  });
});
```

### 2. User Flow Tests
```typescript
// cypress/e2e/user-flows/complete-task-workflow.cy.ts
describe('Complete Task Workflow', () => {
  beforeEach(() => {
    cy.login('test@example.com', 'password');
  });

  it('should complete full task lifecycle', () => {
    // 1. Create task
    cy.visit('/tasks');
    cy.createTask('Complete Workflow Task', 'Test description');
    
    // 2. Edit task
    cy.contains('Complete Workflow Task').click();
    cy.get('[data-testid="edit-task-button"]').click();
    cy.get('[data-testid="task-title-input"]').clear().type('Updated Task Title');
    cy.get('[data-testid="save-task-button"]').click();
    
    // 3. Change status
    cy.get('[data-testid="status-select"]').click();
    cy.get('[data-value="in-progress"]').click();
    cy.get('[data-testid="task-list"]').should('contain', 'In Progress');
    
    // 4. Add comment
    cy.get('[data-testid="add-comment-input"]').type('Test comment');
    cy.get('[data-testid="add-comment-button"]').click();
    cy.get('[data-testid="comments-list"]').should('contain', 'Test comment');
    
    // 5. Mark as complete
    cy.get('[data-testid="status-select"]').click();
    cy.get('[data-value="done"]').click();
    cy.get('[data-testid="task-list"]').should('contain', 'Done');
  });
});
```

### 3. API Integration Tests
```typescript
// cypress/e2e/api/tasks-api.cy.ts
describe('Tasks API', () => {
  beforeEach(() => {
    cy.login('test@example.com', 'password');
  });

  it('should load tasks from API', () => {
    cy.intercept('GET', '/api/tasks', { fixture: 'tasks.json' }).as('getTasks');
    
    cy.visit('/tasks');
    cy.waitForApi('getTasks');
    
    cy.get('[data-testid="task-list"] .task-item').should('have.length', 2);
  });

  it('should create task via API', () => {
    const newTask = TestDataHelper.generateRandomTask();
    
    cy.intercept('POST', '/api/tasks', {
      statusCode: 201,
      body: { ...newTask, id: '3' }
    }).as('createTask');
    
    cy.visit('/tasks');
    cy.createTask(newTask.title, newTask.description);
    cy.waitForApi('createTask');
  });

  it('should handle API errors gracefully', () => {
    cy.intercept('GET', '/api/tasks', {
      statusCode: 500,
      body: { message: 'Internal Server Error' }
    }).as('getTasksError');
    
    cy.visit('/tasks');
    cy.waitForApi('getTasksError');
    
    cy.get('[data-testid="error-message"]').should('be.visible');
    cy.get('[data-testid="error-message"]').should('contain', 'Failed to load tasks');
  });
});
```

## Configuration

### 1. Cypress Configuration
```typescript
// cypress.config.ts
import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    viewportWidth: 1280,
    viewportHeight: 720,
    video: false,
    screenshotOnRunFailure: true,
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
  component: {
    devServer: {
      framework: 'angular',
      bundler: 'webpack',
    },
    specPattern: 'src/**/*.cy.ts',
  },
});
```

### 2. Environment Configuration
```typescript
// cypress/support/e2e.ts
import './commands';

beforeEach(() => {
  // Reset database state
  cy.exec('npm run db:reset');
  
  // Seed test data
  cy.exec('npm run db:seed');
});

afterEach(() => {
  // Clean up any created data
  cy.exec('npm run db:cleanup');
});
```

## Best Practices

### 1. Test Organization
- Group related tests using `describe()` blocks
- Use descriptive test names that explain the scenario
- Keep tests independent and isolated
- Use data attributes for selectors (`data-testid`)

### 2. Performance
- Use `cy.intercept()` to mock API calls
- Avoid unnecessary waits with `cy.wait()`
- Use `cy.get()` with timeouts instead of `cy.wait()`
- Clean up test data after each test

### 3. Reliability
- Use `cy.get()` with assertions instead of `cy.wait()`
- Handle async operations properly
- Use `cy.contains()` for text-based assertions
- Implement proper error handling

### 4. Maintainability
- Create reusable custom commands
- Use Page Object Model for complex pages
- Keep test data in fixtures
- Use TypeScript for better type safety

## Debugging

### 1. Time Travel
```typescript
it('should debug with time travel', () => {
  cy.visit('/tasks');
  cy.pause(); // Pause execution
  cy.get('[data-testid="add-task-button"]').click();
  cy.debug(); // Open DevTools
});
```

### 2. Network Debugging
```typescript
it('should debug network requests', () => {
  cy.intercept('GET', '/api/tasks').as('getTasks');
  cy.visit('/tasks');
  cy.waitForApi('getTasks');
  
  // Log request/response
  cy.get('@getTasks').then((interception) => {
    console.log('Request:', interception.request);
    console.log('Response:', interception.response);
  });
});
```

## Continuous Integration

### 1. GitHub Actions Integration
```yaml
# .github/workflows/e2e.yml
name: E2E Tests
on: [push, pull_request]
jobs:
  e2e:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm ci
      - run: npm run build:client
      - run: npm run start:client &
      - run: npm run e2e:run
      - uses: actions/upload-artifact@v3
        if: failure()
        with:
          name: cypress-screenshots
          path: cypress/screenshots
```

### 2. Parallel Execution
```typescript
// cypress.config.ts
export default defineConfig({
  e2e: {
    // ... other config
    retries: {
      runMode: 2,
      openMode: 0,
    },
  },
});
```

## Common Patterns

### 1. Authentication Handling
```typescript
// cypress/support/auth.ts
export const loginAsUser = (userType: 'admin' | 'user') => {
  const credentials = {
    admin: { email: 'admin@example.com', password: 'admin123' },
    user: { email: 'user@example.com', password: 'user123' }
  };
  
  cy.login(credentials[userType].email, credentials[userType].password);
};
```

### 2. Data Cleanup
```typescript
// cypress/support/cleanup.ts
export const cleanupTestData = () => {
  cy.exec('npm run db:cleanup:test');
};

beforeEach(() => {
  cleanupTestData();
});
```

### 3. Visual Testing
```typescript
// cypress/e2e/visual/task-page.cy.ts
describe('Task Page Visual Tests', () => {
  it('should match task page snapshot', () => {
    cy.login('test@example.com', 'password');
    cy.visit('/tasks');
    cy.wait(1000); // Wait for animations
    cy.matchImageSnapshot('task-page');
  });
});
```

## Resources

- [Cypress Documentation](https://docs.cypress.io/)
- [Cypress Best Practices](https://docs.cypress.io/guides/references/best-practices)
- [Cypress Testing Strategies](https://docs.cypress.io/guides/core-concepts/testing-strategies)
- [Cypress Component Testing](https://docs.cypress.io/guides/component-testing/introduction)

