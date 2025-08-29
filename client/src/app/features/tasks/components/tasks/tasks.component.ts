import { Component, OnInit, inject, OnDestroy, HostListener, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '@services';
import { User, type TaskFilters, type TaskSortOption } from '@models';
import { Subscription } from 'rxjs';
import { LayoutComponent } from '../../../../shared/components/layout';
import { LucideAngularModule, CheckSquare, Plus, Move, Calendar } from 'lucide-angular';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { Store } from '@ngrx/store';
import { selectSortedTasks, selectTasksLoading, selectTasksFilters, selectTasksSortBy, updateFilters, updateSort, loadTasks } from '@store';

import { TaskListComponent } from '../task-list/task-list.component';
import { TaskFiltersComponent } from '../task-filters/task-filters.component';
import { TaskAdvancedFiltersComponent } from '../task-advanced-filters/task-advanced-filters.component';
import { TaskCardComponent } from '../task-card/task-card.component';

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss'],
  standalone: true,
  imports: [
    CommonModule, 
    LayoutComponent, 
    LucideAngularModule, 
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatDividerModule,
    TaskListComponent, 
    TaskFiltersComponent, 
    TaskAdvancedFiltersComponent,
    TaskCardComponent
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TasksComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly store = inject(Store);

  isAuthenticated = false;
  currentUser: User | null = null;
  private authSubscription?: Subscription;
  private userSubscription?: Subscription;
  tasks$ = this.store.select(selectSortedTasks);
  loading$ = this.store.select(selectTasksLoading);
  selectTasksFilters = this.store.select(selectTasksFilters);
  selectTasksSortBy = this.store.select(selectTasksSortBy);

  // Mobile menu state
  isMobileMenuOpen = false;

  // Lucide icons
  readonly CheckSquare = CheckSquare;
  readonly Plus = Plus;
  readonly Move = Move;
  readonly Calendar = Calendar;

  ngOnInit(): void {
    this.authSubscription = this.authService.isAuthenticated$.subscribe(isAuth => {
      this.isAuthenticated = isAuth;
    });

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
    this.authSubscription?.unsubscribe();
    this.userSubscription?.unsubscribe();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  changePassword(): void {
    // TODO: Implement change password functionality
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.nav') && this.isMobileMenuOpen) {
      this.isMobileMenuOpen = false;
    }
  }
}
