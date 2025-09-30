export interface Project {
  id: string;
  name: string;
  description?: string;
  status: ProjectStatus;
  priority: ProjectPriority;
  startDate?: Date;
  endDate?: Date;
  tags?: string[];
  color?: string;
  ownerId: string;
  ownerName?: string;
  teamMembers?: ProjectMember[];
  tasksCount?: number;
  completedTasksCount?: number;
  progress?: number;
  createdAt: Date;
  updatedAt: Date;
}

export interface ProjectMember {
  id: string;
  name: string;
  email: string;
  role: ProjectRole;
  avatar?: string;
  joinedAt: Date;
}

export interface CreateProjectRequest {
  name: string;
  description?: string;
  priority: ProjectPriority;
  startDate?: Date;
  endDate?: Date;
  tags?: string[];
  color?: string;
  teamMemberIds?: string[];
}

export interface UpdateProjectRequest {
  name?: string;
  description?: string;
  status?: ProjectStatus;
  priority?: ProjectPriority;
  startDate?: Date;
  endDate?: Date;
  tags?: string[];
  color?: string;
  teamMemberIds?: string[];
}

export interface ProjectFilters {
  status?: ProjectStatus;
  priority?: ProjectPriority;
  ownerId?: string;
  tags?: string[];
  dateRange?: {
    start: Date;
    end: Date;
  };
}

export interface ProjectStatistics {
  total: number;
  active: number;
  archived: number;
  completed: number;
  completionRate: number;
}

export enum ProjectStatus {
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  ARCHIVED = 'ARCHIVED',
  ON_HOLD = 'ON_HOLD'
}

export enum ProjectPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT'
}

export enum ProjectRole {
  OWNER = 'OWNER',
  ADMIN = 'ADMIN',
  MEMBER = 'MEMBER',
  VIEWER = 'VIEWER'
}

// Utility types
export type ProjectStatusType = keyof typeof ProjectStatus;
export type ProjectPriorityType = keyof typeof ProjectPriority;
export type ProjectRoleType = keyof typeof ProjectRole;

// Constants
export const PROJECT_STATUS_LABELS: Record<ProjectStatus, string> = {
  [ProjectStatus.ACTIVE]: 'Активный',
  [ProjectStatus.COMPLETED]: 'Завершен',
  [ProjectStatus.ARCHIVED]: 'Архивный',
  [ProjectStatus.ON_HOLD]: 'Приостановлен'
};

export const PROJECT_PRIORITY_LABELS: Record<ProjectPriority, string> = {
  [ProjectPriority.LOW]: 'Низкий',
  [ProjectPriority.MEDIUM]: 'Средний',
  [ProjectPriority.HIGH]: 'Высокий',
  [ProjectPriority.URGENT]: 'Срочный'
};

export const PROJECT_ROLE_LABELS: Record<ProjectRole, string> = {
  [ProjectRole.OWNER]: 'Владелец',
  [ProjectRole.ADMIN]: 'Администратор',
  [ProjectRole.MEMBER]: 'Участник',
  [ProjectRole.VIEWER]: 'Наблюдатель'
};

export const PROJECT_STATUS_COLORS: Record<ProjectStatus, string> = {
  [ProjectStatus.ACTIVE]: '#4CAF50',
  [ProjectStatus.COMPLETED]: '#2196F3',
  [ProjectStatus.ARCHIVED]: '#9E9E9E',
  [ProjectStatus.ON_HOLD]: '#FF9800'
};

export const PROJECT_PRIORITY_COLORS: Record<ProjectPriority, string> = {
  [ProjectPriority.LOW]: '#4CAF50',
  [ProjectPriority.MEDIUM]: '#FF9800',
  [ProjectPriority.HIGH]: '#F44336',
  [ProjectPriority.URGENT]: '#E91E63'
};