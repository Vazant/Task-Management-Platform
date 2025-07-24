import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import * as AuthActions from '../../store/auth.actions';
import * as AuthSelectors from '../../store/auth.selectors';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
  standalone: false
})
export class ForgotPasswordComponent implements OnInit, OnDestroy {
  forgotPasswordForm!: FormGroup;
  loading$!: Observable<boolean>;
  error$!: Observable<string | null>;
  success$!: Observable<string | null>;
  emailSent = false;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private store: Store
  ) {
    this.loading$ = this.store.select(AuthSelectors.selectIsLoading);
    this.error$ = this.store.select(AuthSelectors.selectError);
    this.success$ = this.store.select(AuthSelectors.selectError); // Временно используем error для success
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
    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  onSubmit(): void {
    if (this.forgotPasswordForm.valid) {
      const email = this.forgotPasswordForm.value.email;
      this.store.dispatch(AuthActions.forgotPassword({ email }));
      this.emailSent = true;
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.forgotPasswordForm.controls).forEach((key) => {
      const control = this.forgotPasswordForm.get(key);
      control?.markAsTouched();
    });
  }

  private clearError(): void {
    this.store.dispatch(AuthActions.clearAuthError());
  }

  getErrorMessage(controlName: string): string {
    const control = this.forgotPasswordForm.get(controlName);
    
    if (control?.hasError('required')) {
      return 'Это поле обязательно';
    }
    
    if (controlName === 'email' && control?.hasError('email')) {
      return 'Введите корректный email';
    }
    
    return '';
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.forgotPasswordForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  resetForm(): void {
    this.emailSent = false;
    this.forgotPasswordForm.reset();
    this.clearError();
  }
} 