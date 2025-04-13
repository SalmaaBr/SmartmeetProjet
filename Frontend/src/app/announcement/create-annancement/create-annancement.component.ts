import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Announcement } from 'src/app/models/announcement.model';
import { AnnouncementService } from 'src/app/services/announcement.service';

@Component({
  selector: 'app-create-annancement',
  templateUrl: './create-annancement.component.html',
  styleUrls: ['./create-annancement.component.css']
})
export class CreateAnnancementComponent {
  announcementData: Announcement = {
    title: '',
    description: '',
    status: 'UNRESOLVED', // Default status
    // datePosted and eventId are optional in the model
  };
  
  // Remove old enums and add status options
  statusOptions: ('UNRESOLVED' | 'RESOLVED')[] = ['UNRESOLVED', 'RESOLVED'];
  selectedEventId?: number; // To capture the event ID from the form
  
  constructor(private router: Router, private announcementService: AnnouncementService) {}
  
  createAnnouncement() {
    if (this.selectedEventId === undefined) {
      console.error('Event ID is required');
      return;
    }
  
    this.announcementService.createAnnouncement(this.announcementData, this.selectedEventId).subscribe(
      (response) => {
        this.router.navigate(['/front/announcements']); // Adjust the redirect path
      },
      (error) => {
        console.error('Error creating announcement', error);
      }
    );
  }
}
