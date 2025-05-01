import { Component, OnInit } from '@angular/core';
import { User, TypeUserRole , Meeting} from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { RapportService } from '../../services/rapport.service';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service'; // Importez le AuthService
import * as bootstrap from 'bootstrap';




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
  showRapportModal: boolean = false; // Pour afficher le modal de génération de rapport
  rawReportText: string = ''; // Texte saisi pour le rapport
  selectedMeeting: Meeting | null = null; // Réunion sélectionnée pour le rapport

  constructor(private userService: UserService,private http: HttpClient,private router: Router,private rapportService: RapportService,    private authService: AuthService // Injectez le AuthService
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

  sendInterview(user: User): void {
    const meetingRequest = {
      meetingName: `Entretien avec ${user.username}`,
      participantId: user.userID, // Seul l'ID du participant est nécessaire
      durationMinutes: 5
    };
  
    this.http.post<any>('http://localhost:8082/api/meetings/interview', meetingRequest)
      .subscribe(
        (response) => {
          if (user.userID) {
            this.userService.getMeetingsByUserId(user.userID).subscribe(meetings => {
              const userIndex = this.recommendedUsers.findIndex(u => u.userID === user.userID);
              if (userIndex !== -1) {
                this.recommendedUsers[userIndex].meetings = meetings;
                this.recommendedUsers = [...this.recommendedUsers];
              }
            });
          }
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

// Nouvelle méthode pour supprimer une réunion
deleteMeeting(meetingId: number, userId: number | undefined): void {
  if (!userId) {
    console.error('User ID is undefined');
    alert('Erreur : utilisateur non valide');
    return;
  }

  if (confirm('Êtes-vous sûr de vouloir supprimer cette réunion ?')) {
    this.userService.deleteMeeting(meetingId).subscribe(
      () => {
        // Recharger les réunions de l'utilisateur après suppression
        this.userService.getMeetingsByUserId(userId).subscribe(meetings => {
          const user = this.recommendedUsers.find(u => u.userID === userId);
          if (user) {
            user.meetings = meetings;
          }
        });
        alert('Réunion supprimée avec succès');
      },
      (error) => {
        console.error('Erreur lors de la suppression de la réunion', error);
        alert('Erreur lors de la suppression de la réunion');
      }
    );
  }
}

openRapportModal(meeting: Meeting): void {
  this.selectedMeeting = meeting;
  this.rawReportText = '';
  this.showRapportModal = true;
}

generateRapport(): void {
  if (!this.selectedMeeting || !this.selectedMeeting.id) {
    alert('Erreur : réunion non valide');
    return;
  }

  this.rapportService.generateRapport(this.selectedMeeting.id, this.rawReportText).subscribe(
    (blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `rapport-meeting-${this.selectedMeeting!.id}.pdf`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      alert('Rapport généré et téléchargé avec succès');
      this.closeModal('rapportModal');
    },
    (error: any) => {
      console.error('Erreur lors de la génération du rapport', error);
      alert('Erreur lors de la génération du rapport');
    }
  );
}

closeModalReport(modalId: string): void {
  if (modalId === 'deleteModal') {
    this.showDeleteModal = false; // Hide the delete modal
    this.userToDelete = null;
  } else if (modalId === 'rapportModal') {
    this.showRapportModal = false; // Hide the rapport modal
    this.selectedMeeting = null;
    this.rawReportText = ''; // Optionnel: vider le texte du rapport
  }
}

  
}