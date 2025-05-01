import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authUrl = 'http://localhost:8082/api/auth';
  private tokenKey = 'auth_token';
  private refreshTokenInterval: any;
  private refreshTokenSubject = new BehaviorSubject<boolean>(false);

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post(`${this.authUrl}/signin`, credentials).pipe(
      tap((response: any) => {
        this.saveToken(response.token);
        this.startRefreshTokenInterval();
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
    this.router.navigate(['/login']);
  }

  refreshToken(): Observable<any> {
    if (this.refreshTokenSubject.value) {
      return of(null);
    }

    this.refreshTokenSubject.next(true);
    return this.http.post(`${this.authUrl}/refresh-token`, {}).pipe(
      tap((response: any) => {
        this.saveToken(response.token);
        this.refreshTokenSubject.next(false);
      }),
      catchError((error) => {
        this.refreshTokenSubject.next(false);
        this.clearAuthData();
        this.router.navigate(['/login']);
        throw error;
      })
    );
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
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
    return this.http.post(`${this.authUrl}/reset-password`, { token, newPassword }, {
      responseType: 'text'
    });
  }

  getCurrentUserId(): number {
    const userData = localStorage.getItem('currentUser');
    if (userData) {
      const user = JSON.parse(userData);
      return user.userID || 0;
    }
    return 0;
  }

  private clearAuthData(): void {
    this.clearToken();
    this.stopRefreshTokenInterval();
  }

  private startRefreshTokenInterval(): void {
    this.stopRefreshTokenInterval(); // Clear any existing interval
    this.refreshTokenInterval = setInterval(() => {
      this.refreshToken().subscribe();
    }, 4 * 60 * 1000); // every 4 minutes
  }

  private stopRefreshTokenInterval(): void {
    if (this.refreshTokenInterval) {
      clearInterval(this.refreshTokenInterval);
      this.refreshTokenInterval = null;
    }
  }
}
