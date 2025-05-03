import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LostAndFoundService } from '../../../services/lost-and-found.service';

@Component({
  selector: 'app-user-events',
  templateUrl: './user-events.component.html',
  styleUrls: ['./user-events.component.css']
})
export class UserEventsComponent implements OnInit {
  events: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(
    private lostAndFoundService: LostAndFoundService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.loading = true;
    this.lostAndFoundService.getAllEvents().subscribe({
      next: (data: any[]) => {
        this.events = data;
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to load events. Please try again later.';
        this.loading = false;
        console.error('Error loading events:', err);
      }
    });
  }

  navigateToEventLostAndFound(eventId: number): void {
    this.router.navigate(['/front/lost-and-found/event', eventId]);
  }

  reportNewItem(eventId: number): void {
    this.router.navigate(['/front/lost-and-found/event', eventId, 'new']);
  }

  // New methods for handling event status
  isUpcoming(event: any): boolean {
    const now = new Date();
    const startDate = new Date(event.startDate);
    return startDate > now;
  }

  isOngoing(event: any): boolean {
    const now = new Date();
    const startDate = new Date(event.startDate);
    const endDate = new Date(event.endDate || event.startDate); // Fallback to startDate if endDate not available

    // Add one day to end date if it's the same as start date for "all day" events
    if (event.endDate === undefined || event.endDate === null) {
      endDate.setDate(endDate.getDate() + 1);
    }

    return startDate <= now && endDate >= now;
  }

  isPast(event: any): boolean {
    const now = new Date();
    const endDate = new Date(event.endDate || event.startDate); // Fallback to startDate if endDate not available

    // Add one day to end date if it's the same as start date for "all day" events
    if (event.endDate === undefined || event.endDate === null) {
      endDate.setDate(endDate.getDate() + 1);
    }

    return endDate < now;
  }

  getEventStatus(event: any): string {
    if (this.isUpcoming(event)) {
      return 'Upcoming';
    } else if (this.isOngoing(event)) {
      return 'Ongoing';
    } else {
      return 'Past';
    }
  }
}
