import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '@shared/material.module';

@Component({
  selector: 'app-time-tracking',
  standalone: true,
  imports: [CommonModule, MaterialModule],
  templateUrl: './time-tracking.component.html',
  styleUrls: ['./time-tracking.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimeTrackingComponent {
  // Time tracking component implementation
} 