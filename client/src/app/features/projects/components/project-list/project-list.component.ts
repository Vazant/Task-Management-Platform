import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormsModule } from '@angular/forms';
import { Project } from '../../../../core/models/project.model';
import { ProjectAction } from '../../models';
import { 
  loadProjects, 
  deleteProject, 
  setSelectedProject,
  filterProjects,
  searchProjects 
} from '../../store';
import {
  selectFilteredProjects, 
  selectProjectsLoading, 
  selectProjectsError,
  selectProjectsStatistics 
} from '../../store/projects.selectors';
import { ProjectCreateDialogComponent } from '../project-create-dialog/project-create-dialog.component';
import { ProjectEditDialogComponent } from '../project-edit-dialog/project-edit-dialog.component';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule,
    MatChipsModule,
    MatProgressBarModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatOptionModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectListComponent implements OnInit, OnDestroy {
  @Output() projectSelected = new EventEmitter<Project>();
  @Output() projectAction = new EventEmitter<ProjectAction>();

  projects$: Observable<Project[]>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;
  statistics$: Observable<any>;
  
  searchQuery = '';
  selectedFilter = '';
  
  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly store: Store,
    private readonly dialog: MatDialog,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.projects$ = this.store.select(selectFilteredProjects);
    this.loading$ = this.store.select(selectProjectsLoading);
    this.error$ = this.store.select(selectProjectsError);
    this.statistics$ = this.store.select(selectProjectsStatistics);
  }

  ngOnInit(): void {
    this.loadProjects();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadProjects(): void {
    this.store.dispatch(loadProjects());
  }

  onSearch(query: string): void {
    this.searchQuery = query;
    this.store.dispatch(searchProjects({ query }));
  }

  onFilter(filter: string): void {
    this.selectedFilter = filter;
    this.store.dispatch(filterProjects({ filter }));
  }

  onProjectSelect(project: Project): void {
    this.store.dispatch(setSelectedProject({ project }));
    this.projectSelected.emit(project);
  }

  onCreateProject(): void {
    const dialogRef = this.dialog.open(ProjectCreateDialogComponent, {
      width: '600px',
      disableClose: true
    });

    dialogRef.afterClosed()
      .pipe(takeUntil(this.destroy$))
      .subscribe(result => {
        if (result) {
          this.loadProjects();
        }
      });
  }

  onEditProject(project: Project): void {
    const dialogRef = this.dialog.open(ProjectEditDialogComponent, {
      width: '600px',
      data: { project },
      disableClose: true
    });

    dialogRef.afterClosed()
      .pipe(takeUntil(this.destroy$))
      .subscribe(result => {
        if (result) {
          this.loadProjects();
          this.projectAction.emit({ type: 'edit', projectId: project.id });
        }
      });
  }

  onDeleteProject(project: Project): void {
    if (confirm(`Вы уверены, что хотите удалить проект "${project.name}"?`)) {
      this.store.dispatch(deleteProject({ id: project.id }));
      this.projectAction.emit({ type: 'delete', projectId: project.id });
    }
  }

  onArchiveProject(project: Project): void {
    // TODO: Implement archive functionality
    console.log('Archive project:', project);
  }

  onDuplicateProject(project: Project): void {
    // TODO: Implement duplicate functionality
    console.log('Duplicate project:', project);
  }

  getProjectProgress(project: Project): number {
    if (!project.tasksCount || project.tasksCount === 0) {
      return 0;
    }
    return Math.round((project.completedTasksCount || 0) / project.tasksCount * 100);
  }

  getProjectStatusClass(status: string): string {
    return `project-status--${status.toLowerCase()}`;
  }

  getProjectPriorityClass(priority: string): string {
    return `project-priority--${priority.toLowerCase()}`;
  }

  trackByProjectId(index: number, project: Project): string {
    return project.id;
  }
}