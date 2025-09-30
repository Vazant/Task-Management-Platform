import { Routes } from '@angular/router';
import { provideState } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { authReducer } from './store/auth.reducer';
import { AuthEffects } from './store/auth.effects';

// Общие провайдеры для auth routes
const authProviders = [
  provideState('auth', authReducer),
  provideEffects([AuthEffects])
];

export const authRoutes: Routes = [
  { 
    path: 'login', 
    component: LoginComponent,
    providers: authProviders
  },
  { 
    path: 'register', 
    component: RegisterComponent,
    providers: authProviders
  },
  { 
    path: 'forgot-password', 
    component: ForgotPasswordComponent,
    providers: authProviders
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
