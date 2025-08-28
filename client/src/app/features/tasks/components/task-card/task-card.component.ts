import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { LucideAngularModule, Clock, Calendar, User, MoreVertical } from 'lucide-angular';
import { Task } from '@models';

@Component({
  selector: 'app-task-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatMenuModule,
    LucideAngularModule
  ],
  templateUrl: './task-card.component.html',
  styleUrls: ['./task-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskCardComponent {
  @Input() task!: Task;
  @Input() compact = false;
  @Output() edit = new EventEmitter<Task>();
  @Output() delete = new EventEmitter<Task>();
  @Output() statusChange = new EventEmitter<{ task: Task; status: Task['status'] }>();
  @Output() priorityChange = new EventEmitter<{ task: Task; priority: Task['priority'] }>();

  readonly Clock = Clock;
  readonly Calendar = Calendar;
  readonly User = User;
  readonly MoreVertical = MoreVertical;

  getStatusColor(status: Task['status']): string {
    const colors: Record<Task['status'], string> = {
      'backlog': 'accent',
      'in-progress': 'primary',
      'done': 'success',
      'blocked': 'warn'
    };
    return colors[status] || 'accent';
  }

  getPriorityColor(priority: Task['priority']): string {
    const colors: Record<Task['priority'], string> = {
      'low': '',
      'medium': 'accent',
      'high': 'warn',
      'urgent': 'warn'
    };
    return colors[priority] || '';
  }

  getPriorityIcon(priority: Task['priority']): string {
    const icons: Record<Task['priority'], string> = {
      'low': 'arrow_downward',
      'medium': 'remove',
      'high': 'arrow_upward',
      'urgent': 'priority_high'
    };
    return icons[priority] || 'remove';
  }

  formatDate(date: Date | string | undefined): string {
    if (!date) return 'No due date';
    const d = new Date(date);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }

  isOverdue(date: Date | string | undefined): boolean {
    if (!date) return false;
    return new Date(date) < new Date();
  }

  onStatusChange(status: Task['status']): void {
    this.statusChange.emit({ task: this.task, status });
  }

  onPriorityChange(priority: Task['priority']): void {
    this.priorityChange.emit({ task: this.task, priority });
  }
}
