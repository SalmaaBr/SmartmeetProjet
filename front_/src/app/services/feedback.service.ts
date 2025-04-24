import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Feedback } from '../models/feedback.model';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  private apiUrl = 'http://localhost:8082/FeedBack';

  constructor(private http: HttpClient) {}

  // Headers avec token pour l'authentification
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Ajouter un feedback sans association explicite
  addFeedback(feedback: Feedback): Observable<Feedback> {
    console.log("🚀 Sending feedback:", feedback);
    return this.http.post<Feedback>(`${this.apiUrl}/Add-feedbacks`, feedback, { headers: this.getHeaders() }).pipe(
      tap(response => console.log("✅ Feedback added successfully:", response)),
      catchError(error => {
        console.error("❌ Error while adding feedback:", error);
        return throwError(error);
      })
    );
  }

  // Ajouter un feedback et l’associer à un événement
  addFeedbackAndAffectToEvent(feedback: Feedback, eventId: number): Observable<Feedback> {
    console.log("🚀 Sending feedback for event ID:", eventId, feedback);
    return this.http.post<Feedback>(
      `${this.apiUrl}/add-feedback-and-affect-to-event/${eventId}`,
      feedback,
      { headers: this.getHeaders() }
    ).pipe(
      tap(response => console.log("✅ Feedback added and affected to event:", response)),
      catchError(error => {
        console.error("❌ Error while adding feedback to event:", error);
        return throwError(error);
      })
    );
  }

  // Mettre à jour un feedback avec association à un événement
  updateFeedback(id: number, feedback: Feedback, eventId: number): Observable<Feedback> {
    console.log("🚀 Updating feedback ID:", id, "for event ID:", eventId, feedback);
    return this.http.put<Feedback>(
      `${this.apiUrl}/Update-feedbacks/${id}/event/${eventId}`, // Ajusté pour inclure eventId
      feedback,
      { headers: this.getHeaders() }
    ).pipe(
      tap(response => console.log("✅ Feedback updated successfully:", response)),
      catchError(error => {
        console.error("❌ Error while updating feedback:", error);
        return throwError(error);
      })
    );
  }

  // Supprimer un feedback
  deleteFeedback(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/Delete-feedbacks/${id}`, { headers: this.getHeaders() }).pipe(
      catchError(error => {
        console.error("❌ Error while deleting feedback:", error);
        return throwError(error);
      })
    );
  }

  // Récupérer un feedback par ID
  getFeedbackById(id: number): Observable<Feedback> {
    return this.http.get<Feedback>(`${this.apiUrl}/Get-feedbacks/${id}`, { headers: this.getHeaders() }).pipe(
      catchError(error => {
        console.error("❌ Error while fetching feedback:", error);
        return throwError(error);
      })
    );
  }

  // Récupérer tous les feedbacks
  getAllFeedbacks(): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/Get-all-feedbacks`, { headers: this.getHeaders() }).pipe(
      catchError(error => {
        console.error("❌ Error while fetching all feedbacks:", error);
        return throwError(error);
      })
    );
  }
}
