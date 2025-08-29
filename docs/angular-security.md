# Angular Security Best Practices

## Overview

This document outlines security best practices for Angular applications, covering input validation, XSS protection, authentication strategies, and advanced security patterns.

## Table of Contents

1. [Input Validation](#input-validation)
2. [XSS Protection](#xss-protection)
3. [Authentication Strategies](#authentication-strategies)
4. [Advanced Security Patterns](#advanced-security-patterns)
5. [Security Headers](#security-headers)
6. [Content Security Policy](#content-security-policy)
7. [HTTPS and SSL](#https-and-ssl)
8. [Dependency Security](#dependency-security)
9. [Code Examples](#code-examples)

## Input Validation

### Client-Side Validation

Angular provides robust client-side validation through Reactive Forms and Template-driven Forms.

#### Reactive Forms Validation

```typescript
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

export class UserRegistrationComponent {
  registrationForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.registrationForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/)
      ]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password?.value !== confirmPassword?.value) {
      confirmPassword?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    return null;
  }
}
```

#### Custom Validators

```typescript
import { AbstractControl, ValidationErrors } from '@angular/forms';

export class SecurityValidators {
  static noSpecialCharacters(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const specialCharRegex = /[<>\"'&]/;
    if (specialCharRegex.test(value)) {
      return { noSpecialCharacters: true };
    }
    
    return null;
  }

  static securePassword(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumbers = /\d/.test(value);
    const hasSpecialChar = /[@$!%*?&]/.test(value);
    
    const valid = hasUpperCase && hasLowerCase && hasNumbers && hasSpecialChar;
    
    return valid ? null : { securePassword: true };
  }

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
}
```

### Server-Side Validation

Always validate data on the server side as well. Client-side validation is for UX, server-side validation is for security.

```typescript
// Backend validation example (Spring Boot)
@PostMapping("/register")
public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationRequest request) {
    // Server-side validation using Bean Validation
    User user = userService.createUser(request);
    return ResponseEntity.ok(user);
}

// UserRegistrationRequest with validation annotations
public class UserRegistrationRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]", 
             message = "Password must contain uppercase, lowercase, number and special character")
    private String password;
}
```

## XSS Protection

### Angular's Built-in XSS Protection

Angular automatically escapes content to prevent XSS attacks.

#### Safe Content Binding

```typescript
import { Component } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-content-display',
  template: `
    <div [innerHTML]="safeContent"></div>
    <div>{{ userInput }}</div> <!-- Automatically escaped -->
  `
})
export class ContentDisplayComponent {
  userInput = '<script>alert("XSS")</script>';
  safeContent: SafeHtml;

  constructor(private sanitizer: DomSanitizer) {
    // Only use when you trust the content
    this.safeContent = this.sanitizer.bypassSecurityTrustHtml(this.userInput);
  }
}
```

#### Content Sanitization Service

```typescript
import { Injectable } from '@angular/core';
import { DomSanitizer, SafeHtml, SafeUrl, SafeStyle } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class SecurityService {
  constructor(private sanitizer: DomSanitizer) {}

  sanitizeHtml(content: string): SafeHtml {
    // Remove potentially dangerous content
    const cleanContent = this.removeScriptTags(content);
    return this.sanitizer.bypassSecurityTrustHtml(cleanContent);
  }

  sanitizeUrl(url: string): SafeUrl {
    // Validate URL format
    if (this.isValidUrl(url)) {
      return this.sanitizer.bypassSecurityTrustUrl(url);
    }
    return this.sanitizer.bypassSecurityTrustUrl('about:blank');
  }

  sanitizeStyle(style: string): SafeStyle {
    // Remove dangerous CSS
    const cleanStyle = this.removeDangerousCSS(style);
    return this.sanitizer.bypassSecurityTrustStyle(cleanStyle);
  }

  private removeScriptTags(content: string): string {
    return content.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
  }

  private isValidUrl(url: string): boolean {
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  }

  private removeDangerousCSS(style: string): string {
    // Remove dangerous CSS properties
    return style.replace(/expression\(/gi, '')
                .replace(/javascript:/gi, '')
                .replace(/vbscript:/gi, '');
  }
}
```

### Template Security

```html
<!-- Good: Angular automatically escapes -->
<div>{{ userInput }}</div>

<!-- Good: Safe when content is trusted -->
<div [innerHTML]="safeContent"></div>

<!-- Bad: Never use innerHTML with untrusted content -->
<div [innerHTML]="userInput"></div>

<!-- Good: Use property binding for attributes -->
<img [src]="imageUrl" [alt]="imageAlt">

<!-- Bad: Avoid string interpolation for attributes -->
<img src="{{ imageUrl }}" alt="{{ imageAlt }}">
```

## Authentication Strategies

### JWT Token Management

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;
  private tokenKey = 'auth_token';
  private refreshTokenKey = 'refresh_token';

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User | null>(
      this.getUserFromToken()
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', { email, password })
      .pipe(map(response => {
        this.storeTokens(response.token, response.refreshToken);
        this.currentUserSubject.next(response.user);
        return response;
      }));
  }

  logout(): void {
    this.removeTokens();
    this.currentUserSubject.next(null);
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    return this.http.post<AuthResponse>('/api/auth/refresh', { refreshToken })
      .pipe(map(response => {
        this.storeTokens(response.token, response.refreshToken);
        return response;
      }));
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      const payload = this.decodeToken(token);
      return payload.exp > Date.now() / 1000;
    } catch {
      return false;
    }
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  private storeTokens(token: string, refreshToken: string): void {
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.refreshTokenKey, refreshToken);
  }

  private removeTokens(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshTokenKey);
  }

  private getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private getRefreshToken(): string | null {
    return localStorage.getItem(this.refreshTokenKey);
  }

  private decodeToken(token: string): any {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(window.atob(base64));
  }

  private getUserFromToken(): User | null {
    const token = this.getToken();
    if (!token) return null;
    
    try {
      const payload = this.decodeToken(token);
      return payload.user;
    } catch {
      return null;
    }
  }
}
```

### Route Guards

```typescript
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }

    // Store the attempted URL for redirecting
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const requiredRole = route.data['role'];
    const user = this.authService.currentUserValue;
    
    if (user && user.roles.includes(requiredRole)) {
      return true;
    }

    this.router.navigate(['/unauthorized']);
    return false;
  }
}
```

## Advanced Security Patterns

### HTTP Interceptors

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add auth header if user is logged in
    if (this.authService.isAuthenticated()) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${this.authService.getToken()}`
        }
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !request.url.includes('auth/refresh')) {
          return this.handle401Error(request, next);
        }
        return throwError(() => error);
      })
    );
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;

      return this.authService.refreshToken().pipe(
        switchMap((response) => {
          this.isRefreshing = false;
          request = request.clone({
            setHeaders: {
              Authorization: `Bearer ${response.token}`
            }
          });
          return next.handle(request);
        }),
        catchError((error) => {
          this.isRefreshing = false;
          this.authService.logout();
          this.router.navigate(['/login']);
          return throwError(() => error);
        })
      );
    }

    return next.handle(request);
  }
}
```

### CSRF Protection

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class CsrfInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add CSRF token for state-changing operations
    if (request.method !== 'GET' && request.method !== 'HEAD') {
      const csrfToken = this.getCsrfToken();
      if (csrfToken) {
        request = request.clone({
          setHeaders: {
            'X-CSRF-TOKEN': csrfToken
          }
        });
      }
    }

    return next.handle(request);
  }

  private getCsrfToken(): string | null {
    // Get CSRF token from meta tag or cookie
    const metaTag = document.querySelector('meta[name="csrf-token"]');
    return metaTag ? metaTag.getAttribute('content') : null;
  }
}
```

