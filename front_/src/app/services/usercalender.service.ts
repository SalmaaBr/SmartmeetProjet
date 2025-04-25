import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EventUserCalendar {
  id?: number;
  name: string;
  createdDate: string;
  startDate: string;
  endDate?: string;
}

export interface Meeting {
  id?: number;
  meetingName: string;
  meetingLink: string;
  startTime: string;
  endTime: string;
  organizer?: { username: string };
  participant?: { username: string };
}

@Injectable({
  providedIn: 'root'
})
export class UsercalenderService {
  private apiUrl = 'http://localhost:8082'; // Base URL

  constructor(private http: HttpClient) {}

  addEvent(event: EventUserCalendar): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    return this.http.post(`${this.apiUrl}/usercalendar/addeventcalendar`, event, { headers });
  }

  getUserEventsCalender(): Observable<EventUserCalendar[]> {
    const token = localStorage.getItem('jwt_token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<EventUserCalendar[]>(`${this.apiUrl}/usercalendar/events`, { headers });
  }

  // Nouvelle méthode pour récupérer les réunions
  getUserMeetings(): Observable<Meeting[]> {
    const token = localStorage.getItem('jwt_token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Meeting[]>(`${this.apiUrl}/api/meetings/user-meetings`, { headers });
  }
}