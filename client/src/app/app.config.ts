import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideServiceWorker } from '@angular/service-worker';
import { ProjectsEffects } from './features/projects/store/projects.effects';
import { AuthEffects } from './features/auth/store/auth.effects';
import { TasksEffects } from './features/tasks/store/tasks.effects';
import { TimeTrackingEffects } from './features/time-tracking/store/time-tracking.effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';


import { routes } from './app.routes';
import { projectsReducer } from './features/projects/store/projects.reducer';
import { projectListReducer } from './features/projects/store/project-list.reducer';
import { ProjectListEffects } from './features/projects/store/project-list.effects';
import { tasksReducer } from './features/tasks/store/tasks.reducer';
import { timeTrackingReducer } from './features/time-tracking/store/time-tracking.reducer';
import { authReducer } from './features/auth/store/auth.reducer';

// Interceptors
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { loadingInterceptor } from './core/interceptors/loading.interceptor';
import { errorHandlerInterceptor } from './core/interceptors/error-handler.interceptor';
import { SecurityInterceptor } from './core/interceptors/security.interceptor';

// Material Design
import { provideNativeDateAdapter } from '@angular/material/core';

// Core Module
import { CoreModule } from './core/core.module';

// Lucide Icons - импортируем все необходимые иконки
import {
  LucideAngularModule,
  User,
  Settings,
  KeyRound,
  LogOut,
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  Mail,
  Loader2,
  CheckCircle,
  Camera,
  Save,
  RefreshCw,
  AlertCircle,
  Menu,
  FolderOpen,
  Plus,
  Users,
  TrendingUp,
  ChartColumn,
  FileText,
  Target,
  Play,
  PartyPopper,
  ClipboardList,
  Eye,
  EyeOff,
  Palette,
  Bell,
  Shield,
  Grid3X3,
  Link,
  Calendar,
  Move,
  Clock,
  MoreVertical,
  Edit,
  Trash2,
  Archive,
  Copy,
  Share2,
  Pause,
  Search,
  Filter,
  SortAsc,
  SortDesc,
  Grid,
  List,
  MoreHorizontal,
  Globe,
  Smartphone
} from 'lucide-angular';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor, loadingInterceptor, errorHandlerInterceptor, SecurityInterceptor])
    ),
    provideNativeDateAdapter(),
    provideAnimations(),
    provideStore({
      auth: authReducer,
      projects: projectsReducer,
      projectList: projectListReducer,
      tasks: tasksReducer,
      timeTracking: timeTrackingReducer,
    }),
    provideEffects([AuthEffects, ProjectsEffects, ProjectListEffects, TasksEffects, TimeTrackingEffects]),
    provideStoreDevtools({
      maxAge: 25,
      logOnly: false,
      autoPause: true,
      trace: false,
      traceLimit: 75,
    }),
    // Core Module
    importProvidersFrom(CoreModule),
    // Service Worker for PWA
    provideServiceWorker('ngsw-worker.js', {
      enabled: true,
      registrationStrategy: 'registerWhenStable:30000'
    }),
    // Lucide Icons - выбираем конкретные иконки для оптимизации
    importProvidersFrom(LucideAngularModule.pick({
      User,
      Settings,
      KeyRound,
      LogOut,
      ChevronDown,
      ChevronLeft,
      ChevronRight,
      Mail,
      Loader2,
      CheckCircle,
      Camera,
      Save,
      RefreshCw,
      AlertCircle,
      Menu,
      FolderOpen,
      Plus,
      Users,
      TrendingUp,
      ChartColumn,
      FileText,
      Target,
      Play,
      PartyPopper,
      ClipboardList,
      Eye,
      EyeOff,
      Palette,
      Bell,
      Shield,
      Grid3X3,
      Link,
      Calendar,
      Move,
      Clock,
      MoreVertical,
      Edit,
      Trash2,
      Archive,
      Copy,
      Share2,
      Pause,
      Search,
      Filter,
      SortAsc,
      SortDesc,
      Grid,
      List,
      MoreHorizontal,
      Globe,
      Smartphone
    })),
  ],
};
