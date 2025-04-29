import { Component, OnInit } from '@angular/core';
   import { MessageService } from '../../services/message.service';
   import { AuthService } from 'src/app/auth/auth.service';
   import { ToastrService } from 'ngx-toastr';
   import { Router } from '@angular/router';
   import { Message } from '../../models/message.model';

   @Component({
     selector: 'app-message-sent',
     templateUrl: './message-sent.component.html',
     styleUrls: ['./message-sent.component.css']
   })
   export class MessageSentComponent implements OnInit {
    messages: Message[] = [];
    loading: boolean = false;
  
    constructor(
      private messageService: MessageService,
      private authService: AuthService,
      private toastr: ToastrService,
      private router: Router
    ) {}
  
    ngOnInit(): void {
      if (!this.authService.isLoggedIn()) {
        this.toastr.error('Please log in to view your sent messages.');
        this.router.navigate(['/login']);
        return;
      }
      this.loadSentMessages();
    }
  
    loadSentMessages() {
      this.loading = true;
      this.messageService.getSentMessages().subscribe({
        next: (messages: any[]) => {
          this.messages = messages.map(msg => ({
            id: msg.id,
            sender: msg.sender,
            receiver: msg.receiver,
            content: msg.content,
            sentAt: msg.sentAt,
            isRead: msg.read,
            documents: msg.documents || []
          }));
          this.loading = false;
        },
        error: (err) => {
          if (err.message.includes('Session expired')) {
            this.authService.logout();
          }
          this.toastr.error('Error loading sent messages: ' + (err.message || 'Unknown error'));
          this.loading = false;
        }
      });
    }
  }