import { Injectable } from '@angular/core';
import { HttpClient,HttpHeaders } from '@angular/common/http';
import { Observable,throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { TypeUserRole, User } from '../models/user.model';
import { AuthService } from 'src/app/auth/auth.service'
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8082/api/users';

  constructor(private http: HttpClient,private authService: AuthService) { }

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
  getAvailableUsers(): Observable<User[]> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('No token found. Please log in again.'));
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<User[]>(`${this.apiUrl}/available`, { headers }).pipe(
      catchError(this.handleError)
    );
  }
  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    let errorMessage = 'An error occurred. Please try again.';
    if (error.status === 401) {
      errorMessage = 'Session expired. Please log in again.';
    } else if (error.error?.error) {
      errorMessage = error.error.error;
    }
    return throwError(() => new Error(errorMessage));
  }
}
