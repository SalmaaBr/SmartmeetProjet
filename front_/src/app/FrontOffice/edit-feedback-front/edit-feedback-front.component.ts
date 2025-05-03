import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { Feedback, TypeFeeling } from "../../models/feedback.model";
import { FeedbackService } from "../../services/feedback.service";
import { EventService } from "../../services/event.service";
import { ToastrService } from "ngx-toastr";
import { ActivatedRoute, Router } from "@angular/router";
import { AuthService } from "../../auth/auth.service";
import { Observable, tap } from 'rxjs';

@Component({
  selector: 'app-edit-feedback-front',
  templateUrl: './edit-feedback-front.component.html',
  styleUrls: ['./edit-feedback-front.component.css']
})
export class EditFeedbackFrontComponent implements OnInit, OnDestroy {
  newFeedback: Feedback = {
    message: '',
    feeling: '' as TypeFeeling,
    date: new Date().toISOString().split('T')[0]
  };
  selectedEventId: number | null = null;
  events: any[] = [];
  eventTitle: string | null = null;
  userId: number | null = null;
  feedbackId: number | null = null;
  isEditMode: boolean = false;
  feelings: string[] = ['EXCELLENT', 'GOOD', 'AVERAGE', 'BAD', 'TERRIBLE'];
  private intervalId: any;

  constructor(
    private feedbackService: FeedbackService,
    private eventService: EventService,
    private toastr: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    console.log('Current User:', this.authService.getCurrentUser());
    this.loadEvents().subscribe(() => {
      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        this.route.queryParams.subscribe(queryParams => {
          this.eventTitle = queryParams['eventTitle'] || null;
          this.selectedEventId = queryParams['eventId'] ? +queryParams['eventId'] : null;
          this.userId = queryParams['userId'] ? +queryParams['userId'] : this.authService.getCurrentUser()?.userID || null;

          console.log('🔍 Received params:', {
            eventTitle: this.eventTitle,
            eventId: this.selectedEventId,
            userId: this.userId,
            feedbackId: id
          });

          if (id) {
            this.feedbackId = +id;
            this.isEditMode = true;
            this.loadFeedback(this.feedbackId);
          } else {
            this.isEditMode = false;

          }
        });
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
          this.toastr.error('Error loading events', 'Error');
          console.error('❌ Error loading events:', error);
        }
      })
    );
  }

  loadFeedback(id: number): void {
    this.feedbackService.getFeedbackById(id).subscribe({
      next: (feedback) => {
        this.newFeedback = {
          message: feedback.message,
          feeling: feedback.feeling,
          date: new Date(feedback.date).toISOString().split('T')[0]
        };
        this.eventTitle = feedback.eventTitle || this.eventTitle || 'Unknown Event';
        const event = this.events.find(e => e.title === feedback.eventTitle);
        this.selectedEventId = event ? event.id : this.selectedEventId;
        if (!event && !this.selectedEventId) {
          console.warn(`Event "${feedback.eventTitle}" not found`);
        }
        console.log('Loaded feedback:', feedback);
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.toastr.error('Error loading feedback', 'Error');
        console.error('Error loading feedback:', error);
        this.router.navigate(['/front/view-feedback'], { queryParams: { eventTitle: this.eventTitle } });
      }
    });
  }

  onSubmit(): void {
    if (this.isEditMode && this.feedbackId) {
      this.updateFeedback();
    } else {
      this.addFeedback();
    }
  }

  addFeedback(): void {
    if (!this.selectedEventId || !this.userId) {
      this.toastr.error('Valid event ID and user ID are required', 'Error');
      this.router.navigate(['/front/view-feedback'], { queryParams: { eventTitle: this.eventTitle } });
      return;
    }

    const feedbackToSend: Feedback = {
      message: this.newFeedback.message,
      feeling: this.newFeedback.feeling,
      date: this.newFeedback.date,
      eventTitle: this.eventTitle ?? undefined
    };

    console.log('Submitting new feedback:', feedbackToSend);
    this.feedbackService.addFeedbackAndAffectToEvent(feedbackToSend, this.selectedEventId, this.userId).subscribe({
      next: (response) => {
        this.toastr.success('Feedback added successfully', 'Success');
        this.resetForm();
        this.router.navigate(['/front/view-feedback'], { queryParams: { eventTitle: this.eventTitle } });
      },
      error: (error) => {
        const errorMessage = error.error?.message || 'Error adding feedback';
        this.toastr.error(errorMessage, 'Error');
        console.error('Error adding feedback:', error);
        if (error.status === 401) {
          this.toastr.error('Session expired. Please log in again.', 'Error');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          this.router.navigate(['/front/view-feedback'], { queryParams: { eventTitle: this.eventTitle } });
        }
      }
    });
  }

  updateFeedback(): void {
    const feedbackId = this.feedbackId;
    const selectedEventId = this.selectedEventId;
    const userId = this.userId;

    console.log('updateFeedback called with:', {
      feedbackId,
      selectedEventId,
      userId,
      newFeedback: this.newFeedback
    });

    if (!feedbackId || !selectedEventId || !userId) {
      this.toastr.error('Feedback ID, event ID, or user ID missing', 'Error');
      console.error('Validation failed:', {
        feedbackId,
        selectedEventId,
        userId
      });
      this.router.navigate(['/front/view-feedback'], { queryParams: { eventTitle: this.eventTitle } });
      return;
    }

    const feedbackToSend: Feedback = {
      ...this.newFeedback,
      idFeedback: feedbackId
    };

    console.log('Submitting update for feedback:', feedbackToSend);
    this.feedbackService.updateFeedback(feedbackToSend, selectedEventId, userId).subscribe({
      next: (response) => {
        console.log('Update successful:', response);
        this.toastr.success('Feedback updated successfully', 'Success');
        this.goBack();
      },
      error: (error) => {
        const errorMessage = error.error?.message || 'Error updating feedback';
        this.toastr.error(errorMessage, 'Error');
        console.error('Error updating feedback:', error);
        if (error.status === 401) {
          this.toastr.error('Session expired. Please log in again.', 'Error');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          this.router.navigate(['/front/view-feedback'], { queryParams: { eventTitle: this.eventTitle } });
        }
      }
    });
  }

  resetForm(): void {
    this.newFeedback = {
      message: '',
      feeling: '' as TypeFeeling,
      date: new Date().toISOString().split('T')[0]
    };
    this.selectedEventId = null;
    this.userId = null;
    this.cdr.detectChanges();
  }

  selectFeeling(feeling: string): void {
    this.newFeedback.feeling = feeling as TypeFeeling;
    this.cdr.detectChanges();
  }

  goBack(): void {
    if (!this.selectedEventId || !this.userId || !this.eventTitle) {
      console.error('Cannot navigate back: Missing required parameters', {
        eventTitle: this.eventTitle,
        eventId: this.selectedEventId,
        userId: this.userId
      });
      this.toastr.error('Cannot navigate: Missing event or user information', 'Error');
      this.router.navigate(['/front']);
      return;
    }

    console.log('Navigating to /front/view-feedback with params:', {
      eventTitle: this.eventTitle,
      eventId: this.selectedEventId,
      userId: this.userId
    });
    this.router.navigate(['/front/view-feedback'], {
      queryParams: {
        eventTitle: this.eventTitle,
        eventId: this.selectedEventId,
        userId: this.userId
      }
    });
  }
}
