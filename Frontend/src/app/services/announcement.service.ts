import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Announcement } from '../models/announcement.model';


@Injectable({ providedIn: 'root' })
export class AnnouncementService {
  private apiUrl = 'http://localhost:8082/announcements';

  constructor(private http: HttpClient) {}

  getAllAnnouncements(): Observable<Announcement[]> {
    return this.http.get<Announcement[]>(this.apiUrl);
  }

  getAnnouncementById(id: number): Observable<any> {
    return this.http.get<Announcement>(`${this.apiUrl}/${id}`);
  }

  createAnnouncement(announcement: Announcement, eventId: number): Observable<Announcement> {
    return this.http.post<Announcement>(`${this.apiUrl}/event/${eventId}`, announcement);
}

  updateAnnouncement(id: number, announcement: Announcement): Observable<any> {
    return this.http.put<Announcement>(`${this.apiUrl}/${id}`, announcement);
  }

  deleteAnnouncement(id: number): any {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}