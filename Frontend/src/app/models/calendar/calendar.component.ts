import { Component, OnInit } from '@angular/core';
import { EventService, Event } from 'src/app/services/event.service';  // Assurez-vous que vous avez ce service
import { CalendarOptions } from '@fullcalendar/core';  // Importation correcte depuis @fullcalendar/core
import dayGridPlugin from '@fullcalendar/daygrid';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin],
    initialView: 'dayGridMonth',
    weekends: true,
    events: [],  // Liste vide au départ
    eventClick: this.handleEventClick.bind(this),  // Clic sur un événement
  };

  eventDetails: any = null;  // Détails de l'événement sélectionné

  constructor(private eventService: EventService) { }

  ngOnInit(): void {
    this.loadEvents();  // Charger les événements au démarrage du composant
  }

  // Charger les événements depuis le service
  loadEvents(): void {
    this.eventService.getEvents().subscribe(events => {
      // Transformer les événements pour FullCalendar
      this.calendarOptions.events = events.map(event => ({
        title: event.title,
        start: event.startTime,
        end: event.endTime,
        description: event.description,
        location: event.location,
        filePath: event.filePath,  // L'URL ou le chemin de l'image de l'événement
      }));
    });
  }

  // Méthode appelée lors du clic sur un événement
  handleEventClick(info: any): void {
    const event = info.event;
    this.eventDetails = {
      title: event.title,
      description: event.extendedProps.description,
      location: event.extendedProps.location,
      startTime: event.start,
      endTime: event.end,
      filePath: event.extendedProps.filePath,  // Associer l'image à l'événement sélectionné
    };
  }
}
