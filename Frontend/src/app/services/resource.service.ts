import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Resource {
  id?: number;
  name: string;
  typeResource: string;
  typeResourceStatus: string;
}

@Injectable({
  providedIn: 'root'
})
export class ResourceService {
  private apiUrl = 'http://localhost:8082/api/resources'; // Update if needed

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth_token'); // Assuming token is stored in localStorage
    console.log("🚀 ~ ResourceService ~ getHeaders ~ token:", token)
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getResourceTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/types`, { headers: this.getHeaders() });
  }

  getResourceStatuses(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/statuses`, { headers: this.getHeaders() });
  }

  createResource(resource: Resource): Observable<Resource> {
    return this.http.post<Resource>(`${this.apiUrl}/create`, resource, { headers: this.getHeaders() });
  }

  getResourceById(id: number): Observable<Resource> {
    return this.http.get<Resource>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  getAllResources(): Observable<Resource[]> {
    return this.http.get<Resource[]>(`${this.apiUrl}/all`, { headers: this.getHeaders() });
  }

  deleteResource(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, { headers: this.getHeaders() });
  }

  updateResource(id: number, resource: Resource): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/update/${id}`, resource, { headers: this.getHeaders() });
  }
}
