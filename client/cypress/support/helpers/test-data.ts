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

  static generateRandomUser() {
    const timestamp = Date.now();
    return {
      email: `user${timestamp}@example.com`,
      password: 'password123',
      name: `User ${timestamp}`
    };
  }
}

