import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MaintenanceNotification {
  id?: number;
  userId: number;
  resourceId: number;
  resourceName: string;
  reservationId?: number;
  startDate: string;
  endDate: string;
  maintenanceStartDate: string;
  maintenanceEndDate: string;
  status: 'PENDING' | 'SENT' | 'READ';
  createdAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8082/api/notifications';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  // Get all notifications for a user by ID (original method)
  getUserNotifications(userId: number): Observable<MaintenanceNotification[]> {
    return this.http.get<MaintenanceNotification[]>(
      `${this.apiUrl}/user/${userId}`,
      { headers: this.getHeaders() }
    );
  }

  // Get all notifications for a user by username
  getUserNotificationsByUsername(username: string): Observable<MaintenanceNotification[]> {
    return this.http.get<MaintenanceNotification[]>(
      `${this.apiUrl}/username/${username}`,
      { headers: this.getHeaders() }
    );
  }

  // Mark a notification as read
  markAsRead(notificationId: number): Observable<any> {
    return this.http.patch(
      `${this.apiUrl}/${notificationId}/read`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // Create a new maintenance notification
  createMaintenanceNotification(notification: MaintenanceNotification): Observable<MaintenanceNotification> {
    return this.http.post<MaintenanceNotification>(
      `${this.apiUrl}/maintenance`,
      notification,
      { headers: this.getHeaders() }
    );
  }

  // Send notifications for upcoming maintenance
  sendUpcomingMaintenanceNotifications(resourceId: number, days: number = 7): Observable<number> {
    return this.http.post<number>(
      `${this.apiUrl}/maintenance/send-upcoming/${resourceId}?days=${days}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // Get unread notification count for a user by ID (original method)
  getUnreadCount(userId: number): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/user/${userId}/unread-count`,
      { headers: this.getHeaders() }
    );
  }

  // Get unread notification count for a user by username
  getUnreadCountByUsername(username: string): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/username/${username}/unread-count`,
      { headers: this.getHeaders() }
    );
  }

  // Get affected reservations for a specific maintenance period
  getAffectedReservations(resourceId: number, maintenanceStartDate: string, maintenanceEndDate: string): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.apiUrl}/maintenance/affected-reservations/${resourceId}?startDate=${maintenanceStartDate}&endDate=${maintenanceEndDate}`,
      { headers: this.getHeaders() }
    );
  }

  // Trigger notifications for all resources with maintenance enabled
  triggerAllNotifications(days: number = 7): Observable<number> {
    return this.http.post<number>(
      `${this.apiUrl}/maintenance/send-all?days=${days}`,
      {},
      { headers: this.getHeaders() }
    );
  }
}
