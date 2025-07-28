import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule),
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [authGuard],
  },
  {
    path: 'projects',
    loadChildren: () => import('./features/projects/projects.module').then(m => m.ProjectsModule),
    canActivate: [authGuard],
  },
  {
    path: 'tasks',
    loadChildren: () => import('./features/tasks/tasks.module').then(m => m.TasksModule),
    canActivate: [authGuard],
  },
  {
    path: 'time-tracking',
    loadChildren: () => import('./features/time-tracking/time-tracking.module').then(m => m.TimeTrackingModule),
    canActivate: [authGuard],
  },
  {
    path: 'analytics',
    loadChildren: () => import('./features/analytics/analytics.module').then(m => m.AnalyticsModule),
    canActivate: [authGuard],
  },
  {
    path: 'settings',
    loadChildren: () => import('./features/settings/settings.module').then(m => m.SettingsModule),
    canActivate: [authGuard],
  },
  {
    path: '',
    loadComponent: () => import('./home/home.component').then(m => m.HomeComponent),
  },
  {
    path: '**',
    redirectTo: '/',
  },
];
