import { TasksPage } from '../../support/pages/tasks-page';
import { TestDataHelper } from '../../support/helpers/test-data';

describe('Task Management', () => {
  const tasksPage = new TasksPage();

  beforeEach(() => {
    // Mock login for now - in real app this would be actual login
    cy.visit('/tasks');
  });

  it('should display tasks page', () => {
    cy.visit('/tasks');
    cy.get('[data-testid="task-list"]').should('be.visible');
    cy.get('[data-testid="add-task-button"]').should('be.visible');
  });

  it('should show empty state when no tasks', () => {
    cy.visit('/tasks');
    cy.get('[data-testid="task-list"]').should('contain', 'No tasks found');
  });

  it('should create a new task', () => {
    const task = TestDataHelper.generateRandomTask();
    
    tasksPage
      .visit()
      .clickAddTask()
      .fillTaskForm(task.title, task.description)
      .saveTask()
      .shouldShowTask(task.title);
  });

  it('should display validation errors for invalid task', () => {
    tasksPage.visit().clickAddTask();
    
    // Try to save without title
    cy.get('[data-testid="save-task-button"]').click();
    
    cy.get('[data-testid="title-error"]').should('be.visible');
    cy.get('[data-testid="title-error"]').should('contain', 'Title is required');
  });

  it('should filter tasks by status', () => {
    // First create some tasks
    const task1 = TestDataHelper.generateRandomTask();
    const task2 = TestDataHelper.generateRandomTask();
    
    tasksPage.visit();
    cy.createTask(task1.title, task1.description);
    cy.createTask(task2.title, task2.description);
    
    // Filter by status
    tasksPage.filterByStatus('todo');
    
    cy.get('[data-testid="task-list"] .task-item').each(($task) => {
      cy.wrap($task).should('contain', 'Todo');
    });
  });

  it('should search tasks', () => {
    const task = TestDataHelper.generateRandomTask();
    
    tasksPage.visit();
    cy.createTask(task.title, task.description);
    
    // Search for the task
    tasksPage.searchTasks(task.title);
    
    cy.get('[data-testid="task-list"]').should('contain', task.title);
  });

  it('should handle API errors gracefully', () => {
    cy.intercept('GET', '/api/tasks', {
      statusCode: 500,
      body: { message: 'Internal Server Error' }
    }).as('getTasksError');
    
    cy.visit('/tasks');
    cy.wait('@getTasksError');
    
    cy.get('[data-testid="error-message"]').should('be.visible');
    cy.get('[data-testid="error-message"]').should('contain', 'Failed to load tasks');
  });
});

