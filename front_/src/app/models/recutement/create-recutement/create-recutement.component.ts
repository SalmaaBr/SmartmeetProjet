import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RecutementService } from '../../../services/recutement.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-recutement',
  templateUrl: './create-recutement.component.html',
  styleUrls: ['./create-recutement.component.css']
})
export class CreateRecutementComponent {
  recruitmentForm: FormGroup;  // Le formulaire réactif
  successMessage: string = '';  // Message de succès après création

  constructor(private router: Router,private fb: FormBuilder, private recutementService: RecutementService) {
    // Initialisation du formulaire avec des validations
    this.recruitmentForm = this.fb.group({
      title: ['', Validators.required],      // Le titre est obligatoire
      description: ['', Validators.required]  // La description est obligatoire
    });
  }

  // Méthode pour envoyer le formulaire lorsque l'utilisateur clique sur "Créer"
  onSubmit() {
    if (this.recruitmentForm.valid) {
      // Ajouter la date de création au modèle
      const formData = {
        ...this.recruitmentForm.value,    // Inclure les données du formulaire
        createdAt: new Date().toISOString()  // Ajouter la date actuelle (au format ISO)
      };

      // Appel du service pour envoyer les données à l'API
      this.recutementService.createMonitoringRecruitment(formData).subscribe({
        next: () => {
          this.successMessage = 'Recrutement créé avec succès !';  // Affiche un message de succès
          this.recruitmentForm.reset();  // Réinitialise le formulaire
        },
        error: (err) => {
          console.error('Erreur lors de la création du recrutement:', err);
          this.successMessage = 'Erreur lors de la création du recrutement.';  // Affiche un message d'erreur
        }
      });
    } else {
      this.successMessage = 'Veuillez remplir tous les champs.';

            // Redirection vers la liste des événements après 2 secondes (optionnel)
            setTimeout(() => {
              this.router.navigate(['/admin/events']);
            }, 20);
    }
  }



}
