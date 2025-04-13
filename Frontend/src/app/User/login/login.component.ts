import { Component } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentials = {
    username: '',
    password: ''
  };

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.authService.login(this.credentials).subscribe(
      (response: any) => {
        // Correct the token property name to accessToken
        this.authService.saveToken(response.accessToken);

        // Existing role handling
        const roles = response.roles;
        localStorage.setItem("roles", roles)
        localStorage.setItem("username", response.username)
        if (roles.includes('ADMIN')) {
          this.router.navigate(['/front']);
        } else if (roles.includes('USER')) {
          this.router.navigate(['/front']);
        } else {
          console.error('Rôle non reconnu');
        }
      },
      (error: any) => {
        console.error('Erreur d\'authentification', error);
      }
    );
  }

}
