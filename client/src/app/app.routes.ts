import { Routes } from '@angular/router';
import { AuthGuard, GuestGuard } from './core/guards';
import { PageNotFoundComponent } from './shared/components/page-not-found/page-not-found.component';



export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule),
    canActivate: [GuestGuard],
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'projects',
    loadChildren: () => import('./features/projects/projects.module').then(m => m.ProjectsModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'tasks',
    loadChildren: () => import('./features/tasks/tasks.module').then(m => m.TasksModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'time-tracking',
    loadChildren: () => import('./features/time-tracking/time-tracking.module').then(m => m.TimeTrackingModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'analytics',
    loadChildren: () => import('./features/analytics/analytics.module').then(m => m.AnalyticsModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'settings',
    loadChildren: () => import('./features/settings/settings.module').then(m => m.SettingsModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'profile',
    loadChildren: () => import('./features/profile/profile.routes').then(m => m.profileRoutes),
    canActivate: [AuthGuard],
  },
  {
    path: '',
    loadComponent: () => import('./home/home.component').then(m => m.HomeComponent),
  },
  {
    path: '**',
    component: PageNotFoundComponent,
  },
];
