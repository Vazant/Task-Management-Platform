import { Injectable } from '@angular/core';

export interface PerformanceMetric {
  name: string;
  duration: number;
  timestamp: Date;
  metadata?: Record<string, any>;
}

export interface PerformanceReport {
  metrics: PerformanceMetric[];
  averages: Record<string, number>;
  totals: Record<string, number>;
  slowest: PerformanceMetric[];
  fastest: PerformanceMetric[];
}

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {
  private metrics = new Map<string, PerformanceMetric[]>();
  private readonly marks = new Map<string, number>();
  
  /**
   * Начинает измерение времени для указанной операции
   */
  startTimer(name: string): void {
    this.marks.set(name, performance.now());
  }
  
  /**
   * Завершает измерение времени и сохраняет метрику
   */
  endTimer(name: string, metadata?: Record<string, any>): number {
    const startTime = this.marks.get(name);
    if (!startTime) {
      console.warn(`Timer '${name}' was not started`);
      return 0;
    }
    
    const endTime = performance.now();
    const duration = endTime - startTime;
    
    const metric: PerformanceMetric = {
      name,
      duration,
      timestamp: new Date(),
      metadata
    };
    
    if (!this.metrics.has(name)) {
      this.metrics.set(name, []);
    }
    this.metrics.get(name)!.push(metric);
    
    // Очищаем mark
    this.marks.delete(name);
    
    return duration;
  }
  
  /**
   * Измеряет время выполнения функции
   */
  measureFunction<T>(name: string, fn: () => T, metadata?: Record<string, any>): T {
    this.startTimer(name);
    try {
      const result = fn();
      this.endTimer(name, metadata);
      return result;
    } catch (error) {
      this.endTimer(name, { ...metadata, error: (error as Error).message });
      throw error;
    }
  }
  
  /**
   * Измеряет время выполнения асинхронной функции
   */
  async measureAsyncFunction<T>(name: string, fn: () => Promise<T>, metadata?: Record<string, any>): Promise<T> {
    this.startTimer(name);
    try {
      const result = await fn();
      this.endTimer(name, metadata);
      return result;
    } catch (error) {
      this.endTimer(name, { ...metadata, error: (error as Error).message });
      throw error;
    }
  }
  
  /**
   * Получает среднее время выполнения для указанной операции
   */
  getAverageTime(name: string): number {
    const metrics = this.metrics.get(name) || [];
    if (metrics.length === 0) return 0;
    
    const total = metrics.reduce((sum, metric) => sum + metric.duration, 0);
    return total / metrics.length;
  }
  
  /**
   * Получает общее количество измерений для указанной операции
   */
  getTotalCount(name: string): number {
    return this.metrics.get(name)?.length || 0;
  }
  
  /**
   * Получает самое медленное выполнение для указанной операции
   */
  getSlowestExecution(name: string): PerformanceMetric | null {
    const metrics = this.metrics.get(name) || [];
    if (metrics.length === 0) return null;
    
    return metrics.reduce((slowest, current) => 
      current.duration > slowest.duration ? current : slowest
    );
  }
  
  /**
   * Получает самое быстрое выполнение для указанной операции
   */
  getFastestExecution(name: string): PerformanceMetric | null {
    const metrics = this.metrics.get(name) || [];
    if (metrics.length === 0) return null;
    
    return metrics.reduce((fastest, current) => 
      current.duration < fastest.duration ? current : fastest
    );
  }
  
  /**
   * Получает все метрики для указанной операции
   */
  getMetrics(name: string): PerformanceMetric[] {
    return [...(this.metrics.get(name) || [])];
  }
  
  /**
   * Получает отчет по производительности
   */
  getPerformanceReport(): PerformanceReport {
    const allMetrics: PerformanceMetric[] = [];
    const averages: Record<string, number> = {};
    const totals: Record<string, number> = {};
    
    // Собираем все метрики
    for (const [name, metrics] of this.metrics) {
      allMetrics.push(...metrics);
      averages[name] = this.getAverageTime(name);
      totals[name] = metrics.length;
    }
    
    // Сортируем по времени выполнения
    const sortedMetrics = [...allMetrics].sort((a, b) => b.duration - a.duration);
    
    return {
      metrics: allMetrics,
      averages,
      totals,
      slowest: sortedMetrics.slice(0, 10), // Топ 10 самых медленных
      fastest: sortedMetrics.slice(-10).reverse() // Топ 10 самых быстрых
    };
  }
  
  /**
   * Очищает все метрики
   */
  clearMetrics(): void {
    this.metrics.clear();
    this.marks.clear();
  }
  
  /**
   * Очищает метрики для указанной операции
   */
  clearMetricsFor(name: string): void {
    this.metrics.delete(name);
    this.marks.delete(name);
  }
  
  /**
   * Экспортирует метрики в JSON
   */
  exportMetrics(): string {
    const report = this.getPerformanceReport();
    return JSON.stringify(report, null, 2);
  }
  
  /**
   * Импортирует метрики из JSON
   */
  importMetrics(json: string): void {
    try {
      const data = JSON.parse(json);
      if (data.metrics) {
        // Группируем метрики по имени
        const groupedMetrics = new Map<string, PerformanceMetric[]>();
        
        data.metrics.forEach((metric: any) => {
          const metricObj: PerformanceMetric = {
            ...metric,
            timestamp: new Date(metric.timestamp)
          };
          
          if (!groupedMetrics.has(metric.name)) {
            groupedMetrics.set(metric.name, []);
          }
          groupedMetrics.get(metric.name)!.push(metricObj);
        });
        
        this.metrics = groupedMetrics;
      }
    } catch (error) {
      console.error('Failed to import metrics:', error);
    }
  }
  
  /**
   * Проверяет, превышает ли время выполнения пороговое значение
   */
  isSlowExecution(name: string, threshold: number): boolean {
    const average = this.getAverageTime(name);
    return average > threshold;
  }
  
  /**
   * Получает рекомендации по оптимизации
   */
  getOptimizationRecommendations(): string[] {
    const recommendations: string[] = [];
    const report = this.getPerformanceReport();
    
    // Проверяем медленные операции
    report.slowest.forEach(metric => {
      if (metric.duration > 1000) {
        recommendations.push(`Operation '${metric.name}' is very slow (${metric.duration.toFixed(2)}ms). Consider optimization.`);
      }
    });
    
    // Проверяем операции с большим количеством вызовов
    Object.entries(report.totals).forEach(([name, count]) => {
      if (count > 100) {
        const average = report.averages[name];
        recommendations.push(`Operation '${name}' is called frequently (${count} times, avg: ${average.toFixed(2)}ms). Consider caching or optimization.`);
      }
    });
    
    return recommendations;
  }
}
