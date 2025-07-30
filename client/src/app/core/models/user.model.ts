export interface User {
  id: string;
  email: string;
  username: string;
  role: 'user' | 'admin';
  avatar?: string;
  createdAt: Date;
  lastLogin: Date;
  preferences: UserPreferences;
}

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