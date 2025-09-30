import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from './project.service';
import { Project, CreateProjectRequest, UpdateProjectRequest, ProjectStatus, ProjectPriority } from '../../core/models/project.model';
import { environment } from '../../../../environments/environment';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;

  const mockProject: Project = {
    id: '1',
    name: 'Test Project',
    description: 'Test Description',
    status: ProjectStatus.ACTIVE,
    priority: ProjectPriority.MEDIUM,
    startDate: new Date('2024-01-01'),
    endDate: new Date('2024-12-31'),
    tags: ['test', 'project'],
    color: '#1976d2',
    ownerId: 'user1',
    ownerName: 'Test User',
    teamMembers: [],
    tasksCount: 5,
    completedTasksCount: 2,
    progress: 40,
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-15')
  };

  const mockCreateRequest: CreateProjectRequest = {
    name: 'New Project',
    description: 'New Description',
    priority: ProjectPriority.HIGH,
    startDate: new Date('2024-02-01'),
    endDate: new Date('2024-11-30'),
    tags: ['new', 'project'],
    color: '#ff5722'
  };

  const mockUpdateRequest: UpdateProjectRequest = {
    name: 'Updated Project',
    description: 'Updated Description',
    status: ProjectStatus.COMPLETED,
    priority: ProjectPriority.LOW
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService]
    });
    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getProjects', () => {
    it('should return projects array', () => {
      const mockProjects = [mockProject];

      service.getProjects().subscribe(projects => {
        expect(projects).toEqual(mockProjects);
        expect(projects.length).toBe(1);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/projects`);
      expect(req.request.method).toBe('GET');
      req.flush(mockProjects);
    });

    it('should return projects with filters', () => {
      const mockProjects = [mockProject];
      const filters = {
        status: ProjectStatus.ACTIVE,
        priority: ProjectPriority.MEDIUM
      };

      service.getProjects(filters).subscribe(projects => {
        expect(projects).toEqual(mockProjects);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/projects?status=ACTIVE&priority=MEDIUM`);
      expect(req.request.method).toBe('GET');
      req.flush(mockProjects);
    });

    it('should handle error', () => {
      service.getProjects().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Failed to load projects');
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/projects`);
      req.flush('Server Error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getProject', () => {
    it('should return single project', () => {
      service.getProject('1').subscribe(project => {
        expect(project).toEqual(mockProject);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/projects/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockProject);
    });
  });

  describe('createProject', () => {
    it('should create new project', () => {
      service.createProject(mockCreateRequest).subscribe(project => {
        expect(project).toEqual(mockProject);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/projects`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockCreateRequest);
      req.flush(mockProject);
    });
  });

  describe('updateProject', () => {
    it('should update project', () => {
      const updatedProject = { ...mockProject, name: 'Updated Project' };

      service.updateProject('1', mockUpdateRequest).subscribe(project => {
        expect(project.name).toBe('Updated Project');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/projects/1`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(mockUpdateRequest);
      req.flush(updatedProject);
    });
  });

  describe('deleteProject', () => {
    it('should delete project', () => {
      service.deleteProject('1').subscribe(() => {
        expect(true).toBe(true);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/projects/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('searchProjects', () => {
    it('should search projects', () => {
      const mockProjects = [mockProject];
      const query = 'test';

      service.searchProjects(query).subscribe(projects => {
        expect(projects).toEqual(mockProjects);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/projects/search?q=test`);
      expect(req.request.method).toBe('GET');
      req.flush(mockProjects);
    });
  });

  describe('getProjectsStatistics', () => {
    it('should return statistics', () => {
      const mockStats = {
        total: 10,
        active: 5,
        completed: 3,
        archived: 2,
        completionRate: 30
      };

      service.getProjectsStatistics().subscribe(stats => {
        expect(stats).toEqual(mockStats);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/projects/statistics`);
      expect(req.request.method).toBe('GET');
      req.flush(mockStats);
    });
  });
});
