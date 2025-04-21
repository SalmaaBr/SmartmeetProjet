import { Component } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-reset-password-request',
  templateUrl: './reset-password-request.component.html',
  styleUrls: ['./reset-password-request.component.css']
})
export class ResetPasswordRequestComponent {
  email: string = '';
  message: string = '';
  submitted: boolean = false;
  error: boolean = false;

  constructor(private authService: AuthService) {}

  onSubmit() {
    this.authService.resetPasswordRequest(this.email).subscribe({
      next: () => {
        this.message = 'Password reset email sent. Please check your inbox.';
        this.submitted = true;
        this.error = false;
      },
      error: () => {
        this.message = 'Failed to send password reset email.';
        this.submitted = true;
        this.error = true;
      }
    });
  }
}
