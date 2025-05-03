import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { jsPDF } from 'jspdf';

export interface EventData {
  id: number;
  title: string;
  description: string;
  // Add other event properties as needed
}

export interface SponsorEventAssignment {
  sponsorId: number;
  eventId: string;
  terms: string;
  amount: number;
  expiryDate: string;
  contractFile: File;
}

export interface Contract {
  id: number;
  sponsor: {
    idSponsor: number;
    nom: string;
  };
  event: {
    id: number;
    title: string;
  };
  terms: string;
  amount: number;
  signingDate: string;
  expiryDate: string;
  status: string;
  contractPath: string;
  signature?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SponsorEventService {
  private apiUrl = environment.apiUrl + '/api/sponsor-event';

  constructor(private http: HttpClient) { }

  getAllEvents(): Observable<EventData[]> {
    return this.http.get<EventData[]>(`${this.apiUrl}/events`);
  }

  checkExistingAssignment(sponsorId: number, eventId: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check-assignment/${sponsorId}/${eventId}`);
  }

  assignSponsorToEvent(assignment: SponsorEventAssignment): Observable<any> {
    const formData = new FormData();
    formData.append('sponsorId', assignment.sponsorId.toString());
    formData.append('eventId', assignment.eventId);
    formData.append('terms', assignment.terms);
    formData.append('amount', assignment.amount.toString());
    formData.append('expiryDate', assignment.expiryDate);
    formData.append('contract', assignment.contractFile);

    return this.http.post<any>(`${this.apiUrl}/assign`, formData);
  }

  getSponsorEvents(sponsorId: number): Observable<EventData[]> {
    return this.http.get<EventData[]>(`${this.apiUrl}/sponsor/${sponsorId}/events`);
  }

  getSponsorContracts(sponsorId: number): Observable<Contract[]> {
    return this.http.get<Contract[]>(`${this.apiUrl}/sponsor/${sponsorId}/contracts`);
  }

  getContractsByResponsibleUser(responsibleUserId: number): Observable<Contract[]> {
    return this.http.get<Contract[]>(`${this.apiUrl}/responsible/${responsibleUserId}/contracts`);
  }

  getContractFile(contractId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/contract/${contractId}`, { responseType: 'blob' });
  }

  generateContractPDF(assignment: Omit<SponsorEventAssignment, 'contractFile'>, eventName: string): void {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();
    const margin = 20;
    const lineHeight = 7;
    let y = 20;

    // Title
    doc.setFontSize(20);
    doc.setFont('helvetica', 'bold');
    doc.text('Sponsorship Contract', pageWidth / 2, y, { align: 'center' });
    y += lineHeight * 2;

    // Contract Details
    doc.setFontSize(16);
    doc.text('Contract Details:', margin, y);
    y += lineHeight;

    // Details Table
    doc.setFontSize(12);
    doc.setFont('helvetica', 'normal');
    const details = [
      ['Event:', eventName],
      ['Amount:', `$${assignment.amount}`],
      ['Expiry Date:', new Date(assignment.expiryDate).toLocaleDateString()]
    ];

    details.forEach(([label, value]) => {
      doc.text(label, margin, y);
      doc.text(value, margin + 50, y);
      y += lineHeight;
    });

    y += lineHeight;

    // Terms and Conditions
    doc.setFontSize(16);
    doc.setFont('helvetica', 'bold');
    doc.text('Terms and Conditions:', margin, y);
    y += lineHeight;

    // Terms text
    doc.setFontSize(12);
    doc.setFont('helvetica', 'normal');
    const terms = doc.splitTextToSize(assignment.terms, pageWidth - (margin * 2));
    terms.forEach((line: string) => {
      if (y > doc.internal.pageSize.getHeight() - margin) {
        doc.addPage();
        y = margin;
      }
      doc.text(line, margin, y);
      y += lineHeight;
    });

    y += lineHeight * 2;

    // Signature Section
    doc.setFontSize(12);
    doc.setFont('helvetica', 'bold');
    doc.text('Sponsor Signature:', margin, y);
    y += lineHeight;
    doc.setFont('helvetica', 'normal');
    doc.line(margin, y, margin + 80, y);

    // Footer
    const pageCount = doc.getNumberOfPages();
    for (let i = 1; i <= pageCount; i++) {
      doc.setPage(i);
      doc.setFontSize(10);
      doc.setFont('helvetica', 'italic');
      doc.text(
        'This contract is generated automatically and is valid upon signature.',
        pageWidth / 2,
        doc.internal.pageSize.getHeight() - 10,
        { align: 'center' }
      );
    }

    doc.save('sponsorship_contract.pdf');
  }

