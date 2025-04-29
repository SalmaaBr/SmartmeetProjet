import { Component, OnInit } from '@angular/core';
import { MessageService } from '../../services/message.service';
import { AuthService } from 'src/app/auth/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { Message } from '../../models/message.model';
import { UnreadCountService } from '../../services/unread-count.service';
@Component({
  selector: 'app-message-inbox',
  templateUrl: './message-inbox.component.html',
  styleUrls: ['./message-inbox.component.css']
})
export class MessageInboxComponent implements OnInit {
  messages: Message[] = [];
  loading: boolean = false;

  constructor(
    private messageService: MessageService,
    private authService: AuthService,
    private toastr: ToastrService,
    private unreadCountService: UnreadCountService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.toastr.error('Please log in to view your inbox.');
      this.router.navigate(['/login']);
      return;
    }
    this.loadInbox();
  }

  loadInbox() {
    this.loading = true;
    this.messageService.getInbox().subscribe({
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
        this.updateUnreadCount();
        this.loading = false;
      },
      error: (err) => {
        if (err.message.includes('Session expired')) {
          this.authService.logout();
        }
        this.toastr.error('Error loading inbox: ' + (err.message || 'Unknown error'));
        this.loading = false;
      }
    });
  }

  markAsRead(message: Message) {
    if (!message.isRead) {
      this.messageService.markAsRead(message.id).subscribe({
        next: () => {
          message.isRead = true;
          this.updateUnreadCount();
          this.toastr.success('Message marked as read.');
        },
        error: (err) => {
          if (err.message.includes('Session expired')) {
            this.authService.logout();
          }
          this.toastr.error('Error marking message as read: ' + (err.message || 'Unknown error'));
        }
      });
    }
  }

  updateUnreadCount() {
    this.messageService.getUnreadMessageCount().subscribe({
      next: (count) => {
        this.unreadCountService.updateUnreadCount(count);
      },
      error: (err) => {
        console.error('Error updating unread count:', err);
      }
    });
  }
}