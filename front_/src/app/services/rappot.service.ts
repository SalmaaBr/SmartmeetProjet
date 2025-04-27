import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RapportService {
  private apiUrl = 'http://localhost:8082/api/meets';

  constructor(private http: HttpClient) { }

  improveText(text: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/improve-text`, { text });
  }

  generatePdf(text: string): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/generate-pdf`, { text }, { 
      responseType: 'blob' 
    });
  }
}