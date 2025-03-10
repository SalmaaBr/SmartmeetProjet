import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RecutementService } from '../../../services/recutement.service';

@Component({
  selector: 'app-edit-recruitment',
  templateUrl: './edit-recruitment.component.html',
  styleUrls: ['./edit-recruitment.component.css']
})
export class EditRecruitmentComponent implements OnInit {

  recrutement: any = {
    title: '',
    description: ''
  };
  id!: number;

  constructor(
    private route: ActivatedRoute,
    private recrutementService: RecutementService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));  // Récupérer l'ID depuis la route
    if (this.id) {
      this.getMonitoringRecruitment();  // Récupérer les données du recrutement
    }
  }

  // Récupérer les détails du recrutement à éditer
  getMonitoringRecruitment(): void {
    this.recrutementService.getMonitoringRecruitmentById(this.id).subscribe(
      (data) => {
        this.recrutement = data;
      },
      (error) => {
        console.error('Erreur de récupération du recrutement', error);
      }
    );
  }

  // Mettre à jour le recrutement
  updateRecruitment(): void {
    const { title, description } = this.recrutement; // Utiliser les valeurs du formulaire

    // Appeler le service pour mettre à jour
    this.recrutementService.updateMonitoringRecruitment(this.id, title, description).subscribe(
      (data) => {
        alert('Recrutement mis à jour avec succès');
        this.router.navigate(['/admin/monitoring-recruitment-list']);  // Redirige après la mise à jour
      },
      (error) => {
        console.error('Erreur lors de la mise à jour du recrutement', error);
      }
    );
  }
}
