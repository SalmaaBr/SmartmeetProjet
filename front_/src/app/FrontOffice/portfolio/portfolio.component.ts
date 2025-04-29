import { Component,OnInit } from '@angular/core';
import { DocumentService } from 'src/app/services/document.service';
import { ToastrService } from 'ngx-toastr';
import { Document,DocumentLike } from 'src/app/models/document'; 
import { AuthService } from 'src/app/auth/auth.service';
import { Router } from '@angular/router';
import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { MessageService } from 'src/app/services/message.service';
import { RecommendationService } from 'src/app/services/RecommendationService.service';
interface RecommendedDocument extends Document {
  score: number;
}
@Component({
  selector: 'app-portfolio',
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.css']
})
export class PortfolioComponent implements OnInit {
  documents:Document[] = [];
  recommendedDocuments: RecommendedDocument[] = [];
  likedDocuments: Set<number> = new Set();
  searchTerm: string = '';
  errorMessage: string | null = null;
  loading: boolean = false;
  isLoggedIn: boolean = false;
  currentUserId: number | null = null;
  showRecommendations: boolean = false;
  availableUsers: User[] = [];
  selectedDocument: Document | null = null;
  selectedUser: string | null = null;
  messageContent: string = '';
  showModal: boolean = false;
  logId(id: number) {
    console.log('Navigating to document ID:', id);
  }

  constructor(private documentService: DocumentService,
    private authService: AuthService,
    private toastr: ToastrService,
    private userService: UserService,
    private messageService: MessageService,
    private recommendationService: RecommendationService,
    private router: Router 
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    const user = this.authService.getCurrentUser();
    this.currentUserId = user?.userID ?? null;
    this.loadDocuments()
    this.loadAvailableUsers();
  }
  loadAvailableUsers() {
    this.userService.getAvailableUsers().subscribe({
      next: (users) => {
        this.availableUsers = users;
      },
      error: (err) => {
        if (err.message.includes('Session expired')) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        this.toastr.error('Error loading users: ' + (err.message || 'Unknown error'));
      }
    });
  }

  openShareModal(document: Document) {
    this.selectedDocument = document;
    this.selectedUser = null;
    this.messageContent = '';
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.selectedDocument = null;
    this.selectedUser = null;
    this.messageContent = '';
  }

  shareDocument() {
    if (!this.selectedUser || !this.messageContent) {
      this.toastr.error('Please select a user and enter a message.');
      return;
    }
    if (!this.selectedDocument) {
      this.toastr.error('No document selected.');
      return;
    }

    this.loading = true;
    this.messageService.sendMessage(this.selectedUser, this.messageContent, [this.selectedDocument.id]).subscribe({
      next: () => {
        this.toastr.success('Document shared successfully!');
        this.closeModal();
        this.loading = false;
      },
      error: (err) => {
        if (err.message.includes('Session expired')) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        this.toastr.error('Failed to share document: ' + (err.message || 'Unknown error'));
        this.loading = false;
      }
    });
  }
  loadRecommendedDocuments() {
    if (!this.isLoggedIn) {
      this.toastr.error('Veuillez vous connecter pour voir les recommandations.');
      this.router.navigate(['/login']);
      return;
    }

    const token = this.authService.getToken();
    if (!token) {
      this.toastr.error('Aucun token trouvé. Veuillez vous reconnecter.');
      this.authService.logout();
      return;
    }

    this.loading = true;
    this.recommendationService.getRecommendationsForCurrentUser(token).subscribe({
      next: (recommendations) => {
        console.log('Recommandations reçues:', recommendations);
        this.recommendedDocuments = [];
        recommendations.forEach((rec: any) => {
          const doc = this.documents.find(d => d.id === rec.id);
          if (doc) {
            const recommendedDoc: RecommendedDocument = { ...doc, score: rec.score };
            this.recommendedDocuments.push(recommendedDoc);
          }
        });
        // Explicitly type the parameters as RecommendedDocument
        this.recommendedDocuments.sort((a: RecommendedDocument, b: RecommendedDocument) => b.score - a.score);
        this.showRecommendations = true;
        this.loading = false;
        this.toastr.success('Recommandations chargées avec succès !');
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des recommandations:', err);
        if (err.message.includes('401')) {
          this.errorMessage = 'Session expirée. Veuillez vous reconnecter.';
          this.toastr.error('Session expirée. Veuillez vous reconnecter.');
        } else {
          this.errorMessage = 'Erreur lors de la récupération des recommandations.';
          this.toastr.error('Erreur lors de la récupération des recommandations.');
        }
        this.loading = false;
      }
    });
  }

  toggleRecommendations() {
    if (!this.showRecommendations) {
      this.loadRecommendedDocuments();
    } else {
      this.showRecommendations = false;
    }
  }

