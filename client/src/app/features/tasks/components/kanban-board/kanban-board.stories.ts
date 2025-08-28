import type { Meta, StoryObj } from '@storybook/angular';
import { KanbanBoardComponent } from './kanban-board.component';
import { provideMockStore } from '@ngrx/store/testing';
import { Task } from '@models';

const mockTasks: Task[] = [
  {
    id: '1',
    title: 'Implement user authentication',
    description: 'Add login and registration functionality',
    status: 'backlog',
    priority: 'high',
    assignee: 'john.doe',
    project: 'web-app',
    dueDate: '2025-08-15',
    createdAt: '2025-08-01T10:00:00Z',
    updatedAt: '2025-08-01T10:00:00Z'
  },
  {
    id: '2',
    title: 'Design database schema',
    description: 'Create ERD and implement database tables',
    status: 'in-progress',
    priority: 'medium',
    assignee: 'jane.smith',
    project: 'web-app',
    dueDate: '2025-08-10',
    createdAt: '2025-08-01T09:00:00Z',
    updatedAt: '2025-08-01T11:00:00Z'
  },
  {
    id: '3',
    title: 'Setup CI/CD pipeline',
    description: 'Configure automated testing and deployment',
    status: 'done',
    priority: 'low',
    assignee: 'mike.johnson',
    project: 'web-app',
    dueDate: '2025-08-05',
    createdAt: '2025-08-01T08:00:00Z',
    updatedAt: '2025-08-01T16:00:00Z'
  },
  {
    id: '4',
    title: 'Fix critical bug in payment system',
    description: 'Users cannot complete transactions',
    status: 'blocked',
    priority: 'urgent',
    assignee: 'sarah.wilson',
    project: 'web-app',
    dueDate: '2025-08-02',
    createdAt: '2025-08-01T12:00:00Z',
    updatedAt: '2025-08-01T14:00:00Z'
  }
];

const meta: Meta<KanbanBoardComponent> = {
  title: 'Tasks/Kanban Board',
  component: KanbanBoardComponent,
  parameters: {
    layout: 'fullscreen',
  },
  decorators: [
    provideMockStore({
      initialState: {
        tasks: {
          entities: mockTasks.reduce((acc, task) => {
            acc[task.id] = task;
            return acc;
          }, {} as Record<string, Task>),
          ids: mockTasks.map(task => task.id),
          loading: false,
          error: null,
          filters: {
            status: 'all',
            priority: 'all',
            assignee: 'all',
            project: 'all'
          },
          sortBy: 'created'
        }
      }
    })
  ],
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<KanbanBoardComponent>;

export const Default: Story = {
  args: {},
};

export const Loading: Story = {
  decorators: [
    provideMockStore({
      initialState: {
        tasks: {
          entities: {},
          ids: [],
          loading: true,
          error: null,
          filters: {
            status: 'all',
            priority: 'all',
            assignee: 'all',
            project: 'all'
          },
          sortBy: 'created'
        }
      }
    })
  ],
};

export const Empty: Story = {
  decorators: [
    provideMockStore({
      initialState: {
        tasks: {
          entities: {},
          ids: [],
          loading: false,
          error: null,
          filters: {
            status: 'all',
            priority: 'all',
            assignee: 'all',
            project: 'all'
          },
          sortBy: 'created'
        }
      }
    })
  ],
};

export const WithManyTasks: Story = {
  decorators: [
    provideMockStore({
      initialState: {
        tasks: {
          entities: {
            ...mockTasks.reduce((acc, task) => {
              acc[task.id] = task;
              return acc;
            }, {} as Record<string, Task>),
            // Добавляем больше задач для демонстрации
            '5': {
              id: '5',
              title: 'Optimize database queries',
              description: 'Improve performance of slow queries',
              status: 'in-progress',
              priority: 'medium',
              assignee: 'alex.brown',
              project: 'web-app',
              dueDate: '2025-08-12',
              createdAt: '2025-08-01T13:00:00Z',
              updatedAt: '2025-08-01T15:00:00Z'
            },
            '6': {
              id: '6',
              title: 'Write unit tests',
              description: 'Cover all business logic with tests',
              status: 'backlog',
              priority: 'high',
              assignee: 'emma.davis',
              project: 'web-app',
              dueDate: '2025-08-20',
              createdAt: '2025-08-01T14:00:00Z',
              updatedAt: '2025-08-01T14:00:00Z'
            },
            '7': {
              id: '7',
              title: 'Deploy to staging',
              description: 'Deploy latest changes to staging environment',
              status: 'done',
              priority: 'low',
              assignee: 'tom.lee',
              project: 'web-app',
              dueDate: '2025-08-03',
              createdAt: '2025-08-01T15:00:00Z',
              updatedAt: '2025-08-01T17:00:00Z'
            }
          },
          ids: ['1', '2', '3', '4', '5', '6', '7'],
          loading: false,
          error: null,
          filters: {
            status: 'all',
            priority: 'all',
            assignee: 'all',
            project: 'all'
          },
          sortBy: 'created'
        }
      }
    })
  ],
};
