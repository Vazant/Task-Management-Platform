# Angular Performance Optimization

## Обзор

Документация по оптимизации производительности Angular приложений, включая OnPush change detection, trackBy функции, memoized селекторы, lazy loading и global search.

## Ключевые концепции

### 1. Change Detection Strategy

#### OnPush Change Detection
```typescript
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush
})
```

**Принципы:**
- Компонент обновляется только при изменении Input свойств
- Требует иммутабельные объекты
- Улучшает производительность для больших списков

#### TrackBy функции
```typescript
trackByTaskId(index: number, task: Task): string {
  return task.id;
}

// В шаблоне
<div *ngFor="let task of tasks; trackBy: trackByTaskId">
```

**Преимущества:**
- Предотвращает пересоздание DOM элементов
- Улучшает производительность при обновлении списков
- Сохраняет состояние компонентов

### 2. Memoized Selectors

#### NgRx Selectors с memoization
```typescript
export const selectFilteredTasks = createSelector(
  selectAllTasks,
  selectTaskFilters,
  (tasks, filters) => {
    return tasks.filter(task => {
      // Фильтрация логика
    });
  }
);
```

**Особенности:**
- Автоматическое кэширование результатов
- Пересчет только при изменении зависимостей
- Улучшает производительность при частых обновлениях

### 3. Lazy Loading

#### Route-based Lazy Loading
```typescript
const routes: Routes = [
  {
    path: 'tasks',
    loadChildren: () => import('./features/tasks/tasks.module').then(m => m.TasksModule)
  }
];
```

#### Component Lazy Loading
```typescript
const TaskDialog = lazy(() => import('./task-dialog.component'));
```

### 4. Bundle Optimization

#### Tree Shaking
```typescript
// Используйте named imports
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

// Вместо
import { MatButtonModule, MatCardModule } from '@angular/material';
```

#### Code Splitting
```typescript
// Динамические импорты
const heavyComponent = () => import('./heavy-component');
```

## Паттерны реализации

### 1. Performance Optimization

#### OnPush Components
```typescript
@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskListComponent {
  @Input() tasks: Task[] = [];
  
  trackByTaskId(index: number, task: Task): string {
    return task.id;
  }
}
```

#### Memoized Selectors
```typescript
export const selectTasksByStatus = createSelector(
  selectAllTasks,
  (tasks: Task[], props: { status: TaskStatus }) => {
    return tasks.filter(task => task.status === props.status);
  }
);

export const selectTasksStats = createSelector(
  selectAllTasks,
  (tasks: Task[]) => {
    return {
      total: tasks.length,
      completed: tasks.filter(t => t.status === 'completed').length,
      inProgress: tasks.filter(t => t.status === 'in-progress').length
    };
  }
);
```

#### Virtual Scrolling
```typescript
@Component({
  selector: 'app-virtual-task-list',
  template: `
    <cdk-virtual-scroll-viewport itemSize="60" class="task-list">
      <div *cdkVirtualFor="let task of tasks; trackBy: trackByTaskId">
        <app-task-item [task]="task"></app-task-item>
      </div>
    </cdk-virtual-scroll-viewport>
  `
})
export class VirtualTaskListComponent {
  @Input() tasks: Task[] = [];
  
  trackByTaskId(index: number, task: Task): string {
    return task.id;
  }
}
```

### 2. Global Search

#### Search Service
```typescript
@Injectable({
  providedIn: 'root'
})
export class GlobalSearchService {
  private searchSubject = new BehaviorSubject<string>('');
  search$ = this.searchSubject.asObservable();
  
  private searchResultsSubject = new BehaviorSubject<SearchResult[]>([]);
  searchResults$ = this.searchResultsSubject.asObservable();
  
  search(query: string): void {
    this.searchSubject.next(query);
  }
  
  updateResults(results: SearchResult[]): void {
    this.searchResultsSubject.next(results);
  }
}
```

