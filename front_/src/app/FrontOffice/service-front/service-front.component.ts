import { Component, OnInit, AfterViewInit, ChangeDetectorRef, ElementRef, ViewChildren, QueryList } from '@angular/core';
import { CalendarOptions, EventInput } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { EventService, Event } from '../../services/event.service';
import { RecutementService } from '../../services/recutement.service';
import { UsercalenderService, EventUserCalendar } from '../../services/usercalender.service';
import { EventLikeService } from '../../services/event-like.service';
import * as AOS from 'aos';
import { MatSnackBar } from '@angular/material/snack-bar';
import { InteractivePublicationService } from 'src/app/services/interactive-publication.service';
import { InteractivePublication } from 'src/app/models/interactive-publication.model';
import * as L from 'leaflet';
import { MapRoutingService } from '../../services/map-routing.service';
import { forkJoin } from 'rxjs';
import { tap } from 'rxjs/operators';

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
export class ServiceFrontComponent implements OnInit, AfterViewInit {
  events: Event[] = [];
  recrutements: any[] = [];
  errorMessage = '';
  publications: any[] = [];
  myEvents: Event[] = [];
  mapRefs: L.Map[] = [];
  isAuthenticated: boolean = false; // Track authentication status
  currentUserId: number | null = null; // Store current user ID

  TypeIPublicationStatus = TypeIPublicationStatus;
  TypeIPublicationVisibility = TypeIPublicationVisibility;
  isEditing = false;
  currentPublicationId: number | null = null;
  newPublication: InteractivePublication = new InteractivePublication();

  @ViewChildren('mapContainer') mapContainers!: QueryList<ElementRef>;

  unifiedCalendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    weekends: true,
    events: [],
    dateClick: this.handleDateClick.bind(this),
  };

  constructor(
    private eventService: EventService,
    private recrutementService: RecutementService,
    private publicationService: InteractivePublicationService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
    private mapRoutingService: MapRoutingService,
    private usercalenderService: UsercalenderService,
    private eventLikeService: EventLikeService
  ) {}

  ngOnInit(): void {
    this.checkAuthentication();
    this.loadEvents();
    this.loadRecruitments();
    this.loadPublications();
    this.loadMyEvents();
    this.loadUnifiedEventsForCalendar();
    setTimeout(() => AOS.init(), 0);
  }

  checkAuthentication(): void {
    // TODO: Implement actual authentication check
    // This is a placeholder; replace with your auth service
    this.isAuthenticated = true; // Assume user is authenticated
    this.currentUserId = 1; // Replace with actual user ID from auth service
  }

  loadLikeData(): void {
    const likeStatusObservables = this.events.map((event, index) =>
      this.eventLikeService.getLikeStatus(event.id).pipe(
        tap(status => {
          this.events[index].isLiked = status === 1;
        })
      )
    );
  
    const totalLikesObservables = this.events.map((event, index) =>
      this.eventLikeService.getTotalLikes(event.id).pipe(
        tap(total => {
          this.events[index].totalLikes = total;
        })
      )
    );
  
    forkJoin([...likeStatusObservables, ...totalLikesObservables]).subscribe({
      complete: () => {
        this.cdr.detectChanges(); // Appeler detectChanges une seule fois à la fin
      },
      error: (err) => console.error('Error fetching like data:', err)
    });
  }


  toggleLike(eventId: number, index: number): void {
    if (!this.isAuthenticated || !this.currentUserId) {
      this.snackBar.open('Please log in to like events', 'Close', { duration: 3000 });
      return;
    }
  
    this.eventLikeService.toggleLike(eventId).subscribe({
      next: (message) => {
        this.snackBar.open(message, 'Close', { duration: 3000 });
        // Recharger l'état du like et le nombre total de likes
        forkJoin({
          status: this.eventLikeService.getLikeStatus(eventId),
          total: this.eventLikeService.getTotalLikes(eventId)
        }).subscribe({
          next: ({ status, total }) => {
            this.events[index].isLiked = status === 1;
            this.events[index].totalLikes = total;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Error refreshing like data:', err);
            this.snackBar.open('Error refreshing like data', 'Close', { duration: 3000 });
          }
        });
      },
      error: (err) => {
        this.snackBar.open(err.message || 'Error toggling like', 'Close', { duration: 3000 });
      }
    });
  }


  ngAfterViewInit(): void {
    this.mapContainers.changes.subscribe(() => {
      this.initializeEventMaps();
    });
  }

  initializeEventMaps(): void {
    this.mapRefs.forEach(map => map.remove());
    this.mapRefs = [];

    navigator.geolocation.getCurrentPosition(
      position => {
        const userLatLng = L.latLng(position.coords.latitude, position.coords.longitude);

        this.mapContainers.forEach((containerRef, index) => {
          const event = this.myEvents[index];
          if (event.latitude && event.longitude) {
            const eventLatLng = L.latLng(event.latitude, event.longitude);
            const map = L.map(containerRef.nativeElement, {
              center: eventLatLng,
              zoom: 13,
              zoomControl: false,
              attributionControl: false
            });

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
              attribution: '© OpenStreetMap contributors'
            }).addTo(map);

            L.marker(eventLatLng).addTo(map)
              .bindPopup(`<b>${event.title}</b><br>${event.location}`).openPopup();

            L.marker(userLatLng, {
              icon: L.icon({
                iconUrl: 'https://cdn-icons-png.flaticon.com/512/64/64113.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [1, -34]
              })
            }).addTo(map).bindPopup("📍 Vous êtes ici");

            this.mapRoutingService.getRoute(
              [userLatLng.lng, userLatLng.lat],
              [eventLatLng.lng, eventLatLng.lat]
            ).subscribe((route: any) => {
              const coords = route.features[0].geometry.coordinates;
              const latlngs = coords.map(([lng, lat]: [number, number]) => [lat, lng]);
              if (latlngs.length > 0) {
                L.polyline(latlngs, { color: 'blue', weight: 4 }).addTo(map);
                map.fitBounds(L.latLngBounds(latlngs).pad(0.3));
              }
            }, error => {
              console.error('Erreur récupération route :', error);
            });

            this.mapRefs.push(map);
          }
        });
      },
      error => {
        console.error('Erreur géolocalisation :', error);
      }
    );
  }

  formatShortDate(dateString: string): string {
    if (!dateString) return '';
    return new Date(dateString).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  loadEvents(): void {
    this.eventService.getEvents().subscribe({
      next: (data) => {
        this.events = data.map(event => ({
          ...event,
          totalLikes: event.likes, // Mapper le champ `likes` à `totalLikes`
          isLiked: false // Initialiser `isLiked` à false, sera mis à jour par `loadLikeData`
        }));
        if (this.isAuthenticated && this.currentUserId) {
          this.loadLikeData();
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error:', err)
    });
  }

  loadRecruitments(): void {
    this.recrutementService.getAllMonitoringRecruitments().subscribe({
      next: data => this.recrutements = data,
      error: err => {
        this.errorMessage = 'Erreur lors de la récupération des recrutements.';
        console.error('Erreur:', err);
      }
    });
  }  



  loadPublications(): void {
    this.publicationService.getAllPublications().subscribe({
      next: data => this.publications = data,
      error: err => console.error('Error fetching publications:', err)
    });
  }

  loadMyEvents(): void {
    this.eventService.getMyEvents().subscribe({
      next: data => {
        this.myEvents = data;
        this.cdr.detectChanges();
      },
      error: err => console.error('Erreur lors de la récupération des événements participés :', err)
    });
  }

  loadUnifiedEventsForCalendar(): void {
    // Charger les événements auxquels l'utilisateur participe
    this.eventService.getMyEvents().subscribe(myEvents => {
      const myEventsMapped = myEvents.map(event => ({
        title: event.title,
        start: event.startTime,
        end: event.endTime,
        description: event.description,
        location: event.location,
        color: 'blue' // Couleur pour les événements participés
      }));

      // Charger les événements du calendrier utilisateur
      this.usercalenderService.getUserEventsCalender().subscribe({
        next: (userEvents: EventUserCalendar[]) => {
          const userEventsMapped = userEvents.map((event: EventUserCalendar) => ({
            title: event.name,
            start: event.startDate,
            end: event.endDate,
            color: 'green' // Couleur pour les événements du calendrier
          }));

          // Fusionner les deux listes d'événements
          this.unifiedCalendarOptions.events = [...myEventsMapped, ...userEventsMapped];
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Erreur lors de la récupération des événements utilisateur :', err);
          this.snackBar.open('Erreur lors du chargement des événements', 'Fermer', { duration: 3000 });
        }
      });
    });
  }

  handleDateClick(arg: any) {
    const name = prompt("Nom de l'événement :");
    const endDateStr = prompt('Date/heure de fin (YYYY-MM-DDTHH:mm:ss) :', arg.dateStr + 'T00:00:00');
    if (name) {
      const newEvent: EventUserCalendar = {
        name: name,
        createdDate: `${arg.dateStr}T00:00:00`,
        startDate: `${arg.dateStr}T00:00:00`,
        endDate: endDateStr || undefined
      };
      this.usercalenderService.addEvent(newEvent).subscribe({
        next: () => {
          this.snackBar.open('Événement ajouté avec succès !', 'Fermer', { duration: 3000 });
          this.loadUnifiedEventsForCalendar();
        },
        error: (err) => {
          console.error("Erreur lors de l'ajout de l'événement", err);
          this.snackBar.open('Erreur lors de l’ajout de l’événement', 'Fermer', { duration: 3000 });
        }
      });
    }
  }

  deletePublication(id: number): void {
    if (confirm('Are you sure you want to delete this publication?')) {
      this.publicationService.deletePublication(id).subscribe({
        next: () => this.loadPublications(),
        error: err => console.error('Error deleting publication:', err)
      });
    }
  }

  editPublication(pub: InteractivePublication): void {
    this.isEditing = true;
    this.currentPublicationId = pub.iPublicationId!;
    this.newPublication = { ...pub };
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.currentPublicationId = null;
    this.resetForm();
  }

  onSubmit(): void {
    if (!this.newPublication.title) return;

    if (this.isEditing && this.currentPublicationId) {
      this.publicationService
        .updatePublication(this.currentPublicationId, this.newPublication)
        .subscribe({
          next: () => {
            this.loadPublications();
            this.cancelEdit();
          },
          error: (err: any) => {
            console.error('Error updating publication:', err);
          }
        });
    } else {
      this.publicationService
        .createPublication(this.newPublication)
        .subscribe({
          next: () => {
            this.loadPublications();
            this.resetForm();
          },
          error: (err: any) => {
            console.error('Error creating publication:', err);
          }
        });
    }
  }

  resetForm(): void {
    this.newPublication = new InteractivePublication();
  }

  participate(eventId: number, index: number): void {
    this.eventService.participateToEvent(eventId).subscribe({
      next: (response) => {
        this.snackBar.open(response.message, 'Fermer', { duration: 3000 });
        this.events[index].maxParticipants = Math.max(0, this.events[index].maxParticipants - 1);
        this.loadEvents();
        this.loadMyEvents();
        this.loadUnifiedEventsForCalendar(); // Recharger le calendrier unifié
      },
      error: (err) => {
        console.error('Erreur lors de la participation:', err);
        this.snackBar.open(err.error.message || 'Erreur lors de la participation', 'Fermer', { duration: 3000 });
      }
    });
  }
}