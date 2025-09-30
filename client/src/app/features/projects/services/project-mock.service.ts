import { Injectable } from '@angular/core';
import { Observable, of, throwError, delay } from 'rxjs';
import { Project, CreateProjectRequest, UpdateProjectRequest, ProjectFilters, ProjectStatistics, ProjectHistoryEntry } from '../../core/models/project.model';
import { ProjectStatus, ProjectPriority } from '../../core/models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectMockService {
  private projects: Project[] = [
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
    },
    {
      id: '4',
      name: 'API Integration',
      description: 'Third-party API integration and microservices architecture',
      status: ProjectStatus.ACTIVE,
      priority: ProjectPriority.HIGH,
      startDate: new Date('2024-01-01'),
      endDate: new Date('2024-05-15'),
      tags: ['api', 'microservices', 'integration'],
      color: '#f44336',
      ownerId: 'user5',
      ownerName: 'Charlie Wilson',
      teamMembers: [],
      tasksCount: 8,
      completedTasksCount: 2,
      progress: 25,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-02-01')
    },
    {
      id: '5',
      name: 'Legacy System Migration',
      description: 'Migration from legacy system to modern architecture',
      status: ProjectStatus.ON_HOLD,
      priority: ProjectPriority.MEDIUM,
      startDate: new Date('2023-12-01'),
      endDate: new Date('2024-04-30'),
      tags: ['migration', 'legacy', 'modernization'],
      color: '#9e9e9e',
      ownerId: 'user6',
      ownerName: 'David Lee',
      teamMembers: [],
      tasksCount: 25,
      completedTasksCount: 5,
      progress: 20,
      createdAt: new Date('2023-12-01'),
      updatedAt: new Date('2024-01-15')
    }
  ];

  private statistics: ProjectStatistics = {
    total: 5,
    active: 3,
    completed: 1,
    archived: 0,
    onHold: 1,
    completionRate: 20,
    averageProgress: 44.6,
    overdueProjects: 0,
    upcomingDeadlines: 2
  };

  private history: ProjectHistoryEntry[] = [
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

  // Simulate network delay
  private simulateDelay(): Observable<any> {
    return of(null).pipe(delay(Math.random() * 1000 + 500));
  }

  // Simulate error (10% chance)
  private shouldSimulateError(): boolean {
    return Math.random() < 0.1;
  }

  getProjects(filters?: ProjectFilters): Observable<Project[]> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to load projects'));
        }

        let filteredProjects = [...this.projects];

        if (filters) {
          if (filters.status) {
            filteredProjects = filteredProjects.filter(p => p.status === filters.status);
          }
          if (filters.priority) {
            filteredProjects = filteredProjects.filter(p => p.priority === filters.priority);
          }
          if (filters.ownerId) {
            filteredProjects = filteredProjects.filter(p => p.ownerId === filters.ownerId);
          }
          if (filters.tags && filters.tags.length > 0) {
            filteredProjects = filteredProjects.filter(p => 
              p.tags && p.tags.some(tag => filters.tags!.includes(tag))
            );
          }
          if (filters.dateRange) {
            filteredProjects = filteredProjects.filter(p => {
              const projectDate = new Date(p.createdAt);
              return projectDate >= filters.dateRange!.start && projectDate <= filters.dateRange!.end;
            });
          }
        }

        return of(filteredProjects);
      }
    );
  }

  getProject(id: string): Observable<Project> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to load project'));
        }

        const project = this.projects.find(p => p.id === id);
        if (!project) {
          return throwError(() => new Error('Project not found'));
        }

        return of(project);
      }
    );
  }

  createProject(request: CreateProjectRequest): Observable<Project> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to create project'));
        }

        const newProject: Project = {
          id: (this.projects.length + 1).toString(),
          name: request.name,
          description: request.description,
          status: ProjectStatus.ACTIVE,
          priority: request.priority,
          startDate: request.startDate,
          endDate: request.endDate,
          tags: request.tags || [],
          color: request.color || '#1976d2',
          ownerId: 'user1', // Mock current user
          ownerName: 'Current User',
          teamMembers: [],
          tasksCount: 0,
          completedTasksCount: 0,
          progress: 0,
          createdAt: new Date(),
          updatedAt: new Date()
        };

        this.projects.push(newProject);
        this.updateStatistics();

        return of(newProject);
      }
    );
  }

  updateProject(id: string, request: UpdateProjectRequest): Observable<Project> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to update project'));
        }

        const projectIndex = this.projects.findIndex(p => p.id === id);
        if (projectIndex === -1) {
          return throwError(() => new Error('Project not found'));
        }

        const updatedProject = {
          ...this.projects[projectIndex],
          ...request,
          updatedAt: new Date()
        };

        this.projects[projectIndex] = updatedProject;
        this.updateStatistics();

        return of(updatedProject);
      }
    );
  }

  deleteProject(id: string): Observable<void> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to delete project'));
        }

        const projectIndex = this.projects.findIndex(p => p.id === id);
        if (projectIndex === -1) {
          return throwError(() => new Error('Project not found'));
        }

        this.projects.splice(projectIndex, 1);
        this.updateStatistics();

        return of(void 0);
      }
    );
  }

  searchProjects(query: string): Observable<Project[]> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to search projects'));
        }

        const filteredProjects = this.projects.filter(p => 
          p.name.toLowerCase().includes(query.toLowerCase()) ||
          p.description?.toLowerCase().includes(query.toLowerCase()) ||
          p.tags?.some(tag => tag.toLowerCase().includes(query.toLowerCase()))
        );

        return of(filteredProjects);
      }
    );
  }

  getProjectsStatistics(): Observable<ProjectStatistics> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to load statistics'));
        }

        return of(this.statistics);
      }
    );
  }

  getProjectsPaginated(page: number, size: number, filters?: ProjectFilters): Observable<{
    projects: Project[];
    totalElements: number;
    totalPages: number;
    currentPage: number;
  }> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to load paginated projects'));
        }

        let filteredProjects = [...this.projects];

        if (filters) {
          if (filters.status) {
            filteredProjects = filteredProjects.filter(p => p.status === filters.status);
          }
          if (filters.priority) {
            filteredProjects = filteredProjects.filter(p => p.priority === filters.priority);
          }
          if (filters.ownerId) {
            filteredProjects = filteredProjects.filter(p => p.ownerId === filters.ownerId);
          }
          if (filters.tags && filters.tags.length > 0) {
            filteredProjects = filteredProjects.filter(p => 
              p.tags && p.tags.some(tag => filters.tags!.includes(tag))
            );
          }
        }

        const startIndex = page * size;
        const endIndex = startIndex + size;
        const paginatedProjects = filteredProjects.slice(startIndex, endIndex);
        const totalPages = Math.ceil(filteredProjects.length / size);

        return of({
          projects: paginatedProjects,
          totalElements: filteredProjects.length,
          totalPages,
          currentPage: page
        });
      }
    );
  }

  getProjectsByTags(tags: string[]): Observable<Project[]> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to load projects by tags'));
        }

        const filteredProjects = this.projects.filter(p => 
          p.tags && p.tags.some(tag => tags.includes(tag))
        );

        return of(filteredProjects);
      }
    );
  }

  getProjectsByDateRange(startDate: Date, endDate: Date): Observable<Project[]> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to load projects by date range'));
        }

        const filteredProjects = this.projects.filter(p => {
          const projectDate = new Date(p.createdAt);
          return projectDate >= startDate && projectDate <= endDate;
        });

        return of(filteredProjects);
      }
    );
  }

  duplicateProject(projectId: string, newName?: string): Observable<Project> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to duplicate project'));
        }

        const originalProject = this.projects.find(p => p.id === projectId);
        if (!originalProject) {
          return throwError(() => new Error('Project not found'));
        }

        const duplicatedProject: Project = {
          ...originalProject,
          id: (this.projects.length + 1).toString(),
          name: newName || `${originalProject.name} (Copy)`,
          createdAt: new Date(),
          updatedAt: new Date()
        };

        this.projects.push(duplicatedProject);
        this.updateStatistics();

        return of(duplicatedProject);
      }
    );
  }

  archiveProject(projectId: string): Observable<Project> {
    return this.updateProject(projectId, { status: ProjectStatus.ARCHIVED });
  }

  unarchiveProject(projectId: string): Observable<Project> {
    return this.updateProject(projectId, { status: ProjectStatus.ACTIVE });
  }

  changeProjectStatus(projectId: string, status: ProjectStatus): Observable<Project> {
    return this.updateProject(projectId, { status });
  }

  changeProjectPriority(projectId: string, priority: ProjectPriority): Observable<Project> {
    return this.updateProject(projectId, { priority });
  }

  getProjectHistory(projectId: string): Observable<ProjectHistoryEntry[]> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to load project history'));
        }

        const projectHistory = this.history.filter(h => h.projectId === projectId);
        return of(projectHistory);
      }
    );
  }

  exportProjectsToCSV(filters?: ProjectFilters): Observable<Blob> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to export projects'));
        }

        // Create CSV content
        const csvContent = this.createCSVContent(filters);
        const blob = new Blob([csvContent], { type: 'text/csv' });
        
        return of(blob);
      }
    );
  }

  importProjectsFromCSV(file: File): Observable<{ success: number; errors: any[] }> {
    return this.simulateDelay().pipe(
      delay(0),
      () => {
        if (this.shouldSimulateError()) {
          return throwError(() => new Error('Failed to import projects'));
        }

        // Mock import result
        return of({
          success: 3,
          errors: []
        });
      }
    );
  }

  private updateStatistics(): void {
    this.statistics.total = this.projects.length;
    this.statistics.active = this.projects.filter(p => p.status === ProjectStatus.ACTIVE).length;
    this.statistics.completed = this.projects.filter(p => p.status === ProjectStatus.COMPLETED).length;
    this.statistics.archived = this.projects.filter(p => p.status === ProjectStatus.ARCHIVED).length;
    this.statistics.onHold = this.projects.filter(p => p.status === ProjectStatus.ON_HOLD).length;
    this.statistics.completionRate = this.statistics.total > 0 ? 
      (this.statistics.completed / this.statistics.total) * 100 : 0;
    this.statistics.averageProgress = this.projects.length > 0 ? 
      this.projects.reduce((sum, p) => sum + (p.progress || 0), 0) / this.projects.length : 0;
  }

  private createCSVContent(filters?: ProjectFilters): string {
    let filteredProjects = [...this.projects];

    if (filters) {
      if (filters.status) {
        filteredProjects = filteredProjects.filter(p => p.status === filters.status);
      }
      if (filters.priority) {
        filteredProjects = filteredProjects.filter(p => p.priority === filters.priority);
      }
    }

    const headers = ['ID', 'Name', 'Description', 'Status', 'Priority', 'Start Date', 'End Date', 'Progress'];
    const rows = filteredProjects.map(project => [
      project.id,
      project.name,
      project.description || '',
      project.status,
      project.priority,
      project.startDate?.toISOString().split('T')[0] || '',
      project.endDate?.toISOString().split('T')[0] || '',
      project.progress || 0
    ]);

    const csvContent = [headers, ...rows]
      .map(row => row.map(field => `"${field}"`).join(','))
      .join('\n');

    return csvContent;
  }
}
