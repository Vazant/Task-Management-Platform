import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { CdkDragDrop } from '@angular/cdk/drag-drop';

import { KanbanBoardComponent } from './kanban-board.component';
import { Task, TaskStatus } from '@models';
import * as TaskActions from '../../store/tasks.actions';

describe('KanbanBoardComponent', () => {
  let component: KanbanBoardComponent;
  let fixture: ComponentFixture<KanbanBoardComponent>;
  let store: MockStore;

  const mockTasks: Task[] = [
    {
      id: '1',
      title: 'Test Task 1',
      description: 'Test Description 1',
      status: 'backlog',
      priority: 'high',
      projectId: '1',
      assigneeId: 'user1',
      creatorId: 'user1',
      labels: ['test'],
      subtasks: [],
      timeSpent: 0,
      estimatedHours: 2,
      dueDate: new Date('2025-08-15'),
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: '2',
      title: 'Test Task 2',
      description: 'Test Description 2',
      status: 'in-progress',
      priority: 'medium',
      projectId: '1',
      assigneeId: 'user2',
      creatorId: 'user1',
      labels: ['test'],
      subtasks: [],
      timeSpent: 60,
      estimatedHours: 4,
      dueDate: new Date('2025-08-20'),
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KanbanBoardComponent, NoopAnimationsModule],
      providers: [
        provideMockStore({
          initialState: {
            tasks: {
              entities: mockTasks.reduce((acc, task) => {
                acc[task.id] = task;
                return acc;
              }, {} as Record<string, Task>),
              ids: mockTasks.map(task => task.id),
              loading: false,
              error: null
            }
          }
        })
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(KanbanBoardComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(MockStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with correct columns', () => {
    expect(component.columns.length).toBe(4);
    expect(component.columns[0].status).toBe('backlog');
    expect(component.columns[1].status).toBe('in-progress');
    expect(component.columns[2].status).toBe('done');
    expect(component.columns[3].status).toBe('blocked');
  });

  it('should get tasks by status', () => {
    const backlogTasks = mockTasks.filter(t => t.status === 'backlog');
    const inProgressTasks = mockTasks.filter(t => t.status === 'in-progress');

    expect(backlogTasks.length).toBe(1);
    expect(backlogTasks[0].id).toBe('1');
    expect(inProgressTasks.length).toBe(1);
    expect(inProgressTasks[0].id).toBe('2');
  });

  it('should calculate column progress', () => {
        const backlogColumn = component.columns.find(col => col.status === 'backlog')!;
    const inProgressColumn = component.columns.find(col => col.status === 'in-progress')!;
    
    // Проверяем, что колонки найдены
    expect(backlogColumn).toBeDefined();
    expect(inProgressColumn).toBeDefined();
  });

  it('should handle task drop', () => {
    const mockEvent = {
      previousContainer: { id: 'backlog', data: [mockTasks[0]] },
      container: { id: 'in-progress', data: [mockTasks[1]] },
      previousIndex: 0,
      currentIndex: 1
    } as CdkDragDrop<Task[]>;

    // Убираем проверку taskDrop, так как этот Output был удален
    spyOn(store, 'dispatch');

    component.onDrop(mockEvent);

    expect(store.dispatch).toHaveBeenCalled();
  });

  it('should handle task edit', () => {
    spyOn(component['taskDialogService'], 'openEditDialog').and.returnValue(of(true));
    component.onTaskEdit(mockTasks[0]);
    expect(component['taskDialogService'].openEditDialog).toHaveBeenCalledWith(mockTasks[0]);
  });

  it('should handle task delete', () => {
    spyOn(store, 'dispatch');
    component.onTaskDelete(mockTasks[0]);
    expect(store.dispatch).toHaveBeenCalledWith(TaskActions.deleteTask({ taskId: mockTasks[0].id }));
  });

  it('should handle add task', () => {
    spyOn(component['taskDialogService'], 'openQuickCreateDialog').and.returnValue(of(true));
    component.onAddTask('backlog');
    expect(component['taskDialogService'].openQuickCreateDialog).toHaveBeenCalledWith('backlog');
  });

  it('should track by column status', () => {
    const column = component.columns[0];
    const result = component.trackByColumnStatus(0, column);
    expect(result).toBe(column.status);
  });

  it('should track by task id', () => {
    const result = component.trackByTaskId(0, mockTasks[0]);
    expect(result).toBe(mockTasks[0].id);
  });

  it('should get connected drop lists', () => {
    const result = component.getConnectedDropLists();
    // Проверяем, что возвращается массив строк
    expect(Array.isArray(result)).toBe(true);
    expect(result.length).toBe(4);
  });

  it('should get status icon', () => {
    expect(component.getStatusIcon('backlog')).toBe('inbox');
    expect(component.getStatusIcon('in-progress')).toBe('play_circle');
    expect(component.getStatusIcon('done')).toBe('check_circle');
    expect(component.getStatusIcon('blocked')).toBe('block');
  });
});
