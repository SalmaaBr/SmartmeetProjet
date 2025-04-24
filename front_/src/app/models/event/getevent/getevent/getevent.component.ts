import { Component, OnInit  } from '@angular/core';
import { EventService , Event } from '../../../../services/event.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-getevent',
  templateUrl: './getevent.component.html',
  styleUrls: ['./getevent.component.css']
})
export class GeteventComponent implements OnInit{
  events: Event[] = [];

  constructor(private eventService: EventService, private router: Router) {}

  ngOnInit(): void {
    this.eventService.getEvents().subscribe(data => {
      this.events = data;
    });
  }

  deleteEvent(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet événement ?')) {
      this.eventService.deleteEvent(id).subscribe(
        () => {
          this.events = this.events.filter(event => event.id !== id);
          console.log('Événement supprimé avec succès');
        },
        (error) => {
          console.error('Erreur lors de la suppression de l\'événement', error);
        }
      );
    }
  }

  editEvent(id: number): void {
    console.log('Navigating to edit event with ID:', id);
    this.router.navigate([`/admin/edit-event/${id}`]);
  }
  

}