### Input Sanitization Service

```typescript
import { Injectable } from '@angular/core';
import { DomSanitizer, SafeHtml, SafeUrl, SafeStyle } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class InputSanitizationService {
  constructor(private sanitizer: DomSanitizer) {}

  sanitizeUserInput(input: string): string {
    if (!input) return input;

    return input
      .replace(/[<>\"'&]/g, '') // Remove dangerous characters
      .replace(/javascript:/gi, '') // Remove javascript: protocol
      .replace(/vbscript:/gi, '') // Remove vbscript: protocol
      .replace(/on\w+=/gi, '') // Remove event handlers
      .trim();
  }

  sanitizeHtmlContent(content: string): SafeHtml {
    const sanitized = this.sanitizeUserInput(content);
    return this.sanitizer.bypassSecurityTrustHtml(sanitized);
  }

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
}
```

## Security Headers

### Angular Configuration

```typescript
// angular.json
{
  "projects": {
    "your-app": {
      "architect": {
        "build": {
          "options": {
            "assets": [
              {
                "glob": "**/*",
                "input": "src/assets",
                "output": "/assets/"
              }
            ],
            "headers": {
              "X-Content-Type-Options": "nosniff",
              "X-Frame-Options": "DENY",
              "X-XSS-Protection": "1; mode=block",
              "Referrer-Policy": "strict-origin-when-cross-origin",
              "Permissions-Policy": "geolocation=(), microphone=(), camera=()"
            }
          }
        }
      }
    }
  }
}
```

