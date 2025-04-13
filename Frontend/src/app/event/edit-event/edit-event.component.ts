import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EventService } from '../../services/event.service';  // Vérifie l'import
import { TypeEvent, TypeTheme, TypeWeather } from '../../event/createevent/createevent/event.enums';
@Component({
  selector: 'app-edit-event',
  templateUrl: './edit-event.component.html',
  styleUrls: ['./edit-event.component.css']
})
export class EditEventComponent implements OnInit {
    // Initialisation des options avec les énumérations
    typeevent = Object.values(TypeEvent); 
    typetheme = Object.values(TypeTheme); 
    typeweather = Object.values(TypeWeather);
  

  event: any = {};

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private eventService: EventService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    console.log('Event ID:', id); // Vérifie si l'ID est correct
    if (id) {
      this.eventService.getEventById(+id).subscribe(
        (data: any) => {
          this.event = data;
        },
        (error: any) => {
          console.error('Erreur lors du chargement de l\'événement', error);
        }
      );
    }
  }
  

  updateEvent(): void {
    this.eventService.updateEvent(this.event.id, this.event).subscribe(
      () => {
        console.log('Événement mis à jour avec succès');
        this.router.navigate(['/admin/events']); // Redirection après succès
      },
      (error: any) => {
        console.error('Erreur lors de la mise à jour de l\'événement', error);
      }
    );
  }
  
}
