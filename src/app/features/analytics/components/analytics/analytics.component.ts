import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '@shared/material.module';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, MaterialModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnalyticsComponent {
  // Analytics component implementation
} 