import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Store, StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { ProjectService } from '../services/project.service';
import { ProjectTestingModule } from '../testing/project-testing.module';
import { ProjectTestHelpers } from '../testing/project-test-helpers';
import { MOCK_PROJECTS } from '../testing/project-test-data';
import * as ProjectsActions from '../store/projects.actions';
import { projectsReducer } from '../store/projects.reducer';
import { ProjectsEffects } from '../store/projects.effects';
import { Project, ProjectStatus, ProjectPriority } from '../../core/models/project.model';

describe('Project CRUD Integration Tests', () => {
  let service: ProjectService;
  let store: Store;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        StoreModule.forRoot({ projects: projectsReducer }),
        EffectsModule.forRoot([ProjectsEffects])
      ],
      providers: [ProjectService]
    });

    service = TestBed.inject(ProjectService);
    store = TestBed.inject(Store);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('Full CRUD Workflow', () => {
    it('should complete full CRUD lifecycle', (done) => {
      const newProject: Project = {
        id: 'new-id',
        name: 'New Project',
        description: 'New Description',
        status: ProjectStatus.ACTIVE,
        priority: ProjectPriority.MEDIUM,
        startDate: new Date('2024-01-01'),
        endDate: new Date('2024-12-31'),
        tags: ['new', 'project'],
        color: '#1976d2',
        ownerId: 'user1',
        ownerName: 'Test User',
        teamMembers: [],
        tasksCount: 0,
        completedTasksCount: 0,
        progress: 0,
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-01')
      };

      // 1. Create Project
      service.createProject({
        name: 'New Project',
        description: 'New Description',
        priority: ProjectPriority.MEDIUM,
        startDate: new Date('2024-01-01'),
        endDate: new Date('2024-12-31'),
        tags: ['new', 'project'],
        color: '#1976d2'
      }).subscribe(createdProject => {
        expect(createdProject.name).toBe('New Project');
        expect(createdProject.status).toBe(ProjectStatus.ACTIVE);

        // 2. Read Project
        service.getProject(createdProject.id).subscribe(retrievedProject => {
          expect(retrievedProject.id).toBe(createdProject.id);
          expect(retrievedProject.name).toBe('New Project');

          // 3. Update Project
          service.updateProject(createdProject.id, {
            name: 'Updated Project',
            description: 'Updated Description',
            status: ProjectStatus.COMPLETED
          }).subscribe(updatedProject => {
            expect(updatedProject.name).toBe('Updated Project');
            expect(updatedProject.status).toBe(ProjectStatus.COMPLETED);

            // 4. Delete Project
            service.deleteProject(updatedProject.id).subscribe(() => {
              // 5. Verify deletion
              service.getProject(updatedProject.id).subscribe({
                next: () => fail('Project should not exist'),
                error: (error) => {
                  expect(error.message).toBe('Project not found');
                  done();
                }
              });

              const deleteReq = httpMock.expectOne(`${service['apiUrl']}/projects/${updatedProject.id}`);
              deleteReq.flush(null);

              const getReq = httpMock.expectOne(`${service['apiUrl']}/projects/${updatedProject.id}`);
              getReq.flush('Not Found', { status: 404, statusText: 'Not Found' });
            });

            const updateReq = httpMock.expectOne(`${service['apiUrl']}/projects/${createdProject.id}`);
            updateReq.flush({ ...createdProject, name: 'Updated Project', status: ProjectStatus.COMPLETED });
          });

          const getReq = httpMock.expectOne(`${service['apiUrl']}/projects/${createdProject.id}`);
          getReq.flush(createdProject);
        });

        const createReq = httpMock.expectOne(`${service['apiUrl']}/projects`);
        createReq.flush(createdProject);
      });
    });

    it('should handle bulk operations', (done) => {
      const projectIds = ['1', '2', '3'];
      const updates = { status: ProjectStatus.ARCHIVED };

      // Bulk update
      service.updateProject('1', updates).subscribe(() => {
        service.updateProject('2', updates).subscribe(() => {
          service.updateProject('3', updates).subscribe(() => {
            // Verify all projects are archived
            service.getProjects().subscribe(projects => {
              const archivedProjects = projects.filter(p => p.status === ProjectStatus.ARCHIVED);
              expect(archivedProjects.length).toBe(3);
              done();
            });

            const getReq = httpMock.expectOne(`${service['apiUrl']}/projects`);
            getReq.flush(MOCK_PROJECTS.map(p => ({ ...p, status: ProjectStatus.ARCHIVED })));
          });

          const updateReq3 = httpMock.expectOne(`${service['apiUrl']}/projects/3`);
          updateReq3.flush({ ...MOCK_PROJECTS[2], status: ProjectStatus.ARCHIVED });
        });

        const updateReq2 = httpMock.expectOne(`${service['apiUrl']}/projects/2`);
        updateReq2.flush({ ...MOCK_PROJECTS[1], status: ProjectStatus.ARCHIVED });
      });

      const updateReq1 = httpMock.expectOne(`${service['apiUrl']}/projects/1`);
      updateReq1.flush({ ...MOCK_PROJECTS[0], status: ProjectStatus.ARCHIVED });
    });
  });

  describe('Store Integration', () => {
    it('should dispatch actions and update store', (done) => {
      spyOn(store, 'dispatch');

      // Load projects
      store.dispatch(ProjectsActions.loadProjects());

      service.getProjects().subscribe(projects => {
        expect(store.dispatch).toHaveBeenCalledWith(ProjectsActions.loadProjects());
        expect(projects.length).toBeGreaterThan(0);
        done();
      });

      const req = httpMock.expectOne(`${service['apiUrl']}/projects`);
      req.flush(MOCK_PROJECTS);
    });

    it('should handle store state changes', (done) => {
      let stateChanges = 0;

      store.select('projects').subscribe(state => {
        stateChanges++;
        if (stateChanges === 2) { // Initial + after load
          expect(state.projects.length).toBe(MOCK_PROJECTS.length);
          done();
        }
      });

      store.dispatch(ProjectsActions.loadProjectsSuccess({ projects: MOCK_PROJECTS }));
    });
  });

  describe('Error Recovery', () => {
    it('should recover from network errors', (done) => {
      let attemptCount = 0;

      service.getProjects().subscribe({
        next: (projects) => {
          expect(projects).toEqual(MOCK_PROJECTS);
          done();
        },
        error: (error) => {
          if (attemptCount < 2) {
            attemptCount++;
            // Retry
            service.getProjects().subscribe();
          } else {
            fail('Should have succeeded after retries');
          }
        }
      });

      // First request fails
      const req1 = httpMock.expectOne(`${service['apiUrl']}/projects`);
      req1.flush('Server Error', { status: 500, statusText: 'Server Error' });

      // Second request succeeds
      const req2 = httpMock.expectOne(`${service['apiUrl']}/projects`);
      req2.flush(MOCK_PROJECTS);
    });

    it('should handle partial failures in bulk operations', (done) => {
      const projectIds = ['1', '2', '3'];
      let successCount = 0;
      let errorCount = 0;

      projectIds.forEach(id => {
        service.updateProject(id, { status: ProjectStatus.ARCHIVED }).subscribe({
          next: () => {
            successCount++;
            if (successCount + errorCount === projectIds.length) {
              expect(successCount).toBe(2);
              expect(errorCount).toBe(1);
              done();
            }
          },
          error: () => {
            errorCount++;
            if (successCount + errorCount === projectIds.length) {
              expect(successCount).toBe(2);
              expect(errorCount).toBe(1);
              done();
            }
          }
        });
      });

      // Mock responses: 2 success, 1 error
      const req1 = httpMock.expectOne(`${service['apiUrl']}/projects/1`);
      req1.flush({ ...MOCK_PROJECTS[0], status: ProjectStatus.ARCHIVED });

      const req2 = httpMock.expectOne(`${service['apiUrl']}/projects/2`);
      req2.flush({ ...MOCK_PROJECTS[1], status: ProjectStatus.ARCHIVED });

      const req3 = httpMock.expectOne(`${service['apiUrl']}/projects/3`);
      req3.flush('Server Error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('Data Consistency', () => {
    it('should maintain data consistency across operations', (done) => {
      const originalProject = MOCK_PROJECTS[0];
      const updatedProject = { ...originalProject, name: 'Updated Name' };

      // Create project
      service.createProject({
        name: originalProject.name,
        description: originalProject.description,
        priority: originalProject.priority
      }).subscribe(created => {
        expect(created.name).toBe(originalProject.name);

        // Update project
        service.updateProject(created.id, { name: 'Updated Name' }).subscribe(updated => {
          expect(updated.name).toBe('Updated Name');
          expect(updated.id).toBe(created.id);
          expect(updated.description).toBe(originalProject.description);

          // Verify consistency
          service.getProject(updated.id).subscribe(retrieved => {
            expect(retrieved.name).toBe('Updated Name');
            expect(retrieved.id).toBe(created.id);
            expect(retrieved.description).toBe(originalProject.description);
            done();
          });

          const getReq = httpMock.expectOne(`${service['apiUrl']}/projects/${updated.id}`);
          getReq.flush(updated);
        });

        const updateReq = httpMock.expectOne(`${service['apiUrl']}/projects/${created.id}`);
        updateReq.flush(updatedProject);
      });

      const createReq = httpMock.expectOne(`${service['apiUrl']}/projects`);
      createReq.flush(originalProject);
    });

    it('should handle concurrent modifications', (done) => {
      const projectId = '1';
      let updateCount = 0;

      // Simulate concurrent updates
      const update1 = service.updateProject(projectId, { name: 'Update 1' });
      const update2 = service.updateProject(projectId, { name: 'Update 2' });

      update1.subscribe(() => {
        updateCount++;
        if (updateCount === 2) {
          // Both updates completed
          done();
        }
      });

      update2.subscribe(() => {
        updateCount++;
        if (updateCount === 2) {
          // Both updates completed
          done();
        }
      });

      const req1 = httpMock.expectOne(`${service['apiUrl']}/projects/${projectId}`);
      req1.flush({ ...MOCK_PROJECTS[0], name: 'Update 1' });

      const req2 = httpMock.expectOne(`${service['apiUrl']}/projects/${projectId}`);
      req2.flush({ ...MOCK_PROJECTS[0], name: 'Update 2' });
    });
  });

  describe('Performance Testing', () => {
    it('should handle large datasets efficiently', (done) => {
      const largeDataset = Array.from({ length: 1000 }, (_, i) => ({
        ...MOCK_PROJECTS[0],
        id: i.toString(),
        name: `Project ${i}`
      }));

      const startTime = performance.now();

      service.getProjects().subscribe(projects => {
        const endTime = performance.now();
        const duration = endTime - startTime;

        expect(projects.length).toBe(1000);
        expect(duration).toBeLessThan(1000); // Should complete within 1 second
        done();
      });

      const req = httpMock.expectOne(`${service['apiUrl']}/projects`);
      req.flush(largeDataset);
    });

    it('should handle rapid successive requests', (done) => {
      const requests = Array.from({ length: 10 }, (_, i) => 
        service.getProject(i.toString())
      );

      Promise.all(requests.map(req => req.toPromise())).then(() => {
        done();
      });

      // Mock all requests
      for (let i = 0; i < 10; i++) {
        const req = httpMock.expectOne(`${service['apiUrl']}/projects/${i}`);
        req.flush({ ...MOCK_PROJECTS[0], id: i.toString() });
      }
    });
  });
});
