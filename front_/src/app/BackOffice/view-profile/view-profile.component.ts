import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { ActivatedRoute ,Router} from '@angular/router';
import * as L from 'leaflet';
import { EventService, Event } from '../../services/event.service';
import { MapRoutingService } from '../../services/map-routing.service';
import { ElementRef, QueryList, ViewChildren, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { RecommendationService } from '../../services/recommendation.service';



@Component({
  selector: 'app-view-profile',
  templateUrl: './view-profile.component.html',
  styleUrls: ['./view-profile.component.css']
})
export class ViewProfileComponent implements OnInit {
  user: User | undefined;
  isLoading = true;
  error: string | null = null;
  isCurrentUser = false;
  email: string | null = localStorage.getItem("email");
  myEvents: Event[] = [];
  recommendedEvents: Event[] = [];
  mapRefs: L.Map[] = [];

@ViewChildren('mapContainer') mapContainers!: QueryList<ElementRef>;

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private eventService: EventService,
    private mapRoutingService: MapRoutingService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private recommendationService: RecommendationService
  ) {}

  ngOnInit(): void {
    if (this.email) {
      this.loadUserByEmail(this.email);
      this.loadMyEvents();
      this.loadRecommendedEvents();
      this.eventService.getMyEvents().subscribe(events => {
        this.myEvents = events;
        this.cdr.detectChanges(); // Nécessaire pour que @ViewChildren se synchronise
      });
      
    } else {
      this.error = 'Email not found in local storage';
      this.isLoading = false;
    }
  }

  ngAfterViewInit(): void {
    this.mapContainers.changes.subscribe(() => {
      this.initializeEventMaps();
    });
  }

  loadRecommendedEvents(): void {
    this.recommendationService.getRecommendationsForCurrentUser().subscribe({
      next: (events) => {
        this.recommendedEvents = events;
      },
      error: (err) => {
        if (err.status === 401) {
          // Rediriger vers la page de login ou rafraîchir le token
          this.router.navigate(['/login']);
        }
        console.error('Error loading recommended events:', err);
      }
    });
  }

  loadMyEvents(): void {
    this.eventService.getMyEvents().subscribe(events => {
      this.myEvents = events;
      this.cdr.detectChanges();
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
  
  

  loadUserByEmail(email: string): void {
    this.isLoading = true;
    this.error = null;
    this.isCurrentUser = true;

    this.userService.getUserByEmail(email).subscribe({
      next: (user) => {
        this.user = user;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load user data';
        this.isLoading = false;
        console.error('Error loading user by email:', err);
      }
    });
  }

  getUserRoles(): string {
    if (!this.user || !this.user.userRole) return 'No roles assigned';
    return Array.from(this.user.userRole).join(', ');
  }
}
