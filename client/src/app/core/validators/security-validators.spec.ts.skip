import { AbstractControl, ValidationErrors } from '@angular/forms';
import { SecurityValidators } from './security-validators';

describe('SecurityValidators', () => {
  let mockControl: jasmine.SpyObj<AbstractControl>;
  let mockParent: jasmine.SpyObj<AbstractControl>;

  beforeEach(() => {
    mockControl = jasmine.createSpyObj('AbstractControl', ['setValue'], {
      parent: null
    });
    
    // Create a simple mock with mutable value
    (mockControl as any).value = '';

    mockParent = jasmine.createSpyObj('AbstractControl', ['get'], {});
    (mockParent as any).value = '';
  });

  describe('noSpecialCharacters', () => {
    it('should return null for valid input', () => {
      (mockControl as any).value = 'validInput123';
      expect(SecurityValidators.noSpecialCharacters(mockControl)).toBeNull();
    });

    it('should return error for input with special characters', () => {
      (mockControl as any).value = 'input<script>alert("XSS")</script>';
      expect(SecurityValidators.noSpecialCharacters(mockControl)).toEqual({
        noSpecialCharacters: true
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.noSpecialCharacters(mockControl)).toBeNull();
    });

    it('should return null for null input', () => {
      (mockControl as any).value = null;
      expect(SecurityValidators.noSpecialCharacters(mockControl)).toBeNull();
    });
  });

  describe('securePassword', () => {
    it('should return null for strong password', () => {
      (mockControl as any).value = 'StrongPass123!';
      expect(SecurityValidators.securePassword(mockControl)).toBeNull();
    });

    it('should return errors for weak password - missing uppercase', () => {
      (mockControl as any).value = 'weakpass123!';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        missingUpperCase: true
      });
    });

    it('should return errors for weak password - missing lowercase', () => {
      (mockControl as any).value = 'WEAKPASS123!';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        missingLowerCase: true
      });
    });

    it('should return errors for weak password - missing number', () => {
      (mockControl as any).value = 'WeakPass!';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        missingNumber: true
      });
    });

    it('should return errors for weak password - missing special char', () => {
      (mockControl as any).value = 'WeakPass123';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        missingSpecialChar: true
      });
    });

    it('should return errors for weak password - too short', () => {
      (mockControl as any).value = 'Weak1!';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        tooShort: true
      });
    });

    it('should return multiple errors for very weak password', () => {
      (mockControl as any).value = 'weak';
      const result = SecurityValidators.securePassword(mockControl);
      expect(result).toEqual(jasmine.objectContaining({
        missingUpperCase: true,
        missingNumber: true,
        missingSpecialChar: true,
        tooShort: true
      }));
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.securePassword(mockControl)).toBeNull();
    });
  });

  describe('sanitizeInput', () => {
    it('should sanitize input and update control value', () => {
      (mockControl as any).value = '<script>alert("XSS")</script>';
      SecurityValidators.sanitizeInput(mockControl);
      expect(mockControl.setValue).toHaveBeenCalledWith('scriptalert("XSS")/script');
    });

    it('should not update control for safe input', () => {
      (mockControl as any).value = 'safe input';
      SecurityValidators.sanitizeInput(mockControl);
      expect(mockControl.setValue).not.toHaveBeenCalled();
    });

    it('should return null', () => {
      (mockControl as any).value = 'test';
      expect(SecurityValidators.sanitizeInput(mockControl)).toBeNull();
    });
  });

  describe('validEmail', () => {
    it('should return null for valid email', () => {
      (mockControl as any).value = 'test@example.com';
      expect(SecurityValidators.validEmail(mockControl)).toBeNull();
    });

    it('should return error for invalid email', () => {
      (mockControl as any).value = 'invalid-email';
      expect(SecurityValidators.validEmail(mockControl)).toEqual({
        invalidEmail: true
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.validEmail(mockControl)).toBeNull();
    });
  });

  describe('validUrl', () => {
    it('should return null for valid URL', () => {
      (mockControl as any).value = 'https://example.com';
      expect(SecurityValidators.validUrl(mockControl)).toBeNull();
    });

    it('should return error for invalid URL', () => {
      (mockControl as any).value = 'not-a-url';
      expect(SecurityValidators.validUrl(mockControl)).toEqual({
        invalidUrl: true
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.validUrl(mockControl)).toBeNull();
    });
  });

  describe('noSqlInjection', () => {
    it('should return null for safe input', () => {
      (mockControl as any).value = 'normal user input';
      expect(SecurityValidators.noSqlInjection(mockControl)).toBeNull();
    });

    it('should detect SELECT statement', () => {
      (mockControl as any).value = 'SELECT * FROM users';
      expect(SecurityValidators.noSqlInjection(mockControl)).toEqual({
        sqlInjectionDetected: true
      });
    });

    it('should detect INSERT statement', () => {
      (mockControl as any).value = 'INSERT INTO users VALUES';
      expect(SecurityValidators.noSqlInjection(mockControl)).toEqual({
        sqlInjectionDetected: true
      });
    });

    it('should detect OR condition', () => {
      (mockControl as any).value = "admin' OR '1'='1";
      expect(SecurityValidators.noSqlInjection(mockControl)).toEqual({
        sqlInjectionDetected: true
      });
    });

    it('should detect comment syntax', () => {
      (mockControl as any).value = 'admin--';
      expect(SecurityValidators.noSqlInjection(mockControl)).toEqual({
        sqlInjectionDetected: true
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.noSqlInjection(mockControl)).toBeNull();
    });
  });

  describe('noScriptInjection', () => {
    it('should return null for safe input', () => {
      (mockControl as any).value = 'normal text content';
      expect(SecurityValidators.noScriptInjection(mockControl)).toBeNull();
    });

    it('should detect script tags', () => {
      (mockControl as any).value = '<script>alert("XSS")</script>';
      expect(SecurityValidators.noScriptInjection(mockControl)).toEqual({
        scriptInjectionDetected: true
      });
    });

    it('should detect javascript protocol', () => {
      (mockControl as any).value = 'javascript:alert("XSS")';
      expect(SecurityValidators.noScriptInjection(mockControl)).toEqual({
        scriptInjectionDetected: true
      });
    });

    it('should detect event handlers', () => {
      (mockControl as any).value = 'onclick="alert(\'XSS\')"';
      expect(SecurityValidators.noScriptInjection(mockControl)).toEqual({
        scriptInjectionDetected: true
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.noScriptInjection(mockControl)).toBeNull();
    });
  });

  describe('minLengthSecure', () => {
    it('should return null for valid length', () => {
      (mockControl as any).value = '123456';
      const validator = SecurityValidators.minLengthSecure(5);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for short input', () => {
      (mockControl as any).value = '123';
      const validator = SecurityValidators.minLengthSecure(5);
      expect(validator(mockControl)).toEqual({
        minLengthSecure: { requiredLength: 5, actualLength: 3 }
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      const validator = SecurityValidators.minLengthSecure(5);
      expect(validator(mockControl)).toBeNull();
    });
  });

  describe('maxLengthSecure', () => {
    it('should return null for valid length', () => {
      (mockControl as any).value = '123';
      const validator = SecurityValidators.maxLengthSecure(5);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for long input', () => {
      (mockControl as any).value = '123456789';
      const validator = SecurityValidators.maxLengthSecure(5);
      expect(validator(mockControl)).toEqual({
        maxLengthSecure: { requiredLength: 5, actualLength: 9 }
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      const validator = SecurityValidators.maxLengthSecure(5);
      expect(validator(mockControl)).toBeNull();
    });
  });

  describe('alphanumericOnly', () => {
    it('should return null for alphanumeric input', () => {
      (mockControl as any).value = 'abc123';
      expect(SecurityValidators.alphanumericOnly(mockControl)).toBeNull();
    });

    it('should return error for input with special characters', () => {
      (mockControl as any).value = 'abc123!@#';
      expect(SecurityValidators.alphanumericOnly(mockControl)).toEqual({
        alphanumericOnly: true
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.alphanumericOnly(mockControl)).toBeNull();
    });
  });

  describe('noConsecutiveRepeats', () => {
    it('should return null for input without consecutive repeats', () => {
      (mockControl as any).value = 'abc123';
      expect(SecurityValidators.noConsecutiveRepeats(mockControl)).toBeNull();
    });

    it('should return error for input with consecutive repeats', () => {
      (mockControl as any).value = 'aaabc123';
      expect(SecurityValidators.noConsecutiveRepeats(mockControl)).toEqual({
        consecutiveRepeats: true
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.noConsecutiveRepeats(mockControl)).toBeNull();
    });
  });

  describe('noCommonPatterns', () => {
    it('should return null for input without common patterns', () => {
      (mockControl as any).value = 'uniquePassword';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toBeNull();
    });

    it('should detect 123 pattern', () => {
      (mockControl as any).value = 'password123';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toEqual({
        commonPattern: true
      });
    });

    it('should detect abc pattern', () => {
      (mockControl as any).value = 'passwordabc';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toEqual({
        commonPattern: true
      });
    });

    it('should detect password pattern', () => {
      (mockControl as any).value = 'mypassword';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toEqual({
        commonPattern: true
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toBeNull();
    });
  });

  describe('passwordMatch', () => {
    beforeEach(() => {
      (mockControl as any).parent = mockParent;
    });

    it('should return null for matching passwords', () => {
      (mockControl as any).value = 'password123';
      const passwordControl = jasmine.createSpyObj('AbstractControl', [], { value: 'password123' });
      mockParent.get.and.returnValue(passwordControl);
      
      const validator = SecurityValidators.passwordMatch('password');
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for non-matching passwords', () => {
      (mockControl as any).value = 'password123';
      const passwordControl = jasmine.createSpyObj('AbstractControl', [], { value: 'different123' });
      mockParent.get.and.returnValue(passwordControl);
      
      const validator = SecurityValidators.passwordMatch('password');
      expect(validator(mockControl)).toEqual({
        passwordMismatch: true
      });
    });

    it('should return null when parent control not found', () => {
      mockParent.get.and.returnValue(null);
      
      const validator = SecurityValidators.passwordMatch('password');
      expect(validator(mockControl)).toBeNull();
    });
  });

  describe('validPhoneNumber', () => {
    it('should return null for valid phone numbers', () => {
      (mockControl as any).value = '+1234567890';
      expect(SecurityValidators.validPhoneNumber(mockControl)).toBeNull();
      
      (mockControl as any).value = '1234567890';
      expect(SecurityValidators.validPhoneNumber(mockControl)).toBeNull();
    });

    it('should return error for invalid phone numbers', () => {
      (mockControl as any).value = 'not-a-phone';
      expect(SecurityValidators.validPhoneNumber(mockControl)).toEqual({
        invalidPhoneNumber: true
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      expect(SecurityValidators.validPhoneNumber(mockControl)).toBeNull();
    });
  });

  describe('validDateRange', () => {
    const minDate = new Date('2023-01-01');
    const maxDate = new Date('2023-12-31');

    it('should return null for valid date', () => {
      (mockControl as any).value = '2023-06-15';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for invalid date', () => {
      (mockControl as any).value = 'invalid-date';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toEqual({
        invalidDate: true
      });
    });

    it('should return error for date too early', () => {
      (mockControl as any).value = '2022-12-31';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toEqual({
        dateTooEarly: { minDate, actualDate: new Date('2022-12-31') }
      });
    });

    it('should return error for date too late', () => {
      (mockControl as any).value = '2024-01-01';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toEqual({
        dateTooLate: { maxDate, actualDate: new Date('2024-01-01') }
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toBeNull();
    });
  });

  describe('allowedFileTypes', () => {
    const allowedTypes = ['jpg', 'png', 'pdf'];

    it('should return null for allowed file type', () => {
      const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
      (mockControl as any).value = file;
      
      const validator = SecurityValidators.allowedFileTypes(allowedTypes);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for disallowed file type', () => {
      const file = new File([''], 'test.exe', { type: 'application/x-msdownload' });
      (mockControl as any).value = file;
      
      const validator = SecurityValidators.allowedFileTypes(allowedTypes);
      expect(validator(mockControl)).toEqual({
        invalidFileType: { allowedTypes, actualType: 'exe' }
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      const validator = SecurityValidators.allowedFileTypes(allowedTypes);
      expect(validator(mockControl)).toBeNull();
    });
  });

  describe('maxFileSize', () => {
    const maxSize = 1024 * 1024; // 1MB

    it('should return null for file within size limit', () => {
      const file = new File(['x'.repeat(512 * 1024)], 'test.jpg', { type: 'image/jpeg' });
      (mockControl as any).value = file;
      
      const validator = SecurityValidators.maxFileSize(maxSize);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for file exceeding size limit', () => {
      const file = new File(['x'.repeat(2 * 1024 * 1024)], 'large.jpg', { type: 'image/jpeg' });
      (mockControl as any).value = file;
      
      const validator = SecurityValidators.maxFileSize(maxSize);
      expect(validator(mockControl)).toEqual({
        fileTooLarge: { maxSize, actualSize: 2 * 1024 * 1024 }
      });
    });

    it('should return null for empty input', () => {
      (mockControl as any).value = '';
      const validator = SecurityValidators.maxFileSize(maxSize);
      expect(validator(mockControl)).toBeNull();
    });
  });
});
