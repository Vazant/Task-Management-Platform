import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TaskDialogService } from './task-dialog.service';
import { TaskDialogComponent } from '../components/task-dialog/task-dialog.component';
import { Task } from '@models';

describe('TaskDialogService', () => {
  let service: TaskDialogService;
  let mockDialog: jasmine.SpyObj<MatDialog>;

  const mockTask: Task = {
    id: '1',
    title: 'Test Task',
    description: 'Test Description',
    status: 'in-progress',
    priority: 'medium',
    assignee: 'john.doe',
    project: 'test-project',
    dueDate: new Date('2025-08-15'),
    estimatedHours: 4,
    labels: ['bug'],
    createdAt: new Date('2025-08-01T10:00:00Z'),
    updatedAt: new Date('2025-08-01T10:00:00Z')
  };

  beforeEach(() => {
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    
    TestBed.configureTestingModule({
      providers: [
        TaskDialogService,
        { provide: MatDialog, useValue: dialogSpy }
      ]
    });
    
    service = TestBed.inject(TaskDialogService);
    mockDialog = TestBed.inject(MatDialog) as jasmine.SpyObj<MatDialog>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('openCreateDialog', () => {
    it('should open create dialog with default configuration', () => {
      const mockDialogRef = { afterClosed: () => of(true) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      service.openCreateDialog();

      expect(mockDialog.open).toHaveBeenCalledWith(
        TaskDialogComponent,
        {
          width: '600px',
          maxWidth: '90vw',
          maxHeight: '90vh',
          disableClose: true,
          data: {
            mode: 'create',
            initialStatus: undefined
          }
        }
      );
    });

    it('should open create dialog with initial status', () => {
      const mockDialogRef = { afterClosed: () => of(true) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      service.openCreateDialog('in-progress');

      expect(mockDialog.open).toHaveBeenCalledWith(
        TaskDialogComponent,
        {
          width: '600px',
          maxWidth: '90vw',
          maxHeight: '90vh',
          disableClose: true,
          data: {
            mode: 'create',
            initialStatus: 'in-progress'
          }
        }
      );
    });

    it('should return observable from dialog afterClosed', () => {
      const expectedResult = true;
      const mockDialogRef = { afterClosed: () => of(expectedResult) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      const result$ = service.openCreateDialog();

      result$.subscribe(result => {
        expect(result).toBe(expectedResult);
      });
    });
  });

  describe('openEditDialog', () => {
    it('should open edit dialog with task data', () => {
      const mockDialogRef = { afterClosed: () => of(true) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      service.openEditDialog(mockTask);

      expect(mockDialog.open).toHaveBeenCalledWith(
        TaskDialogComponent,
        {
          width: '600px',
          maxWidth: '90vw',
          maxHeight: '90vh',
          disableClose: true,
          data: {
            mode: 'edit',
            task: mockTask
          }
        }
      );
    });

    it('should return observable from dialog afterClosed', () => {
      const expectedResult = false;
      const mockDialogRef = { afterClosed: () => of(expectedResult) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      const result$ = service.openEditDialog(mockTask);

      result$.subscribe(result => {
        expect(result).toBe(expectedResult);
      });
    });
  });

  describe('openQuickCreateDialog', () => {
    it('should open quick create dialog with smaller width', () => {
      const mockDialogRef = { afterClosed: () => of(true) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      service.openQuickCreateDialog('backlog');

      expect(mockDialog.open).toHaveBeenCalledWith(
        TaskDialogComponent,
        {
          width: '500px',
          maxWidth: '90vw',
          maxHeight: '90vh',
          disableClose: true,
          data: {
            mode: 'create',
            initialStatus: 'backlog'
          }
        }
      );
    });

    it('should return observable from dialog afterClosed', () => {
      const expectedResult = true;
      const mockDialogRef = { afterClosed: () => of(expectedResult) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      const result$ = service.openQuickCreateDialog('done');

      result$.subscribe(result => {
        expect(result).toBe(expectedResult);
      });
    });
  });

  describe('dialog configuration', () => {
    it('should use consistent configuration across all dialog types', () => {
      const mockDialogRef = { afterClosed: () => of(true) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      // Test create dialog
      service.openCreateDialog();
      const createCall = mockDialog.open.calls.first();
      expect(createCall.args[1].maxWidth).toBe('90vw');
      expect(createCall.args[1].maxHeight).toBe('90vh');
      expect(createCall.args[1].disableClose).toBe(true);

      // Test edit dialog
      service.openEditDialog(mockTask);
      const editCall = mockDialog.open.calls.mostRecent();
      expect(editCall.args[1].maxWidth).toBe('90vw');
      expect(editCall.args[1].maxHeight).toBe('90vh');
      expect(editCall.args[1].disableClose).toBe(true);

      // Test quick create dialog
      service.openQuickCreateDialog('backlog');
      const quickCreateCall = mockDialog.open.calls.mostRecent();
      expect(quickCreateCall.args[1].maxWidth).toBe('90vw');
      expect(quickCreateCall.args[1].maxHeight).toBe('90vh');
      expect(quickCreateCall.args[1].disableClose).toBe(true);
    });

    it('should use different widths for different dialog types', () => {
      const mockDialogRef = { afterClosed: () => of(true) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      // Create dialog
      service.openCreateDialog();
      expect(mockDialog.open.calls.first().args[1].width).toBe('600px');

      // Edit dialog
      service.openEditDialog(mockTask);
      expect(mockDialog.open.calls.mostRecent().args[1].width).toBe('600px');

      // Quick create dialog
      service.openQuickCreateDialog('backlog');
      expect(mockDialog.open.calls.mostRecent().args[1].width).toBe('500px');
    });
  });

  describe('error handling', () => {
    it('should handle dialog open errors gracefully', () => {
      mockDialog.open.and.throwError('Dialog open error');

      expect(() => {
        service.openCreateDialog();
      }).toThrowError('Dialog open error');
    });

    it('should handle afterClosed errors gracefully', () => {
      const mockDialogRef = { afterClosed: () => of().pipe(() => { throw new Error('AfterClosed error'); }) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      const result$ = service.openCreateDialog();

      result$.subscribe({
        error: (error) => {
          expect(error.message).toBe('AfterClosed error');
        }
      });
    });
  });

  describe('data passing', () => {
    it('should pass correct data structure for create mode', () => {
      const mockDialogRef = { afterClosed: () => of(true) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      service.openCreateDialog('in-progress');

      const callArgs = mockDialog.open.calls.mostRecent().args;
      expect(callArgs[1].data).toEqual({
        mode: 'create',
        initialStatus: 'in-progress'
      });
    });

    it('should pass correct data structure for edit mode', () => {
      const mockDialogRef = { afterClosed: () => of(true) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      service.openEditDialog(mockTask);

      const callArgs = mockDialog.open.calls.mostRecent().args;
      expect(callArgs[1].data).toEqual({
        mode: 'edit',
        task: mockTask
      });
    });

    it('should pass correct data structure for quick create mode', () => {
      const mockDialogRef = { afterClosed: () => of(true) };
      mockDialog.open.and.returnValue(mockDialogRef as any);

      service.openQuickCreateDialog('done');

      const callArgs = mockDialog.open.calls.mostRecent().args;
      expect(callArgs[1].data).toEqual({
        mode: 'create',
        initialStatus: 'done'
      });
    });
  });
});
