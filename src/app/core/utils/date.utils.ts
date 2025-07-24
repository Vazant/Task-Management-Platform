export class DateUtils {
  static formatDate(date: Date): string {
    return date.toLocaleDateString('ru-RU');
  }

  static formatDateTime(date: Date): string {
    return date.toLocaleString('ru-RU');
  }

  static formatTime(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}`;
  }

  static getTimeSpent(startTime: Date, endTime?: Date): number {
    const end = endTime || new Date();
    return Math.floor((end.getTime() - startTime.getTime()) / (1000 * 60));
  }

  static isOverdue(dueDate: Date): boolean {
    return new Date() > dueDate;
  }

  static getDaysUntil(dueDate: Date): number {
    const now = new Date();
    const diffTime = dueDate.getTime() - now.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }
} 