import { Component, OnInit } from '@angular/core';
import { MentalHealthService } from '../../services/mental-health.service';
import { MentalHealth } from '../../models/mental-health';
import { Router } from '@angular/router';

@Component({
  selector: 'app-mental-health-list',
  templateUrl: './mental-health-list.component.html',
  styleUrls: ['./mental-health-list.component.css']
})
export class MentalHealthListComponent implements OnInit {
  mentalHealths: MentalHealth[] = [];

  constructor(private mentalHealthService: MentalHealthService, private router: Router) {}

  ngOnInit(): void {
    this.loadMentalHealths();
  }

  loadMentalHealths(): void {
    this.mentalHealthService.getAllMentalHealths().subscribe({
      next: (data) => {
        this.mentalHealths = data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des MentalHealths:', error);
      }
    });
  }

  startEdit(mentalHealth: MentalHealth): void {
    this.router.navigate(['/admin/mental-health'], { state: { mentalHealth } });
  }

  deleteMentalHealth(id: number): void {
    this.mentalHealthService.deleteMentalHealth(id).subscribe({
      next: () => {
        this.mentalHealths = this.mentalHealths.filter(m => m.idMentalHealth !== id);
      },
      error: (error) => {
        console.error('Erreur lors de la suppression:', error);
      }
    });
  }
}
