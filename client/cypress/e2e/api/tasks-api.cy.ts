import { TestDataHelper } from '../../support/helpers/test-data';

describe('Tasks API', () => {
  beforeEach(() => {
    cy.visit('/tasks');
  });

  it('should load tasks from API', () => {
    cy.intercept('GET', '/api/tasks', { fixture: 'tasks.json' }).as('getTasks');
    
    cy.visit('/tasks');
    cy.wait('@getTasks');
    
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
    cy.wait('@createTask');
  });

  it('should update task via API', () => {
    const updatedTask = TestDataHelper.generateRandomTask();
    
    cy.intercept('PUT', '/api/tasks/*', {
      statusCode: 200,
      body: { ...updatedTask, id: '1' }
    }).as('updateTask');
    
    cy.visit('/tasks');
    cy.contains('Task 1').click();
    cy.get('[data-testid="edit-task-button"]').click();
    cy.get('[data-testid="task-title-input"]').clear().type(updatedTask.title);
    cy.get('[data-testid="save-task-button"]').click();
    cy.wait('@updateTask');
  });

  it('should delete task via API', () => {
    cy.intercept('DELETE', '/api/tasks/*', {
      statusCode: 204
    }).as('deleteTask');
    
    cy.visit('/tasks');
    cy.contains('Task 1').click();
    cy.get('[data-testid="delete-task-button"]').click();
    cy.get('[data-testid="confirm-delete-button"]').click();
    cy.wait('@deleteTask');
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

  it('should handle network timeout', () => {
    cy.intercept('GET', '/api/tasks', {
      statusCode: 408,
      body: { message: 'Request Timeout' }
    }).as('getTasksTimeout');
    
    cy.visit('/tasks');
    cy.wait('@getTasksTimeout');
    
    cy.get('[data-testid="error-message"]').should('be.visible');
    cy.get('[data-testid="error-message"]').should('contain', 'Request timeout');
  });

  it('should handle unauthorized access', () => {
    cy.intercept('GET', '/api/tasks', {
      statusCode: 401,
      body: { message: 'Unauthorized' }
    }).as('getTasksUnauthorized');
    
    cy.visit('/tasks');
    cy.wait('@getTasksUnauthorized');
    
    cy.url().should('include', '/login');
  });
});

