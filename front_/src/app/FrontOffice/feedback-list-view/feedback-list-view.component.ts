import { Component, OnInit } from '@angular/core';
import { Feedback, FeedbackStats } from '../../models/feedback.model';
import { FeedbackService } from '../../services/feedback.service';
import { ToastrService } from 'ngx-toastr';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-feedback-list-view',
  templateUrl: './feedback-list-view.component.html',
  styleUrls: ['./feedback-list-view.component.css']
})
export class FeedbackListViewComponent implements OnInit {
  feedbacks: Feedback[] = [];
  selectedEventTitle: string | null = null;
  stats: FeedbackStats | null = null;
  isLoading: boolean = false;
  eventId: number | null = null;
  userId: number | null = null;

  sentimentChartData: any = {
    labels: ['POSITIVE', 'HAPPY', 'LOVE', 'EXCITED', 'SAD', 'ANGRY', 'AFRAID', 'DISAPPOINTED', 'CONFUSED', 'SURPRISED', 'NEUTRAL'],
    datasets: [
      {
        label: 'Sentiment Percentages (%)',
        data: [],
        backgroundColor: [
          '#28a745',
          '#28a745',
          '#28a745',
          '#28a745',
          '#dc3545',
          '#dc3545',
          '#dc3545',
          '#dc3545',
          '#17a2b8',
          '#ffc107',
          '#6c757d'
        ],
        borderColor: [
          '#28a745',
          '#28a745',
          '#28a745',
          '#28a745',
          '#dc3545',
          '#dc3545',
          '#dc3545',
          '#dc3545',
          '#17a2b8',
          '#ffc107',
          '#6c757d'
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

  constructor(
    private feedbackService: FeedbackService,
    private toastr: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Retrieve query parameters
    this.route.queryParams.subscribe(params => {
      this.selectedEventTitle = params['eventTitle'] || null;
      this.eventId = params['eventId'] ? +params['eventId'] : null;
      this.userId = params['userId'] ? +params['userId'] : null;

      console.log('🔍 Received params:', {
        eventTitle: this.selectedEventTitle,
        eventId: this.eventId,
        userId: this.userId
      });

      // Validate user authentication
      const currentUser = this.authService.getCurrentUser();
      console.log('👤 Current user:', currentUser);
      if (!this.userId && currentUser?.userID) {
        this.userId = currentUser.userID;
      }

      // Check for required parameters
      if (!this.selectedEventTitle || !this.eventId || !this.userId) {
        this.toastr.error('Missing required parameters (event title, event ID, or user ID)', 'Error');
        this.router.navigate(['/front']);
        return;
      }

      // Load feedbacks
      this.loadFeedbacksByEventTitle(this.selectedEventTitle);
    });
  }

  loadFeedbacksByEventTitle(eventTitle: string): void {
    this.isLoading = true;
    this.feedbackService.getFeedbacksByEventTitle(eventTitle).subscribe({
      next: (response) => {
        this.feedbacks = response ?? [];
        console.log('✅ Filtered feedbacks:', this.feedbacks);
        this.loadStats(eventTitle);
        this.isLoading = false;
      },
      error: (error) => {
        this.toastr.error('Error loading feedback for this event', 'Error');
        console.error('❌ Error:', error);
        this.feedbacks = [];
        this.stats = null;
        this.updateChart();
        this.isLoading = false;
      }
    });
  }

  loadStats(eventTitle: string): void {
    this.feedbackService.getSentimentStatistics(eventTitle).subscribe({
      next: (response) => {
        this.stats = response ?? null;
        this.updateChart();
        console.log('✅ Sentiment stats loaded:', this.stats);
      },
      error: (error) => {
        this.toastr.error('Error loading sentiment statistics', 'Error');
        console.error('❌ Error:', error);
        this.stats = null;
        this.updateChart();
      }
    });
  }

  updateChart(): void {
    if (this.stats?.sentimentPercentages) {
      const sentimentPercentages = this.stats.sentimentPercentages;
      const percentages = this.sentimentChartData.labels.map((label: string) => {
        return sentimentPercentages[label] ?? 0;
      });
      this.sentimentChartData.datasets[0].data = percentages;
      this.sentimentChartData = { ...this.sentimentChartData };
    } else {
      this.sentimentChartData.datasets[0].data = [];
      this.sentimentChartData = { ...this.sentimentChartData };
    }
  }

  deleteFeedback(id: number): void {
    if (confirm('Are you sure you want to delete this feedback?')) {
      this.feedbackService.deleteFeedback(id).subscribe({
        next: () => {
          this.toastr.success('Feedback deleted successfully', 'Success');
          if (this.selectedEventTitle) {
            this.loadFeedbacksByEventTitle(this.selectedEventTitle);
          }
        },
        error: (error) => {
          this.toastr.error('Error deleting feedback', 'Error');
          console.error('❌ Error:', error);
        }
      });
    }
  }

  editFeedback(feedback: Feedback): void {
    if (!feedback.idFeedback) {
      this.toastr.error('Feedback ID missing', 'Error');
      return;
    }
    if (!this.eventId || !this.userId) {
      this.toastr.error('Event ID or User ID missing', 'Error');
      console.error('🔍 Missing parameters:', {
        eventId: this.eventId,
        userId: this.userId,
        selectedEventTitle: this.selectedEventTitle
      });
      return;
    }
    console.log('Navigating to /front/edit-feedback/' + feedback.idFeedback, {
      eventId: this.eventId,
      userId: this.userId,
      eventTitle: this.selectedEventTitle
    });
    this.router.navigate(['/front/edit-feedback', feedback.idFeedback], {
      queryParams: {
        eventTitle: this.selectedEventTitle,
        eventId: this.eventId,
        userId: this.userId
      }
    });
  }

  getSentimentClass(sentiment?: string): string {
    if (!sentiment) return 'bg-secondary';

    sentiment = sentiment.toUpperCase();

    switch(sentiment) {
      case 'POSITIVE':
      case 'HAPPY':
      case 'LOVE':
      case 'EXCITED':
        return 'bg-success';
      case 'NEUTRAL':
      case 'SURPRISED':
        return 'bg-warning text-dark';
      case 'NEGATIVE':
      case 'SAD':
      case 'ANGRY':
      case 'AFRAID':
      case 'DISAPPOINTED':
        return 'bg-danger';
      case 'CONFUSED':
        return 'bg-info text-dark';
      default:
        return 'bg-secondary';
    }
  }
}
