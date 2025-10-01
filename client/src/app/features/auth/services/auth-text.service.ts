import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthTexts, getAuthTexts, formatMessage } from '../i18n/auth-texts';

@Injectable({
  providedIn: 'root'
})
export class AuthTextService {
  private readonly currentLanguage$ = new BehaviorSubject<'ru' | 'en'>('ru');
  private readonly texts$ = new BehaviorSubject<AuthTexts>(getAuthTexts('ru'));

  constructor() {
    // Listen to language changes
    this.currentLanguage$.subscribe(language => {
      this.texts$.next(getAuthTexts(language));
    });
  }

  /**
   * Get current language
   */
  getCurrentLanguage(): 'ru' | 'en' {
    return this.currentLanguage$.value;
  }

  /**
   * Set language
   */
  setLanguage(language: 'ru' | 'en'): void {
    this.currentLanguage$.next(language);
  }

  /**
   * Get texts observable
   */
  getTexts(): Observable<AuthTexts> {
    return this.texts$.asObservable();
  }

  /**
   * Get current texts
   */
  getCurrentTexts(): AuthTexts {
    return this.texts$.value;
  }

  /**
   * Format message with parameters
   */
  formatMessage(template: string, params: Record<string, string>): string {
    return formatMessage(template, params);
  }

  /**
   * Get login texts
   */
  getLoginTexts() {
    return this.getCurrentTexts().login;
  }

  /**
   * Get register texts
   */
  getRegisterTexts() {
    return this.getCurrentTexts().register;
  }

  /**
   * Get forgot password texts
   */
  getForgotPasswordTexts() {
    return this.getCurrentTexts().forgotPassword;
  }

  /**
   * Get password strength texts
   */
  getPasswordStrengthTexts() {
    return this.getCurrentTexts().passwordStrength;
  }

  /**
   * Get validation texts
   */
  getValidationTexts() {
    return this.getCurrentTexts().validation;
  }

  /**
   * Get action texts
   */
  getActionTexts() {
    return this.getCurrentTexts().actions;
  }
}
