// src/app/services/mental-health.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { MentalHealth } from '../models/mental-health';

@Injectable({
  providedIn: 'root'
})
export class MentalHealthService {
  private apiUrl = 'http://localhost:8082/MentalHealth'; // URL de ton backend

  constructor(private http: HttpClient) {}

  // Headers avec token pour l'authentification
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Ajouter un MentalHealth
  addMentalHealth(mentalHealth: MentalHealth): Observable<MentalHealth> {
    console.log("🚀 Sending mental health:", mentalHealth);
    return this.http.post<MentalHealth>(`${this.apiUrl}/add-mentalhealth`, mentalHealth, { headers: this.getHeaders() }).pipe(
      tap(response => console.log("✅ MentalHealth added successfully:", response)),
      catchError(error => {
        console.error("❌ Error while adding mental health:", error);
        return throwError(error);
      })
    );
  }

  // Mettre à jour un MentalHealth
  updateMentalHealth(mentalHealth: MentalHealth): Observable<MentalHealth> {
    console.log("🚀 Updating mental health:", mentalHealth);
    const url = `${this.apiUrl}/update-mentalhealth/${mentalHealth.idMentalHealth}`;
    return this.http.put<MentalHealth>(url, mentalHealth, { headers: this.getHeaders() }).pipe(
      tap(response => console.log("✅ MentalHealth updated successfully:", response)),
      catchError(error => {
        console.error("❌ Error while updating mental health:", error);
        return throwError(error);
      })
    );
  }

  // Supprimer un MentalHealth
  deleteMentalHealth(id: number): Observable<void> {
    console.log("🚀 Deleting mental health ID:", id);
    return this.http.delete<void>(`${this.apiUrl}/delete-mentalhealth/${id}`, { headers: this.getHeaders() }).pipe(
      tap(() => console.log("✅ MentalHealth deleted successfully")),
      catchError(error => {
        console.error("❌ Error while deleting mental health:", error);
        return throwError(error);
      })
    );
  }

  // Récupérer un MentalHealth par ID
  getMentalHealthById(id: number): Observable<MentalHealth> {
    console.log("🚀 Fetching mental health ID:", id);
    return this.http.get<MentalHealth>(`${this.apiUrl}/get-mentalhealth/${id}`, { headers: this.getHeaders() }).pipe(
      tap(response => console.log("✅ MentalHealth fetched successfully:", response)),
      catchError(error => {
        console.error("❌ Error while fetching mental health:", error);
        return throwError(error);
      })
    );
  }

  // Récupérer tous les MentalHealths
  getAllMentalHealths(): Observable<MentalHealth[]> {
    console.log("🚀 Fetching all mental health records");
    return this.http.get<MentalHealth[]>(`${this.apiUrl}/get-all-mentalhealths`, { headers: this.getHeaders() }).pipe(
      tap(response => console.log("✅ All MentalHealths fetched successfully:", response)),
      catchError(error => {
        console.error("❌ Error while fetching all mental health records:", error);
        return throwError(error);
      })
    );
  }


}
