import { TestBed } from '@angular/core/testing';

import { PerformanceService, PerformanceMetric, PerformanceReport } from './performance.service';

describe('PerformanceService', () => {
  let service: PerformanceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PerformanceService]
    });
    service = TestBed.inject(PerformanceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Timer functionality', () => {
    it('should start and end timer correctly', () => {
      service.startTimer('test-timer');
      const duration = service.endTimer('test-timer');
      
      expect(duration).toBeGreaterThan(0);
    });

    it('should return 0 if timer was not started', () => {
      const duration = service.endTimer('non-existent-timer');
      expect(duration).toBe(0);
    });

    it('should measure function execution time', () => {
      const result = service.measureFunction('test-function', () => {
        // Simulate some work
        let sum = 0;
        for (let i = 0; i < 1000; i++) {
          sum += i;
        }
        return sum;
      });

      expect(result).toBe(499500); // Sum of 0 to 999
    });

    it('should measure async function execution time', async () => {
      const result = await service.measureAsyncFunction('test-async-function', async () => {
        await new Promise(resolve => setTimeout(resolve, 10));
        return 'async result';
      });

      expect(result).toBe('async result');
    });

    it('should handle errors in function measurement', () => {
      expect(() => {
        service.measureFunction('error-function', () => {
          throw new Error('Test error');
        });
      }).toThrow('Test error');
    });

    it('should handle errors in async function measurement', async () => {
      await expectAsync(
        service.measureAsyncFunction('error-async-function', async () => {
          throw new Error('Async test error');
        })
      ).toBeRejectedWith('Async test error');
    });
  });

  describe('Metrics retrieval', () => {
    beforeEach(() => {
      service.startTimer('test-metric');
      service.endTimer('test-metric');
    });

    it('should get average time for metric', () => {
      const average = service.getAverageTime('test-metric');
      expect(average).toBeGreaterThan(0);
    });

    it('should return 0 for non-existent metric', () => {
      const average = service.getAverageTime('non-existent');
      expect(average).toBe(0);
    });

    it('should get total count for metric', () => {
      const count = service.getTotalCount('test-metric');
      expect(count).toBe(1);
    });

    it('should return 0 for non-existent metric count', () => {
      const count = service.getTotalCount('non-existent');
      expect(count).toBe(0);
    });

    it('should get slowest execution', () => {
      const slowest = service.getSlowestExecution('test-metric');
      expect(slowest).toBeTruthy();
      expect(slowest?.name).toBe('test-metric');
    });

    it('should return null for non-existent slowest execution', () => {
      const slowest = service.getSlowestExecution('non-existent');
      expect(slowest).toBeNull();
    });

    it('should get fastest execution', () => {
      const fastest = service.getFastestExecution('test-metric');
      expect(fastest).toBeTruthy();
      expect(fastest?.name).toBe('test-metric');
    });

    it('should return null for non-existent fastest execution', () => {
      const fastest = service.getFastestExecution('non-existent');
      expect(fastest).toBeNull();
    });

    it('should get all metrics for operation', () => {
      const metrics = service.getMetrics('test-metric');
      expect(metrics.length).toBe(1);
      expect(metrics[0].name).toBe('test-metric');
    });

    it('should return empty array for non-existent metrics', () => {
      const metrics = service.getMetrics('non-existent');
      expect(metrics).toEqual([]);
    });
  });

  describe('Performance report', () => {
    beforeEach(() => {
      // Add multiple metrics
      service.startTimer('fast-operation');
      service.endTimer('fast-operation');
      
      service.startTimer('slow-operation');
      // Simulate slow operation
      const start = performance.now();
      while (performance.now() - start < 1) {
        // Wait for at least 1ms
      }
      service.endTimer('slow-operation');
      
      service.startTimer('fast-operation');
      service.endTimer('fast-operation');
    });

    it('should generate performance report', () => {
      const report = service.getPerformanceReport();
      
      expect(report).toBeTruthy();
      expect(report.metrics.length).toBeGreaterThan(0);
      expect(report.averages).toBeTruthy();
      expect(report.totals).toBeTruthy();
      expect(report.slowest.length).toBeGreaterThan(0);
      expect(report.fastest.length).toBeGreaterThan(0);
    });

    it('should calculate correct averages', () => {
      const report = service.getPerformanceReport();
      
      expect(report.averages['fast-operation']).toBeGreaterThan(0);
      expect(report.averages['slow-operation']).toBeGreaterThan(0);
    });

    it('should calculate correct totals', () => {
      const report = service.getPerformanceReport();
      
      expect(report.totals['fast-operation']).toBe(2);
      expect(report.totals['slow-operation']).toBe(1);
    });

    it('should identify slowest operations', () => {
      const report = service.getPerformanceReport();
      
      expect(report.slowest.length).toBeGreaterThan(0);
      expect(report.slowest[0].name).toBe('slow-operation');
    });

    it('should identify fastest operations', () => {
      const report = service.getPerformanceReport();
      
      expect(report.fastest.length).toBeGreaterThan(0);
      // Fastest should be one of the fast operations
      expect(['fast-operation', 'slow-operation']).toContain(report.fastest[0].name);
    });
  });

  describe('Metrics management', () => {
    beforeEach(() => {
      service.startTimer('test-clear');
      service.endTimer('test-clear');
    });

    it('should clear all metrics', () => {
      service.clearMetrics();
      
      const report = service.getPerformanceReport();
      expect(report.metrics.length).toBe(0);
    });

    it('should clear metrics for specific operation', () => {
      service.clearMetricsFor('test-clear');
      
      const metrics = service.getMetrics('test-clear');
      expect(metrics.length).toBe(0);
    });

    it('should not affect other metrics when clearing specific operation', () => {
      service.startTimer('other-operation');
      service.endTimer('other-operation');
      
      service.clearMetricsFor('test-clear');
      
      const otherMetrics = service.getMetrics('other-operation');
      expect(otherMetrics.length).toBe(1);
    });
  });

  describe('Export and import', () => {
    beforeEach(() => {
      service.startTimer('export-test');
      service.endTimer('export-test');
    });

    it('should export metrics to JSON', () => {
      const json = service.exportMetrics();
      
      expect(json).toBeTruthy();
      expect(typeof json).toBe('string');
      
      const parsed = JSON.parse(json);
      expect(parsed.metrics).toBeTruthy();
      expect(parsed.averages).toBeTruthy();
      expect(parsed.totals).toBeTruthy();
    });

    it('should import metrics from JSON', () => {
      const originalReport = service.getPerformanceReport();
      const json = service.exportMetrics();
      
      service.clearMetrics();
      service.importMetrics(json);
      
      const importedReport = service.getPerformanceReport();
      expect(importedReport.metrics.length).toBe(originalReport.metrics.length);
    });

    it('should handle invalid JSON gracefully', () => {
      expect(() => {
        service.importMetrics('invalid json');
      }).not.toThrow();
    });

    it('should handle JSON without metrics gracefully', () => {
      expect(() => {
        service.importMetrics('{"other": "data"}');
      }).not.toThrow();
    });
  });

  describe('Performance analysis', () => {
    beforeEach(() => {
      service.startTimer('slow-operation');
      service.endTimer('slow-operation', { duration: 1500 }); // 1.5 seconds
      
      service.startTimer('frequent-operation');
      service.endTimer('frequent-operation');
      service.startTimer('frequent-operation');
      service.endTimer('frequent-operation');
      service.startTimer('frequent-operation');
      service.endTimer('frequent-operation');
    });

    it('should identify slow executions', () => {
      const isSlow = service.isSlowExecution('slow-operation', 1000);
      expect(isSlow).toBe(true);
    });

    it('should identify fast executions', () => {
      const isSlow = service.isSlowExecution('frequent-operation', 1000);
      expect(isSlow).toBe(false);
    });

    it('should generate optimization recommendations', () => {
      const recommendations = service.getOptimizationRecommendations();
      
      expect(Array.isArray(recommendations)).toBe(true);
      expect(recommendations.length).toBeGreaterThan(0);
      
      // Should have recommendation for slow operation
      const slowRecommendation = recommendations.find(r => 
        r.includes('slow-operation') && r.includes('slow')
      );
      expect(slowRecommendation).toBeTruthy();
    });

    it('should handle empty metrics gracefully', () => {
      service.clearMetrics();
      
      const recommendations = service.getOptimizationRecommendations();
      expect(recommendations).toEqual([]);
    });
  });

  describe('Edge cases', () => {
    it('should handle multiple rapid timer starts', () => {
      service.startTimer('rapid');
      service.startTimer('rapid'); // Should overwrite previous start
      const duration = service.endTimer('rapid');
      
      expect(duration).toBeGreaterThan(0);
    });

    it('should handle very short operations', () => {
      const result = service.measureFunction('short', () => {
        return 'immediate';
      });
      
      expect(result).toBe('immediate');
    });

    it('should handle operations with metadata', () => {
      service.startTimer('with-metadata');
      service.endTimer('with-metadata', { 
        userId: '123', 
        operation: 'test' 
      });
      
      const metrics = service.getMetrics('with-metadata');
      expect(metrics[0].metadata).toEqual({ 
        userId: '123', 
        operation: 'test' 
      });
    });

    it('should handle operations with error metadata', () => {
      try {
        service.measureFunction('error-with-metadata', () => {
          throw new Error('Test error');
        });
      } catch (error) {
        // Error should be thrown
      }
      
      const metrics = service.getMetrics('error-with-metadata');
      expect(metrics[0].metadata?.error).toBe('Test error');
    });
  });
});
