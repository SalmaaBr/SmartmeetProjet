import { Component, OnInit } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { EventService } from '../../services/event.service';
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
  events: any[] = [];
  selectedEventTitle: string | null = null;
  stats: FeedbackStats | null = null;

  sentimentChartData: any = {
    labels: ['POSITIVE', 'HAPPY', 'LOVE', 'EXCITED', 'SAD', 'ANGRY', 'AFRAID', 'DISAPPOINTED', 'CONFUSED', 'SURPRISED', 'NEUTRAL'],
    datasets: [
      {
        label: 'Sentiment Percentages (%)',
        data: [],
        backgroundColor: [
          'rgba(75, 192, 192, 0.2)', // POSITIVE
          'rgba(255, 205, 86, 0.2)', // HAPPY
          'rgba(255, 99, 132, 0.2)', // LOVE
          'rgba(54, 162, 235, 0.2)', // EXCITED
          'rgba(153, 102, 255, 0.2)', // SAD
          'rgba(255, 159, 64, 0.2)', // ANGRY
          'rgba(201, 203, 207, 0.2)', // AFRAID
          'rgba(100, 149, 237, 0.2)', // DISAPPOINTED
          'rgba(173, 216, 230, 0.2)', // CONFUSED
          'rgba(144, 238, 144, 0.2)', // SURPRISED
          'rgba(169, 169, 169, 0.2)' // NEUTRAL
        ],
        borderColor: [
          'rgba(75, 192, 192, 1)',
          'rgba(255, 205, 86, 1)',
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64, 1)',
          'rgba(201, 203, 207, 1)',
          'rgba(100, 149, 237, 1)',
          'rgba(173, 216, 230, 1)',
          'rgba(144, 238, 144, 1)',
          'rgba(169, 169, 169, 1)'
        ],
        borderWidth: 1
      }
    ]
  };

  chartOptions = {
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
        ticks: { stepSize: 10 }
      }
    }
  };

  displayedColumns: string[] = ['idFeedback','username', 'eventTitle', 'feeling', 'sentiment', 'message', 'date', 'actions'];

  constructor(
    private feedbackService: FeedbackService,
    private eventService: EventService,
    private toastr: ToastrService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadAllFeedbacks();
    this.loadEvents();
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
        const uniqueEvents = Array.from(
          new Map((response ?? []).map(event => [event.title, event])).values()
        );
        this.events = [{ id: null, title: 'All events' }, ...uniqueEvents];
        console.log('✅ Events loaded:', this.events);
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des événements', 'Erreur');
        console.error('❌ Erreur:', error);
      }
    });
  }


  loadAllFeedbacks(): void {
    this.feedbackService.getAllFeedbacks().subscribe({
      next: (response) => {
        this.feedbacks = response ?? []; // Ensure feedbacks is never null
        console.log('Feedbacks loaded:', this.feedbacks);
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des feedbacks', 'Erreur');
        console.error('Erreur:', error);
      }
    });
  }

  loadStats(eventTitle?: string): void {
    this.feedbackService.getSentimentStatistics(eventTitle).subscribe({
      next: (response) => {
        this.stats = response ?? null; // Ensure stats is assigned safely
        this.updateChart();
        console.log('Sentiment stats loaded:', this.stats);
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des statistiques', 'Erreur');
        console.error('Erreur:', error);
        this.stats = null; // Reset stats on error
        this.updateChart(); // Update chart to reflect empty state
      }
    });
  }

  updateChart(): void {
    if (this.stats?.sentimentPercentages) {
      const sentimentPercentages = this.stats.sentimentPercentages; // Assign to local variable
      const percentages = this.sentimentChartData.labels.map((label: string) => {
        return sentimentPercentages[label] ?? 0; // Safe access with fallback
      });
      this.sentimentChartData.datasets[0].data = percentages;
      this.sentimentChartData = { ...this.sentimentChartData };
    } else {
      this.sentimentChartData.datasets[0].data = [];
      this.sentimentChartData = { ...this.sentimentChartData };
    }
  }

  filterFeedbacks(): void {
    if (this.selectedEventTitle === 'All events') {
      // When "All events" is selected, fetch all feedbacks
      this.loadAllFeedbacks();
      this.loadStats(); // Load stats for all events (no argument, defaults to undefined)
    } else {
      // Convert null to undefined for the event title
      const eventTitleToFilter: string | undefined = this.selectedEventTitle ?? undefined;

      // Filter by the selected event title
      this.feedbackService.getFeedbacksByEventTitle(eventTitleToFilter).subscribe({
        next: (response) => {
          this.feedbacks = response ?? [];
          this.toastr.success('Feedbacks filtrés avec succès', 'Succès');
          console.log('Filtered feedbacks:', this.feedbacks);
        },
        error: (error) => {
          this.toastr.error('Erreur lors du filtrage', 'Erreur');
          console.error('Erreur:', error);
        }
      });
      this.loadStats(eventTitleToFilter); // Load stats for the selected event
    }
  }
  deleteFeedback(id: number): void {
    this.feedbackService.deleteFeedback(id).subscribe({
      next: () => {
        this.toastr.success('Feedback supprimé avec succès', 'Succès');
        this.feedbacks = this.feedbacks.filter(feedback => feedback.idFeedback !== id);
        this.loadAllFeedbacks();
        this.loadEvents();
        this.loadStats(this.selectedEventTitle || undefined);
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
