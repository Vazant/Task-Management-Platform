import { ChangeDetectionStrategy, Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DragDropModule, CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Store } from '@ngrx/store';
import { Observable, Subject, combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { Task, TaskStatus } from '@models';
import { TaskCardComponent } from '../task-card/task-card.component';
import { TaskDialogService } from '../../services/task-dialog.service';
import * as TaskSelectors from '../../store/tasks.selectors';
import * as TaskActions from '../../store/tasks.actions';

interface KanbanColumn {
  status: TaskStatus;
  title: string;
  tasks: Task[];
  color: string;
}

@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [
    CommonModule,
    DragDropModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    TaskCardComponent
  ],
  templateUrl: './kanban-board.component.html',
  styleUrls: ['./kanban-board.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class KanbanBoardComponent implements OnInit, OnDestroy {
  columns$: Observable<KanbanColumn[]>;
  loading$: Observable<boolean>;
  
  private readonly destroy$ = new Subject<void>();
  private readonly store = inject(Store);
  private readonly taskDialogService = inject(TaskDialogService);

  readonly columns: KanbanColumn[] = [
    { status: 'backlog', title: 'Backlog', tasks: [], color: 'accent' },
    { status: 'in-progress', title: 'In Progress', tasks: [], color: 'primary' },
    { status: 'done', title: 'Done', tasks: [], color: 'success' },
    { status: 'blocked', title: 'Blocked', tasks: [], color: 'warn' }
  ];

  constructor() {
    this.loading$ = this.store.select(TaskSelectors.selectTasksLoading);
    
    this.columns$ = combineLatest([
      this.store.select(TaskSelectors.selectAllTasks),
      this.loading$
    ]).pipe(
      map(([tasks, loading]) => {
        if (loading) return this.columns;
        
        return this.columns.map(column => ({
          ...column,
          tasks: tasks.filter(task => task.status === column.status)
        }));
      })
    );
  }

  ngOnInit(): void {
    this.store.dispatch(TaskActions.loadTasks());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onDrop(event: CdkDragDrop<Task[]>): void {
    if (event.previousContainer === event.container) {
      // Перемещение в пределах одной колонки
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      
      // Обновляем порядок задач в NgRx
      const reorderedTasks = event.container.data.map((task, index) => ({
        ...task,
        order: index
      }));
      
      this.store.dispatch(TaskActions.reorderTasks({ tasks: reorderedTasks }));
    } else {
      // Перемещение между колонками
      const task = event.previousContainer.data[event.previousIndex];
      const newStatus = this.getStatusFromContainerId(event.container.id);
      
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      
      // Обновляем статус задачи в NgRx
      this.store.dispatch(TaskActions.updateTask({
        task: {
          id: task.id,
          status: newStatus,
          updatedAt: new Date()
        }
      }));
    }
  }

  onTaskEdit(task: Task): void {
    this.taskDialogService.openEditDialog(task).subscribe(result => {
      if (result) {
        // Задача была обновлена
      }
    });
  }

  onTaskDelete(task: Task): void {
    this.store.dispatch(TaskActions.deleteTask({ taskId: task.id }));
  }

  onTaskStatusChange(event: { task: Task; status: Task['status'] }): void {
    this.store.dispatch(TaskActions.updateTask({
      task: {
        id: event.task.id,
        status: event.status,
        updatedAt: new Date()
      }
    }));
  }

  onTaskPriorityChange(event: { task: Task; priority: Task['priority'] }): void {
    this.store.dispatch(TaskActions.updateTask({
      task: {
        id: event.task.id,
        priority: event.priority,
        updatedAt: new Date()
      }
    }));
  }

  onAddTask(status: TaskStatus): void {
    this.taskDialogService.openQuickCreateDialog(status).subscribe(result => {
      if (result) {
        // Задача была создана
      }
    });
  }

  getConnectedDropLists(): string[] {
    return this.columns.map(col => col.status);
  }

  getStatusIcon(status: TaskStatus): string {
    const icons: Record<TaskStatus, string> = {
      'backlog': 'inbox',
      'pending': 'schedule',
      'in-progress': 'play_circle',
      'done': 'check_circle',
      'blocked': 'block',
      'completed': 'check_circle',
      'todo': 'assignment'
    };
    return icons[status];
  }

  trackByTaskId(index: number, task: Task): string {
    return task.id;
  }

  trackByColumnStatus(index: number, column: KanbanColumn): string {
    return column.status;
  }

  private getStatusFromContainerId(containerId: string): TaskStatus {
    const statusMap: Record<string, TaskStatus> = {
      'backlog': 'backlog',
      'in-progress': 'in-progress',
      'done': 'done',
      'blocked': 'blocked'
    };
    return statusMap[containerId] || 'backlog';
  }
}
