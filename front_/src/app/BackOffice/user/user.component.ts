import { Component, OnInit } from '@angular/core';
import { User, TypeUserRole } from '../../models/user.model';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  users: User[] = [];
  recommendedUsers: User[] = [];
  selectedUser: User = {} as User;
  isEditing = false;
  roles: TypeUserRole[] = [];
  userToDelete: number | null = null;
  showDeleteModal: boolean = false; // Boolean to toggle delete modal

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadRecommendedUsers();
    this.roles = this.userService.getRoles();
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe(users => {
      this.users = users;
    });
  }

  loadRecommendedUsers(): void {
    this.userService.getRecommendedUsers().subscribe(recommendedUsers => {
      this.recommendedUsers = recommendedUsers;
    });
  }

  initNewUser(): void {
    this.isEditing = false;
    this.selectedUser = {
      username: '',
      email: '',
      userRole: []
    };
  }

  editUser(user: User): void {
    this.isEditing = true;
    this.selectedUser = { ...user };
  }

  saveUser(): void {
    if (this.isEditing) {
      this.userService.updateUser(this.selectedUser.userID!, this.selectedUser)
        .subscribe(() => this.loadUsers());
    } else {
      this.userService.createUser(this.selectedUser)
        .subscribe(() => this.loadUsers());
    }
    this.initNewUser();
  }

  confirmDelete(userId: number): void {
    this.userToDelete = userId;
    this.showDeleteModal = true; // Show the delete modal
  }

  deleteConfirmed(): void {
    if (this.userToDelete !== null) {
      this.userService.deleteUser(this.userToDelete)
        .subscribe(() => {
          this.loadUsers();
          this.userToDelete = null;
          this.showDeleteModal = false; // Hide the delete modal
        });
    }
  }

  toggleRole(role: TypeUserRole): void {
    if (!this.selectedUser.userRole) {
      this.selectedUser.userRole = [];
    }
    const index = this.selectedUser.userRole.indexOf(role);
    if (index === -1) {
      this.selectedUser.userRole.push(role);
    } else {
      this.selectedUser.userRole.splice(index, 1);
    }
  }

  closeModal(modalId: string): void {
    if (modalId === 'deleteModal') {
      this.showDeleteModal = false; // Hide the delete modal
      this.userToDelete = null;
    }
  }
}