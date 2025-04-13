import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RecutementService } from '../../../services/recutement.service';
import { EventService, Event } from '../../../services/event.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-recutement',
  templateUrl: './create-recutement.component.html',
  styleUrls: ['./create-recutement.component.css']
})
export class CreateRecutementComponent implements OnInit {
  recruitmentForm: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';
  events: Event[] = [];
  eventHasRecruitment: boolean = false;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private recutementService: RecutementService,
    private eventService: EventService
  ) {
    this.recruitmentForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      eventTitle: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.eventService.getEvents().subscribe(events => {
      this.events = events;
    });
  }

  onEventSelect(eventTitle: string) {
    if (eventTitle) {
      this.eventService.checkEventHasRecruitment(eventTitle).subscribe(hasRecruitment => {
        this.eventHasRecruitment = hasRecruitment;
        if (hasRecruitment) {
          this.errorMessage = 'Cet événement possède déjà un recrutement.';
        } else {
          this.errorMessage = '';
        }
      });
    }
  }

  onSubmit() {
    if (this.recruitmentForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs.';
      return;
    }

    if (this.eventHasRecruitment) {
      this.errorMessage = 'Impossible d\'assigner un nouveau recrutement à cet événement.';
      return;
    }

    const formData = {
      ...this.recruitmentForm.value,
      createdAt: new Date().toISOString()
    };

    const selectedEventTitle = this.recruitmentForm.value.eventTitle;

    this.recutementService.assignRecruitmentToEvent(selectedEventTitle, formData).subscribe({
      next: () => {
        this.successMessage = 'Recrutement créé et associé avec succès !';
        this.recruitmentForm.reset();
        setTimeout(() => {
          this.router.navigate(['/admin/events']);
        }, 2000);
      },
      error: (err) => {
        console.error('Erreur lors de l\'association du recrutement:', err);
        this.errorMessage = 'Erreur lors de l\'association du recrutement.';
      }
    });
  }
}