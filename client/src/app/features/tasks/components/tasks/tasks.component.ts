import { Component, OnInit, inject, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '@services';
import { User, type TaskFilters, type TaskSortOption } from '@models';
import { Subscription } from 'rxjs';
import { LayoutComponent } from '../../../../shared/components/layout';
import { LucideAngularModule, CheckSquare } from 'lucide-angular';
import { Store } from '@ngrx/store';
import { selectSortedTasks, selectTasksLoading, selectTasksFilters, selectTasksSortBy, updateFilters, updateSort, loadTasks } from '@store';

import { TaskListComponent } from '../task-list/task-list.component';
import { TaskFiltersComponent } from '../task-filters/task-filters.component';
import { TaskAdvancedFiltersComponent } from '../task-advanced-filters/task-advanced-filters.component';

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss'],
  standalone: true,
  imports: [
    CommonModule, 
    LayoutComponent, 
    LucideAngularModule,
    TaskListComponent, 
    TaskFiltersComponent, 
    TaskAdvancedFiltersComponent
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TasksComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly store = inject(Store);

  currentUser: User | null = null;
  private userSubscription?: Subscription;
  tasks$ = this.store.select(selectSortedTasks);
  loading$ = this.store.select(selectTasksLoading);
  selectTasksFilters = this.store.select(selectTasksFilters);
  selectTasksSortBy = this.store.select(selectTasksSortBy);

  // Lucide icons
  readonly CheckSquare = CheckSquare;

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    this.store.dispatch(loadTasks());
  }

  onFiltersChange(partial: Partial<TaskFilters>): void {
    this.store.dispatch(updateFilters({ filters: partial }));
  }

  onSortChange(sortBy: TaskSortOption): void {
    this.store.dispatch(updateSort({ sortBy }));
  }

  ngOnDestroy(): void {
    this.userSubscription?.unsubscribe();
  }
}
