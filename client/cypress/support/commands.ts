/// <reference types="cypress" />

// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

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