// ========================================
// PASSWORD UTILITIES
// ========================================

export interface PasswordStrength {
  score: number; // 0-100
  level: 'weak' | 'medium' | 'strong';
  text: string;
  percentage: number; // 0-100
}

export interface PasswordValidation {
  isValid: boolean;
  errors: string[];
  strength: PasswordStrength;
}

/**
 * Calculate password strength
 */
export function calculatePasswordStrength(password: string): PasswordStrength {
  if (!password) {
    return {
      score: 0,
      level: 'weak',
      text: 'weak',
      percentage: 0
    };
  }

  let score = 0;

  // Length check
  if (password.length >= 8) {
    score += 25;
  }

  // Lowercase check
  if (/[a-z]/.test(password)) {
    score += 25;
  }

  // Uppercase check
  if (/[A-Z]/.test(password)) {
    score += 15;
  }

  // Number check
  if (/\d/.test(password)) {
    score += 25;
  }

  // Special character check
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
    score += 10;
  }

  // Determine level
  let level: 'weak' | 'medium' | 'strong';
  let text: string;
  let percentage: number;

  if (score < 50) {
    level = 'weak';
    text = 'Слабый';
    percentage = 33;
  } else if (score < 80) {
    level = 'medium';
    text = 'Средний';
    percentage = 66;
  } else {
    level = 'strong';
    text = 'Сильный';
    percentage = 100;
  }

  return {
    score,
    level,
    text,
    percentage
  };
}

/**
 * Validate password
 */
export function validatePassword(password: string): PasswordValidation {
  const strength = calculatePasswordStrength(password);
  const errors: string[] = [];

  if (password.length < 8) {
    errors.push('Минимум 8 символов');
  }

  if (!/[a-z]/.test(password)) {
    errors.push('Добавьте строчные буквы');
  }

  if (!/\d/.test(password)) {
    errors.push('Добавьте цифры');
  }

  return {
    isValid: errors.length === 0,
    errors,
    strength
  };
}

/**
 * Check if passwords match
 */
export function passwordsMatch(password: string, confirmPassword: string): boolean {
  return password === confirmPassword;
}

/**
 * Get password strength class
 */
export function getPasswordStrengthClass(strength: PasswordStrength): string {
  return `strength-${strength.level}`;
}

/**
 * Get password strength percentage
 */
export function getPasswordStrengthPercentage(strength: PasswordStrength): number {
  return strength.percentage;
}

/**
 * Get password strength text
 */
export function getPasswordStrengthText(strength: PasswordStrength): string {
  return strength.text;
}
