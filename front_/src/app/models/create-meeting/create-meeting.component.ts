import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-meeting',
  templateUrl: './create-meeting.component.html',
  styleUrls: ['./create-meeting.component.css']
})
export class CreateMeetingComponent {
  meetingName: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  createMeeting(): void {
    if (!this.meetingName) {
      alert('Veuillez entrer un nom pour la réunion');
      return;
    }

    this.http.post<any>('http://localhost:8082/api/meetings', { meetingName: this.meetingName }).subscribe(
      (response) => {
        this.router.navigate([`/meeting/${response.id}`]);
      },
      (error) => {
        console.error('Erreur lors de la création de la réunion', error);
      }
    );
  }
}