### Server-Side Headers (Express.js example)

```javascript
const helmet = require('helmet');
const express = require('express');

const app = express();

// Security headers
app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      styleSrc: ["'self'", "'unsafe-inline'"],
      scriptSrc: ["'self'"],
      imgSrc: ["'self'", "data:", "https:"],
      connectSrc: ["'self'"],
      fontSrc: ["'self'"],
      objectSrc: ["'none'"],
      mediaSrc: ["'self'"],
      frameSrc: ["'none'"]
    }
  },
  hsts: {
    maxAge: 31536000,
    includeSubDomains: true,
    preload: true
  }
}));

// Additional security headers
app.use((req, res, next) => {
  res.setHeader('X-Content-Type-Options', 'nosniff');
  res.setHeader('X-Frame-Options', 'DENY');
  res.setHeader('X-XSS-Protection', '1; mode=block');
  res.setHeader('Referrer-Policy', 'strict-origin-when-cross-origin');
  res.setHeader('Permissions-Policy', 'geolocation=(), microphone=(), camera=()');
  next();
});
```

## Content Security Policy

### CSP Configuration

```html
<!-- index.html -->
<meta http-equiv="Content-Security-Policy" 
      content="default-src 'self'; 
               script-src 'self' 'unsafe-inline' 'unsafe-eval'; 
               style-src 'self' 'unsafe-inline'; 
               img-src 'self' data: https:; 
               font-src 'self'; 
               connect-src 'self'; 
               object-src 'none'; 
               media-src 'self'; 
               frame-src 'none';">
```

### Dynamic CSP

```typescript
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CspService {
  setCspPolicy(policy: string): void {
    const meta = document.createElement('meta');
    meta.httpEquiv = 'Content-Security-Policy';
    meta.content = policy;
    document.head.appendChild(meta);
  }

  addCspNonce(nonce: string): void {
    const script = document.createElement('script');
    script.nonce = nonce;
    // Add script content
  }
}
```

## HTTPS and SSL

### Angular CLI HTTPS

```bash
# Development with HTTPS
ng serve --ssl true

# Production build
ng build --prod
```

### SSL Configuration

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'https://localhost:3000/api',
  ssl: {
    enabled: true,
    cert: './ssl/cert.pem',
    key: './ssl/key.pem'
  }
};
```

## Dependency Security

### Security Audits

```bash
# Check for vulnerabilities
npm audit

# Fix vulnerabilities
npm audit fix

# Update dependencies
npm update

# Check outdated packages
npm outdated
```

### Package.json Security Scripts

```json
{
  "scripts": {
    "security:audit": "npm audit",
    "security:fix": "npm audit fix",
    "security:check": "npm audit --audit-level=moderate",
    "deps:update": "npm update",
    "deps:outdated": "npm outdated"
  }
}
```

## Code Examples

### Secure Form Component

```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SecurityValidators } from './security-validators';
import { InputSanitizationService } from './input-sanitization.service';

