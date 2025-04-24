import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RecutementService {

  // URL de base
  private apiUrl = 'http://localhost:8082/MonitoringRecruitment';

  constructor(private http: HttpClient) { }

  // Fonction pour créer un monitoringRecruitment
  createMonitoringRecruitment(monitoringRecruitment: any): Observable<any> {
    const url = `${this.apiUrl}/createRecutement`;  // Combine l'URL de base avec la route spécifique
    return this.http.post<any>(url, monitoringRecruitment);
  }

    // Fonction pour récupérer tous les recrutements
    getAllMonitoringRecruitments(): Observable<any[]> {
      const url = `${this.apiUrl}/ReadAll`;  // Route pour récupérer la liste des recrutements
      return this.http.get<any[]>(url);  // Retourne un Observable contenant la liste des recrutements
    }

      // Méthode pour supprimer un recrutement
  deleteMonitoringRecruitment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deleteRecutement/${id}`);
  }

    // Récupérer un recrutement par ID
    getMonitoringRecruitmentById(id: number): Observable<any> {
      return this.http.get(`${this.apiUrl}/getbyRecutementId/${id}`);
    }

    // Mettre à jour un recrutement
    updateMonitoringRecruitment(id: number, title: string, description: string): Observable<any> {
      return this.http.put(`${this.apiUrl}/updateRecutement/${id}`,  { title, description });
    }


}
