import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EventService } from '../../../../services/event.service';
import { TypeEvent, TypeTheme, TypeWeather } from './event.enums';
import { Router } from '@angular/router';

@Component({
  selector: 'app-createevent',
  templateUrl: './createevent.component.html',
  styleUrls: ['./createevent.component.css']
})
export class CreateeventComponent {
  form: FormGroup;
  typeevent = Object.values(TypeEvent);
  typetheme = Object.values(TypeTheme);
  typeweather = Object.values(TypeWeather);
  

  constructor(private fb: FormBuilder, private router: Router, private eventService: EventService) {
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

  get dateError(): boolean {
    const start = this.form.value.startTime;
    const end = this.form.value.endTime;
    return start && end && new Date(start) >= new Date(end);
  }

  onSubmit() {
    if (this.dateError) {
      alert('La date de fin doit être après la date de début.');
      return;
    }

    if (this.form.valid) {
      this.eventService.createEvent(this.form.value).subscribe(
        () => {
          alert('Événement créé avec succès !');
          this.router.navigate(['/admin/events']);
        },
        (error) => {
          console.error('Erreur lors de la création de l\'événement', error);
        }
      );
    }
  }
}
