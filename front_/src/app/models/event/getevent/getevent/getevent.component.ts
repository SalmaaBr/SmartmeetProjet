import { Component, OnInit } from '@angular/core';
import { EventService, Event } from '../../../../services/event.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-getevent',
  templateUrl: './getevent.component.html',
  styleUrls: ['./getevent.component.css']
})
export class GeteventComponent implements OnInit {
  events: Event[] = [];
  currentPage: number = 1;  // Page actuelle
  eventsPerPage: number = 5; // Nombre d'événements par page
  totalPages: number = 0; // Nombre total de pages

  constructor(private eventService: EventService, private router: Router) {}

  ngOnInit(): void {
    this.eventService.getEvents().subscribe(data => {
      this.events = data;
      this.totalPages = Math.ceil(this.events.length / this.eventsPerPage); // Calculer le nombre total de pages
    });
  }

  // Fonction pour récupérer les événements de la page actuelle
  getPaginatedEvents(): Event[] {
    const startIndex = (this.currentPage - 1) * this.eventsPerPage;
    const endIndex = startIndex + this.eventsPerPage;
    return this.events.slice(startIndex, endIndex);
  }

  // Fonction pour aller à la page suivante
  goToNextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  // Fonction pour aller à la page précédente
  goToPreviousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  // Fonction pour aller à une page spécifique
  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  // Nouvelle méthode pour aller à la première page
  goToFirstPage(): void {
    this.currentPage = 1;
  }

  // Nouvelle méthode pour aller à la dernière page
  goToLastPage(): void {
    this.currentPage = this.totalPages;
  }

  getPageNumbers(): number[] {
    const pages = [];
    for (let i = 1; i <= this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  deleteEvent(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet événement ?')) {
      this.eventService.deleteEvent(id).subscribe(
        () => {
          this.events = this.events.filter(event => event.id !== id);
          console.log('Événement supprimé avec succès');
          this.totalPages = Math.ceil(this.events.length / this.eventsPerPage);
        },
        (error) => {
          console.error('Erreur lors de la suppression de l\'événement', error);
        }
      );
    }
  }

  editEvent(id: number): void {
    console.log('Navigating to edit event with ID:', id);
    this.router.navigate([`/admin/edit-event/${id}`]);
  }
}
