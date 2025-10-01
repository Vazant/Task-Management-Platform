import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { LucideAngularModule, Loader2, Eye, EyeOff, Mail, Lock } from 'lucide-angular';

import { AuthService } from '../../../../core/services/auth.service';
import { LoginRequest } from '../../../../core/models/api-response.model';
import * as AuthActions from '../../store/auth.actions';
import * as AuthSelectors from '../../store/auth.selectors';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, LucideAngularModule]
})
export class LoginComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);

  readonly Loader2 = Loader2;
  readonly Eye = Eye;
  readonly EyeOff = EyeOff;
  readonly Mail = Mail;
  readonly Lock = Lock;

  loginForm!: FormGroup;
  loading$ = this.store.select(AuthSelectors.selectIsLoading);
  error$ = this.store.select(AuthSelectors.selectError);
  hidePassword = true;
  private readonly subscription = new Subscription();

  ngOnInit(): void {
    this.initForm();
    this.subscribeToAuthState();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private initForm(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });
  }

  private subscribeToAuthState(): void {
    this.subscription.add(
      this.store.select(AuthSelectors.selectIsAuthenticated).subscribe(isAuthenticated => {
        if (isAuthenticated) {
          this.router.navigate(['/dashboard']);
        }
      })
    );
  }

  onSubmit(): void {
    console.log('Login form submitted');
    console.log('Form valid:', this.loginForm.valid);
    console.log('Form value:', this.loginForm.value);
    console.log('Form errors:', this.loginForm.errors);
    
    if (this.loginForm.valid) {
      const credentials: LoginRequest = {
        email: this.loginForm.value.email,
        password: this.loginForm.value.password
      };

      console.log('Dispatching login action with credentials:', credentials);
      this.store.dispatch(AuthActions.login({ credentials }));
    } else {
      console.log('Login form is invalid');
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.loginForm.get(fieldName);
    if (!field?.errors) return '';

    if (field.errors['required']) {
      return 'Это поле обязательно для заполнения';
    }
    if (field.errors['email']) {
      return 'Введите корректный email';
    }
    if (field.errors['minlength']) {
      return `Минимальная длина ${field.errors['minlength'].requiredLength} символов`;
    }

    return 'Неверное значение';
  }
}
