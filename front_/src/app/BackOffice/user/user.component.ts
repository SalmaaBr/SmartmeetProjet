declare var bootstrap: any; // Ensure bootstrap is available

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
  selectedUser: User = {} as User;
  isEditing = false;
  roles: TypeUserRole[] = [];
  userToDelete: number | null = null; // Store ID of user to delete

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
    this.roles = this.userService.getRoles();
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe(users => {
      this.users = users;
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
    // Create a copy to avoid direct mutation
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
    // Reset the form using the initializer
    this.initNewUser();
  }

  confirmDelete(userId: number): void {
    this.userToDelete = userId;
    // Programmatically show the delete modal
    const modalElement = document.getElementById('deleteModal');
    if (modalElement) {
      const modal = new bootstrap.Modal(modalElement);
      modal.show();
    }
  }

  deleteConfirmed(): void {
    if (this.userToDelete !== null) {
      this.userService.deleteUser(this.userToDelete)
        .subscribe(() => {
          this.loadUsers();
          this.userToDelete = null;
          // Hide the modal programmatically
          const modalElement = document.getElementById('deleteModal');
          if (modalElement) {
            const modalInstance = bootstrap.Modal.getInstance(modalElement);
            if (modalInstance) {
              modalInstance.hide();
            } else {
              const modal = new bootstrap.Modal(modalElement);
              modal.hide();
            }
          }
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

  // Utility method to close modals by ID
  closeModal(modalId: string): void {
    const modalElement = document.getElementById(modalId);
    if (modalElement) {
      const modalInstance = bootstrap.Modal.getInstance(modalElement);
      if (modalInstance) {
        modalInstance.hide();
      } else {
        const modal = new bootstrap.Modal(modalElement);
        modal.hide();
      }
    }
  }
}
