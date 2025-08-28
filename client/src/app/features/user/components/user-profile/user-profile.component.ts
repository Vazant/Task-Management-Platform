import { Component, OnInit, ChangeDetectionStrategy, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatAvatarModule } from '@angular/material/avatar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { Store } from '@ngrx/store';
import { Observable, Subject, takeUntil } from 'rxjs';
import { User, UserProfile } from '@core/models/user.model';
import { UserActions } from '@store/user/user.actions';
import { UserSelectors } from '@store/user/user.selectors';
import { UserService } from '@core/services/user.service';
import { AuthService } from '@core/services/auth.service';
import { 
  User, 
  UserProfile, 
  UserPreferences, 
  UserSettings 
} from '@core/models/user.model';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatChipsModule,
    MatAvatarModule,
    MatTooltipModule,
    MatMenuModule
  ],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserProfileComponent implements OnInit, OnDestroy {
  user$: Observable<User | null>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;
  
  profileForm: FormGroup;
  preferencesForm: FormGroup;
  settingsForm: FormGroup;
  
  isEditing = false;
  isSaving = false;
  avatarPreview: string | null = null;
  
  private destroy$ = new Subject<void>();

  constructor(
    private store: Store,
    private userService: UserService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.user$ = this.store.select(UserSelectors.selectCurrentUser);
    this.loading$ = this.store.select(UserSelectors.selectUserLoading);
    this.error$ = this.store.select(UserSelectors.selectUserError);
    
    this.initializeForms();
  }

  ngOnInit(): void {
    this.loadUserProfile();
    this.subscribeToUserChanges();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForms(): void {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.pattern(/^\+?[\d\s\-\(\)]+$/)]],
      bio: ['', [Validators.maxLength(500)]],
      location: ['', [Validators.maxLength(100)]],
      website: ['', [Validators.pattern(/^https?:\/\/.+/)]],
      dateOfBirth: [null],
      avatar: [null]
    });

    this.preferencesForm = this.fb.group({
      language: ['en', Validators.required],
      timezone: ['UTC', Validators.required],
      dateFormat: ['MM/DD/YYYY', Validators.required],
      timeFormat: ['12h', Validators.required],
      theme: ['light', Validators.required],
      notifications: this.fb.group({
        email: [true],
        push: [true],
        sms: [false],
        taskReminders: [true],
        projectUpdates: [true],
        teamMessages: [true]
      })
    });

    this.settingsForm = this.fb.group({
      privacy: this.fb.group({
        profileVisibility: ['public'],
        showEmail: [true],
        showPhone: [false],
        showLocation: [true],
        allowMessages: [true]
      }),
      security: this.fb.group({
        twoFactorAuth: [false],
        sessionTimeout: [30],
        passwordExpiry: [90],
        loginNotifications: [true]
      }),
      display: this.fb.group({
        compactMode: [false],
        showAvatars: [true],
        showStatus: [true],
        autoRefresh: [true],
        refreshInterval: [30]
      })
    });
  }

  private loadUserProfile(): void {
    this.store.dispatch(UserActions.loadCurrentUser());
  }

  private subscribeToUserChanges(): void {
    this.user$.pipe(takeUntil(this.destroy$)).subscribe(user => {
      if (user) {
        this.populateForms(user);
      }
    });
  }

  private populateForms(user: User): void {
    // Profile form
    this.profileForm.patchValue({
      firstName: user.profile?.firstName || '',
      lastName: user.profile?.lastName || '',
      email: user.email || '',
      phone: user.profile?.phone || '',
      bio: user.profile?.bio || '',
      location: user.profile?.location || '',
      website: user.profile?.website || '',
      dateOfBirth: user.profile?.dateOfBirth ? new Date(user.profile.dateOfBirth) : null
    });

    // Preferences form
    if (user.preferences) {
      this.preferencesForm.patchValue({
        language: user.preferences.language || 'en',
        timezone: user.preferences.timezone || 'UTC',
        dateFormat: user.preferences.dateFormat || 'MM/DD/YYYY',
        timeFormat: user.preferences.timeFormat || '12h',
        theme: user.preferences.theme || 'light',
        notifications: {
          email: user.preferences.notifications?.email ?? true,
          push: user.preferences.notifications?.push ?? true,
          sms: user.preferences.notifications?.sms ?? false,
          taskReminders: user.preferences.notifications?.taskReminders ?? true,
          projectUpdates: user.preferences.notifications?.projectUpdates ?? true,
          teamMessages: user.preferences.notifications?.teamMessages ?? true
        }
      });
    }

    // Settings form
    if (user.settings) {
      this.settingsForm.patchValue({
        privacy: {
          profileVisibility: user.settings.privacy?.profileVisibility || 'public',
          showEmail: user.settings.privacy?.showEmail ?? true,
          showPhone: user.settings.privacy?.showPhone ?? false,
          showLocation: user.settings.privacy?.showLocation ?? true,
          allowMessages: user.settings.privacy?.allowMessages ?? true
        },
        security: {
          twoFactorAuth: user.settings.security?.twoFactorAuth ?? false,
          sessionTimeout: user.settings.security?.sessionTimeout || 30,
          passwordExpiry: user.settings.security?.passwordExpiry || 90,
          loginNotifications: user.settings.security?.loginNotifications ?? true
        },
        display: {
          compactMode: user.settings.display?.compactMode ?? false,
          showAvatars: user.settings.display?.showAvatars ?? true,
          showStatus: user.settings.display?.showStatus ?? true,
          autoRefresh: user.settings.display?.autoRefresh ?? true,
          refreshInterval: user.settings.display?.refreshInterval || 30
        }
      });
    }
  }

  onEdit(): void {
    this.isEditing = true;
  }

  onCancel(): void {
    this.isEditing = false;
    this.avatarPreview = null;
    this.user$.pipe(takeUntil(this.destroy$)).subscribe(user => {
      if (user) {
        this.populateForms(user);
      }
    });
  }

  onSave(): void {
    if (this.profileForm.valid && this.preferencesForm.valid && this.settingsForm.valid) {
      this.isSaving = true;
      
      const profileData = {
        ...this.profileForm.value,
        ...this.preferencesForm.value,
        ...this.settingsForm.value
      };

      this.store.dispatch(UserActions.updateUserProfile({ profile: profileData }));
      
      // Subscribe to loading state to know when save is complete
      this.loading$.pipe(takeUntil(this.destroy$)).subscribe(loading => {
        if (!loading && this.isSaving) {
          this.isSaving = false;
          this.isEditing = false;
          this.avatarPreview = null;
        }
      });
    } else {
      this.markFormGroupsTouched();
    }
  }

  onAvatarChange(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.avatarPreview = e.target?.result as string;
        this.profileForm.patchValue({ avatar: file });
      };
      reader.readAsDataURL(file);
    }
  }

  onRemoveAvatar(): void {
    this.avatarPreview = null;
    this.profileForm.patchValue({ avatar: null });
  }

  onPasswordChange(): void {
    // Navigate to password change component or open dialog
    console.log('Password change requested');
  }

  onTwoFactorToggle(): void {
    const twoFactorAuth = this.settingsForm.get('security.twoFactorAuth')?.value;
    if (twoFactorAuth) {
      // Enable 2FA
      console.log('Enabling 2FA');
    } else {
      // Disable 2FA
      console.log('Disabling 2FA');
    }
  }

  onExportData(): void {
    this.userService.exportUserData().subscribe(data => {
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'user-data.json';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  onDeleteAccount(): void {
    if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
      this.store.dispatch(UserActions.deleteUserAccount());
    }
  }

  private markFormGroupsTouched(): void {
    Object.keys(this.profileForm.controls).forEach(key => {
      const control = this.profileForm.get(key);
      control?.markAsTouched();
    });

    Object.keys(this.preferencesForm.controls).forEach(key => {
      const control = this.preferencesForm.get(key);
      if (control instanceof FormGroup) {
        Object.keys(control.controls).forEach(subKey => {
          control.get(subKey)?.markAsTouched();
        });
      } else {
        control?.markAsTouched();
      }
    });

    Object.keys(this.settingsForm.controls).forEach(key => {
      const control = this.settingsForm.get(key);
      if (control instanceof FormGroup) {
        Object.keys(control.controls).forEach(subKey => {
          const subControl = control.get(subKey);
          if (subControl instanceof FormGroup) {
            Object.keys(subControl.controls).forEach(subSubKey => {
              subControl.get(subSubKey)?.markAsTouched();
            });
          } else {
            subControl?.markAsTouched();
          }
        });
      } else {
        control?.markAsTouched();
      }
    });
  }

  getFieldError(form: FormGroup, fieldName: string): string | null {
    const field = form.get(fieldName);
    if (field?.invalid && field?.touched) {
      if (field.errors?.['required']) {
        return `${fieldName} is required`;
      }
      if (field.errors?.['email']) {
        return 'Please enter a valid email address';
      }
      if (field.errors?.['minlength']) {
        return `${fieldName} must be at least ${field.errors['minlength'].requiredLength} characters`;
      }
      if (field.errors?.['maxlength']) {
        return `${fieldName} must not exceed ${field.errors['maxlength'].requiredLength} characters`;
      }
      if (field.errors?.['pattern']) {
        return `Please enter a valid ${fieldName}`;
      }
    }
    return null;
  }

  getFullName(user: User): string {
    const firstName = user.profile?.firstName || '';
    const lastName = user.profile?.lastName || '';
    return `${firstName} ${lastName}`.trim() || user.email || 'Unknown User';
  }

  getInitials(user: User): string {
    const firstName = user.profile?.firstName || '';
    const lastName = user.profile?.lastName || '';
    const email = user.email || '';
    
    if (firstName && lastName) {
      return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
    } else if (firstName) {
      return firstName.charAt(0).toUpperCase();
    } else if (email) {
      return email.charAt(0).toUpperCase();
    }
    
    return 'U';
  }
}
