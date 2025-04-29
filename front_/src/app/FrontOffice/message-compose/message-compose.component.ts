import { Component, OnInit } from '@angular/core';
import { MessageService } from '../../services/message.service';
import { DocumentService } from '../../services/document.service';
import { AuthService } from 'src/app/auth/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { Document } from 'src/app/models/document';
@Component({
  selector: 'app-message-compose',
  templateUrl: './message-compose.component.html',
  styleUrls: ['./message-compose.component.css']
})
export class MessageComposeComponent implements OnInit {
  receiverUsername: string = '';
  content: string = '';
  selectedDocumentIds: number[] = [];
  documents: Document[] = [];
  loading: boolean = false;

  constructor(
    private messageService: MessageService,
    private documentService: DocumentService,
    private authService: AuthService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.toastr.error('Please log in to send messages.');
      this.router.navigate(['/login']);
      return;
    }
    this.loadDocuments();
  }

  loadDocuments() {
    this.loading = true;
    this.documentService.getDocuments().subscribe({
      next: (docs) => {
        this.documents = docs;
        this.loading = false;
      },
      error: (err) => {
        this.toastr.error('Error loading documents: ' + (err.message || 'Unknown error'));
        this.loading = false;
      }
    });
  }

  toggleDocumentSelection(docId: number) {
    const index = this.selectedDocumentIds.indexOf(docId);
    if (index > -1) {
      this.selectedDocumentIds.splice(index, 1);
    } else {
      this.selectedDocumentIds.push(docId);
    }
  }

  sendMessage() {
    if (!this.receiverUsername || !this.content) {
      this.toastr.error('Please fill in all required fields.');
      return;
    }

    this.loading = true;
    this.messageService.sendMessage(this.receiverUsername, this.content, this.selectedDocumentIds).subscribe({
      next: () => {
        this.toastr.success('Message sent successfully!');
        this.receiverUsername = '';
        this.content = '';
        this.selectedDocumentIds = [];
        this.loading = false;
        this.router.navigate(['/messages/sent']);
      },
      error: (err) => {
        if (err.message.includes('Session expired')) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        this.toastr.error('Failed to send message: ' + (err.message || 'Unknown error'));
        this.loading = false;
      }
    });
  }
}