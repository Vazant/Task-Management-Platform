import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';

// Components
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';

// Shared Module
import { SharedModule } from '../../shared/shared.module';

// Routes
import { authRoutes } from './auth.routes';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(authRoutes),
    SharedModule,
    LucideAngularModule,
    LoginComponent,
    RegisterComponent,
    ForgotPasswordComponent
  ]
})
export class AuthModule { }
