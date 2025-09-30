import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { SecurityValidators } from '@core/validators/security-validators';
import { SecurityService } from '@core/services/security.service';
import { AuthService } from '../../../../core/services/auth.service';
import { NotificationService } from '../../../../core/services/notification.service';
import * as AuthActions from '../../store/auth.actions';
import * as AuthSelectors from '../../store/auth.selectors';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-secure-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatCheckboxModule,
    MatTooltipModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="secure-register-container">
      <mat-card class="register-card">
        <mat-card-header>
          <mat-card-title>Secure Registration</mat-card-title>
          <mat-card-subtitle>Create your account with enhanced security</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="register-form">
            <!-- Username Field -->
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Username</mat-label>
              <input 
                matInput 
                formControlName="username" 
                placeholder="Enter username"
                autocomplete="username"
                [attr.aria-describedby]="'username-errors'">
              <mat-icon matSuffix>person</mat-icon>
              
              <mat-error *ngIf="registerForm.get('username')?.hasError('required')">
                Username is required
              </mat-error>
              <mat-error *ngIf="registerForm.get('username')?.hasError('noSpecialCharacters')">
                Special characters are not allowed
              </mat-error>
              <mat-error *ngIf="registerForm.get('username')?.hasError('alphanumericOnly')">
                Only alphanumeric characters are allowed
              </mat-error>
              <mat-error *ngIf="registerForm.get('username')?.hasError('minLengthSecure')">
                Username must be at least {{ registerForm.get('username')?.errors?.['minLengthSecure']?.requiredLength }} characters
              </mat-error>
              <mat-error *ngIf="registerForm.get('username')?.hasError('maxLengthSecure')">
                Username cannot exceed {{ registerForm.get('username')?.errors?.['maxLengthSecure']?.requiredLength }} characters
              </mat-error>
            </mat-form-field>

            <!-- Email Field -->
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Email</mat-label>
              <input 
                matInput 
                type="email" 
                formControlName="email" 
                placeholder="Enter email"
                autocomplete="email"
                [attr.aria-describedby]="'email-errors'">
              <mat-icon matSuffix>email</mat-icon>
              
              <mat-error *ngIf="registerForm.get('email')?.hasError('required')">
                Email is required
              </mat-error>
              <mat-error *ngIf="registerForm.get('email')?.hasError('invalidEmail')">
                Please enter a valid email address
              </mat-error>
              <mat-error *ngIf="registerForm.get('email')?.hasError('noSpecialCharacters')">
                Special characters are not allowed
              </mat-error>
            </mat-form-field>

            <!-- Password Field -->
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Password</mat-label>
              <input 
                matInput 
                [type]="showPassword ? 'text' : 'password'" 
                formControlName="password" 
                placeholder="Enter password"
                autocomplete="new-password"
                [attr.aria-describedby]="'password-errors'">
              <button 
                mat-icon-button 
                matSuffix 
                type="button"
                (click)="togglePasswordVisibility()"
                [attr.aria-label]="showPassword ? 'Hide password' : 'Show password'">
                <mat-icon>{{ showPassword ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              
              <mat-error *ngIf="registerForm.get('password')?.hasError('required')">
                Password is required
              </mat-error>
              <mat-error *ngIf="registerForm.get('password')?.hasError('missingUpperCase')">
                Password must contain at least one uppercase letter
              </mat-error>
              <mat-error *ngIf="registerForm.get('password')?.hasError('missingLowerCase')">
                Password must contain at least one lowercase letter
              </mat-error>
              <mat-error *ngIf="registerForm.get('password')?.hasError('missingNumber')">
                Password must contain at least one number
              </mat-error>
              <mat-error *ngIf="registerForm.get('password')?.hasError('missingSpecialChar')">
                Password must contain at least one special character (@$!%*?&)
              </mat-error>
              <mat-error *ngIf="registerForm.get('password')?.hasError('tooShort')">
                Password must be at least 8 characters long
              </mat-error>
              <mat-error *ngIf="registerForm.get('password')?.hasError('consecutiveRepeats')">
                Password cannot contain consecutive repeated characters
              </mat-error>
              <mat-error *ngIf="registerForm.get('password')?.hasError('commonPattern')">
                Password cannot contain common patterns
              </mat-error>
            </mat-form-field>

            <!-- Password Strength Indicator -->
            <div class="password-strength" *ngIf="registerForm.get('password')?.value">
              <mat-progress-bar 
                [value]="passwordStrength" 
                [color]="passwordStrengthColor">
              </mat-progress-bar>
              <span class="strength-text">{{ passwordStrengthText }}</span>
            </div>

            <!-- Confirm Password Field -->
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Confirm Password</mat-label>
              <input 
                matInput 
                [type]="showConfirmPassword ? 'text' : 'password'" 
                formControlName="confirmPassword" 
                placeholder="Confirm password"
                autocomplete="new-password"
                [attr.aria-describedby]="'confirm-password-errors'">
              <button 
                mat-icon-button 
                matSuffix 
                type="button"
                (click)="toggleConfirmPasswordVisibility()"
                [attr.aria-label]="showConfirmPassword ? 'Hide password' : 'Show password'">
                <mat-icon>{{ showConfirmPassword ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              
              <mat-error *ngIf="registerForm.get('confirmPassword')?.hasError('required')">
                Please confirm your password
              </mat-error>
              <mat-error *ngIf="registerForm.get('confirmPassword')?.hasError('passwordMismatch')">
                Passwords do not match
              </mat-error>
            </mat-form-field>

            <!-- Phone Number Field -->
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Phone Number (Optional)</mat-label>
              <input 
                matInput 
                formControlName="phoneNumber" 
                placeholder="Enter phone number"
                autocomplete="tel"
                [attr.aria-describedby]="'phone-errors'">
              <mat-icon matSuffix>phone</mat-icon>
              
              <mat-error *ngIf="registerForm.get('phoneNumber')?.hasError('invalidPhoneNumber')">
                Please enter a valid phone number
              </mat-error>
            </mat-form-field>

            <!-- Terms and Conditions -->
            <div class="terms-section">
              <mat-checkbox 
                formControlName="acceptTerms"
                color="primary">
                I accept the 
                <a href="/terms" target="_blank" class="link">Terms and Conditions</a>
                and 
                <a href="/privacy" target="_blank" class="link">Privacy Policy</a>
              </mat-checkbox>
              
              <mat-error *ngIf="registerForm.get('acceptTerms')?.hasError('required') && registerForm.get('acceptTerms')?.touched">
                You must accept the terms and conditions
              </mat-error>
            </div>

            <!-- Security Notice -->
            <div class="security-notice">
              <mat-icon>security</mat-icon>
              <span>Your data is protected with industry-standard encryption and security measures.</span>
            </div>

            <!-- Submit Button -->
            <button 
              mat-raised-button 
              color="primary" 
              type="submit"
              [disabled]="registerForm.invalid || (loading$ | async)"
              class="submit-button">
              <mat-icon *ngIf="!(loading$ | async)">person_add</mat-icon>
              <mat-spinner *ngIf="loading$ | async" diameter="20"></mat-spinner>
              {{ (loading$ | async) ? 'Creating Account...' : 'Create Account' }}
            </button>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .secure-register-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      padding: 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .register-card {
      max-width: 500px;
      width: 100%;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      border-radius: 12px;
    }

    .register-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .full-width {
      width: 100%;
    }

    .password-strength {
      margin-top: -8px;
      margin-bottom: 8px;
    }

    .strength-text {
      font-size: 12px;
      margin-top: 4px;
      display: block;
    }

    .terms-section {
      margin: 16px 0;
    }

    .link {
      color: #1976d2;
      text-decoration: none;
    }

    .link:hover {
      text-decoration: underline;
    }

    .security-notice {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px;
      background-color: #e3f2fd;
      border-radius: 8px;
      font-size: 14px;
      color: #1976d2;
    }

    .submit-button {
      margin-top: 16px;
      height: 48px;
      font-size: 16px;
    }

    .submit-button mat-icon {
      margin-right: 8px;
    }

    @media (max-width: 600px) {
      .secure-register-container {
        padding: 10px;
      }
      
      .register-card {
        margin: 0;
      }
    }
  `]
})
export class SecureRegisterComponent implements OnInit {
  registerForm!: FormGroup;
  showPassword = false;
  showConfirmPassword = false;
  loading$: Observable<boolean>;

  constructor(
    private readonly fb: FormBuilder,
    private readonly router: Router,
    private readonly store: Store,
    private readonly securityService: SecurityService,
    private readonly authService: AuthService,
    private readonly notificationService: NotificationService
  ) {
    this.loading$ = this.store.select(AuthSelectors.selectIsLoading);
  }

  ngOnInit(): void {
    this.initForm();
    this.setupFormValidation();
  }

  private initForm(): void {
    this.registerForm = this.fb.group({
      username: ['', [
        Validators.required,
        SecurityValidators.noSpecialCharacters,
        SecurityValidators.alphanumericOnly,
        SecurityValidators.minLengthSecure(3),
        SecurityValidators.maxLengthSecure(50),
        SecurityValidators.noCommonPatterns
      ]],
      email: ['', [
        Validators.required,
        SecurityValidators.validEmail,
        SecurityValidators.noSpecialCharacters
      ]],
      password: ['', [
        Validators.required,
        SecurityValidators.securePassword,
        SecurityValidators.noConsecutiveRepeats,
        SecurityValidators.noCommonPatterns
      ]],
      confirmPassword: ['', [
        Validators.required,
        SecurityValidators.passwordMatch('password')
      ]],
      phoneNumber: ['', [
        SecurityValidators.validPhoneNumber
      ]],
      acceptTerms: [false, [
        Validators.requiredTrue
      ]]
    });
  }

  private setupFormValidation(): void {
    // Monitor password changes for strength calculation
    this.registerForm.get('password')?.valueChanges.subscribe(password => {
      if (password) {
        this.calculatePasswordStrength(password);
      }
    });

    // Real-time validation feedback
    this.registerForm.valueChanges.subscribe(values => {
      this.sanitizeFormValues(values);
    });
  }

  private sanitizeFormValues(values: any): void {
    Object.keys(values).forEach(key => {
      const control = this.registerForm.get(key);
      if (control && typeof values[key] === 'string' && key !== 'password' && key !== 'confirmPassword') {
        const sanitized = this.securityService.sanitizeUserInput(values[key]);
        if (sanitized !== values[key]) {
          control.setValue(sanitized, { emitEvent: false });
        }
      }
    });
  }

  private calculatePasswordStrength(password: string): void {
    let strength = 0;
    
    if (password.length >= 8) strength += 20;
    if (/[a-z]/.test(password)) strength += 20;
    if (/[A-Z]/.test(password)) strength += 20;
    if (/\d/.test(password)) strength += 20;
    if (/[@$!%*?&]/.test(password)) strength += 20;
    
    // Deduct points for common patterns
    if (/123|abc|qwe|password|admin|user|test/i.test(password)) {
      strength = Math.max(0, strength - 40);
    }
    
    // Deduct points for consecutive repeats
    for (let i = 0; i < password.length - 2; i++) {
      if (password[i] === password[i + 1] && password[i] === password[i + 2]) {
        strength = Math.max(0, strength - 20);
        break;
      }
    }
    
    this.passwordStrength = strength;
  }

  get passwordStrength(): number {
    return this._passwordStrength;
  }

  set passwordStrength(value: number) {
    this._passwordStrength = value;
  }

  private _passwordStrength = 0;

  get passwordStrengthColor(): string {
    if (this.passwordStrength >= 80) return 'accent';
    if (this.passwordStrength >= 60) return 'primary';
    if (this.passwordStrength >= 40) return 'warn';
    return 'warn';
  }

  get passwordStrengthText(): string {
    if (this.passwordStrength >= 80) return 'Very Strong';
    if (this.passwordStrength >= 60) return 'Strong';
    if (this.passwordStrength >= 40) return 'Medium';
    if (this.passwordStrength >= 20) return 'Weak';
    return 'Very Weak';
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const formData = this.registerForm.value;
      
      // Additional security checks
      if (!this.securityService.isValidEmail(formData.email)) {
        this.notificationService.showError('Error', 'Invalid email format');
        return;
      }

      if (!this.securityService.isStrongPassword(formData.password)) {
        this.notificationService.showError('Error', 'Password does not meet security requirements');
        return;
      }

      // Dispatch registration action
      this.store.dispatch(AuthActions.register({
        userData: {
          email: formData.email,
          password: formData.password,
          firstName: formData.firstName,
          lastName: formData.lastName
        }
      }));
    } else {
      this.markFormGroupTouched();
              this.notificationService.showError('Error', 'Please fix the errors in the form');
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach(key => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }

  // Helper methods for template
  getFieldError(fieldName: string): string | null {
    const control = this.registerForm.get(fieldName);
    if (control?.errors && control.touched) {
      const errors = control.errors;
      if (errors['required']) return `${fieldName} is required`;
      if (errors['invalidEmail']) return 'Invalid email format';
      if (errors['noSpecialCharacters']) return 'Special characters are not allowed';
      if (errors['alphanumericOnly']) return 'Only alphanumeric characters are allowed';
      if (errors['minLengthSecure']) return `Minimum ${errors['minLengthSecure'].requiredLength} characters required`;
      if (errors['maxLengthSecure']) return `Maximum ${errors['maxLengthSecure'].requiredLength} characters allowed`;
      if (errors['passwordMismatch']) return 'Passwords do not match';
      if (errors['invalidPhoneNumber']) return 'Invalid phone number format';
      if (errors['requiredTrue']) return 'You must accept the terms and conditions';
    }
    return null;
  }
}
