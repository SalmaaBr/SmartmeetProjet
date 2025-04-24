import { Injectable } from '@angular/core';
import {
  TypeDocument,
  TypeDocumentTheme,
  TypeDocumentVisibility,
  TypeAccessLevelDocument
} from '../models/document.enum'; 

@Injectable({
  providedIn: 'root'
})
export class DocumentEnumService {

  private customTypeDocuments: string[] = [];
  private customThemes: string[] = [];

  constructor() {}

  // ----------- TypeDocument --------------
  getTypeDocuments(): string[] {
    return [...Object.values(TypeDocument), ...this.customTypeDocuments];
  }

  addTypeDocument(type: string): void {
    if (!Object.values(TypeDocument).includes(type as TypeDocument) && !this.customTypeDocuments.includes(type)) {
      this.customTypeDocuments.push(type);
    }
  }

  // ----------- TypeDocumentTheme --------------
  getDocumentThemes(): string[] {
    return [...Object.values(TypeDocumentTheme), ...this.customThemes];
  }

  addDocumentTheme(theme: string): void {
    if (!Object.values(TypeDocumentTheme).includes(theme as TypeDocumentTheme) && !this.customThemes.includes(theme)) {
      this.customThemes.push(theme);
    }
  }

  // ----------- TypeDocumentVisibility --------------
  getVisibilities(): string[] {
    return Object.values(TypeDocumentVisibility);
  }

  // ----------- TypeAccessLevelDocument --------------
  getAccessLevels(): string[] {
    return Object.values(TypeAccessLevelDocument);
  }
}
