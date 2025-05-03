import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { TypeUserRole, User } from '../models/user.model';
import { TypeTheme } from '../models/event/createevent/createevent/event.enums';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/api/users`;
  private profileImageUpdated = new Subject<void>();

  profileImageUpdate$ = this.profileImageUpdated.asObservable();

  constructor(private http: HttpClient) { }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  getUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/by-email`, {
      params: { email }
    });
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  updateUser(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getRoles(): TypeUserRole[] {
    return ['USER', 'ADMIN', 'PARTICIPANT', 'SPEAKER', 'TRAINER', 'SPONSOR', 'COMPANY'];
  }

  getInterests(): string[] {
    return Object.values(TypeTheme);
  }

  // Ajoutez cette nouvelle méthode pour l'utilisateur connecté

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  uploadProfileImage(userId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{imageUrl: string}>(`${this.apiUrl}/upload-profile-image/${userId}`, formData).pipe(
      map(response => {
        // Notify subscribers that the profile image has been updated
        this.profileImageUpdated.next();
        return response;
      })
    );
  }

  getProfileImage(userId: number): Observable<string> {
    const timestamp = new Date().getTime();
    return this.http.get(`${this.apiUrl}/profile-image/${userId}?t=${timestamp}`, {
      responseType: 'blob'
    }).pipe(
      map(blob => {
        const objectUrl = URL.createObjectURL(blob);
        return objectUrl;
      })
    );
  }
}
