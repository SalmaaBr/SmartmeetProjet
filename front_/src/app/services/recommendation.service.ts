// recommendation.service.ts
import { Injectable } from '@angular/core';
import { HttpClient ,HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';



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

}

@Injectable({
  providedIn: 'root'
})
export class RecommendationService {
  private apiUrl = 'http://localhost:8082/api/recommendations';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  getRecommendationsForCurrentUser(): Observable<Event[]> {
    const token = this.authService.getToken();
    
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.get<Event[]>(`${this.apiUrl}/current-user`, { 
      headers,
      withCredentials: true // Important
    });
  }
}