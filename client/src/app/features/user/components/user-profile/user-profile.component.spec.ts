import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
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
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Store } from '@ngrx/store';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { UserProfileComponent } from './user-profile.component';
import { UserService } from '@core/services/user.service';
import { AuthService } from '@core/services/auth.service';
import { UserActions } from '@store/user/user.actions';
import { UserSelectors } from '@store/user/user.selectors';
import { User, UserProfile, UserPreferences, UserSettings } from '@core/models/user.model';

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;
  let store: MockStore;
  let userService: jasmine.SpyObj<UserService>;
  let authService: jasmine.SpyObj<AuthService>;

  const mockUser: User = {
    id: '1',
    email: 'test@example.com',
    profile: {
      firstName: 'John',
      lastName: 'Doe',
      phone: '+1234567890',
      bio: 'Test bio',
      location: 'New York',
      website: 'https://example.com',
      dateOfBirth: '1990-01-01',
      avatarUrl: 'https://example.com/avatar.jpg'
    },
    preferences: {
      language: 'en',
      timezone: 'UTC',
      dateFormat: 'MM/DD/YYYY',
      timeFormat: '12h',
      theme: 'light',
      notifications: {
        email: true,
        push: true,
        sms: false,
        taskReminders: true,
        projectUpdates: true,
        teamMessages: true
      }
    },
    settings: {
      privacy: {
        profileVisibility: 'public',
        showEmail: true,
        showPhone: false,
        showLocation: true,
        allowMessages: true
      },
      security: {
        twoFactorAuth: false,
        sessionTimeout: 30,
        passwordExpiry: 90,
        loginNotifications: true
      },
      display: {
        compactMode: false,
        showAvatars: true,
        showStatus: true,
        autoRefresh: true,
        refreshInterval: 30
      }
    }
  };

  const initialState = {
    user: {
      currentUser: mockUser,
      loading: false,
      error: null
    }
  };

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['exportUserData']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['logout']);

    await TestBed.configureTestingModule({
      imports: [
        UserProfileComponent,
        ReactiveFormsModule,
        NoopAnimationsModule,
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
        MatMenuModule,
        MatSlideToggleModule
      ],
      providers: [
        provideMockStore({ initialState }),
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(MockStore) as MockStore;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user profile on init', () => {
    spyOn(store, 'dispatch');
    
    component.ngOnInit();
    
    expect(store.dispatch).toHaveBeenCalledWith(UserActions.loadCurrentUser());
  });

  it('should display user information correctly', () => {
    const compiled = fixture.debugElement.nativeElement;
    
    expect(compiled.textContent).toContain('John Doe');
    expect(compiled.textContent).toContain('test@example.com');
  });

  it('should initialize forms with user data', () => {
    expect(component.profileForm.get('firstName')?.value).toBe('John');
    expect(component.profileForm.get('lastName')?.value).toBe('Doe');
    expect(component.profileForm.get('email')?.value).toBe('test@example.com');
    expect(component.preferencesForm.get('language')?.value).toBe('en');
    expect(component.settingsForm.get('privacy.profileVisibility')?.value).toBe('public');
  });

  it('should enable editing mode when edit button is clicked', () => {
    const editButton = fixture.debugElement.query(By.css('button[mat-raised-button]'));
    
    editButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    
    expect(component.isEditing).toBe(true);
  });

  it('should show forms when in editing mode', () => {
    component.isEditing = true;
    fixture.detectChanges();
    
    const forms = fixture.debugElement.queryAll(By.css('.form-card'));
    expect(forms.length).toBeGreaterThan(0);
  });

  it('should validate required fields', () => {
    component.isEditing = true;
    fixture.detectChanges();
    
    const firstNameControl = component.profileForm.get('firstName');
    firstNameControl?.setValue('');
    firstNameControl?.markAsTouched();
    
    expect(firstNameControl?.errors?.['required']).toBeTruthy();
  });

  it('should validate email format', () => {
    component.isEditing = true;
    fixture.detectChanges();
    
    const emailControl = component.profileForm.get('email');
    emailControl?.setValue('invalid-email');
    emailControl?.markAsTouched();
    
    expect(emailControl?.errors?.['email']).toBeTruthy();
  });

  it('should validate phone number format', () => {
    component.isEditing = true;
    fixture.detectChanges();
    
    const phoneControl = component.profileForm.get('phone');
    phoneControl?.setValue('invalid-phone');
    phoneControl?.markAsTouched();
    
    expect(phoneControl?.errors?.['pattern']).toBeTruthy();
  });

  it('should validate website URL format', () => {
    component.isEditing = true;
    fixture.detectChanges();
    
    const websiteControl = component.profileForm.get('website');
    websiteControl?.setValue('not-a-url');
    websiteControl?.markAsTouched();
    
    expect(websiteControl?.errors?.['pattern']).toBeTruthy();
  });

  it('should save profile when form is valid', fakeAsync(() => {
    spyOn(store, 'dispatch');
    component.isEditing = true;
    fixture.detectChanges();
    
    // Fill form with valid data
    component.profileForm.patchValue({
      firstName: 'Jane',
      lastName: 'Smith',
      email: 'jane@example.com'
    });
    
    component.onSave();
    tick();
    
    expect(store.dispatch).toHaveBeenCalledWith(
      jasmine.objectContaining({
        type: '[User] Update User Profile'
      })
    );
  }));

  it('should not save when form is invalid', () => {
    spyOn(store, 'dispatch');
    component.isEditing = true;
    fixture.detectChanges();
    
    // Make form invalid
    component.profileForm.get('firstName')?.setValue('');
    component.profileForm.get('firstName')?.markAsTouched();
    
    component.onSave();
    
    expect(store.dispatch).not.toHaveBeenCalled();
  });

  it('should cancel editing and reset forms', () => {
    component.isEditing = true;
    component.profileForm.get('firstName')?.setValue('Changed Name');
    fixture.detectChanges();
    
    component.onCancel();
    
    expect(component.isEditing).toBe(false);
    expect(component.profileForm.get('firstName')?.value).toBe('John');
  });

  it('should handle avatar change', fakeAsync(() => {
    const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
    const event = { target: { files: [file] } };
    
    spyOn(FileReader.prototype, 'readAsDataURL');
    spyOn(FileReader.prototype, 'onload');
    
    component.onAvatarChange(event as any);
    tick();
    
    expect(component.avatarPreview).toBeTruthy();
  }));

  it('should remove avatar', () => {
    component.avatarPreview = 'data:image/jpeg;base64,test';
    
    component.onRemoveAvatar();
    
    expect(component.avatarPreview).toBeNull();
    expect(component.profileForm.get('avatar')?.value).toBeNull();
  });

  it('should export user data', fakeAsync(() => {
    const mockData = { user: mockUser, tasks: [], projects: [] };
    userService.exportUserData.and.returnValue(of(mockData));
    
    spyOn(window.URL, 'createObjectURL').and.returnValue('blob:test');
    spyOn(window.URL, 'revokeObjectURL');
    spyOn(document, 'createElement').and.returnValue({
      href: '',
      download: '',
      click: jasmine.createSpy('click')
    } as any);
    
    component.onExportData();
    tick();
    
    expect(userService.exportUserData).toHaveBeenCalled();
  }));

  it('should handle delete account confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(store, 'dispatch');
    
    component.onDeleteAccount();
    
    expect(store.dispatch).toHaveBeenCalledWith(UserActions.deleteUserAccount());
  });

  it('should not delete account when user cancels', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    spyOn(store, 'dispatch');
    
    component.onDeleteAccount();
    
    expect(store.dispatch).not.toHaveBeenCalled();
  });

  it('should get full name correctly', () => {
    const fullName = component.getFullName(mockUser);
    expect(fullName).toBe('John Doe');
  });

  it('should get full name from email when profile is empty', () => {
    const userWithoutProfile = { ...mockUser, profile: undefined };
    const fullName = component.getFullName(userWithoutProfile);
    expect(fullName).toBe('test@example.com');
  });

  it('should get initials correctly', () => {
    const initials = component.getInitials(mockUser);
    expect(initials).toBe('JD');
  });

  it('should get initials from first name only', () => {
    const userWithFirstNameOnly = { 
      ...mockUser, 
      profile: { ...mockUser.profile, lastName: undefined } 
    };
    const initials = component.getInitials(userWithFirstNameOnly);
    expect(initials).toBe('J');
  });

  it('should get initials from email when no name', () => {
    const userWithoutName = { ...mockUser, profile: undefined };
    const initials = component.getInitials(userWithoutName);
    expect(initials).toBe('T');
  });

  it('should get field error messages', () => {
    const firstNameControl = component.profileForm.get('firstName');
    firstNameControl?.setValue('');
    firstNameControl?.markAsTouched();
    
    const error = component.getFieldError(component.profileForm, 'firstName');
    expect(error).toBe('firstName is required');
  });

  it('should get email error message', () => {
    const emailControl = component.profileForm.get('email');
    emailControl?.setValue('invalid-email');
    emailControl?.markAsTouched();
    
    const error = component.getFieldError(component.profileForm, 'email');
    expect(error).toBe('Please enter a valid email address');
  });

  it('should get minlength error message', () => {
    const firstNameControl = component.profileForm.get('firstName');
    firstNameControl?.setValue('A');
    firstNameControl?.markAsTouched();
    
    const error = component.getFieldError(component.profileForm, 'firstName');
    expect(error).toBe('firstName must be at least 2 characters');
  });

  it('should get maxlength error message', () => {
    const bioControl = component.profileForm.get('bio');
    bioControl?.setValue('a'.repeat(501));
    bioControl?.markAsTouched();
    
    const error = component.getFieldError(component.profileForm, 'bio');
    expect(error).toBe('bio must not exceed 500 characters');
  });

  it('should get pattern error message', () => {
    const phoneControl = component.profileForm.get('phone');
    phoneControl?.setValue('invalid-phone');
    phoneControl?.markAsTouched();
    
    const error = component.getFieldError(component.profileForm, 'phone');
    expect(error).toBe('Please enter a valid phone');
  });

  it('should return null for valid field', () => {
    const firstNameControl = component.profileForm.get('firstName');
    firstNameControl?.setValue('Valid Name');
    firstNameControl?.markAsTouched();
    
    const error = component.getFieldError(component.profileForm, 'firstName');
    expect(error).toBeNull();
  });

  it('should handle two factor authentication toggle', () => {
    spyOn(console, 'log');
    const twoFactorControl = component.settingsForm.get('security.twoFactorAuth');
    
    twoFactorControl?.setValue(true);
    component.onTwoFactorToggle();
    
    expect(console.log).toHaveBeenCalledWith('Enabling 2FA');
  });

  it('should handle password change request', () => {
    spyOn(console, 'log');
    
    component.onPasswordChange();
    
    expect(console.log).toHaveBeenCalledWith('Password change requested');
  });

  it('should show loading state', () => {
    store.setState({
      user: {
        currentUser: mockUser,
        loading: true,
        error: null
      }
    });
    store.refreshState();
    fixture.detectChanges();
    
    const loadingSpinner = fixture.debugElement.query(By.css('mat-spinner'));
    expect(loadingSpinner).toBeTruthy();
  });

  it('should show error state', () => {
    store.setState({
      user: {
        currentUser: mockUser,
        loading: false,
        error: 'Failed to load profile'
      }
    });
    store.refreshState();
    fixture.detectChanges();
    
    const errorElement = fixture.debugElement.query(By.css('.error-card'));
    expect(errorElement).toBeTruthy();
    expect(errorElement.nativeElement.textContent).toContain('Failed to load profile');
  });

  it('should unsubscribe on destroy', () => {
    spyOn(component['destroy$'], 'next');
    spyOn(component['destroy$'], 'complete');
    
    component.ngOnDestroy();
    
    expect(component['destroy$'].next).toHaveBeenCalled();
    expect(component['destroy$'].complete).toHaveBeenCalled();
  });

  it('should mark all form groups as touched when saving invalid form', () => {
    component.isEditing = true;
    fixture.detectChanges();
    
    // Make forms invalid
    component.profileForm.get('firstName')?.setValue('');
    component.preferencesForm.get('language')?.setValue('');
    component.settingsForm.get('privacy.profileVisibility')?.setValue('');
    
    spyOn(component.profileForm, 'markAsTouched');
    spyOn(component.preferencesForm, 'markAsTouched');
    spyOn(component.settingsForm, 'markAsTouched');
    
    component.onSave();
    
    expect(component.profileForm.markAsTouched).toHaveBeenCalled();
    expect(component.preferencesForm.markAsTouched).toHaveBeenCalled();
    expect(component.settingsForm.markAsTouched).toHaveBeenCalled();
  });

  it('should handle form validation for nested form groups', () => {
    component.isEditing = true;
    fixture.detectChanges();
    
    const notificationsGroup = component.preferencesForm.get('notifications');
    const emailControl = notificationsGroup?.get('email');
    
    emailControl?.setValue(false);
    emailControl?.markAsTouched();
    
    expect(notificationsGroup?.valid).toBe(true);
  });

  it('should populate forms with user data on user change', fakeAsync(() => {
    const newUser = { ...mockUser, profile: { ...mockUser.profile, firstName: 'Jane' } };
    
    store.setState({
      user: {
        currentUser: newUser,
        loading: false,
        error: null
      }
    });
    store.refreshState();
    tick();
    
    expect(component.profileForm.get('firstName')?.value).toBe('Jane');
  }));

  it('should handle missing user data gracefully', () => {
    const userWithMissingData = {
      id: '1',
      email: 'test@example.com'
    };
    
    store.setState({
      user: {
        currentUser: userWithMissingData as User,
        loading: false,
        error: null
      }
    });
    store.refreshState();
    
    expect(component.profileForm.get('firstName')?.value).toBe('');
    expect(component.preferencesForm.get('language')?.value).toBe('en');
  });

  it('should validate form submission with all required fields', fakeAsync(() => {
    spyOn(store, 'dispatch');
    component.isEditing = true;
    fixture.detectChanges();
    
    // Fill all required fields
    component.profileForm.patchValue({
      firstName: 'Jane',
      lastName: 'Smith',
      email: 'jane@example.com'
    });
    
    component.preferencesForm.patchValue({
      language: 'en',
      timezone: 'UTC',
      dateFormat: 'MM/DD/YYYY',
      timeFormat: '12h',
      theme: 'light'
    });
    
    component.settingsForm.patchValue({
      privacy: {
        profileVisibility: 'public'
      },
      security: {
        sessionTimeout: 30,
        passwordExpiry: 90
      },
      display: {
        refreshInterval: 30
      }
    });
    
    component.onSave();
    tick();
    
    expect(store.dispatch).toHaveBeenCalled();
  }));
});
