import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { TaskFilters } from '@models';

@Component({
  selector: 'app-task-advanced-filters',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatChipsModule
  ],
  templateUrl: './task-advanced-filters.component.html',
  styleUrls: ['./task-advanced-filters.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskAdvancedFiltersComponent implements OnInit, OnDestroy {
  @Input() filters: Partial<TaskFilters> = {};
  @Output() filtersChange = new EventEmitter<Partial<TaskFilters>>();
  @Output() clearFilters = new EventEmitter<void>();

  private readonly destroy$ = new Subject<void>();
  filterForm!: FormGroup;
  activeFiltersCount = 0;

  statuses = ['all', 'backlog', 'in-progress', 'done', 'blocked'];
  priorities = ['all', 'low', 'medium', 'high', 'urgent'];

  constructor(private readonly fb: FormBuilder) {}

  ngOnInit(): void {
    this.initializeForm();
    this.setupFormSubscriptions();
    this.updateActiveFiltersCount();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForm(): void {
    this.filterForm = this.fb.group({
      query: [this.filters.query || ''],
      status: [this.filters.status || 'all'],
      priority: [this.filters.priority || 'all'],
      assignee: [this.filters.assignee || 'all'],
      project: [this.filters.project || 'all'],
      dueDateFrom: [null],
      dueDateTo: [null],
      createdFrom: [null],
      createdTo: [null]
    });
  }

  private setupFormSubscriptions(): void {
    // Debounce text input
    this.filterForm.get('query')?.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(query => {
        this.emitFilters({ query });
      });

    // Immediate emit for selects
    ['status', 'priority', 'assignee', 'project'].forEach(field => {
      this.filterForm.get(field)?.valueChanges
        .pipe(takeUntil(this.destroy$))
        .subscribe(value => {
          this.emitFilters({ [field]: value });
        });
    });

    // Date range filters
    ['dueDateFrom', 'dueDateTo', 'createdFrom', 'createdTo'].forEach(field => {
      this.filterForm.get(field)?.valueChanges
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          this.emitDateRangeFilters();
        });
    });
  }

  private emitFilters(filters: Partial<TaskFilters>): void {
    this.filtersChange.emit(filters);
    this.updateActiveFiltersCount();
  }

  private emitDateRangeFilters(): void {
    const dateFilters: any = {};
    const { dueDateFrom, dueDateTo, createdFrom, createdTo } = this.filterForm.value;

    if (dueDateFrom || dueDateTo) {
      dateFilters.dueDateRange = {
        from: dueDateFrom,
        to: dueDateTo
      };
    }

    if (createdFrom || createdTo) {
      dateFilters.createdDateRange = {
        from: createdFrom,
        to: createdTo
      };
    }

    this.emitFilters(dateFilters);
  }

  private updateActiveFiltersCount(): void {
    const values = this.filterForm.value;
    let count = 0;

    if (values.query?.trim()) count++;
    if (values.status !== 'all') count++;
    if (values.priority !== 'all') count++;
    if (values.assignee !== 'all') count++;
    if (values.project !== 'all') count++;
    if (values.dueDateFrom || values.dueDateTo) count++;
    if (values.createdFrom || values.createdTo) count++;

    this.activeFiltersCount = count;
  }

  onClearFilters(): void {
    this.filterForm.reset({
      query: '',
      status: 'all',
      priority: 'all',
      assignee: 'all',
      project: 'all',
      dueDateFrom: null,
      dueDateTo: null,
      createdFrom: null,
      createdTo: null
    });
    this.clearFilters.emit();
    this.updateActiveFiltersCount();
  }

  removeFilter(field: string): void {
    const resetValue = ['status', 'priority', 'assignee', 'project'].includes(field) ? 'all' : '';
    this.filterForm.get(field)?.setValue(resetValue);
  }
}
