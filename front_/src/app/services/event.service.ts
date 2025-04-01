import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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

  createEvent(event: any, file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('event', new Blob([JSON.stringify(event)], { type: 'application/json' }));
    formData.append('file', file);

    return this.http.post(`${this.apiUrl}/createevent`, formData);
}

getEvents(): Observable<Event[]> {
  return this.http.get<Event[]>(`${this.apiUrl}/getallevent`).pipe(
    map(events => events.map(event => {
      if (event.filePath) {
        // Récupération du nom de fichier uniquement
        const fileName = event.filePath.split('\\').pop()?.split('/').pop();
        if (fileName) {
          event.imageUrl = `${this.apiUrl}/image/${fileName}`;
        }
      }
      return event;
    }))
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
    return this.http.get(`${this.apiUrl}/image/${fileName}`, { responseType: 'blob' });
}

  

}