import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SponsorStatsService {

  private apiUrl = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) { }

  getEventStats(eventId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/sponsor-stats/event/${eventId}`);
  }

  getContracts(eventId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/contracts/event/${eventId}`);
  }
}
