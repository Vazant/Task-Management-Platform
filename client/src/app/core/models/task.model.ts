export interface Task {
  id: string;
  title: string;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  projectId: string;
  assigneeId?: string;
  creatorId: string;
  labels: string[];
  subtasks: SubTask[];
  timeSpent: number;
  estimatedHours?: number;
  dueDate?: Date;
  createdAt: Date;
  updatedAt: Date;
  blocked?: boolean;
}

export interface TaskLabel {
  name: string;
  color: string;
}

export type TaskStatus = 'backlog' | 'in-progress' | 'done' | 'blocked';
export type TaskPriority = 'low' | 'medium' | 'high' | 'urgent';

export interface SubTask {
  id: string;
  title: string;
  completed: boolean;
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
