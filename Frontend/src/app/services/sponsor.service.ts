import { Injectable } from '@angular/core';
import { Sponsor } from '../models/sponsor.model';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SponsorService {
  private apiUrl = 'http://localhost:8082/sponsors';

  constructor(private http: HttpClient) {}

  getAllSponsors(): Observable<Sponsor[]> {
    return this.http.get<Sponsor[]>(this.apiUrl);
  }

  getSponsorById(id: number): Observable<any> {
    return this.http.get<Sponsor>(`${this.apiUrl}/${id}`);
  }

  createSponsor(sponsor: Sponsor): Observable<Sponsor> {
    return this.http.post<Sponsor>(this.apiUrl, sponsor);
  }

  updateSponsor(id: number, sponsor: Sponsor): Observable<any> {
    return this.http.put<Sponsor>(`${this.apiUrl}/${id}`, sponsor);
  }

  deleteSponsor(id: number): any {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  addEventToSponsor(sponsorId: number, eventId: number): any {
    return this.http.post<void>(`${this.apiUrl}/${sponsorId}/events/${eventId}`, {});
  }
}