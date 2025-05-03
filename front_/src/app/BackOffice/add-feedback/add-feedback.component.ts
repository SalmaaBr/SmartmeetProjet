import { Component, OnInit, OnDestroy } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { EventService } from '../../services/event.service';
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
    date: new Date().toISOString().split('T')[0]
  };
  selectedEventId: number | null = null;
  userId: number | null = null;
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
    // Retrieve query params for eventId, userId, and eventTitle
    this.route.queryParams.subscribe(params => {
      this.selectedEventId = params['eventId'] ? +params['eventId'] : null;
      this.userId = params['userId'] ? +params['userId'] : null;
    });

    // Retrieve feedbackId from route params for editing
    this.route.paramMap.subscribe(params => {
      const feedbackId = params.get('id');
      if (feedbackId) {
        this.isEditing = true;
        this.currentFeedbackId = +feedbackId;
        this.loadFeedback(this.currentFeedbackId);
      }
    });

    // Load events
    this.loadEvents().subscribe();
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
          date: new Date(feedback.date).toISOString().split('T')[0]
        };
        const event = this.events.find(e => e.title === feedback.eventTitle);
        this.selectedEventId = event ? event.id : this.selectedEventId;
        if (!event && !this.selectedEventId) {
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
    if (!this.userId) {
      this.toastr.error('Utilisateur non identifié', 'Erreur');
      return;
    }

    if (this.isEditing && this.currentFeedbackId) {
      this.updateFeedback();
    } else {
      this.addFeedback();
    }
  }

  addFeedback() {
    const feedbackToSend: Feedback = {
      message: this.newFeedback.message,
      feeling: this.newFeedback.feeling,
      date: this.newFeedback.date
    };

    console.log('🚀 Données envoyées au serveur :', JSON.stringify(feedbackToSend, null, 2));
    console.log('🚀 Event ID :', this.selectedEventId);
    console.log('🚀 User ID :', this.userId);

    this.feedbackService.addFeedbackAndAffectToEvent(feedbackToSend, this.selectedEventId!, this.userId!).subscribe({
      next: (response) => {
        this.toastr.success('Feedback ajouté avec succès', 'Succès');
        console.log('✅ Feedback ajouté (réponse brute) :', response);
        console.log('✅ Titre de l\'événement dans la réponse :', response.eventTitle);
        this.resetForm();
        this.router.navigate(['/front/view-feedback'], { queryParams: { eventTitle: this.events.find(e => e.id === this.selectedEventId)?.title } });
      },
      error: (error) => {
        this.toastr.error('Erreur lors de l’ajout du feedback', 'Erreur');
        console.error('❌ Détails de l\'erreur :', {
          status: error.status,
          statusText: error.statusText,
          url: error.url,
          message: error.message,
          error: error.error
        });
      }
    });
  }

  updateFeedback() {
    if (!this.currentFeedbackId) {
      this.toastr.error('ID de feedback manquant', 'Erreur');
      return;
    }

    const updatedFeedback: Feedback = {
      ...this.newFeedback,
      idFeedback: this.currentFeedbackId
    };

    this.feedbackService.updateFeedback(updatedFeedback, this.selectedEventId!, this.userId!).subscribe({
      next: (response) => {
        this.toastr.success('Feedback mis à jour avec succès', 'Succès');
        console.log('✅ Feedback mis à jour (réponse brute) :', response);
        this.resetForm();
        this.router.navigate(['/front/view-feedback'], { queryParams: { eventTitle: this.events.find(e => e.id === this.selectedEventId)?.title } });
      },
      error: (error) => {
        this.toastr.error('Erreur lors de la mise à jour du feedback', 'Erreur');
        console.error('❌ Détails de l\'erreur :', error);
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
    this.userId = null;
    this.isEditing = false;
    this.currentFeedbackId = null;
  }

  selectFeeling(feeling: string) {
    this.newFeedback.feeling = feeling as TypeFeeling;
  }

  goToFeedbackList() {
    this.router.navigate(['/front/view-feedback']);
  }

  cancelEdit() {
    this.isEditing = false;
    this.currentFeedbackId = null;
    this.resetForm();
    this.router.navigate(['/front/view-feedback']);
  }
}
