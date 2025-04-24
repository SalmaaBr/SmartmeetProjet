import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Event {
  id: number;
  typeevent: string;
  typetheme: string;
  title: string;
  description: string;
  location: string;
  typeweather: string;
  startTime: string;
  endTime: string;
  maxParticipants: number;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8082/event';

  constructor(private http: HttpClient) { }

  createEvent(event: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/createevent`, event);
  }

  getEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/getallevent`);
  }


  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deleteevent/${id}`);
  }

  updateEvent(id: number, event: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/updateevent/${id}`, event);
  }

  // Méthode pour récupérer un événement par son ID
  getEventById(id: number): Observable<Event> {
      return this.http.get<Event>(`${this.apiUrl}/getevent/${id}`);
  }



}
