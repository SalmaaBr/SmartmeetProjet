// src/app/back-office/feedback-list/feedback-list.component.ts
import { Component, OnInit } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { EventService } from '../../services/event.service'; // Ajouté
import { ToastrService } from 'ngx-toastr';
import { Feedback, FeedbackStats } from '../../models/feedback.model';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-feedback-list',
  templateUrl: './feedback-list.component.html',
  styleUrls: ['./feedback-list.component.css']
})
export class FeedbackListComponent implements OnInit {
  feedbacks: Feedback[] = [];
  events: any[] = []; // Remplace eventTitles
  selectedEventTitle: string | null = null; // null pour "Tous les événements"
  stats: FeedbackStats | null = null;

  chartData: any = {
    labels: [],
    datasets: [
      {
        label: 'Moyenne des notes',
        data: [],
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1
      }
    ]
  };
  chartOptions = {
    scales: {
      y: {
        beginAtZero: true,
        max: 5,
        ticks: { stepSize: 1 }
      }
    }
  };

  displayedColumns: string[] = ['idFeedback', 'eventTitle', 'feeling', 'message', 'date', 'actions'];

  constructor(
    private feedbackService: FeedbackService,
    private eventService: EventService, // Ajouté
    private toastr: ToastrService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadAllFeedbacks();
    this.loadEvents(); // Nouvelle méthode
    this.loadStats();
    this.route.queryParams.subscribe(params => {
      if (params['refresh']) {
        console.log('Forcing refresh of feedback list');
        this.loadAllFeedbacks();
        this.loadEvents();
        this.loadStats();
      }
    });
  }

  loadEvents(): void {
    this.eventService.getEvents().subscribe({
      next: (response) => {
        this.events = response;
        console.log('Events loaded:', response);
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des événements', 'Erreur');
        console.error('Erreur:', error);
      }
    });
  }

  loadAllFeedbacks(): void {
    this.feedbackService.getAllFeedbacks().subscribe({
      next: (response) => {
        this.feedbacks = response;
        console.log('Feedbacks loaded:', response);
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des feedbacks', 'Erreur');
        console.error('Erreur:', error);
      }
    });
  }

  loadStats(eventTitle?: string): void {
    this.feedbackService.getFeedbackStats(eventTitle).subscribe({
      next: (response) => {
        this.stats = response;
        this.updateChart();
        console.log('Stats loaded:', response);
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des statistiques', 'Erreur');
        console.error('Erreur:', error);
      }
    });
  }

  updateChart(): void {
    if (this.stats && this.stats.averageFeelingByEvent) {
      this.chartData.labels = Object.keys(this.stats.averageFeelingByEvent);
      this.chartData.datasets[0].data = Object.values(this.stats.averageFeelingByEvent);
      this.chartData = { ...this.chartData };
    } else {
      this.chartData.labels = [];
      this.chartData.datasets[0].data = [];
      this.chartData = { ...this.chartData };
    }
  }

  filterFeedbacks(): void {
    this.feedbackService.getFeedbacksByEventTitle(this.selectedEventTitle || undefined).subscribe({
      next: (response) => {
        this.feedbacks = response;
        this.toastr.success('Feedbacks filtrés avec succès', 'Succès');
        console.log('Filtered feedbacks:', response);
      },
      error: (error) => {
        this.toastr.error('Erreur lors du filtrage', 'Erreur');
        console.error('Erreur:', error);
      }
    });
    this.loadStats(this.selectedEventTitle || undefined);
  }

  deleteFeedback(id: number): void {
    this.feedbackService.deleteFeedback(id).subscribe({
      next: () => {
        this.toastr.success('Feedback supprimé avec succès', 'Succès');
        this.feedbacks = this.feedbacks.filter(feedback => feedback.idFeedback !== id);
        this.loadAllFeedbacks(); // Recharger pour cohérence
        this.loadEvents(); // Mettre à jour la liste des événements
      },
      error: (error) => {
        this.toastr.error('Erreur de suppression', 'Erreur');
        console.error('Erreur:', error);
      }
    });
  }

  editFeedback(feedback: Feedback): void {
    if (!feedback.idFeedback) {
      this.toastr.error('ID du feedback manquant', 'Erreur');
      return;
    }
    console.log('Navigating to /admin/add-feedback/', feedback.idFeedback);
    this.router.navigate(['/admin/add-feedback', feedback.idFeedback]);
  }
}
