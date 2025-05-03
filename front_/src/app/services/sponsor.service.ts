import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sponsor } from '../models/sponsor.model';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SponsorService {
  private apiUrl = `${environment.apiUrl}/api/sponsors`;
  private sponsorEventUrl = `${environment.apiUrl}/api/sponsor-event`;

  constructor(private http: HttpClient) { }

  getAllSponsors(): Observable<Sponsor[]> {
    return this.http.get<Sponsor[]>(this.apiUrl);
  }

  getSponsorById(id: number): Observable<Sponsor> {
    return this.http.get<Sponsor>(`${this.apiUrl}/${id}`);
  }

  createSponsor(sponsor: Sponsor): Observable<Sponsor> {
    return this.http.post<Sponsor>(`${this.sponsorEventUrl}/sponsors?responsibleUserId=${sponsor.responsibleUserId}`, sponsor);
  }

  updateSponsor(sponsor: Sponsor): Observable<Sponsor> {
    return this.http.put<Sponsor>(`${this.sponsorEventUrl}/sponsors/${sponsor.idSponsor}?responsibleUserId=${sponsor.responsibleUserId}`, sponsor);
  }

  deleteSponsor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getUsersWithSponsorRole(): Observable<User[]> {
    return this.http.get<User[]>(`${this.sponsorEventUrl}/users/sponsors`);
  }
} 