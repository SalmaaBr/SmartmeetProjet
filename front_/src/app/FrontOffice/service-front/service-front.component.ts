import { Component, OnInit } from '@angular/core';
import { EventService, Event } from '../../services/event.service';
import { RecutementService } from '../../services/recutement.service';
import * as AOS from 'aos';

import { InteractivePublicationService } from 'src/app/services/interactive-publication.service';
import { InteractivePublication } from 'src/app/models/interactive-publication.model';
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
  styleUrls: ['./service-front.component.css']
})
export class ServiceFrontComponent implements OnInit {
  events: Event[] = [];
  recrutements: any[] = [];  // Tableau pour stocker les recrutements
  errorMessage: string = '';

  // Variables liées aux publications
  TypeIPublicationStatus = TypeIPublicationStatus;
  TypeIPublicationVisibility = TypeIPublicationVisibility;
  TypeIPublicationModerationStatus = TypeIPublicationModerationStatus;
  isEditing = false;
  currentPublicationId: number | null = null;
  newPublication: InteractivePublication = new InteractivePublication();
  publications: InteractivePublication[] = [];

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
  email = localStorage.getItem("email")

  // Current user - for development purposes, we'll use a hardcoded user ID
  currentUserId: number = 1; // Replace with auth service in real implementation

  // Comments form
  commentForm: FormGroup;
  editCommentForm: FormGroup;

  constructor(
    private eventService: EventService,
    private recrutementService: RecutementService,
    private publicationService: InteractivePublicationService,
    private interactionService: PublicationInteractionService,
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
    // Appel aux méthodes pour récupérer les événements
    this.loadEvents();

    // Appel aux méthodes pour récupérer les recrutements
    this.loadRecruitments();

    // Charger les publications
    this.loadPublications();
    this.loadUserProfile();
    // Initialisation d'AOS pour les animations
    setTimeout(() => AOS.init(), 0);

    console.log('ServiceFrontComponent Loaded ✅');
  }
  loadUserProfile(): void {
    if (this.email) {
      this.userService.getUserByEmail(this.email).subscribe({
        next: (user) => {
          if (user.userID) {
          this.currentUserId = user.userID
          }
        },
        error: (err) => {
          this.errorMessage = 'Failed to load profile';

        }
      });
    }
  }
  // Charger les événements
  loadEvents(): void {
    this.eventService.getEvents().subscribe(
      (data: Event[]) => {
        console.log("Données récupérées pour la home page :", data);
        this.events = data;  // Stocke les événements dans la variable
      },
      (error) => {
        console.error("Erreur lors de la récupération des événements :", error);
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

  // Load likes and comments for a publication
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
            publication.comments = comments || []; // Ensure comments is never undefined
            publication.commentCount = comments ? comments.length : 0;
          },
          error: error => {
            console.error(`Error loading comments for publication ${pubId}:`, error);
            publication.comments = []; // Initialize with empty array on error
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
          publication.likeCount = 0; // Initialize with 0 on error
        }
      });

