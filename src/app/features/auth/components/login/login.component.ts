import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import * as AuthActions from '../../store/auth.actions';
import * as AuthSelectors from '../../store/auth.selectors';
import { LoginRequest } from '../../../../core/models';
import { ValidationUtils } from '../../../../core/utils/validation.utils';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: false
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm!: FormGroup;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private store: Store
  ) {
    this.loading$ = this.store.select(AuthSelectors.selectIsLoading);
    this.error$ = this.store.select(AuthSelectors.selectError);
  }

  ngOnInit(): void {
    this.initForm();
    this.clearError();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      rememberMe: [false]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const credentials: LoginRequest = {
        email: this.loginForm.value.email,
        password: this.loginForm.value.password
      };

      this.store.dispatch(AuthActions.login({ credentials }));
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      control?.markAsTouched();
    });
  }

  private clearError(): void {
    this.store.dispatch(AuthActions.clearAuthError());
  }

  getErrorMessage(controlName: string): string {
    const control = this.loginForm.get(controlName);
    if (control?.hasError('required')) {
      return 'Это поле обязательно';
    }
    if (control?.hasError('email')) {
      return 'Введите корректный email';
    }
    if (control?.hasError('minlength')) {
      return 'Минимум 8 символов';
    }
    return '';
  }
} 