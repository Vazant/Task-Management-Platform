import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NetworkStatusService } from '../../services/network-status.service';
import { SyncService } from '../../services/sync.service';
import { OfflineStorageService, SyncQueueItem } from '../../services/offline-storage.service';
import { Wifi, WifiOff, RefreshCw, AlertCircle, CheckCircle } from 'lucide-angular';
import { Observable, BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-network-status',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatTooltipModule,
    MatChipsModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="network-status" [class.offline]="!(isOnline$ | async)">
      <mat-chip-set>
        <!-- Network Status -->
        <mat-chip 
          [color]="(isOnline$ | async) ? 'accent' : 'warn'"
          [disabled]="!(isOnline$ | async)">
          <mat-icon>
            <lucide-icon [icon]="(isOnline$ | async) ? wifiIcon : wifiOffIcon" />
          </mat-icon>
          {{ (isOnline$ | async) ? 'Online' : 'Offline' }}
        </mat-chip>

        <!-- Connection Type -->
        <mat-chip 
          *ngIf="(isOnline$ | async) && (connectionType$ | async) !== 'unknown'"
          color="primary">
          {{ (connectionType$ | async) | titlecase }}
        </mat-chip>

        <!-- Sync Status -->
        <mat-chip 
          *ngIf="(syncQueueCount$ | async) > 0"
          color="warn">
          <mat-icon>
            <lucide-icon [icon]="alertCircleIcon" />
          </mat-icon>
          {{ (syncQueueCount$ | async) }} pending
        </mat-chip>

        <!-- Sync Success -->
        <mat-chip 
          *ngIf="(lastSyncSuccess$ | async)"
          color="accent">
          <mat-icon>
            <lucide-icon [icon]="checkCircleIcon" />
          </mat-icon>
          Synced
        </mat-chip>
      </mat-chip-set>

      <!-- Sync Actions -->
      <div class="sync-actions" *ngIf="(syncQueueCount$ | async) > 0">
        <button 
          mat-icon-button
          [disabled]="!(isOnline$ | async) || (syncing$ | async)"
          (click)="manualSync()"
          matTooltip="Sync pending changes">
          <mat-icon>
            <lucide-icon 
              [icon]="refreshIcon" 
              [class.spinning]="syncing$ | async" />
          </mat-icon>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .network-status {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px;
      border-radius: 8px;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      transition: all 0.3s ease;

      &.offline {
        background: rgba(244, 67, 54, 0.1);
        border: 1px solid rgba(244, 67, 54, 0.3);
      }
    }

    mat-chip-set {
      display: flex;
      gap: 4px;
    }

    mat-chip {
      font-size: 12px;
      height: 24px;
      
      mat-icon {
        font-size: 14px;
        width: 14px;
        height: 14px;
        margin-right: 4px;
      }
    }

    .sync-actions {
      display: flex;
      align-items: center;
    }

    .spinning {
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }

    @media (max-width: 768px) {
      .network-status {
        flex-direction: column;
        align-items: flex-start;
        gap: 4px;
      }

      mat-chip-set {
        flex-wrap: wrap;
      }
    }
  `]
})
export class NetworkStatusComponent {
  private readonly networkStatus = inject(NetworkStatusService);
  private readonly syncService = inject(SyncService);
  private readonly offlineStorage = inject(OfflineStorageService);

  // Icons
  readonly wifiIcon = Wifi;
  readonly wifiOffIcon = WifiOff;
  readonly refreshIcon = RefreshCw;
  readonly alertCircleIcon = AlertCircle;
  readonly checkCircleIcon = CheckCircle;

  // Observables
  readonly isOnline$ = this.networkStatus.isOnline$;
  readonly connectionType$ = this.networkStatus.connectionType$;
  readonly syncQueueCount$ = new BehaviorSubject<number>(0);
  readonly syncing$ = new BehaviorSubject<boolean>(false);
  readonly lastSyncSuccess$ = new BehaviorSubject<boolean>(false);

  constructor() {
    this.initializeSyncMonitoring();
  }

  private async initializeSyncMonitoring(): Promise<void> {
    // Monitor sync queue
    setInterval(async () => {
      try {
        const queue = await this.syncService.getSyncQueue();
        this.syncQueueCount$.next(queue.length);
      } catch (error) {
        console.error('Failed to get sync queue:', error);
      }
    }, 5000);
  }

  async manualSync(): Promise<void> {
    if (!this.networkStatus.isOnline) return;

    this.syncing$.next(true);
    
    try {
      await this.syncService.syncPendingChanges();
      this.lastSyncSuccess$.next(true);
      
      // Reset success indicator after 3 seconds
      setTimeout(() => {
        this.lastSyncSuccess$.next(false);
      }, 3000);
    } catch (error) {
      console.error('Manual sync failed:', error);
    } finally {
      this.syncing$.next(false);
    }
  }
}
