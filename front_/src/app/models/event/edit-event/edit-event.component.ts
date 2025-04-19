import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EventService } from '../../../services/event.service'; 
import { TypeEvent, TypeTheme, TypeWeather } from '../../event/createevent/createevent/event.enums';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-edit-event',
  templateUrl: './edit-event.component.html',
  styleUrls: ['./edit-event.component.css']
})
export class EditEventComponent implements OnInit {
  typeevent = Object.values(TypeEvent); 
  typetheme = Object.values(TypeTheme); 
  typeweather = Object.values(TypeWeather);

  event: any = {};
  form: FormGroup;
  selectedFile: File | null = null;
  previewUrl: string | ArrayBuffer | null = null;
  currentImageUrl: string | null = null;
  isFormSubmitted = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private fb: FormBuilder,
    private toastr: ToastrService
  ) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required],
      typeevent: [TypeEvent.CONCERT, Validators.required],
      typetheme: [TypeTheme.PROGRAMMING, Validators.required],
      typeweather: [TypeWeather.SUNNY, Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      maxParticipants: [0, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.eventService.getEventById(+id).subscribe(
        (data: any) => {
          this.event = data;
          this.currentImageUrl = data.imageUrl;
          
          this.form.patchValue({
            title: this.event.title,
            description: this.event.description,
            location: this.event.location,
            typeevent: this.event.typeevent,
            typetheme: this.event.typetheme,
            typeweather: this.event.typeweather,
            startTime: this.formatDateTime(this.event.startTime),
            endTime: this.formatDateTime(this.event.endTime),
            maxParticipants: this.event.maxParticipants
          });
        },
        (error: any) => {
          console.error('Erreur lors du chargement de l\'événement', error);
          this.toastr.error('Erreur lors du chargement de l\'événement', 'Erreur');
        }
      );
    }
  }

  // Helper method to format date for datetime-local input
  private formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16);
  }

  get dateError(): boolean {
    const start = this.form.value.startTime;
    const end = this.form.value.endTime;
    return start && end && new Date(start) >= new Date(end);
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    this.selectedFile = file;

    // Prévisualisation de l'image
    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl = reader.result;
    };
    reader.readAsDataURL(file);
  }

  updateEvent(): void {
    this.isFormSubmitted = true;
  
    if (this.dateError) {
      this.toastr.error('La date de fin doit être après la date de début.', 'Erreur');
      return;
    }
  
    if (this.form.valid) {
      this.eventService.updateEventWithImage(
        this.event.id, 
        this.form.value, 
        this.selectedFile ?? null // Envoie null si selectedFile est null
      ).subscribe(
        () => {
          this.toastr.success('Événement mis à jour avec succès', 'Succès');
          this.router.navigate(['/admin/events']);
        },
        (error: any) => {
          console.error('Erreur lors de la mise à jour de l\'événement', error);
          this.toastr.error('Erreur lors de la mise à jour de l\'événement', 'Erreur');
        }
      );
    } else {
      this.toastr.error('Veuillez remplir tous les champs requis', 'Erreur');
    }
  }
}