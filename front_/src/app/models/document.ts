import { 
    TypeDocument, TypeDocumentVisibility, TypeAccessLevelDocument, TypeDocumentTheme,
    
  } from './document.enum';
  
  export interface Document {
    id: number;
    name: string;
    description: string;
    createdAt: Date;
    documentType: TypeDocument;        // Changé de typeDocument
    documentVisibility: TypeDocumentVisibility;  // Changé de typeDocumentVisibility
    documentAccessLevel: TypeAccessLevelDocument;  // Changé de typeAccessLevelDocument
    documentTheme: TypeDocumentTheme; 
    filePath: string;
    users: { userID: number };
  documentLikes: DocumentLike[];
  }
  export interface DocumentLike {
    likeId: number;
    createdAt: string;
    user: { userID: number };
  }
  