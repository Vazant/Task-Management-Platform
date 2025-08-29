import { TestDataHelper } from '../../support/helpers/test-data';

describe('Complete Task Workflow', () => {
  beforeEach(() => {
    cy.visit('/tasks');
  });

  it('should complete full task lifecycle', () => {
    const task = TestDataHelper.generateRandomTask();
    
    // 1. Create task
    cy.visit('/tasks');
    cy.createTask(task.title, 'Test description');
    
    // 2. Edit task
    cy.contains(task.title).click();
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

  it('should handle task assignment workflow', () => {
    const task = TestDataHelper.generateRandomTask();
    
    // Create task
    cy.createTask(task.title, task.description);
    
    // Assign to user
    cy.contains(task.title).click();
    cy.get('[data-testid="assignee-select"]').click();
    cy.get('[data-value="user1"]').click();
    
    // Verify assignment
    cy.get('[data-testid="task-assignee"]').should('contain', 'User 1');
  });

  it('should handle task priority changes', () => {
    const task = TestDataHelper.generateRandomTask();
    
    // Create task
    cy.createTask(task.title, task.description);
    
    // Change priority
    cy.contains(task.title).click();
    cy.get('[data-testid="priority-select"]').click();
    cy.get('[data-value="high"]').click();
    
    // Verify priority change
    cy.get('[data-testid="task-priority"]').should('contain', 'High');
  });

  it('should handle task deletion workflow', () => {
    const task = TestDataHelper.generateRandomTask();
    
    // Create task
    cy.createTask(task.title, task.description);
    
    // Delete task
    cy.contains(task.title).click();
    cy.get('[data-testid="delete-task-button"]').click();
    cy.get('[data-testid="confirm-delete-button"]').click();
    
    // Verify deletion
    cy.get('[data-testid="task-list"]').should('not.contain', task.title);
  });
});

