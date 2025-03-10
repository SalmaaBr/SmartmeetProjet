import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar-back',
  templateUrl: './navbar-back.component.html',
  styleUrls: ['./navbar-back.component.css']
})
export class NavbarBackComponent {
  username = localStorage.getItem("username")
  roles = localStorage.getItem("roles")
    constructor(private router: Router) {}

  logout(){
    localStorage.removeItem("roles")
    localStorage.removeItem("username")
    localStorage.removeItem("auth_token")
    this.router.navigate(['/']);

  }
}
