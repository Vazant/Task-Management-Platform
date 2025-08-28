import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { TaskFilters, TaskSortOption } from '@models';

@Component({
  selector: 'app-task-filters',
  standalone: true,
  imports: [CommonModule, FormsModule, MatFormFieldModule, MatSelectModule, MatInputModule],
  templateUrl: './task-filters.component.html',
  styleUrls: ['./task-filters.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskFiltersComponent {
  @Input() filters: TaskFilters = { status: 'all', priority: 'all', assignee: 'all', project: 'all' };
  @Input() sortBy: TaskSortOption = 'created';
  @Output() filtersChange = new EventEmitter<Partial<TaskFilters>>();
  @Output() sortChange = new EventEmitter<TaskSortOption>();

  statuses: TaskFilters['status'][] = ['all', 'backlog', 'in-progress', 'done'];
  priorities: TaskFilters['priority'][] = ['all', 'low', 'medium', 'high', 'urgent'];
  sortOptions: TaskSortOption[] = ['created', 'updated', 'priority', 'dueDate', 'title'];
}

