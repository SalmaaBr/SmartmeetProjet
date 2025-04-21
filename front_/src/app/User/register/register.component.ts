import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  signupForm: FormGroup;
  message: string = '';

  constructor(private authService: AuthService) {
    this.signupForm = new FormGroup({
      username: new FormControl('', [
        Validators.required,
        Validators.minLength(3)
      ]),
      email: new FormControl('', [
        Validators.required,
        Validators.email
      ]),
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        this.passwordValidator.bind(this) // Fixed binding
      ]),
      terms: new FormControl(false, Validators.requiredTrue)
    });

    // For debugging
    this.signupForm.statusChanges.subscribe(status => {
      console.log('Form Status:', status);
      console.log('Form Errors:', this.signupForm.errors);
      console.log('Password Errors:', this.signupForm.get('password')?.errors);
    });
  }

  passwordValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';
    if (!value) return null;

    const hasUpperCase = /[A-Z]/.test(value);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);

    const errors: ValidationErrors = {};
    if (!hasUpperCase) errors['missingUpperCase'] = true;
    if (!hasSpecialChar) errors['missingSpecialChar'] = true;

    return Object.keys(errors).length ? errors : null;
  }

  get f() {
    return this.signupForm.controls;
  }

  onSignup() {
    if (this.signupForm.invalid) {
      console.log('Form Invalid - Aborting Submission');
      return;
    }

    const { username, email, password } = this.signupForm.value;

    this.authService.signup(username, email, password, ['USER']).subscribe(
      response => {
        this.message = 'Inscription réussie !';
        this.signupForm.reset();
      },
      error => {
        this.message = error.error?.message || 'Erreur lors de l\'inscription';
        console.error('Erreur:', error);
      }
    );
  }
}
