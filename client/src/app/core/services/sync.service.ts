import { Injectable, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { NetworkStatusService } from './network-status.service';
import { OfflineStorageService, SyncQueueItem } from './offline-storage.service';
import { takeUntil, filter } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SyncService implements OnDestroy {
  private readonly destroy$ = new Subject<void>();
  private syncInProgress = false;

  constructor(
    private readonly store: Store,
    private readonly networkStatus: NetworkStatusService,
    private readonly offlineStorage: OfflineStorageService
  ) {
    this.initializeSync();
  }

  private initializeSync(): void {
    // Initialize database
    this.offlineStorage.initDatabase().catch(console.error);

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
          await this.offlineStorage.removeFromSyncQueue(item.id!);
        } catch (error) {
          console.error('Sync item failed:', error);
          
          // Increment retry count
          item.retryCount++;
          
          // Remove if max retries reached
          if (item.retryCount >= 3) {
            await this.offlineStorage.removeFromSyncQueue(item.id!);
          } else {
            // Update retry count
            await this.offlineStorage.updateSyncQueueItem(item);
          }
        }
      }
    } finally {
      this.syncInProgress = false;
    }
  }

  private async processSyncItem(item: SyncQueueItem): Promise<void> {
    // Implement based on action type
    switch (item.action.type) {
      case '[Tasks] Create Task':
        // Send to API
        console.log('Processing create task:', item.action.payload);
        break;
      case '[Tasks] Update Task':
        // Send to API
        console.log('Processing update task:', item.action.payload);
        break;
      case '[Tasks] Delete Task':
        // Send to API
        console.log('Processing delete task:', item.action.payload);
        break;
      case '[Projects] Create Project':
        // Send to API
        console.log('Processing create project:', item.action.payload);
        break;
      case '[Projects] Update Project':
        // Send to API
        console.log('Processing update project:', item.action.payload);
        break;
      case '[Projects] Delete Project':
        // Send to API
        console.log('Processing delete project:', item.action.payload);
        break;
      default:
        throw new Error(`Unknown action type: ${item.action.type}`);
    }
  }

  async addToSyncQueue(action: any): Promise<void> {
    await this.offlineStorage.addToSyncQueue(action);
  }

  async getSyncQueue(): Promise<SyncQueueItem[]> {
    return this.offlineStorage.getSyncQueue();
  }

  async clearSyncQueue(): Promise<void> {
    return this.offlineStorage.clearSyncQueue();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
