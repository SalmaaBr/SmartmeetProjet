import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms'; // Import FormBuilder and Validators
import { EventService } from '../../../services/event.service'; 
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

  event: any = {}; // This will hold the event data from the API
  form: FormGroup; // Declare form

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private fb: FormBuilder // Inject FormBuilder to create the form
  ) {
    // Initialize form in the constructor
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required],
      typeevent: [TypeEvent.CONCERT, Validators.required],
      typetheme: [TypeTheme.CULTURE, Validators.required],
      typeweather: [TypeWeather.SUNNY, Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      maxParticipants: [0, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    console.log('Event ID:', id);
    if (id) {
      this.eventService.getEventById(+id).subscribe(
        (data: any) => {
          this.event = data;
          // Patch the form with existing event data
          this.form.patchValue({
            title: this.event.title,
            description: this.event.description,
            location: this.event.location,
            typeevent: this.event.typeevent,
            typetheme: this.event.typetheme,
            typeweather: this.event.typeweather,
            startTime: this.event.startTime,
            endTime: this.event.endTime,
            maxParticipants: this.event.maxParticipants
          });
        },
        (error: any) => {
          console.error('Erreur lors du chargement de l\'événement', error);
        }
      );
    }
  }

  // Add date validation as in the CreateEvent component
  get dateError(): boolean {
    const start = this.form.value.startTime;
    const end = this.form.value.endTime;
    return start && end && new Date(start) >= new Date(end);
  }

  updateEvent(): void {
    if (this.dateError) {
      alert('La date de fin doit être après la date de début.');
      return;
    }

    if (this.form.valid) {
      this.eventService.updateEvent(this.event.id, this.form.value).subscribe(
        () => {
          console.log('Événement mis à jour avec succès');
          this.router.navigate(['/admin/events']);
        },
        (error: any) => {
          console.error('Erreur lors de la mise à jour de l\'événement', error);
        }
      );
    }
  }
}
