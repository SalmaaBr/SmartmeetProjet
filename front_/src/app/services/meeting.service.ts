// meeting.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Meeting {
  id?: number;
  meetingName: string;
  meetingLink: string;
  startTime: string;
  endTime: string;
  organizer?: any;
  participant?: any;
}

@Injectable({
  providedIn: 'root'
})
export class MeetingService {
  private apiUrl = 'http://localhost:8082/api/meetings'; // Adaptez selon votre backend

  constructor(private http: HttpClient) {}

  getUserMeetings(): Observable<Meeting[]> {
    const token = localStorage.getItem('jwt_token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Meeting[]>(`${this.apiUrl}/user`, { headers });
  }
}