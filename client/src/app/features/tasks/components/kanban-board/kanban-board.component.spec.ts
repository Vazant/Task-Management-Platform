import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { KanbanBoardComponent } from './kanban-board.component';
import { Task } from '@models';

describe('KanbanBoardComponent', () => {
  let component: KanbanBoardComponent;
  let fixture: ComponentFixture<KanbanBoardComponent>;

  const mockTasks: Task[] = [
    {
      id: '1',
      title: 'Task 1',
      description: 'Description 1',
      status: 'backlog',
      priority: 'high',
      projectId: 'p1',
      creatorId: 'u1',
      timeSpent: 0,
      labels: [],
      subtasks: [],
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: '2',
      title: 'Task 2',
      description: 'Description 2',
      status: 'in-progress',
      priority: 'medium',
      projectId: 'p1',
      creatorId: 'u1',
      timeSpent: 2,
      labels: [],
      subtasks: [],
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: '3',
      title: 'Task 3',
      description: 'Description 3',
      status: 'done',
      priority: 'low',
      projectId: 'p1',
      creatorId: 'u1',
      timeSpent: 5,
      labels: [],
      subtasks: [],
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        KanbanBoardComponent,
        NoopAnimationsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(KanbanBoardComponent);
    component = fixture.componentInstance;
    component.tasks = mockTasks;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should organize tasks by status', () => {
    const organizedColumns = component.organizedColumns;
    
    const backlogColumn = organizedColumns.find(col => col.id === 'backlog');
    const inProgressColumn = organizedColumns.find(col => col.id === 'in-progress');
    const doneColumn = organizedColumns.find(col => col.id === 'done');
    const blockedColumn = organizedColumns.find(col => col.id === 'blocked');

    expect(backlogColumn?.tasks).toHaveLength(1);
    expect(inProgressColumn?.tasks).toHaveLength(1);
    expect(doneColumn?.tasks).toHaveLength(1);
    expect(blockedColumn?.tasks).toHaveLength(0);
  });

  it('should return correct task count for columns', () => {
    const backlogColumn = component.columns.find(col => col.id === 'backlog')!;
    const inProgressColumn = component.columns.find(col => col.id === 'in-progress')!;
    const doneColumn = component.columns.find(col => col.id === 'done')!;
    const blockedColumn = component.columns.find(col => col.id === 'blocked')!;

    expect(component.getColumnTaskCount(backlogColumn)).toBe(1);
    expect(component.getColumnTaskCount(inProgressColumn)).toBe(1);
    expect(component.getColumnTaskCount(doneColumn)).toBe(1);
    expect(component.getColumnTaskCount(blockedColumn)).toBe(0);
  });

  it('should calculate column progress correctly', () => {
    const backlogColumn = component.columns.find(col => col.id === 'backlog')!;
    const inProgressColumn = component.columns.find(col => col.id === 'in-progress')!;
    const doneColumn = component.columns.find(col => col.id === 'done')!;

    expect(component.getColumnProgress(backlogColumn)).toBe(33.33333333333333);
    expect(component.getColumnProgress(inProgressColumn)).toBe(33.33333333333333);
    expect(component.getColumnProgress(doneColumn)).toBe(33.33333333333333);
  });

  it('should return 0 progress for empty board', () => {
    component.tasks = [];
    const backlogColumn = component.columns.find(col => col.id === 'backlog')!;
    
    expect(component.getColumnProgress(backlogColumn)).toBe(0);
  });

  it('should emit taskDrop event when task is moved between columns', () => {
    spyOn(component.taskDrop, 'emit');
    
    const mockDropEvent = {
      previousContainer: { id: 'backlog' },
      container: { id: 'in-progress' },
      previousIndex: 0,
      currentIndex: 0,
      item: { data: mockTasks[0] }
    } as any;

    component.onDrop(mockDropEvent);

    expect(component.taskDrop.emit).toHaveBeenCalledWith({
      taskId: '1',
      newStatus: 'in-progress'
    });
  });

  it('should emit taskEdit event', () => {
    spyOn(component.taskEdit, 'emit');
    
    component.onTaskEdit(mockTasks[0]);

    expect(component.taskEdit.emit).toHaveBeenCalledWith(mockTasks[0]);
  });

  it('should emit taskDelete event', () => {
    spyOn(component.taskDelete, 'emit');
    
    component.onTaskDelete(mockTasks[0]);

    expect(component.taskDelete.emit).toHaveBeenCalledWith(mockTasks[0]);
  });

  it('should emit addTask event', () => {
    spyOn(component.addTask, 'emit');
    
    component.onAddTask('backlog');

    expect(component.addTask.emit).toHaveBeenCalledWith('backlog');
  });

  it('should return connected drop lists', () => {
    const connectedLists = component.getConnectedDropLists();
    
    expect(connectedLists).toEqual(['backlog', 'in-progress', 'done', 'blocked']);
  });

  it('should track tasks by id', () => {
    const result = component.trackByTaskId(0, mockTasks[0]);
    expect(result).toBe('1');
  });

  it('should track columns by id', () => {
    const column = component.columns[0];
    const result = component.trackByColumnId(0, column);
    expect(result).toBe('backlog');
  });

  it('should have correct column configuration', () => {
    expect(component.columns).toHaveLength(4);
    
    const backlogColumn = component.columns.find(col => col.id === 'backlog');
    const inProgressColumn = component.columns.find(col => col.id === 'in-progress');
    const doneColumn = component.columns.find(col => col.id === 'done');
    const blockedColumn = component.columns.find(col => col.id === 'blocked');

    expect(backlogColumn?.title).toBe('Backlog');
    expect(backlogColumn?.color).toBe('#9e9e9e');
    
    expect(inProgressColumn?.title).toBe('In Progress');
    expect(inProgressColumn?.color).toBe('#2196f3');
    
    expect(doneColumn?.title).toBe('Done');
    expect(doneColumn?.color).toBe('#4caf50');
    
    expect(blockedColumn?.title).toBe('Blocked');
    expect(blockedColumn?.color).toBe('#f44336');
  });
});
