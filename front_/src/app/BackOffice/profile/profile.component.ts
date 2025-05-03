import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { TypeTheme } from '../../models/event/createevent/createevent/event.enums';
import { catchError, of } from 'rxjs';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy {
  profileForm!: FormGroup;
  user!: User;
  showPasswordFields = false;
  isLoading = true;
  errorMessage = '';
  successMessage = '';
  email = localStorage.getItem("email");
  userId?: number;
  interests: string[] = [];
  selectedInterests: Set<string> = new Set();
  profileImageUrl: string | null = null;
  timestamp: number = Date.now();

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadUserProfile();
  }

  initializeForm(): void {
    this.profileForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: [''],
      address: [''],
      currentPassword: [''],
      newPassword: [''],
      confirmPassword: ['']
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    return form.get('newPassword')?.value === form.get('confirmPassword')?.value
      ? null : { mismatch: true };
  }

  loadUserProfile(): void {
    if (this.email) {
      this.userService.getUserByEmail(this.email).subscribe({
        next: (user) => {
          this.user = user;
          this.userId = user.userID;
          this.profileForm.patchValue(user);
          
          // Load profile image if exists
          if (this.userId) {
            this.loadProfileImage();
          }
          
          // Initialize selected interests
          if (user.interests) {
            this.selectedInterests = new Set(user.interests);
          }
          
          // Load available interests
          this.interests = this.userService.getInterests();
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'Failed to load profile';
          this.isLoading = false;
        }
      });
    }
  }

  loadProfileImage(): void {
    if (this.userId) {
      this.userService.getProfileImage(this.userId).subscribe({
        next: (imageUrl) => {
          // Cleanup previous URL if it exists
          if (this.profileImageUrl) {
            URL.revokeObjectURL(this.profileImageUrl);
          }
          this.profileImageUrl = imageUrl;
        },
        error: (err) => {
          console.error('Error loading profile image:', err);
          this.profileImageUrl = null;
        }
      });
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file && this.userId) {
      this.isLoading = true;
      
      // Create a temporary URL for immediate display
      const tempUrl = URL.createObjectURL(file);
      this.profileImageUrl = tempUrl;
      
      this.userService.uploadProfileImage(this.userId, file).subscribe({
        next: (response) => {
          // Revoke the temporary URL
          URL.revokeObjectURL(tempUrl);
          
          // Load the new image from server
          this.loadProfileImage();
          
          this.successMessage = 'Profile image updated successfully!';
          this.isLoading = false;
        },
        error: (err) => {
          // Cleanup temporary URL
          URL.revokeObjectURL(tempUrl);
          // Reload the previous image
          this.loadProfileImage();
          this.errorMessage = 'Failed to upload profile image';
          this.isLoading = false;
        }
      });
    }
  }

  // Ajoutez cette méthode pour gérer la sélection/désélection
  toggleInterest(interest: string): void {
    if (this.selectedInterests.has(interest)) {
      this.selectedInterests.delete(interest);
    } else {
      this.selectedInterests.add(interest);
    }
  }

  togglePasswordFields(): void {
    this.showPasswordFields = !this.showPasswordFields;
    if (!this.showPasswordFields) {
      this.profileForm.get('currentPassword')?.reset();
      this.profileForm.get('newPassword')?.reset();
      this.profileForm.get('confirmPassword')?.reset();
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Create form data and preserve the enabled status
    const formData = { 
      ...this.profileForm.value,
      interests: Array.from(this.selectedInterests),
      enabled: this.user.enabled // Preserve the enabled status
    };

    if (!this.showPasswordFields) {
      delete formData.currentPassword;
      delete formData.newPassword;
      delete formData.confirmPassword;
    }

    if (this.userId) {
      this.userService.updateUser(this.userId, formData).subscribe({
        next: (updatedUser) => {
          this.user = updatedUser;
          
          // Update local storage with new user data
          const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
          const updatedUserData = {
            ...currentUser,
            username: updatedUser.username,
            email: updatedUser.email,
            interests: updatedUser.interests
          };
          localStorage.setItem('user', JSON.stringify(updatedUserData));
          localStorage.setItem('username', updatedUser.username);
          localStorage.setItem('email', updatedUser.email);

          this.successMessage = 'Profile updated successfully!';
          this.profileForm.patchValue(updatedUser);
          this.isLoading = false;
          if (this.showPasswordFields) {
            this.togglePasswordFields();
            // If password was changed, you might want to show a message suggesting re-login
            this.successMessage = 'Profile updated successfully! Please re-login if you changed your password.';
          }
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update profile';
          this.isLoading = false;
        }
      });
    }
  }

  ngOnDestroy() {
    // Clean up any object URLs when component is destroyed
    if (this.profileImageUrl) {
      URL.revokeObjectURL(this.profileImageUrl);
    }
  }
}
