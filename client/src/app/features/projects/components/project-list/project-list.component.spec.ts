import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { of, Subject } from 'rxjs';
import { ProjectListComponent } from './project-list.component';
import { Project, ProjectStatus, ProjectPriority } from '../../../../core/models/project.model';
import * as ProjectsActions from '../../store/projects.actions';
import * as ProjectsSelectors from '../../store/projects.selectors';

describe('ProjectListComponent', () => {
  let component: ProjectListComponent;
  let fixture: ComponentFixture<ProjectListComponent>;
  let mockStore: jasmine.SpyObj<Store>;
  let mockDialog: jasmine.SpyObj<MatDialog>;

  const mockProjects: Project[] = [
    {
      id: '1',
      name: 'Test Project 1',
      description: 'Description 1',
      status: ProjectStatus.ACTIVE,
      priority: ProjectPriority.HIGH,
      startDate: new Date('2024-01-01'),
      endDate: new Date('2024-12-31'),
      tags: ['test'],
      color: '#1976d2',
      ownerId: 'user1',
      ownerName: 'User 1',
      teamMembers: [],
      tasksCount: 5,
      completedTasksCount: 2,
      progress: 40,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-15')
    },
    {
      id: '2',
      name: 'Test Project 2',
      description: 'Description 2',
      status: ProjectStatus.COMPLETED,
      priority: ProjectPriority.MEDIUM,
      startDate: new Date('2024-02-01'),
      endDate: new Date('2024-11-30'),
      tags: ['completed'],
      color: '#4caf50',
      ownerId: 'user2',
      ownerName: 'User 2',
      teamMembers: [],
      tasksCount: 3,
      completedTasksCount: 3,
      progress: 100,
      createdAt: new Date('2024-02-01'),
      updatedAt: new Date('2024-02-15')
    }
  ];

  const mockStatistics = {
    total: 2,
    active: 1,
    completed: 1,
    archived: 0,
    completionRate: 50
  };

  beforeEach(async () => {
    const storeSpy = jasmine.createSpyObj('Store', ['select', 'dispatch']);
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [ProjectListComponent],
      providers: [
        { provide: Store, useValue: storeSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectListComponent);
    component = fixture.componentInstance;
    mockStore = TestBed.inject(Store) as jasmine.SpyObj<Store>;
    mockDialog = TestBed.inject(MatDialog) as jasmine.SpyObj<MatDialog>;

    // Setup store selectors
    mockStore.select.and.callFake((selector: any) => {
      if (selector === ProjectsSelectors.selectFilteredProjects) {
        return of(mockProjects);
      }
      if (selector === ProjectsSelectors.selectProjectsLoading) {
        return of(false);
      }
      if (selector === ProjectsSelectors.selectProjectsError) {
        return of(null);
      }
      if (selector === ProjectsSelectors.selectProjectsStatistics) {
        return of(mockStatistics);
      }
      return of(null);
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load projects on init', () => {
    component.ngOnInit();
    expect(mockStore.dispatch).toHaveBeenCalledWith(ProjectsActions.loadProjects());
  });

  it('should dispatch search action when searching', () => {
    const query = 'test';
    component.onSearch(query);
    expect(mockStore.dispatch).toHaveBeenCalledWith(ProjectsActions.searchProjects({ query }));
  });

  it('should dispatch filter action when filtering', () => {
    const filter = 'ACTIVE';
    component.onFilter(filter);
    expect(mockStore.dispatch).toHaveBeenCalledWith(ProjectsActions.filterProjects({ filter }));
  });

  it('should dispatch set selected project action', () => {
    const project = mockProjects[0];
    component.onProjectSelect(project);
    expect(mockStore.dispatch).toHaveBeenCalledWith(ProjectsActions.setSelectedProject({ project }));
  });

  it('should open create dialog', () => {
    const dialogRef = jasmine.createSpyObj('MatDialogRef', ['afterClosed']);
    dialogRef.afterClosed.and.returnValue(of(true));
    mockDialog.open.and.returnValue(dialogRef);

    component.onCreateProject();

    expect(mockDialog.open).toHaveBeenCalled();
  });

  it('should open edit dialog', () => {
    const project = mockProjects[0];
    const dialogRef = jasmine.createSpyObj('MatDialogRef', ['afterClosed']);
    dialogRef.afterClosed.and.returnValue(of(true));
    mockDialog.open.and.returnValue(dialogRef);

    component.onEditProject(project);

    expect(mockDialog.open).toHaveBeenCalled();
  });

  it('should dispatch delete action with confirmation', () => {
    const project = mockProjects[0];
    spyOn(window, 'confirm').and.returnValue(true);

    component.onDeleteProject(project);

    expect(mockStore.dispatch).toHaveBeenCalledWith(ProjectsActions.deleteProject({ id: project.id }));
  });

  it('should not delete project without confirmation', () => {
    const project = mockProjects[0];
    spyOn(window, 'confirm').and.returnValue(false);

    component.onDeleteProject(project);

    expect(mockStore.dispatch).not.toHaveBeenCalledWith(ProjectsActions.deleteProject({ id: project.id }));
  });

  it('should calculate project progress correctly', () => {
    const project = mockProjects[0];
    const progress = component.getProjectProgress(project);
    expect(progress).toBe(40);
  });

  it('should return 0 progress for project with no tasks', () => {
    const project = { ...mockProjects[0], tasksCount: 0 };
    const progress = component.getProjectProgress(project);
    expect(progress).toBe(0);
  });

  it('should return correct status class', () => {
    const project = mockProjects[0];
    const statusClass = component.getProjectStatusClass(project.status);
    expect(statusClass).toBe('project-status--active');
  });

  it('should return correct priority class', () => {
    const project = mockProjects[0];
    const priorityClass = component.getProjectPriorityClass(project.priority);
    expect(priorityClass).toBe('project-priority--high');
  });

  it('should track by project id', () => {
    const project = mockProjects[0];
    const result = component.trackByProjectId(0, project);
    expect(result).toBe(project.id);
  });

  it('should handle archive project', () => {
    const project = mockProjects[0];
    
    // Method should not throw error
    expect(() => component.onArchiveProject(project)).not.toThrow();
  });

  it('should handle duplicate project', () => {
    const project = mockProjects[0];
    
    // Method should not throw error
    expect(() => component.onDuplicateProject(project)).not.toThrow();
  });
});
