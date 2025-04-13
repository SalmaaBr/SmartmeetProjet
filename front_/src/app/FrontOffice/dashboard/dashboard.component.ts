import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

  constructor(private authService: AuthService, private router: Router) {}

  navigateToTeam(): void {
    const roles = localStorage.getItem('roles');
    if (roles && roles.includes('ADMIN')) {
      this.router.navigate(['/admin/resources']);  // Redirige vers /admin/resources si l'utilisateur est ADMIN
    } else {
      this.router.navigate(['/front']);  // Reste sur la même page ou redirige ailleurs pour les autres rôles
    }
  }

}
