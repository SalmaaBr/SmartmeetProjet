import { Component } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  username: string = '';
  email: string = '';
  password: string = '';
  message: string = '';

  constructor(private authService: AuthService) {}

  onSignup() {
    this.authService.signup(this.username, this.email, this.password, ['USER']).subscribe(
      response => {
        this.message = 'Inscription réussie !';
        alert('Inscription réussie');
      },
      error => {
        this.message = 'Erreur lors de l\'inscription';
        console.error('Erreur:', error);
        alert(JSON.stringify(error.error)); 
      }
    );
  }
  
  

}
