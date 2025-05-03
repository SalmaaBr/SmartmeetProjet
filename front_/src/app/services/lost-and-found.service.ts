import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { LostAndFoundRequest, LostAndFoundResponse, Notification } from '../models/lost-and-found.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LostAndFoundService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Get all available events for the current user
  getAllEvents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/event/my-events`)
      .pipe(catchError(this.handleError));
  }

  // Create a new lost and found item
  createLostAndFound(request: LostAndFoundRequest): Observable<LostAndFoundResponse> {
    return this.http.post<LostAndFoundResponse>(`${this.baseUrl}/lost-found`, request)
      .pipe(catchError(this.handleError));
  }

  // Get all lost and found items for an event
  getLostAndFoundByEvent(eventId: number): Observable<LostAndFoundResponse[]> {
    return this.http.get<LostAndFoundResponse[]>(`${this.baseUrl}/lost-found/event/${eventId}`)
      .pipe(catchError(this.handleError));
  }

  // Get a single lost and found item by ID
  getLostAndFoundById(id: number): Observable<LostAndFoundResponse> {
    return this.http.get<LostAndFoundResponse>(`${this.baseUrl}/lost-found/${id}`)
      .pipe(catchError(this.handleError));
  }

  // Update a lost and found item
  updateLostAndFound(id: number, request: LostAndFoundRequest): Observable<LostAndFoundResponse> {
    return this.http.put<LostAndFoundResponse>(`${this.baseUrl}/lost-found/${id}`, request)
      .pipe(catchError(this.handleError));
  }

  // Delete a lost and found item
  deleteLostAndFound(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/lost-found/${id}`)
      .pipe(catchError(this.handleError));
  }

  // Upload an image for a lost and found item
  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);

    // Use responseType: 'text' to handle text responses
    return this.http.post(`${this.baseUrl}/api/upload/lostfound`, formData, { responseType: 'text' })
      .pipe(
        map(response => response as string),
        catchError(this.handleError)
      );
  }

  // Get all notifications for the current user
  getUserNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.baseUrl}/lost-found/notifications`)
      .pipe(catchError(this.handleError));
  }

  // Mark a notification as read
  markNotificationAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/lost-found/notifications/${id}/read`, {})
      .pipe(catchError(this.handleError));
  }

  // Error handling method
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;

      if (error.error && typeof error.error === 'string') {
        errorMessage = error.error;
      } else if (error.error && error.error.message) {
        errorMessage = error.error.message;
      }
    }

    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
