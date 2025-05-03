import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RecommendationService {
  private apiUrl = 'http://localhost:8082/api/recommendations';

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) {}

  getRecommendationsForCurrentUser(token: string): Observable<any[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<any>(`${this.apiUrl}/current-user`, { headers }).pipe(
      map(response => response || []),
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  getRecommendationsForUser(userID: number, token: string): Observable<any[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<any[]>(`${this.apiUrl}/user/${userID}`, { headers }).pipe(
      map(response => response || []),
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  private handleError(error: HttpErrorResponse): Observable<any[]> {
    if (error.status === 401) {
      console.error('Token expiré ou invalide. Redirection vers la page de connexion.');
      this.authService.logout(); // Utiliser logout() au lieu de handleUnauthorized()
      return of([]); // Retourner une liste vide pour éviter de bloquer l'application
    }
    console.error('Erreur lors de la récupération des recommandations:', error.message);
    return throwError(() => new Error('Erreur lors de la récupération des recommandations'));
  }
}