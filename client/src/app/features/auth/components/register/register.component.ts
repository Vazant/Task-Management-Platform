import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { LucideAngularModule, User, Mail, Eye, EyeOff, Loader2, Lock, Github } from 'lucide-angular';

import { ValidationUtils } from '../../../../core/utils/validation.utils';
import { RegisterRequest } from '../../../../core/models/api-response.model';
import * as AuthActions from '../../store/auth.actions';
import * as AuthSelectors from '../../store/auth.selectors';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, LucideAngularModule]
})
export class RegisterComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);

  readonly User = User;
  readonly Mail = Mail;
  readonly Eye = Eye;
  readonly EyeOff = EyeOff;
  readonly Loader2 = Loader2;
  readonly Lock = Lock;
  readonly Github = Github;

  registerForm!: FormGroup;
  loading$!: Observable<boolean>;
  error$!: Observable<string | null>;
  hidePassword = true;
  hideConfirmPassword = true;
  private readonly destroy$ = new Subject<void>();

  constructor() {
    this.loading$ = this.store.select(AuthSelectors.selectIsLoading);
    this.error$ = this.store.select(AuthSelectors.selectError);
  }

  ngOnInit(): void {
    this.initForm();
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
      email: ['', [
        Validators.required,
        Validators.email
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        this.passwordStrengthValidator.bind(this)
      ]],
      confirmPassword: ['', [
        Validators.required
      ]],
      agreeToTerms: [false, [
        Validators.requiredTrue
      ]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  private passwordStrengthValidator(control: AbstractControl): { [key: string]: boolean } | null {
    if (!control.value) return null;

    const strength = ValidationUtils.getPasswordStrength(control.value);
    if (strength === 'weak') {
      return { weakPassword: true };
    }
    return null;
  }

  private passwordMatchValidator(group: AbstractControl): { [key: string]: boolean } | null {
    const password = group.get('password');
    const confirmPassword = group.get('confirmPassword');

    if (!password || !confirmPassword) return null;

    if (password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const userData: RegisterRequest = {
        username: this.registerForm.value.username,
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
        confirmPassword: this.registerForm.value.confirmPassword
      };

      this.store.dispatch(AuthActions.register({ userData }));
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach(key => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }

  getErrorMessage(controlName: string): string {
    const control = this.registerForm.get(controlName);
    if (!control?.errors) return '';

    if (control.errors['required']) {
      return 'Это поле обязательно для заполнения';
    }
    if (control.errors['email']) {
      return 'Введите корректный email';
    }
    if (control.errors['minlength']) {
      const requiredLength = control.errors['minlength'].requiredLength;
      if (controlName === 'username') {
        return `Имя пользователя должно содержать минимум ${requiredLength} символа`;
      }
      return `Минимальная длина ${requiredLength} символов`;
    }
    if (control.errors['maxlength']) {
      const requiredLength = control.errors['maxlength'].requiredLength;
      return `Максимальная длина ${requiredLength} символов`;
    }
    if (control.errors['pattern']) {
      if (controlName === 'username') {
        return 'Имя пользователя может содержать только буквы, цифры и знак подчеркивания';
      }
      return 'Неверный формат';
    }
    if (control.errors['weakPassword']) {
      return 'Пароль слишком слабый. Используйте буквы, цифры и специальные символы';
    }
    if (control.errors['requiredTrue']) {
      return 'Необходимо согласиться с условиями использования';
    }

    return 'Неверное значение';
  }

  getPasswordStrength(): string {
    const password = this.registerForm.get('password')?.value;
    if (!password) return '';
    
    const strength = ValidationUtils.getPasswordStrength(password);
    return strength;
  }

  getPasswordStrengthPercentage(): number {
    const password = this.registerForm.get('password')?.value;
    if (!password) return 0;
    
    const strength = ValidationUtils.getPasswordStrength(password);
    switch (strength) {
      case 'weak': return 33;
      case 'medium': return 66;
      case 'strong': return 100;
      default: return 0;
    }
  }

  getPasswordStrengthText(): string {
    const password = this.registerForm.get('password')?.value;
    if (!password) return '';
    
    const strength = ValidationUtils.getPasswordStrength(password);
    switch (strength) {
      case 'weak': return 'Слабый пароль';
      case 'medium': return 'Средний пароль';
      case 'strong': return 'Сильный пароль';
      default: return '';
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

    hasPasswordMismatch(): boolean {
    return !!(this.registerForm.hasError('passwordMismatch') &&
           this.registerForm.get('confirmPassword')?.touched);
  }
}
