import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-view-profile',
  templateUrl: './view-profile.component.html',
  styleUrls: ['./view-profile.component.css']
})
export class ViewProfileComponent implements OnInit {
  user: User | undefined;
  isLoading = true;
  error: string | null = null;
  isCurrentUser = false;
  email: string | null = localStorage.getItem("email");

  constructor(
    private userService: UserService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    if (this.email) {
      this.loadUserByEmail(this.email);
    } else {
      this.error = 'Email not found in local storage';
      this.isLoading = false;
    }
  }

  loadUserByEmail(email: string): void {
    this.isLoading = true;
    this.error = null;
    this.isCurrentUser = true;

    this.userService.getUserByEmail(email).subscribe({
      next: (user) => {
        this.user = user;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load user data';
        this.isLoading = false;
        console.error('Error loading user by email:', err);
      }
    });
  }

  getUserRoles(): string {
    if (!this.user || !this.user.userRole) return 'No roles assigned';
    return Array.from(this.user.userRole).join(', ');
  }
}
