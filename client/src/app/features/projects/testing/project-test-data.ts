import { Project, ProjectStatus, ProjectPriority } from '../../core/models/project.model';

export const MOCK_PROJECTS: Project[] = [
  {
    id: '1',
    name: 'E-commerce Platform',
    description: 'Development of a modern e-commerce platform with React and Node.js',
    status: ProjectStatus.ACTIVE,
    priority: ProjectPriority.HIGH,
    startDate: new Date('2024-01-15'),
    endDate: new Date('2024-06-30'),
    tags: ['e-commerce', 'react', 'nodejs'],
    color: '#1976d2',
    ownerId: 'user1',
    ownerName: 'John Doe',
    teamMembers: [],
    tasksCount: 15,
    completedTasksCount: 8,
    progress: 53,
    createdAt: new Date('2024-01-15'),
    updatedAt: new Date('2024-02-15')
  },
  {
    id: '2',
    name: 'Mobile App Development',
    description: 'Cross-platform mobile application using React Native',
    status: ProjectStatus.ACTIVE,
    priority: ProjectPriority.MEDIUM,
    startDate: new Date('2024-02-01'),
    endDate: new Date('2024-08-31'),
    tags: ['mobile', 'react-native', 'ios', 'android'],
    color: '#4caf50',
    ownerId: 'user1',
    ownerName: 'John Doe',
    teamMembers: [],
    tasksCount: 12,
    completedTasksCount: 3,
    progress: 25,
    createdAt: new Date('2024-02-01'),
    updatedAt: new Date('2024-02-10')
  },
  {
    id: '3',
    name: 'Data Analytics Dashboard',
    description: 'Business intelligence dashboard with real-time analytics',
    status: ProjectStatus.COMPLETED,
    priority: ProjectPriority.LOW,
    startDate: new Date('2023-10-01'),
    endDate: new Date('2024-01-31'),
    tags: ['analytics', 'dashboard', 'business-intelligence'],
    color: '#ff9800',
    ownerId: 'user3',
    ownerName: 'Bob Johnson',
    teamMembers: [],
    tasksCount: 20,
    completedTasksCount: 20,
    progress: 100,
    createdAt: new Date('2023-10-01'),
    updatedAt: new Date('2024-01-31')
  }
];

export const MOCK_PROJECT_FILTERS = {
  status: ProjectStatus.ACTIVE,
  priority: ProjectPriority.HIGH,
  ownerId: 'user1',
  tags: ['react', 'nodejs'],
  dateRange: {
    start: new Date('2024-01-01'),
    end: new Date('2024-12-31')
  }
};

export const MOCK_PROJECT_STATISTICS = {
  total: 3,
  active: 2,
  completed: 1,
  archived: 0,
  onHold: 0,
  completionRate: 33.33,
  averageProgress: 59.33,
  overdueProjects: 0,
  upcomingDeadlines: 2
};

export const MOCK_PROJECT_HISTORY = [
  {
    id: '1',
    projectId: '1',
    action: 'CREATED',
    description: 'Project created',
    userId: 'user1',
    userName: 'John Doe',
    timestamp: new Date('2024-01-15'),
    changes: []
  },
  {
    id: '2',
    projectId: '1',
    action: 'UPDATED',
    description: 'Project description updated',
    userId: 'user1',
    userName: 'John Doe',
    timestamp: new Date('2024-01-20'),
    changes: [
      {
        field: 'description',
        oldValue: 'Initial description',
        newValue: 'Development of a modern e-commerce platform with React and Node.js'
      }
    ]
  }
];
