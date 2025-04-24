import { Component, OnInit } from '@angular/core';
import { NotificationService, MaintenanceNotification } from '../../services/notification.service';
import { AuthService } from 'src/app/auth/auth.service';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss']
})
export class NotificationComponent implements OnInit {
  notifications: MaintenanceNotification[] = [];
  unreadCount: number = 0;
  loading: boolean = false;
  error: string | null = null;
  username: string | null = null;

  constructor(
    private notificationService: NotificationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.getUserInfoFromToken();
  }

  private getUserInfoFromToken(): void {
    const token = this.authService.getToken();
    if (!token) {
      this.error = 'User not authenticated';
      return;
    }

    try {
      // Decode JWT token manually (without external library)
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      console.log("🚀 ~ NotificationComponent ~ getUserInfoFromToken ~ decoded:", decoded)

      // Extract username from token sub field
      this.username = decoded.sub;

      if (this.username) {
        this.loadNotifications();
        this.loadUnreadCount();
      } else {
        this.error = 'Username not found in token';
      }
    } catch (error) {
      console.error('Error decoding token', error);
      this.error = 'Failed to get user information';
    }
  }

  loadNotifications(): void {
    if (!this.username) return;

    this.loading = true;
    this.notificationService.getUserNotificationsByUsername(this.username).subscribe({
      next: (data) => {
        this.notifications = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading notifications', err);
        this.error = 'Failed to load notifications';
        this.loading = false;
      }
    });
  }

  loadUnreadCount(): void {
    if (!this.username) return;

    this.notificationService.getUnreadCountByUsername(this.username).subscribe({
      next: (count) => {
        this.unreadCount = count;
      },
      error: (err) => {
        console.error('Error loading unread count', err);
      }
    });
  }

  markAsRead(notification: MaintenanceNotification): void {
    if (!notification.id) return;

    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        // Update the notification status locally
        notification.status = 'READ';
        // Refresh unread count
        this.loadUnreadCount();
      },
      error: (err) => {
        console.error('Error marking notification as read', err);
      }
    });
  }

  markAllAsRead(): void {
    const pendingNotifications = this.notifications.filter(n => n.status === 'PENDING' && n.id);

    if (pendingNotifications.length === 0) return;

    // Create an array of observables for each notification
    const markRequests = pendingNotifications.map(notification =>
      this.notificationService.markAsRead(notification.id!)
    );

    // Process them sequentially to avoid overwhelming the server
    const processSequentially = async () => {
      for (let i = 0; i < markRequests.length; i++) {
        try {
          await firstValueFrom(markRequests[i]);
          pendingNotifications[i].status = 'READ';
        } catch (error) {
          console.error('Error marking notification as read', error);
        }
      }
      this.loadUnreadCount();
    };

    processSequentially();
  }

  formatDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString();
    } catch (e) {
      return dateString;
    }
  }

  refreshNotifications(): void {
    this.loadNotifications();
    this.loadUnreadCount();
  }
}
