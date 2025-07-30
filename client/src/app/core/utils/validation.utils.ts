export class ValidationUtils {
  static isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  static isValidPassword(password: string): boolean {
    // Минимум 8 символов, хотя бы одна буква и цифра
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,}$/;
    return passwordRegex.test(password);
  }

  static isValidUsername(username: string): boolean {
    // 3-20 символов, только буквы, цифры и подчеркивания
    const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/;
    return usernameRegex.test(username);
  }

  static getPasswordStrength(password: string): 'weak' | 'medium' | 'strong' {
    if (password.length < 6) return 'weak';
    if (password.length < 10) return 'medium';
    return 'strong';
  }

  static validateTaskTitle(title: string): string | null {
    if (!title || title.trim().length === 0) {
      return 'Название задачи обязательно';
    }
    if (title.trim().length < 3) {
      return 'Название должно содержать минимум 3 символа';
    }
    if (title.trim().length > 100) {
      return 'Название не должно превышать 100 символов';
    }
    return null;
  }

  static validateProjectName(name: string): string | null {
    if (!name || name.trim().length === 0) {
      return 'Название проекта обязательно';
    }
    if (name.trim().length < 2) {
      return 'Название должно содержать минимум 2 символа';
    }
    if (name.trim().length > 50) {
      return 'Название не должно превышать 50 символов';
    }
    return null;
  }
} 