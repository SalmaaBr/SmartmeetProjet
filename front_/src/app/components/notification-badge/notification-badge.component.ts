import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { NotificationService } from '../../services/notification.service';
import { Subscription, interval, of } from 'rxjs';
import { switchMap, startWith, catchError } from 'rxjs/operators';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-notification-badge',
  templateUrl: './notification-badge.component.html',
  styleUrls: ['./notification-badge.component.scss']
})
export class NotificationBadgeComponent implements OnInit, OnDestroy {
  unreadCount: number = 0;
  private subscription: Subscription | null = null;
  private readonly REFRESH_INTERVAL = 60000; // Refresh every minute

  @Input() standaloneMode: boolean = false;

  constructor(
    private notificationService: NotificationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Check if we have a token and get username
    const token = this.authService.getToken();
    if (!token) {
      return;
    }

    try {
      // Decode JWT token manually
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));

      // Extract username from token
      const username = decoded.sub;

      if (username) {
        // Initial load and then refresh every minute
        this.subscription = interval(this.REFRESH_INTERVAL)
          .pipe(
            startWith(0),
            switchMap(() => this.notificationService.getUnreadCountByUsername(username)),
            catchError(err => {
              console.error('Error loading notifications', err);
              return of(0);
            })
          )
          .subscribe({
            next: (count) => {
              this.unreadCount = count;
            },
            error: (err) => {
              console.error('Error loading notification count', err);
            }
          });
      }
    } catch (error) {
      console.error('Error decoding token', error);
    }
  }

  ngOnDestroy(): void {
    // Clean up the subscription when the component is destroyed
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
