import { Injectable } from '@angular/core';
import { DomSanitizer, SafeHtml, SafeUrl, SafeStyle } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class SecurityService {
  constructor(private readonly sanitizer: DomSanitizer) {}

  /**
   * Sanitize user input to prevent XSS attacks
   */
  sanitizeUserInput(input: string): string {
    if (!input) return input;

    return input
      .replace(/[<>\"'&]/g, '') // Remove dangerous characters
      .replace(/javascript:/gi, '') // Remove javascript: protocol
      .replace(/vbscript:/gi, '') // Remove vbscript: protocol
      .replace(/on\w+=/gi, '') // Remove event handlers
      .trim();
  }

  /**
   * Sanitize HTML content
   */
  sanitizeHtmlContent(content: string): SafeHtml {
    const sanitized = this.removeScriptTags(content);
    return this.sanitizer.bypassSecurityTrustHtml(sanitized);
  }

  /**
   * Sanitize URL
   */
  sanitizeUrl(url: string): SafeUrl {
    if (!url) return this.sanitizer.bypassSecurityTrustUrl('about:blank');

    try {
      const urlObj = new URL(url);
      // Only allow http and https protocols
      if (urlObj.protocol === 'http:' || urlObj.protocol === 'https:') {
        return this.sanitizer.bypassSecurityTrustUrl(url);
      }
    } catch {
      // Invalid URL
    }

    return this.sanitizer.bypassSecurityTrustUrl('about:blank');
  }

  /**
   * Sanitize CSS styles
   */
  sanitizeStyle(style: string): SafeStyle {
    if (!style) return this.sanitizer.bypassSecurityTrustStyle('');

    const sanitized = style
      .replace(/expression\(/gi, '') // Remove CSS expressions
      .replace(/javascript:/gi, '') // Remove javascript: protocol
      .replace(/vbscript:/gi, '') // Remove vbscript: protocol
      .replace(/url\(javascript:/gi, 'url(') // Remove javascript: in URLs
      .replace(/url\(vbscript:/gi, 'url('); // Remove vbscript: in URLs

    return this.sanitizer.bypassSecurityTrustStyle(sanitized);
  }

  /**
   * Validate email format
   */
  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Validate password strength
   */
  isStrongPassword(password: string): boolean {
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumbers = /\d/.test(password);
    const hasSpecialChar = /[@$!%*?&]/.test(password);
    const isLongEnough = password.length >= 8;

    return hasUpperCase && hasLowerCase && hasNumbers && hasSpecialChar && isLongEnough;
  }

  /**
   * Validate URL format
   */
  isValidUrl(url: string): boolean {
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Remove script tags from content
   */
  private removeScriptTags(content: string): string {
    return content.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
  }

  /**
   * Generate CSRF token
   */
  generateCsrfToken(): string {
    return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
  }

  /**
   * Validate CSRF token
   */
  validateCsrfToken(token: string): boolean {
    return !!(token && token.length >= 20);
  }

  /**
   * Escape HTML entities
   */
  escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  /**
   * Unescape HTML entities
   */
  unescapeHtml(text: string): string {
    const div = document.createElement('div');
    div.innerHTML = text;
    return div.textContent || div.innerText || '';
  }
}
