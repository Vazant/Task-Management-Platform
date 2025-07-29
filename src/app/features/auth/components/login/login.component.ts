import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LucideAngularModule, Loader2, Eye, EyeOff } from 'lucide-angular';
import { Store } from '@ngrx/store';

import { AuthService } from '@services';
import { Subscription } from 'rxjs';
import * as AuthActions from '../../store/auth.actions';
import * as AuthSelectors from '../../store/auth.selectors';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    LucideAngularModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);

  // Lucide icons для standalone компонентов
  readonly Loader2 = Loader2;
  readonly Eye = Eye;
  readonly EyeOff = EyeOff;

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
      this.authService.currentUser$.subscribe(user => {
        if (user) {
          this.router.navigate(['/dashboard']);
        }
      })
    );
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const { email, password, rememberMe } = this.loginForm.value;
      // Отправляем только email и password, rememberMe обрабатываем отдельно
      this.store.dispatch(AuthActions.login({ credentials: { email, password } }));

      // Обработка rememberMe (можно сохранить в localStorage)
      if (rememberMe) {
        localStorage.setItem('rememberMe', 'true');
      } else {
        localStorage.removeItem('rememberMe');
      }
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