#### Search Component
```typescript
@Component({
  selector: 'app-global-search',
  template: `
    <mat-form-field appearance="outline">
      <mat-label>Search</mat-label>
      <input matInput 
             [formControl]="searchControl"
             placeholder="Search tasks, projects, users...">
      <mat-icon matSuffix>search</mat-icon>
    </mat-form-field>
    
    <div *ngIf="searchResults$ | async as results" class="search-results">
      <div *ngFor="let result of results; trackBy: trackByResultId" 
           class="search-result-item"
           (click)="onResultClick(result)">
        <mat-icon>{{ result.icon }}</mat-icon>
        <div class="result-content">
          <div class="result-title">{{ result.title }}</div>
          <div class="result-subtitle">{{ result.subtitle }}</div>
        </div>
      </div>
    </div>
  `
})
export class GlobalSearchComponent implements OnInit, OnDestroy {
  searchControl = new FormControl('');
  searchResults$ = this.searchService.searchResults$;
  private destroy$ = new Subject<void>();
  
  constructor(
    private searchService: GlobalSearchService,
    private store: Store
  ) {}
  
  ngOnInit(): void {
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(query => {
      if (query) {
        this.performSearch(query);
      }
    });
  }
  
  private performSearch(query: string): void {
    this.store.dispatch(SearchActions.search({ query }));
  }
  
  trackByResultId(index: number, result: SearchResult): string {
    return result.id;
  }
  
  onResultClick(result: SearchResult): void {
    this.router.navigate([result.route]);
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

#### Search Effects
```typescript
@Injectable()
export class SearchEffects {
  search$ = createEffect(() => 
    this.actions$.pipe(
      ofType(SearchActions.search),
      switchMap(({ query }) => 
        this.searchService.search(query).pipe(
          map(results => SearchActions.searchSuccess({ results })),
          catchError(error => of(SearchActions.searchFailure({ error })))
        )
      )
    )
  );
  
  constructor(
    private actions$: Actions,
    private searchService: SearchService
  ) {}
}
```

### 3. Debounced Search

#### Debounced Input Component
```typescript
@Component({
  selector: 'app-debounced-search',
  template: `
    <mat-form-field appearance="outline">
      <mat-label>{{ label }}</mat-label>
      <input matInput 
             [formControl]="searchControl"
             [placeholder]="placeholder">
      <mat-icon matSuffix>search</mat-icon>
      <mat-spinner matSuffix *ngIf="loading$ | async" diameter="20"></mat-spinner>
    </mat-form-field>
  `
})
export class DebouncedSearchComponent implements OnInit, OnDestroy {
  @Input() label = 'Search';
  @Input() placeholder = 'Enter search term...';
  @Input() debounceTime = 300;
  @Output() search = new EventEmitter<string>();
  
  searchControl = new FormControl('');
  loading$ = new BehaviorSubject<boolean>(false);
  private destroy$ = new Subject<void>();
  
