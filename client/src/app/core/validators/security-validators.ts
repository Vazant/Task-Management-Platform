import { AbstractControl, ValidationErrors } from '@angular/forms';

export class SecurityValidators {
  /**
   * Validator to prevent special characters that could be used for XSS
   */
  static noSpecialCharacters(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const specialCharRegex = /[<>\"'&]/;
    if (specialCharRegex.test(value)) {
      return { noSpecialCharacters: true };
    }
    
    return null;
  }

  /**
   * Validator for strong password requirements
   */
  static securePassword(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumbers = /\d/.test(value);
    const hasSpecialChar = /[@$!%*?&]/.test(value);
    const isLongEnough = value.length >= 8;
    
    const errors: ValidationErrors = {};
    
    if (!hasUpperCase) errors.missingUpperCase = true;
    if (!hasLowerCase) errors.missingLowerCase = true;
    if (!hasNumbers) errors.missingNumber = true;
    if (!hasSpecialChar) errors.missingSpecialChar = true;
    if (!isLongEnough) errors.tooShort = true;
    
    return Object.keys(errors).length > 0 ? errors : null;
  }

  /**
   * Validator that automatically sanitizes input
   */
  static sanitizeInput(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    // Remove potentially dangerous characters
    const sanitized = value.replace(/[<>\"'&]/g, '');
    
    if (sanitized !== value) {
      control.setValue(sanitized);
    }
    
    return null;
  }

  /**
   * Validator for email format
   */
  static validEmail(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(value)) {
      return { invalidEmail: true };
    }
    
    return null;
  }

  /**
   * Validator for URL format
   */
  static validUrl(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    try {
      new URL(value);
      return null;
    } catch {
      return { invalidUrl: true };
    }
  }

  /**
   * Validator to prevent SQL injection patterns
   */
  static noSqlInjection(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const sqlPatterns = [
      /(\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|SCRIPT)\b)/i,
      /(\b(OR|AND)\b\s+\d+\s*=\s*\d+)/i,
      /(\b(OR|AND)\b\s+['"]?\w+['"]?\s*=\s*['"]?\w+['"]?)/i,
      /(--|\/\*|\*\/|;)/,
      /(\b(WAITFOR|DELAY)\b)/i
    ];
    
    for (const pattern of sqlPatterns) {
      if (pattern.test(value)) {
        return { sqlInjectionDetected: true };
      }
    }
    
    return null;
  }

  /**
   * Validator to prevent script injection
   */
  static noScriptInjection(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const scriptPatterns = [
      /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
      /javascript:/gi,
      /vbscript:/gi,
      /on\w+\s*=/gi,
      /expression\s*\(/gi
    ];
    
    for (const pattern of scriptPatterns) {
      if (pattern.test(value)) {
        return { scriptInjectionDetected: true };
      }
    }
    
    return null;
  }

  /**
   * Validator for minimum length with security considerations
   */
  static minLengthSecure(minLength: number) {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;
      
      if (value.length < minLength) {
        return { minLengthSecure: { requiredLength: minLength, actualLength: value.length } };
      }
      
      return null;
    };
  }

  /**
   * Validator for maximum length to prevent buffer overflow attempts
   */
  static maxLengthSecure(maxLength: number) {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;
      
      if (value.length > maxLength) {
        return { maxLengthSecure: { requiredLength: maxLength, actualLength: value.length } };
      }
      
      return null;
    };
  }

  /**
   * Validator for alphanumeric characters only
   */
  static alphanumericOnly(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const alphanumericRegex = /^[a-zA-Z0-9]+$/;
    if (!alphanumericRegex.test(value)) {
      return { alphanumericOnly: true };
    }
    
    return null;
  }

  /**
   * Validator for no consecutive repeated characters
   */
  static noConsecutiveRepeats(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    for (let i = 0; i < value.length - 2; i++) {
      if (value[i] === value[i + 1] && value[i] === value[i + 2]) {
        return { consecutiveRepeats: true };
      }
    }
    
    return null;
  }

  /**
   * Validator for no common patterns
   */
  static noCommonPatterns(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const commonPatterns = [
      /123/,
      /abc/,
      /qwe/,
      /password/i,
      /admin/i,
      /user/i,
      /test/i
    ];
    
    for (const pattern of commonPatterns) {
      if (pattern.test(value)) {
        return { commonPattern: true };
      }
    }
    
    return null;
  }

  /**
   * Validator for password confirmation
   */
  static passwordMatch(passwordControlName: string) {
    return (control: AbstractControl): ValidationErrors | null => {
      const passwordControl = control.parent?.get(passwordControlName);
      
      if (!passwordControl) return null;
      
      if (control.value !== passwordControl.value) {
        return { passwordMismatch: true };
      }
      
      return null;
    };
  }

  /**
   * Validator for phone number format
   */
  static validPhoneNumber(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
    if (!phoneRegex.test(value.replace(/[\s\-\(\)]/g, ''))) {
      return { invalidPhoneNumber: true };
    }
    
    return null;
  }

  /**
   * Validator for date format and range
   */
  static validDateRange(minDate?: Date, maxDate?: Date) {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;
      
      const date = new Date(value);
      if (isNaN(date.getTime())) {
        return { invalidDate: true };
      }
      
      if (minDate && date < minDate) {
        return { dateTooEarly: { minDate, actualDate: date } };
      }
      
      if (maxDate && date > maxDate) {
        return { dateTooLate: { maxDate, actualDate: date } };
      }
      
      return null;
    };
  }

  /**
   * Validator for file type restrictions
   */
  static allowedFileTypes(allowedTypes: string[]) {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;
      
      if (value instanceof File) {
        const fileExtension = value.name.split('.').pop()?.toLowerCase();
        if (!fileExtension || !allowedTypes.includes(fileExtension)) {
          return { invalidFileType: { allowedTypes, actualType: fileExtension } };
        }
      }
      
      return null;
    };
  }

  /**
   * Validator for file size restrictions
   */
  static maxFileSize(maxSizeInBytes: number) {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;
      
      if (value instanceof File && value.size > maxSizeInBytes) {
        return { fileTooLarge: { maxSize: maxSizeInBytes, actualSize: value.size } };
      }
      
      return null;
    };
  }
}
