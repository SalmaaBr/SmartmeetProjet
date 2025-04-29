import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'src/app/services/message.service';
import { UnreadCountService } from 'src/app/services/unread-count.service';
import { AuthService } from 'src/app/auth/auth.service';
import { ToastrService } from 'ngx-toastr';
import { User, TypeUserRole } from 'src/app/models/user.model';
@Component({
  selector: 'app-header-front',
  templateUrl: './header-front.component.html',
  styleUrls: ['./header-front.component.css']
})
export class HeaderFrontComponent implements OnInit {
  isAuth: boolean = false;
username = localStorage.getItem("username");
roles = localStorage.getItem("roles");
unreadCount: number = 0;
  currentUser: User | null = null;

    constructor(private router: Router,
      private messageService: MessageService,
      private unreadCountService: UnreadCountService,
      private authService: AuthService,
      private toastr: ToastrService
    ) {}
    ngOnInit(): void {
      // Initialize auth status
      this.isAuth = this.authService.isLoggedIn();
      if (this.isAuth) {
        this.currentUser = this.authService.getCurrentUser();
        this.username = this.authService.getUsername() || 'User';
        this.roles = this.currentUser?.userRole?.join(', ') || 'USER';
        this.loadUnreadMessageCount();
      }
  
      // Subscribe to unread count updates
      this.unreadCountService.unreadCount$.subscribe(count => {
        this.unreadCount = count;
      });
  
      // Subscribe to auth status changes
      this.authService.authStatus$.subscribe(status => {
        this.isAuth = status;
        if (status) {
          this.currentUser = this.authService.getCurrentUser();
          this.username = this.authService.getUsername() || 'User';
          this.roles = this.currentUser?.userRole?.join(', ') || 'USER';
          this.loadUnreadMessageCount();
        } else {
          this.currentUser = null;
          this.username = null;
          this.roles = null;
          this.unreadCount = 0;
          this.unreadCountService.updateUnreadCount(0);
        }
      });
    }
  
    loadUnreadMessageCount() {
      if (this.isAuth) {
        this.messageService.getUnreadMessageCount().subscribe({
          next: (count) => {
            this.unreadCount = count;
            this.unreadCountService.updateUnreadCount(count);
          },
          error: (err) => {
            if (err.message.includes('Session expired')) {
              this.authService.logout();
              this.toastr.error('Session expired. Please log in again.');
            } else {
              this.toastr.error('Error loading unread message count: ' + (err.message || 'Unknown error'));
            }
          }
        });
      }
    }
  logout(){
    localStorage.removeItem("roles")
    localStorage.removeItem("username")
    localStorage.removeItem("auth-token")
    this.router.navigate(['login']);
  }
}