 /* loadUserDetails(): void {
    this.documentService.getCurrentUser().subscribe({
      next: (user) => {
        this.userId = user.userId; // Adjust based on actual response field (e.g., user.id)
        if (this.userId) {
          this.loadDocuments();
        } else {
          this.errorMessage = 'Unable to retrieve user ID. Please log in again.';
          this.toastr.error(this.errorMessage, 'Error');
          this.authService.logout();
        }
      },
      error: (err) => {
        this.errorMessage = 'Error fetching user details: ' + err.message;
        this.toastr.error(this.errorMessage, 'Error');
        this.authService.logout();
      }
    });
  }*/
    loadDocuments() {
      this.documentService.getDocuments().subscribe(
        (allDocuments) => {
          console.log('All documents received:', allDocuments);
          this.documents = allDocuments;
  
          // Vérifier les documents déjà likés par l'utilisateur
          if (this.currentUserId !== null) {
            allDocuments.forEach(doc => {
              const hasLiked = doc.documentLikes?.some(like => like.user.userID === this.currentUserId);
              if (hasLiked) {
                this.likedDocuments.add(doc.id);
              }
            });
          }
        },
        (error) => {
          console.error('Error loading documents:', error);
          this.errorMessage = 'Erreur lors du chargement des documents.';
          this.toastr.error('Erreur lors du chargement des documents.');
        }
      );
    }
  
    likeDocument(id: number) {
      if (!this.isLoggedIn) {
        this.errorMessage = 'You must be logged in to like a document.';
        this.toastr.error('Please log in.');
        this.authService.logout();
        this.router.navigate(['/login']);
        return;
      }
  
      const token = this.authService.getToken();
      if (!token) {
        this.errorMessage = 'Session expired or token missing. Please log in again.';
        this.toastr.error('Session expired. Please log in again.');
        this.authService.logout();
        this.router.navigate(['/login']);
        return;
      }
  
      if (!id || isNaN(id)) {
        console.error('Invalid document ID:', id);
        this.errorMessage = 'Invalid document ID.';
        this.toastr.error('Invalid document ID.');
        return;
      }
  
      console.log('Attempting to like document with ID:', id);
      this.documentService.likeDocument(id).subscribe({
        next: (updatedDoc: Document) => {
          console.log('Like successful, new likes:', updatedDoc.documentLikes.length);
          const index = this.documents.findIndex(doc => doc.id === id);
          if (index !== -1) {
            this.documents[index] = updatedDoc;
          }
          this.likedDocuments.add(id);
          this.toastr.success('Document liked!');
          this.loadDocuments();
        },
        error: (err) => {
          console.error('Like failed:', err);
          if (err.status === 400 && err.error?.error === 'User has already liked this document') {
            this.toastr.warning('You have already liked this document.');
          } else if (err.status === 401) {
            this.errorMessage = 'Session expired or unauthorized. Please log in again.';
            this.toastr.error('Session expired. Please log in again.');
            this.authService.logout();
            this.router.navigate(['/login']);
          } else {
            this.errorMessage = 'Failed to like document: ' + (err.error?.error || 'Unknown error');
            this.toastr.error('Failed to like document: ' + (err.error?.error || 'Unknown error'));
          }
        }
      });
    }
 
 /* downloadDocument(id: number) {
    console.log('Attempting to download document with ID:', id);
    this.documentService.downloadDocument(id).subscribe({
      next: (blob: Blob) => {
        console.log('Download successful, blob size:', blob.size);
        // Renommé 'document' en 'doc' pour éviter la collision
        const doc = this.documents.find(d => d.id === id);
        const fileName = doc ? `${doc.name}_details.txt` : 'document.txt';
        const link = window.document.createElement('a'); // Utilisation explicite de window.document
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
        window.URL.revokeObjectURL(link.href);
        this.toastr.success('Document downloaded successfully');
      },
      error: (err) => {
        console.error('Download failed:', err);
        this.toastr.error('Failed to download document: ' + (err.message || 'Unknown error'));
      }
    });
  }
 /* likeDocument(id: number) {
    console.log('Attempting to like document with ID:', id);
    this.documentService.likeDocument(id).subscribe({
      next: (updatedDoc: Document) => {
        console.log('Like successful, new likes:', updatedDoc.likes);
        const index = this.documents.findIndex(doc => doc.id === id);
        if (index !== -1) {
          this.documents[index] = updatedDoc;
        }
        this.likedDocuments.add(id);
        this.toastr.success('Document liked!');
      },
      error: (err) => {
        console.error('Like failed:', err);
        this.toastr.error('Failed to like document: ' + (err.message || 'Unknown error'));
      }
    });
  }*/
}
