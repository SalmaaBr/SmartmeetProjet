import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RapportService {
  private apiUrl = 'http://localhost:8082/api/rapport-meetings';

  constructor(private http: HttpClient) { }

  generateRapport(meetingId: number, rawReport: string): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/${meetingId}/generer`, rawReport, {
      responseType: 'blob'
    });
  }
}