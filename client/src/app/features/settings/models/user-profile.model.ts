export interface UserProfile {
  id: string;
  username: string;
  email: string;
  displayName?: string;
  firstName?: string;
  lastName?: string;
  avatar?: string;
  role: 'user' | 'admin';
  lastLogin?: Date;
  createdAt: Date;
  updatedAt: Date;
}

export interface UpdateProfileRequest {
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
}

export interface UpdateAvatarRequest {
  avatar: File;
}

export interface ProfileFormData {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  avatar?: File;
}

export interface ProfileValidationErrors {
  username?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  avatar?: string;
}
