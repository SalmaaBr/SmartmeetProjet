import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header-front',
  templateUrl: './header-front.component.html',
  styleUrls: ['./header-front.component.css']
})
export class HeaderFrontComponent {
isAuth = localStorage.getItem('auth_token')

errorMessage: string = '';

    constructor(private router: Router) {}

  logout(){
    localStorage.removeItem("roles")
    localStorage.removeItem("username")
    localStorage.removeItem("auth_token")
    this.router.navigate(['/login']);
  }

    // Méthode pour gérer la navigation selon le rôle
    navigateToTeam(): void {
      const roles = localStorage.getItem('roles');
      if (roles && roles.includes('ADMIN')) {
        this.router.navigate(['/admin/resources']);  // Redirige vers /admin/resources si l'utilisateur est ADMIN
      } else {
        this.router.navigate(['/front']);  // Reste sur la même page ou redirige ailleurs pour les autres rôles
        alert('Accès non autorisé');
      }
    }
}
