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

  get dateError(): boolean {
    const start = this.form.value.startTime;
    const end = this.form.value.endTime;
    return start && end && new Date(start) >= new Date(end);
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  onSubmit() {  
    this.isFormSubmitted = true;  // Marquer le formulaire comme soumis

    if (this.dateError) {
      this.toastr.error('La date de fin doit être après la date de début.', 'Erreur');
    }

    if (this.form.valid && this.selectedFile && !this.dateError) {
      this.eventService.createEvent(this.form.value, this.selectedFile).subscribe(
        () => {
          this.toastr.success('Événement créé avec succès !', 'Succès');
          this.router.navigate(['/admin/events']);
        },
        (error) => {
          console.error('Erreur lors de la création de l\'événement', error);
          this.toastr.error('Erreur lors de la création de l\'événement', 'Erreur');
        }
      );
    } else {
      this.toastr.error('Veuillez remplir tous les champs requis et sélectionner un fichier.', 'Erreur');
    }
  }
}