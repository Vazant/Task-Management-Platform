import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { NetworkStatusService } from '../../services/network-status.service';

@Component({
  selector: 'app-network-status',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  template: `
    <div class="network-status" [class.offline]="!(isOnline$ | async)">
      <mat-icon [class.offline-icon]="!(isOnline$ | async)">
        {{ (isOnline$ | async) ? 'wifi' : 'wifi_off' }}
      </mat-icon>
      <span class="status-text">
        {{ (isOnline$ | async) ? 'Online' : 'Offline' }}
      </span>
    </div>
  `,
  styles: [`
    .network-status {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      border-radius: 6px;
      background: #e8f5e8;
      color: #2e7d32;
      font-size: 14px;
      font-weight: 500;
      transition: all 0.3s ease;
    }

    .network-status.offline {
      background: #ffebee;
      color: #c62828;
    }

    .network-status mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
    }

    .network-status .offline-icon {
      color: #c62828;
    }

    .status-text {
      font-size: 12px;
    }
  `]
})
export class NetworkStatusComponent implements OnInit, OnDestroy {
  isOnline$: Observable<boolean>;
  private destroy$ = new Subject<void>();

  constructor(private networkStatus: NetworkStatusService) {
    this.isOnline$ = this.networkStatus.isOnline$;
  }

  ngOnInit(): void {
    // Component initialization
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
