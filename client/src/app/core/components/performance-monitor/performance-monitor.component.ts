import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule } from '@angular/material/list';
import { Subject, Observable, timer } from 'rxjs';
import { takeUntil, map } from 'rxjs/operators';

import { PerformanceService, PerformanceMetric, PerformanceReport } from '../../services/performance.service';

@Component({
  selector: 'app-performance-monitor',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatProgressBarModule,
    MatChipsModule,
    MatTooltipModule,
    MatExpansionModule,
    MatListModule
  ],
  templateUrl: './performance-monitor.component.html',
  styleUrls: ['./performance-monitor.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PerformanceMonitorComponent implements OnInit, OnDestroy {
  performanceReport$: Observable<PerformanceReport>;
  recommendations$: Observable<string[]>;
  isMonitoring = false;
  
  displayedColumns = ['name', 'count', 'average', 'slowest', 'fastest', 'actions'];
  
  private destroy$ = new Subject<void>();
  private updateTimer$ = timer(0, 5000); // Update every 5 seconds
  
  constructor(private performanceService: PerformanceService) {
    this.performanceReport$ = this.updateTimer$.pipe(
      map(() => this.performanceService.getPerformanceReport())
    );
    
    this.recommendations$ = this.updateTimer$.pipe(
      map(() => this.performanceService.getOptimizationRecommendations())
    );
  }
  
  ngOnInit(): void {
    this.startMonitoring();
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  startMonitoring(): void {
    this.isMonitoring = true;
    this.performanceService.startTimer('component-render');
  }
  
  stopMonitoring(): void {
    this.isMonitoring = false;
  }
  
  clearMetrics(): void {
    this.performanceService.clearMetrics();
  }
  
  exportMetrics(): void {
    const metrics = this.performanceService.exportMetrics();
    const blob = new Blob([metrics], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `performance-metrics-${new Date().toISOString().split('T')[0]}.json`;
    a.click();
    URL.revokeObjectURL(url);
  }
  
  getPerformanceStatus(metric: PerformanceMetric): 'good' | 'warning' | 'critical' {
    if (metric.duration < 100) return 'good';
    if (metric.duration < 500) return 'warning';
    return 'critical';
  }
  
  getStatusColor(status: 'good' | 'warning' | 'critical'): string {
    switch (status) {
      case 'good': return '#4caf50';
      case 'warning': return '#ff9800';
      case 'critical': return '#f44336';
      default: return '#757575';
    }
  }
  
  getStatusIcon(status: 'good' | 'warning' | 'critical'): string {
    switch (status) {
      case 'good': return 'check_circle';
      case 'warning': return 'warning';
      case 'critical': return 'error';
      default: return 'help';
    }
  }
  
  formatDuration(duration: number): string {
    if (duration < 1) {
      return `${(duration * 1000).toFixed(1)}μs`;
    }
    if (duration < 1000) {
      return `${duration.toFixed(1)}ms`;
    }
    return `${(duration / 1000).toFixed(2)}s`;
  }
  
  getSlowestOperations(report: PerformanceReport): PerformanceMetric[] {
    return report.slowest.slice(0, 5);
  }
  
  getMostFrequentOperations(report: PerformanceReport): Array<{name: string, count: number, average: number}> {
    return Object.entries(report.totals)
      .map(([name, count]) => ({
        name,
        count,
        average: report.averages[name]
      }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);
  }
  
  trackByMetricName(index: number, metric: PerformanceMetric): string {
    return metric.name;
  }
  
  trackByOperationName(index: number, operation: {name: string, count: number, average: number}): string {
    return operation.name;
  }
}
