// src/app/services/event-like.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EventLikeService {
  private apiUrl = 'http://localhost:8082/api/event-likes'; // Adjust to your backend URL

  constructor(private http: HttpClient) {}

  // Toggle like for an event
  toggleLike(eventId: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/toggle/${eventId}`, {}).pipe(
      catchError(this.handleError)
    );
  }

// Get like status for an event (0 = unliked, 1 = liked)
getLikeStatus(eventId: number): Observable<number> {
  return this.http.get<number>(`${this.apiUrl}/status/${eventId}`).pipe(
    catchError(this.handleError)
  );
}


  // Get total likes for an event
  getTotalLikes(eventId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/total/${eventId}`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Une erreur s\'est produite. Veuillez réessayer.';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = error.error.message || `Code: ${error.status}, Message: ${error.message}`;
    }
    return throwError(() => new Error(errorMessage));
  }
}
