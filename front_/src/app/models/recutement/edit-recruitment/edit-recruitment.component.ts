import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RecutementService } from '../../../services/recutement.service';
import { EventService, Event } from '../../../services/event.service';

@Component({
  selector: 'app-edit-recruitment',
  templateUrl: './edit-recruitment.component.html',
  styleUrls: ['./edit-recruitment.component.css']
})
export class EditRecruitmentComponent implements OnInit {

  recrutement: any = {
    title: '',
    description: '',
    eventTitle: ''
  };
  id!: number;
  events: Event[] = [];
  errorMessage: string = '';
  eventHasRecruitment: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private recrutementService: RecutementService,
    private eventService: EventService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));  // Récupérer l'ID depuis la route
    this.loadEvents();
    if (this.id) {
      this.getMonitoringRecruitment();  // Récupérer les données du recrutement
    }
  }

  loadEvents(): void {
    this.eventService.getEvents().subscribe(events => {
      this.events = events;
    });
  }

  getMonitoringRecruitment(): void {
    this.recrutementService.getMonitoringRecruitmentById(this.id).subscribe(
      (data) => {
        this.recrutement = data;
        this.recrutement.eventTitle = data.eventTitle || '';  // Assigner le titre de l'événement s'il existe
      },
      (error) => {
        console.error('Erreur de récupération du recrutement', error);
      }
    );
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

  updateRecruitment(): void {
    const { title, description, eventTitle } = this.recrutement;

    if (this.eventHasRecruitment) {
      this.errorMessage = 'Impossible d\'assigner un nouveau recrutement à cet événement.';
      return;
    }

    this.recrutementService.updateMonitoringRecruitment(this.id, title, description).subscribe(
      (data) => {
        alert('Recrutement mis à jour avec succès');
        this.router.navigate(['/admin/monitoring-recruitment-list']);  // Redirige après la mise à jour
      },
      (error) => {
        console.error('Erreur lors de la mise à jour du recrutement', error);
      }
    );
  }
}
