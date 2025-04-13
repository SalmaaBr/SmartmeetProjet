import { Injectable } from '@angular/core';
import { Reservation } from '../models/reservation.model';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';



@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = 'http://localhost:8082/api/reservations';
  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getResourceTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/types`, { headers: this.getHeaders() });
  }

  getResourceStatuses(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/statuses`, { headers: this.getHeaders() });
  }

  createResourceReservation(reservation: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/create/${reservation.resourceId}`, reservation, { headers: this.getHeaders() });
  }

  getResourceReservationById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  getAllResourceReservations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/all`, { headers: this.getHeaders() });
  }

  deleteResourceReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, { headers: this.getHeaders() });
  }

  updateResourceReservation(id: number, reservation: any): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/update/${id}`, reservation, { headers: this.getHeaders() });
  }
}