  async downloadPDF(contract: Contract, signatureDataUrl?: string | null): Promise<void> {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();
    const margin = 20;
    const lineHeight = 7;
    let y = 20;

    // Header
    doc.setFontSize(20);
    doc.setFont('helvetica', 'bold');
    doc.text('Sponsorship Contract', pageWidth / 2, y, { align: 'center' });
    y += lineHeight * 2;

    // Contract Details Section
    doc.setFontSize(16);
    doc.text('Contract Details:', margin, y);
    y += lineHeight;

    // Details Table
    doc.setFontSize(12);
    doc.setFont('helvetica', 'normal');
    const details = [
      ['Contract ID:', `#${contract.id}`],
      ['Event:', contract.event?.title || 'N/A'],
      ['Sponsor:', contract.sponsor?.nom || 'N/A'],
      ['Amount:', `$${contract.amount}`],
      ['Signing Date:', contract.signingDate ? new Date(contract.signingDate).toLocaleDateString('fr-FR') : 'N/A'],
      ['Expiry Date:', contract.expiryDate ? new Date(contract.expiryDate).toLocaleDateString('fr-FR') : 'N/A'],
      ['Status:', contract.status]
    ];

    details.forEach(([label, value]) => {
      doc.text(label, margin, y);
      doc.text(value, margin + 50, y);
      y += lineHeight;
    });

    y += lineHeight;

    // Terms and Conditions
    doc.setFontSize(16);
    doc.setFont('helvetica', 'bold');
    doc.text('Terms and Conditions:', margin, y);
    y += lineHeight;

    doc.setFontSize(12);
    doc.setFont('helvetica', 'normal');
    const terms = doc.splitTextToSize(contract.terms || 'No terms provided.', pageWidth - (margin * 2));
    terms.forEach((line: string) => {
      if (y > doc.internal.pageSize.getHeight() - margin) {
        doc.addPage();
        y = margin;
      }
      doc.text(line, margin, y);
      y += lineHeight;
    });

    y += lineHeight * 2;

    // Signature Section
    doc.setFontSize(12);
    doc.setFont('helvetica', 'bold');
    doc.text('Sponsor Signature:', margin, y);
    y += lineHeight;

    if (signatureDataUrl) {
      try {
        // Convertir l'image de la signature (base64) en données utilisables par jsPDF
        const imgData = signatureDataUrl;
        const imgProps = doc.getImageProperties(imgData);
        const imgWidth = 80; // Largeur de la signature en mm
        const imgHeight = (imgProps.height * imgWidth) / imgProps.width; // Hauteur proportionnelle

        if (y + imgHeight > doc.internal.pageSize.getHeight() - margin) {
          doc.addPage();
          y = margin;
        }

        doc.addImage(imgData, 'PNG', margin, y, imgWidth, imgHeight);
        y += imgHeight + lineHeight;
      } catch (error) {
        console.error('Erreur lors de l\'ajout de la signature au PDF:', error);
        doc.setFontSize(12);
        doc.setFont('helvetica', 'normal');
        doc.text('Signature not available', margin, y);
        y += lineHeight;
      }
    } else {
      doc.setFontSize(12);
      doc.setFont('helvetica', 'normal');
      doc.text('Not signed', margin, y);
      y += lineHeight;
    }

    // Footer
    const pageCount = doc.getNumberOfPages();
    for (let i = 1; i <= pageCount; i++) {
      doc.setPage(i);
      doc.setFontSize(10);
      doc.setFont('helvetica', 'italic');
      doc.text(
        `Event Management - ${new Date().getFullYear()} - All rights reserved`,
        pageWidth / 2,
        doc.internal.pageSize.getHeight() - 10,
        { align: 'center' }
      );
    }

    // Télécharger le PDF
    const filename = `contract_${contract.sponsor?.nom}_${contract.event?.title || 'contract'}.pdf`;
    doc.save(filename);
  }
}
