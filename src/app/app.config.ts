import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { ProjectsEffects } from './features/projects/store/projects.effects';
import { AuthEffects } from './features/auth/store/auth.effects';
import { TasksEffects } from './features/tasks/store/tasks.effects';
import { TimeTrackingEffects } from './features/time-tracking/store/time-tracking.effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { projectsReducer } from './features/projects/store/projects.reducer';
import { tasksReducer } from './features/tasks/store/tasks.reducer';
import { timeTrackingReducer } from './features/time-tracking/store/time-tracking.reducer';
import { authReducer } from './features/auth/store/auth.reducer';

// Interceptors
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { loadingInterceptor } from './core/interceptors/loading.interceptor';

// Core Services
import { ApiService } from './core/services/api.service';
import { AuthService } from './core/services/auth.service';
import { NotificationService } from './core/services/notification.service';

// Material Design
import { provideNativeDateAdapter } from '@angular/material/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor, errorInterceptor, loadingInterceptor])
    ),
    provideNativeDateAdapter(),
    provideStore({
      auth: authReducer,
      projects: projectsReducer,
      tasks: tasksReducer,
      timeTracking: timeTrackingReducer,
    }),
    provideEffects([AuthEffects, ProjectsEffects, TasksEffects, TimeTrackingEffects]),
    provideStoreDevtools({
      maxAge: 25,
      logOnly: false,
      autoPause: true,
      trace: false,
      traceLimit: 75,
    }),
    // Core Services
    ApiService,
    AuthService,
    NotificationService
  ],
};
