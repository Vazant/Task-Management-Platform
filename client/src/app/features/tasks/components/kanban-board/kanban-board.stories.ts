import type { Meta, StoryObj } from '@storybook/angular';
import { KanbanBoardComponent } from './kanban-board.component';
import { Task } from '@models';
import { of } from 'rxjs';

const meta: Meta<KanbanBoardComponent> = {
  title: 'Tasks/KanbanBoard',
  component: KanbanBoardComponent,
  parameters: {
    layout: 'fullscreen',
  },
};

export default meta;
type Story = StoryObj<KanbanBoardComponent>;

const mockTasks: Task[] = [
  {
    id: '1',
    title: 'Создать дизайн',
    description: 'Разработать макет главной страницы',
    status: 'in-progress',
    priority: 'high',
    projectId: '1',
    assigneeId: 'user1',
    creatorId: 'user1',
    labels: ['design', 'frontend'],
    subtasks: [],
    timeSpent: 120,
    estimatedHours: 4,
    dueDate: new Date('2025-08-15'),
    createdAt: new Date('2025-08-01T10:00:00Z'),
    updatedAt: new Date('2025-08-01T10:00:00Z')
  },
  {
    id: '2',
    title: 'Настроить API',
    description: 'Создать REST API endpoints',
    status: 'backlog',
    priority: 'medium',
    projectId: '1',
    assigneeId: 'user2',
    creatorId: 'user1',
    labels: ['backend', 'api'],
    subtasks: [],
    timeSpent: 0,
    estimatedHours: 8,
    dueDate: new Date('2025-08-10'),
    createdAt: new Date('2025-08-01T09:00:00Z'),
    updatedAt: new Date('2025-08-01T11:00:00Z')
  },
  {
    id: '3',
    title: 'Написать тесты',
    description: 'Создать unit и integration тесты',
    status: 'done',
    priority: 'low',
    projectId: '1',
    assigneeId: 'user3',
    creatorId: 'user1',
    labels: ['testing'],
    subtasks: [],
    timeSpent: 180,
    estimatedHours: 3,
    dueDate: new Date('2025-08-05'),
    createdAt: new Date('2025-08-01T08:00:00Z'),
    updatedAt: new Date('2025-08-01T16:00:00Z')
  },
  {
    id: '4',
    title: 'Деплой на продакшн',
    description: 'Развернуть приложение на сервере',
    status: 'blocked',
    priority: 'high',
    projectId: '1',
    assigneeId: 'user1',
    creatorId: 'user1',
    labels: ['deployment'],
    subtasks: [],
    timeSpent: 0,
    estimatedHours: 2,
    dueDate: new Date('2025-08-02'),
    createdAt: new Date('2025-08-01T12:00:00Z'),
    updatedAt: new Date('2025-08-01T14:00:00Z')
  }
];

export const Default: Story = {
  args: {},
};

export const Empty: Story = {
  args: {},
};

export const Loading: Story = {
  args: {
    loading$: of(true),
  },
};
