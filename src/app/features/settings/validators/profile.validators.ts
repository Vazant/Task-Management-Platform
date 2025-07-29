import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class ProfileValidators {
  // Валидатор для username
  static username(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return { required: 'Имя пользователя обязательно' };
      }

      if (value.length < 3) {
        return { minlength: 'Имя пользователя должно содержать минимум 3 символа' };
      }

      if (value.length > 20) {
        return { maxlength: 'Имя пользователя не должно превышать 20 символов' };
      }

      // Только буквы, цифры и подчеркивания
      const usernameRegex = /^[a-zA-Z0-9_]+$/;
      if (!usernameRegex.test(value)) {
        return { pattern: 'Имя пользователя может содержать только буквы, цифры и подчеркивания' };
      }

      return null;
    };
  }

  // Валидатор для email
  static email(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return { required: 'Email обязателен' };
      }

      const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      if (!emailRegex.test(value)) {
        return { email: 'Введите корректный email адрес' };
      }

      return null;
    };
  }

  // Валидатор для имени
  static firstName(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return null; // Имя не обязательно
      }

      if (value.length < 2) {
        return { minlength: 'Имя должно содержать минимум 2 символа' };
      }

      if (value.length > 50) {
        return { maxlength: 'Имя не должно превышать 50 символов' };
      }

      // Только буквы, пробелы и дефисы
      const nameRegex = /^[a-zA-Zа-яА-Я\s-]+$/;
      if (!nameRegex.test(value)) {
        return { pattern: 'Имя может содержать только буквы, пробелы и дефисы' };
      }

      return null;
    };
  }

  // Валидатор для фамилии
  static lastName(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return null; // Фамилия не обязательна
      }

      if (value.length < 2) {
        return { minlength: 'Фамилия должна содержать минимум 2 символа' };
      }

      if (value.length > 50) {
        return { maxlength: 'Фамилия не должна превышать 50 символов' };
      }

      // Только буквы, пробелы и дефисы
      const nameRegex = /^[a-zA-Zа-яА-Я\s-]+$/;
      if (!nameRegex.test(value)) {
        return { pattern: 'Фамилия может содержать только буквы, пробелы и дефисы' };
      }

      return null;
    };
  }

  // Общий валидатор для имени и фамилии
  static name(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return null; // Не обязательно
      }

      if (value.length < 2) {
        return { minlength: 'Минимум 2 символа' };
      }

      if (value.length > 50) {
        return { maxlength: 'Максимум 50 символов' };
      }

      // Только буквы, пробелы и дефисы
      const nameRegex = /^[a-zA-Zа-яА-Я\s-]+$/;
      if (!nameRegex.test(value)) {
        return { pattern: 'Только буквы, пробелы и дефисы' };
      }

      return null;
    };
  }

  // Валидатор для аватара
  static avatar(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const file = control.value;

      if (!file) {
        return null; // Аватар не обязателен
      }

      // Проверяем, что это файл
      if (!(file instanceof File)) {
        return { invalidFile: 'Выберите файл' };
      }

      // Проверяем тип файла
      const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
      if (!allowedTypes.includes(file.type)) {
        return { fileType: 'Поддерживаются только изображения (JPEG, PNG, GIF, WebP)' };
      }

      // Проверяем размер файла (максимум 5MB)
      const maxSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxSize) {
        return { fileSize: 'Размер файла не должен превышать 5MB' };
      }

      return null;
    };
  }
}
