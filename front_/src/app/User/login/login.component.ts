import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  SpecialChar = '(!@#$%^&*(),.?":{}|<>)';
  loginError: string | null = null;

  constructor(private authService: AuthService, private router: Router) {
    this.loginForm = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        this.passwordValidator.bind(this)
      ])
    });
  }

  passwordValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';
    const hasUpperCase = /[A-Z]/.test(value);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);

    const errors: ValidationErrors = {};
    if (!hasUpperCase) errors['missingUpperCase'] = true;
    if (!hasSpecialChar) errors['missingSpecialChar'] = true;

    return Object.keys(errors).length ? errors : null;
  }

  onSubmit() {
    this.loginError = null; // Reset error message on new submission

    if (this.loginForm.invalid) return;

    const credentials = {
      username: this.loginForm.value.username,
      password: this.loginForm.value.password
    };

    this.authService.login(credentials).subscribe(
      (response: any) => {
        this.authService.saveToken(response.accessToken);
        const roles = response.roles;
        console.log("🚀 ~ LoginComponent ~ onSubmit ~ roles:", roles)

        localStorage.setItem("roles", roles);
        localStorage.setItem("username", response.username);
        localStorage.setItem("email", response.email);

        console.log("🚀 ~ LoginComponent ~ onSubmit ~ roles:", roles)
        if (roles.includes('ADMIN')) {
          this.router.navigate(['/admin']);
          return;
        } else if (roles.includes('USER')) {
          this.router.navigate(['/front']);
          return;
        } else {
          console.error('Rôle non reconnu');
        }
      },
      (error: any) => {
        console.error('Erreur d\'authentification', error);
        if (error.status === 403 && error.error.message === 'User Not Active') {
          this.loginError = 'Your account has been disabled. Please contact support.';
        } else if (error.status === 401) {
          this.loginError = 'Invalid username or password.';
        } else {
          this.loginError = 'An unexpected error occurred. Please try again later.';
        }
      }
    );
  }

  get username() { return this.loginForm.get('username'); }
  get password() { return this.loginForm.get('password'); }
}
