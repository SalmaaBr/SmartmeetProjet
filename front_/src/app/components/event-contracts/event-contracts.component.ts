import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SponsorStatsService } from '../../services/sponsor-stats.service';
import { Chart } from 'chart.js/auto';

interface Contract {
  id: number;
  sponsorName: string;
  amount: number;
  status: string;
  signingDate?: string;
  expiryDate?: string;
}

@Component({
  selector: 'app-event-contracts',
  templateUrl: './event-contracts.component.html',
  styleUrls: ['./event-contracts.component.css']
})
export class EventContractsComponent implements OnInit {
  eventId: number;
  contracts: Contract[] = [];
  stats: any = null;

  constructor(
    private route: ActivatedRoute,
    private sponsorStatsService: SponsorStatsService
  ) {
    this.eventId = +this.route.snapshot.paramMap.get('id')!;
  }

  ngOnInit(): void {
    this.loadContracts();
    this.loadStats();
  }

  loadContracts(): void {
    this.sponsorStatsService.getContracts(this.eventId).subscribe({
      next: (contracts) => {
        this.contracts = contracts;
      },
      error: (err) => console.error('Erreur chargement contrats:', err)
    });
  }

  loadStats(): void {
    this.sponsorStatsService.getEventStats(this.eventId).subscribe({
      next: (stats) => {
        this.stats = stats;
        this.renderCharts();
      },
      error: (err) => console.error('Erreur chargement stats:', err)
    });
  }

  renderCharts(): void {
    if (!this.stats) return;

    // Graphique 1: Répartition des montants par sponsor
    if (this.stats.amountsBySponsor) {
      new Chart('amountsChart', {
        type: 'doughnut',
        data: {
          labels: this.stats.amountsBySponsor.map((s: any) => s.sponsorName),
          datasets: [{
            data: this.stats.amountsBySponsor.map((s: any) => s.amount),
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56']
          }]
        },
        options: { plugins: { title: { display: true, text: 'Montants par Sponsor' } } }
      });
    }

    // Graphique 3: Statut des contrats
    if (this.stats.statusDistribution) {
      new Chart('statusChart', {
        type: 'pie',
        data: {
          labels: this.stats.statusDistribution.map((s: any) => s.status),
          datasets: [{
            data: this.stats.statusDistribution.map((s: any) => s.count),
            backgroundColor: ['#36A2EB', '#FFCE56', '#FF6384']
          }]
        },
        options: { plugins: { title: { display: true, text: 'Statut des Contrats' } } }
      });
    }
  }
}
