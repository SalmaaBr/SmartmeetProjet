import { Pipe, PipeTransform } from '@angular/core';
import { Document } from '../models/document';

@Pipe({
  name: 'documentFilter'
})
export class DocumentFilterPipe implements PipeTransform {
  transform(documents: Document[], searchText: string): Document[] {
    if (!documents || !searchText) {
      return documents;
    }
    const lowerSearch = searchText.toLowerCase();
    return documents.filter(doc =>
      doc.documentType?.toLowerCase().includes(lowerSearch) ||
      doc.name?.toLowerCase().includes(lowerSearch) ||
      doc.description?.toLowerCase().includes(lowerSearch) ||
      doc.documentTheme?.toLowerCase().includes(lowerSearch)
    );
  }
}