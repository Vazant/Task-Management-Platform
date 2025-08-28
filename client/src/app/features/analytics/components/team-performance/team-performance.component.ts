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

export interface TeamMemberPerformance {
  name: string;
  totalTasks: number;
  completedTasks: number;
  completionRate: number;
  averageCompletionTime: number;
  overdueTasks: number;
  productivity: number;
  workload: 'low' | 'medium' | 'high';
}

@Component({
  selector: 'app-team-performance',
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
  templateUrl: './team-performance.component.html',
  styleUrls: ['./team-performance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TeamPerformanceComponent {
  @Input() analytics!: TaskAnalytics;

  readonly displayedColumns = ['member', 'tasks', 'completion', 'productivity', 'workload', 'actions'];

  get teamPerformance(): TeamMemberPerformance[] {
    return Object.entries(this.analytics.tasksByAssignee).map(([assignee, totalTasks]) => {
      // Simulate performance data for demonstration
      const completedTasks = Math.floor(totalTasks * (0.6 + Math.random() * 0.4)); // 60-100% completion
      const completionRate = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;
      const averageCompletionTime = 3 + Math.random() * 10; // 3-13 days
      const overdueTasks = Math.floor(totalTasks * Math.random() * 0.3); // 0-30% overdue
      const productivity = completionRate * (1 - overdueTasks / totalTasks);
      
      let workload: 'low' | 'medium' | 'high';
      if (totalTasks <= 3) workload = 'low';
      else if (totalTasks <= 8) workload = 'medium';
      else workload = 'high';

      return {
        name: assignee,
        totalTasks,
        completedTasks,
        completionRate,
        averageCompletionTime,
        overdueTasks,
        productivity,
        workload
      };
    }).sort((a, b) => b.productivity - a.productivity);
  }

  get teamSummary() {
    const totalMembers = this.teamPerformance.length;
    const avgCompletionRate = this.teamPerformance.reduce((sum, member) => sum + member.completionRate, 0) / totalMembers;
    const avgProductivity = this.teamPerformance.reduce((sum, member) => sum + member.productivity, 0) / totalMembers;
    const totalOverdue = this.teamPerformance.reduce((sum, member) => sum + member.overdueTasks, 0);

    return {
      totalMembers,
      avgCompletionRate,
      avgProductivity,
      totalOverdue,
      topPerformer: this.teamPerformance[0]?.name || 'N/A',
      needsAttention: this.teamPerformance.filter(m => m.overdueTasks > 0 || m.completionRate < 50).length
    };
  }

  getWorkloadColor(workload: 'low' | 'medium' | 'high'): string {
    switch (workload) {
      case 'low':
        return '#4caf50';
      case 'medium':
        return '#ff9800';
      case 'high':
        return '#f44336';
      default:
        return '#9e9e9e';
    }
  }

  getProductivityColor(productivity: number): string {
    if (productivity >= 80) return '#4caf50';
    if (productivity >= 60) return '#ff9800';
    return '#f44336';
  }

  getWorkloadIcon(workload: 'low' | 'medium' | 'high'): string {
    switch (workload) {
      case 'low':
        return 'sentiment_satisfied';
      case 'medium':
        return 'sentiment_neutral';
      case 'high':
        return 'sentiment_dissatisfied';
      default:
        return 'help';
    }
  }

  exportTeamReport(): void {
    const reportData = {
      timestamp: new Date().toISOString(),
      teamSummary: this.teamSummary,
      memberPerformance: this.teamPerformance,
      recommendations: this.generateRecommendations()
    };

    const jsonData = JSON.stringify(reportData, null, 2);
    const blob = new Blob([jsonData], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `team-performance-report-${new Date().toISOString().split('T')[0]}.json`;
    link.click();
    URL.revokeObjectURL(url);
  }

  private generateRecommendations(): string[] {
    const recommendations: string[] = [];

    if (this.teamSummary.avgCompletionRate < 70) {
      recommendations.push('Consider providing additional support or training to improve completion rates');
    }

    if (this.teamSummary.totalOverdue > 0) {
      recommendations.push('Address overdue tasks to prevent project delays');
    }

    const lowPerformers = this.teamPerformance.filter(m => m.productivity < 60);
    if (lowPerformers.length > 0) {
      recommendations.push(`Provide additional support to ${lowPerformers.length} team member(s) with low productivity`);
    }

    const highWorkload = this.teamPerformance.filter(m => m.workload === 'high');
    if (highWorkload.length > 0) {
      recommendations.push('Consider redistributing workload to prevent burnout');
    }

    return recommendations;
  }
}
