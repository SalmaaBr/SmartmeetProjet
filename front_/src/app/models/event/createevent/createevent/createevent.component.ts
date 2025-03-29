import { Component } from '@angular/core'; 
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EventService } from '../../../../services/event.service';
import { TypeEvent, TypeTheme, TypeWeather } from './event.enums';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

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
  isFormSubmitted = false;  // Variable pour suivre la soumission du formulaire
  selectedFile: File | null = null;
  
  

  constructor(private fb: FormBuilder, private router: Router, private eventService: EventService, private toastr: ToastrService) {
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

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  get dateError(): boolean {
    const start = this.form.value.startTime;
    const end = this.form.value.endTime;
    return start && end && new Date(start) >= new Date(end);
  }

  onSubmit() {
    this.isFormSubmitted = true;

    if (this.dateError) {
      this.toastr.error('La date de fin doit être après la date de début.', 'Erreur');
      return;
    }

    if (this.form.valid) {
      if (this.selectedFile) {
        this.uploadImageAndCreateEvent();
      } else {
        this.createEvent(null);
      }
    }
  }

  private uploadImageAndCreateEvent() {
    this.eventService.uploadImage(this.selectedFile!).subscribe({
      next: (imagePath) => {
        this.createEvent(imagePath);
      },
      error: (err) => {
        this.toastr.error('Erreur lors de l\'upload de l\'image', 'Erreur');
      }
    });
  }

  private createEvent(imagePath: string | null) {
    this.eventService.createEventWithImage(this.form.value, imagePath).subscribe({
      next: () => {
        this.toastr.success('Événement créé avec succès !', 'Succès');
        this.router.navigate(['/admin/events']);
      },
      error: (error) => {
        console.error('Erreur lors de la création', error);
        this.toastr.error('Erreur lors de la création', 'Erreur');
      }
    });
  }
}



