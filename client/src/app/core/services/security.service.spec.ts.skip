import { TestBed } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { SecurityService } from './security.service';

describe('SecurityService', () => {
  let service: SecurityService;
  let sanitizer: jasmine.SpyObj<DomSanitizer>;

  beforeEach(() => {
    const sanitizerSpy = jasmine.createSpyObj('DomSanitizer', [
      'bypassSecurityTrustHtml',
      'bypassSecurityTrustUrl',
      'bypassSecurityTrustStyle'
    ]);

    TestBed.configureTestingModule({
      providers: [
        SecurityService,
        { provide: DomSanitizer, useValue: sanitizerSpy }
      ]
    });

    service = TestBed.inject(SecurityService);
    sanitizer = TestBed.inject(DomSanitizer) as jasmine.SpyObj<DomSanitizer>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('sanitizeUserInput', () => {
    it('should return null for null input', () => {
      expect(service.sanitizeUserInput(null as any)).toBeNull();
    });

    it('should return empty string for empty input', () => {
      expect(service.sanitizeUserInput('')).toBe('');
    });

    it('should remove dangerous characters', () => {
      const input = '<script>alert("XSS")</script>';
      const expected = 'scriptalert("XSS")/script';
      expect(service.sanitizeUserInput(input)).toBe(expected);
    });

    it('should remove javascript protocol', () => {
      const input = 'javascript:alert("XSS")';
      const expected = 'alert("XSS")';
      expect(service.sanitizeUserInput(input)).toBe(expected);
    });

    it('should remove vbscript protocol', () => {
      const input = 'vbscript:msgbox("XSS")';
      const expected = 'msgbox("XSS")';
      expect(service.sanitizeUserInput(input)).toBe(expected);
    });

    it('should remove event handlers', () => {
      const input = 'onclick="alert(\'XSS\')"';
      const expected = 'click="alert(\'XSS\')"';
      expect(service.sanitizeUserInput(input)).toBe(expected);
    });

    it('should trim whitespace', () => {
      const input = '  test  ';
      const expected = 'test';
      expect(service.sanitizeUserInput(input)).toBe(expected);
    });

    it('should handle safe input unchanged', () => {
      const input = 'Hello World 123';
      expect(service.sanitizeUserInput(input)).toBe(input);
    });
  });

  describe('sanitizeHtmlContent', () => {
    it('should remove script tags and return safe HTML', () => {
      const input = '<div>Hello<script>alert("XSS")</script>World</div>';
      const expected = '<div>HelloWorld</div>';
      
      sanitizer.bypassSecurityTrustHtml.and.returnValue(expected as any);
      
      const result = service.sanitizeHtmlContent(input);
      
      expect(sanitizer.bypassSecurityTrustHtml).toHaveBeenCalledWith(expected);
      expect(result).toBe(expected);
    });

    it('should handle HTML without scripts', () => {
      const input = '<div>Hello World</div>';
      
      sanitizer.bypassSecurityTrustHtml.and.returnValue(input as any);
      
      const result = service.sanitizeHtmlContent(input);
      
      expect(sanitizer.bypassSecurityTrustHtml).toHaveBeenCalledWith(input);
      expect(result).toBe(input);
    });
  });

  describe('sanitizeUrl', () => {
    it('should return safe URL for valid HTTP URL', () => {
      const input = 'https://example.com';
      const safeUrl = { toString: () => input };
      
      sanitizer.bypassSecurityTrustUrl.and.returnValue(safeUrl as any);
      
      const result = service.sanitizeUrl(input);
      
      expect(sanitizer.bypassSecurityTrustUrl).toHaveBeenCalledWith(input);
      expect(result).toBe(safeUrl);
    });

    it('should return safe URL for valid HTTPS URL', () => {
      const input = 'https://secure.example.com';
      const safeUrl = { toString: () => input };
      
      sanitizer.bypassSecurityTrustUrl.and.returnValue(safeUrl as any);
      
      const result = service.sanitizeUrl(input);
      
      expect(sanitizer.bypassSecurityTrustUrl).toHaveBeenCalledWith(input);
      expect(result).toBe(safeUrl);
    });

    it('should return about:blank for invalid URL', () => {
      const input = 'invalid-url';
      const safeUrl = { toString: () => 'about:blank' };
      
      sanitizer.bypassSecurityTrustUrl.and.returnValue(safeUrl as any);
      
      const result = service.sanitizeUrl(input);
      
      expect(sanitizer.bypassSecurityTrustUrl).toHaveBeenCalledWith('about:blank');
      expect(result).toBe(safeUrl);
    });

    it('should return about:blank for javascript protocol', () => {
      const input = 'javascript:alert("XSS")';
      const safeUrl = { toString: () => 'about:blank' };
      
      sanitizer.bypassSecurityTrustUrl.and.returnValue(safeUrl as any);
      
      const result = service.sanitizeUrl(input);
      
      expect(sanitizer.bypassSecurityTrustUrl).toHaveBeenCalledWith('about:blank');
      expect(result).toBe(safeUrl);
    });

    it('should return about:blank for null input', () => {
      const safeUrl = { toString: () => 'about:blank' };
      
      sanitizer.bypassSecurityTrustUrl.and.returnValue(safeUrl as any);
      
      const result = service.sanitizeUrl(null as any);
      
      expect(sanitizer.bypassSecurityTrustUrl).toHaveBeenCalledWith('about:blank');
      expect(result).toBe(safeUrl);
    });
  });

  describe('sanitizeStyle', () => {
    it('should remove dangerous CSS expressions', () => {
      const input = 'background: expression(alert("XSS"))';
      const expected = 'background: (alert("XSS"))';
      const safeStyle = { toString: () => expected };
      
      sanitizer.bypassSecurityTrustStyle.and.returnValue(safeStyle as any);
      
      const result = service.sanitizeStyle(input);
      
      expect(sanitizer.bypassSecurityTrustStyle).toHaveBeenCalledWith(expected);
      expect(result).toBe(safeStyle);
    });

    it('should remove javascript protocol from CSS', () => {
      const input = 'background: url(javascript:alert("XSS"))';
      const expected = 'background: url(alert("XSS"))';
      const safeStyle = { toString: () => expected };
      
      sanitizer.bypassSecurityTrustStyle.and.returnValue(safeStyle as any);
      
      const result = service.sanitizeStyle(input);
      
      expect(sanitizer.bypassSecurityTrustStyle).toHaveBeenCalledWith(expected);
      expect(result).toBe(safeStyle);
    });

    it('should return empty style for null input', () => {
      const safeStyle = { toString: () => '' };
      
      sanitizer.bypassSecurityTrustStyle.and.returnValue(safeStyle as any);
      
      const result = service.sanitizeStyle(null as any);
      
      expect(sanitizer.bypassSecurityTrustStyle).toHaveBeenCalledWith('');
      expect(result).toBe(safeStyle);
    });
  });

  describe('isValidEmail', () => {
    it('should return true for valid email', () => {
      expect(service.isValidEmail('test@example.com')).toBe(true);
      expect(service.isValidEmail('user.name@domain.co.uk')).toBe(true);
      expect(service.isValidEmail('test+tag@example.org')).toBe(true);
    });

    it('should return false for invalid email', () => {
      expect(service.isValidEmail('invalid-email')).toBe(false);
      expect(service.isValidEmail('test@')).toBe(false);
      expect(service.isValidEmail('@example.com')).toBe(false);
      expect(service.isValidEmail('test@.com')).toBe(false);
      expect(service.isValidEmail('')).toBe(false);
    });
  });

  describe('isStrongPassword', () => {
    it('should return true for strong password', () => {
      expect(service.isStrongPassword('StrongPass123!')).toBe(true);
      expect(service.isStrongPassword('MySecureP@ssw0rd')).toBe(true);
    });

    it('should return false for weak password', () => {
      expect(service.isStrongPassword('weak')).toBe(false); // too short
      expect(service.isStrongPassword('weakpassword')).toBe(false); // no uppercase, numbers, special chars
      expect(service.isStrongPassword('WEAKPASSWORD')).toBe(false); // no lowercase, numbers, special chars
      expect(service.isStrongPassword('WeakPassword')).toBe(false); // no numbers, special chars
      expect(service.isStrongPassword('WeakPass123')).toBe(false); // no special chars
    });
  });

  describe('isValidUrl', () => {
    it('should return true for valid URLs', () => {
      expect(service.isValidUrl('https://example.com')).toBe(true);
      expect(service.isValidUrl('http://localhost:3000')).toBe(true);
      expect(service.isValidUrl('https://api.example.com/v1/users')).toBe(true);
    });

    it('should return false for invalid URLs', () => {
      expect(service.isValidUrl('not-a-url')).toBe(false);
      expect(service.isValidUrl('')).toBe(false);
      expect(service.isValidUrl('ftp://example.com')).toBe(false); // not http/https
    });
  });

  describe('generateCsrfToken', () => {
    it('should generate a token with minimum length', () => {
      const token = service.generateCsrfToken();
      expect(token).toBeTruthy();
      expect(token.length).toBeGreaterThanOrEqual(20);
    });

    it('should generate different tokens on each call', () => {
      const token1 = service.generateCsrfToken();
      const token2 = service.generateCsrfToken();
      expect(token1).not.toBe(token2);
    });
  });

  describe('validateCsrfToken', () => {
    it('should return true for valid token', () => {
      const token = 'validtoken12345678901234567890';
      expect(service.validateCsrfToken(token)).toBe(true);
    });

    it('should return false for invalid token', () => {
      expect(service.validateCsrfToken('')).toBe(false);
      expect(service.validateCsrfToken('short')).toBe(false);
      expect(service.validateCsrfToken(null as any)).toBe(false);
    });
  });

  describe('escapeHtml', () => {
    it('should escape HTML entities', () => {
      expect(service.escapeHtml('<script>alert("XSS")</script>'))
        .toBe('&lt;script&gt;alert("XSS")&lt;/script&gt;');
      expect(service.escapeHtml('& < > " \''))
        .toBe('&amp; &lt; &gt; &quot; &#39;');
    });

    it('should handle safe text unchanged', () => {
      const safeText = 'Hello World 123';
      expect(service.escapeHtml(safeText)).toBe(safeText);
    });
  });

  describe('unescapeHtml', () => {
    it('should unescape HTML entities', () => {
      expect(service.unescapeHtml('&lt;script&gt;alert("XSS")&lt;/script&gt;'))
        .toBe('<script>alert("XSS")</script>');
      expect(service.unescapeHtml('&amp; &lt; &gt; &quot; &#39;'))
        .toBe('& < > " \'');
    });

    it('should handle text without entities unchanged', () => {
      const text = 'Hello World 123';
      expect(service.unescapeHtml(text)).toBe(text);
    });
  });
});
