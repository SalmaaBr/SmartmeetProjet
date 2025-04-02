import { Component, OnInit } from '@angular/core';
import { EventService, Event } from '../../services/event.service'; // Vérifie le bon chemin
import { RecutementService } from '../../services/recutement.service';
import * as AOS from 'aos';

import { InteractivePublicationService } from 'src/app/services/interactive-publication.service';
import { InteractivePublication } from 'src/app/models/interactive-publication.model';

export enum TypeIPublicationStatus {
  PUBLISHED = 'PUBLISHED',
  DRAFT = 'DRAFT',
  MODERATED = 'MODERATED',
  ARCHIVED = 'ARCHIVED'
}

export enum TypeIPublicationVisibility {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE',
  RESTRICTED = 'RESTRICTED'
}

@Component({
  selector: 'app-service-front',
  templateUrl: './service-front.component.html',
  styleUrls: ['./service-front.component.css']
})
export class ServiceFrontComponent implements OnInit {
  events: Event[] = [];
  recrutements: any[] = [];  // Tableau pour stocker les recrutements
  errorMessage: string = '';
  
  // Variables liées aux publications
  TypeIPublicationStatus = TypeIPublicationStatus;
  TypeIPublicationVisibility = TypeIPublicationVisibility;
  isEditing = false;
  currentPublicationId: number | null = null;
  newPublication: InteractivePublication = new InteractivePublication();
  publications: any[] = [];

  constructor(
    private eventService: EventService,
    private recrutementService: RecutementService,
    private publicationService: InteractivePublicationService
  ) {}

  ngOnInit(): void {
    // Appel aux méthodes pour récupérer les événements
    this.loadEvents();

    // Appel aux méthodes pour récupérer les recrutements
    this.loadRecruitments();

    // Charger les publications
    this.loadPublications();

    // Initialisation d'AOS pour les animations
    setTimeout(() => AOS.init(), 0);

    console.log('ServiceFrontComponent Loaded ✅');
  }

  // Charger les événements
  loadEvents(): void {
    this.eventService.getEvents().subscribe(
      (data: Event[]) => {
        this.events = data; // Le mapping est déjà fait dans le service
      },
      (error) => {
        console.error("Erreur :", error);
      }
    );
  }
  
  

  // Charger les recrutements
  loadRecruitments(): void {
    this.recrutementService.getAllMonitoringRecruitments().subscribe({
      next: (data) => {
        this.recrutements = data;  // Stocke les recrutements dans la variable
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de la récupération des recrutements.';  // Affiche un message d'erreur
        console.error('Erreur:', error);
      }
    });
  }

  // Charger les publications
  loadPublications(): void {
    this.publicationService.getAllPublications()
      .subscribe({
        next: (data: InteractivePublication[]) => {
          this.publications = data;
          console.log('Publications loaded successfully:', this.publications);
        },
        error: (error) => console.error('Error fetching publications:', error)
      });
  }

  // Supprimer une publication
  deletePublication(id: number): void {
    if (confirm('Are you sure you want to delete this publication?')) {
      this.publicationService.deletePublication(id)
        .subscribe({
          next: () => {
            console.log('Publication deleted successfully');
            this.loadPublications();
          },
          error: (error) => console.error('Error deleting publication:', error)
        });
    }
  }

  // Editer une publication - charger dans le formulaire
  editPublication(pub: InteractivePublication): void {
    console.log("🚀 ~ ServiceFrontComponent ~ editPublication ~ pub:", pub);
    this.isEditing = true;
    this.currentPublicationId = pub.iPublicationId!;
    this.newPublication = { ...pub }; // Clone the publication object
  }

  // Annuler le mode édition
  cancelEdit(): void {
    this.isEditing = false;
    this.currentPublicationId = null;
    this.resetForm();
  }

  // Soumettre le formulaire
  onSubmit(): void {
    if (!this.newPublication.title) {
      console.warn('Please fill in all required fields.');
      return;
    }

    if (this.isEditing && this.currentPublicationId) {
      // Mettre à jour une publication existante
      this.publicationService.updatePublication(this.currentPublicationId, this.newPublication)
        .subscribe({
          next: () => {
            console.log('Publication updated successfully');
            this.loadPublications();
            this.cancelEdit();
          },
          error: (error) => console.error('Error updating publication:', error)
        });
    } else {
      // Créer une nouvelle publication
      this.publicationService.createPublication(this.newPublication as InteractivePublication)
        .subscribe({
          next: (response: InteractivePublication) => {
            console.log('Publication added successfully:', response);
            this.loadPublications();
            this.resetForm();
          },
          error: (error) => console.error('Error adding publication:', error)
        });
    }
  }

  // Réinitialiser les champs du formulaire
  resetForm(): void {
    this.newPublication = new InteractivePublication();
  }
}
