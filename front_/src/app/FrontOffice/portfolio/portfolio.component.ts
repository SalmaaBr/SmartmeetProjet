import { Component,OnInit } from '@angular/core';
import { DocumentService } from 'src/app/services/document.service';
import { ToastrService } from 'ngx-toastr';
import { Document,DocumentLike } from 'src/app/models/document'; 
import { AuthService } from 'src/app/auth/auth.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-portfolio',
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.css']
})
export class PortfolioComponent implements OnInit {
  documents:Document[] = [];;
  likedDocuments: Set<number> = new Set();
  searchTerm: string = '';
  errorMessage: string | null = null;
  loading: boolean = false;
  isLoggedIn: boolean = false;
  currentUserId: number | null = null;
  logId(id: number) {
    console.log('Navigating to document ID:', id);
  }

  constructor(private documentService: DocumentService,
    private authService: AuthService,
    private toastr: ToastrService,
    private router: Router 
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    const user = this.authService.getCurrentUser();
    this.currentUserId = user?.userID ?? null;
    this.loadDocuments()
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
        this.errorMessage = 'Vous devez être connecté pour aimer un document.';
        this.toastr.error('Veuillez vous connecter.');
        this.authService.logout();
        return;
      }
  
      if (!id || isNaN(id)) {
        console.error('Invalid document ID:', id);
        this.errorMessage = 'ID du document invalide.';
        this.toastr.error('ID du document invalide.');
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
          this.toastr.success('Document aimé !');
          this.loadDocuments(); // Recharger pour mettre à jour l'état
        },
        error: (err) => {
          console.error('Like failed:', err);
          if (err.message.includes('already liked')) {
            this.toastr.warning('Vous avez déjà aimé ce document.');
          } else {
            this.errorMessage = 'Échec de l\'action Like : ' + (err.message || 'Erreur inconnue');
            this.toastr.error('Échec de l\'action Like : ' + (err.message || 'Erreur inconnue'));
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
