import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';


declare var JitsiMeetExternalAPI: any;

@Component({
  selector: 'app-jitsi-meeting',
  templateUrl: './jitsi-meeting.component.html'
})
export class JitsiMeetingComponent {
  meetingName: string | null = null;
  domain: string = 'meet.jit.si';
  api: any;

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    const meetingId = this.route.snapshot.paramMap.get('id');
    this.http.get<any>(`http://localhost:8082/api/meetings/${meetingId}`)
      .subscribe(meeting => {
        this.meetingName = meeting.meetingLink.split('/').pop(); // Extraire la roomName
        setTimeout(() => this.initJitsi(), 0);
      });
  }
  

  initJitsi() {
    const container = document.querySelector('#jitsi-container');
    if (!container) {
      console.error('Conteneur Jitsi non trouvé');
      return;
    }

    const options = {
      roomName: this.meetingName,
      width: '100%',
      height: 600,
      parentNode: container
    };

    this.api = new JitsiMeetExternalAPI(this.domain, options);
  }
}
