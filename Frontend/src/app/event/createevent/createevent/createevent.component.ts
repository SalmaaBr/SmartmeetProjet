import { Component } from '@angular/core';
import { EventService } from '../../../services/event.service';
import { TypeEvent, TypeTheme, TypeWeather } from './event.enums';
import { Router } from '@angular/router';


@Component({
  selector: 'app-createevent',
  templateUrl: './createevent.component.html',
  styleUrls: ['./createevent.component.css']
})
export class CreateeventComponent {
  eventData = {
    title: '',
    description: '',
    location: '',
    typeevent: TypeEvent.CONCERT,
    typetheme: TypeTheme.CULTURE,
    typeweather: TypeWeather.SUNNY,
    startTime: '',
    endTime: '',
    maxParticipants: 0
  };

  typeevent = Object.values(TypeEvent);
  typetheme = Object.values(TypeTheme);
  typeweather = Object.values(TypeWeather);

  constructor(private router: Router,private eventService: EventService) {}

  createEvent() {
    // Appel à ton service pour créer l'événement
    this.eventService.createEvent(this.eventData).subscribe(
      (response) => {
        // Si l'événement est créé avec succès, redirige vers /admin/events
        this.router.navigate(['/admin/events']);
      },
      (error) => {
        console.error('Erreur lors de la création de l\'événement', error);
      }
    );
  }

}
