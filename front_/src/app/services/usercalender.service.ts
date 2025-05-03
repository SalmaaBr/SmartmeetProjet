import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EventUserCalendar {
  id?: number;
  name: string;
  createdDate: string;
  startDate: string;
  endDate?: string;
  // Add other properties as needed
}

@Injectable({
  providedIn: 'root'
})
export class UsercalenderService {
  private apiUrl = 'http://localhost:8082/usercalendar'; // Backend URL

  constructor(private http: HttpClient) {}

  addEvent(event: EventUserCalendar): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    return this.http.post(`${this.apiUrl}/addeventcalendar`, event, { headers });
  }

  getUserEventsCalender(): Observable<EventUserCalendar[]> {
    const token = localStorage.getItem('jwt_token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<EventUserCalendar[]>(`${this.apiUrl}/events`, { headers });
  }
}