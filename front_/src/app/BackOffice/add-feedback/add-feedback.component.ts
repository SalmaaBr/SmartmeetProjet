import { Component, OnInit, OnDestroy } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { EventService } from '../../services/event.service';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-add-feedback',
  templateUrl: './add-feedback.component.html',
  styleUrls: ['./add-feedback.component.css']
})
export class AddFeedbackComponent implements OnInit, OnDestroy {
  newFeedback: any = {
    message: '',
    feeling: '',
    date: new Date().toISOString().split('T')[0],
  };
  selectedEventId: number | null = null;
  feedbacks: any[] = [];
  events: any[] = [];
  feelings: string[] = ['EXCELLENT', 'GOOD', 'AVERAGE', 'BAD', 'TERRIBLE'];
  isEditing: boolean = false;
  currentFeedbackId: number | null = null;
  private intervalId: any;

  constructor(
    private feedbackService: FeedbackService,
    private eventService: EventService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.getAllFeedbacks();
    this.loadEvents();
    this.intervalId = setInterval(() => this.loadEvents(), 10000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  loadEvents() {
    this.eventService.getEvents().subscribe({
      next: (response) => {
        this.events = response;
        console.log('✅ Events loaded successfully:', this.events);
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des événements', 'Erreur');
        console.error('❌ Error loading events:', error);
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
        this.feedbacks.push(response);
        this.toastr.success('Le feedback est ajouté avec succès', 'Succès');
        this.resetForm();
      },
      error: (error) => {
        this.toastr.error('Il y a une erreur', 'Erreur');
        console.error('Erreur lors de l’ajout du feedback:', error);
      }
    });
  }

  updateFeedback() {
    if (!this.currentFeedbackId || !this.selectedEventId) {
      console.error('ID du feedback ou événement manquant:', {
        currentFeedbackId: this.currentFeedbackId,
        selectedEventId: this.selectedEventId
      });
      this.toastr.error('ID du feedback ou de l’événement manquant', 'Erreur');
      return;
    }
    console.log('Données envoyées pour mise à jour:', {
      id: this.currentFeedbackId,
      eventId: this.selectedEventId,
      feedback: this.newFeedback
    });
    this.feedbackService.updateFeedback(this.currentFeedbackId, this.newFeedback, this.selectedEventId).subscribe({
      next: (response) => {
        const index = this.feedbacks.findIndex(f => f.idFeedback === this.currentFeedbackId);
        this.feedbacks[index] = response;
        this.toastr.success('Feedback mis à jour avec succès', 'Succès');
        this.resetForm();
      },
      error: (error) => {
        console.error('Erreur complète lors de la mise à jour:', error);
        this.toastr.error('Erreur lors de la mise à jour', 'Erreur');
      }
    });
  }

  deleteFeedback(id: number) {
    this.feedbackService.deleteFeedback(id).subscribe({
      next: () => {
        this.toastr.success('Feedback supprimé avec succès', 'Succès');
        this.feedbacks = this.feedbacks.filter(feedback => feedback.idFeedback !== id);
      },
      error: (error) => {
        this.toastr.error('Erreur de suppression', 'Erreur');
        console.error('Erreur lors de la suppression du feedback:', error);
      }
    });
  }

  getAllFeedbacks() {
    this.feedbackService.getAllFeedbacks().subscribe({
      next: (response) => {
        this.feedbacks = response;
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des feedbacks', 'Erreur');
        console.error('Erreur lors de la récupération des feedbacks:', error);
      }
    });
  }

  editFeedback(feedback: any) {
    this.isEditing = true;
    this.currentFeedbackId = feedback.idFeedback;
    this.newFeedback = { ...feedback };
    // Trouver l'ID de l'événement en fonction de eventTitle
    const event = this.events.find(e => e.title === feedback.eventTitle);
    this.selectedEventId = event ? event.id : null; // Pré-remplit l'événement
    if (!event) {
      console.warn(`Événement avec le titre "${feedback.eventTitle}" non trouvé dans la liste des événements`);
    }
  }

  cancelEdit() {
    this.isEditing = false;
    this.currentFeedbackId = null;
    this.resetForm();
  }

  resetForm() {
    this.newFeedback = {
      message: '',
      feeling: '',
      date: new Date().toISOString().split('T')[0],
    };
    this.selectedEventId = null;
    this.isEditing = false;
    this.currentFeedbackId = null;
  }

  selectFeeling(feeling: string) {
    this.newFeedback.feeling = feeling;
  }
}
