import { TestDataHelper } from '../../support/helpers/test-data';

describe('Project Management Workflow', () => {
  let testProject: any;
  let testTasks: any[] = [];

  beforeEach(() => {
    // Clean up before each test
    cy.clearLocalStorage();
    cy.clearCookies();
    
    // Visit the application
    cy.visit('/');
    
    // Login if needed
    cy.login('test@example.com', 'password123');
  });

  afterEach(() => {
    // Clean up test data
    if (testProject) {
      cy.deleteProject(testProject.id);
    }
  });

  it('should complete full project management workflow', () => {
    // 1. Create a new project
    cy.log('Creating new project...');
    testProject = TestDataHelper.generateRandomProject();
    
    cy.visit('/projects');
    cy.get('[data-cy=create-project-btn]').click();
    
    cy.get('[data-cy=project-name-input]').type(testProject.name);
    cy.get('[data-cy=project-description-input]').type(testProject.description);
    cy.get('[data-cy=project-start-date]').type(testProject.startDate);
    cy.get('[data-cy=project-end-date]').type(testProject.endDate);
    cy.get('[data-cy=project-priority]').select(testProject.priority);
    
    cy.get('[data-cy=save-project-btn]').click();
    
    // Verify project creation
    cy.get('[data-cy=project-list]').should('contain', testProject.name);
    cy.get('[data-cy=success-message]').should('be.visible');
    
    // 2. Navigate to project details
    cy.log('Navigating to project details...');
    cy.contains(testProject.name).click();
    cy.url().should('include', '/projects/');
    
    // 3. Create multiple tasks for the project
    cy.log('Creating tasks for the project...');
    testTasks = TestDataHelper.generateRandomTasks(5);
    
    testTasks.forEach((task, index) => {
      cy.get('[data-cy=create-task-btn]').click();
      
      cy.get('[data-cy=task-title-input]').type(task.title);
      cy.get('[data-cy=task-description-input]').type(task.description);
      cy.get('[data-cy=task-priority]').select(task.priority);
      cy.get('[data-cy=task-status]').select(task.status);
      
      if (task.assignee) {
        cy.get('[data-cy=task-assignee]').select(task.assignee);
      }
      
      cy.get('[data-cy=save-task-btn]').click();
      
      // Verify task creation
      cy.get('[data-cy=task-list]').should('contain', task.title);
    });
    
    // 4. Test task management within project
    cy.log('Testing task management...');
    
    // Update first task
    cy.get('[data-cy=task-item]').first().click();
    cy.get('[data-cy=edit-task-btn]').click();
    cy.get('[data-cy=task-title-input]').clear().type('Updated Task Title');
    cy.get('[data-cy=save-task-btn]').click();
    
    // Change task status
    cy.get('[data-cy=task-status-select]').select('IN_PROGRESS');
    cy.get('[data-cy=task-list]').should('contain', 'IN_PROGRESS');
    
    // Add comment to task
    cy.get('[data-cy=add-comment-input]').type('Working on this task');
    cy.get('[data-cy=add-comment-btn]').click();
    cy.get('[data-cy=comments-list]').should('contain', 'Working on this task');
    
    // 5. Test project team management
    cy.log('Testing team management...');
    cy.get('[data-cy=project-team-tab]').click();
    
    // Add team member
    cy.get('[data-cy=add-member-btn]').click();
    cy.get('[data-cy=member-email-input]').type('team@example.com');
    cy.get('[data-cy=member-role-select]').select('DEVELOPER');
    cy.get('[data-cy=add-member-confirm]').click();
    
    // Verify team member addition
    cy.get('[data-cy=team-list]').should('contain', 'team@example.com');
    
    // 6. Test project analytics
    cy.log('Testing project analytics...');
    cy.get('[data-cy=project-analytics-tab]').click();
    
    // Verify analytics data
    cy.get('[data-cy=task-completion-chart]').should('be.visible');
    cy.get('[data-cy=project-progress-bar]').should('be.visible');
    cy.get('[data-cy=team-productivity-metrics]').should('be.visible');
    
    // 7. Test project settings
    cy.log('Testing project settings...');
    cy.get('[data-cy=project-settings-tab]').click();
    
    // Update project settings
    cy.get('[data-cy=project-name-input]').clear().type('Updated Project Name');
    cy.get('[data-cy=project-description-input]').clear().type('Updated project description');
    cy.get('[data-cy=save-settings-btn]').click();
    
    // Verify settings update
    cy.get('[data-cy=success-message]').should('be.visible');
    
    // 8. Test project notifications
    cy.log('Testing project notifications...');
    cy.get('[data-cy=notifications-tab]').click();
    
    // Enable notifications
    cy.get('[data-cy=email-notifications-toggle]').check();
    cy.get('[data-cy=push-notifications-toggle]').check();
    cy.get('[data-cy=save-notifications-btn]').click();
    
    // 9. Test project export
    cy.log('Testing project export...');
    cy.get('[data-cy=export-project-btn]').click();
    cy.get('[data-cy=export-format-select]').select('PDF');
    cy.get('[data-cy=confirm-export-btn]').click();
    
    // Verify export completion
    cy.get('[data-cy=export-success-message]').should('be.visible');
    
    // 10. Test project archiving
    cy.log('Testing project archiving...');
    cy.get('[data-cy=archive-project-btn]').click();
    cy.get('[data-cy=confirm-archive-btn]').click();
    
    // Verify project is archived
    cy.get('[data-cy=project-status]').should('contain', 'ARCHIVED');
  });

  it('should handle project collaboration workflow', () => {
    // Create project
    testProject = TestDataHelper.generateRandomProject();
    cy.createProject(testProject);
    
    // Add multiple team members
    const teamMembers = [
      { email: 'dev1@example.com', role: 'DEVELOPER' },
      { email: 'dev2@example.com', role: 'DEVELOPER' },
      { email: 'manager@example.com', role: 'MANAGER' }
    ];
    
    teamMembers.forEach(member => {
      cy.addTeamMember(testProject.id, member.email, member.role);
    });
    
    // Test real-time collaboration
    cy.get('[data-cy=online-users-indicator]').should('be.visible');
    
    // Test task assignment to team members
    const task = TestDataHelper.generateRandomTask();
    cy.createTask(task.title, task.description);
    
    cy.get('[data-cy=assign-task-btn]').click();
    cy.get('[data-cy=assignee-select]').select('dev1@example.com');
    cy.get('[data-cy=confirm-assignment-btn]').click();
    
    // Verify assignment
    cy.get('[data-cy=task-assignee]').should('contain', 'dev1@example.com');
  });

  it('should handle project deadline management', () => {
    // Create project with deadline
    testProject = TestDataHelper.generateRandomProject();
    testProject.endDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]; // 7 days from now
    
    cy.createProject(testProject);
    
    // Create tasks with different deadlines
    const tasks = [
      { title: 'Urgent Task', deadline: 1 }, // 1 day
      { title: 'Medium Task', deadline: 3 }, // 3 days
      { title: 'Low Priority Task', deadline: 5 } // 5 days
    ];
    
    tasks.forEach(task => {
      cy.createTask(task.title, 'Task description');
      cy.get('[data-cy=task-deadline-input]').type(
        new Date(Date.now() + task.deadline * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
      );
      cy.get('[data-cy=save-task-btn]').click();
    });
    
    // Test deadline notifications
    cy.get('[data-cy=deadline-warnings]').should('be.visible');
    cy.get('[data-cy=urgent-tasks-count]').should('contain', '1');
    
    // Test deadline filtering
    cy.get('[data-cy=filter-by-deadline]').click();
    cy.get('[data-cy=deadline-filter-urgent]').click();
    cy.get('[data-cy=task-list]').should('contain', 'Urgent Task');
    cy.get('[data-cy=task-list]').should('not.contain', 'Medium Task');
  });

  it('should handle project reporting and analytics', () => {
    // Create project with tasks
    testProject = TestDataHelper.generateRandomProject();
    cy.createProject(testProject);
    
    // Create tasks with different statuses
    const taskStatuses = ['TODO', 'IN_PROGRESS', 'DONE', 'CANCELLED'];
    taskStatuses.forEach((status, index) => {
      const task = TestDataHelper.generateRandomTask();
      cy.createTask(task.title, task.description);
      cy.get('[data-cy=task-status-select]').select(status);
      cy.get('[data-cy=save-task-btn]').click();
    });
    
    // Navigate to analytics
    cy.get('[data-cy=project-analytics-tab]').click();
    
    // Test various analytics views
    cy.get('[data-cy=task-status-chart]').should('be.visible');
    cy.get('[data-cy=completion-rate]').should('be.visible');
    cy.get('[data-cy=team-productivity]').should('be.visible');
    
    // Test date range filtering
    cy.get('[data-cy=date-range-picker]').click();
    cy.get('[data-cy=last-30-days]').click();
    cy.get('[data-cy=analytics-chart]').should('be.visible');
    
    // Test export analytics
    cy.get('[data-cy=export-analytics-btn]').click();
    cy.get('[data-cy=export-format-select]').select('CSV');
    cy.get('[data-cy=confirm-export-btn]').click();
    
    // Verify export
    cy.get('[data-cy=export-success-message]').should('be.visible');
  });

  it('should handle project template creation and usage', () => {
    // Create a project template
    cy.visit('/projects/templates');
    cy.get('[data-cy=create-template-btn]').click();
    
    const template = {
      name: 'Software Development Template',
      description: 'Template for software development projects',
      tasks: [
        { title: 'Requirements Analysis', status: 'TODO' },
        { title: 'Design', status: 'TODO' },
        { title: 'Implementation', status: 'TODO' },
        { title: 'Testing', status: 'TODO' },
        { title: 'Deployment', status: 'TODO' }
      ]
    };
    
    cy.get('[data-cy=template-name-input]').type(template.name);
    cy.get('[data-cy=template-description-input]').type(template.description);
    
    // Add template tasks
    template.tasks.forEach(task => {
      cy.get('[data-cy=add-template-task-btn]').click();
      cy.get('[data-cy=template-task-title]').type(task.title);
      cy.get('[data-cy=template-task-status]').select(task.status);
    });
    
    cy.get('[data-cy=save-template-btn]').click();
    
    // Verify template creation
    cy.get('[data-cy=template-list]').should('contain', template.name);
    
    // Use template to create project
    cy.get('[data-cy=use-template-btn]').click();
    
    const projectFromTemplate = {
      name: 'New Project from Template',
      description: 'Project created from template'
    };
    
    cy.get('[data-cy=project-name-input]').type(projectFromTemplate.name);
    cy.get('[data-cy=project-description-input]').type(projectFromTemplate.description);
    cy.get('[data-cy=create-from-template-btn]').click();
    
    // Verify project creation from template
    cy.get('[data-cy=project-list]').should('contain', projectFromTemplate.name);
    
    // Verify all template tasks were created
    template.tasks.forEach(task => {
      cy.get('[data-cy=task-list]').should('contain', task.title);
    });
  });
});
