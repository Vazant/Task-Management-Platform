import { Component, inject, OnInit, OnDestroy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { LucideAngularModule, Loader2, AlertCircle, RefreshCw, Camera, User, Save } from 'lucide-angular';
import { Subject, takeUntil } from 'rxjs';

import { ProfileService } from '../../services/profile.service';
import { ProfileValidators } from '../../validators/profile.validators';
import { DateUtils } from '../../utils/date.utils';
import { UserProfile, UpdateProfileRequest, UpdateAvatarRequest } from '../../models/user-profile.model';

@Component({
  selector: 'app-user-profile-settings',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    LucideAngularModule
  ],
  templateUrl: './user-profile-settings.component.html',
  styleUrls: ['./user-profile-settings.component.scss']
})
export class UserProfileSettingsComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly profileService = inject(ProfileService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly destroy$ = new Subject<void>();

  // Document для доступа к DOM
  readonly document = document;

  // Lucide icons
  readonly Loader2 = Loader2;
  readonly AlertCircle = AlertCircle;
  readonly RefreshCw = RefreshCw;
  readonly Camera = Camera;
  readonly User = User;
  readonly Save = Save;



  // Form
  profileForm!: FormGroup;

  // Signals
  private readonly _avatarPreview = signal<string | null>(null);
  private readonly _isAvatarLoading = signal(false);
  private readonly _isFormSubmitting = signal(false);

  // Computed values
  readonly avatarPreview = this._avatarPreview.asReadonly();
  readonly isAvatarLoading = this._isAvatarLoading.asReadonly();
  readonly isFormSubmitting = this._isFormSubmitting.asReadonly();

  // Service signals
  readonly profile = this.profileService.profile;
  readonly isLoading = this.profileService.isLoading;
  readonly error = this.profileService.error;
  readonly hasError = this.profileService.hasError;

  // Computed values from service
  readonly displayName = computed(() => {
    const profile = this.profile();
    if (!profile) return '';

    if (profile.firstName && profile.lastName) {
      return `${profile.firstName} ${profile.lastName}`;
    }
    return profile.username;
  });

  readonly lastLoginFormatted = computed(() => {
    const profile = this.profile();
    return profile?.lastLogin ? DateUtils.formatDateTime(profile.lastLogin) : 'Неизвестно';
  });

  readonly createdAtFormatted = computed(() => {
    const profile = this.profile();
    return profile?.createdAt ? DateUtils.formatDate(profile.createdAt) : 'Неизвестно';
  });

  ngOnInit(): void {
    this.initializeForm();
    this.loadProfile();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForm(): void {
    this.profileForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), ProfileValidators.username()]],
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', [Validators.minLength(2), Validators.maxLength(50), ProfileValidators.name()]],
      lastName: ['', [Validators.minLength(2), Validators.maxLength(50), ProfileValidators.name()]],
      avatar: [null]
    });
  }

  private loadProfile(): void {
    // Загружаем профиль с сервера
    this.profileService.loadProfile().pipe(
      takeUntil(this.destroy$)
    ).subscribe(profile => {
      if (profile) {
        this.patchForm(profile);
        this._avatarPreview.set(profile.avatar ?? null);
      }
    });
  }

  private patchForm(profile: UserProfile): void {
    this.profileForm.patchValue({
      username: profile.username,
      email: profile.email,
      firstName: profile.firstName ?? '',
      lastName: profile.lastName ?? ''
    });
  }

  onAvatarChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    // Валидация файла
    const avatarControl = this.profileForm.get('avatar');
    if (!avatarControl) return;

    avatarControl.setValue(file);

    const validation = ProfileValidators.avatar()(avatarControl);
    if (validation) {
      this.showError(Object.values(validation)[0]);
      return;
    }

    // Создание превью
    this.createAvatarPreview(file);

    // Загрузка аватара
    this.uploadAvatar(file);
  }

  onAvatarError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/images/default-avatar.png';
  }

  private createAvatarPreview(file: File): void {
    const reader = new FileReader();
    reader.onload = (e) => {
      this._avatarPreview.set(e.target?.result as string);
    };
    reader.readAsDataURL(file);
  }

  private uploadAvatar(file: File): void {
    this._isAvatarLoading.set(true);

    const request: UpdateAvatarRequest = { avatar: file };
    this.profileService.updateAvatar(request).pipe(
      takeUntil(this.destroy$)
    ).subscribe(profile => {
      this._isAvatarLoading.set(false);
      if (profile) {
        this.showSuccess('Аватар успешно обновлен');
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.markFormGroupTouched();
      return;
    }

    this._isFormSubmitting.set(true);

    const formValue = this.profileForm.value;
    const request: UpdateProfileRequest = {
      username: formValue.username,
      email: formValue.email,
      firstName: formValue.firstName ?? undefined,
      lastName: formValue.lastName ?? undefined
    };

    this.profileService.updateProfile(request).pipe(
      takeUntil(this.destroy$)
    ).subscribe(profile => {
      this._isFormSubmitting.set(false);
      if (profile) {
        this.showSuccess('Профиль успешно обновлен');
      }
    });
  }

  onReset(): void {
    const profile = this.profile();
    if (profile) {
      this.patchForm(profile);
      this._avatarPreview.set(profile.avatar ?? null);
      this.profileForm.markAsPristine();
      this.profileService.clearError();
    }
  }

  retryLoadProfile(): void {
    this.profileService.retryLoadProfile();
  }

  clearError(): void {
    this.profileService.clearError();
  }

  private markFormGroupTouched(): void {
    Object.keys(this.profileForm.controls).forEach(key => {
      const control = this.profileForm.get(key);
      control?.markAsTouched();
    });
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Закрыть', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Закрыть', {
      duration: 5000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['error-snackbar']
    });
  }

  // Геттеры для удобного доступа к контролам формы
  get usernameControl() {
    return this.profileForm.get('username');
  }

  get emailControl() {
    return this.profileForm.get('email');
  }

  get firstNameControl() {
    return this.profileForm.get('firstName');
  }

  get lastNameControl() {
    return this.profileForm.get('lastName');
  }

  get avatarControl() {
    return this.profileForm.get('avatar');
  }
}
