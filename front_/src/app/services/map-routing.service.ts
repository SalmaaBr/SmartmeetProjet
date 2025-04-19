import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MapRoutingService {
  private apiUrl = 'http://localhost:8082/api/map/route';

  constructor(private http: HttpClient) {}

  getRoute(start: [number, number], end: [number, number]): Observable<any> {
    const body = {
      coordinates: [start, end],
      instructions: false
    };
    return this.http.post<any>(this.apiUrl, body);
  }
}
