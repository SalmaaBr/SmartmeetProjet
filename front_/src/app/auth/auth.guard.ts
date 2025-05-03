import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, CanActivateChild } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate, CanActivateChild {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    const token = this.authService.getToken();
    const rolesStr = localStorage.getItem("roles");
    
    if (!token || !rolesStr) {
      this.router.navigate(['/login']);
      return false;
    }

    try {
      const roles = JSON.parse(rolesStr);
      if (Array.isArray(roles) && roles.includes('ADMIN')) {
        return true;
      }
    } catch (e) {
      console.error('Error parsing roles:', e);
    }

    this.router.navigate(['/login']);
    return false;
  }

  canActivateChild(): boolean {
    return this.canActivate();
  }
}
