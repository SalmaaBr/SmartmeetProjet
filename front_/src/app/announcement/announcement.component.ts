import { Component, OnInit } from '@angular/core';
import { AnnouncementService } from '../services/announcement.service';
import { Announcement } from '../models/announcement.model';

@Component({
  selector: 'app-announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent implements OnInit {
  announcements: Announcement[] = []; // Liste des annonces
  selectedAnnouncement: Announcement | null = null; // Annonce sélectionnée pour modification
  newAnnouncement: Announcement = { id: 0, title: '', description: '' }; // Nouvelle annonce à créer
  eventId: number = 1; // ID de l'événement (à ajuster selon vos besoins)
  isEditMode: boolean = false; // Mode édition ou création

  constructor(private announcementService: AnnouncementService) {}

  ngOnInit(): void {
    this.loadAnnouncements(); // Charger les annonces à l'initialisation
  }

  // Récupérer toutes les annonces
  loadAnnouncements(): void {
    this.announcementService.getAllAnnouncements().subscribe(
      (data: Announcement[]) => {
        this.announcements = data;
      },
      (error: any) => {
        console.error('Erreur lors de la récupération des annonces', error);
      }
    );
  }

  // Récupérer une annonce par ID (pour modification)
  getAnnouncementById(id: number): void {
    this.announcementService.getAnnouncementById(id).subscribe(
      (data: Announcement) => {
        this.selectedAnnouncement = data;
        this.newAnnouncement = { ...data }; // Pré-remplir le formulaire
        this.isEditMode = true; // Passer en mode édition
      },
      (error: any) => {
        console.error('Erreur lors de la récupération de l’annonce', error);
      }
    );
  }

  // Créer une nouvelle annonce
  createAnnouncement(): void {
    this.announcementService.createAnnouncement(this.newAnnouncement, this.eventId).subscribe(
      () => {
        this.loadAnnouncements(); // Rafraîchir la liste
        this.resetForm(); // Réinitialiser le formulaire
      },
      (error) => {
        console.error('Erreur lors de la création de l’annonce', error);
      }
    );
  }

  // Mettre à jour une annonce existante
  updateAnnouncement(): void {
    if (this.newAnnouncement.id) {
      this.announcementService.updateAnnouncement(this.newAnnouncement.id, this.newAnnouncement).subscribe(
        () => {
          this.loadAnnouncements(); // Rafraîchir la liste
          this.resetForm(); // Réinitialiser le formulaire
        },
        (error) => {
          console.error('Erreur lors de la mise à jour de l’annonce', error);
        }
      );
    }
  }

  // Supprimer une annonce
  deleteAnnouncement(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette annonce ?')) {
      this.announcementService.deleteAnnouncement(id).subscribe(
        () => {
          this.loadAnnouncements(); // Rafraîchir la liste
        },
        (error:any) => {
          console.error('Erreur lors de la suppression de l’annonce', error);
        }
      );
    }
  }

  // Préparer le formulaire pour la création
  prepareCreate(): void {
    this.resetForm();
    this.isEditMode = false;
  }

  // Réinitialiser le formulaire
  resetForm(): void {
    this.newAnnouncement = { id: 0, title: '', description: '' };
    this.selectedAnnouncement = null;
    this.isEditMode = false;
  }

  // Soumettre le formulaire (création ou mise à jour)
  submitForm(): void {
    if (this.isEditMode) {
      this.updateAnnouncement();
    } else {
      this.createAnnouncement();
    }
  }
}