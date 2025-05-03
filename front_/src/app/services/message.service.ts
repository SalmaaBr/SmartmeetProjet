import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Message } from '../models/message.model';
import { AuthService } from 'src/app/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private apiUrl = 'http://localhost:8082/messages';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  sendMessage(receiverUsername: string, content: string, documentIds: number[]): Observable<Message> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('No token found. Please log in again.'));
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const body = { receiverUsername, content, documentIds };
    return this.http.post<Message>(`${this.apiUrl}/send`, body, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  getInbox(): Observable<Message[]> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('No token found. Please log in again.'));
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Message[]>(`${this.apiUrl}/inbox`, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  getSentMessages(): Observable<Message[]> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('No token found. Please log in again.'));
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Message[]>(`${this.apiUrl}/sent`, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  markAsRead(messageId: number): Observable<void> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('No token found. Please log in again.'));
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.post<void>(`${this.apiUrl}/${messageId}/read`, null, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  getUnreadMessageCount(): Observable<number> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('No token found. Please log in again.'));
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<number>(`${this.apiUrl}/unread-count`, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    let errorMessage = 'An error occurred. Please try again.';
    if (error.status === 401) {
      errorMessage = 'Session expired. Please log in again.';
    } else if (error.error?.error) {
      errorMessage = error.error.error;
    }
    return throwError(() => new Error(errorMessage));
  }
}