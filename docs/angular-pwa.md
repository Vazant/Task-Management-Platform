# Angular PWA Guide

## Overview

Progressive Web Apps (PWAs) combine the best of web and mobile apps, providing offline functionality, push notifications, and app-like experience. This guide covers Angular PWA implementation patterns.

## Key Concepts

### Service Workers
- Background scripts that run independently of the main application
- Handle caching, push notifications, and background sync
- Can work offline and intercept network requests

### App Manifest
- JSON file that provides metadata about the web application
- Enables "Add to Home Screen" functionality
- Defines app appearance and behavior

### Caching Strategies
- **Cache First**: Serve from cache, fallback to network
- **Network First**: Try network, fallback to cache
- **Stale While Revalidate**: Serve cached version while updating in background
- **Cache Only**: Serve only from cache
- **Network Only**: Always use network

## Implementation Patterns

### 1. Service Worker Registration

```typescript
// app.module.ts
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';

@NgModule({
  imports: [
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: environment.production,
      registrationStrategy: 'registerWhenStable:30000'
    })
  ]
})
export class AppModule { }
```

### 2. App Manifest Configuration

```json
{
  "name": "Task Management Platform",
  "short_name": "TaskManager",
  "description": "Modern task management application",
  "start_url": "/",
  "display": "standalone",
  "background_color": "#ffffff",
  "theme_color": "#1976d2",
  "icons": [
    {
      "src": "assets/icons/icon-72x72.png",
      "sizes": "72x72",
      "type": "image/png"
    }
  ]
}
```

### 3. Caching Strategies

```typescript
// ngsw-config.json
{
  "assetGroups": [
    {
      "name": "app",
      "installMode": "prefetch",
      "resources": {
        "files": [
          "/favicon.ico",
          "/index.html",
          "/manifest.webmanifest",
          "/*.css",
          "/*.js"
        ]
      }
    },
    {
      "name": "assets",
      "installMode": "lazy",
      "updateMode": "prefetch",
      "resources": {
        "files": [
          "/assets/**",
          "/*.(svg|cur|jpg|jpeg|png|apng|webp|avif|gif|otf|ttf|woff|woff2)"
        ]
      }
    }
  ],
  "dataGroups": [
    {
      "name": "api-freshness",
      "urls": [
        "/api/**"
      ],
      "cacheConfig": {
        "strategy": "freshness",
        "maxSize": 100,
        "maxAge": "3d",
        "timeout": "10s"
      }
    },
    {
      "name": "api-performance",
      "urls": [
        "/api/tasks/**",
        "/api/projects/**"
      ],
      "cacheConfig": {
        "strategy": "performance",
        "maxSize": 100,
        "maxAge": "1d"
      }
    }
  ]
}
```

### 4. Offline Data Management

```typescript
// services/offline-storage.service.ts
import { Injectable } from '@angular/core';
import { NgRx } from '@ngrx/store';

@Injectable({
  providedIn: 'root'
})
export class OfflineStorageService {
  private readonly DB_NAME = 'taskManagerDB';
  private readonly DB_VERSION = 1;
  private db: IDBDatabase | null = null;

  async initDatabase(): Promise<void> {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.DB_NAME, this.DB_VERSION);
      
      request.onerror = () => reject(request.error);
      request.onsuccess = () => {
        this.db = request.result;
        resolve();
      };
      
      request.onupgradeneeded = (event) => {
        const db = (event.target as IDBOpenDBRequest).result;
        
        // Create object stores
        if (!db.objectStoreNames.contains('tasks')) {
          const taskStore = db.createObjectStore('tasks', { keyPath: 'id' });
          taskStore.createIndex('status', 'status', { unique: false });
          taskStore.createIndex('projectId', 'projectId', { unique: false });
        }
        
        if (!db.objectStoreNames.contains('projects')) {
          const projectStore = db.createObjectStore('projects', { keyPath: 'id' });
        }
        
        if (!db.objectStoreNames.contains('syncQueue')) {
          const syncStore = db.createObjectStore('syncQueue', { keyPath: 'id', autoIncrement: true });
          syncStore.createIndex('type', 'type', { unique: false });
          syncStore.createIndex('timestamp', 'timestamp', { unique: false });
        }
      };
    });
  }

  async saveTasks(tasks: any[]): Promise<void> {
    if (!this.db) throw new Error('Database not initialized');
    
    const transaction = this.db.transaction(['tasks'], 'readwrite');
    const store = transaction.objectStore('tasks');
    
    for (const task of tasks) {
      await store.put(task);
    }
  }

  async getTasks(): Promise<any[]> {
    if (!this.db) throw new Error('Database not initialized');
    
    return new Promise((resolve, reject) => {
      const transaction = this.db!.transaction(['tasks'], 'readonly');
      const store = transaction.objectStore('tasks');
      const request = store.getAll();
      
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }

  async addToSyncQueue(action: any): Promise<void> {
    if (!this.db) throw new Error('Database not initialized');
    
    const transaction = this.db.transaction(['syncQueue'], 'readwrite');
    const store = transaction.objectStore('syncQueue');
    
    await store.add({
      action,
      type: action.type,
      timestamp: Date.now(),
      retryCount: 0
    });
  }

  async getSyncQueue(): Promise<any[]> {
    if (!this.db) throw new Error('Database not initialized');
    
    return new Promise((resolve, reject) => {
      const transaction = this.db!.transaction(['syncQueue'], 'readonly');
      const store = transaction.objectStore('syncQueue');
      const request = store.getAll();
      
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }
}
```

### 5. Network Status Service

