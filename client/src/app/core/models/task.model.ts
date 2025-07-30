export interface Task {
  id: string;
  title: string;
  description: string;
  status: 'backlog' | 'in-progress' | 'done';
  priority: 'low' | 'medium' | 'high' | 'urgent';
  projectId: string; // ref: Project
  assigneeId?: string; // ref: User
  creatorId: string; // ref: User
  labels: string[]; // ref: Label[]
  subtasks: Subtask[];
  timeSpent: number; // в минутах
  estimatedTime?: number; // в минутах
  dueDate?: Date;
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
  status: 'all' | 'backlog' | 'in-progress' | 'done';
  priority: 'all' | 'low' | 'medium' | 'high' | 'urgent';
  assignee: 'all' | string;
  project: 'all' | string;
}

export type TaskSortOption = 'created' | 'updated' | 'priority' | 'dueDate' | 'title';
