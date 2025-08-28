import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatMenuModule } from '@angular/material/menu';

import { TaskAnalytics } from '../analytics-dashboard/analytics-dashboard.component';

export interface TimeTrackingData {
  taskId: string;
  taskTitle: string;
  assignee: string;
  estimatedHours: number;
  actualHours: number;
  accuracy: number;
  status: 'accurate' | 'underestimated' | 'overestimated';
  variance: number;
}

export interface TimeSummary {
  totalEstimated: number;
  totalActual: number;
  averageAccuracy: number;
  accurateEstimates: number;
  underestimatedTasks: number;
  overestimatedTasks: number;
  efficiency: number;
}

@Component({
  selector: 'app-time-tracking',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatChipsModule,
    MatTooltipModule,
    MatProgressBarModule,
    MatMenuModule
  ],
  templateUrl: './time-tracking.component.html',
  styleUrls: ['./time-tracking.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimeTrackingComponent {
  @Input() analytics!: TaskAnalytics;

  readonly displayedColumns = ['task', 'assignee', 'estimated', 'actual', 'accuracy', 'status', 'actions'];

  get timeTrackingData(): TimeTrackingData[] {
    // Simulate time tracking data for demonstration
    return this.analytics.recentActivity.map((task, index) => {
      const estimatedHours = Math.floor(Math.random() * 20) + 1; // 1-20 hours
      const actualHours = estimatedHours * (0.7 + Math.random() * 0.8); // 70-150% of estimated
      const accuracy = Math.min(100, (estimatedHours / actualHours) * 100);
      const variance = actualHours - estimatedHours;
      
      let status: 'accurate' | 'underestimated' | 'overestimated';
      if (accuracy >= 90) status = 'accurate';
      else if (actualHours > estimatedHours) status = 'underestimated';
      else status = 'overestimated';

      return {
        taskId: task.id,
        taskTitle: task.title,
        assignee: task.assignee || 'Unassigned',
        estimatedHours,
        actualHours: Math.round(actualHours * 10) / 10,
        accuracy: Math.round(accuracy * 10) / 10,
        status,
        variance: Math.round(variance * 10) / 10
      };
    });
  }

  get timeSummary(): TimeSummary {
    const totalEstimated = this.timeTrackingData.reduce((sum, item) => sum + item.estimatedHours, 0);
    const totalActual = this.timeTrackingData.reduce((sum, item) => sum + item.actualHours, 0);
    const averageAccuracy = this.timeTrackingData.reduce((sum, item) => sum + item.accuracy, 0) / this.timeTrackingData.length;
    
    const accurateEstimates = this.timeTrackingData.filter(item => item.status === 'accurate').length;
    const underestimatedTasks = this.timeTrackingData.filter(item => item.status === 'underestimated').length;
    const overestimatedTasks = this.timeTrackingData.filter(item => item.status === 'overestimated').length;
    
    const efficiency = totalEstimated > 0 ? (totalEstimated / totalActual) * 100 : 0;

    return {
      totalEstimated,
      totalActual,
      averageAccuracy,
      accurateEstimates,
      underestimatedTasks,
      overestimatedTasks,
      efficiency
    };
  }

  get accuracyBreakdown() {
    const accurate = this.timeTrackingData.filter(item => item.status === 'accurate').length;
    const underestimated = this.timeTrackingData.filter(item => item.status === 'underestimated').length;
    const overestimated = this.timeTrackingData.filter(item => item.status === 'overestimated').length;
    const total = this.timeTrackingData.length;

    return [
      {
        status: 'Accurate',
        count: accurate,
        percentage: total > 0 ? (accurate / total) * 100 : 0,
        color: '#4caf50'
      },
      {
        status: 'Underestimated',
        count: underestimated,
        percentage: total > 0 ? (underestimated / total) * 100 : 0,
        color: '#f44336'
      },
      {
        status: 'Overestimated',
        count: overestimated,
        percentage: total > 0 ? (overestimated / total) * 100 : 0,
        color: '#ff9800'
      }
    ];
  }

  getStatusColor(status: 'accurate' | 'underestimated' | 'overestimated'): string {
    switch (status) {
      case 'accurate':
        return '#4caf50';
      case 'underestimated':
        return '#f44336';
      case 'overestimated':
        return '#ff9800';
      default:
        return '#9e9e9e';
    }
  }

  getStatusIcon(status: 'accurate' | 'underestimated' | 'overestimated'): string {
    switch (status) {
      case 'accurate':
        return 'check_circle';
      case 'underestimated':
        return 'trending_down';
      case 'overestimated':
        return 'trending_up';
      default:
        return 'help';
    }
  }

  getAccuracyColor(accuracy: number): string {
    if (accuracy >= 90) return '#4caf50';
    if (accuracy >= 70) return '#ff9800';
    return '#f44336';
  }

  exportTimeReport(): void {
    const reportData = {
      timestamp: new Date().toISOString(),
      summary: this.timeSummary,
      breakdown: this.accuracyBreakdown,
      detailedData: this.timeTrackingData,
      recommendations: this.generateRecommendations()
    };

    const jsonData = JSON.stringify(reportData, null, 2);
    const blob = new Blob([jsonData], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `time-tracking-report-${new Date().toISOString().split('T')[0]}.json`;
    link.click();
    URL.revokeObjectURL(url);
  }

  private generateRecommendations(): string[] {
    const recommendations: string[] = [];

    if (this.timeSummary.averageAccuracy < 80) {
      recommendations.push('Consider improving estimation accuracy through better planning and historical data analysis');
    }

    if (this.timeSummary.underestimatedTasks > this.timeSummary.overestimatedTasks) {
      recommendations.push('Tasks are frequently underestimated - consider adding buffer time to estimates');
    }

    if (this.timeSummary.overestimatedTasks > this.timeSummary.underestimatedTasks) {
      recommendations.push('Tasks are frequently overestimated - consider refining estimation process');
    }

    if (this.timeSummary.efficiency < 80) {
      recommendations.push('Overall efficiency is low - consider process improvements and resource allocation');
    }

    return recommendations;
  }
}
