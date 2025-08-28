import { selectAllTasks, selectSortedTasks } from './tasks.selectors';
import { TasksState } from './tasks.state';
import { tasksAdapter } from './tasks.reducer';
import { Task } from '@models';

function buildState(tasks: Task[]): TasksState {
  return tasksAdapter.getInitialState({
    loading: false,
    error: null,
    filters: { status: 'all', priority: 'all', assignee: 'all', project: 'all' },
    sortBy: 'created',
  },);
}

describe('Tasks Selectors', () => {
  it('selectAllTasks should return all tasks', () => {
    const tasks: Task[] = [
      {
        id: '1',
        title: 'A',
        description: '',
        status: 'backlog',
        priority: 'low',
        projectId: 'p',
        creatorId: 'u',
        labels: [],
        subtasks: [],
        timeSpent: 0,
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-02'),
      },
      {
        id: '2',
        title: 'B',
        description: '',
        status: 'done',
        priority: 'high',
        projectId: 'p',
        creatorId: 'u',
        labels: [],
        subtasks: [],
        timeSpent: 0,
        createdAt: new Date('2024-01-03'),
        updatedAt: new Date('2024-01-04'),
      },
    ];

    const base: TasksState = tasksAdapter.setAll(tasks, buildState([]));
    const result = selectAllTasks.projector(base);
    expect(result.length).toBe(2);
  });

  it('selectSortedTasks should sort by created desc by default', () => {
    const a: Task = {
      id: '1',
      title: 'A',
      description: '',
      status: 'backlog',
      priority: 'low',
      projectId: 'p',
      creatorId: 'u',
      labels: [],
      subtasks: [],
      timeSpent: 0,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-02'),
    };
    const b: Task = { ...a, id: '2', createdAt: new Date('2024-02-01') };
    tasksAdapter.setAll([a, b], buildState([]));
    const result = selectSortedTasks.projector([a, b], 'created');
    expect(result[0].id).toBe('2');
  });
});


