import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Sponsor } from 'src/app/models/sponsor.model';
import { SponsorService } from 'src/app/services/sponsor.service';

@Component({
  selector: 'app-create-sponsor',
  templateUrl: './create-sponsor.component.html',
  styleUrls: ['./create-sponsor.component.css']
})
export class CreateSponsorComponent {
  sponsorData: Sponsor = {
    name: '',
    logo: '',
    contributionAmount: 0,
    partnershipType: undefined,
    events: []
  };
  
  partnershipTypes = ['GOLD', 'SILVER', 'BRONZE'];
  
  constructor(private router: Router, private sponsorService: SponsorService) {}
  
  createSponsor() {
    this.sponsorService.createSponsor(this.sponsorData).subscribe(
      (response) => {
        this.router.navigate(['/admin/sponsors']);
      },
      (error) => {
        console.error('Erreur lors de la création du sponsor', error);
      }
    );
  }
}
