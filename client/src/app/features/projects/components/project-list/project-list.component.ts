import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  HostListener,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import {
  LucideAngularModule,
  Search,
  Filter,
  SortAsc,
  SortDesc,
  Plus,
  RefreshCw,
  Grid,
  List,
  MoreHorizontal
} from 'lucide-angular';
import {
  Subject,
  Observable,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  takeUntil,
  switchMap,
  catchError,
  of,
  shareReplay,
  startWith,
  map
} from 'rxjs';
import { Store } from '@ngrx/store';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Project } from '@models';
import {
  ProjectListState,
  ProjectFilters,
  ProjectSort,
  ProjectPagination,
  ProjectListConfig,
  ProjectAction,
  ProjectStatus
} from '../../models';
import { ProjectCardComponent } from '../project-card/project-card.component';
import { ProjectListService } from '../../services/project-list.service';
import * as ProjectListActions from '../../store/project-list.actions';
import * as ProjectListSelectors from '../../store/project-list.selectors';

/**
 * Контейнерный компонент списка проектов
 * Управляет состоянием, фильтрацией, сортировкой и пагинацией
 */
@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatChipsModule,
    MatTooltipModule,
    MatDialogModule,
    MatSnackBarModule,
    LucideAngularModule,
    ProjectCardComponent
  ],
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectListComponent implements OnInit, OnDestroy {

  // Input properties
  @Input() config: Partial<ProjectListConfig> = {};
  @Input() enableSelection = false;
  @Input() enableInfiniteScroll = true;
  @Input() enableFilters = true;
  @Input() enableSorting = true;
  @Input() enableSearch = true;
  @Input() enableActions = true;
  @Input() pageSize = 12;
  @Input() debounceTime = 300;

  // Output events
  @Output() projectSelected = new EventEmitter<Project>();
  @Output() projectAction = new EventEmitter<ProjectAction>();
  @Output() selectionChanged = new EventEmitter<string[]>();

  // ViewChild для infinite scroll
  @ViewChild('scrollContainer', { static: false }) scrollContainer!: ElementRef;

  // Public properties
  public readonly icons = {
    search: Search,
    filter: Filter,
    sortAsc: SortAsc,
    sortDesc: SortDesc,
    plus: Plus,
    refresh: RefreshCw,
    grid: Grid,
    list: List,
    more: MoreHorizontal
  };

  public readonly statusOptions = [
    { value: 'all', label: 'Все статусы' },
    { value: 'active', label: 'Активные' },
    { value: 'completed', label: 'Завершенные' },
    { value: 'on-hold', label: 'На паузе' },
    { value: 'archived', label: 'Архивные' }
  ];

  public readonly sortOptions = [
    { value: 'name', label: 'По названию' },
    { value: 'createdAt', label: 'По дате создания' },
    { value: 'updatedAt', label: 'По дате обновления' },
    { value: 'status', label: 'По статусу' }
  ];

  public readonly viewModes = [
    { value: 'grid', label: 'Сетка', icon: Grid },
    { value: 'list', label: 'Список', icon: List }
  ];

  // Form controls
  public searchForm: FormGroup;
  public filtersForm: FormGroup;

  // State observables
  public projects$: Observable<Project[]>;
  public loading$: Observable<boolean>;
  public error$: Observable<boolean>;
  public pagination$: Observable<{
    currentPage: number;
    pageSize: number;
    total: number;
    hasMore: boolean;
    currentCount: number;
    totalPages: number;
  }>;
  public selectedProjectIds$: Observable<string[]>;
  public hasMoreProjects$: Observable<boolean>;

  // Local state
  public selectedProjectIds: string[] = [];
  public hoveredProjectId: string | null = null;
  public viewMode: 'grid' | 'list' = 'grid';
  public showFilters = false;
  public isLoadingMore = false;

  // Private properties
  private readonly destroy$ = new Subject<void>();
  private readonly defaultConfig: ProjectListConfig = {
    enableSelection: false,
    enableInfiniteScroll: true,
    enableFilters: true,
    enableSorting: true,
    enableSearch: true,
    enableActions: true,
    pageSize: 12,
    debounceTime: 300
  };

  constructor(
    private store: Store,
    private projectListService: ProjectListService,
    private fb: FormBuilder
  ) {
    // Инициализация форм
    this.searchForm = this.fb.group({
      searchTerm: ['', [Validators.maxLength(100)]]
    });

    this.filtersForm = this.fb.group({
      status: ['all'],
      dateRange: this.fb.group({
        start: [null],
        end: [null]
      })
    });

    // Инициализация observables
    this.projects$ = this.store.select(ProjectListSelectors.selectAllProjects);
    this.loading$ = this.store.select(ProjectListSelectors.selectIsLoading);
    this.error$ = this.store.select(ProjectListSelectors.selectHasError);
    this.pagination$ = this.store.select(ProjectListSelectors.selectPaginationInfo);
    this.selectedProjectIds$ = this.store.select(ProjectListSelectors.selectSelectedProjectIds);
    this.hasMoreProjects$ = this.store.select(ProjectListSelectors.selectHasMoreProjects);
  }

  ngOnInit(): void {
    this.initializeComponent();
    this.setupFormSubscriptions();

    // Временно эмулируем пустой список для тестирования empty state
    // this.loadInitialData();

    // Эмулируем загрузку с тестовыми данными для демонстрации UI
    setTimeout(() => {
      const mockProjects: Project[] = [
        {
          id: '1',
          name: 'Веб-приложение TaskBoard',
          description: 'Современная платформа для управления задачами с красивым UI',
          status: 'active',
          ownerId: 'user1',
          members: ['user1', 'user2', 'user3'],
          settings: {
            allowGuestAccess: false,
            defaultTaskPriority: 'medium',
            autoAssignTasks: true,
            requireTimeTracking: true
          },
          createdAt: new Date('2024-01-15'),
          updatedAt: new Date('2024-01-20')
        },
        {
          id: '2',
          name: 'Мобильное приложение',
          description: 'React Native приложение для iOS и Android',
          status: 'on-hold',
          ownerId: 'user1',
          members: ['user1', 'user4'],
          settings: {
            allowGuestAccess: true,
            defaultTaskPriority: 'high',
            autoAssignTasks: false,
            requireTimeTracking: false
          },
          createdAt: new Date('2024-01-10'),
          updatedAt: new Date('2024-01-18')
        },
        {
          id: '3',
          name: 'UI/UX Дизайн системы',
          description: 'Создание дизайн-системы и компонентов',
          status: 'completed',
          ownerId: 'user2',
          members: ['user2', 'user5'],
          settings: {
            allowGuestAccess: false,
            defaultTaskPriority: 'low',
            autoAssignTasks: true,
            requireTimeTracking: false
          },
          createdAt: new Date('2023-12-01'),
          updatedAt: new Date('2024-01-15')
        }
      ];

      this.store.dispatch(ProjectListActions.loadProjectsSuccess({
        response: {
          projects: mockProjects,
          total: mockProjects.length,
          hasMore: false
        },
        append: false
      }));
    }, 1500);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Event handlers
  @HostListener('window:scroll')
  onWindowScroll(): void {
    if (!this.enableInfiniteScroll || this.isLoadingMore) return;

    this.checkInfiniteScroll();
  }

  // Public methods
  onProjectClick(project: Project): void {
    this.projectSelected.emit(project);
  }

  onProjectAction(action: ProjectAction): void {
    this.projectAction.emit(action);
  }

  onProjectSelect(selection: { projectId: string; selected: boolean }): void {
    if (selection.selected) {
      this.selectedProjectIds = [...this.selectedProjectIds, selection.projectId];
    } else {
      this.selectedProjectIds = this.selectedProjectIds.filter(id => id !== selection.projectId);
    }

    this.selectionChanged.emit(this.selectedProjectIds);
  }

  onProjectHover(hover: { projectId: string; isHovered: boolean }): void {
    this.hoveredProjectId = hover.isHovered ? hover.projectId : null;
  }

  onSearch(): void {
    const searchTerm = this.searchForm.get('searchTerm')?.value;
    this.updateFilters({ searchTerm });
  }

  onFiltersChange(): void {
    const filters = this.filtersForm.value;
    this.updateFilters(filters);
  }

  onSortChange(field: keyof Project, direction: 'asc' | 'desc'): void {
    this.store.dispatch(ProjectListActions.updateSort({
      sort: { field, direction }
    }));
  }

  onViewModeChange(mode: 'grid' | 'list'): void {
    this.viewMode = mode;
  }

  onRefresh(): void {
    this.store.dispatch(ProjectListActions.refreshProjects());
  }

  public onLoadMore(): void {
    if (this.isLoadingMore) return;

    this.isLoadingMore = true;
    this.store.dispatch(ProjectListActions.loadMoreProjects());
  }

  onClearFilters(): void {
    this.searchForm.reset();
    this.filtersForm.reset();
    this.store.dispatch(ProjectListActions.clearFilters());
  }

  onSelectAll(): void {
    this.projects$.pipe(takeUntil(this.destroy$)).subscribe(projects => {
      this.selectedProjectIds = projects.map(p => p.id);
      this.selectionChanged.emit(this.selectedProjectIds);
    });
  }

  onDeselectAll(): void {
    this.selectedProjectIds = [];
    this.selectionChanged.emit(this.selectedProjectIds);
  }

  hasActiveFilters(): boolean {
    const searchTerm = this.searchForm.get('searchTerm')?.value;
    const status = this.filtersForm.get('status')?.value;
    const dateRange = this.filtersForm.get('dateRange')?.value;

    return !!(searchTerm ||
             (status && status !== 'all') ||
             (dateRange?.start || dateRange?.end));
  }

  getActiveFiltersText(): string {
    const activeFilters: string[] = [];

    const searchTerm = this.searchForm.get('searchTerm')?.value;
    if (searchTerm) {
      activeFilters.push(`поиск: "${searchTerm}"`);
    }

    const status = this.filtersForm.get('status')?.value;
    if (status && status !== 'all') {
      const statusOption = this.statusOptions.find(opt => opt.value === status);
      activeFilters.push(`статус: ${statusOption?.label || status}`);
    }

    const dateRange = this.filtersForm.get('dateRange')?.value;
    if (dateRange?.start || dateRange?.end) {
      const dates = [];
      if (dateRange.start) {
        dates.push(`с ${new Date(dateRange.start).toLocaleDateString()}`);
      }
      if (dateRange.end) {
        dates.push(`по ${new Date(dateRange.end).toLocaleDateString()}`);
      }
      activeFilters.push(`дата: ${dates.join(' ')}`);
    }

    return activeFilters.join(', ');
  }

    createProjectWithTemplate(template: string): void {
    // Определяем шаблон проекта
    const templateData = {
      web: {
        name: 'Веб-проект',
        description: 'Проект для разработки веб-приложения',
        template: 'web'
      },
      mobile: {
        name: 'Мобильное приложение',
        description: 'Проект для разработки мобильного приложения',
        template: 'mobile'
      },
      design: {
        name: 'UI/UX дизайн',
        description: 'Проект для дизайна пользовательского интерфейса',
        template: 'design'
      }
    };

    const selectedTemplate = templateData[template as keyof typeof templateData];
    if (selectedTemplate) {
      this.projectAction.emit({
        type: 'create',
        projectId: '',
        payload: { template: selectedTemplate }
      });
    }
  }

  onPageChange(event: any): void {
    // Обработка изменения страницы пагинации
    const page = event.pageIndex + 1;
    const pageSize = event.pageSize;

    this.store.dispatch(ProjectListActions.updatePagination({
      pagination: { page, pageSize }
    }));
  }

  // Utility methods
  getSortIcon(field: keyof Project): Observable<any> {
    return this.store.select(ProjectListSelectors.selectProjectListSort).pipe(
      map(sort => {
        if (sort.field === field) {
          return sort.direction === 'asc' ? this.icons.sortAsc : this.icons.sortDesc;
        }
        return this.icons.sortDesc;
      })
    );
  }

  isProjectSelected(projectId: string): boolean {
    return this.selectedProjectIds.includes(projectId);
  }

  isProjectHovered(projectId: string): boolean {
    return this.hoveredProjectId === projectId;
  }

  getSelectedCount(): number {
    return this.selectedProjectIds.length;
  }

  // Private methods
  private initializeComponent(): void {
    // Объединяем конфигурацию
    const config = { ...this.defaultConfig, ...this.config };

    this.enableSelection = config.enableSelection;
    this.enableInfiniteScroll = config.enableInfiniteScroll;
    this.enableFilters = config.enableFilters;
    this.enableSorting = config.enableSorting;
    this.enableSearch = config.enableSearch;
    this.enableActions = config.enableActions;
    this.pageSize = config.pageSize;
    this.debounceTime = config.debounceTime;

    // Подписываемся на изменения выбранных проектов
    this.selectedProjectIds$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(ids => {
      this.selectedProjectIds = ids;
    });
  }

  private setupFormSubscriptions(): void {
    // Подписка на изменения поиска с debounce
    this.searchForm.get('searchTerm')?.valueChanges.pipe(
      takeUntil(this.destroy$),
      debounceTime(this.debounceTime),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.updateFilters({ searchTerm });
    });

    // Подписка на изменения фильтров
    this.filtersForm.valueChanges.pipe(
      takeUntil(this.destroy$),
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(filters => {
      this.updateFilters(filters);
    });
  }

  private loadInitialData(): void {
    const request = {
      filters: {
        searchTerm: '',
        status: 'all' as const,
        ownerId: null,
        dateRange: { start: null, end: null }
      },
      sort: {
        field: 'createdAt' as keyof Project,
        direction: 'desc' as const
      },
      pagination: {
        page: 1,
        pageSize: this.pageSize
      }
    };
    this.store.dispatch(ProjectListActions.loadProjects({ request }));
  }

  private updateFilters(filters: Partial<ProjectFilters>): void {
    this.store.dispatch(ProjectListActions.updateFilters({ filters }));
  }

  private checkInfiniteScroll(): void {
    if (!this.scrollContainer) return;

    const element = this.scrollContainer.nativeElement;
    const scrollTop = element.scrollTop;
    const scrollHeight = element.scrollHeight;
    const clientHeight = element.clientHeight;

    // Загружаем больше проектов когда пользователь приближается к концу списка
    if (scrollTop + clientHeight >= scrollHeight - 100) {
      this.onLoadMore();
    }
  }

  // TrackBy function для оптимизации
  trackByProjectId(index: number, project: Project): string {
    return project.id;
  }
}
