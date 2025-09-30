export interface Project {
  id: string;
  name: string;
  description?: string;
  status: ProjectStatus;
  priority: ProjectPriority;
  createdAt: Date;
  updatedAt: Date;
  members?: ProjectMember[];
  tags?: string[];
  startDate?: Date;
  endDate?: Date;
  budget?: number;
  progress: number;
  ownerId: string;
  ownerName?: string;
  teamMembers?: ProjectMember[];
  tasksCount?: number;
  completedTasksCount?: number;
  analytics?: ProjectAnalytics;
  history?: ProjectHistoryEntry[];
  color?: string;
}

export enum ProjectStatus {
  ACTIVE = 'active',
  ARCHIVED = 'archived',
  COMPLETED = 'completed',
  ON_HOLD = 'on-hold'
}

export enum ProjectPriority {
  LOW = 'low',
  MEDIUM = 'medium',
  HIGH = 'high',
  URGENT = 'urgent'
}

export interface ProjectMember {
  id: string;
  name: string;
  email: string;
  role: string;
  avatar?: string;
}

export interface ProjectAnalytics {
  tasksByStatus: { [key: string]: number };
  tasksByPriority: { [key: string]: number };
  tasksByAssignee: { [key: string]: number };
  completionTrend: Array<{ date: Date; completed: number }>;
  resourceUtilization: Array<{ memberId: string; utilization: number }>;
  budgetSpent: number;
  budgetAllocated: number;
}

export interface ProjectHistoryEntry {
  id: string;
  projectId: string;
  action: string;
  description: string;
  userId: string;
  userName: string;
  timestamp: Date;
  changes: Array<{ field: string; oldValue: any; newValue: any }>;
}

export interface CreateProjectRequest {
  name: string;
  description?: string;
  status: ProjectStatus;
  priority: ProjectPriority;
  startDate?: Date;
  endDate?: Date;
  tags?: string[];
  color?: string;
}

export interface UpdateProjectRequest {
  id?: string;
  name?: string;
  description?: string;
  status?: ProjectStatus;
  priority?: ProjectPriority;
  startDate?: Date;
  endDate?: Date;
  tags?: string[];
  color?: string;
}

export interface ProjectFilters {
  status?: string;
  priority?: string;
  tags?: string[];
  ownerId?: string;
  dateRange?: {
    start: Date;
    end: Date;
  };
}

export interface ProjectStatistics {
  total: number;
  active: number;
  completed: number;
  archived: number;
  onHold: number;
  completionRate: number;
  averageProgress: number;
  overdueProjects: number;
  upcomingDeadlines: number;
}