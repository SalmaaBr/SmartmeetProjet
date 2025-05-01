import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EventLikeService {
  private apiUrl = 'http://localhost:8082/api/event-likes'; // Hardcoded backend URL

  constructor(private http: HttpClient) {}

  // Toggle like for an event
  toggleLike(eventId: number): Observable<string> {
    const token = localStorage.getItem('jwt_token'); // Use consistent token key
    const headers = token
      ? new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        })
      : new HttpHeaders({ 'Content-Type': 'application/json' });

    return this.http
      .post<{ data: string }>(`${this.apiUrl}/toggle/${eventId}`, {}, { headers })
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get like status for an event (0 = unliked, 1 = liked)
  getLikeStatus(eventId: number): Observable<number> {
    const token = localStorage.getItem('jwt_token');
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();

    return this.http
      .get<{ data: number }>(`${this.apiUrl}/status/${eventId}`, { headers })
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get total likes for an event
  getTotalLikes(eventId: number): Observable<number> {
    const token = localStorage.getItem('jwt_token');
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();

    return this.http
      .get<{ data: number }>(`${this.apiUrl}/total/${eventId}`, { headers })
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred. Please try again.';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      // Server-side error
      if (error.status === 401) {
        errorMessage = 'Unauthorized: Please log in';
      } else if (error.status === 400) {
        errorMessage = error.error?.error || 'Bad request';
      } else if (error.status === 500) {
        errorMessage = error.error?.error || `Server Error: Code ${error.status}`;
      } else {
        errorMessage = error.error?.error || `Server Error: Code ${error.status}, Message: ${error.message}`;
      }
      console.error('Backend Error:', error); // Log full error for debugging
    }
    return throwError(() => new Error(errorMessage));
  }
}