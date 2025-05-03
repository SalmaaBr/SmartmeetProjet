import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { LostAndFoundService } from '../../services/lost-and-found.service';
import { UserService } from '../../services/user.service';
import { Notification } from '../../models/lost-and-found.model';
import { interval, Subscription } from 'rxjs';
import { startWith, switchMap } from 'rxjs/operators';

interface Toast {
  title: string;
  message: string;
  type: 'notification' | 'info';
  exiting: boolean;
}

@Component({
  selector: 'app-header-front',
  templateUrl: './header-front.component.html',
  styleUrls: ['./header-front.component.css']
})
export class HeaderFrontComponent implements OnInit, OnDestroy {
  isAuth = localStorage.getItem('auth_token');
  username = localStorage.getItem("username");
  roles = localStorage.getItem("roles");
  user = JSON.parse(localStorage.getItem("user") || '{}');
  profileImageUrl: string | null = null;

  // Notification properties
  notifications: Notification[] = [];
  unreadCount = 0;
  isNotificationOpen = false;
  loading = false;
  error: string | null = null;
  toasts: Toast[] = [];
  private notificationSubscription?: Subscription;
  private previousNotificationCount = 0;
  private markedReadIds: Set<number> = new Set(); // Suivre les IDs marqués comme lus localement

  constructor(
    private router: Router,
    private lostAndFoundService: LostAndFoundService,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.isAuth) {
      // Load profile image
      this.loadProfileImage();

      // Subscribe to profile image updates
      this.userService.profileImageUpdate$.subscribe(() => {
        this.loadProfileImage();
      });

      // Initial load
      this.loadNotifications();

      // Poll for new notifications every 30 seconds
      this.notificationSubscription = interval(30000)
        .pipe(
          startWith(0),
          switchMap(() => this.lostAndFoundService.getUserNotifications())
        )
        .subscribe({
          next: (data) => {
            // Fusionner les données du serveur avec l'état local
            this.notifications = data.map(serverNotif => {
              // Conserver l'état read local si l'ID a été marqué comme lu
              return this.markedReadIds.has(serverNotif.relatedItemId)
                ? { ...serverNotif, read: true }
                : serverNotif;
            });

            // Vérifier les nouvelles notifications
            if (this.previousNotificationCount > 0 &&
              data.length > this.previousNotificationCount) {
              const newCount = data.length - this.previousNotificationCount;
              this.showToast(
                'New Notification',
                `You have ${newCount} new notification${newCount > 1 ? 's' : ''}`,
                'notification'
              );
            }

            this.unreadCount = this.countUnread();
            this.previousNotificationCount = data.length;
            this.cdr.detectChanges();
            console.log('Notifications après polling:', this.notifications, 'Unread:', this.unreadCount);
          },
          error: (err) => {
            console.error('Failed to fetch notifications:', err);
          }
        });
    }
  }

  ngOnDestroy(): void {
    if (this.notificationSubscription) {
      this.notificationSubscription.unsubscribe();
    }
  }

  private parseRoles(): string[] {
    if (!this.roles) return [];
    try {
      return JSON.parse(this.roles);
    } catch {
      return this.roles
        .replace(/[\[\]"']/g, '')
        .split(',')
        .map(role => role.trim())
        .filter(role => role);
    }
  }

  hasSponsorRole(): boolean {
    const userRoles = this.parseRoles();
    return userRoles.includes('SPONSOR');
  }

  canViewContracts(): boolean {
    const userRoles = this.parseRoles();
    return userRoles.includes('SPONSOR') && userRoles.includes('USER');
  }

  logout() {
    localStorage.removeItem("roles");
    localStorage.removeItem("username");
    localStorage.removeItem("auth_token");
    localStorage.removeItem("user");
    this.router.navigate(['login']);
  }

  loadNotifications(): void {
    if (!this.isAuth) return;

    this.loading = true;
    this.error = null;

    this.lostAndFoundService.getUserNotifications()
      .subscribe({
        next: (data) => {
          // Fusionner avec l'état local
          this.notifications = data.map(serverNotif => {
            return this.markedReadIds.has(serverNotif.relatedItemId)
              ? { ...serverNotif, read: true }
              : serverNotif;
          });
          this.unreadCount = this.countUnread();
          this.previousNotificationCount = data.length;
          this.loading = false;
          this.cdr.detectChanges();
          console.log('Notifications après chargement:', this.notifications, 'Unread:', this.unreadCount);
        },
        error: (err) => {
          this.error = 'Failed to load notifications';
          this.loading = false;
          console.error(err);
        }
      });
  }

  toggleNotifications(): void {
    this.isNotificationOpen = !this.isNotificationOpen;

    if (this.isNotificationOpen) {
      this.loadNotifications();
    }
  }

  handleNotificationClick(notification: Notification): void {
    this.lostAndFoundService.markNotificationAsRead(notification.relatedItemId)
      .subscribe({
        next: () => {
          console.log('Notification marquée comme lue:', notification.relatedItemId);

          // Mettre à jour la notification spécifique
          this.notifications = this.notifications.map(n =>
            n.relatedItemId === notification.relatedItemId && n.timestamp === notification.timestamp
              ? { ...n, read: true }
              : n
          );

          // Ajouter à markedReadIds pour préserver l'état local
          this.markedReadIds.add(notification.relatedItemId);

          // Mettre à jour le compteur
          this.unreadCount = this.countUnread();
          this.cdr.detectChanges();
          console.log('Notifications après clic:', this.notifications, 'Unread:', this.unreadCount);

          // Rediriger si type LOST_AND_FOUND
          if (notification.type === 'LOST_AND_FOUND') {
            this.router.navigate(['/front/lost-and-found/events']);
          }

          // Fermer le dropdown
          this.isNotificationOpen = false;
        },
        error: (err) => {
          console.error('Failed to mark notification as read:', err);
        }
      });
  }

  countUnread(): number {
    return this.notifications.filter(n => n.read === false).length;
  }

  getTimeAgo(timestamp: string): string {
    const now = new Date();
    const notificationTime = new Date(timestamp);
    const diffMs = now.getTime() - notificationTime.getTime();

    const diffSecs = Math.floor(diffMs / 1000);
    if (diffSecs < 60) return `${diffSecs} seconds ago`;

    const diffMins = Math.floor(diffSecs / 60);
    if (diffMins < 60) return `${diffMins} minutes ago`;

    const diffHours = Math.floor(diffMins / 60);
    if (diffHours < 24) return `${diffHours} hours ago`;

    const diffDays = Math.floor(diffHours / 24);
    return `${diffDays} days ago`;
  }

  showToast(title: string, message: string, type: 'notification' | 'info' = 'notification'): void {
    const toast: Toast = {
      title,
      message,
      type,
      exiting: false
    };

    this.toasts.push(toast);

    setTimeout(() => {
      const index = this.toasts.indexOf(toast);
      if (index !== -1) {
        this.toasts[index].exiting = true;
        setTimeout(() => {
          this.removeToast(index);
        }, 300);
      }
    }, 4000);
  }

  removeToast(index: number): void {
    if (index >= 0 && index < this.toasts.length) {
      this.toasts[index].exiting = true;
      setTimeout(() => {
        this.toasts.splice(index, 1);
      }, 300);
    }
  }

  loadProfileImage(): void {
    const userId = this.user?.userID;
    if (userId) {
      this.userService.getProfileImage(userId).subscribe({
        next: (imageUrl) => {
          this.profileImageUrl = imageUrl;
        },
        error: (err) => {
          console.error('Error loading profile image:', err);
          this.profileImageUrl = null;
        }
      });
    }
  }
}
