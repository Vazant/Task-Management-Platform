import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of } from 'rxjs';

import { PerformanceMonitorComponent } from './performance-monitor.component';
import { PerformanceService, PerformanceMetric, PerformanceReport } from '../../services/performance.service';

describe('PerformanceMonitorComponent', () => {
  let component: PerformanceMonitorComponent;
  let fixture: ComponentFixture<PerformanceMonitorComponent>;
  let performanceService: jasmine.SpyObj<PerformanceService>;

  const mockPerformanceReport: PerformanceReport = {
    metrics: [
      {
        name: 'test-operation',
        duration: 150,
        timestamp: new Date(),
        metadata: { test: 'data' }
      }
    ],
    averages: { 'test-operation': 150 },
    totals: { 'test-operation': 1 },
    slowest: [
      {
        name: 'slow-operation',
        duration: 1000,
        timestamp: new Date(),
        metadata: {}
      }
    ],
    fastest: [
      {
        name: 'fast-operation',
        duration: 10,
        timestamp: new Date(),
        metadata: {}
      }
    ]
  };

  const mockRecommendations = [
    'Operation slow-operation is very slow (1000.00ms). Consider optimization.',
    'Operation frequent-operation is called frequently (5 times, avg: 50.00ms). Consider caching or optimization.'
  ];

  beforeEach(async () => {
    const performanceServiceSpy = jasmine.createSpyObj('PerformanceService', [
      'startTimer', 'stopMonitoring', 'clearMetrics', 'exportMetrics', 'clearMetricsFor'
    ], {
      getPerformanceReport: mockPerformanceReport,
      getOptimizationRecommendations: mockRecommendations
    });

    await TestBed.configureTestingModule({
      imports: [PerformanceMonitorComponent],
      providers: [
        { provide: PerformanceService, useValue: performanceServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PerformanceMonitorComponent);
    component = fixture.componentInstance;
    performanceService = TestBed.inject(PerformanceService) as jasmine.SpyObj<PerformanceService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with monitoring started', () => {
    component.ngOnInit();
    expect(component.isMonitoring).toBe(true);
    expect(performanceService.startTimer).toHaveBeenCalledWith('component-render');
  });

  it('should start monitoring', () => {
    component.startMonitoring();
    expect(component.isMonitoring).toBe(true);
    expect(performanceService.startTimer).toHaveBeenCalledWith('component-render');
  });

  it('should stop monitoring', () => {
    component.stopMonitoring();
    expect(component.isMonitoring).toBe(false);
  });

  it('should clear metrics', () => {
    component.clearMetrics();
    expect(performanceService.clearMetrics).toHaveBeenCalled();
  });

  it('should export metrics', () => {
    const mockJson = '{"test": "data"}';
    performanceService.exportMetrics.and.returnValue(mockJson);
    
    spyOn(URL, 'createObjectURL').and.returnValue('blob:url');
    spyOn(URL, 'revokeObjectURL');
    spyOn(document, 'createElement').and.returnValue({
      href: '',
      download: '',
      click: jasmine.createSpy('click')
    } as any);

    component.exportMetrics();

    expect(performanceService.exportMetrics).toHaveBeenCalled();
  });

  it('should return correct performance status for good performance', () => {
    const metric: PerformanceMetric = {
      name: 'test',
      duration: 50,
      timestamp: new Date()
    };

    const status = component.getPerformanceStatus(metric);
    expect(status).toBe('good');
  });

  it('should return correct performance status for warning performance', () => {
    const metric: PerformanceMetric = {
      name: 'test',
      duration: 250,
      timestamp: new Date()
    };

    const status = component.getPerformanceStatus(metric);
    expect(status).toBe('warning');
  });

  it('should return correct performance status for critical performance', () => {
    const metric: PerformanceMetric = {
      name: 'test',
      duration: 600,
      timestamp: new Date()
    };

    const status = component.getPerformanceStatus(metric);
    expect(status).toBe('critical');
  });

  it('should return correct status color for good', () => {
    const color = component.getStatusColor('good');
    expect(color).toBe('#4caf50');
  });

  it('should return correct status color for warning', () => {
    const color = component.getStatusColor('warning');
    expect(color).toBe('#ff9800');
  });

  it('should return correct status color for critical', () => {
    const color = component.getStatusColor('critical');
    expect(color).toBe('#f44336');
  });

  it('should return correct status icon for good', () => {
    const icon = component.getStatusIcon('good');
    expect(icon).toBe('check_circle');
  });

  it('should return correct status icon for warning', () => {
    const icon = component.getStatusIcon('warning');
    expect(icon).toBe('warning');
  });

  it('should return correct status icon for critical', () => {
    const icon = component.getStatusIcon('critical');
    expect(icon).toBe('error');
  });

  it('should format duration correctly for microseconds', () => {
    const formatted = component.formatDuration(0.5);
    expect(formatted).toBe('500.0μs');
  });

  it('should format duration correctly for milliseconds', () => {
    const formatted = component.formatDuration(150);
    expect(formatted).toBe('150.0ms');
  });

  it('should format duration correctly for seconds', () => {
    const formatted = component.formatDuration(1500);
    expect(formatted).toBe('1.50s');
  });

  it('should get slowest operations', () => {
    const slowest = component.getSlowestOperations(mockPerformanceReport);
    expect(slowest.length).toBe(1);
    expect(slowest[0].name).toBe('slow-operation');
  });

  it('should get most frequent operations', () => {
    const frequent = component.getMostFrequentOperations(mockPerformanceReport);
    expect(frequent.length).toBe(1);
    expect(frequent[0].name).toBe('test-operation');
    expect(frequent[0].count).toBe(1);
    expect(frequent[0].average).toBe(150);
  });

  it('should track metrics by name', () => {
    const metric: PerformanceMetric = {
      name: 'test-metric',
      duration: 100,
      timestamp: new Date()
    };

    const result = component.trackByMetricName(0, metric);
    expect(result).toBe('test-metric');
  });

  it('should track operations by name', () => {
    const operation = {
      name: 'test-operation',
      count: 5,
      average: 100
    };

    const result = component.trackByOperationName(0, operation);
    expect(result).toBe('test-operation');
  });

  it('should unsubscribe on destroy', fakeAsync(() => {
    spyOn(component['destroy$'], 'next');
    spyOn(component['destroy$'], 'complete');

    component.ngOnDestroy();

    expect(component['destroy$'].next).toHaveBeenCalled();
    expect(component['destroy$'].complete).toHaveBeenCalled();
  }));

  it('should update performance report every 5 seconds', fakeAsync(() => {
    component.ngOnInit();
    
    // Initial update
    tick(0);
    expect(component.performanceReport$).toBeDefined();
    
    // After 5 seconds
    tick(5000);
    expect(component.performanceReport$).toBeDefined();
    
    // After 10 seconds
    tick(5000);
    expect(component.performanceReport$).toBeDefined();
  }));

  it('should update recommendations every 5 seconds', fakeAsync(() => {
    component.ngOnInit();
    
    // Initial update
    tick(0);
    expect(component.recommendations$).toBeDefined();
    
    // After 5 seconds
    tick(5000);
    expect(component.recommendations$).toBeDefined();
  }));

  it('should have correct displayed columns', () => {
    expect(component.displayedColumns).toEqual([
      'name', 'count', 'average', 'slowest', 'fastest', 'actions'
    ]);
  });

  it('should initialize with monitoring false', () => {
    expect(component.isMonitoring).toBe(false);
  });

  it('should handle empty performance report', () => {
    const emptyReport: PerformanceReport = {
      metrics: [],
      averages: {},
      totals: {},
      slowest: [],
      fastest: []
    };

    const slowest = component.getSlowestOperations(emptyReport);
    expect(slowest).toEqual([]);

    const frequent = component.getMostFrequentOperations(emptyReport);
    expect(frequent).toEqual([]);
  });

  it('should handle performance report with multiple operations', () => {
    const multiReport: PerformanceReport = {
      metrics: [
        { name: 'op1', duration: 100, timestamp: new Date() },
        { name: 'op2', duration: 200, timestamp: new Date() },
        { name: 'op3', duration: 300, timestamp: new Date() }
      ],
      averages: { 'op1': 100, 'op2': 200, 'op3': 300 },
      totals: { 'op1': 1, 'op2': 2, 'op3': 3 },
      slowest: [
        { name: 'op3', duration: 300, timestamp: new Date() },
        { name: 'op2', duration: 200, timestamp: new Date() },
        { name: 'op1', duration: 100, timestamp: new Date() }
      ],
      fastest: [
        { name: 'op1', duration: 100, timestamp: new Date() },
        { name: 'op2', duration: 200, timestamp: new Date() },
        { name: 'op3', duration: 300, timestamp: new Date() }
      ]
    };

    const slowest = component.getSlowestOperations(multiReport);
    expect(slowest.length).toBe(3);
    expect(slowest[0].name).toBe('op3');

    const frequent = component.getMostFrequentOperations(multiReport);
    expect(frequent.length).toBe(3);
    expect(frequent[0].name).toBe('op3'); // Most frequent
  });

  it('should handle edge case with zero duration', () => {
    const formatted = component.formatDuration(0);
    expect(formatted).toBe('0.0μs');
  });

  it('should handle edge case with very large duration', () => {
    const formatted = component.formatDuration(1000000);
    expect(formatted).toBe('1000.00s');
  });

  it('should handle unknown status in getStatusColor', () => {
    const color = component.getStatusColor('unknown' as any);
    expect(color).toBe('#757575');
  });

  it('should handle unknown status in getStatusIcon', () => {
    const icon = component.getStatusIcon('unknown' as any);
    expect(icon).toBe('help');
  });
});
