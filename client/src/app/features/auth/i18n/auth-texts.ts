// ========================================
// AUTHENTICATION TEXTS - RU/EN
// ========================================

export interface AuthTexts {
  // Common
  logo: string;
  
  // Login
  login: {
    title: string;
    subtitle: string;
    emailLabel: string;
    emailPlaceholder: string;
    passwordLabel: string;
    passwordPlaceholder: string;
    rememberMe: string;
    forgotPassword: string;
    submitButton: string;
    oauthButton: string;
    footerText: string;
    footerLink: string;
  };
  
  // Register
  register: {
    title: string;
    subtitle: string;
    usernameLabel: string;
    usernamePlaceholder: string;
    emailLabel: string;
    emailPlaceholder: string;
    passwordLabel: string;
    passwordPlaceholder: string;
    confirmPasswordLabel: string;
    confirmPasswordPlaceholder: string;
    termsText: string;
    termsLink: string;
    privacyLink: string;
    submitButton: string;
    oauthButton: string;
    footerText: string;
    footerLink: string;
  };
  
  // Forgot Password
  forgotPassword: {
    title: string;
    subtitle: string;
    emailLabel: string;
    emailPlaceholder: string;
    submitButton: string;
    successMessage: string;
    resendButton: string;
    footerText: string;
    footerLink: string;
  };
  
  // Password Strength
  passwordStrength: {
    weak: string;
    medium: string;
    strong: string;
    hint: string;
  };
  
  // Validation Messages
  validation: {
    required: string;
    email: string;
    minLength: string;
    passwordMismatch: string;
    termsRequired: string;
    usernameLength: string;
  };
  
  // Common Actions
  actions: {
    loading: string;
    submit: string;
    cancel: string;
    back: string;
  };
}

// ========================================
// RUSSIAN TEXTS
// ========================================

export const authTextsRu: AuthTexts = {
  logo: 'TaskBoard Pro',
  
  login: {
    title: 'Войти в TaskBoard Pro',
    subtitle: 'Рады видеть вас снова 👋',
    emailLabel: 'Email',
    emailPlaceholder: 'Введите ваш email',
    passwordLabel: 'Пароль',
    passwordPlaceholder: 'Введите ваш пароль',
    rememberMe: 'Запомнить меня',
    forgotPassword: 'Забыли пароль?',
    submitButton: 'Войти',
    oauthButton: 'Войти через GitHub',
    footerText: 'Нет аккаунта?',
    footerLink: 'Зарегистрироваться'
  },
  
  register: {
    title: 'Создать аккаунт',
    subtitle: 'Быстрый старт — 30 секунд',
    usernameLabel: 'Имя пользователя',
    usernamePlaceholder: 'Введите имя пользователя',
    emailLabel: 'Email',
    emailPlaceholder: 'Введите ваш email',
    passwordLabel: 'Пароль',
    passwordPlaceholder: 'Создайте пароль',
    confirmPasswordLabel: 'Подтвердите пароль',
    confirmPasswordPlaceholder: 'Повторите пароль',
    termsText: 'Я согласен с',
    termsLink: 'условиями использования',
    privacyLink: 'политикой конфиденциальности',
    submitButton: 'Создать аккаунт',
    oauthButton: 'Зарегистрироваться через GitHub',
    footerText: 'Уже есть аккаунт?',
    footerLink: 'Войти'
  },
  
  forgotPassword: {
    title: 'Восстановление пароля',
    subtitle: 'Введите email, мы пришлём ссылку для сброса',
    emailLabel: 'Email',
    emailPlaceholder: 'Введите ваш email',
    submitButton: 'Отправить ссылку',
    successMessage: 'Мы отправили письмо на {email}. Проверьте входящие и спам.',
    resendButton: 'Отправить еще раз',
    footerText: 'Вернуться к входу',
    footerLink: 'Войти'
  },
  
  passwordStrength: {
    weak: 'Слабый',
    medium: 'Средний',
    strong: 'Сильный',
    hint: 'Минимум 8 символов, буква и цифра'
  },
  
  validation: {
    required: 'Поле обязательно для заполнения',
    email: 'Введите корректный email',
    minLength: 'Минимум {min} символов',
    passwordMismatch: 'Пароли не совпадают',
    termsRequired: 'Необходимо согласиться с условиями',
    usernameLength: 'Имя должно быть от 2 до 40 символов'
  },
  
  actions: {
    loading: 'Загрузка...',
    submit: 'Отправить',
    cancel: 'Отмена',
    back: 'Назад'
  }
};

// ========================================
// ENGLISH TEXTS
// ========================================

export const authTextsEn: AuthTexts = {
  logo: 'TaskBoard Pro',
  
  login: {
    title: 'Log in to TaskBoard Pro',
    subtitle: 'Good to see you back 👋',
    emailLabel: 'Email',
    emailPlaceholder: 'Enter your email',
    passwordLabel: 'Password',
    passwordPlaceholder: 'Enter your password',
    rememberMe: 'Remember me',
    forgotPassword: 'Forgot password?',
    submitButton: 'Log in',
    oauthButton: 'Continue with GitHub',
    footerText: 'Don\'t have an account?',
    footerLink: 'Sign up'
  },
  
  register: {
    title: 'Create Account',
    subtitle: 'Get started in 30 seconds',
    usernameLabel: 'Username',
    usernamePlaceholder: 'Enter username',
    emailLabel: 'Email',
    emailPlaceholder: 'Enter your email',
    passwordLabel: 'Password',
    passwordPlaceholder: 'Create password',
    confirmPasswordLabel: 'Confirm Password',
    confirmPasswordPlaceholder: 'Repeat password',
    termsText: 'I agree to the',
    termsLink: 'Terms of Service',
    privacyLink: 'Privacy Policy',
    submitButton: 'Create Account',
    oauthButton: 'Continue with GitHub',
    footerText: 'Already have an account?',
    footerLink: 'Log in'
  },
  
  forgotPassword: {
    title: 'Password Recovery',
    subtitle: 'Enter your email and we\'ll send you a reset link',
    emailLabel: 'Email',
    emailPlaceholder: 'Enter your email',
    submitButton: 'Send reset link',
    successMessage: 'We\'ve emailed you a reset link to {email}. Check your inbox and spam.',
    resendButton: 'Send again',
    footerText: 'Back to login',
    footerLink: 'Log in'
  },
  
  passwordStrength: {
    weak: 'Weak',
    medium: 'Medium',
    strong: 'Strong',
    hint: 'Minimum 8 characters, letter and number'
  },
  
  validation: {
    required: 'This field is required',
    email: 'Please enter a valid email',
    minLength: 'Minimum {min} characters',
    passwordMismatch: 'Passwords do not match',
    termsRequired: 'You must agree to the terms',
    usernameLength: 'Username must be 2-40 characters'
  },
  
  actions: {
    loading: 'Loading...',
    submit: 'Submit',
    cancel: 'Cancel',
    back: 'Back'
  }
};

// ========================================
// TEXT UTILITIES
// ========================================

export function getAuthTexts(language: 'ru' | 'en' = 'ru'): AuthTexts {
  return language === 'en' ? authTextsEn : authTextsRu;
}

export function formatMessage(template: string, params: Record<string, string>): string {
  return template.replace(/\{(\w+)\}/g, (match, key) => params[key] || match);
}



