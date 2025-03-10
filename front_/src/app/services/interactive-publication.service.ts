import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InteractivePublication } from 'src/app/models/interactive-publication.model';

@Injectable({
  providedIn: 'root'
})
export class InteractivePublicationService {

  private baseUrl = 'http://localhost:8082/InteractivePublication'; // Remplace par l'URL de ton API Spring.

  constructor(private http: HttpClient) {}

  // Create a new interactive publication
  createPublication(publication: InteractivePublication): Observable<InteractivePublication> {
    return this.http.post<InteractivePublication>(`${this.baseUrl}/create`, publication);
  }

  // Retrieve a publication by its ID
  getPublicationById(id: number): Observable<InteractivePublication> {
    return this.http.get<InteractivePublication>(`${this.baseUrl}/ReadByID/${id}`);
  }

  // Retrieve all interactive publications
  getAllPublications(): Observable<InteractivePublication[]> {
    return this.http.get<InteractivePublication[]>(`${this.baseUrl}/ReadAllIPublications`);
  }

  // Delete a publication by its ID
  deletePublication(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/DeleteIPublicationByID/${id}`);
  }

  // Update a publication by its ID
  updatePublication(id: number, publication: InteractivePublication): Observable<void> {
    // Ensure your endpoint mapping in the controller is changed to /UpdateIPublicationByID/{id}
    return this.http.put<void>(`${this.baseUrl}/UpdateIPublicationByID/${id}`, publication);
  }
}
