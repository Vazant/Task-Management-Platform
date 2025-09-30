import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { Project } from '../../../../core/models/project.model';

@Component({
  selector: 'app-project-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule,
    MatChipsModule,
    MatProgressBarModule,
    MatDividerModule
  ],
  templateUrl: './project-card.component.html',
  styleUrls: ['./project-card.component.scss']
})
export class ProjectCardComponent {
  @Input() project!: Project;
  @Output() projectSelected = new EventEmitter<Project>();
  @Output() projectEdited = new EventEmitter<Project>();
  @Output() projectDeleted = new EventEmitter<Project>();
  @Output() projectArchived = new EventEmitter<Project>();

  onProjectClick(): void {
    this.projectSelected.emit(this.project);
  }

  onEditProject(event: Event): void {
    event.stopPropagation();
    this.projectEdited.emit(this.project);
  }

  onDeleteProject(event: Event): void {
    event.stopPropagation();
    this.projectDeleted.emit(this.project);
  }

  onArchiveProject(event: Event): void {
    event.stopPropagation();
    this.projectArchived.emit(this.project);
  }

  getProgressPercentage(): number {
    if (!this.project.tasksCount || this.project.tasksCount === 0) {
      return 0;
    }
    return Math.round((this.project.completedTasksCount || 0) / this.project.tasksCount * 100);
  }

  getStatusColor(): string {
    switch (this.project.status) {
      case 'active':
        return '#4caf50';
      case 'completed':
        return '#2196f3';
      case 'archived':
        return '#9e9e9e';
      case 'on-hold':
        return '#ff9800';
      default:
        return '#9e9e9e';
    }
  }

  getPriorityColor(): string {
    switch (this.project.priority) {
      case 'low':
        return '#4caf50';
      case 'medium':
        return '#ff9800';
      case 'high':
        return '#f44336';
      case 'urgent':
        return '#9c27b0';
      default:
        return '#9e9e9e';
    }
  }
}
