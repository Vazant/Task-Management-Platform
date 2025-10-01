import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { LucideAngularModule, User, Mail, Eye, EyeOff, Loader2, Lock } from 'lucide-angular';

import { RegisterRequest } from '../../../../core/models/api-response.model';
import * as AuthActions from '../../store/auth.actions';
import * as AuthSelectors from '../../store/auth.selectors';
import { AuthTextService } from '../../services/auth-text.service';
import { 
  usernameValidator, 
  emailValidator, 
  passwordValidator, 
  passwordConfirmationValidator, 
  termsAgreementValidator,
  getValidationErrorMessage 
} from '../../validators/auth.validators';
import { 
  calculatePasswordStrength, 
  getPasswordStrengthClass, 
  getPasswordStrengthPercentage, 
  getPasswordStrengthText 
} from '../../utils/password.utils';

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
  private readonly authTextService = inject(AuthTextService);

  readonly User = User;
  readonly Mail = Mail;
  readonly Eye = Eye;
  readonly EyeOff = EyeOff;
  readonly Loader2 = Loader2;
  readonly Lock = Lock;

  registerForm!: FormGroup;
  loading$!: Observable<boolean>;
  error$!: Observable<string | null>;
  hidePassword = true;
  hideConfirmPassword = true;
  private readonly destroy$ = new Subject<void>();

  // Texts
  texts$ = this.authTextService.getTexts();

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
        usernameValidator
      ]],
      email: ['', [
        Validators.required,
        emailValidator
      ]],
      password: ['', [
        Validators.required,
        passwordValidator
      ]],
      confirmPassword: ['', [
        Validators.required,
        passwordConfirmationValidator('password')
      ]],
      agreeToTerms: [false, [
        termsAgreementValidator
      ]]
    });
  }

  // Password strength methods
  getPasswordStrength(): string {
    const password = this.registerForm.get('password')?.value;
    if (!password) return '';
    
    const strength = calculatePasswordStrength(password);
    return getPasswordStrengthClass(strength);
  }

  getPasswordStrengthPercentage(): number {
    const password = this.registerForm.get('password')?.value;
    if (!password) return 0;
    
    const strength = calculatePasswordStrength(password);
    return getPasswordStrengthPercentage(strength);
  }

  getPasswordStrengthText(): string {
    const password = this.registerForm.get('password')?.value;
    if (!password) return '';
    
    const strength = calculatePasswordStrength(password);
    return getPasswordStrengthText(strength);
  }

  onSubmit(): void {
    console.log('Register form submitted');
    console.log('Form valid:', this.registerForm.valid);
    console.log('Form value:', this.registerForm.value);
    console.log('Form errors:', this.registerForm.errors);
    
    if (this.registerForm.valid) {
      const userData: RegisterRequest = {
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
        username: this.registerForm.value.username,
        confirmPassword: this.registerForm.value.confirmPassword
      };

      console.log('Dispatching register action with data:', userData);
      this.store.dispatch(AuthActions.register({ userData }));
    } else {
      console.log('Form is invalid, marking fields as touched');
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

    return getValidationErrorMessage(control.errors);
  }


  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  hasPasswordMismatch(): boolean {
    return !!(this.registerForm.get('confirmPassword')?.hasError('passwordMismatch') &&
           this.registerForm.get('confirmPassword')?.touched);
  }
}
