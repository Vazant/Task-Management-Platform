import { Routes } from '@angular/router';
import { SettingsComponent } from './components/settings/settings.component';



export const settingsRoutes: Routes = [
  {
    path: '',
    component: SettingsComponent,
    children: [
      {
        path: '',
        redirectTo: 'profile',
        pathMatch: 'full'
      },
      {
        path: 'profile',
        loadComponent: () => import('./components/user-profile-settings/user-profile-settings.component')
          .then(m => m.UserProfileSettingsComponent)
      },
      {
        path: 'appearance',
        loadComponent: () => import('./components/appearance-settings/appearance-settings.component')
          .then(m => m.AppearanceSettingsComponent)
      },
      {
        path: 'notifications',
        loadComponent: () => import('./components/notification-settings/notification-settings.component')
          .then(m => m.NotificationSettingsComponent)
      },
      {
        path: 'security',
        loadComponent: () => import('./components/security-settings/security-settings.component')
          .then(m => m.SecuritySettingsComponent)
      },
      {
        path: 'workspace',
        loadComponent: () => import('./components/workspace-settings/workspace-settings.component')
          .then(m => m.WorkspaceSettingsComponent)
      },
      {
        path: 'integrations',
        loadComponent: () => import('./components/integrations-settings/integrations-settings.component')
          .then(m => m.IntegrationsSettingsComponent)
      },
      {
        path: 'advanced',
        loadComponent: () => import('./components/advanced-settings/advanced-settings.component')
          .then(m => m.AdvancedSettingsComponent)
      }
    ]
  }
];
