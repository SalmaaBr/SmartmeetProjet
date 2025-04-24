import { Component, OnInit } from '@angular/core';
import { DocumentService } from 'src/app/services/document.service';
import { Document } from 'src/app/models/document';
import { ToastrService } from 'ngx-toastr';
import { DocumentEnumService } from 'src/app/services/documentenums.service';
import { TypeDocument, TypeDocumentVisibility, TypeAccessLevelDocument, TypeDocumentTheme } from 'src/app/models/document.enum';

@Component({
  selector: 'app-document-management',
  templateUrl: './document-management.component.html',
  styleUrls: ['./document-management.component.css']
})
export class DocumentManagementComponent implements OnInit {
  documents: Document[] = [];
  currentDocument: Document = this.getEmptyDocument();
  isEditing = false; // Track if we're editing an existing document

  typeDocument: string[] = [];
  typeDocumentTheme: string[] = [];
  typeAccessLevelDocument: string[] = [];
  typeDocumentVisibility: string[] = [];

  constructor(
    private documentService: DocumentService,
    private toastr: ToastrService,
    private enumService: DocumentEnumService
  ) {}

  ngOnInit() {
    this.getDocuments();
    this.typeDocument = this.enumService.getTypeDocuments();
    this.typeDocumentTheme = this.enumService.getDocumentThemes();
    this.typeAccessLevelDocument = this.enumService.getAccessLevels();
    this.typeDocumentVisibility = this.enumService.getVisibilities();
  }

  getDocuments() {
    this.documentService.getDocuments().subscribe({
      next: (docs: Document[]) => {
        console.log('Documents récupérés :', docs);
        if (docs && docs.length > 0) {
          // Log des détails du premier document pour debug
          console.log('Détails du premier document :', {
            id: docs[0].id,
            name: docs[0].name,
            documentType: docs[0].documentType,
            documentVisibility: docs[0].documentVisibility,
            documentAccessLevel: docs[0].documentAccessLevel,
            documentTheme: docs[0].documentTheme,
            createdAt: docs[0].createdAt,
            users: docs[0].users,
            documentLikes: docs[0].documentLikes
          });
        }
        this.documents = docs; // Les URLs d'image sont déjà ajustées par le service
      },
      error: (err) => {
        console.error('Erreur lors du chargement:', err);
        this.toastr.error("Error loading documents");
      },
      complete: () => {
        console.log('Récupération des documents terminée');
      }
    });
  }
  saveDocument() {
    if (!this.currentDocument.name || !this.currentDocument.documentType) {
      this.toastr.warning("Please fill in all required fields!");
      return;
    }
  
    // Création de l'objet document à sauvegarder
    const documentToSave: Partial<Document> = {
      id: this.isEditing ? this.currentDocument.id : 0,
      name: this.currentDocument.name,
      description: this.currentDocument.description,
      createdAt: this.isEditing ? this.currentDocument.createdAt : new Date(), // Conserve la date existante si édition
      documentType: this.currentDocument.documentType ?? TypeDocument.ARTICLE,
      documentVisibility: this.currentDocument.documentVisibility ?? TypeDocumentVisibility.PUBLIC,
      documentAccessLevel: this.currentDocument.documentAccessLevel ?? TypeAccessLevelDocument.READ,
      documentTheme: this.currentDocument.documentTheme ?? TypeDocumentTheme.TECHNOLOGY
    };
   
    if (this.isEditing) {
      this.documentService.updateDocument(this.currentDocument.id, documentToSave as Document).subscribe({
        next: () => {
          this.getDocuments();
          this.toastr.success("Document updated!");
          this.resetForm();
        },
        error: () => this.toastr.error("Error updating document")
      });
    } else {
      this.documentService.addDocument(documentToSave as Document).subscribe({
        next: (newDoc) => {
          console.log("Document added :", newDoc);
          this.documents.push(newDoc);
          this.toastr.success("Document added!");
          this.getDocuments();
          this.resetForm();
        },
        error: (err) => {
          console.error("Erreur ajout :", err);
          this.toastr.error("Error adding document");
        }
      });
    }
  }

  editDocument(documentId: number) {
    const documentToEdit = this.documents.find(doc => doc.id === documentId);
    if (documentToEdit) {
      this.currentDocument = { ...documentToEdit };
      this.isEditing = true;
    }
  }

  deleteDocument(id: number) {
    this.documentService.deleteDocument(id).subscribe({
      next: () => {
        this.documents = this.documents.filter(doc => doc.id !== id);
        this.toastr.success("Document deleted!");
      },
      error: () => this.toastr.error("Error deleting document")
    });
  }

  closeModal() {
    this.resetForm();
  }

  resetForm() {
    this.currentDocument = this.getEmptyDocument();
    this.isEditing = false;
  }

  getVisibilityClass(visibility: string | null) {
    if (!visibility) return 'bg-secondary';
    return visibility === 'PUBLIC' ? 'bg-success' : 'bg-danger';
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Vérifier la taille du fichier (par exemple, max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.toastr.error("File size must be less than 5MB");
        return;
      }
  
    
      
  
   
    }}
  private getEmptyDocument(): Document {
    return {
      id: 0,
      name: '',
      description: '',
      createdAt: new Date(),
      documentType: TypeDocument.ARTICLE,
      documentVisibility: TypeDocumentVisibility.PUBLIC,
      documentAccessLevel: TypeAccessLevelDocument.READ,
      documentTheme: TypeDocumentTheme.TECHNOLOGY,
      users: { userID: 0 }, // Valeur par défaut (sera définie côté backend)
      documentLikes: []
      
    };
  }
 
}