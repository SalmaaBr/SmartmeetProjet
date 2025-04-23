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
  latitude: number;
  longitude:number;
  typeweather: string;
  startTime: string;
  endTime: string;
  maxParticipants: number;
  filePath?: string;  // Ajout de la propriété filePath
  imageUrl?: string;  // Ajout de la propriété imageUrl
  isLiked?: boolean; // Add isLiked
  totalLikes?: number;
  likes?:number;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8082/event';
  private apiUrll = 'http://localhost:8082';
  private notificationsUrl = 'http://localhost:8082/notifications'; // URL de l'API pour les notifications


  constructor(private http: HttpClient) { }

  createEvent(event: any, file?: File): Observable<Event> {
    const formData: FormData = new FormData();
  
    formData.append('event', new Blob([JSON.stringify({
      ...event,
      location: `${event.location} (${event.latitude},${event.longitude})`
    })], {
      type: 'application/json'
    }));
  
    // Ajouter le fichier uniquement s'il existe
    if (file) {
      formData.append('file', file);
    }
  
    return this.http.post<Event>(`${this.apiUrl}/createevent`, formData);
  }
  

getEvents(): Observable<Event[]> {
  return this.http.get<Event[]>(`${this.apiUrl}/getallevent`).pipe(
    map(events => events.map(event => {
      if (event.filePath) {
        event.imageUrl = `${this.apiUrl}/images/${event.filePath}`;
      }
      return event;
    }))
  );
}
  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deleteevent/${id}`);
  }

  updateEventWithImage(id: number, event: any, file: File | null): Observable<Event> {
    const formData: FormData = new FormData();
    formData.append('event', new Blob([JSON.stringify(event)], {
      type: 'application/json'
    }));
    if (file) {
      formData.append('file', file);
    }

    return this.http.put<Event>(`${this.apiUrl}/updateevent/${id}`, formData).pipe(
      tap(() => {
        // Ajouter la notification après la mise à jour de l'événement
        this.addNotification(id);
      })
    );
  }

  addNotification(eventId: number): void {
    const notification: any = {
      message: `L'événement avec l'ID ${eventId} a été mis à jour.`,
      timestamp: new Date().toISOString(), // Vous pouvez utiliser le format de date que vous préférez
      event: { id: eventId }
    };

    this.http.post('http://localhost:8082/notifications/add', notification)
      .subscribe(
        response => {
          console.log('Notification ajoutée avec succès', response);
        },
        error => {
          console.error('Erreur lors de l\'ajout de la notification', error);
        }
      );
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
//aadandAsignEventToUser
participateToEvent(eventId: number): Observable<{message: string, maxParticipants: number}> {
  return this.http.post<{message: string, maxParticipants: number}>(`${this.apiUrl}/evenements/${eventId}/participer`, {});
}

getNotifications(): Observable<any[]> {
  return this.http.get<any[]>(this.notificationsUrl); // Appel à l'API des notifications
}

getMyEvents(): Observable<Event[]> {
  return this.http.get<Event[]>(`${this.apiUrl}/my-events`).pipe(
    map(events => events.map(event => {
      if (event.filePath) {
        event.imageUrl = `${this.apiUrl}/images/${event.filePath}`;
      }
      return event;
    }))
  );
}


}
