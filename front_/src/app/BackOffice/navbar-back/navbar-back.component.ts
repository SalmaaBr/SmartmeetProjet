import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { EventService } from 'src/app/services/event.service';

@Component({
  selector: 'app-navbar-back',
  templateUrl: './navbar-back.component.html',
  styleUrls: ['./navbar-back.component.css']
})
export class NavbarBackComponent {
  username = localStorage.getItem("username");
  roles = localStorage.getItem("roles");
  notifications: any[] = []; // Tableau pour stocker les notifications
  notificationCount: number = 0;

  constructor(private router: Router, private eventService: EventService) {}

  ngOnInit() {
    this.getNotifications();
  }

  // Récupère les notifications depuis l'API Spring
  getNotifications() {
    this.eventService.getNotifications().subscribe(notifications => {
      this.notifications = notifications;
      this.notificationCount = notifications.length;
    });
  }

  logout() {
    localStorage.removeItem("roles");
    localStorage.removeItem("username");
    localStorage.removeItem("auth_token");
    this.router.navigate(['/']);
  }
}
