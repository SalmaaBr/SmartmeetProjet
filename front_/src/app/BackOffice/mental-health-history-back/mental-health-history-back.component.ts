import { Component } from '@angular/core';
import {MentalHealth} from "../../models/mental-health";
import {MentalHealthService} from "../../services/mental-health.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-mental-health-history-back',
  templateUrl: './mental-health-history-back.component.html',
  styleUrls: ['./mental-health-history-back.component.css']
})
export class MentalHealthHistoryBackComponent {
  mentalHealths: MentalHealth[] = [];

  constructor(private mentalHealthService: MentalHealthService, private router: Router) {}

  ngOnInit(): void {
    this.loadMentalHealths();
  }

  loadMentalHealths(): void {
    this.mentalHealthService.getMentalHealthsByCurrentUser().subscribe({
      next: (data) => {
        this.mentalHealths = data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des MentalHealths pour l\'utilisateur courant:', error);
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
