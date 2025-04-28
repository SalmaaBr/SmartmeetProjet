import { Component, OnInit } from '@angular/core';
import { User, TypeUserRole } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service'; // Importez le AuthService




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

  constructor(private userService: UserService,private http: HttpClient,private router: Router,    private authService: AuthService // Injectez le AuthService
  ) {}

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
      // Pour chaque utilisateur recommandé, récupérer ses réunions
      this.recommendedUsers.forEach(user => {
        if (user.userID) {
          this.userService.getMeetingsByUserId(user.userID).subscribe(meetings => {
            user.meetings = meetings;
          });
        }
      });
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

// Dans votre composant Angular
sendInterview(user: User): void {
  // Récupérer l'ID de l'utilisateur connecté (peut être null/undefined si non connecté)
  const organizerId = this.authService.getCurrentUserId();
  
  const meetingRequest = {
    meetingName: `Entretien avec ${user.username}`,
    participantId: user.userID,
    durationMinutes: 5
  };

  // Envoyer la requête avec ou sans organizerId
  this.http.post<any>('http://localhost:8082/api/meetings/interview', meetingRequest, {
    headers: { 
      'X-User-ID': organizerId ? organizerId.toString() : '0' // 0 pour les utilisateurs non connectés
    }
  }).subscribe(
    (response) => {
      this.router.navigate([`/meeting/${response.id}`]);
      alert(`Réunion programmée pour le ${new Date(response.startTime).toLocaleString()}`);
    },
    (error) => {
      console.error('Erreur lors de la création de la réunion', error);
      alert('Erreur lors de la planification de la réunion');
    }
  );
}


// Nouvelle méthode pour naviguer vers une réunion
goToMeeting(meetingLink: string): void {
  window.open(meetingLink, '_blank'); // Ouvre le lien dans un nouvel onglet
}

  
}