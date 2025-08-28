import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';

import { TaskAnalytics } from '../analytics-dashboard/analytics-dashboard.component';
import { TaskStatus } from '@core/enums/task-status.enum';
import { TaskPriority } from '@core/enums/task-priority.enum';

export interface MetricItem {
  label: string;
  value: number | string;
  unit?: string;
  trend?: 'up' | 'down' | 'stable';
  trendValue?: number;
  color?: string;
  icon?: string;
}

@Component({
  selector: 'app-task-metrics',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatChipsModule,
    MatTooltipModule
  ],
  templateUrl: './task-metrics.component.html',
  styleUrls: ['./task-metrics.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskMetricsComponent {
  @Input() analytics!: TaskAnalytics;

  readonly displayedColumns = ['metric', 'value', 'trend', 'actions'];

  get detailedMetrics(): MetricItem[] {
    return [
      {
        label: 'Total Tasks',
        value: this.analytics.totalTasks,
        icon: 'assignment',
        color: '#1976d2'
      },
      {
        label: 'Completion Rate',
        value: this.analytics.completionRate,
        unit: '%',
        icon: 'trending_up',
        color: '#4caf50',
        trend: this.analytics.completionRate > 75 ? 'up' : this.analytics.completionRate < 50 ? 'down' : 'stable'
      },
      {
        label: 'Average Completion Time',
        value: this.analytics.averageCompletionTime,
        unit: ' days',
        icon: 'schedule',
        color: '#ff9800',
        trend: this.analytics.averageCompletionTime < 7 ? 'up' : this.analytics.averageCompletionTime > 14 ? 'down' : 'stable'
      },
      {
        label: 'Overdue Tasks',
        value: this.analytics.overdueTasks,
        icon: 'warning',
        color: '#f44336',
        trend: this.analytics.overdueTasks === 0 ? 'up' : this.analytics.overdueTasks > 5 ? 'down' : 'stable'
      },
      {
        label: 'Upcoming Deadlines',
        value: this.analytics.upcomingDeadlines,
        icon: 'event',
        color: '#9c27b0'
      },
      {
        label: 'Blocked Tasks',
        value: this.analytics.blockedTasks,
        icon: 'block',
        color: '#f44336',
        trend: this.analytics.blockedTasks === 0 ? 'up' : this.analytics.blockedTasks > 3 ? 'down' : 'stable'
      }
    ];
  }

  get statusBreakdown() {
    return Object.entries(this.analytics.tasksByStatus).map(([status, count]) => ({
      status: this.formatStatusLabel(status as TaskStatus),
      count,
      percentage: this.analytics.totalTasks > 0 ? (count / this.analytics.totalTasks) * 100 : 0,
      color: this.getStatusColor(status as TaskStatus)
    }));
  }

  get priorityBreakdown() {
    return Object.entries(this.analytics.tasksByPriority).map(([priority, count]) => ({
      priority: this.formatPriorityLabel(priority as TaskPriority),
      count,
      percentage: this.analytics.totalTasks > 0 ? (count / this.analytics.totalTasks) * 100 : 0,
      color: this.getPriorityColor(priority as TaskPriority)
    }));
  }

  get assigneeBreakdown() {
    return Object.entries(this.analytics.tasksByAssignee)
      .map(([assignee, count]) => ({
        assignee,
        count,
        percentage: this.analytics.totalTasks > 0 ? (count / this.analytics.totalTasks) * 100 : 0
      }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 10); // Top 10 assignees
  }

  getTrendIcon(trend?: 'up' | 'down' | 'stable'): string {
    switch (trend) {
      case 'up':
        return 'trending_up';
      case 'down':
        return 'trending_down';
      case 'stable':
        return 'trending_flat';
      default:
        return 'help';
    }
  }

  getTrendColor(trend?: 'up' | 'down' | 'stable'): string {
    switch (trend) {
      case 'up':
        return '#4caf50';
      case 'down':
        return '#f44336';
      case 'stable':
        return '#ff9800';
      default:
        return '#9e9e9e';
    }
  }

  private formatStatusLabel(status: TaskStatus): string {
    switch (status) {
      case TaskStatus.TODO:
        return 'To Do';
      case TaskStatus.IN_PROGRESS:
        return 'In Progress';
      case TaskStatus.COMPLETED:
        return 'Completed';
      case TaskStatus.BLOCKED:
        return 'Blocked';
      default:
        return status;
    }
  }

  private formatPriorityLabel(priority: TaskPriority): string {
    switch (priority) {
      case TaskPriority.LOW:
        return 'Low';
      case TaskPriority.MEDIUM:
        return 'Medium';
      case TaskPriority.HIGH:
        return 'High';
      default:
        return priority;
    }
  }

  private getStatusColor(status: TaskStatus): string {
    switch (status) {
      case TaskStatus.TODO:
        return '#9e9e9e';
      case TaskStatus.IN_PROGRESS:
        return '#ff9800';
      case TaskStatus.COMPLETED:
        return '#4caf50';
      case TaskStatus.BLOCKED:
        return '#f44336';
      default:
        return '#9e9e9e';
    }
  }

  private getPriorityColor(priority: TaskPriority): string {
    switch (priority) {
      case TaskPriority.LOW:
        return '#4caf50';
      case TaskPriority.MEDIUM:
        return '#ff9800';
      case TaskPriority.HIGH:
        return '#f44336';
      default:
        return '#9e9e9e';
    }
  }

  exportMetrics(): void {
    const metricsData = {
      timestamp: new Date().toISOString(),
      summary: {
        totalTasks: this.analytics.totalTasks,
        completedTasks: this.analytics.completedTasks,
        completionRate: this.analytics.completionRate,
        averageCompletionTime: this.analytics.averageCompletionTime,
        overdueTasks: this.analytics.overdueTasks,
        upcomingDeadlines: this.analytics.upcomingDeadlines,
        blockedTasks: this.analytics.blockedTasks
      },
      breakdowns: {
        byStatus: this.statusBreakdown,
        byPriority: this.priorityBreakdown,
        byAssignee: this.assigneeBreakdown
      }
    };

    const jsonData = JSON.stringify(metricsData, null, 2);
    const blob = new Blob([jsonData], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `task-metrics-${new Date().toISOString().split('T')[0]}.json`;
    link.click();
    URL.revokeObjectURL(url);
  }
}
