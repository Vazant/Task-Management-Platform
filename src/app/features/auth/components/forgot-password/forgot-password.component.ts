import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';

import { MaterialModule } from '@shared/material.module';
import * as AuthActions from '../../store/auth.actions';
import * as AuthSelectors from '../../store/auth.selectors';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MaterialModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ForgotPasswordComponent implements OnInit, OnDestroy {
  forgotPasswordForm!: FormGroup;
  loading$!: Observable<boolean>;
  error$!: Observable<string | null>;
  success$!: Observable<string | null>;
  emailSent = false;
  private readonly destroy$ = new Subject<void>();

  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);

  constructor() {
    this.loading$ = this.store.select(AuthSelectors.selectIsLoading);
    this.error$ = this.store.select(AuthSelectors.selectError);
    this.success$ = this.store.select(AuthSelectors.selectSuccess);
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
