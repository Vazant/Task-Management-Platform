import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable, combineLatest, map } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';

import { Task } from '@core/models/task.model';
import { TaskStatus, TaskPriority } from '@models';
import * as TasksSelectors from '@features/tasks/store/tasks.selectors';
import * as TasksActions from '@features/tasks/store/tasks.actions';
import { TaskMetricsComponent } from '../task-metrics/task-metrics.component';
import { TaskChartsComponent } from '../task-charts/task-charts.component';
import { TeamPerformanceComponent } from '../team-performance/team-performance.component';
import { TimeTrackingComponent } from '../time-tracking/time-tracking.component';
import { ExportReportsComponent } from '../export-reports/export-reports.component';

export interface TaskAnalytics {
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  blockedTasks: number;
  completionRate: number;
  averageCompletionTime: number;
  tasksByPriority: Record<TaskPriority, number>;
  tasksByStatus: Record<TaskStatus, number>;
  tasksByAssignee: Record<string, number>;
  recentActivity: Task[];
  overdueTasks: number;
  upcomingDeadlines: number;
}

@Component({
  selector: 'app-analytics-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatTooltipModule,
    TaskMetricsComponent,
    TaskChartsComponent,
    TeamPerformanceComponent,
    TimeTrackingComponent,
    ExportReportsComponent
  ],
  templateUrl: './analytics-dashboard.component.html',
  styleUrls: ['./analytics-dashboard.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnalyticsDashboardComponent implements OnInit {
  private readonly store = inject(Store);

  readonly loading$ = this.store.select(TasksSelectors.selectTasksLoading);
  readonly error$ = this.store.select(TasksSelectors.selectTasksError);
  readonly analytics$: Observable<TaskAnalytics>;

  constructor() {
    this.analytics$ = this.calculateAnalytics();
  }

  ngOnInit(): void {
    this.store.dispatch(TasksActions.loadTasks());
  }

  private calculateAnalytics(): Observable<TaskAnalytics> {
    return combineLatest([
      this.store.select(TasksSelectors.selectAllTasks),
      this.store.select(TasksSelectors.selectFilteredTasks)
    ]).pipe(
      map(([allTasks, filteredTasks]) => {
        const tasks = filteredTasks.length > 0 ? filteredTasks : allTasks;
        
        const totalTasks = tasks.length;
        const completedTasks = tasks.filter(task => task.status === 'done').length;
        const inProgressTasks = tasks.filter(task => task.status === 'in-progress').length;
        const blockedTasks = tasks.filter(task => task.status === 'blocked').length;
        
        const completionRate = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;
        
        const completedTaskTimes = tasks
          .filter(task => task.status === 'done' && task.updatedAt && task.createdAt)
          .map(task => {
            const created = new Date(task.createdAt);
            const completed = new Date(task.updatedAt);
            return (completed.getTime() - created.getTime()) / (1000 * 60 * 60 * 24); // days
          });
        
        const averageCompletionTime = completedTaskTimes.length > 0 
          ? completedTaskTimes.reduce((sum, time) => sum + time, 0) / completedTaskTimes.length 
          : 0;

        const tasksByPriority = tasks.reduce((acc, task) => {
          acc[task.priority] = (acc[task.priority] || 0) + 1;
          return acc;
        }, {} as Record<TaskPriority, number>);

        const tasksByStatus = tasks.reduce((acc, task) => {
          acc[task.status] = (acc[task.status] || 0) + 1;
          return acc;
        }, {} as Record<TaskStatus, number>);

        const tasksByAssignee = tasks.reduce((acc, task) => {
          if (task.assigneeId) {
            acc[task.assigneeId] = (acc[task.assigneeId] || 0) + 1;
          }
          return acc;
        }, {} as Record<string, number>);

        const recentActivity = tasks
          .sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())
          .slice(0, 10);

        const now = new Date();
        const overdueTasks = tasks.filter(task => 
          task.dueDate && new Date(task.dueDate) < now && task.status !== 'done'
        ).length;

        const upcomingDeadlines = tasks.filter(task => {
          if (!task.dueDate || task.status === 'done') return false;
          const dueDate = new Date(task.dueDate);
          const daysUntilDue = (dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
          return daysUntilDue >= 0 && daysUntilDue <= 7;
        }).length;

        return {
          totalTasks,
          completedTasks,
          inProgressTasks,
          blockedTasks,
          completionRate,
          averageCompletionTime,
          tasksByPriority,
          tasksByStatus,
          tasksByAssignee,
          recentActivity,
          overdueTasks,
          upcomingDeadlines
        };
      })
    );
  }

  trackByTaskId(index: number, task: Task): string {
    return task.id;
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'backlog':
        return 'radio_button_unchecked';
      case 'in-progress':
        return 'pending';
      case 'done':
        return 'check_circle';
      case 'blocked':
        return 'block';
      default:
        return 'help';
    }
  }
}
