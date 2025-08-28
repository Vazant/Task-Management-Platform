import type { Meta, StoryObj } from '@storybook/angular';
import { TaskCardComponent } from './task-card.component';
import { Task } from '@models';

const meta: Meta<TaskCardComponent> = {
  title: 'Features/Tasks/TaskCard',
  component: TaskCardComponent,
  parameters: {
    layout: 'padded',
  },
};

export default meta;
type Story = StoryObj<TaskCardComponent>;

const baseTask: Task = {
  id: '1',
  title: 'Implement user authentication',
  description: 'Add JWT-based authentication to the application',
  status: 'in-progress',
  priority: 'high',
  projectId: 'p1',
  creatorId: 'u1',
  assigneeId: 'u2',
  dueDate: new Date('2024-12-31'),
  estimatedHours: 8,
  timeSpent: 3,
  labels: [
    { name: 'Backend', color: '#3f51b5' },
    { name: 'Security', color: '#f44336' }
  ],
  subtasks: [],
  createdAt: new Date(),
  updatedAt: new Date()
};

export const Default: Story = {
  args: {
    task: baseTask,
    compact: false
  }
};

export const Compact: Story = {
  args: {
    task: baseTask,
    compact: true
  }
};

export const Overdue: Story = {
  args: {
    task: {
      ...baseTask,
      dueDate: new Date('2020-01-01')
    }
  }
};

export const UrgentPriority: Story = {
  args: {
    task: {
      ...baseTask,
      priority: 'urgent'
    }
  }
};

export const BacklogStatus: Story = {
  args: {
    task: {
      ...baseTask,
      status: 'backlog'
    }
  }
};

export const NoAssignee: Story = {
  args: {
    task: {
      ...baseTask,
      assigneeId: undefined
    }
  }
};

export const NoDueDate: Story = {
  args: {
    task: {
      ...baseTask,
      dueDate: undefined
    }
  }
};

export const ManyLabels: Story = {
  args: {
    task: {
      ...baseTask,
      labels: [
        { name: 'Backend', color: '#3f51b5' },
        { name: 'Security', color: '#f44336' },
        { name: 'Feature', color: '#4caf50' },
        { name: 'High Priority', color: '#ff9800' },
        { name: 'API', color: '#9c27b0' }
      ]
    }
  }
};
