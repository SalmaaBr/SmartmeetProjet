import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class EventLikeService {
  private apiUrl = 'http://localhost:8082/api/event-likes'; // Backend URL

  constructor(private http: HttpClient) {}

  // Toggle like for an event
  toggleLike(eventId: number): Observable<string> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${localStorage.getItem('auth_token')}`, // Ensure token key matches
      'Content-Type': 'application/json',
    });
    return this.http
      .post<string>(`${this.apiUrl}/toggle/${eventId}`, {}, { headers })
      .pipe(catchError(this.handleError));
  }

  // Get like status for an event (0 = unliked, 1 = liked)
  getLikeStatus(eventId: number): Observable<number> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${localStorage.getItem('auth_token')}`,
    });
    return this.http
      .get<number>(`${this.apiUrl}/status/${eventId}`, { headers })
      .pipe(catchError(this.handleError));
  }

  // Get total likes for an event
  getTotalLikes(eventId: number): Observable<number> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${localStorage.getItem('auth_token')}`,
    });
    return this.http
      .get<number>(`${this.apiUrl}/total/${eventId}`, { headers })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred. Please try again.';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = error.error.message || `Server Error: Code ${error.status}, Message: ${error.message}`;
      console.error('Backend Error:', error); // Log full error for debugging
    }
    return throwError(() => new Error(errorMessage));
  }
}