  ngOnInit(): void {
    this.searchControl.valueChanges.pipe(
      debounceTime(this.debounceTime),
      distinctUntilChanged(),
      filter(query => query !== null && query !== undefined),
      takeUntil(this.destroy$)
    ).subscribe(query => {
      this.loading$.next(true);
      this.search.emit(query);
    });
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

## Оптимизации производительности

### 1. Bundle Optimization

#### Angular.json Configuration
```json
{
  "projects": {
    "client": {
      "architect": {
        "build": {
          "options": {
            "optimization": true,
            "outputHashing": "all",
            "sourceMap": false,
            "namedChunks": false,
            "aot": true,
            "extractLicenses": true,
            "vendorChunk": true,
            "buildOptimizer": true
          }
        }
      }
    }
  }
}
```

#### Webpack Bundle Analyzer
```bash
npm install --save-dev webpack-bundle-analyzer
```

```typescript
// webpack.config.js
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

module.exports = {
  plugins: [
    new BundleAnalyzerPlugin()
  ]
};
```

### 2. Memory Management

#### Unsubscribe Pattern
```typescript
@Component({
  selector: 'app-task-list'
})
export class TaskListComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  ngOnInit(): void {
    this.store.select(selectAllTasks).pipe(
      takeUntil(this.destroy$)
    ).subscribe(tasks => {
      this.tasks = tasks;
    });
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

#### WeakMap для кэширования
```typescript
export class CacheService {
  private cache = new WeakMap<object, any>();
  
  get<T>(key: object): T | undefined {
    return this.cache.get(key);
  }
  
  set<T>(key: object, value: T): void {
    this.cache.set(key, value);
  }
}
```

### 3. Performance Monitoring

#### Performance Service
```typescript
@Injectable({
  providedIn: 'root'
})
export class PerformanceService {
  private metrics = new Map<string, number[]>();
  
  startTimer(name: string): void {
    performance.mark(`${name}-start`);
  }
  
  endTimer(name: string): void {
    performance.mark(`${name}-end`);
    performance.measure(name, `${name}-start`, `${name}-end`);
    
    const measure = performance.getEntriesByName(name)[0];
    if (!this.metrics.has(name)) {
      this.metrics.set(name, []);
    }
    this.metrics.get(name)!.push(measure.duration);
  }
  
  getAverageTime(name: string): number {
    const times = this.metrics.get(name) || [];
    return times.reduce((sum, time) => sum + time, 0) / times.length;
  }
}
```

## Лучшие практики

### 1. Change Detection

- Используйте OnPush для компонентов с большими списками
- Применяйте trackBy функции для всех *ngFor
- Избегайте сложных вычислений в шаблонах
- Используйте pure pipes для трансформаций

### 2. Memory Management

- Всегда отписывайтесь от Observable
- Используйте takeUntil для автоматической отписки
- Применяйте WeakMap для кэширования
- Избегайте memory leaks в сервисах

### 3. Bundle Size

- Используйте tree shaking
- Применяйте lazy loading
- Разделяйте vendor и application bundles
- Анализируйте размер бандла

### 4. Search Optimization

- Используйте debouncing для поиска
- Применяйте memoization для результатов
- Кэшируйте частые запросы
- Используйте индексы для быстрого поиска

## Тестирование производительности

### 1. Unit Tests
```typescript
describe('PerformanceService', () => {
  let service: PerformanceService;
  
  beforeEach(() => {
    service = new PerformanceService();
  });
  
  it('should measure execution time', () => {
    service.startTimer('test');
    // Выполнение операции
    service.endTimer('test');
    
    const avgTime = service.getAverageTime('test');
    expect(avgTime).toBeGreaterThan(0);
  });
});
```

### 2. E2E Performance Tests
```typescript
describe('Performance E2E', () => {
  it('should load task list within 2 seconds', () => {
    const startTime = performance.now();
    
    cy.visit('/tasks');
    cy.get('[data-testid="task-list"]').should('be.visible');
    
    const endTime = performance.now();
    const loadTime = endTime - startTime;
    
    expect(loadTime).to.be.lessThan(2000);
  });
});
```

## Мониторинг и метрики

### 1. Core Web Vitals
- LCP (Largest Contentful Paint)
- FID (First Input Delay)
- CLS (Cumulative Layout Shift)

### 2. Angular Metrics
- Change Detection Cycles
- Bundle Size
- Memory Usage
- Component Render Time

### 3. User Experience Metrics
- Time to Interactive
- First Contentful Paint
- Search Response Time
- Navigation Speed

## Заключение

Оптимизация производительности Angular приложений требует комплексного подхода:

1. **Change Detection**: OnPush стратегия и trackBy функции
2. **Memory Management**: Правильная отписка от Observable
3. **Bundle Optimization**: Tree shaking и lazy loading
4. **Search Performance**: Debouncing и memoization
5. **Monitoring**: Постоянный мониторинг метрик

Регулярный анализ производительности и оптимизация критических путей обеспечивают отличный пользовательский опыт.
