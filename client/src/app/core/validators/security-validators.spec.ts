import { AbstractControl, ValidationErrors } from '@angular/forms';
import { SecurityValidators } from './security-validators';

describe('SecurityValidators', () => {
  let mockControl: jasmine.SpyObj<AbstractControl>;
  let mockParent: jasmine.SpyObj<AbstractControl>;

  beforeEach(() => {
    mockControl = jasmine.createSpyObj('AbstractControl', ['setValue'], {
      value: '',
      parent: null
    });

    mockParent = jasmine.createSpyObj('AbstractControl', ['get'], {
      value: ''
    });
  });

  describe('noSpecialCharacters', () => {
    it('should return null for valid input', () => {
      mockControl.value = 'validInput123';
      expect(SecurityValidators.noSpecialCharacters(mockControl)).toBeNull();
    });

    it('should return error for input with special characters', () => {
      mockControl.value = 'input<script>alert("XSS")</script>';
      expect(SecurityValidators.noSpecialCharacters(mockControl)).toEqual({
        noSpecialCharacters: true
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.noSpecialCharacters(mockControl)).toBeNull();
    });

    it('should return null for null input', () => {
      mockControl.value = null;
      expect(SecurityValidators.noSpecialCharacters(mockControl)).toBeNull();
    });
  });

  describe('securePassword', () => {
    it('should return null for strong password', () => {
      mockControl.value = 'StrongPass123!';
      expect(SecurityValidators.securePassword(mockControl)).toBeNull();
    });

    it('should return errors for weak password - missing uppercase', () => {
      mockControl.value = 'weakpass123!';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        missingUpperCase: true
      });
    });

    it('should return errors for weak password - missing lowercase', () => {
      mockControl.value = 'WEAKPASS123!';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        missingLowerCase: true
      });
    });

    it('should return errors for weak password - missing number', () => {
      mockControl.value = 'WeakPass!';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        missingNumber: true
      });
    });

    it('should return errors for weak password - missing special char', () => {
      mockControl.value = 'WeakPass123';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        missingSpecialChar: true
      });
    });

    it('should return errors for weak password - too short', () => {
      mockControl.value = 'Weak1!';
      expect(SecurityValidators.securePassword(mockControl)).toEqual({
        tooShort: true
      });
    });

    it('should return multiple errors for very weak password', () => {
      mockControl.value = 'weak';
      const result = SecurityValidators.securePassword(mockControl);
      expect(result).toEqual(jasmine.objectContaining({
        missingUpperCase: true,
        missingNumber: true,
        missingSpecialChar: true,
        tooShort: true
      }));
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.securePassword(mockControl)).toBeNull();
    });
  });

  describe('sanitizeInput', () => {
    it('should sanitize input and update control value', () => {
      mockControl.value = '<script>alert("XSS")</script>';
      SecurityValidators.sanitizeInput(mockControl);
      expect(mockControl.setValue).toHaveBeenCalledWith('scriptalert("XSS")/script');
    });

    it('should not update control for safe input', () => {
      mockControl.value = 'safe input';
      SecurityValidators.sanitizeInput(mockControl);
      expect(mockControl.setValue).not.toHaveBeenCalled();
    });

    it('should return null', () => {
      mockControl.value = 'test';
      expect(SecurityValidators.sanitizeInput(mockControl)).toBeNull();
    });
  });

  describe('validEmail', () => {
    it('should return null for valid email', () => {
      mockControl.value = 'test@example.com';
      expect(SecurityValidators.validEmail(mockControl)).toBeNull();
    });

    it('should return error for invalid email', () => {
      mockControl.value = 'invalid-email';
      expect(SecurityValidators.validEmail(mockControl)).toEqual({
        invalidEmail: true
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.validEmail(mockControl)).toBeNull();
    });
  });

  describe('validUrl', () => {
    it('should return null for valid URL', () => {
      mockControl.value = 'https://example.com';
      expect(SecurityValidators.validUrl(mockControl)).toBeNull();
    });

    it('should return error for invalid URL', () => {
      mockControl.value = 'not-a-url';
      expect(SecurityValidators.validUrl(mockControl)).toEqual({
        invalidUrl: true
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.validUrl(mockControl)).toBeNull();
    });
  });

  describe('noSqlInjection', () => {
    it('should return null for safe input', () => {
      mockControl.value = 'normal user input';
      expect(SecurityValidators.noSqlInjection(mockControl)).toBeNull();
    });

    it('should detect SELECT statement', () => {
      mockControl.value = 'SELECT * FROM users';
      expect(SecurityValidators.noSqlInjection(mockControl)).toEqual({
        sqlInjectionDetected: true
      });
    });

    it('should detect INSERT statement', () => {
      mockControl.value = 'INSERT INTO users VALUES';
      expect(SecurityValidators.noSqlInjection(mockControl)).toEqual({
        sqlInjectionDetected: true
      });
    });

    it('should detect OR condition', () => {
      mockControl.value = "admin' OR '1'='1";
      expect(SecurityValidators.noSqlInjection(mockControl)).toEqual({
        sqlInjectionDetected: true
      });
    });

    it('should detect comment syntax', () => {
      mockControl.value = 'admin--';
      expect(SecurityValidators.noSqlInjection(mockControl)).toEqual({
        sqlInjectionDetected: true
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.noSqlInjection(mockControl)).toBeNull();
    });
  });

  describe('noScriptInjection', () => {
    it('should return null for safe input', () => {
      mockControl.value = 'normal text content';
      expect(SecurityValidators.noScriptInjection(mockControl)).toBeNull();
    });

    it('should detect script tags', () => {
      mockControl.value = '<script>alert("XSS")</script>';
      expect(SecurityValidators.noScriptInjection(mockControl)).toEqual({
        scriptInjectionDetected: true
      });
    });

    it('should detect javascript protocol', () => {
      mockControl.value = 'javascript:alert("XSS")';
      expect(SecurityValidators.noScriptInjection(mockControl)).toEqual({
        scriptInjectionDetected: true
      });
    });

    it('should detect event handlers', () => {
      mockControl.value = 'onclick="alert(\'XSS\')"';
      expect(SecurityValidators.noScriptInjection(mockControl)).toEqual({
        scriptInjectionDetected: true
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.noScriptInjection(mockControl)).toBeNull();
    });
  });

  describe('minLengthSecure', () => {
    it('should return null for valid length', () => {
      mockControl.value = '123456';
      const validator = SecurityValidators.minLengthSecure(5);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for short input', () => {
      mockControl.value = '123';
      const validator = SecurityValidators.minLengthSecure(5);
      expect(validator(mockControl)).toEqual({
        minLengthSecure: { requiredLength: 5, actualLength: 3 }
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      const validator = SecurityValidators.minLengthSecure(5);
      expect(validator(mockControl)).toBeNull();
    });
  });

  describe('maxLengthSecure', () => {
    it('should return null for valid length', () => {
      mockControl.value = '123';
      const validator = SecurityValidators.maxLengthSecure(5);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for long input', () => {
      mockControl.value = '123456789';
      const validator = SecurityValidators.maxLengthSecure(5);
      expect(validator(mockControl)).toEqual({
        maxLengthSecure: { requiredLength: 5, actualLength: 9 }
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      const validator = SecurityValidators.maxLengthSecure(5);
      expect(validator(mockControl)).toBeNull();
    });
  });

  describe('alphanumericOnly', () => {
    it('should return null for alphanumeric input', () => {
      mockControl.value = 'abc123';
      expect(SecurityValidators.alphanumericOnly(mockControl)).toBeNull();
    });

    it('should return error for input with special characters', () => {
      mockControl.value = 'abc123!@#';
      expect(SecurityValidators.alphanumericOnly(mockControl)).toEqual({
        alphanumericOnly: true
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.alphanumericOnly(mockControl)).toBeNull();
    });
  });

  describe('noConsecutiveRepeats', () => {
    it('should return null for input without consecutive repeats', () => {
      mockControl.value = 'abc123';
      expect(SecurityValidators.noConsecutiveRepeats(mockControl)).toBeNull();
    });

    it('should return error for input with consecutive repeats', () => {
      mockControl.value = 'aaabc123';
      expect(SecurityValidators.noConsecutiveRepeats(mockControl)).toEqual({
        consecutiveRepeats: true
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.noConsecutiveRepeats(mockControl)).toBeNull();
    });
  });

  describe('noCommonPatterns', () => {
    it('should return null for input without common patterns', () => {
      mockControl.value = 'uniquePassword';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toBeNull();
    });

    it('should detect 123 pattern', () => {
      mockControl.value = 'password123';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toEqual({
        commonPattern: true
      });
    });

    it('should detect abc pattern', () => {
      mockControl.value = 'passwordabc';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toEqual({
        commonPattern: true
      });
    });

    it('should detect password pattern', () => {
      mockControl.value = 'mypassword';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toEqual({
        commonPattern: true
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.noCommonPatterns(mockControl)).toBeNull();
    });
  });

  describe('passwordMatch', () => {
    beforeEach(() => {
      mockControl.parent = mockParent;
    });

    it('should return null for matching passwords', () => {
      mockControl.value = 'password123';
      mockParent.get.and.returnValue({ value: 'password123' });
      
      const validator = SecurityValidators.passwordMatch('password');
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for non-matching passwords', () => {
      mockControl.value = 'password123';
      mockParent.get.and.returnValue({ value: 'different123' });
      
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
      mockControl.value = '+1234567890';
      expect(SecurityValidators.validPhoneNumber(mockControl)).toBeNull();
      
      mockControl.value = '1234567890';
      expect(SecurityValidators.validPhoneNumber(mockControl)).toBeNull();
    });

    it('should return error for invalid phone numbers', () => {
      mockControl.value = 'not-a-phone';
      expect(SecurityValidators.validPhoneNumber(mockControl)).toEqual({
        invalidPhoneNumber: true
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      expect(SecurityValidators.validPhoneNumber(mockControl)).toBeNull();
    });
  });

  describe('validDateRange', () => {
    const minDate = new Date('2023-01-01');
    const maxDate = new Date('2023-12-31');

    it('should return null for valid date', () => {
      mockControl.value = '2023-06-15';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for invalid date', () => {
      mockControl.value = 'invalid-date';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toEqual({
        invalidDate: true
      });
    });

    it('should return error for date too early', () => {
      mockControl.value = '2022-12-31';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toEqual({
        dateTooEarly: { minDate, actualDate: new Date('2022-12-31') }
      });
    });

    it('should return error for date too late', () => {
      mockControl.value = '2024-01-01';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toEqual({
        dateTooLate: { maxDate, actualDate: new Date('2024-01-01') }
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      const validator = SecurityValidators.validDateRange(minDate, maxDate);
      expect(validator(mockControl)).toBeNull();
    });
  });

  describe('allowedFileTypes', () => {
    const allowedTypes = ['jpg', 'png', 'pdf'];

    it('should return null for allowed file type', () => {
      const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
      mockControl.value = file;
      
      const validator = SecurityValidators.allowedFileTypes(allowedTypes);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for disallowed file type', () => {
      const file = new File([''], 'test.exe', { type: 'application/x-msdownload' });
      mockControl.value = file;
      
      const validator = SecurityValidators.allowedFileTypes(allowedTypes);
      expect(validator(mockControl)).toEqual({
        invalidFileType: { allowedTypes, actualType: 'exe' }
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      const validator = SecurityValidators.allowedFileTypes(allowedTypes);
      expect(validator(mockControl)).toBeNull();
    });
  });

  describe('maxFileSize', () => {
    const maxSize = 1024 * 1024; // 1MB

    it('should return null for file within size limit', () => {
      const file = new File(['x'.repeat(512 * 1024)], 'test.jpg', { type: 'image/jpeg' });
      mockControl.value = file;
      
      const validator = SecurityValidators.maxFileSize(maxSize);
      expect(validator(mockControl)).toBeNull();
    });

    it('should return error for file exceeding size limit', () => {
      const file = new File(['x'.repeat(2 * 1024 * 1024)], 'large.jpg', { type: 'image/jpeg' });
      mockControl.value = file;
      
      const validator = SecurityValidators.maxFileSize(maxSize);
      expect(validator(mockControl)).toEqual({
        fileTooLarge: { maxSize, actualSize: 2 * 1024 * 1024 }
      });
    });

    it('should return null for empty input', () => {
      mockControl.value = '';
      const validator = SecurityValidators.maxFileSize(maxSize);
      expect(validator(mockControl)).toBeNull();
    });
  });
});
