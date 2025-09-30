import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { NetworkStatusService } from '../../services/network-status.service';

@Component({
  selector: 'app-network-status',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatChipsModule
  ],
  template: `
    <mat-chip-set>
      <mat-chip 
        [color]="isOnline ? 'primary' : 'warn'"
        [class]="isOnline ? 'online' : 'offline'">
        <mat-icon>{{ isOnline ? 'wifi' : 'wifi_off' }}</mat-icon>
        {{ isOnline ? 'Online' : 'Offline' }}
      </mat-chip>
    </mat-chip-set>
  `,
  styles: [`
    .online {
      background-color: #4caf50 !important;
      color: white !important;
    }
    
    .offline {
      background-color: #f44336 !important;
      color: white !important;
    }
    
    mat-chip {
      font-size: 12px;
      height: 24px;
    }
    
    mat-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
      margin-right: 4px;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NetworkStatusComponent {
  private readonly networkStatusService = inject(NetworkStatusService);
  
  get isOnline(): boolean {
    return this.networkStatusService.isOnline;
  }
}