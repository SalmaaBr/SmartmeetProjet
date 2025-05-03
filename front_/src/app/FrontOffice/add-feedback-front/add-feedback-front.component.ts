import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Feedback, TypeFeeling } from '../../models/feedback.model';
import { FeedbackService } from '../../services/feedback.service';
import { ToastrService } from 'ngx-toastr';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-add-feedback-front',
  templateUrl: './add-feedback-front.component.html',
  styleUrls: ['./add-feedback-front.component.css']
})
export class AddFeedbackFrontComponent implements OnInit {
  newFeedback: Feedback = {
    message: '',
    feeling: '' as TypeFeeling,
    date: new Date().toISOString().split('T')[0] // Format ISO for LocalDate
  };
  eventTitle: string | null = null;
  eventId: number | null = null;
  userId: number | null = null;

  constructor(
    private feedbackService: FeedbackService,
    private toastr: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.eventTitle = params['eventTitle'] || null;
      this.eventId = params['eventId'] ? +params['eventId'] : null; // Convert to number
      this.userId = params['userId'] ? +params['userId'] : null; // Convert to number
      console.log('🔍 Received params:', { eventTitle: this.eventTitle, eventId: this.eventId, userId: this.userId });
    });
  }

  onSubmit(): void {
    if (!this.eventTitle || !this.eventId || !this.userId) {
      this.toastr.warning('Event title, event ID, and user ID are required', 'Warning');
      return;
    }

    const feedbackToSend: Feedback = {
      message: this.newFeedback.message,
      feeling: this.newFeedback.feeling,
      date: this.newFeedback.date,
      eventTitle: this.eventTitle
    };

    console.log('🚀 Sending feedback:', JSON.stringify(feedbackToSend, null, 2));

    this.feedbackService.addFeedbackAndAffectToEvent(feedbackToSend, this.eventId, this.userId).subscribe({
      next: (response) => {
        this.toastr.success('Feedback added successfully', 'Success');
        console.log('✅ Feedback added:', response);
        this.resetForm();
        this.goBack(); // Use goBack() for consistent navigation
      },
      error: (error) => {
        const errorMessage = error.error || 'Error adding feedback';
        this.toastr.error(errorMessage, 'Error');
        console.error('❌ Error details:', {
          status: error.status,
          statusText: error.statusText,
          url: error.url,
          message: error.message,
          error: error.error
        });
        this.goBack(); // Navigate back even on error, consistent with EditFeedbackFrontComponent
      }
    });
  }

  resetForm(): void {
    this.newFeedback = {
      message: '',
      feeling: '' as TypeFeeling,
      date: new Date().toISOString().split('T')[0]
    };
    this.cdr.detectChanges();
  }

  selectFeeling(feeling: string): void {
    console.log('🎯 Selected feeling:', feeling);
    this.newFeedback.feeling = feeling as TypeFeeling;
    console.log('🎯 newFeedback:', this.newFeedback);
    this.cdr.detectChanges();
  }

  goBack(): void {
    if (!this.eventTitle || !this.eventId || !this.userId) {
      console.error('Cannot navigate back: Missing required parameters', {
        eventTitle: this.eventTitle,
        eventId: this.eventId,
        userId: this.userId
      });
      this.toastr.error('Cannot navigate: Missing event or user information', 'Error');
      this.router.navigate(['/front']);
      return;
    }

    console.log('Navigating to /front/view-feedback with params:', {
      eventTitle: this.eventTitle,
      eventId: this.eventId,
      userId: this.userId
    });
    this.router.navigate(['/front/view-feedback'], {
      queryParams: {
        eventTitle: this.eventTitle,
        eventId: this.eventId,
        userId: this.userId
      }
    });
  }
}
