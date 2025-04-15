// src/app/back-office/add-feedback/add-feedback.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { EventService } from '../../services/event.service';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Router, ActivatedRoute } from '@angular/router';
import { Feedback, TypeFeeling } from '../../models/feedback.model';
import { Observable, tap } from 'rxjs';

@Component({
  selector: 'app-add-feedback',
  templateUrl: './add-feedback.component.html',
  styleUrls: ['./add-feedback.component.css']
})
export class AddFeedbackComponent implements OnInit, OnDestroy {
  newFeedback: Feedback = {
    message: '',
    feeling: '' as TypeFeeling,
    date: new Date().toISOString().split('T')[0] // Format ISO pour LocalDate
  };
  selectedEventId: number | null = null;
  events: any[] = [];
  feelings: string[] = ['EXCELLENT', 'GOOD', 'AVERAGE', 'BAD', 'TERRIBLE'];
  isEditing: boolean = false;
  currentFeedbackId: number | null = null;
  private intervalId: any;

  constructor(
    private feedbackService: FeedbackService,
    private eventService: EventService,
    private toastr: ToastrService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadEvents().subscribe(() => {
      this.route.paramMap.subscribe(params => {
        const feedbackId = params.get('id');
        if (feedbackId) {
          this.isEditing = true;
          this.currentFeedbackId = +feedbackId;
          this.loadFeedback(this.currentFeedbackId);
        }
      });
    });
    this.intervalId = setInterval(() => this.loadEvents(), 10000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  loadEvents(): Observable<any> {
    return this.eventService.getEvents().pipe(
      tap({
        next: (response) => {
          this.events = response;
          console.log('✅ Events loaded:', response);
        },
        error: (error) => {
          this.toastr.error('Erreur de chargement des événements', 'Erreur');
          console.error('❌ Error loading events:', error);
        }
      })
    );
  }

  loadFeedback(id: number) {
    this.feedbackService.getFeedbackById(id).subscribe({
      next: (feedback) => {
        this.newFeedback = {
          message: feedback.message,
          feeling: feedback.feeling,
          date: new Date(feedback.date).toISOString().split('T')[0] // Format ISO
        };
        const event = this.events.find(e => e.title === feedback.eventTitle);
        this.selectedEventId = event ? event.id : null;
        if (!event) {
          console.warn(`Événement "${feedback.eventTitle}" non trouvé`);
        }
      },
      error: (error) => {
        this.toastr.error('Erreur lors du chargement du feedback', 'Erreur');
        console.error('Erreur:', error);
      }
    });
  }

  onSubmit() {
    if (!this.selectedEventId) {
      this.toastr.warning('Veuillez sélectionner un événement !', 'Attention');
      return;
    }

    if (this.isEditing && this.currentFeedbackId) {
      this.updateFeedback();
    } else {
      this.addFeedback();
    }
  }

  addFeedback() {
    this.feedbackService.addFeedbackAndAffectToEvent(this.newFeedback, this.selectedEventId!).subscribe({
      next: (response) => {
        this.toastr.success('Feedback ajouté avec succès', 'Succès');
        this.resetForm();
        this.router.navigate(['/admin/feedback-list'], { queryParams: { refresh: true } });
      },
      error: (error) => {
        this.toastr.error('Erreur lors de l’ajout', 'Erreur');
        console.error('Erreur:', error);
      }
    });
  }

  updateFeedback() {
    if (!this.currentFeedbackId || !this.selectedEventId) {
      this.toastr.error('ID du feedback ou événement manquant', 'Erreur');
      return;
    }
    this.feedbackService.updateFeedback(this.currentFeedbackId, this.newFeedback, this.selectedEventId).subscribe({
      next: (response) => {
        this.toastr.success('Feedback mis à jour avec succès', 'Succès');
        this.resetForm();
        this.router.navigate(['/admin/feedback-list'], { queryParams: { refresh: true } });
      },
      error: (error) => {
        this.toastr.error('Erreur lors de la mise à jour', 'Erreur');
        console.error('Erreur:', error);
      }
    });
  }

  resetForm() {
    this.newFeedback = {
      message: '',
      feeling: '' as TypeFeeling,
      date: new Date().toISOString().split('T')[0]
    };
    this.selectedEventId = null;
    this.isEditing = false;
    this.currentFeedbackId = null;
  }

  selectFeeling(feeling: string) {
    this.newFeedback.feeling = feeling as TypeFeeling;
  }

  goToFeedbackList() {
    this.router.navigate(['/admin/feedback-list']);
  }

  cancelEdit() {
    this.isEditing = false;
    this.currentFeedbackId = null;
    this.resetForm();
    this.router.navigate(['/admin/feedback-list']);
  }
}
