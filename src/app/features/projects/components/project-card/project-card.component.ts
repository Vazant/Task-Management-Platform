import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
  ViewEncapsulation,
  OnDestroy,
  OnInit,
  ElementRef,
  HostListener,
  HostBinding
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import {
  LucideAngularModule,
  MoreVertical,
  Edit,
  Trash2,
  Archive,
  Copy,
  Share2,
  Calendar,
  Users,
  Clock,
  CheckCircle,
  AlertCircle,
  Pause,
  Archive as ArchiveIcon
} from 'lucide-angular';
import { Subject, takeUntil } from 'rxjs';

import { Project } from '@models';
import { ProjectCard, ProjectAction, ProjectStatus } from '../../models';

/**
 * Презентационный компонент карточки проекта
 * Отвечает только за отображение данных и эмиссию событий
 */
@Component({
  selector: 'app-project-card',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatChipsModule,
    MatTooltipModule,
    MatCheckboxModule,
    MatDialogModule,
    MatDividerModule,
    LucideAngularModule
  ],
  templateUrl: './project-card.component.html',
  styleUrls: ['./project-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  host: {
    class: 'project-card-container'
  }
})
export class ProjectCardComponent implements OnInit, OnDestroy {

  // Input properties
  @Input() project!: Project;
  @Input() isSelected = false;
  @Input() isHovered = false;
  @Input() showActions = true;
  @Input() showSelection = false;
  @Input() showStatus = true;
  @Input() showMembers = true;
  @Input() showDates = true;
  @Input() loading = false;
  @Input() disabled = false;

  // Output events
  @Output() select = new EventEmitter<{ projectId: string; selected: boolean }>();
  @Output() action = new EventEmitter<ProjectAction>();
  @Output() click = new EventEmitter<Project>();
  @Output() hover = new EventEmitter<{ projectId: string; isHovered: boolean }>();

  // Host bindings для анимаций
  @HostBinding('class.selected') get selectedClass() { return this.isSelected; }
  @HostBinding('class.hovered') get hoveredClass() { return this.isHovered; }
  @HostBinding('class.loading') get loadingClass() { return this.loading; }
  @HostBinding('class.disabled') get disabledClass() { return this.disabled; }

  // Public properties
  public readonly icons = {
    more: MoreVertical,
    edit: Edit,
    delete: Trash2,
    archive: Archive,
    copy: Copy,
    share: Share2,
    calendar: Calendar,
    users: Users,
    clock: Clock,
    checkCircle: CheckCircle,
    alertCircle: AlertCircle,
    pause: Pause,
    archiveIcon: ArchiveIcon
  };

  public readonly statusConfig = {
    active: {
      label: 'Активный',
      color: 'primary',
      icon: this.icons.checkCircle,
      class: 'status-active'
    },
    completed: {
      label: 'Завершен',
      color: 'accent',
      icon: this.icons.checkCircle,
      class: 'status-completed'
    },
    'on-hold': {
      label: 'На паузе',
      color: 'warn',
      icon: this.icons.pause,
      class: 'status-on-hold'
    },
    archived: {
      label: 'Архив',
      color: 'default',
      icon: this.icons.archiveIcon,
      class: 'status-archived'
    }
  };

  // Private properties
  private readonly destroy$ = new Subject<void>();

  constructor(private elementRef: ElementRef) {}

  ngOnInit(): void {
    this.validateInputs();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Event handlers
  @HostListener('click', ['$event'])
  onCardClick(event: MouseEvent): void {
    if (this.disabled || this.loading) return;

    // Не эмитим событие если клик был по кнопке или чекбоксу
    const target = event.target as HTMLElement;
    if (target.closest('button, .mat-checkbox, .mat-menu-trigger')) {
      return;
    }

    this.click.emit(this.project);
  }

  @HostListener('mouseenter')
  onMouseEnter(): void {
    if (this.disabled) return;
    this.hover.emit({ projectId: this.project.id, isHovered: true });
  }

  @HostListener('mouseleave')
  onMouseLeave(): void {
    if (this.disabled) return;
    this.hover.emit({ projectId: this.project.id, isHovered: false });
  }

  // Public methods
  onSelectionChange(selected: boolean): void {
    this.select.emit({ projectId: this.project.id, selected });
  }

  onAction(actionType: ProjectAction['type']): void {
    const action: ProjectAction = {
      type: actionType,
      projectId: this.project.id
    };
    this.action.emit(action);
  }

  getStatusConfig(status: ProjectStatus) {
    return this.statusConfig[status] || this.statusConfig.active;
  }

  getStatusIconName(status: ProjectStatus): string {
    switch (status) {
      case 'active':
      case 'completed':
        return 'check-circle';
      case 'on-hold':
        return 'pause';
      case 'archived':
        return 'archive';
      default:
        return 'check-circle';
    }
  }

  getMemberCount(): number {
    return this.project.members?.length || 0;
  }

  getFormattedDate(date: Date): string {
    return new Date(date).toLocaleDateString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  getRelativeDate(date: Date): string {
    const now = new Date();
    const projectDate = new Date(date);
    const diffTime = Math.abs(now.getTime() - projectDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return 'Сегодня';
    if (diffDays === 1) return 'Вчера';
    if (diffDays < 7) return `${diffDays} дней назад`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)} недель назад`;
    if (diffDays < 365) return `${Math.floor(diffDays / 30)} месяцев назад`;
    return `${Math.floor(diffDays / 365)} лет назад`;
  }

  // Private methods
  private validateInputs(): void {
    if (!this.project) {
      throw new Error('ProjectCardComponent: project input is required');
    }
  }

  // TrackBy function для оптимизации
  trackByProjectId(index: number, project: Project): string {
    return project.id;
  }
}
