import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SecurityService } from '../services/security.service';

@Injectable()
export class SecurityInterceptor implements HttpInterceptor {
  constructor(private securityService: SecurityService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add security headers
    request = this.addSecurityHeaders(request);
    
    // Sanitize request data
    request = this.sanitizeRequest(request);
    
    // Add CSRF token for state-changing operations
    if (this.isStateChangingRequest(request)) {
      request = this.addCsrfToken(request);
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        return this.handleSecurityError(error);
      })
    );
  }

  /**
   * Add security headers to the request
   */
  private addSecurityHeaders(request: HttpRequest<any>): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        'X-Requested-With': 'XMLHttpRequest',
        'X-Content-Type-Options': 'nosniff',
        'X-Frame-Options': 'DENY',
        'X-XSS-Protection': '1; mode=block',
        'Referrer-Policy': 'strict-origin-when-cross-origin'
      }
    });
  }

  /**
   * Sanitize request data to prevent injection attacks
   */
  private sanitizeRequest(request: HttpRequest<any>): HttpRequest<any> {
    if (request.body) {
      const sanitizedBody = this.sanitizeData(request.body);
      return request.clone({ body: sanitizedBody });
    }
    
    return request;
  }

  /**
   * Recursively sanitize data objects
   */
  private sanitizeData(data: any): any {
    if (typeof data === 'string') {
      return this.securityService.sanitizeUserInput(data);
    }
    
    if (Array.isArray(data)) {
      return data.map(item => this.sanitizeData(item));
    }
    
    if (typeof data === 'object' && data !== null) {
      const sanitized: any = {};
      for (const [key, value] of Object.entries(data)) {
        sanitized[key] = this.sanitizeData(value);
      }
      return sanitized;
    }
    
    return data;
  }

  /**
   * Check if request is state-changing (POST, PUT, DELETE, PATCH)
   */
  private isStateChangingRequest(request: HttpRequest<any>): boolean {
    const stateChangingMethods = ['POST', 'PUT', 'DELETE', 'PATCH'];
    return stateChangingMethods.includes(request.method);
  }

  /**
   * Add CSRF token to state-changing requests
   */
  private addCsrfToken(request: HttpRequest<any>): HttpRequest<any> {
    const csrfToken = this.getCsrfToken();
    if (csrfToken) {
      return request.clone({
        setHeaders: {
          'X-CSRF-TOKEN': csrfToken
        }
      });
    }
    
    return request;
  }

  /**
   * Get CSRF token from meta tag or generate new one
   */
  private getCsrfToken(): string | null {
    // Try to get from meta tag first
    const metaTag = document.querySelector('meta[name="csrf-token"]');
    if (metaTag) {
      return metaTag.getAttribute('content');
    }
    
    // Generate new token if not available
    return this.securityService.generateCsrfToken();
  }

  /**
   * Handle security-related errors
   */
  private handleSecurityError(error: HttpErrorResponse): Observable<never> {
    switch (error.status) {
      case 403:
        console.error('Access forbidden - possible CSRF attack or insufficient permissions');
        break;
      case 401:
        console.error('Unauthorized - authentication required');
        break;
      case 400:
        if (error.error?.message?.includes('CSRF')) {
          console.error('CSRF token validation failed');
        }
        break;
      case 413:
        console.error('Request entity too large - possible DoS attempt');
        break;
      case 429:
        console.error('Too many requests - rate limiting in effect');
        break;
    }
    
    return throwError(() => error);
  }
}