@Component({
  selector: 'app-secure-form',
  template: `
    <form [formGroup]="secureForm" (ngSubmit)="onSubmit()">
      <div>
        <label for="name">Name:</label>
        <input id="name" type="text" formControlName="name">
        <div *ngIf="secureForm.get('name')?.errors?.noSpecialCharacters">
          Special characters are not allowed
        </div>
      </div>
      
      <div>
        <label for="email">Email:</label>
        <input id="email" type="email" formControlName="email">
      </div>
      
      <div>
        <label for="password">Password:</label>
        <input id="password" type="password" formControlName="password">
        <div *ngIf="secureForm.get('password')?.errors?.securePassword">
          Password must be secure
        </div>
      </div>
      
      <button type="submit" [disabled]="!secureForm.valid">Submit</button>
    </form>
  `
})
export class SecureFormComponent implements OnInit {
  secureForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private sanitizationService: InputSanitizationService
  ) {
    this.secureForm = this.fb.group({
      name: ['', [
        Validators.required,
        SecurityValidators.noSpecialCharacters,
        SecurityValidators.sanitizeInput
      ]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        SecurityValidators.securePassword
      ]]
    });
  }

  ngOnInit(): void {
    // Monitor form changes for additional sanitization
    this.secureForm.valueChanges.subscribe(values => {
      Object.keys(values).forEach(key => {
        const control = this.secureForm.get(key);
        if (control && typeof values[key] === 'string') {
          const sanitized = this.sanitizationService.sanitizeUserInput(values[key]);
          if (sanitized !== values[key]) {
            control.setValue(sanitized, { emitEvent: false });
          }
        }
      });
    });
  }

  onSubmit(): void {
    if (this.secureForm.valid) {
      const formData = this.secureForm.value;
      
      // Additional server-side validation will be performed
      console.log('Form submitted:', formData);
    }
  }
}
```

### Secure API Service

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class SecureApiService {
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  get<T>(url: string, params?: any): Observable<T> {
    const httpParams = this.sanitizeParams(params);
    const options = {
      headers: this.getSecureHeaders(),
      params: httpParams
    };

    return this.http.get<T>(url, options);
  }

  post<T>(url: string, data: any): Observable<T> {
    const sanitizedData = this.sanitizeData(data);
    const options = {
      headers: this.getSecureHeaders()
    };

    return this.http.post<T>(url, sanitizedData, options);
  }

  put<T>(url: string, data: any): Observable<T> {
    const sanitizedData = this.sanitizeData(data);
    const options = {
      headers: this.getSecureHeaders()
    };

    return this.http.put<T>(url, sanitizedData, options);
  }

  delete<T>(url: string): Observable<T> {
    const options = {
      headers: this.getSecureHeaders()
    };

    return this.http.delete<T>(url, options);
  }

  private getSecureHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-Requested-With': 'XMLHttpRequest'
    });

    if (this.authService.isAuthenticated()) {
      headers = headers.set('Authorization', `Bearer ${this.authService.getToken()}`);
    }

    return headers;
  }

  private sanitizeParams(params: any): HttpParams {
    let httpParams = new HttpParams();
    
    if (params) {
      Object.keys(params).forEach(key => {
        const value = params[key];
        if (value !== null && value !== undefined) {
          // Sanitize parameter values
          const sanitizedValue = this.sanitizeValue(value);
          httpParams = httpParams.set(key, sanitizedValue);
        }
      });
    }

    return httpParams;
  }

  private sanitizeData(data: any): any {
    if (typeof data === 'string') {
      return this.sanitizeValue(data);
    }

    if (typeof data === 'object' && data !== null) {
      const sanitized = {};
      Object.keys(data).forEach(key => {
        sanitized[key] = this.sanitizeData(data[key]);
      });
      return sanitized;
    }

    return data;
  }

  private sanitizeValue(value: any): string {
    if (typeof value !== 'string') {
      return String(value);
    }

    return value
      .replace(/[<>\"'&]/g, '')
      .replace(/javascript:/gi, '')
      .replace(/vbscript:/gi, '')
      .trim();
  }
}
```

## Best Practices Summary

1. **Always validate input** on both client and server side
2. **Use Angular's built-in XSS protection** - avoid bypassing unless necessary
3. **Implement proper authentication** with JWT tokens and refresh mechanisms
4. **Use HTTPS** in production
5. **Set security headers** to protect against common attacks
6. **Implement Content Security Policy** to prevent XSS and injection attacks
7. **Regularly audit dependencies** for security vulnerabilities
8. **Sanitize all user input** before processing or displaying
9. **Use route guards** to protect sensitive routes
10. **Implement proper error handling** without exposing sensitive information

## Security Checklist

- [ ] Input validation implemented
- [ ] XSS protection enabled
- [ ] CSRF protection configured
- [ ] Authentication system in place
- [ ] Route guards implemented
- [ ] HTTPS enabled
- [ ] Security headers set
- [ ] CSP configured
- [ ] Dependencies audited
- [ ] Error handling secure
- [ ] Logging configured
- [ ] Rate limiting implemented
- [ ] Input sanitization active
- [ ] Output encoding applied
- [ ] Session management secure

## Resources

- [Angular Security Guide](https://angular.io/guide/security)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Content Security Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)
- [JWT Security Best Practices](https://auth0.com/blog/a-look-at-the-latest-draft-for-jwt-bcp/)
- [Angular Security Best Practices](https://angular.io/guide/security#angular-security-best-practices)
