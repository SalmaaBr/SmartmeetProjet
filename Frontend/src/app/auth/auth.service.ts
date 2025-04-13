import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
}
