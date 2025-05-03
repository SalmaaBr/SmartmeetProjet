import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MentalHealthService } from '../../services/mental-health.service';
import { MentalHealth } from '../../models/mental-health';
import { Router } from '@angular/router';

@Component({
  selector: 'app-mental-health',
  templateUrl: './mental-health.component.html',
  styleUrls: ['./mental-health.component.css']
})
export class MentalHealthComponent implements OnInit {
  mentalHealthForm: FormGroup;
  editMentalHealth: MentalHealth | null = null;
  submissionMessage: string = '';
  submissionCount: number = 0;

  constructor(
    private fb: FormBuilder,
    private mentalHealthService: MentalHealthService,
    private router: Router
  ) {
    this.mentalHealthForm = this.fb.group({
      idMentalHealth: [null],
      responseMoment: ['', Validators.required],
      stressLevel: [1, [Validators.required, Validators.min(1), Validators.max(5)]],
      emotionalState: ['', Validators.required],
      supportNeed: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const state = history.state;
    if (state.mentalHealth) {
      this.editMentalHealth = { ...state.mentalHealth };
      if (this.editMentalHealth) {
        this.mentalHealthForm.patchValue(this.editMentalHealth);
      }
    }
    // Initialiser le compteur depuis le localStorage
    const count = localStorage.getItem('submissionCount');
    this.submissionCount = count ? parseInt(count) : 0;
  }

  // Méthode unifiée pour la soumission du formulaire
  onSubmit(): void {
    if (this.mentalHealthForm.valid) {
      if (this.editMentalHealth) {
        this.updateMentalHealth();
      } else {
        this.addMentalHealth();
      }
    }
  }

  addMentalHealth(): void {
    if (this.mentalHealthForm.valid) {
      this.submissionMessage = 'Submitting...';

      this.mentalHealthService.addMentalHealth(this.mentalHealthForm.value).subscribe({
        next: (response: any) => {
          this.submissionCount++;
          localStorage.setItem('submissionCount', this.submissionCount.toString());

          if (this.submissionCount % 3 === 0 && response.prediction) {
            // Afficher uniquement tous les 3 envois (3ème, 6ème, 9ème, etc.)
            localStorage.setItem('mentalHealthPrediction', JSON.stringify(response.prediction));
            this.submissionMessage = 'Mental health prediction available in your profile!';

            // Redirection vers le profil après un délai de 2 secondes
            setTimeout(() => {
              this.router.navigate(['/profile']);
            }, 2000);
          } else {
            // Message normal pour les autres soumissions
            const currentInCycle = this.submissionCount % 3 || 3;
            this.submissionMessage = `Form submitted successfully! (${currentInCycle}/3)`;

            // Redirection vers la liste après un délai de 2 secondes
            setTimeout(() => {
              this.navigateToList();
            }, 2000);
          }

          this.resetForm();
        },
        error: (error) => {
          console.error('Error during submission:', error);
          this.submissionMessage = 'Error during submission. Please try again.';
        }
      });
    }
  }

  // Méthode pour réinitialiser le compteur
  private resetSubmissionCount(): void {
    this.submissionCount = 0;
    localStorage.removeItem('submissionCount');
    this.submissionMessage = ''; // Effacer le message
  }

  updateMentalHealth(): void {
    if (this.editMentalHealth && this.mentalHealthForm.valid) {
      const updatedMentalHealth: MentalHealth = {
        ...this.editMentalHealth,
        ...this.mentalHealthForm.value,
        idMentalHealth: this.editMentalHealth.idMentalHealth
      };
      this.mentalHealthService.updateMentalHealth(updatedMentalHealth).subscribe({
        next: () => {
          this.submissionMessage = 'Mental health check-in updated successfully';
          this.resetForm();

          // Redirection vers la liste après un délai de 2 secondes
          setTimeout(() => {
            this.router.navigate(['/admin/mental-health-list']);
          }, 2000);
        },
        error: (error) => {
          console.error('Erreur lors de la mise à jour:', error);
          this.submissionMessage = 'Error updating record. Please try again.';
        }
      });
    }
  }

  resetForm(): void {
    this.mentalHealthForm.reset({
      idMentalHealth: null,
      responseMoment: '',
      stressLevel: 1,
      emotionalState: '',
      supportNeed: ''
    });
    this.editMentalHealth = null;
  }

  navigateToList(): void {
    this.router.navigate(['/admin/mental-health-history']);
  }
}
