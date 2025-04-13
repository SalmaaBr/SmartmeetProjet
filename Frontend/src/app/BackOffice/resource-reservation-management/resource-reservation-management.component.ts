import { Component, OnInit } from '@angular/core';
import { ReservationService } from '../../services/reservation.service';
import { Reservation } from '../../models/reservation.model';
import { Resource } from 'src/app/models/resource.model';
import { ResourceService } from 'src/app/services/resource.service';
import { format, parseISO } from 'date-fns';
import { ToastrService } from 'ngx-toastr';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-resource-reservation-management',
  templateUrl: './resource-reservation-management.component.html',
  styleUrls: ['./resource-reservation-management.component.css']
})
export class ResourceReservationManagementComponent implements OnInit {
  reservations: Reservation[] = [];
  newReservation: Reservation = { resourceId: 0, user: '', startTime: '', endTime: '' };
  resources: Resource[] = [];
  isEditing = false;
  currentReservationId: number | null = null;

  constructor(
    private resourceService: ResourceService,
    private reservationService: ReservationService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.loadResources();
    this.loadReservations();
  }

  private loadResources(): void {
    this.resourceService.getAllResources().subscribe({
      next: (resources) => {
        this.resources = resources;
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des ressources', 'Erreur');
        console.error('Error loading resources:', error);
      }
    });
  }

  loadReservations() {
    this.reservationService.getAllResourceReservations().subscribe({
      next: (reservations) => {
        this.reservations = reservations;
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des réservations', 'Erreur');
        console.error(error);
      }
    });
  }

  onSubmit() {
    if (!this.validateForm()) return;

    // Format dates
    const formattedStartDate = format(new Date(this.newReservation.startTime), 'yyyy-MM-dd');
    const formattedEndDate = format(new Date(this.newReservation.endTime), 'yyyy-MM-dd');

    // Prepare reservation data
    const reservationToSend: Reservation = {
      ...this.newReservation,
      startTime: formattedStartDate,
      endTime: formattedEndDate,
      resources: [] // Initialize resources array
    };

    if (this.isEditing && this.currentReservationId) {
      // Update existing reservation
      this.resourceService.getResourceById(reservationToSend.resourceId).pipe(
        switchMap((resource: Resource) => {
          if (!resource.idResource) {
            throw new Error('Resource not found');
          }
          reservationToSend.resources = [resource];
          return this.reservationService.updateResourceReservation(
            this.currentReservationId!,
            reservationToSend
          );
        })
      ).subscribe({
        next: () => {
          this.toastr.success('Réservation mise à jour avec succès', 'Succès');
          this.resetForm();
          this.loadReservations();
        },
        error: (error) => {
          this.toastr.error(error.message || 'Erreur de mise à jour', 'Erreur');
          console.error(error);
        }
      });
    } else {
      // Create new reservation
      this.resourceService.getResourceById(reservationToSend.resourceId).pipe(
        switchMap((resource: Resource) => {
          if (!resource.idResource) {
            throw new Error('Resource not found');
          }
          reservationToSend.resources = [resource];
          return this.reservationService.createResourceReservation(reservationToSend);
        })
      ).subscribe({
        next: () => {
          this.toastr.success('Réservation créée avec succès', 'Succès');
          this.resetForm();
          this.loadReservations();
        },
        error: (error) => {
          this.toastr.error(error.message || 'Erreur de création', 'Erreur');
          console.error(error);
        }
      });
    }
  }

  deleteReservation(id: number) {
    this.reservationService.deleteResourceReservation(id).subscribe({
      next: () => {
        this.toastr.success('Réservation supprimée avec succès', 'Succès');
        this.loadReservations();
      },
      error: (error) => {
        this.toastr.error('Erreur de suppression', 'Erreur');
        console.error(error);
      }
    });
  }

  editReservation(reservation: Reservation) {
    this.isEditing = true;
    this.currentReservationId = reservation.reservationId!;
    this.newReservation = {
      ...reservation,
      startTime: format(parseISO(reservation.startTime.toString()), 'yyyy-MM-dd'),
      endTime: format(parseISO(reservation.endTime.toString()), 'yyyy-MM-dd')
    };
  }

  cancelEdit() {
    this.isEditing = false;
    this.currentReservationId = null;
    this.resetForm();
  }

  private validateForm(): boolean {
    if (!this.newReservation.resourceId ||
        !this.newReservation.user ||
        !this.newReservation.startTime ||
        !this.newReservation.endTime) {
      this.toastr.warning('Veuillez remplir tous les champs obligatoires', 'Attention');
      return false;
    }
    return true;
  }

  private resetForm() {
    this.newReservation = { resourceId: 0, user: '', startTime: '', endTime: '' };
    this.isEditing = false;
    this.currentReservationId = null;
  }
}
