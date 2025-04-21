import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MapService {
  private apiUrl = 'http://localhost:8082/event';

  constructor(private http: HttpClient) { }

  searchLocation(query: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/search-location?query=${encodeURIComponent(query)}`);
  }

  reverseGeocode(lat: number, lng: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/reverse-geocode?lat=${lat}&lon=${lng}`);
  }
}