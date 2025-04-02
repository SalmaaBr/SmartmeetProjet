import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators'; // Ajoutez 'tap' ici


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
  filePath?: string;  // Ajout de la propriété filePath
  imageUrl?: string;  // Ajout de la propriété imageUrl

}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8082/event';

  constructor(private http: HttpClient) { }

  createEvent(event: any, file: File): Observable<Event> {
    const formData: FormData = new FormData();
    formData.append('event', new Blob([JSON.stringify(event)], { 
        type: 'application/json' 
    }));
    formData.append('file', file);
    
    return this.http.post<Event>(`${this.apiUrl}/createevent`, formData);
}

getEvents(): Observable<Event[]> {
  return this.http.get<Event[]>(`${this.apiUrl}/getallevent`).pipe(
    tap(events => console.log("Données brutes reçues :", events)),
    map(events => events.map(event => {
      if (event.filePath) {
        const url = `${this.apiUrl}/images/${event.filePath}`;
        console.log("URL construite pour", event.filePath, ":", url);
        event.imageUrl = url;
      }
      return event;
    })),
    tap(events => console.log("Événements transformés :", events))
  );
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
  
  getImage(fileName: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/images/${fileName}`, { responseType: 'blob' });
}

// Dans event.service.ts
// Ajoutez cette méthode
checkEventHasRecruitment(title: string): Observable<boolean> {
  return this.http.get<boolean>(`${this.apiUrl}/check-recruitment/${title}`);
}

}