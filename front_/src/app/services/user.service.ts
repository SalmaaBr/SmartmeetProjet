import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TypeUserRole, User } from '../models/user.model';
import {TypeTheme} from '../models/event/createevent/createevent/event.enums';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8082/api/users';

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

  getCurrentUserInterests(): Observable<Set<TypeTheme>> {
    return this.http.get<Set<TypeTheme>>(`${this.apiUrl}/me/interests`);
  }

  getRecommendedUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/me/recommendations`);
  }

  getMeetingsByUserId(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8082/api/meetings/user/${userId}`);
  }
  
}
