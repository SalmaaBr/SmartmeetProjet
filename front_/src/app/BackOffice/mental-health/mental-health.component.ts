// src/app/components/mental-health/mental-health.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MentalHealthService } from '../../services/mental-health.service';
import { MentalHealth } from '../../models/mental-health';

@Component({
  selector: 'app-mental-health',
  templateUrl: './mental-health.component.html',
  styleUrls: ['./mental-health.component.css']
})
export class MentalHealthComponent implements OnInit {
  mentalHealths: MentalHealth[] = [];
  mentalHealthForm: FormGroup;
  editMentalHealth: MentalHealth | null = null;

  constructor(private fb: FormBuilder, private mentalHealthService: MentalHealthService) {
    // Initialisation du formulaire avec des validations, incluant idMentalHealth
    this.mentalHealthForm = this.fb.group({
      idMentalHealth: [null], // Champ pour l'ID, non affiché mais utilisé pour les mises à jour
      responseMoment: ['', Validators.required],
      stressLevel: [1, [Validators.required, Validators.min(1), Validators.max(5)]],
      emotionalState: ['', Validators.required],
      supportNeed: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadMentalHealths();
  }

  // Charger tous les enregistrements de santé mentale
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

  // Démarrer l'édition d'un enregistrement
  startEdit(mentalHealth: MentalHealth): void {
    this.editMentalHealth = { ...mentalHealth }; // Créer une copie pour éviter les modifications directes
    this.mentalHealthForm.patchValue(this.editMentalHealth); // Remplir le formulaire, y compris l'ID
  }

  // Ajouter un nouvel enregistrement de santé mentale
  addMentalHealth(): void {
    if (this.mentalHealthForm.valid) {
      this.mentalHealthService.addMentalHealth(this.mentalHealthForm.value).subscribe({
        next: (data) => {
          this.mentalHealths.push(data);
          this.resetForm();
        },
        error: (error) => {
          console.error('Erreur lors de l’ajout:', error);
        }
      });
    }
  }

  // Mettre à jour un enregistrement existant
  updateMentalHealth(): void {
    if (this.editMentalHealth && this.mentalHealthForm.valid) {
      const updatedMentalHealth: MentalHealth = {
        ...this.editMentalHealth, // Conserver les données existantes (comme user, submissionDate)
        ...this.mentalHealthForm.value, // Mettre à jour avec les nouvelles valeurs du formulaire
        idMentalHealth: this.editMentalHealth.idMentalHealth // Assurer que l'ID est inclus
      };
      this.mentalHealthService.updateMentalHealth(updatedMentalHealth).subscribe({
        next: (data) => {
          const index = this.mentalHealths.findIndex(m => m.idMentalHealth === data.idMentalHealth);
          if (index !== -1) {
            this.mentalHealths[index] = data;
          }
          this.editMentalHealth = null;
          this.resetForm();
        },
        error: (error) => {
          console.error('Erreur lors de la mise à jour:', error);
        }
      });
    }
  }

  // Réinitialiser le formulaire
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

  // Supprimer un enregistrement de santé mentale
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
