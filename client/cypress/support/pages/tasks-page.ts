export class TasksPage {
  // Selectors
  private readonly taskList = '[data-testid="task-list"]';
  private readonly addTaskButton = '[data-testid="add-task-button"]';
  private readonly taskTitleInput = '[data-testid="task-title-input"]';
  private readonly taskDescriptionInput = '[data-testid="task-description-input"]';
  private readonly saveTaskButton = '[data-testid="save-task-button"]';
  private readonly statusFilter = '[data-testid="status-filter"]';
  private readonly priorityFilter = '[data-testid="priority-filter"]';
  private readonly searchInput = '[data-testid="search-input"]';

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

  filterByStatus(status: string) {
    cy.get(this.statusFilter).click();
    cy.get(`[data-value="${status}"]`).click();
    return this;
  }

  filterByPriority(priority: string) {
    cy.get(this.priorityFilter).click();
    cy.get(`[data-value="${priority}"]`).click();
    return this;
  }

  searchTasks(query: string) {
    cy.get(this.searchInput).type(query);
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

  shouldShowTasksCount(count: number) {
    cy.get(this.taskList).find('.task-item').should('have.length', count);
    return this;
  }

  shouldShowError(message: string) {
    cy.get('[data-testid="error-message"]').should('contain', message);
    return this;
  }
}

