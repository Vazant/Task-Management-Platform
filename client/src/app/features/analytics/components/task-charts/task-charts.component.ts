import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';

import { TaskAnalytics } from '../analytics-dashboard/analytics-dashboard.component';
import { TaskStatus, TaskPriority } from '@models';

export interface ChartData {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    backgroundColor: string[];
    borderColor: string[];
    borderWidth: number;
  }[];
}

export interface ChartOptions {
  responsive: boolean;
  maintainAspectRatio: boolean;
  plugins: {
    legend: {
      display: boolean;
      position: 'top' | 'bottom' | 'left' | 'right';
    };
    title: {
      display: boolean;
      text: string;
    };
  };
  scales?: {
    y?: {
      beginAtZero: boolean;
      ticks: {
        stepSize: number;
      };
    };
  };
}

@Component({
  selector: 'app-task-charts',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatSelectModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule
  ],
  templateUrl: './task-charts.component.html',
  styleUrls: ['./task-charts.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskChartsComponent implements OnInit {
  @Input() analytics!: TaskAnalytics;

  readonly chartTypes = ['pie', 'bar', 'doughnut', 'line'] as const;
  selectedChartType: typeof this.chartTypes[number] = 'pie';
  
  readonly timeRanges = ['7d', '30d', '90d', '1y'] as const;
  selectedTimeRange: typeof this.timeRanges[number] = '30d';

  statusChartData: ChartData | null = null;
  priorityChartData: ChartData | null = null;
  assigneeChartData: ChartData | null = null;
  trendChartData: ChartData | null = null;

  readonly statusColors: Record<string, string> = {
    'backlog': '#9e9e9e',
    'in-progress': '#ff9800',
    'done': '#4caf50',
    'blocked': '#f44336'
  };

  readonly priorityColors: Record<string, string> = {
    'low': '#4caf50',
    'medium': '#ff9800',
    'high': '#f44336',
    'urgent': '#d32f2f'
  };

  ngOnInit(): void {
    this.updateCharts();
  }

  getCompletedTasksCount(): number {
    return this.trendChartData?.datasets?.[0]?.data?.reduce((sum, val) => sum + val, 0) ?? 0;
  }

  getCreatedTasksCount(): number {
    return this.trendChartData?.datasets?.[1]?.data?.reduce((sum, val) => sum + val, 0) ?? 0;
  }

  getCurrentDate(): Date {
    return new Date();
  }

  onChartTypeChange(): void {
    this.updateCharts();
  }

  onTimeRangeChange(): void {
    this.updateCharts();
  }

  private updateCharts(): void {
    this.statusChartData = this.createStatusChartData();
    this.priorityChartData = this.createPriorityChartData();
    this.assigneeChartData = this.createAssigneeChartData();
    this.trendChartData = this.createTrendChartData();
  }

  private createStatusChartData(): ChartData {
    const labels = Object.keys(this.analytics.tasksByStatus).map(status => 
      this.formatStatusLabel(status as TaskStatus)
    );
    const data = Object.values(this.analytics.tasksByStatus);
    const colors = Object.keys(this.analytics.tasksByStatus).map(status => 
      this.statusColors[status]
    );

    return {
      labels,
      datasets: [{
        label: 'Tasks by Status',
        data,
        backgroundColor: colors,
        borderColor: colors,
        borderWidth: 2
      }]
    };
  }

  private createPriorityChartData(): ChartData {
    const labels = Object.keys(this.analytics.tasksByPriority).map(priority => 
      this.formatPriorityLabel(priority as TaskPriority)
    );
    const data = Object.values(this.analytics.tasksByPriority);
    const colors = Object.keys(this.analytics.tasksByPriority).map(priority => 
      this.priorityColors[priority]
    );

    return {
      labels,
      datasets: [{
        label: 'Tasks by Priority',
        data,
        backgroundColor: colors,
        borderColor: colors,
        borderWidth: 2
      }]
    };
  }

  private createAssigneeChartData(): ChartData {
    const assignees = Object.keys(this.analytics.tasksByAssignee);
    const data = Object.values(this.analytics.tasksByAssignee);
    
    // Generate colors for assignees
    const colors = assignees.map((_, index) => {
      const hue = (index * 137.508) % 360; // Golden angle approximation
      return `hsl(${hue}, 70%, 60%)`;
    });

    return {
      labels: assignees,
      datasets: [{
        label: 'Tasks by Assignee',
        data,
        backgroundColor: colors,
        borderColor: colors,
        borderWidth: 2
      }]
    };
  }

  private createTrendChartData(): ChartData {
    // Simulate trend data for the selected time range
    const days = this.getDaysForTimeRange();
    const labels = days.map(day => day.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
    
    // Generate mock trend data
    const completedData = days.map(() => Math.floor(Math.random() * 10) + 1);
    const createdData = days.map(() => Math.floor(Math.random() * 15) + 2);

    return {
      labels,
      datasets: [
        {
          label: 'Tasks Completed',
          data: completedData,
          backgroundColor: ['rgba(76, 175, 80, 0.2)'],
          borderColor: ['#4caf50'],
          borderWidth: 2
        },
        {
          label: 'Tasks Created',
          data: createdData,
          backgroundColor: ['rgba(25, 118, 210, 0.2)'],
          borderColor: ['#1976d2'],
          borderWidth: 2
        }
      ]
    };
  }

  private getDaysForTimeRange(): Date[] {
    const days: Date[] = [];
    const today = new Date();
    
    let daysCount: number;
    switch (this.selectedTimeRange) {
      case '7d':
        daysCount = 7;
        break;
      case '30d':
        daysCount = 30;
        break;
      case '90d':
        daysCount = 90;
        break;
      case '1y':
        daysCount = 365;
        break;
      default:
        daysCount = 30;
    }

    for (let i = daysCount - 1; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(date.getDate() - i);
      days.push(date);
    }

    return days;
  }

  private formatStatusLabel(status: string): string {
    switch (status) {
      case 'backlog':
        return 'Backlog';
      case 'in-progress':
        return 'In Progress';
      case 'done':
        return 'Done';
      case 'blocked':
        return 'Blocked';
      default:
        return status;
    }
  }

  private formatPriorityLabel(priority: string): string {
    switch (priority) {
      case 'low':
        return 'Low';
      case 'medium':
        return 'Medium';
      case 'high':
        return 'High';
      case 'urgent':
        return 'Urgent';
      default:
        return priority;
    }
  }

  getChartOptions(title: string): ChartOptions {
    const isLineChart = this.selectedChartType === 'line';
    
    return {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: isLineChart ? 'top' : 'bottom'
        },
        title: {
          display: true,
          text: title
        }
      },
      ...(isLineChart && {
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      })
    };
  }

  exportChartData(chartType: 'status' | 'priority' | 'assignee' | 'trend'): void {
    let data: ChartData | null = null;
    let filename = '';

    switch (chartType) {
      case 'status':
        data = this.statusChartData;
        filename = 'task-status-chart';
        break;
      case 'priority':
        data = this.priorityChartData;
        filename = 'task-priority-chart';
        break;
      case 'assignee':
        data = this.assigneeChartData;
        filename = 'task-assignee-chart';
        break;
      case 'trend':
        data = this.trendChartData;
        filename = 'task-trend-chart';
        break;
    }

    if (data) {
      const jsonData = JSON.stringify(data, null, 2);
      const blob = new Blob([jsonData], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${filename}-${this.selectedTimeRange}.json`;
      link.click();
      URL.revokeObjectURL(url);
    }
  }
}
