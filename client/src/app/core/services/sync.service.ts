import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

import { OfflineStorageService } from './offline-storage.service';
import { NetworkStatusService } from './network-status.service';

export interface SyncItem {
  id: string;
  type: 'task' | 'project' | 'user';
  action: 'create' | 'update' | 'delete';
  data: any;
  timestamp: number;
  retryCount: number;
}

export interface SyncQueueItem {
  id?: string;
  action: {
    type: string;
    payload: any;
  };
  timestamp: number;
  retryCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class SyncService implements OnDestroy {
  private readonly syncQueue$ = new BehaviorSubject<SyncItem[]>([]);
  private readonly isSyncing$ = new BehaviorSubject<boolean>(false);
  private readonly destroy$ = new Subject<void>();
  private syncInProgress = false;

  constructor(
    private offlineStorage: OfflineStorageService,
    private networkStatus: NetworkStatusService
  ) {
    this.initializeSync();
  }

  get syncQueue(): Observable<SyncItem[]> {
    return this.syncQueue$.asObservable();
  }

  get isSyncing(): Observable<boolean> {
    return this.isSyncing$.asObservable();
  }

  addToSyncQueue(item: Omit<SyncItem, 'id' | 'timestamp' | 'retryCount'>): void {
    const syncItem: SyncItem = {
      ...item,
      id: crypto.randomUUID(),
      timestamp: Date.now(),
      retryCount: 0
    };

    const currentQueue = this.syncQueue$.value;
    this.syncQueue$.next([...currentQueue, syncItem]);
  }

  async processSyncQueue(): Promise<void> {
    if (this.syncInProgress) return;

    this.syncInProgress = true;
    this.isSyncing$.next(true);
    
    try {
      const syncQueue = await this.offlineStorage.getSyncQueue();
      
      for (const item of syncQueue) {
        try {
          await this.processSyncItem(item);
          await this.offlineStorage.removeFromSyncQueue(item.id!);
        } catch (error) {
          console.error('Sync item failed:', error);
          
          item.retryCount++;
          
          if (item.retryCount >= 3) {
            await this.offlineStorage.removeFromSyncQueue(item.id!);
          } else {
            await this.offlineStorage.updateSyncQueueItem(item);
          }
        }
      }
    } finally {
      this.syncInProgress = false;
      this.isSyncing$.next(false);
    }
  }

  private async processSyncItem(item: SyncQueueItem): Promise<void> {
    switch (item.action.type) {
      case '[Tasks] Create Task':
        break;
      case '[Tasks] Update Task':
        break;
      case '[Tasks] Delete Task':
        break;
      case '[Projects] Create Project':
        break;
      case '[Projects] Update Project':
        break;
      case '[Projects] Delete Project':
        break;
      default:
        throw new Error(`Unknown action type: ${item.action.type}`);
    }
  }

  private initializeSync(): void {
    this.networkStatus.isOnline$
      .pipe(
        filter(online => online && !this.syncInProgress),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.processSyncQueue();
      });

    setInterval(() => {
      if (this.networkStatus.isOnline && !this.syncInProgress) {
        this.processSyncQueue();
      }
    }, 30000);
  }

  async addToSyncQueueAction(action: any): Promise<void> {
    await this.offlineStorage.addToSyncQueue(action);
  }

  async getSyncQueueItems(): Promise<SyncQueueItem[]> {
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
