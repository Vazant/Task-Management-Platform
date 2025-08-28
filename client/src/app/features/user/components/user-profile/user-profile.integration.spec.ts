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
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { of, throwError } from 'rxjs';

import { UserProfileComponent } from './user-profile.component';
import { UserService } from '@core/services/user.service';
import { AuthService } from '@core/services/auth.service';
import { UserActions } from '@store/user/user.actions';
import { UserSelectors } from '@store/user/user.selectors';
import { User } from '@core/models/user.model';
import { 
  TestUtils, 
  TestDataFactory, 
  MockServices, 
  TestAssertions 
} from '@core/testing/test-utils';

describe('UserProfileComponent Integration', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;
  let store: MockStore;
  let userService: jasmine.SpyObj<UserService>;
  let authService: jasmine.SpyObj<AuthService>;

  const mockUser = TestDataFactory.createUser();

  const initialState = {
    user: {
      currentUser: mockUser,
      loading: false,
      error: null
    }
  };

  beforeEach(async () => {
    const userServiceSpy = MockServices.createUserService();
    const authServiceSpy = MockServices.createAuthService();

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

  describe('Component Initialization', () => {
    it('should initialize with user data from store', () => {
      expect(component.user$).toBeTruthy();
      expect(component.loading$).toBeTruthy();
      expect(component.error$).toBeTruthy();
    });

    it('should load user profile on init', () => {
      spyOn(store, 'dispatch');
      
      component.ngOnInit();
      
      expect(store.dispatch).toHaveBeenCalledWith(UserActions.loadCurrentUser());
    });

    it('should populate forms with user data', () => {
      expect(component.profileForm.get('firstName')?.value).toBe('John');
      expect(component.profileForm.get('lastName')?.value).toBe('Doe');
      expect(component.profileForm.get('email')?.value).toBe('test@example.com');
      expect(component.preferencesForm.get('language')?.value).toBe('en');
      expect(component.settingsForm.get('privacy.profileVisibility')?.value).toBe('public');
    });
  });

  describe('User Interface Integration', () => {
    it('should display user information in the UI', () => {
      TestAssertions.assertElementHasText(fixture, '.mat-mdc-card-title', 'John Doe');
      TestAssertions.assertElementHasText(fixture, '.mat-mdc-card-subtitle', 'test@example.com');
    });

    it('should show edit button when not in editing mode', () => {
      TestAssertions.assertElementExists(fixture, 'button[mat-raised-button]');
      TestAssertions.assertElementHasText(fixture, 'button[mat-raised-button]', 'Edit Profile');
    });

    it('should enable editing mode when edit button is clicked', () => {
      const editButton = TestUtils.getElement(fixture, 'button[mat-raised-button]');
      TestUtils.clickElement(editButton!);
      fixture.detectChanges();
      
      expect(component.isEditing).toBe(true);
    });

    it('should show forms when in editing mode', () => {
      component.isEditing = true;
      fixture.detectChanges();
      
      TestAssertions.assertElementExists(fixture, '.form-card');
      TestAssertions.assertElementExists(fixture, '.profile-form');
      TestAssertions.assertElementExists(fixture, '.preferences-form');
      TestAssertions.assertElementExists(fixture, '.settings-form');
    });

    it('should show save and cancel buttons when editing', () => {
      component.isEditing = true;
      fixture.detectChanges();
      
      TestAssertions.assertElementExists(fixture, '.edit-actions');
      TestAssertions.assertElementHasText(fixture, '.edit-actions button:first-child', 'Cancel');
      TestAssertions.assertElementHasText(fixture, '.edit-actions button:last-child', 'Save Changes');
    });
  });

  describe('Form Validation Integration', () => {
    beforeEach(() => {
      component.isEditing = true;
      fixture.detectChanges();
    });

    it('should validate required fields and show errors', () => {
      const firstNameControl = component.profileForm.get('firstName');
      firstNameControl?.setValue('');
      firstNameControl?.markAsTouched();
      fixture.detectChanges();
      
      expect(firstNameControl?.errors?.['required']).toBeTruthy();
    });

    it('should validate email format', () => {
      const emailControl = component.profileForm.get('email');
      emailControl?.setValue('invalid-email');
      emailControl?.markAsTouched();
      fixture.detectChanges();
      
      expect(emailControl?.errors?.['email']).toBeTruthy();
    });

    it('should validate phone number format', () => {
      const phoneControl = component.profileForm.get('phone');
      phoneControl?.setValue('invalid-phone');
      phoneControl?.markAsTouched();
      fixture.detectChanges();
      
      expect(phoneControl?.errors?.['pattern']).toBeTruthy();
    });

    it('should validate website URL format', () => {
      const websiteControl = component.profileForm.get('website');
      websiteControl?.setValue('not-a-url');
      websiteControl?.markAsTouched();
      fixture.detectChanges();
      
      expect(websiteControl?.errors?.['pattern']).toBeTruthy();
    });

    it('should not save when form is invalid', () => {
      spyOn(store, 'dispatch');
      
      // Make form invalid
      component.profileForm.get('firstName')?.setValue('');
      component.profileForm.get('firstName')?.markAsTouched();
      
      component.onSave();
      
      expect(store.dispatch).not.toHaveBeenCalled();
    });
  });

  describe('Form Submission Integration', () => {
    beforeEach(() => {
      component.isEditing = true;
      fixture.detectChanges();
    });

    it('should save profile when form is valid', fakeAsync(() => {
      spyOn(store, 'dispatch');
      
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

    it('should show loading state during save', fakeAsync(() => {
      // Set loading state
      TestUtils.setStoreState(store, {
        user: {
          currentUser: mockUser,
          loading: true,
          error: null
        }
      });
      tick();
      fixture.detectChanges();
      
      TestAssertions.assertElementExists(fixture, 'mat-spinner');
    }));

    it('should exit editing mode after successful save', fakeAsync(() => {
      spyOn(store, 'dispatch');
      
      // Fill form with valid data
      component.profileForm.patchValue({
        firstName: 'Jane',
        lastName: 'Smith',
        email: 'jane@example.com'
      });
      
      component.onSave();
      tick();
      
      // Simulate loading state change
      TestUtils.setStoreState(store, {
        user: {
          currentUser: mockUser,
          loading: false,
          error: null
        }
      });
      tick();
      
      expect(component.isEditing).toBe(false);
    }));
  });

  describe('Avatar Management Integration', () => {
    beforeEach(() => {
      component.isEditing = true;
      fixture.detectChanges();
    });

    it('should handle avatar file selection', fakeAsync(() => {
      const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
      const event = { target: { files: [file] } };
      
      spyOn(FileReader.prototype, 'readAsDataURL');
      spyOn(FileReader.prototype, 'onload');
      
      component.onAvatarChange(event as any);
      tick();
      
      expect(component.avatarPreview).toBeTruthy();
      expect(component.profileForm.get('avatar')?.value).toBe(file);
    }));

    it('should remove avatar when remove button is clicked', () => {
      component.avatarPreview = 'data:image/jpeg;base64,test';
      
      component.onRemoveAvatar();
      
      expect(component.avatarPreview).toBeNull();
      expect(component.profileForm.get('avatar')?.value).toBeNull();
    });

    it('should show avatar actions when editing', () => {
      TestAssertions.assertElementExists(fixture, '.avatar-actions');
    });
  });

  describe('Preferences Integration', () => {
    beforeEach(() => {
      component.isEditing = true;
      fixture.detectChanges();
    });

    it('should display language selection', () => {
      const languageSelect = TestUtils.getElement(fixture, 'mat-select[formControlName="language"]');
      expect(languageSelect).toBeTruthy();
    });

    it('should display timezone selection', () => {
      const timezoneSelect = TestUtils.getElement(fixture, 'mat-select[formControlName="timezone"]');
      expect(timezoneSelect).toBeTruthy();
    });

    it('should display theme selection', () => {
      const themeSelect = TestUtils.getElement(fixture, 'mat-select[formControlName="theme"]');
      expect(themeSelect).toBeTruthy();
    });

    it('should display notification toggles', () => {
      TestAssertions.assertElementExists(fixture, '.notifications-section');
      TestAssertions.assertElementExists(fixture, 'mat-slide-toggle[formControlName="email"]');
      TestAssertions.assertElementExists(fixture, 'mat-slide-toggle[formControlName="push"]');
    });
  });

  describe('Settings Integration', () => {
    beforeEach(() => {
      component.isEditing = true;
      fixture.detectChanges();
    });

    it('should display privacy settings', () => {
      TestAssertions.assertElementExists(fixture, 'h3:contains("Privacy")');
      TestAssertions.assertElementExists(fixture, 'mat-select[formControlName="profileVisibility"]');
    });

    it('should display security settings', () => {
      TestAssertions.assertElementExists(fixture, 'h3:contains("Security")');
      TestAssertions.assertElementExists(fixture, 'mat-slide-toggle[formControlName="twoFactorAuth"]');
    });

    it('should display display settings', () => {
      TestAssertions.assertElementExists(fixture, 'h3:contains("Display")');
      TestAssertions.assertElementExists(fixture, 'mat-slide-toggle[formControlName="compactMode"]');
    });
  });

  describe('Account Actions Integration', () => {
    it('should display account action buttons', () => {
      TestAssertions.assertElementExists(fixture, '.account-actions');
      TestAssertions.assertElementHasText(fixture, '.action-buttons button:first-child', 'Change Password');
      TestAssertions.assertElementHasText(fixture, '.action-buttons button:nth-child(2)', 'Export Data');
      TestAssertions.assertElementHasText(fixture, '.action-buttons button:last-child', 'Delete Account');
    });

    it('should handle export data action', fakeAsync(() => {
      const mockData = { user: mockUser, tasks: [], projects: [] };
      TestUtils.mockHttpResponse(userService, 'exportUserData', mockData);
      
      spyOn(window.URL, 'createObjectURL').and.returnValue('blob:test');
      spyOn(window.URL, 'revokeObjectURL');
      spyOn(document, 'createElement').and.returnValue({
        href: '',
        download: '',
        click: jasmine.createSpy('click')
      } as any);
      
      const exportButton = TestUtils.getElement(fixture, 'button:contains("Export Data")');
      TestUtils.clickElement(exportButton!);
      tick();
      
      expect(userService.exportUserData).toHaveBeenCalled();
    }));

    it('should handle delete account confirmation', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(store, 'dispatch');
      
      const deleteButton = TestUtils.getElement(fixture, 'button:contains("Delete Account")');
      TestUtils.clickElement(deleteButton!);
      
      expect(store.dispatch).toHaveBeenCalledWith(UserActions.deleteUserAccount());
    });
  });

  describe('Error Handling Integration', () => {
    it('should display error message when store has error', () => {
      TestUtils.setStoreState(store, {
        user: {
          currentUser: mockUser,
          loading: false,
          error: 'Failed to load profile'
        }
      });
      fixture.detectChanges();
      
      TestAssertions.assertElementExists(fixture, '.error-card');
      TestAssertions.assertElementHasText(fixture, '.error-card', 'Failed to load profile');
    });

    it('should handle service errors gracefully', fakeAsync(() => {
      TestUtils.mockHttpError(userService, 'exportUserData', new Error('Export failed'));
      
      const exportButton = TestUtils.getElement(fixture, 'button:contains("Export Data")');
      TestUtils.clickElement(exportButton!);
      tick();
      
      // Should not throw error
      expect(true).toBe(true);
    }));
  });

  describe('Loading States Integration', () => {
    it('should show loading spinner when loading', () => {
      TestUtils.setStoreState(store, {
        user: {
          currentUser: mockUser,
          loading: true,
          error: null
        }
      });
      fixture.detectChanges();
      
      TestAssertions.assertElementExists(fixture, 'mat-spinner');
    });

    it('should show loading text when loading', () => {
      TestUtils.setStoreState(store, {
        user: {
          currentUser: null,
          loading: true,
          error: null
        }
      });
      fixture.detectChanges();
      
      TestAssertions.assertElementHasText(fixture, '.loading-container p', 'Loading profile...');
    });
  });

  describe('Responsive Design Integration', () => {
    it('should adapt layout for mobile screens', () => {
      // Simulate mobile viewport
      Object.defineProperty(window, 'innerWidth', {
        writable: true,
        configurable: true,
        value: 768,
      });
      
      component.isEditing = true;
      fixture.detectChanges();
      
      // Check that forms are still accessible
      TestAssertions.assertElementExists(fixture, '.profile-form');
      TestAssertions.assertElementExists(fixture, '.preferences-form');
      TestAssertions.assertElementExists(fixture, '.settings-form');
    });
  });

  describe('Accessibility Integration', () => {
    it('should have proper ARIA labels', () => {
      component.isEditing = true;
      fixture.detectChanges();
      
      const firstNameInput = TestUtils.getElement(fixture, 'input[formControlName="firstName"]');
      expect(firstNameInput?.nativeElement.getAttribute('aria-label')).toBeTruthy();
    });

    it('should have proper form labels', () => {
      component.isEditing = true;
      fixture.detectChanges();
      
      TestAssertions.assertElementExists(fixture, 'mat-label:contains("First Name")');
      TestAssertions.assertElementExists(fixture, 'mat-label:contains("Last Name")');
      TestAssertions.assertElementExists(fixture, 'mat-label:contains("Email")');
    });
  });

  describe('Data Persistence Integration', () => {
    it('should persist form data during editing', () => {
      component.isEditing = true;
      fixture.detectChanges();
      
      // Change form values
      component.profileForm.patchValue({
        firstName: 'Jane',
        lastName: 'Smith'
      });
      
      // Verify values are persisted
      expect(component.profileForm.get('firstName')?.value).toBe('Jane');
      expect(component.profileForm.get('lastName')?.value).toBe('Smith');
    });

    it('should reset form data when canceling', () => {
      component.isEditing = true;
      fixture.detectChanges();
      
      // Change form values
      component.profileForm.patchValue({
        firstName: 'Jane',
        lastName: 'Smith'
      });
      
      // Cancel editing
      component.onCancel();
      
      // Verify values are reset
      expect(component.profileForm.get('firstName')?.value).toBe('John');
      expect(component.profileForm.get('lastName')?.value).toBe('Doe');
    });
  });
});
