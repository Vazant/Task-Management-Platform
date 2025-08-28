import { tasksReducer, initialState } from './tasks.reducer';
import * as TasksActions from './tasks.actions';
import { Task } from '@models';

describe('Tasks Reducer with Entity Adapter', () => {
  it('should set loading true on loadTasks', () => {
    const state = tasksReducer(initialState, TasksActions.loadTasks());
    expect(state.loading).toBeTrue();
  });

  it('should setAll on loadTasksSuccess', () => {
    const tasks: Task[] = [
      {
        id: '1',
        title: 'A',
        description: '',
        status: 'backlog',
        priority: 'low',
        projectId: 'p1',
        creatorId: 'u1',
        labels: [],
        subtasks: [],
        timeSpent: 0,
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    ];
    const state = tasksReducer(initialState, TasksActions.loadTasksSuccess({ tasks }));
    expect(state.ids.length).toBe(1);
  });

  it('should addOne on createTaskSuccess', () => {
    const task: Task = {
      id: '10',
      title: 'New',
      description: '',
      status: 'backlog',
      priority: 'medium',
      projectId: 'p1',
      creatorId: 'u1',
      labels: [],
      subtasks: [],
      timeSpent: 0,
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    const state = tasksReducer(initialState, TasksActions.createTaskSuccess({ task }));
    expect(state.ids).toContain('10');
  });

  it('should upsertOne on updateTaskSuccess', () => {
    const base: Task = {
      id: '2',
      title: 'Base',
      description: '',
      status: 'backlog',
      priority: 'low',
      projectId: 'p1',
      creatorId: 'u1',
      labels: [],
      subtasks: [],
      timeSpent: 0,
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    const afterLoad = tasksReducer(initialState, TasksActions.loadTasksSuccess({ tasks: [base] }));
    const updated: Task = { ...base, title: 'Updated' };
    const afterUpdate = tasksReducer(afterLoad, TasksActions.updateTaskSuccess({ task: updated }));
    const entity = (afterUpdate.entities as Record<string, Task>)['2'];
    expect(entity.title).toBe('Updated');
  });

  it('should removeOne on deleteTaskSuccess', () => {
    const task: Task = {
      id: '3',
      title: 'To delete',
      description: '',
      status: 'backlog',
      priority: 'low',
      projectId: 'p1',
      creatorId: 'u1',
      labels: [],
      subtasks: [],
      timeSpent: 0,
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    const s1 = tasksReducer(initialState, TasksActions.createTaskSuccess({ task }));
    const s2 = tasksReducer(s1, TasksActions.deleteTaskSuccess({ taskId: '3' }));
    expect(s2.ids).not.toContain('3');
  });
});


