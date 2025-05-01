import { Injectable } from '@angular/core';
import { Reservation } from '../models/reservation.model';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MaintenancePeriod {
  startDate: string;
  endDate: string;
}

export interface AffectedReservation {
  reservationId: number;
  userId: number;
  userName: string;
  startTime: string;
  endTime: string;
  resourceId: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = 'http://localhost:8082/api/resource-reservations';
  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  getResourceTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/types`, { headers: this.getHeaders() });
  }

  getResourceStatuses(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/statuses`, { headers: this.getHeaders() });
  }

  getAllResourceReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.apiUrl);
  }

  createReservation(reservation: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, reservation, { headers: this.getHeaders() });
  }

  getResourceReservationById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  updateReservation(id: number, reservation: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, reservation, { headers: this.getHeaders() });
  }

  deleteReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getReservedDatesForResource(resourceId: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/reserved-dates/${resourceId}`);
  }

  isDateRangeAvailable(resourceId: number, startDate: Date, endDate: Date): Observable<boolean> {
    const params = new HttpParams()
      .set('resourceId', resourceId.toString())
      .set('startDate', startDate.toISOString().split('T')[0])
      .set('endDate', endDate.toISOString().split('T')[0]);

    return this.http.get<boolean>(`${this.apiUrl}/check-availability`, { params });
  }

  // Maintenance-related methods
  getMaintenancePeriods(resourceId: number, months: number = 12): Observable<MaintenancePeriod[]> {
    const params = new HttpParams()
      .set('months', months.toString());

    return this.http.get<MaintenancePeriod[]>(
      `${this.apiUrl}/maintenance-periods/${resourceId}`,
      {
        params,
        headers: this.getHeaders(),
        withCredentials: true
      }
    );
  }

  checkMaintenanceOverlap(reservationId: number): Observable<boolean> {
    return this.http.get<boolean>(
      `${this.apiUrl}/check-maintenance-overlap/${reservationId}`,
      {
        headers: this.getHeaders(),
        withCredentials: true
      }
    );
  }

  // Method to get reservations that conflict with maintenance periods
  getConflictingReservations(resourceId: number): Observable<AffectedReservation[]> {
    return this.http.get<AffectedReservation[]>(
      `${this.apiUrl}/conflicting-reservations/${resourceId}`,
      {
        headers: this.getHeaders(),
        withCredentials: true
      }
    );
  }
}
