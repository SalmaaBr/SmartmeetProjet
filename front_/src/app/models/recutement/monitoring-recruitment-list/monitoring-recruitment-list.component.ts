import { Component, OnInit } from '@angular/core';
import { RecutementService } from '../../../services/recutement.service';
import { Router } from '@angular/router';  // Importer Router pour la redirection après l'édition

@Component({
  selector: 'app-monitoring-recruitment-list',
  templateUrl: './monitoring-recruitment-list.component.html',
  styleUrls: ['./monitoring-recruitment-list.component.css']
})
export class MonitoringRecruitmentListComponent implements OnInit {

  public recrutements: any[] = [];  // Remplacez "any" par le type MonitoringRecruitment si vous avez un modèle défini
  errorMessage: string = '';  // Message d'erreur en cas de problème
  recrutementToEdit: any = {};  // Données du recrutement à éditer
  isEditing: boolean = false;  // Message d'erreur en cas de problème

  constructor(private recrutementService: RecutementService, private router: Router) { }

  ngOnInit(): void {
    // Appel à la méthode du service pour récupérer les recrutements
    this.recrutementService.getAllMonitoringRecruitments().subscribe({
      next: (data) => {
        this.recrutements = data;  // Stocke les recrutements dans le tableau
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de la récupération des recrutements.';  // Message d'erreur
        console.error('Erreur:', error);
      }
    });
  }

  // Méthode pour supprimer un recrutement
  deleteRecutement(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce recrutement ?')) {
      this.recrutementService.deleteMonitoringRecruitment(id).subscribe({
        next: () => {
          // Met à jour la liste des recrutements après suppression
          this.recrutements = this.recrutements.filter(recrutement => recrutement.id !== id);
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la suppression du recrutement.';
          console.error('Erreur:', error);
        }
      });
    }
  }

  // Rediriger vers l'édition d'un recrutement
  editRecruitment(id: number): void {
    this.router.navigate(['/admin/edit-recruitment', id]);  // Redirige vers le composant d'édition avec l'ID du recrutement
  }


}
