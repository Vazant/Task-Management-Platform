export interface User {
  id: string;
  email: string;
  username: string;
  displayName?: string;
  avatar?: string;
  avatarUrl?: string; // может быть undefined
  role: 'user' | 'admin';
  createdAt: Date;
  updatedAt?: Date;
  lastLogin?: Date;
}

// для совместимости старых импортов
export type UserDto = User;

export interface UserPreferences {
  theme: 'light' | 'dark';
  language: string;
  notifications: NotificationSettings;
}

export interface NotificationSettings {
  email: boolean;
  push: boolean;
  taskUpdates: boolean;
  projectUpdates: boolean;
} 