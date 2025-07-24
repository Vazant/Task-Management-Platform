export interface Activity {
  id: string;
  type: 'task_created' | 'task_moved' | 'task_completed' | 'time_logged' | 'comment_added';
  userId: string; // ref: User
  projectId: string; // ref: Project
  taskId?: string; // ref: Task
  data: any; // дополнительные данные
  createdAt: Date;
}

export interface TimeEntry {
  id: string;
  taskId: string; // ref: Task
  userId: string; // ref: User
  startTime: Date;
  endTime?: Date;
  duration: number; // в минутах
  description?: string;
} 