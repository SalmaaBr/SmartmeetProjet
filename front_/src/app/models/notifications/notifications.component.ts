import { Component } from '@angular/core';
import { EventService } from 'src/app/services/event.service';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent {

  notifications: any[] = [];

  constructor(private eventService: EventService) {}

  ngOnInit(): void {
    this.getNotifications();
  }

  getNotifications(): void {
    this.eventService.getNotifications().subscribe(notifications => {
      this.notifications = notifications;
    });
  }

}