```typescript
// services/network-status.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NetworkStatusService {
  private online$ = new BehaviorSubject<boolean>(navigator.onLine);
  private connectionType$ = new BehaviorSubject<string>('unknown');

  constructor() {
    this.initializeNetworkMonitoring();
  }

  private initializeNetworkMonitoring(): void {
    window.addEventListener('online', () => {
      this.online$.next(true);
      this.updateConnectionType();
    });

    window.addEventListener('offline', () => {
      this.online$.next(false);
      this.connectionType$.next('offline');
    });

    // Monitor connection changes
    if ('connection' in navigator) {
      const connection = (navigator as any).connection;
      connection.addEventListener('change', () => {
        this.updateConnectionType();
      });
      this.updateConnectionType();
    }
  }

  private updateConnectionType(): void {
    if ('connection' in navigator) {
      const connection = (navigator as any).connection;
      this.connectionType$.next(connection.effectiveType || 'unknown');
    }
  }

  get isOnline$(): Observable<boolean> {
    return this.online$.asObservable();
  }

  get isOnline(): boolean {
    return this.online$.value;
  }

  get connectionType$(): Observable<string> {
    return this.connectionType$.asObservable();
  }

  get connectionType(): string {
    return this.connectionType$.value;
  }
}
```

### 6. Sync Service

```typescript
// services/sync.service.ts
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { NetworkStatusService } from './network-status.service';
import { OfflineStorageService } from './offline-storage.service';
import { takeUntil, filter } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SyncService {
  private destroy$ = new Subject<void>();
  private syncInProgress = false;

  constructor(
    private store: Store,
    private networkStatus: NetworkStatusService,
    private offlineStorage: OfflineStorageService
  ) {
    this.initializeSync();
  }

  private initializeSync(): void {
    // Sync when coming back online
    this.networkStatus.isOnline$
      .pipe(
        filter(online => online && !this.syncInProgress),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.syncPendingChanges();
      });

    // Periodic sync when online
    setInterval(() => {
      if (this.networkStatus.isOnline && !this.syncInProgress) {
        this.syncPendingChanges();
      }
    }, 30000); // Sync every 30 seconds
  }

  async syncPendingChanges(): Promise<void> {
    if (this.syncInProgress) return;

    this.syncInProgress = true;
    
    try {
      const syncQueue = await this.offlineStorage.getSyncQueue();
      
      for (const item of syncQueue) {
        try {
          // Process sync item based on action type
          await this.processSyncItem(item);
          
          // Remove from queue on success
          await this.removeFromSyncQueue(item.id);
        } catch (error) {
          console.error('Sync item failed:', error);
          
          // Increment retry count
          item.retryCount++;
          
          // Remove if max retries reached
          if (item.retryCount >= 3) {
            await this.removeFromSyncQueue(item.id);
          }
        }
      }
    } finally {
      this.syncInProgress = false;
    }
  }

  private async processSyncItem(item: any): Promise<void> {
    // Implement based on action type
    switch (item.action.type) {
      case '[Tasks] Create Task':
        // Send to API
        break;
      case '[Tasks] Update Task':
        // Send to API
        break;
      case '[Tasks] Delete Task':
        // Send to API
        break;
      default:
        throw new Error(`Unknown action type: ${item.action.type}`);
    }
  }

  private async removeFromSyncQueue(id: number): Promise<void> {
    // Implementation to remove item from sync queue
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

## Best Practices

### 1. Performance Optimization
- Use appropriate caching strategies for different resource types
- Implement lazy loading for non-critical resources
- Optimize bundle size and implement code splitting

### 2. Offline Experience
- Provide meaningful offline feedback to users
- Cache essential data for offline access
- Implement graceful degradation for offline features

### 3. Data Synchronization
- Use optimistic updates for better UX
- Implement conflict resolution strategies
- Queue offline actions for later synchronization

### 4. User Experience
- Show network status indicators
- Provide clear feedback about sync status
- Implement retry mechanisms with exponential backoff

### 5. Security
- Validate all cached data
- Implement proper CORS policies
- Use HTTPS for all network requests

## Testing PWA Features

### 1. Service Worker Testing
```typescript
// service-worker.spec.ts
describe('Service Worker', () => {
  beforeEach(() => {
    // Mock service worker
  });

  it('should cache assets', async () => {
    // Test caching behavior
  });

  it('should handle offline requests', async () => {
    // Test offline functionality
  });
});
```

### 2. Offline Storage Testing
```typescript
// offline-storage.service.spec.ts
describe('OfflineStorageService', () => {
  let service: OfflineStorageService;

  beforeEach(async () => {
    service = TestBed.inject(OfflineStorageService);
    await service.initDatabase();
  });

  it('should save and retrieve tasks', async () => {
    const tasks = [{ id: '1', title: 'Test Task' }];
    await service.saveTasks(tasks);
    
    const retrieved = await service.getTasks();
    expect(retrieved).toEqual(tasks);
  });
});
```

## Deployment Considerations

### 1. HTTPS Requirement
- PWAs require HTTPS for service worker functionality
- Implement proper SSL certificates
- Use HSTS headers for security

### 2. Cache Invalidation
- Implement proper cache versioning
- Use cache busting strategies
- Provide cache update mechanisms

### 3. Monitoring
- Monitor service worker registration success
- Track offline usage patterns
- Monitor sync queue performance

## Resources

- [Angular PWA Documentation](https://angular.io/guide/service-worker-getting-started)
- [Web App Manifest](https://developer.mozilla.org/en-US/docs/Web/Manifest)
- [Service Worker API](https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API)
- [IndexedDB API](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API)
