import { Component, OnInit, AfterViewInit, ChangeDetectorRef, ElementRef, ViewChildren, QueryList } from '@angular/core';
import { CalendarOptions, EventInput } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { EventService, Event } from '../../services/event.service';
import { RecutementService } from '../../services/recutement.service';
import { UsercalenderService, EventUserCalendar, Meeting } from '../../services/usercalender.service';
import { EventLikeService } from '../../services/event-like.service';
import { AuthService } from '../../auth/auth.service';
import * as AOS from 'aos';
import { MatSnackBar } from '@angular/material/snack-bar';
import { InteractivePublicationService } from 'src/app/services/interactive-publication.service';
import { InteractivePublication } from 'src/app/models/interactive-publication.model';
import * as L from 'leaflet';
import { MapRoutingService } from '../../services/map-routing.service';
import { forkJoin } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PublicationInteractionService } from 'src/app/services/publication-interaction.service';
import { PublicationComment } from 'src/app/models/publication-comment.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { UserService } from 'src/app/services/user.service';
import { ContentModerationService, ModerationResult } from 'src/app/services/content-moderation.service';

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

export enum TypeIPublicationModerationStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  FLAGGED = 'FLAGGED'
}

@Component({
  selector: 'app-service-front',
  templateUrl: './service-front.component.html',
  styleUrls: ['./service-front.component.css'],
})
export class ServiceFrontComponent implements OnInit, AfterViewInit {
  events: Event[] = [];
  recrutements: any[] = [];
  errorMessage = '';
  publications: InteractivePublication[] = [];
  myEvents: Event[] = [];
  mapRefs: L.Map[] = [];
  isAuthenticated: boolean = false;
  currentUserId: number | null = null;

  // Publication related enums
  TypeIPublicationStatus = TypeIPublicationStatus;
  TypeIPublicationVisibility = TypeIPublicationVisibility;
  TypeIPublicationModerationStatus = TypeIPublicationModerationStatus;
  isEditing = false;
  currentPublicationId: number | null = null;
  newPublication: InteractivePublication = new InteractivePublication();

  // Moderation related properties
  moderationErrors: {
    title: string | null,
    description: string | null
  } = {
    title: null,
    description: null
  };

  isCheckingModeration = false;
  moderationResult: ModerationResult | null = null;

  // Loading states
  loading = {
    publications: false,
    comments: {} as {[key: number]: boolean},
    likes: {} as {[key: number]: boolean},
    submitting: false
  };

  // Comments form
  commentForm: FormGroup;
  editCommentForm: FormGroup;

  @ViewChildren('mapContainer') mapContainers!: QueryList<ElementRef>;

  unifiedCalendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    weekends: true,
    events: [],
    dateClick: this.handleDateClick.bind(this),
    eventClick: this.handleEventClick.bind(this),
  };

  constructor(
    private eventService: EventService,
    private recrutementService: RecutementService,
    private publicationService: InteractivePublicationService,
    private interactionService: PublicationInteractionService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
    private mapRoutingService: MapRoutingService,
    private usercalenderService: UsercalenderService,
    private eventLikeService: EventLikeService,
    private authService: AuthService,
    private fb: FormBuilder,
    private userService: UserService,
    private contentModerationService: ContentModerationService
  ) {
    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(2)]]
    });

    this.editCommentForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(2)]]
    });
  }

  ngOnInit(): void {
    this.checkAuthentication();
    this.loadEvents();
    this.loadRecruitments();
    this.loadPublications();
    this.loadMyEvents();
    this.loadUnifiedEventsForCalendar();
    this.loadUserProfile();
    setTimeout(() => AOS.init(), 0);
  }

  checkAuthentication(): void {
    this.isAuthenticated = this.authService.isAuthenticated();
    if (this.isAuthenticated) {
      this.currentUserId = this.authService.getCurrentUserId();
    }
  }

  loadUserProfile(): void {
    const email = localStorage.getItem("email");
    if (email) {
      this.userService.getUserByEmail(email).subscribe({
        next: (user) => {
          if (user.userID) {
            this.currentUserId = user.userID;
          }
        },
        error: (err) => {
          this.errorMessage = 'Failed to load profile';
        }
      });
    }
  }

  loadLikeData(): void {
    if (!this.isAuthenticated || !this.currentUserId) {
      return;
    }

    const likeStatusObservables = this.events.map((event, index) =>
      this.eventLikeService.getLikeStatus(event.id).pipe(
        tap((status) => {
          this.events[index].isLiked = status === 1;
        })
      )
    );

    const totalLikesObservables = this.events.map((event, index) =>
      this.eventLikeService.getTotalLikes(event.id).pipe(
        tap((total) => {
          this.events[index].totalLikes = total;
        })
      )
    );

    forkJoin([...likeStatusObservables, ...totalLikesObservables]).subscribe({
      complete: () => {
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching like data:', err);
        this.snackBar.open('Error loading like data', 'Close', { duration: 3000 });
      },
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
        forkJoin({
          status: this.eventLikeService.getLikeStatus(eventId),
          total: this.eventLikeService.getTotalLikes(eventId),
        }).subscribe({
          next: ({ status, total }) => {
            this.events[index].isLiked = status === 1;
            this.events[index].totalLikes = total;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Error refreshing like data:', err);
            this.snackBar.open('Error refreshing like data', 'Close', { duration: 3000 });
          },
        });
      },
      error: (err) => {
        console.error('Error toggling like:', err);
        this.snackBar.open(err.message || 'Error toggling like', 'Close', { duration: 3000 });
      },
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
          totalLikes: event.likes,
          isLiked: false
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
    this.loading.publications = true;
    this.publicationService.getAllPublications()
      .pipe(
        finalize(() => this.loading.publications = false)
      )
      .subscribe({
        next: (data: InteractivePublication[]) => {
          this.publications = data;
          console.log('Publications loaded successfully:', this.publications);

          // Load likes and comments for each publication
          this.publications.forEach(pub => {
            this.loadLikesAndComments(pub);
          });
        },
        error: (error) => {
          console.error('Error fetching publications:', error);
          this.errorMessage = 'Failed to load publications. Please try again later.';
        }
      });
  }

  loadLikesAndComments(publication: InteractivePublication): void {
    const pubId = publication.ipublicationId;

    // Skip if publication ID is not defined
    if (pubId === undefined || pubId === null) {
      console.warn('Publication ID is undefined or null. Skipping API calls for likes and comments.');
      return;
    }

    // Initialize loading state for this publication if not exists
    if (!this.loading.comments[pubId]) {
      this.loading.comments[pubId] = false;
    }
    if (!this.loading.likes[pubId]) {
      this.loading.likes[pubId] = false;
    }

    // Initialize comments array if not exists
    if (!publication.comments) {
      publication.comments = [];
    }

    // Load comments only if they are being shown
    if (publication.showComments) {
      this.loading.comments[pubId] = true;
      this.interactionService.getCommentsByPublicationId(pubId)
        .pipe(
          finalize(() => this.loading.comments[pubId] = false)
        )
        .subscribe({
          next: comments => {
            publication.comments = comments || [];
            publication.commentCount = comments ? comments.length : 0;
          },
          error: error => {
            console.error(`Error loading comments for publication ${pubId}:`, error);
            publication.comments = [];
            publication.commentCount = 0;
          }
        });
    }

    // Always load likes count and user like status
    this.loading.likes[pubId] = true;
    this.interactionService.getLikesCount(pubId)
      .pipe(
        finalize(() => this.loading.likes[pubId] = false)
      )
      .subscribe({
        next: count => {
          publication.likeCount = count;
        },
        error: error => {
          console.error(`Error loading likes count for publication ${pubId}:`, error);
          publication.likeCount = 0;
        }
      });

    this.interactionService.getUserHasLiked(pubId, this.currentUserId || 0)
      .subscribe({
        next: hasLiked => {
          publication.userHasLiked = hasLiked;
        },
        error: error => {
          console.error(`Error checking if user liked publication ${pubId}:`, error);
          publication.userHasLiked = false;
        }
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
    this.eventService.getMyEvents().subscribe(myEvents => {
      const myEventsMapped = myEvents.map(event => ({
        title: event.title,
        start: event.startTime,
        end: event.endTime,
        description: event.description,
        location: event.location,
        color: 'blue'
      }));

      this.usercalenderService.getUserEventsCalender().subscribe({
        next: (userEvents: EventUserCalendar[]) => {
          const userEventsMapped = userEvents.map((event: EventUserCalendar) => ({
            title: event.name,
            start: event.startDate,
            end: event.endDate,
            color: 'green'
          }));

          this.usercalenderService.getUserMeetings().subscribe({
            next: (meetings: Meeting[]) => {
              const meetingsMapped = meetings.map((meeting: Meeting) => ({
                title: meeting.meetingName,
                start: meeting.startTime,
                end: meeting.endTime,
                color: 'red',
                url: meeting.meetingLink
              }));

              this.unifiedCalendarOptions.events = [...myEventsMapped, ...userEventsMapped, ...meetingsMapped];
              this.cdr.detectChanges();
            },
            error: (err) => {
              console.error('Erreur lors de la récupération des réunions :', err);
              this.snackBar.open('Erreur lors du chargement des réunions', 'Fermer', { duration: 3000 });
            }
          });
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
          this.snackBar.open("Erreur lors de l'ajout de l'événement", "Fermer", { duration: 3000 });
        }
      });
    }
  }

  deletePublication(id: number): void {
    if (confirm('Are you sure you want to delete this publication?')) {
      this.loading.publications = true;
      this.publicationService.deletePublication(id)
        .pipe(
          finalize(() => this.loading.publications = false)
        )
        .subscribe({
          next: () => {
            this.loadPublications();
          },
          error: (error) => {
            console.error('Error deleting publication:', error);
            this.errorMessage = 'Failed to delete publication. Please try again later.';
          }
        });
    }
  }

  editPublication(pub: InteractivePublication): void {
    this.isEditing = true;
    this.currentPublicationId = pub.ipublicationId!;
    this.newPublication = { ...pub };
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.currentPublicationId = null;
    this.resetForm();
  }

  checkContentModeration(content: string, field: 'title' | 'description'): void {
    if (!content || content.trim() === '') {
      this.moderationErrors[field] = null;
      return;
    }

    this.isCheckingModeration = true;
    this.contentModerationService.checkContent(content)
      .pipe(
        finalize(() => this.isCheckingModeration = false)
      )
      .subscribe(result => {
        this.moderationResult = result;

        if (!result.passes) {
          if (!result.checks?.badWords) {
            this.moderationErrors[field] = result.badWordsMessage || "Content contains prohibited language";
          } else if (!result.checks?.illegalContent) {
            this.moderationErrors[field] = result.illegalContentMessage || "Content may contain prohibited topics";
          } else if (!result.checks?.badPublicity) {
            this.moderationErrors[field] = result.badPublicityMessage || "Content may contain negative references to brands";
          } else {
            this.moderationErrors[field] = result.message || "Content failed moderation";
          }
        } else {
          this.moderationErrors[field] = null;
        }
      });
  }

  clearModerationErrors(): void {
    this.moderationErrors = {
      title: null,
      description: null
    };
    this.moderationResult = null;
  }

  shouldFlagContent(): boolean {
    return this.moderationErrors.title !== null || this.moderationErrors.description !== null;
  }

  onSubmit(): void {
    if (!this.newPublication.title) {
      console.warn('Please fill in all required fields.');
      return;
    }

    const shouldFlag = this.shouldFlagContent();

    if (shouldFlag) {
      this.newPublication.publicationModerationStatus = TypeIPublicationModerationStatus.FLAGGED;
    } else {
      this.newPublication.publicationModerationStatus = TypeIPublicationModerationStatus.APPROVED;
    }

    this.loading.submitting = true;

    if (this.isEditing && this.currentPublicationId) {
      this.publicationService.updatePublication(this.currentPublicationId, this.newPublication)
        .pipe(
          finalize(() => this.loading.submitting = false)
        )
        .subscribe({
          next: () => {
            this.loadPublications();
            this.cancelEdit();
            this.clearModerationErrors();
          },
          error: (error) => {
            console.error('Error updating publication:', error);
            this.errorMessage = 'Failed to update publication. Please try again later.';
          }
        });
    } else {
      this.publicationService.createPublication(this.newPublication as InteractivePublication)
        .pipe(
          finalize(() => this.loading.submitting = false)
        )
        .subscribe({
          next: () => {
            this.loadPublications();
            this.resetForm();
            this.clearModerationErrors();
          },
          error: (error) => {
            console.error('Error adding publication:', error);
            this.errorMessage = 'Failed to create publication. Please try again later.';
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
        this.loadUnifiedEventsForCalendar();
      },
      error: (err) => {
        console.error('Erreur lors de la participation:', err);
        this.snackBar.open(err.error.message || 'Erreur lors de la participation', 'Fermer', { duration: 3000 });
      }
    });
  }

  handleEventClick(info: any): void {
    if (info.event.url) {
      info.jsEvent.preventDefault();
      window.open(info.event.url, '_blank');
    }
  }

  toggleLikePublication(publication: InteractivePublication): void {
    const pubId = publication.ipublicationId;

    if (pubId === undefined || pubId === null) {
      console.warn('Cannot toggle like on publication with undefined ID');
      return;
    }

    this.loading.likes[pubId] = true;

    this.interactionService.toggleLike(pubId, this.currentUserId || 0)
      .pipe(
        finalize(() => this.loading.likes[pubId] = false)
      )
      .subscribe({
        next: hasLiked => {
          publication.userHasLiked = hasLiked;
          this.interactionService.getLikesCount(pubId).subscribe(count => {
            publication.likeCount = count;
          });
        },
        error: error => {
          console.error(`Error toggling like for publication ${pubId}:`, error);
        }
      });
  }

  toggleComments(publication: InteractivePublication): void {
    publication.showComments = !publication.showComments;

    if (!publication.comments) {
      publication.comments = [];
    }

    if (publication.showComments && (!publication.comments || publication.comments.length === 0)) {
      this.loadLikesAndComments(publication);
    }
  }

  addComment(publication: InteractivePublication): void {
    if (this.commentForm.invalid) {
      return;
    }

    const pubId = publication.ipublicationId;
    const content = this.commentForm.value.content;

    if (pubId === undefined || pubId === null) {
      console.warn('Cannot add comment to publication with undefined ID');
      return;
    }

    this.isCheckingModeration = true;
    this.contentModerationService.checkContent(content)
      .pipe(
        finalize(() => this.isCheckingModeration = false)
      )
      .subscribe(result => {
        if (!result.passes) {
          this.errorMessage = 'Comment cannot be posted: ' +
            (result.message || 'Content failed moderation checks');
          return;
        }

        this.postComment(publication, content);
      });
  }

  private postComment(publication: InteractivePublication, content: string): void {
    const pubId = publication.ipublicationId;

    if (!publication.comments) {
      publication.comments = [];
    }

    this.loading.comments[pubId] = true;

    const comment: PublicationComment = {
      commentId: 0,
      publicationId: pubId,
      userId: this.currentUserId || 0,
      content: content,
      createdAt: new Date().toISOString()
    };

    this.interactionService.addComment(comment)
      .pipe(
        finalize(() => this.loading.comments[pubId] = false)
      )
      .subscribe({
        next: newComment => {
          publication.comments.push(newComment);
          publication.commentCount++;
          this.commentForm.reset();
          this.errorMessage = '';
        },
        error: error => {
          console.error(`Error adding comment to publication ${pubId}:`, error);
          this.errorMessage = 'Failed to add comment: ' + (error.message || 'Unknown error');
        }
      });
  }

  startEditComment(comment: PublicationComment): void {
    comment.isEditing = true;
    this.editCommentForm.patchValue({
      content: comment.content
    });
  }

  saveComment(comment: PublicationComment): void {
    if (this.editCommentForm.invalid) {
      return;
    }

    const pubId = comment.publicationId;
    this.loading.comments[pubId] = true;

    const content = this.editCommentForm.value.content;

    this.interactionService.updateComment(comment.commentId, content)
      .pipe(
        finalize(() => this.loading.comments[pubId] = false)
      )
      .subscribe({
        next: updatedComment => {
          comment.content = updatedComment.content;
          comment.updatedAt = updatedComment.updatedAt;
          comment.isEditing = false;
        },
        error: error => {
          console.error(`Error updating comment ${comment.commentId}:`, error);
        }
      });
  }

  cancelEditComment(comment: PublicationComment): void {
    comment.isEditing = false;
    this.editCommentForm.reset();
  }

  deleteComment(publication: InteractivePublication, comment: PublicationComment): void {
    if (confirm('Are you sure you want to delete this comment?')) {
      const pubId = publication.ipublicationId;
      this.loading.comments[pubId] = true;

      this.interactionService.deleteComment(comment.commentId)
        .pipe(
          finalize(() => this.loading.comments[pubId] = false)
        )
        .subscribe({
          next: () => {
            publication.comments = publication.comments.filter(c => c.commentId !== comment.commentId);
            publication.commentCount--;
          },
          error: error => {
            console.error(`Error deleting comment ${comment.commentId}:`, error);
          }
        });
    }
  }

  formatDate(date: string | Date): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleString();
  }

  isPublicationLoading(pubId: number): boolean {
    return this.loading.publications ||
           this.loading.comments[pubId] ||
           this.loading.likes[pubId];
  }
}