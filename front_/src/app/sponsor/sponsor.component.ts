import { Component, OnInit } from '@angular/core';
import { Sponsor } from '../models/sponsor.model';
import { SponsorService } from '../services/sponsor.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-sponsor',
  templateUrl: './sponsor.component.html',
  styleUrls: ['./sponsor.component.css']
})
export class SponsorComponent implements OnInit {
  sponsors: Sponsor[] = [];
  newSponsor: Sponsor = { name: '', logo: '', contributionAmount: 0 };

  constructor(private sponsorService: SponsorService, private route: ActivatedRoute,
      private router: Router,) {}

  ngOnInit() {
    this.loadSponsors();
  }

  loadSponsors() {
    this.sponsorService.getAllSponsors().subscribe(data => {
      this.sponsors = data;
      console.log("test", this.sponsors)
    });
  }

  createSponsor() {
    this.sponsorService.createSponsor(this.newSponsor).subscribe(() => {
      this.loadSponsors();
    });

  }
  deleteSponsor(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet événement ?')) {
      this.sponsorService.deleteSponsor(id).subscribe(
        () => {
          this.sponsors = this.sponsors.filter(sponsor => sponsor.id !== id);
          console.log('Événement supprimé avec succès');
        },
        (error: any) => {
          console.error('Erreur lors de la suppression de l\'événement', error);
        }
      );
    }
  }
  
  editSponsor(id: number): void {
    console.log('Navigating to edit sponsor with ID:', id);
    this.router.navigate([`/admin/edit-sponsor/${id}`]);
  }
}