export interface Task {
  id: string;
  title: string;
  description: string;
  status: 'backlog' | 'in-progress' | 'done' | 'blocked';
  priority: 'low' | 'medium' | 'high' | 'urgent';
  projectId: string;
  creatorId: string;
  assigneeId?: string; 
  dueDate?: Date;
  estimatedHours?: number;
  timeSpent: number;
  labels: Array<{ name: string; color: string }>;
  subtasks: Array<{ id: string; title: string; completed: boolean }>;
  createdAt: Date;
  updatedAt: Date;
}

export interface Subtask {
  id: string;
  title: string;
  completed: boolean;
  taskId: string; // ref: Task
  order: number;
}

export interface Label {
  id: string;
  name: string;
  color: string;
  projectId: string; // ref: Project
}

export interface TaskFilters {
  status: 'all' | 'backlog' | 'in-progress' | 'done' | 'blocked';
  priority: 'all' | 'low' | 'medium' | 'high' | 'urgent';
  assignee: 'all' | string;
  project: 'all' | string;
  query?: string;
  dueDateRange?: { from: Date | null; to: Date | null };
  createdDateRange?: { from: Date | null; to: Date | null };
}

export type TaskSortOption = 'created' | 'updated' | 'priority' | 'dueDate' | 'title';

export type TaskStatus = Task['status'];
