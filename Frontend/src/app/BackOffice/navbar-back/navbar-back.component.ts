import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar-back',
  templateUrl: './navbar-back.component.html',
  styleUrls: ['./navbar-back.component.css']
})
export class NavbarBackComponent {
  isAuth = localStorage.getItem('auth_token')

  username = localStorage.getItem("username")
  roles = localStorage.getItem("roles")
    constructor(private router: Router) {}

    logout() {
      localStorage.removeItem("roles");
      localStorage.removeItem("username");
      localStorage.removeItem("auth_token");
      this.router.navigate(['/login']).then(() => {
        location.reload(); // Forcer un rafraîchissement de la page après la redirection
      });
    }
}
