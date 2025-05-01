import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Resource {
  idResource?: number;
  name: string;
  typeResource: string;
  typeResourceStatus: string;

  // Maintenance-related fields
  maintenanceEnabled?: boolean;
  maintenancePeriodMonths?: number;
  maintenanceDurationDays?: number;
  initialMaintenanceDate?: string;
  nextMaintenanceDate?: string;
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

  // Maintenance-related methods
  updateMaintenanceSettings(id: number, maintenanceConfig: {
    maintenanceEnabled: boolean;
    maintenancePeriodMonths: number;
    maintenanceDurationDays: number;
    initialMaintenanceDate: string;
  }): Observable<Resource> {
    // Create a deep copy of the config to avoid mutating the original object
    const config = {
      maintenanceEnabled: maintenanceConfig.maintenanceEnabled,
      maintenancePeriodMonths: maintenanceConfig.maintenancePeriodMonths,
      maintenanceDurationDays: maintenanceConfig.maintenanceDurationDays,
      initialMaintenanceDate: maintenanceConfig.initialMaintenanceDate
    };

    console.log('Sending maintenance config to backend:', config);

    return this.http.patch<Resource>(
      `${this.apiUrl}/${id}/maintenance`,
      config,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        }),
        withCredentials: true
      }
    );
  }
}
