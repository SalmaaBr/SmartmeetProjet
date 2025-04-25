import { Injectable } from '@angular/core';
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { filter, map, Observable } from 'rxjs';
import { RequestBuilder } from './request-builder';
import { StrictHttpResponse } from './strict-http-response';
export interface Confirm$Params {
  token: string;
}
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authUrl = 'http://localhost:8082/api/auth';

  constructor(private http: HttpClient) {}

  login(credentials: { username: string; password: string }): Observable<any> {
    // Ajout de l'URL complète avec une meilleure gestion de l'API
    const url = `${this.authUrl}/signin`;

    // Envoi de la requête POST avec les informations d'identification
    return this.http.post<any>(url, credentials);
  }


  // Méthode pour l'inscription
  signup(username: string, email: string, password: string, roles: string[] = ['USER']) {
    return this.http.post(`${this.authUrl}/signup`, {
      username,
      email,
      password,
      roles
    });
  }

  saveToken(token: string): void {
    localStorage.setItem('auth_token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  clearToken(): void {
    localStorage.removeItem('auth_token');
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
    return this.http.post(`${this.authUrl}/reset-password`,
      { token, newPassword },
      { responseType: 'text' }
    );
  }

  getCurrentUserId(): number {
    // Implémentation basique - à adapter selon votre système d'authentification
    // Par exemple, si vous stockez les infos utilisateur dans le localStorage
    const userData = localStorage.getItem('currentUser');
    if (userData) {
      const user = JSON.parse(userData);
      return user.userID || 0; // Retourne l'ID ou 0 si non trouvé
    }
    return 0; // Retourne 0 si non connecté
  }
  
}
