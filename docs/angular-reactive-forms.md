# Angular Reactive Forms — Patterns and Best Practices

## Overview
Angular reactive forms provide a model-driven approach to handling form inputs, offering greater flexibility and scalability for complex forms. They use explicit, immutable data structures to manage form state.

## Key Concepts

### FormControl
Manages the value and validation status of an individual form control.

```typescript
import { FormControl, Validators } from '@angular/forms';

const emailControl = new FormControl('', [
  Validators.required,
  Validators.email
]);
```

### FormGroup
Tracks the same values and status for a collection of form controls.

```typescript
import { FormGroup, FormControl, Validators } from '@angular/forms';

const userForm = new FormGroup({
  firstName: new FormControl('', Validators.required),
  lastName: new FormControl('', Validators.required),
  email: new FormControl('', [Validators.required, Validators.email])
});
```

### FormArray
Manages the value and validity state of an array of form controls.

```typescript
import { FormArray, FormControl } from '@angular/forms';

const skillsArray = new FormArray([
  new FormControl('Angular'),
  new FormControl('TypeScript'),
  new FormControl('RxJS')
]);
```

## Implementation Patterns

### Basic Form Setup
```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html'
})
export class UserFormComponent implements OnInit {
  userForm: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.userForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.pattern(/^\d{10}$/)],
      address: this.fb.group({
        street: ['', Validators.required],
        city: ['', Validators.required],
        state: ['', Validators.required],
        zipCode: ['', [Validators.required, Validators.pattern(/^\d{5}$/)]]
      })
    });
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      console.log(this.userForm.value);
    }
  }
}
```

### Template Binding
```html
<form [formGroup]="userForm" (ngSubmit)="onSubmit()">
  <mat-form-field>
    <mat-label>First Name</mat-label>
    <input matInput formControlName="firstName">
    <mat-error *ngIf="userForm.get('firstName')?.hasError('required')">
      First name is required
    </mat-error>
    <mat-error *ngIf="userForm.get('firstName')?.hasError('minlength')">
      First name must be at least 2 characters
    </mat-error>
  </mat-form-field>

  <div formGroupName="address">
    <mat-form-field>
      <mat-label>Street</mat-label>
      <input matInput formControlName="street">
    </mat-form-field>
  </div>

  <button mat-raised-button color="primary" type="submit" 
          [disabled]="userForm.invalid">
    Submit
  </button>
</form>
```

## Validation Patterns

### Custom Validators
```typescript
import { AbstractControl, ValidationErrors } from '@angular/forms';

export function passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (!value) return null;

  const hasUpperCase = /[A-Z]/.test(value);
  const hasLowerCase = /[a-z]/.test(value);
  const hasNumbers = /\d/.test(value);
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);

  const valid = hasUpperCase && hasLowerCase && hasNumbers && hasSpecialChar;
  return valid ? null : { passwordStrength: true };
}

// Usage
const passwordControl = new FormControl('', [
  Validators.required,
  Validators.minLength(8),
  passwordStrengthValidator
]);
```

### Cross-Field Validation
```typescript
export function passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password');
  const confirmPassword = group.get('confirmPassword');

  if (!password || !confirmPassword) return null;

  return password.value === confirmPassword.value ? null : { passwordMismatch: true };
}

// Usage in FormGroup
const form = this.fb.group({
  password: ['', [Validators.required, Validators.minLength(8)]],
  confirmPassword: ['', Validators.required]
}, { validators: passwordMatchValidator });
```

### Async Validators
```typescript
import { AbstractControl, AsyncValidatorFn } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { map, delay } from 'rxjs/operators';

export function emailExistsValidator(userService: UserService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    if (!control.value) return of(null);

    return userService.checkEmailExists(control.value).pipe(
      map(exists => exists ? { emailExists: true } : null)
    );
  };
}

// Usage
const emailControl = new FormControl('', {
  validators: [Validators.required, Validators.email],
  asyncValidators: [emailExistsValidator(this.userService)]
});
```

## Dynamic Forms

### Adding/Removing Form Controls
```typescript
export class DynamicFormComponent {
  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      skills: this.fb.array([])
    });
  }

  get skillsArray(): FormArray {
    return this.form.get('skills') as FormArray;
  }

  addSkill(): void {
    this.skillsArray.push(new FormControl('', Validators.required));
  }

  removeSkill(index: number): void {
    this.skillsArray.removeAt(index);
  }
}
```

### Template for Dynamic Forms
```html
<form [formGroup]="form">
  <mat-form-field>
    <mat-label>Name</mat-label>
    <input matInput formControlName="name">
  </mat-form-field>

  <div formArrayName="skills">
    <div *ngFor="let skill of skillsArray.controls; let i = index">
      <mat-form-field>
        <mat-label>Skill {{ i + 1 }}</mat-label>
        <input matInput [formControlName]="i">
        <button mat-icon-button matSuffix (click)="removeSkill(i)">
          <mat-icon>delete</mat-icon>
        </button>
      </mat-form-field>
    </div>
  </div>

  <button mat-button type="button" (click)="addSkill()">
    Add Skill
  </button>
</form>
```

## Form State Management

