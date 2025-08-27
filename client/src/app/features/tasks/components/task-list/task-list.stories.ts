import type { Meta, StoryObj } from '@storybook/angular';
import { TaskListComponent } from './task-list.component';

const meta: Meta<TaskListComponent> = {
  title: 'Features/Tasks/TaskList',
  component: TaskListComponent,
  render: (args: TaskListComponent) => ({ props: args }),
};

export default meta;
type Story = StoryObj<TaskListComponent>;

export const Empty: Story = {
  args: { tasks: [] },
};

export const WithItems: Story = {
  args: {
    tasks: [
      {
        id: '1',
        title: 'Design homepage',
        description: '',
        status: 'in-progress',
        priority: 'high',
        projectId: 'p1',
        creatorId: 'u1',
        labels: [],
        subtasks: [],
        timeSpent: 0,
        createdAt: new Date(),
        updatedAt: new Date(),
      },
      {
        id: '2',
        title: 'Setup API',
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
      },
    ],
  },
};

