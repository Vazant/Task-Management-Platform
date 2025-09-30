import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
// import { NetworkStatusComponent } from '../../../core/components/network-status/network-status.component';
// import { NetworkStatusService } from '../../../core/services/network-status.service';
// import { OfflineStorageService } from '../../../core/services/offline-storage.service';
// import { SyncService } from '../../../core/services/sync.service';
import { selectAllProjects } from '../../store/projects.selectors';
import { selectAllTasks } from '../../../tasks/store/tasks.selectors';
// Using Material icons instead of Lucide
import { Observable, combineLatest, map } from 'rxjs';

interface ProjectStats {
  totalProjects: number;
  activeProjects: number;
  completedProjects: number;
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  overdueTasks: number;
  completionRate: number;
  averageTasksPerProject: number;
}

@Component({
  selector: 'app-project-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatProgressBarModule,
    MatChipsModule,
    MatTooltipModule,
    MatDividerModule,
    // NetworkStatusComponent
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="project-dashboard">
      <!-- Header -->
      <div class="dashboard-header">
        <div class="header-content">
          <h1>Project Dashboard</h1>
          <p>Overview of your projects and tasks</p>
        </div>
        <div class="header-actions">
          <!-- Network status component removed -->
          <button 
            mat-icon-button
            (click)="refreshData()"
            [disabled]="refreshing"
            matTooltip="Refresh data">
            <mat-icon [class.spinning]="refreshing">refresh</mat-icon>
          </button>
        </div>
      </div>

      <!-- Offline Notice -->
      <mat-card *ngIf="!(isOnline$ | async)" class="offline-notice">
        <mat-card-content>
          <div class="offline-content">
            <mat-icon>wifi_off</mat-icon>
            <div class="offline-text">
              <h3>You're offline</h3>
              <p>Some features may be limited. Data will sync when you're back online.</p>
            </div>
          </div>
        </mat-card-content>
      </mat-card>

      <!-- Stats Grid -->
      <div class="stats-grid">
        <!-- Projects Stats -->
        <mat-card class="stat-card">
          <mat-card-content>
            <div class="stat-header">
              <mat-icon>folder</mat-icon>
              <h3>Projects</h3>
            </div>
            <div class="stat-numbers">
              <div class="main-stat">{{ (stats$ | async)?.totalProjects || 0 }}</div>
              <div class="sub-stats">
                <span class="active">{{ (stats$ | async)?.activeProjects || 0 }} active</span>
                <span class="completed">{{ (stats$ | async)?.completedProjects || 0 }} completed</span>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <!-- Tasks Stats -->
        <mat-card class="stat-card">
          <mat-card-content>
            <div class="stat-header">
              <mat-icon>
                <mat-icon>check_circle</mat-icon>
              </mat-icon>
              <h3>Tasks</h3>
            </div>
            <div class="stat-numbers">
              <div class="main-stat">{{ (stats$ | async)?.totalTasks || 0 }}</div>
              <div class="sub-stats">
                <span class="completed">{{ (stats$ | async)?.completedTasks || 0 }} done</span>
                <span class="pending">{{ (stats$ | async)?.pendingTasks || 0 }} pending</span>
              </div>
            </div>
            <mat-progress-bar 
              [value]="(stats$ | async)?.completionRate || 0"
              color="primary"
              class="completion-bar" />
          </mat-card-content>
        </mat-card>

        <!-- Team Stats -->
        <mat-card class="stat-card">
          <mat-card-content>
            <div class="stat-header">
              <mat-icon>
                <mat-icon>people</mat-icon>
              </mat-icon>
              <h3>Team</h3>
            </div>
            <div class="stat-numbers">
              <div class="main-stat">{{ (stats$ | async)?.averageTasksPerProject || 0 }}</div>
              <div class="sub-stats">
                <span>avg tasks per project</span>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <!-- Performance Stats -->
        <mat-card class="stat-card">
          <mat-card-content>
            <div class="stat-header">
              <mat-icon>
                <mat-icon>trending_up</mat-icon>
              </mat-icon>
              <h3>Performance</h3>
            </div>
            <div class="stat-numbers">
              <div class="main-stat">{{ (stats$ | async)?.completionRate || 0 }}%</div>
              <div class="sub-stats">
                <span>completion rate</span>
              </div>
            </div>
            <mat-progress-bar 
              [value]="(stats$ | async)?.completionRate || 0"
              color="accent"
              class="performance-bar" />
          </mat-card-content>
        </mat-card>
      </div>

      <!-- Recent Activity -->
      <mat-card class="activity-card">
        <mat-card-header>
          <mat-card-title>Recent Activity</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <div class="activity-list" *ngIf="(recentActivity$ | async)?.length; else noActivity">
            <div 
              *ngFor="let activity of recentActivity$ | async; trackBy: trackByActivity"
              class="activity-item">
              <div class="activity-icon">
                <mat-icon>
                  <mat-icon>{{ getActivityIcon(activity.type) }}</mat-icon>
                </mat-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-meta">
                  {{ activity.timestamp | date:'short' }}
                  <span class="activity-project" *ngIf="activity.project">
                    • {{ activity.project }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          <ng-template #noActivity>
            <div class="no-activity">
              <mat-icon>
                <mat-icon>schedule</mat-icon>
              </mat-icon>
              <p>No recent activity</p>
            </div>
          </ng-template>
        </mat-card-content>
      </mat-card>

      <!-- Quick Actions -->
      <mat-card class="actions-card">
        <mat-card-header>
          <mat-card-title>Quick Actions</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <div class="quick-actions">
            <button 
              mat-raised-button 
              color="primary"
              (click)="createProject()"
              [disabled]="!(isOnline$ | async)">
              <mat-icon>
                <mat-icon>add</mat-icon>
              </mat-icon>
              New Project
            </button>
            <button 
              mat-raised-button 
              color="accent"
              (click)="createTask()"
              [disabled]="!(isOnline$ | async)">
              <mat-icon>
                <mat-icon>add</mat-icon>
              </mat-icon>
              New Task
            </button>
            <button 
              mat-stroked-button
              (click)="viewAnalytics()">
              <mat-icon>
                <mat-icon>trending_up</mat-icon>
              </mat-icon>
              View Analytics
            </button>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .project-dashboard {
      padding: 24px;
      max-width: 1200px;
      margin: 0 auto;
    }

    .dashboard-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24px;
      gap: 16px;
    }

    .header-content h1 {
      margin: 0 0 8px 0;
      font-size: 2rem;
      font-weight: 600;
      color: #1976d2;
    }

    .header-content p {
      margin: 0;
      color: #666;
      font-size: 1rem;
    }

    .header-actions {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .offline-notice {
      margin-bottom: 24px;
      background: rgba(244, 67, 54, 0.1);
      border: 1px solid rgba(244, 67, 54, 0.3);
    }

    .offline-content {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .offline-text h3 {
      margin: 0 0 4px 0;
      color: #d32f2f;
    }

    .offline-text p {
      margin: 0;
      color: #666;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 24px;
      margin-bottom: 24px;
    }

    .stat-card {
      transition: transform 0.2s ease, box-shadow 0.2s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
      }
    }

    .stat-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      h3 {
        margin: 0;
        font-size: 1.1rem;
        font-weight: 500;
        color: #333;
      }

      mat-icon {
        color: #1976d2;
      }
    }

    .stat-numbers {
      margin-bottom: 16px;
    }

    .main-stat {
      font-size: 2.5rem;
      font-weight: 700;
      color: #1976d2;
      line-height: 1;
      margin-bottom: 8px;
    }

    .sub-stats {
      display: flex;
      gap: 16px;
      font-size: 0.9rem;
      color: #666;

      .active {
        color: #4caf50;
      }

      .completed {
        color: #2196f3;
      }

      .pending {
        color: #ff9800;
      }
    }

    .completion-bar,
    .performance-bar {
      height: 6px;
      border-radius: 3px;
    }

    .activity-card,
    .actions-card {
      margin-bottom: 24px;
    }

    .activity-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .activity-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 12px;
      border-radius: 8px;
      background: #f8f9fa;
      transition: background-color 0.2s ease;

      &:hover {
        background: #e9ecef;
      }
    }

    .activity-icon {
      flex-shrink: 0;
      
      mat-icon {
        color: #1976d2;
      }
    }

    .activity-content {
      flex: 1;
    }

    .activity-title {
      font-weight: 500;
      margin-bottom: 4px;
      color: #333;
    }

    .activity-meta {
      font-size: 0.85rem;
      color: #666;
    }

    .activity-project {
      color: #1976d2;
      font-weight: 500;
    }

    .no-activity {
      text-align: center;
      padding: 32px;
      color: #666;

      mat-icon {
        font-size: 3rem;
        width: 3rem;
        height: 3rem;
        margin-bottom: 16px;
        opacity: 0.5;
      }
    }

    .quick-actions {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
    }

    .spinning {
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }

    @media (max-width: 768px) {
      .project-dashboard {
        padding: 16px;
      }

      .dashboard-header {
        flex-direction: column;
        align-items: stretch;
      }

      .header-actions {
        justify-content: space-between;
      }

      .stats-grid {
        grid-template-columns: 1fr;
        gap: 16px;
      }

      .quick-actions {
        flex-direction: column;
      }

      .quick-actions button {
        width: 100%;
      }
    }
  `]
})
export class ProjectDashboardComponent implements OnInit {
  private readonly store = inject(Store);
  // private readonly networkStatus = inject(NetworkStatusService);
  // private readonly offlineStorage = inject(OfflineStorageService);
  // private readonly syncService = inject(SyncService);

  // Icons - using Material Icons
  readonly folderIcon = 'folder_open';
  readonly usersIcon = 'people';
  readonly trendingUpIcon = 'trending_up';
  readonly clockIcon = 'schedule';
  readonly checkCircleIcon = 'check_circle';
  readonly alertCircleIcon = 'error';
  readonly plusIcon = 'add';
  readonly refreshIcon = 'refresh';
  readonly wifiIcon = 'wifi';
  readonly wifiOffIcon = 'wifi_off';

  // Observables
  readonly isOnline$ = of(true); // this.networkStatus.isOnline;
  readonly projects$ = this.store.select(selectAllProjects);
  readonly tasks$ = this.store.select(selectAllTasks);

  // Component state
  refreshing = false;

  // Computed observables
  readonly stats$: Observable<ProjectStats> = combineLatest([
    this.projects$,
    this.tasks$
  ]).pipe(
    map(([projects, tasks]) => {
      const totalProjects = projects.length;
      const activeProjects = projects.filter(p => p.status === 'active').length;
      const completedProjects = projects.filter(p => p.status === 'completed').length;
      
      const totalTasks = tasks.length;
      const completedTasks = tasks.filter(t => t.status === 'done').length;
      const pendingTasks = tasks.filter(t => t.status === 'in-progress').length;
              const overdueTasks = tasks.filter(t => 
          t.dueDate && new Date(t.dueDate) < new Date() && t.status !== 'done'
        ).length;

      const completionRate = totalTasks > 0 ? Math.round((completedTasks / totalTasks) * 100) : 0;
      const averageTasksPerProject = totalProjects > 0 ? Math.round(totalTasks / totalProjects) : 0;

      return {
        totalProjects,
        activeProjects,
        completedProjects,
        totalTasks,
        completedTasks,
        pendingTasks,
        overdueTasks,
        completionRate,
        averageTasksPerProject
      };
    })
  );

  readonly recentActivity$: Observable<any[]> = combineLatest([
    this.projects$,
    this.tasks$
  ]).pipe(
    map(([projects, tasks]) => {
      const activities = [
        ...projects.map(p => ({
          type: 'project',
          title: `Project "${p.name}" created`,
          timestamp: p.createdAt,
          project: p.name
        })),
        ...tasks.map(t => ({
          type: 'task',
          title: `Task "${t.title}" ${t.status === 'done' ? 'completed' : 'created'}`,
          timestamp: t.status === 'done' ? t.updatedAt : t.createdAt,
          project: projects.find(p => p.id === t.projectId)?.name
        }))
      ];

      return activities
        .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
        .slice(0, 5);
    })
  );

  ngOnInit(): void {
    // Initialize offline storage
    // this.offlineStorage.initDatabase().catch((error: unknown) => console.error('Failed to init database:', error));
  }

  async refreshData(): Promise<void> {
    this.refreshing = true;
    
    try {
      // Trigger data refresh
      // This would typically dispatch actions to reload data
      await new Promise(resolve => setTimeout(resolve, 1000));
    } finally {
      this.refreshing = false;
    }
  }

  getActivityIcon(type: string): any {
    switch (type) {
      case 'project':
        return this.folderIcon;
      case 'task':
        return this.checkCircleIcon;
      default:
        return this.clockIcon;
    }
  }

  trackByActivity(index: number, activity: any): string {
    return `${activity.type}-${activity.timestamp}`;
  }

  createProject(): void {
    // Navigate to project creation
    console.log('Create project');
  }

  createTask(): void {
    // Navigate to task creation
    console.log('Create task');
  }

  viewAnalytics(): void {
    // Navigate to analytics
    console.log('View analytics');
  }
}
