import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { validatePassword, passwordsMatch } from '../utils/password.utils';

// ========================================
// CUSTOM VALIDATORS
// ========================================

/**
 * Email validator
 */
export function emailValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) {
    return null;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const isValid = emailRegex.test(control.value);

  return isValid ? null : { email: true };
}

/**
 * Username validator
 */
export function usernameValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) {
    return null;
  }

  const value = control.value.trim();
  
  if (value.length < 3) {
    return { usernameMinLength: { requiredLength: 3, actualLength: value.length } };
  }
  
  if (value.length > 20) {
    return { usernameMaxLength: { requiredLength: 20, actualLength: value.length } };
  }
  
  // Only allow alphanumeric characters, underscores, and hyphens
  const usernameRegex = /^[a-zA-Z0-9_-]+$/;
  if (!usernameRegex.test(value)) {
    return { usernamePattern: true };
  }

  return null;
}

/**
 * Password validator
 */
export function passwordValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) {
    return null;
  }

  const validation = validatePassword(control.value);
  
  if (!validation.isValid) {
    return { 
      password: { 
        errors: validation.errors,
        strength: validation.strength
      }
    };
  }

  return null;
}

/**
 * Password confirmation validator
 */
export function passwordConfirmationValidator(passwordControlName: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }

    const passwordControl = control.parent?.get(passwordControlName);
    if (!passwordControl) {
      return null;
    }

    const password = passwordControl.value;
    const confirmPassword = control.value;

    if (!passwordsMatch(password, confirmPassword)) {
      return { passwordMismatch: true };
    }

    return null;
  };
}

/**
 * Terms agreement validator
 */
export function termsAgreementValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) {
    return { termsRequired: true };
  }

  return null;
}

/**
 * Required field validator with custom message
 */
export function requiredValidator(message: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value || (typeof control.value === 'string' && !control.value.trim())) {
      return { required: { message } };
    }
    return null;
  };
}

/**
 * Minimum length validator with custom message
 */
export function minLengthValidator(minLength: number, message: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }

    const value = control.value.toString();
    if (value.length < minLength) {
      return { minlength: { requiredLength: minLength, actualLength: value.length, message } };
    }

    return null;
  };
}

/**
 * Maximum length validator with custom message
 */
export function maxLengthValidator(maxLength: number, message: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }

    const value = control.value.toString();
    if (value.length > maxLength) {
      return { maxlength: { requiredLength: maxLength, actualLength: value.length, message } };
    }

    return null;
  };
}

// ========================================
// VALIDATION ERROR MESSAGES
// ========================================

export interface ValidationErrorMessages {
  required: string;
  email: string;
  usernameMinLength: string;
  usernameMaxLength: string;
  usernamePattern: string;
  password: string;
  passwordMismatch: string;
  termsRequired: string;
  minlength: string;
  maxlength: string;
}

export const defaultValidationMessages: ValidationErrorMessages = {
  required: 'Поле обязательно для заполнения',
  email: 'Введите корректный email',
  usernameMinLength: 'Имя должно содержать минимум {requiredLength} символa',
  usernameMaxLength: 'Имя не должно превышать {requiredLength} символов',
  usernamePattern: 'Имя может содержать только буквы, цифры, _ и -',
  password: 'Пароль не соответствует требованиям',
  passwordMismatch: 'Пароли не совпадают',
  termsRequired: 'Необходимо согласиться с условиями',
  minlength: 'Минимум {requiredLength} символa',
  maxlength: 'Максимум {requiredLength} символов'
};

/**
 * Get validation error message
 */
export function getValidationErrorMessage(
  errors: ValidationErrors,
  messages: ValidationErrorMessages = defaultValidationMessages
): string {
  const errorKeys = Object.keys(errors);
  
  if (errorKeys.length === 0) {
    return '';
  }

  const firstError = errorKeys[0];
  const errorValue = errors[firstError];

  switch (firstError) {
    case 'required':
      return messages.required;
    
    case 'email':
      return messages.email;
    
    case 'usernameMinLength':
      return messages.usernameMinLength.replace('{requiredLength}', errorValue.requiredLength);
    
    case 'usernameMaxLength':
      return messages.usernameMaxLength.replace('{requiredLength}', errorValue.requiredLength);
    
    case 'usernamePattern':
      return messages.usernamePattern;
    
    case 'password':
      return messages.password;
    
    case 'passwordMismatch':
      return messages.passwordMismatch;
    
    case 'termsRequired':
      return messages.termsRequired;
    
    case 'minlength':
      return messages.minlength.replace('{requiredLength}', errorValue.requiredLength);
    
    case 'maxlength':
      return messages.maxlength.replace('{requiredLength}', errorValue.requiredLength);
    
    default:
      return 'Неверное значение';
  }
}



