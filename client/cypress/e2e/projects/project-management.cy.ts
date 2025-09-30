describe('Project Management E2E', () => {
  beforeEach(() => {
    // Mock API responses
    cy.intercept('GET', '/api/projects', { fixture: 'projects.json' }).as('getProjects');
    cy.intercept('POST', '/api/projects', { fixture: 'project-created.json' }).as('createProject');
    cy.intercept('PUT', '/api/projects/*', { fixture: 'project-updated.json' }).as('updateProject');
    cy.intercept('DELETE', '/api/projects/*', { statusCode: 204 }).as('deleteProject');
    cy.intercept('GET', '/api/projects/statistics', { fixture: 'project-statistics.json' }).as('getStatistics');

    // Login before each test
    cy.login('test@example.com', 'password123');
    cy.visit('/projects');
  });

  describe('Project List View', () => {
    it('should display projects list with statistics', () => {
      cy.wait('@getProjects');
      cy.wait('@getStatistics');

      // Check statistics
      cy.get('[data-cy=project-statistics]').should('be.visible');
      cy.get('[data-cy=stat-total]').should('contain', '5');
      cy.get('[data-cy=stat-active]').should('contain', '3');
      cy.get('[data-cy=stat-completed]').should('contain', '2');

      // Check projects grid
      cy.get('[data-cy=projects-grid]').should('be.visible');
      cy.get('[data-cy=project-card]').should('have.length.at.least', 1);
    });

    it('should filter projects by status', () => {
      cy.wait('@getProjects');

      // Filter by active status
      cy.get('[data-cy=filter-select]').click();
      cy.get('[data-cy=filter-option-active]').click();

      // Verify filtered results
      cy.get('[data-cy=project-card]').should('have.length', 3);
      cy.get('[data-cy=project-status]').each(($status) => {
        cy.wrap($status).should('contain', 'ACTIVE');
      });
    });

    it('should search projects by name', () => {
      cy.wait('@getProjects');

      // Search for specific project
      cy.get('[data-cy=search-input]').type('E-commerce');
      cy.get('[data-cy=search-input]').should('have.value', 'E-commerce');

      // Verify search results
      cy.get('[data-cy=project-card]').should('have.length', 1);
      cy.get('[data-cy=project-name]').should('contain', 'E-commerce');
    });

    it('should display empty state when no projects', () => {
      cy.intercept('GET', '/api/projects', { body: [] }).as('getEmptyProjects');
      cy.visit('/projects');
      cy.wait('@getEmptyProjects');

      cy.get('[data-cy=empty-state]').should('be.visible');
      cy.get('[data-cy=empty-message]').should('contain', 'Нет проектов');
      cy.get('[data-cy=create-project-btn]').should('be.visible');
    });
  });

  describe('Create Project Workflow', () => {
    it('should create new project successfully', () => {
      cy.wait('@getProjects');

      // Click create project button
      cy.get('[data-cy=create-project-btn]').click();

      // Fill project form
      cy.get('[data-cy=project-name]').type('New Test Project');
      cy.get('[data-cy=project-description]').type('This is a test project description');
      cy.get('[data-cy=project-priority]').click();
      cy.get('[data-cy=priority-high]').click();
      cy.get('[data-cy=project-color]').type('#ff5722');

      // Add tags
      cy.get('[data-cy=tag-input]').type('test{enter}');
      cy.get('[data-cy=tag-input]').type('project{enter}');

      // Set dates
      cy.get('[data-cy=start-date]').click();
      cy.get('[data-cy=date-picker]').should('be.visible');
      cy.get('[data-cy=date-today]').click();

      // Submit form
      cy.get('[data-cy=submit-btn]').click();

      // Verify API call
      cy.wait('@createProject');
      cy.wait('@getProjects'); // Refresh list

      // Verify success
      cy.get('[data-cy=success-message]').should('contain', 'Проект успешно создан');
      cy.get('[data-cy=project-card]').should('contain', 'New Test Project');
    });

    it('should show validation errors for invalid form', () => {
      cy.get('[data-cy=create-project-btn]').click();

      // Try to submit empty form
      cy.get('[data-cy=submit-btn]').click();

      // Verify validation errors
      cy.get('[data-cy=name-error]').should('contain', 'Это поле обязательно');
      cy.get('[data-cy=project-name]').should('have.class', 'mat-form-field-invalid');
    });

    it('should cancel project creation', () => {
      cy.get('[data-cy=create-project-btn]').click();
      cy.get('[data-cy=cancel-btn]').click();

      // Verify dialog is closed
      cy.get('[data-cy=project-dialog]').should('not.exist');
    });
  });

  describe('Edit Project Workflow', () => {
    it('should edit existing project', () => {
      cy.wait('@getProjects');

      // Click edit button on first project
      cy.get('[data-cy=project-card]').first().within(() => {
        cy.get('[data-cy=edit-btn]').click();
      });

      // Update project details
      cy.get('[data-cy=project-name]').clear().type('Updated Project Name');
      cy.get('[data-cy=project-description]').clear().type('Updated description');
      cy.get('[data-cy=project-status]').click();
      cy.get('[data-cy=status-completed]').click();

      // Submit changes
      cy.get('[data-cy=submit-btn]').click();

      // Verify API call
      cy.wait('@updateProject');
      cy.wait('@getProjects'); // Refresh list

      // Verify success
      cy.get('[data-cy=success-message]').should('contain', 'Проект успешно обновлен');
      cy.get('[data-cy=project-card]').should('contain', 'Updated Project Name');
    });

    it('should show current project data in edit form', () => {
      cy.wait('@getProjects');

      // Click edit button
      cy.get('[data-cy=project-card]').first().within(() => {
        cy.get('[data-cy=edit-btn]').click();
      });

      // Verify form is pre-filled
      cy.get('[data-cy=project-name]').should('not.be.empty');
      cy.get('[data-cy=project-description]').should('not.be.empty');
    });
  });

  describe('Delete Project Workflow', () => {
    it('should delete project with confirmation', () => {
      cy.wait('@getProjects');

      // Get initial project count
      cy.get('[data-cy=project-card]').then(($cards) => {
        const initialCount = $cards.length;

        // Click delete button
        cy.get('[data-cy=project-card]').first().within(() => {
          cy.get('[data-cy=more-menu]').click();
          cy.get('[data-cy=delete-btn]').click();
        });

        // Confirm deletion
        cy.get('[data-cy=confirm-dialog]').should('be.visible');
        cy.get('[data-cy=confirm-delete]').click();

        // Verify API call
        cy.wait('@deleteProject');
        cy.wait('@getProjects'); // Refresh list

        // Verify project is removed
        cy.get('[data-cy=project-card]').should('have.length', initialCount - 1);
        cy.get('[data-cy=success-message]').should('contain', 'Проект успешно удален');
      });
    });

    it('should cancel project deletion', () => {
      cy.wait('@getProjects');

      // Click delete button
      cy.get('[data-cy=project-card]').first().within(() => {
        cy.get('[data-cy=more-menu]').click();
        cy.get('[data-cy=delete-btn]').click();
      });

      // Cancel deletion
      cy.get('[data-cy=confirm-dialog]').should('be.visible');
      cy.get('[data-cy=cancel-delete]').click();

      // Verify project still exists
      cy.get('[data-cy=project-card]').should('have.length.at.least', 1);
    });
  });

  describe('Project Actions', () => {
    it('should open project details', () => {
      cy.wait('@getProjects');

      // Click on project card
      cy.get('[data-cy=project-card]').first().click();

      // Verify navigation to project details
      cy.url().should('include', '/projects/');
      cy.get('[data-cy=project-details]').should('be.visible');
    });

    it('should show project progress', () => {
      cy.wait('@getProjects');

      // Check progress bar
      cy.get('[data-cy=project-card]').first().within(() => {
        cy.get('[data-cy=progress-bar]').should('be.visible');
        cy.get('[data-cy=progress-text]').should('contain', '%');
      });
    });

    it('should display project tags', () => {
      cy.wait('@getProjects');

      // Check tags display
      cy.get('[data-cy=project-card]').first().within(() => {
        cy.get('[data-cy=project-tags]').should('be.visible');
        cy.get('[data-cy=tag-chip]').should('have.length.at.least', 1);
      });
    });
  });

  describe('Error Handling', () => {
    it('should handle API errors gracefully', () => {
      // Mock API error
      cy.intercept('GET', '/api/projects', { statusCode: 500, body: { message: 'Server Error' } }).as('getProjectsError');
      cy.visit('/projects');
      cy.wait('@getProjectsError');

      // Verify error message
      cy.get('[data-cy=error-message]').should('contain', 'Ошибка загрузки проектов');
      cy.get('[data-cy=retry-btn]').should('be.visible');
    });

    it('should retry on error', () => {
      // Mock initial error then success
      cy.intercept('GET', '/api/projects', { statusCode: 500 }).as('getProjectsError');
      cy.visit('/projects');
      cy.wait('@getProjectsError');

      // Mock success on retry
      cy.intercept('GET', '/api/projects', { fixture: 'projects.json' }).as('getProjectsSuccess');

      // Click retry
      cy.get('[data-cy=retry-btn]').click();
      cy.wait('@getProjectsSuccess');

      // Verify projects are loaded
      cy.get('[data-cy=project-card]').should('have.length.at.least', 1);
    });
  });

  describe('Loading States', () => {
    it('should show loading spinner during data fetch', () => {
      // Mock slow API response
      cy.intercept('GET', '/api/projects', { delay: 1000, fixture: 'projects.json' }).as('getProjectsSlow');
      cy.visit('/projects');

      // Verify loading state
      cy.get('[data-cy=loading-spinner]').should('be.visible');
      cy.get('[data-cy=loading-text]').should('contain', 'Загрузка проектов');

      // Wait for completion
      cy.wait('@getProjectsSlow');
      cy.get('[data-cy=loading-spinner]').should('not.exist');
    });

    it('should disable form during submission', () => {
      cy.intercept('POST', '/api/projects', { delay: 1000, fixture: 'project-created.json' }).as('createProjectSlow');
      cy.get('[data-cy=create-project-btn]').click();

      // Fill form
      cy.get('[data-cy=project-name]').type('Test Project');
      cy.get('[data-cy=submit-btn]').click();

      // Verify form is disabled
      cy.get('[data-cy=project-name]').should('be.disabled');
      cy.get('[data-cy=submit-btn]').should('be.disabled');
      cy.get('[data-cy=submit-spinner]').should('be.visible');

      // Wait for completion
      cy.wait('@createProjectSlow');
    });
  });
});
