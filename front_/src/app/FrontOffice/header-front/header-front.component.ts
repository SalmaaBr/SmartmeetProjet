import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header-front',
  templateUrl: './header-front.component.html',
  styleUrls: ['./header-front.component.css']
})
export class HeaderFrontComponent {
isAuth = localStorage.getItem('auth-token')
username = localStorage.getItem("username")
roles = localStorage.getItem("roles")

    constructor(private router: Router) {}

  logout(){
    localStorage.removeItem("roles")
    localStorage.removeItem("username")
    localStorage.removeItem("auth-token")
    this.router.navigate(['login']);
  }
}