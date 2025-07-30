export class DateUtils {
  // Форматирование даты для отображения
  static formatDate(date: Date | string): string {
    const dateObj = typeof date === 'string' ? new Date(date) : date;

    if (isNaN(dateObj.getTime())) {
      return 'Неизвестно';
    }

    const now = new Date();
    const diffInMs = now.getTime() - dateObj.getTime();
    const diffInMinutes = Math.floor(diffInMs / (1000 * 60));
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
    const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));

    // Сегодня
    if (diffInDays === 0) {
      if (diffInHours === 0) {
        if (diffInMinutes < 1) {
          return 'Только что';
        }
        return `${diffInMinutes} мин. назад`;
      }
      return `${diffInHours} ч. назад`;
    }

    // Вчера
    if (diffInDays === 1) {
      return 'Вчера';
    }

    // Неделя назад
    if (diffInDays < 7) {
      return `${diffInDays} дн. назад`;
    }

    // Форматирование полной даты
    return dateObj.toLocaleDateString('ru-RU', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // Форматирование времени
  static formatTime(date: Date | string): string {
    const dateObj = typeof date === 'string' ? new Date(date) : date;

    if (isNaN(dateObj.getTime())) {
      return '--:--';
    }

    return dateObj.toLocaleTimeString('ru-RU', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // Форматирование даты и времени
  static formatDateTime(date: Date | string): string {
    const dateObj = typeof date === 'string' ? new Date(date) : date;

    if (isNaN(dateObj.getTime())) {
      return 'Неизвестно';
    }

    return dateObj.toLocaleString('ru-RU', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // Проверка, является ли дата сегодняшней
  static isToday(date: Date | string): boolean {
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    const today = new Date();

    return dateObj.toDateString() === today.toDateString();
  }

  // Получить относительное время
  static getRelativeTime(date: Date | string): string {
    const dateObj = typeof date === 'string' ? new Date(date) : date;

    if (isNaN(dateObj.getTime())) {
      return 'Неизвестно';
    }

    const now = new Date();
    const diffInMs = now.getTime() - dateObj.getTime();
    const diffInSeconds = Math.floor(diffInMs / 1000);
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    const diffInHours = Math.floor(diffInMinutes / 60);
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInSeconds < 60) {
      return 'только что';
    } else if (diffInMinutes < 60) {
      return `${diffInMinutes} мин. назад`;
    } else if (diffInHours < 24) {
      return `${diffInHours} ч. назад`;
    } else if (diffInDays < 7) {
      return `${diffInDays} дн. назад`;
    } else {
      return this.formatDate(dateObj);
    }
  }
}
