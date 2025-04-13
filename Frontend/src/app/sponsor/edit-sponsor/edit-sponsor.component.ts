import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Sponsor } from 'src/app/models/sponsor.model';
import { SponsorService } from 'src/app/services/sponsor.service';

@Component({
  selector: 'app-edit-sponsor',
  templateUrl: './edit-sponsor.component.html',
  styleUrls: ['./edit-sponsor.component.css']
})
export class EditSponsorComponent {
  partnershipTypes = ['GOLD', 'SILVER', 'BRONZE'];
  sponsor: Sponsor = {
    name: '',
    logo: '',
    contributionAmount: 0,
    partnershipType: undefined,
    events: []
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private sponsorService: SponsorService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.sponsorService.getSponsorById(+id).subscribe(
        (data: Sponsor) => {
          this.sponsor = data;
        },
        (error) => {
          console.error('Erreur lors du chargement du sponsor', error);
        }
      );
    }
  }

  updateSponsor(): void {
    this.sponsorService.updateSponsor(this.sponsor.id!, this.sponsor).subscribe(
      () => {
        this.router.navigate(['/admin/sponsors']);
      },
      (error) => {
        console.error('Erreur lors de la mise à jour du sponsor', error);
      }
    );
  }

}