### Reactive Form State
```typescript
export class FormStateComponent {
  form: FormGroup;

  // Form state observables
  formValue$ = this.form.valueChanges;
  formStatus$ = this.form.statusChanges;
  isFormValid$ = this.form.statusChanges.pipe(
    map(status => status === 'VALID')
  );

  // Individual control states
  emailControl = this.form.get('email');
  emailValue$ = this.emailControl?.valueChanges;
  emailErrors$ = this.emailControl?.statusChanges.pipe(
    map(() => this.emailControl?.errors)
  );
}
```

### Form Reset and Patch
```typescript
export class FormManagementComponent {
  form: FormGroup;

  // Reset form to initial state
  resetForm(): void {
    this.form.reset();
  }

  // Reset with specific values
  resetWithValues(): void {
    this.form.reset({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com'
    });
  }

  // Patch specific values
  patchForm(): void {
    this.form.patchValue({
      firstName: 'Jane',
      email: 'jane@example.com'
    });
  }

  // Set specific values (requires all controls)
  setFormValues(): void {
    this.form.setValue({
      firstName: 'Bob',
      lastName: 'Smith',
      email: 'bob@example.com',
      address: {
        street: '123 Main St',
        city: 'Anytown',
        state: 'CA',
        zipCode: '12345'
      }
    });
  }
}
```

## Error Handling

### Global Error Handling
```typescript
export class FormErrorHandler {
  static getErrorMessage(control: AbstractControl, fieldName: string): string {
    if (control.hasError('required')) {
      return `${fieldName} is required`;
    }
    if (control.hasError('email')) {
      return 'Please enter a valid email address';
    }
    if (control.hasError('minlength')) {
      const requiredLength = control.errors?.['minlength'].requiredLength;
      return `${fieldName} must be at least ${requiredLength} characters`;
    }
    if (control.hasError('pattern')) {
      return `Please enter a valid ${fieldName}`;
    }
    return `${fieldName} is invalid`;
  }
}
```

### Template Error Display
```html
<mat-form-field>
  <mat-label>Email</mat-label>
  <input matInput formControlName="email">
  <mat-error *ngIf="form.get('email')?.hasError('required')">
    Email is required
  </mat-error>
  <mat-error *ngIf="form.get('email')?.hasError('email')">
    Please enter a valid email address
  </mat-error>
  <mat-error *ngIf="form.get('email')?.hasError('emailExists')">
    This email is already registered
  </mat-error>
</mat-form-field>
```

## Performance Optimization

### OnPush Change Detection
```typescript
import { ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserFormComponent {
  // Use observables for reactive updates
  formValue$ = this.form.valueChanges;
  formStatus$ = this.form.statusChanges;
}
```

### Debounced Form Updates
```typescript
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

export class DebouncedFormComponent implements OnInit {
  form: FormGroup;

  ngOnInit(): void {
    // Debounce form value changes
    this.form.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(value => {
      this.saveFormDraft(value);
    });
  }

  private saveFormDraft(value: any): void {
    // Auto-save form data
    localStorage.setItem('formDraft', JSON.stringify(value));
  }
}
```

## Best Practices

### 1. Use FormBuilder
```typescript
// Good
constructor(private fb: FormBuilder) {
  this.form = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]]
  });
}

// Avoid
constructor() {
  this.form = new FormGroup({
    name: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email])
  });
}
```

### 2. Separate Validation Logic
```typescript
// Create reusable validators
export class CustomValidators {
  static passwordStrength = (control: AbstractControl): ValidationErrors | null => {
    // Validation logic
  };

  static emailExists = (userService: UserService): AsyncValidatorFn => {
    // Async validation logic
  };
}
```

### 3. Use TypeScript for Form Types
```typescript
interface UserFormData {
  firstName: string;
  lastName: string;
  email: string;
  address: {
    street: string;
    city: string;
    state: string;
    zipCode: string;
  };
}

export class TypedFormComponent {
  form: FormGroup<UserFormData>;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group<UserFormData>({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      address: this.fb.group({
        street: ['', Validators.required],
        city: ['', Validators.required],
        state: ['', Validators.required],
        zipCode: ['', Validators.required]
      })
    });
  }
}
```

### 4. Handle Form Submission Properly
```typescript
export class FormSubmissionComponent {
  form: FormGroup;
  isSubmitting = false;

  async onSubmit(): Promise<void> {
    if (this.form.invalid) {
      this.markFormGroupTouched(this.form);
      return;
    }

    this.isSubmitting = true;
    try {
      const formData = this.form.value;
      await this.userService.createUser(formData);
      this.form.reset();
      this.showSuccessMessage();
    } catch (error) {
      this.handleSubmissionError(error);
    } finally {
      this.isSubmitting = false;
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}
```

## Testing Reactive Forms

### Unit Testing
```typescript
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

describe('UserFormComponent', () => {
  let component: UserFormComponent;
  let fixture: ComponentFixture<UserFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [UserFormComponent]
    }).compileComponents();
  });

  it('should create form with required controls', () => {
    expect(component.form.get('firstName')).toBeTruthy();
    expect(component.form.get('email')).toBeTruthy();
  });

  it('should validate required fields', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('');
    expect(emailControl?.hasError('required')).toBeTruthy();
  });

  it('should validate email format', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.hasError('email')).toBeTruthy();
  });
});
```

## References
- [Angular Reactive Forms Guide](https://angular.dev/guide/forms/reactive-forms)
- [Angular Form Validation](https://angular.dev/guide/forms/form-validation)
- [Angular Dynamic Forms](https://angular.dev/guide/forms/dynamic-forms)
