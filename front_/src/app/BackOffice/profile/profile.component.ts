import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  user!: User;
  showPasswordFields = false;
  isLoading = true;
  errorMessage = '';
  successMessage = '';

  // Propriétés pour la prédiction
  showPrediction = false;
  predictionData: any = null;
  predictionStatus = '';
  predictionProbability = '';

  email =localStorage.getItem("email")
  userId?:number;
  constructor(
    private fb: FormBuilder,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadUserProfile();
    this.checkForPrediction();

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
          this.userId = user.userID
          this.profileForm.patchValue(user);
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'Failed to load profile';
          this.isLoading = false;
        }
      });
    }
  }
  checkForPrediction(): void {
    // Vérifier le localStorage
    const storedPrediction = localStorage.getItem('mentalHealthPrediction');
    if (storedPrediction) {
      try {
        this.displayPrediction(JSON.parse(storedPrediction));
      } catch (e) {
        console.error('Error parsing prediction data:', e);
        localStorage.removeItem('mentalHealthPrediction');
      }
    }
  }
  displayPrediction(prediction: any): void {
    this.showPrediction = true;
    this.predictionData = prediction;
    this.predictionStatus = prediction.crisis_detected ?
      'Potential Crisis Detected' : 'No Crisis Detected';
    this.predictionProbability = (prediction.crisis_probability * 100).toFixed(1) + '%';
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

    const formData = { ...this.profileForm.value, enabled: this.user.enabled}
    if (!this.showPasswordFields) {
      delete formData.currentPassword;
      delete formData.newPassword;
      delete formData.confirmPassword;
    }
    if (this.userId) {
      formData.password = formData.newPassword;
      //@ts-ignore
      this.userService.updateUser(parseInt(this.userId),formData).subscribe({
        next: (updatedUser) => {
          this.user = updatedUser;
          this.successMessage = 'Profile updated successfully!';
          this.profileForm.patchValue(updatedUser);
          this.isLoading = false;
          if (this.showPasswordFields) this.togglePasswordFields();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update profile';
          this.isLoading = false;
        }
      });
    }
  }
}
