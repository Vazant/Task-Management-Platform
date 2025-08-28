import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';

import { TaskAnalytics } from '../analytics-dashboard/analytics-dashboard.component';

export interface ExportConfig {
  reportType: 'summary' | 'detailed' | 'charts' | 'team' | 'time' | 'all';
  format: 'json' | 'csv' | 'pdf' | 'excel';
  dateRange: {
    start: Date | null;
    end: Date | null;
  };
  includeCharts: boolean;
  includeRecommendations: boolean;
  includeRawData: boolean;
  sections: string[];
}

export interface ExportTemplate {
  id: string;
  name: string;
  description: string;
  config: ExportConfig;
}

@Component({
  selector: 'app-export-reports',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatChipsModule,
    MatTooltipModule,
    FormsModule
  ],
  templateUrl: './export-reports.component.html',
  styleUrls: ['./export-reports.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExportReportsComponent {
  @Input() analytics!: TaskAnalytics;

  readonly reportTypes = [
    { value: 'summary', label: 'Summary Report', icon: 'assessment' },
    { value: 'detailed', label: 'Detailed Metrics', icon: 'analytics' },
    { value: 'charts', label: 'Charts & Trends', icon: 'pie_chart' },
    { value: 'team', label: 'Team Performance', icon: 'people' },
    { value: 'time', label: 'Time Tracking', icon: 'schedule' },
    { value: 'all', label: 'Complete Report', icon: 'description' }
  ];

  readonly exportFormats = [
    { value: 'json', label: 'JSON', icon: 'code' },
    { value: 'csv', label: 'CSV', icon: 'table_chart' },
    { value: 'pdf', label: 'PDF', icon: 'picture_as_pdf' },
    { value: 'excel', label: 'Excel', icon: 'table_view' }
  ];

  readonly availableSections = [
    { value: 'overview', label: 'Overview', icon: 'dashboard' },
    { value: 'metrics', label: 'Key Metrics', icon: 'trending_up' },
    { value: 'charts', label: 'Charts', icon: 'pie_chart' },
    { value: 'team', label: 'Team Performance', icon: 'people' },
    { value: 'time', label: 'Time Tracking', icon: 'schedule' },
    { value: 'recommendations', label: 'Recommendations', icon: 'lightbulb' },
    { value: 'raw_data', label: 'Raw Data', icon: 'data_object' }
  ];

  readonly exportTemplates: ExportTemplate[] = [
    {
      id: 'executive',
      name: 'Executive Summary',
      description: 'High-level overview for stakeholders',
      config: {
        reportType: 'summary',
        format: 'pdf',
        dateRange: { start: null, end: null },
        includeCharts: true,
        includeRecommendations: true,
        includeRawData: false,
        sections: ['overview', 'metrics', 'recommendations']
      }
    },
    {
      id: 'detailed',
      name: 'Detailed Analysis',
      description: 'Comprehensive report with all data',
      config: {
        reportType: 'all',
        format: 'excel',
        dateRange: { start: null, end: null },
        includeCharts: true,
        includeRecommendations: true,
        includeRawData: true,
        sections: ['overview', 'metrics', 'charts', 'team', 'time', 'recommendations', 'raw_data']
      }
    },
    {
      id: 'team',
      name: 'Team Performance',
      description: 'Focus on team metrics and productivity',
      config: {
        reportType: 'team',
        format: 'pdf',
        dateRange: { start: null, end: null },
        includeCharts: true,
        includeRecommendations: true,
        includeRawData: false,
        sections: ['overview', 'team', 'recommendations']
      }
    },
    {
      id: 'data',
      name: 'Raw Data Export',
      description: 'Export raw data for external analysis',
      config: {
        reportType: 'all',
        format: 'json',
        dateRange: { start: null, end: null },
        includeCharts: false,
        includeRecommendations: false,
        includeRawData: true,
        sections: ['raw_data']
      }
    }
  ];

  exportConfig: ExportConfig = {
    reportType: 'summary',
    format: 'pdf',
    dateRange: {
      start: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000), // 30 days ago
      end: new Date()
    },
    includeCharts: true,
    includeRecommendations: true,
    includeRawData: false,
    sections: ['overview', 'metrics', 'recommendations']
  };

  selectedTemplate: string = 'executive';
  isExporting = false;

  onTemplateChange(templateId: string): void {
    const template = this.exportTemplates.find(t => t.id === templateId);
    if (template) {
      this.exportConfig = { ...template.config };
      this.selectedTemplate = templateId;
    }
  }

  onReportTypeChange(): void {
    // Update sections based on report type
    switch (this.exportConfig.reportType) {
      case 'summary':
        this.exportConfig.sections = ['overview', 'metrics', 'recommendations'];
        break;
      case 'detailed':
        this.exportConfig.sections = ['overview', 'metrics', 'charts', 'recommendations'];
        break;
      case 'charts':
        this.exportConfig.sections = ['charts'];
        break;
      case 'team':
        this.exportConfig.sections = ['overview', 'team', 'recommendations'];
        break;
      case 'time':
        this.exportConfig.sections = ['overview', 'time', 'recommendations'];
        break;
      case 'all':
        this.exportConfig.sections = ['overview', 'metrics', 'charts', 'team', 'time', 'recommendations', 'raw_data'];
        break;
    }
  }

  toggleSection(section: string): void {
    const index = this.exportConfig.sections.indexOf(section);
    if (index > -1) {
      this.exportConfig.sections.splice(index, 1);
    } else {
      this.exportConfig.sections.push(section);
    }
  }

  isSectionSelected(section: string): boolean {
    return this.exportConfig.sections.includes(section);
  }

  async exportReport(): Promise<void> {
    this.isExporting = true;

    try {
      const reportData = this.generateReportData();
      
      switch (this.exportConfig.format) {
        case 'json':
          this.exportAsJson(reportData);
          break;
        case 'csv':
          this.exportAsCsv(reportData);
          break;
        case 'pdf':
          await this.exportAsPdf(reportData);
          break;
        case 'excel':
          this.exportAsExcel(reportData);
          break;
      }
    } catch (error) {
      console.error('Export failed:', error);
    } finally {
      this.isExporting = false;
    }
  }

  private generateReportData(): any {
    const data: any = {
      timestamp: new Date().toISOString(),
      config: this.exportConfig,
      analytics: this.analytics
    };

    if (this.exportConfig.sections.includes('overview')) {
      data.overview = {
        totalTasks: this.analytics.totalTasks,
        completedTasks: this.analytics.completedTasks,
        completionRate: this.analytics.completionRate,
        averageCompletionTime: this.analytics.averageCompletionTime
      };
    }

    if (this.exportConfig.sections.includes('metrics')) {
      data.metrics = {
        tasksByStatus: this.analytics.tasksByStatus,
        tasksByPriority: this.analytics.tasksByPriority,
        tasksByAssignee: this.analytics.tasksByAssignee
      };
    }

    if (this.exportConfig.sections.includes('recommendations')) {
      data.recommendations = this.generateRecommendations();
    }

    return data;
  }

  private generateRecommendations(): string[] {
    const recommendations: string[] = [];

    if (this.analytics.completionRate < 70) {
      recommendations.push('Focus on improving task completion rates through better resource allocation');
    }

    if (this.analytics.overdueTasks > 0) {
      recommendations.push('Address overdue tasks to prevent project delays');
    }

    if (this.analytics.blockedTasks > 0) {
      recommendations.push('Resolve blocked tasks to improve workflow efficiency');
    }

    if (this.analytics.averageCompletionTime > 10) {
      recommendations.push('Consider breaking down complex tasks to reduce completion time');
    }

    return recommendations;
  }

  private exportAsJson(data: any): void {
    const jsonData = JSON.stringify(data, null, 2);
    const blob = new Blob([jsonData], { type: 'application/json' });
    this.downloadFile(blob, `analytics-report-${new Date().toISOString().split('T')[0]}.json`);
  }

  private exportAsCsv(data: any): void {
    // Simple CSV export for basic data
    const csvContent = this.convertToCsv(data);
    const blob = new Blob([csvContent], { type: 'text/csv' });
    this.downloadFile(blob, `analytics-report-${new Date().toISOString().split('T')[0]}.csv`);
  }

  private async exportAsPdf(data: any): Promise<void> {
    // Simulate PDF export
    const pdfContent = this.convertToPdf(data);
    const blob = new Blob([pdfContent], { type: 'application/pdf' });
    this.downloadFile(blob, `analytics-report-${new Date().toISOString().split('T')[0]}.pdf`);
  }

  private exportAsExcel(data: any): void {
    // Simulate Excel export
    const excelContent = this.convertToExcel(data);
    const blob = new Blob([excelContent], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    this.downloadFile(blob, `analytics-report-${new Date().toISOString().split('T')[0]}.xlsx`);
  }

  private downloadFile(blob: Blob, filename: string): void {
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
  }

  private convertToCsv(data: any): string {
    // Simple CSV conversion
    const rows = [
      ['Metric', 'Value'],
      ['Total Tasks', data.analytics.totalTasks],
      ['Completed Tasks', data.analytics.completedTasks],
      ['Completion Rate', `${data.analytics.completionRate}%`],
      ['Average Completion Time', `${data.analytics.averageCompletionTime} days`]
    ];

    return rows.map(row => row.join(',')).join('\n');
  }

  private convertToPdf(data: any): string {
    // Simulate PDF content
    return `PDF Report Content\nGenerated: ${new Date().toLocaleString()}\n\n${JSON.stringify(data, null, 2)}`;
  }

  private convertToExcel(data: any): string {
    // Simulate Excel content
    return `Excel Report Content\nGenerated: ${new Date().toLocaleString()}\n\n${JSON.stringify(data, null, 2)}`;
  }
}
