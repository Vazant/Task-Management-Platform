describe('Basic E2E Test', () => {
  it('should load the application', () => {
    cy.visit('/');
    cy.contains('Task Management Platform').should('be.visible');
  });

  it('should navigate to tasks page', () => {
    cy.visit('/tasks');
    cy.url().should('include', '/tasks');
  });

  it('should have working navigation', () => {
    cy.visit('/');
    cy.get('nav').should('be.visible');
  });
});
