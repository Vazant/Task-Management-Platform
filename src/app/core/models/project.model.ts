export interface Project {
  id: string;
  name: string;
  description: string;
  ownerId: string; // ref: User
  members: string[]; // ref: User[]
  settings: ProjectSettings;
  createdAt: Date;
  updatedAt: Date;
}

export interface ProjectSettings {
  allowGuestAccess: boolean;
  defaultTaskPriority: 'low' | 'medium' | 'high' | 'urgent';
  autoAssignTasks: boolean;
  requireTimeTracking: boolean;
} 