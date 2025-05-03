import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { User, TypeUserRole } from '../models/user.model'; // Importer le modèle User

export interface Confirm$Params {
  token: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authUrl = 'http://localhost:8082/api/auth';
  private tokenKey = 'auth-token';
  private userKey = 'current-user'; // Clé pour stocker l'utilisateur dans localStorage
  private refreshTokenInterval: any;
  private refreshTokenSubject = new BehaviorSubject<boolean>(false);
  private currentUser: User | null = null; // Propriété pour stocker l'utilisateur
  private authStatusSubject = new BehaviorSubject<boolean>(this.isAuthenticated()); // Added for authStatus$
  authStatus$ = this.authStatusSubject.asObservable();
  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Charger l'utilisateur depuis localStorage au démarrage
    const userData = localStorage.getItem(this.userKey);
    if (userData && typeof userData === 'string') {
      try {
        this.currentUser = JSON.parse(userData);
      } catch (error) {
        console.error('Error parsing user data from localStorage:', error);
        this.currentUser = null;
        localStorage.removeItem(this.userKey);
      }
    } else {
      this.currentUser = null;
    }
  }

  login(credentials: any): Observable<any> {
    return this.http.post(this.authUrl + '/signin', credentials).pipe(
      tap((response: any) => {
        this.saveToken(response.token);
        this.currentUser = response.user as User; // Stocker l'utilisateur
        localStorage.setItem(this.userKey, JSON.stringify(this.currentUser)); // Sauvegarder dans localStorage
        this.startRefreshTokenInterval();
        this.authStatusSubject.next(true); // Emit auth status change
      })
    );
  }

  signup(username: string, email: string, password: string, roles: string[] = ['USER']) {
    return this.http.post(`${this.authUrl}/signup`, {
      username,
      email,
      password,
      roles
    });
  }

  logout(): void {
    this.clearAuthData();
    this.currentUser = null; // Réinitialiser l'utilisateur
    this.authStatusSubject.next(false); // Emit auth status change
    this.router.navigate(['/login']);
  }

  refreshToken(): Observable<any> {
    if (this.refreshTokenSubject.value) {
      return of(null);
    }

    this.refreshTokenSubject.next(true);
    return this.http.post(this.authUrl + '/refresh-token', {}).pipe(
      tap((response: any) => {
        this.saveToken(response.token);
        this.refreshTokenSubject.next(false);
      }),
      catchError((error) => {
        this.refreshTokenSubject.next(false);
        this.clearAuthData();
        this.currentUser = null; // Réinitialiser l'utilisateur
        this.authStatusSubject.next(false); // Emit auth status change
        this.router.navigate(['/login']);
        throw error;
      })
    );
  }
getUsername(): string | null {
  return this.currentUser ? this.currentUser.username : null;
}
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  private clearAuthData(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey); // Supprimer les données de l'utilisateur
    this.stopRefreshTokenInterval();
  }

  private startRefreshTokenInterval(): void {
    this.stopRefreshTokenInterval();
    this.refreshTokenInterval = setInterval(() => {
      this.refreshToken().subscribe();
    }, 120 * 60 * 1000); // Refresh every 4 minutes
  }

  private stopRefreshTokenInterval(): void {
    if (this.refreshTokenInterval) {
      clearInterval(this.refreshTokenInterval);
      this.refreshTokenInterval = null;
    }
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // Ajouter la méthode isLoggedIn
  isLoggedIn(): boolean {
    return this.isAuthenticated();
  }

  getCurrentUser(): User | null {
    // Try to get the latest user from localStorage first
    const userData = localStorage.getItem(this.userKey);
    if (userData && typeof userData === 'string') {
      try {
        this.currentUser = JSON.parse(userData);
      } catch (error) {
        console.error('Error parsing user data from localStorage:', error);
        this.currentUser = null;
        localStorage.removeItem(this.userKey);
      }
    }

    // Log the user for debugging
    console.log('getCurrentUser() returning:', this.currentUser);

    return this.currentUser;
  }

  activateAccount(token: string): Observable<string> {
    return this.http.get(`${this.authUrl}/activate-account`, {
      params: { token },
      responseType: 'text'
    });
  }

  resetPasswordRequest(email: string): Observable<string> {
    return this.http.post(`${this.authUrl}/reset-password-request`, null, {
      params: { email },
      responseType: 'text'
    });
  }

  validateResetToken(token: string): Observable<string> {
    return this.http.get(`${this.authUrl}/validate-reset-token`, {
      params: { token },
      responseType: 'text'
    });
  }

  resetPassword(token: string, newPassword: string): Observable<string> {
    return this.http.post(`${this.authUrl}/reset-password`, {
      token,
      newPassword
    }, {
      responseType: 'text'
    });
  }
}
