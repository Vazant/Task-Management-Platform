import * as TasksActions from './tasks.actions';

describe('Tasks Actions', () => {
  it('should create loadTasks action', () => {
    const action = TasksActions.loadTasks();
    expect(action.type).toBe('[Tasks] Load Tasks');
  });
});

