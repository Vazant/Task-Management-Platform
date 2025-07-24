import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';


import * as AuthActions from '../../store/auth.actions';
import * as AuthSelectors from '../../store/auth.selectors';
import { RegisterRequest } from '@models';
import { ValidationUtils } from '@utils';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: false
})
export class RegisterComponent implements OnInit, OnDestroy {
  registerForm!: FormGroup;
  loading$!: Observable<boolean>;
  error$!: Observable<string | null>;
  hidePassword = true;
  hideConfirmPassword = true;
  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly fb: FormBuilder,
    private readonly store: Store
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
    this.registerForm = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(20),
        Validators.pattern(/^[a-zA-Z0-9_]+$/)
      ]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        this.passwordStrengthValidator
      ]],
      confirmPassword: ['', [Validators.required]],
      agreeToTerms: [false, [Validators.requiredTrue]]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordStrengthValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.value;
    if (!password) return null;

    const hasLetter = /[a-zA-Z]/.test(password);
    const hasNumber = /\d/.test(password);


    if (!hasLetter || !hasNumber) {
      return { passwordStrength: true };
    }

    return null;
  }

  private passwordMatchValidator(group: AbstractControl): { [key: string]: boolean } | null {
    const password = group.get('password');
    const confirmPassword = group.get('confirmPassword');

    if (!password || !confirmPassword) return null;

    return password.value === confirmPassword.value ? null : { passwordMismatch: true };
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const userData: RegisterRequest = {
        username: this.registerForm.value.username,
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
        confirmPassword: this.registerForm.value.confirmPassword,
      };

      this.store.dispatch(AuthActions.register({ userData }));
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach((key) => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }

  private clearError(): void {
    this.store.dispatch(AuthActions.clearAuthError());
  }

  getErrorMessage(controlName: string): string {
    const control = this.registerForm.get(controlName);

    if (control?.hasError('required')) {
      return 'Это поле обязательно';
    }

    if (controlName === 'username') {
      if (control?.hasError('minlength')) {
        return 'Имя пользователя должно содержать минимум 3 символа';
      }
      if (control?.hasError('maxlength')) {
        return 'Имя пользователя не должно превышать 20 символов';
      }
      if (control?.hasError('pattern')) {
        return 'Имя пользователя может содержать только буквы, цифры и подчеркивания';
      }
    }

    if (controlName === 'email' && control?.hasError('email')) {
      return 'Введите корректный email';
    }

    if (controlName === 'password') {
      if (control?.hasError('minlength')) {
        return 'Пароль должен содержать минимум 8 символов';
      }
      if (control?.hasError('passwordStrength')) {
        return 'Пароль должен содержать буквы и цифры';
      }
    }

    if (controlName === 'confirmPassword' && control?.hasError('required')) {
      return 'Подтвердите пароль';
    }

    if (controlName === 'agreeToTerms' && control?.hasError('required')) {
      return 'Необходимо согласиться с условиями';
    }

    return '';
  }

  getPasswordStrength(): string {
    const password = this.registerForm.get('password')?.value;
    if (!password) return '';

    return ValidationUtils.getPasswordStrength(password);
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  hasPasswordMismatch(): boolean {
    return this.registerForm.hasError('passwordMismatch') &&
    (this.registerForm.get('confirmPassword')?.touched ?? false);
  }
}
