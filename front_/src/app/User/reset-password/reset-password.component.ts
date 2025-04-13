// reset-password.component.ts
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  resetForm: FormGroup;
  token: string = '';
  message: string = '';
  tokenValid: boolean = false;
  submitted: boolean = false;
  error: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {
    this.resetForm = new FormGroup({
      newPassword: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        this.passwordValidator
      ]),
      confirmPassword: new FormControl('', [Validators.required])
    }, { validators: this.matchValidator });
  }

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (this.token) {
      this.authService.validateResetToken(this.token).subscribe({
        next: () => this.tokenValid = true,
        error: () => {
          this.tokenValid = false;
          this.message = 'Invalid or expired token. Please request a new password reset.';
        }
      });
    } else {
      this.tokenValid = false;
      this.message = 'No reset token provided. Please use the link from your email.';
    }
  }

  passwordValidator(control: AbstractControl) {
    const value = control.value;
    if (!value) return null;

    const hasUpperCase = /[A-Z]/.test(value);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);

    return !hasUpperCase || !hasSpecialChar
      ? { passwordStrength: true }
      : null;
  }

  matchValidator(control: AbstractControl) {
    const password = control.get('newPassword')?.value;
    const confirm = control.get('confirmPassword')?.value;
    return password === confirm ? null : { mismatch: true };
  }

  onSubmit() {
    if (this.resetForm.invalid) return;

    const newPassword = this.resetForm.get('newPassword')?.value;
    this.authService.resetPassword(this.token, newPassword).subscribe({
      next: () => {
        this.message = 'Password reset successfully! You can now log in with your new password.';
        this.error = false;
        this.submitted = true;
        this.resetForm.reset();
      },
      error: () => {
        this.message = 'Failed to reset password. Please try again.';
        this.error = true;
      }
    });
  }
  validateUppercase(): boolean {
    return /[A-Z]/.test(this.resetForm.get('newPassword')?.value || '');
  }

  validateSpecialCharacter(): boolean {
    return /[!@#$%^&*(),.?":{}|<>]/.test(this.resetForm.get('newPassword')?.value || '');
  }
  redirectToLogin() {
    this.router.navigate(['/login']);
  }
}
