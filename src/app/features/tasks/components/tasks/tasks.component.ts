import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '@shared/material.module';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [CommonModule, MaterialModule],
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TasksComponent {
  // Tasks component implementation
} 