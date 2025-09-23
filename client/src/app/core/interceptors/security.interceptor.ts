import { HttpInterceptorFn, HttpRequest, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { inject } from '@angular/core';
import { SecurityService } from '../services/security.service';

export const securityInterceptor: HttpInterceptorFn = (
  request: HttpRequest<any>,
  next: (request: HttpRequest<any>) => Observable<HttpEvent<any>>
): Observable<HttpEvent<any>> => {
  const securityService = inject(SecurityService);

  // Add security headers
  request = addSecurityHeaders(request);
  
  // Sanitize request data
  request = sanitizeRequest(request, securityService);
  
  // Add CSRF token for state-changing operations
  if (isStateChangingRequest(request)) {
    request = addCsrfToken(request, securityService);
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      return handleSecurityError(error);
    })
  );
};

/**
 * Add security headers to the request
 */
function addSecurityHeaders(request: HttpRequest<any>): HttpRequest<any> {
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
function sanitizeRequest(request: HttpRequest<any>, securityService: SecurityService): HttpRequest<any> {
  if (request.body) {
    const sanitizedBody = sanitizeData(request.body, securityService);
    return request.clone({ body: sanitizedBody });
  }
  
  return request;
}

/**
 * Recursively sanitize data objects
 */
function sanitizeData(data: any, securityService: SecurityService): any {
  if (typeof data === 'string') {
    return securityService.sanitizeUserInput(data);
  }
  
  if (Array.isArray(data)) {
    return data.map(item => sanitizeData(item, securityService));
  }
  
  if (typeof data === 'object' && data !== null) {
    const sanitized: any = {};
    for (const [key, value] of Object.entries(data)) {
      sanitized[key] = sanitizeData(value, securityService);
    }
    return sanitized;
  }
  
  return data;
}

/**
 * Check if request is state-changing (POST, PUT, DELETE, PATCH)
 */
function isStateChangingRequest(request: HttpRequest<any>): boolean {
  const stateChangingMethods = ['POST', 'PUT', 'DELETE', 'PATCH'];
  return stateChangingMethods.includes(request.method);
}

/**
 * Add CSRF token to state-changing requests
 */
function addCsrfToken(request: HttpRequest<any>, securityService: SecurityService): HttpRequest<any> {
  const csrfToken = getCsrfToken(securityService);
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
function getCsrfToken(securityService: SecurityService): string | null {
  // Try to get from meta tag first
  const metaTag = document.querySelector('meta[name="csrf-token"]');
  if (metaTag) {
    return metaTag.getAttribute('content');
  }
  
  // Generate new token if not available
  return securityService.generateCsrfToken();
}

/**
 * Handle security-related errors
 */
function handleSecurityError(error: HttpErrorResponse): Observable<never> {
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
