import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Feedback, TypeFeeling, FeedbackStats } from '../models/feedback.model';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  private apiUrl = 'http://localhost:8082/FeedBack';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth-token');
    console.log('🔍 Retrieved token for request:', token ? `Bearer ${token}` : 'Missing');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  addFeedback(feedback: Feedback): Observable<Feedback> {
    console.log("🚀 Sending feedback to add:", feedback);
    return this.http.post<Feedback>(`${this.apiUrl}/Add-feedbacks`, feedback, { headers: this.getHeaders() }).pipe(
      tap(response => console.log("✅ Feedback added successfully:", response)),
      catchError(error => {
        console.error("❌ Error while adding feedback:", error);
        return throwError(() => new Error('Error adding feedback'));
      })
    );
  }

  addFeedbackAndAffectToEvent(feedback: Feedback, eventId: number, userId: number): Observable<Feedback> {
    console.log("🚀 Sending feedback to add and affect to event:", feedback, "Event ID:", eventId, "User ID:", userId);
    return this.http.post<Feedback>(
      `${this.apiUrl}/add-feedback-and-affect-to-event/${eventId}/${userId}`,
      feedback,
      { headers: this.getHeaders() }
    ).pipe(
      tap(response => {
        console.log("✅ Feedback added and affected to event:", response);
        if (!response.eventTitle) {
          console.warn("⚠️ EventTitle is null or undefined in response");
        }
      }),
      catchError(error => {
        console.error("❌ Error while adding feedback to event:", error);
        return throwError(() => new Error('Error adding feedback to event'));
      })
    );
  }

  updateFeedback(feedback: Feedback, eventId: number, userId: number): Observable<Feedback> {
    console.log("🚀 Sending feedback to add/update for event:", feedback, "Event ID:", eventId, "User ID:", userId);
    return this.http.post<Feedback>(
      `${this.apiUrl}/add-feedback-and-affect-to-event/${eventId}/${userId}`,
      feedback,
      { headers: this.getHeaders() }
    ).pipe(
      tap(response => console.log("✅ Feedback added/updated successfully:", response)),
      catchError(error => {
        console.error("❌ Error while adding/updating feedback:", error);
        if (error.status === 401) {
          console.error("🔐 401 Unauthorized: Check token validity, eventId, userId, or endpoint authorization");
          console.error("🔍 Request details:", { eventId, userId, feedback });
        }
        return throwError(() => new Error('Error adding/updating feedback'));
      })
    );
  }

  deleteFeedback(id: number): Observable<void> {
    console.log("🚀 Deleting feedback ID:", id);
    return this.http.delete<void>(`${this.apiUrl}/Delete-feedbacks/${id}`, { headers: this.getHeaders() }).pipe(
      catchError(error => {
        console.error("❌ Error while deleting feedback:", error);
        return throwError(() => new Error('Error deleting feedback'));
      })
    );
  }

  getFeedbackById(id: number): Observable<Feedback> {
    console.log("🚀 Fetching feedback ID:", id);
    return this.http.get<Feedback>(`${this.apiUrl}/Get-feedbacks/${id}`, { headers: this.getHeaders() }).pipe(
      catchError(error => {
        console.error("❌ Error while fetching feedback:", error);
        return throwError(() => new Error('Error fetching feedback'));
      })
    );
  }

  getAllFeedbacks(): Observable<Feedback[]> {
    console.log("🚀 Fetching all feedbacks");
    return this.http.get<Feedback[]>(`${this.apiUrl}/Get-all-feedbacks`, { headers: this.getHeaders() }).pipe(
      catchError(error => {
        console.error("❌ Error while fetching all feedbacks:", error);
        return throwError(() => new Error('Error fetching all feedbacks'));
      })
    );
  }

  getFeedbacksByEventTitle(eventTitle?: string): Observable<Feedback[]> {
    const url = eventTitle ? `${this.apiUrl}/filtered?eventTitle=${encodeURIComponent(eventTitle)}` : `${this.apiUrl}/filtered`;
    console.log("🚀 Fetching feedbacks for event:", eventTitle || 'all');
    return this.http.get<Feedback[]>(url, { headers: this.getHeaders() }).pipe(
      tap(response => console.log("✅ Filtered feedbacks:", response)),
      catchError(error => {
        console.error("❌ Error fetching filtered feedbacks:", error);
        return throwError(() => new Error('Error fetching filtered feedbacks'));
      })
    );
  }

  getSentimentStatistics(eventTitle?: string): Observable<FeedbackStats> {
    const url = eventTitle ? `${this.apiUrl}/sentiment-statistics?eventTitle=${encodeURIComponent(eventTitle)}` : `${this.apiUrl}/sentiment-statistics`;
    console.log("🚀 Fetching sentiment statistics for event:", eventTitle || 'all');
    return this.http.get<FeedbackStats>(url, { headers: this.getHeaders() }).pipe(
      tap(response => console.log("✅ Sentiment statistics:", response)),
      catchError(error => {
        console.error("❌ Error fetching sentiment statistics:", error);
        return throwError(() => new Error('Error fetching sentiment statistics'));
      })
    );
  }
}
