import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SponsorEventService } from '../../services/sponsor-event.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-sponsor-event-assignment',
  templateUrl: './sponsor-event-assignment.component.html',
  standalone: true,
  styleUrls: ['./sponsor-event-assignment.component.css']
})
export class SponsorEventAssignmentComponent implements OnInit {
  assignmentForm: FormGroup;
  events: any[] = [];
  sponsorId: number;

  constructor(
    private fb: FormBuilder,
    private sponsorEventService: SponsorEventService,
    public activeModal: NgbActiveModal
  ) {
    this.assignmentForm = this.fb.group({
      eventId: ['', Validators.required],
      terms: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(0)]],
      expiryDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.sponsorEventService.getAllEvents().subscribe(
      (events) => {
        this.events = events;
      },
      (error) => {
        console.error('Error loading events:', error);
      }
    );
  }

  onSubmit(): void {
    if (this.assignmentForm.valid) {
      const assignment = {
        sponsorId: this.sponsorId,
        ...this.assignmentForm.value
      };

      this.sponsorEventService.assignSponsorToEvent(assignment).subscribe(
        (response) => {
          this.activeModal.close(response);
        },
        (error) => {
          console.error('Error assigning sponsor to event:', error);
        }
      );
    }
  }
}