    this.interactionService.getUserHasLiked(pubId, this.currentUserId)
      .subscribe({
        next: hasLiked => {
          publication.userHasLiked = hasLiked;
        },
        error: error => {
          console.error(`Error checking if user liked publication ${pubId}:`, error);
          publication.userHasLiked = false; // Initialize with false on error
        }
      });
  }

  // Supprimer une publication
  deletePublication(id: number): void {
    // Check if ID is valid
    if (id === undefined || id === null) {
      console.error('Cannot delete publication with undefined ID');
      this.errorMessage = 'Error: Cannot delete publication with undefined ID';
      return;
    }

    if (confirm('Are you sure you want to delete this publication?')) {
      this.loading.publications = true;
      this.publicationService.deletePublication(id)
        .pipe(
          finalize(() => this.loading.publications = false)
        )
        .subscribe({
          next: () => {
            console.log('Publication deleted successfully');
            this.loadPublications();
          },
          error: (error) => {
            console.error('Error deleting publication:', error);
            this.errorMessage = 'Failed to delete publication. Please try again later.';
          }
        });
    }
  }

  // Editer une publication - charger dans le formulaire
  editPublication(pub: InteractivePublication): void {
    console.log("🚀 ~ ServiceFrontComponent ~ editPublication ~ pub:", pub);
    this.isEditing = true;
    this.currentPublicationId = pub.ipublicationId!;
    this.newPublication = { ...pub }; // Clone the publication object
  }

  // Annuler le mode édition
  cancelEdit(): void {
    this.isEditing = false;
    this.currentPublicationId = null;
    this.resetForm();
  }

  // Check content for moderation issues
  checkContentModeration(content: string, field: 'title' | 'description'): void {
    if (!content || content.trim() === '') {
      // Clear any existing moderation errors for empty content
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
          // Set appropriate error message
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

  // Clear moderation errors
  clearModerationErrors(): void {
    this.moderationErrors = {
      title: null,
      description: null
    };
    this.moderationResult = null;
  }

  // Check if content should be flagged for moderation
  shouldFlagContent(): boolean {
    return this.moderationErrors.title !== null || this.moderationErrors.description !== null;
  }

  // Soumettre le formulaire
  onSubmit(): void {
    if (!this.newPublication.title) {
      console.warn('Please fill in all required fields.');
      return;
    }

    // Final check for moderation issues
    const shouldFlag = this.shouldFlagContent();

    // Set moderation status based on check
    if (shouldFlag) {
      this.newPublication.publicationModerationStatus = TypeIPublicationModerationStatus.FLAGGED;
    } else {
      this.newPublication.publicationModerationStatus = TypeIPublicationModerationStatus.APPROVED;
    }

    this.loading.submitting = true;

    if (this.isEditing && this.currentPublicationId) {
      // Mettre à jour une publication existante
      this.publicationService.updatePublication(this.currentPublicationId, this.newPublication)
        .pipe(
          finalize(() => this.loading.submitting = false)
        )
        .subscribe({
          next: () => {
            console.log('Publication updated successfully');
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
      // Créer une nouvelle publication
      this.publicationService.createPublication(this.newPublication as InteractivePublication)
        .pipe(
          finalize(() => this.loading.submitting = false)
        )
        .subscribe({
          next: (response: InteractivePublication) => {
            console.log('Publication added successfully:', response);
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

  // Réinitialiser les champs du formulaire
  resetForm(): void {
    this.newPublication = new InteractivePublication();
  }

  // Toggle like on a publication
  toggleLike(publication: InteractivePublication): void {
    const pubId = publication.ipublicationId;

    // Skip if publication ID is not defined
    if (pubId === undefined || pubId === null) {
      console.warn('Cannot toggle like on publication with undefined ID');
      return;
    }

    this.loading.likes[pubId] = true;

    this.interactionService.toggleLike(pubId, this.currentUserId)
      .pipe(
        finalize(() => this.loading.likes[pubId] = false)
      )
      .subscribe({
        next: hasLiked => {
          publication.userHasLiked = hasLiked;
          // Update like count
          this.interactionService.getLikesCount(pubId).subscribe(count => {
            publication.likeCount = count;
          });
        },
        error: error => {
          console.error(`Error toggling like for publication ${pubId}:`, error);
        }
      });
  }

  // Toggle comments visibility
  toggleComments(publication: InteractivePublication): void {
    publication.showComments = !publication.showComments;

    // Ensure comments array exists
    if (!publication.comments) {
      publication.comments = [];
    }

    // If comments are being shown and haven't been loaded yet, load them
    if (publication.showComments && (!publication.comments || publication.comments.length === 0)) {
      this.loadLikesAndComments(publication);
    }
  }

  // Add a comment to a publication
  addComment(publication: InteractivePublication): void {
    if (this.commentForm.invalid) {
      return;
    }

    const pubId = publication.ipublicationId;
    const content = this.commentForm.value.content;

    // Skip if publication ID is not defined
    if (pubId === undefined || pubId === null) {
      console.warn('Cannot add comment to publication with undefined ID');
      return;
    }

    // Check content for moderation
    this.isCheckingModeration = true;
    this.contentModerationService.checkContent(content)
      .pipe(
        finalize(() => this.isCheckingModeration = false)
      )
      .subscribe(result => {
        if (!result.passes) {
          // Show moderation error to user
          this.errorMessage = 'Comment cannot be posted: ' +
            (result.message || 'Content failed moderation checks');
          return;
        }

        // Continue with posting the comment
        this.postComment(publication, content);
      });
  }

  // Post a comment that has passed moderation
  private postComment(publication: InteractivePublication, content: string): void {
    const pubId = publication.ipublicationId;

    // Ensure comments array exists
    if (!publication.comments) {
      publication.comments = [];
    }

    this.loading.comments[pubId] = true;

    const comment: PublicationComment = {
      commentId: 0, // Will be set by the backend
      publicationId: pubId,
      userId: this.currentUserId,
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
          this.errorMessage = ''; // Clear any error messages
        },
        error: error => {
          console.error(`Error adding comment to publication ${pubId}:`, error);
          this.errorMessage = 'Failed to add comment: ' + (error.message || 'Unknown error');
        }
      });
  }

  // Edit a comment
  startEditComment(comment: PublicationComment): void {
    comment.isEditing = true;
    this.editCommentForm.patchValue({
      content: comment.content
    });
  }

  // Save edited comment
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

  // Cancel comment edit
  cancelEditComment(comment: PublicationComment): void {
    comment.isEditing = false;
    // Reset the form to prevent showing stale data if edit is started again
    this.editCommentForm.reset();
  }

  // Delete a comment
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

  // Format date for display
  formatDate(date: string | Date): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleString();
  }

  // Check if a publication is in loading state
  isPublicationLoading(pubId: number): boolean {
    return this.loading.publications ||
           this.loading.comments[pubId] ||
           this.loading.likes[pubId];
  }
}
