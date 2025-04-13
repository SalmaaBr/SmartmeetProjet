import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
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
  reservationForm: FormGroup;
  resources: Resource[] = [];
  isEditing = false;
  currentReservationId: number | null = null;

  constructor(
    private resourceService: ResourceService,
    private reservationService: ReservationService,
    private toastr: ToastrService
  ) {
    this.reservationForm = new FormGroup({
      resourceId: new FormControl('', Validators.required),
      user: new FormControl('', Validators.required),
      startTime: new FormControl('', Validators.required),
      endTime: new FormControl('', Validators.required)
    });
  }

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
    if (this.reservationForm.invalid) {
      this.markFormGroupTouched(this.reservationForm);
      return;
    }

    const formValue = this.reservationForm.value;
    const reservationToSend: Reservation = {
      resourceId: formValue.resourceId,
      user: formValue.user,
      startTime: format(new Date(formValue.startTime), 'yyyy-MM-dd'),
      endTime: format(new Date(formValue.endTime), 'yyyy-MM-dd'),
      resources: []
    };

    if (this.isEditing && this.currentReservationId) {
      this.updateReservation(reservationToSend);
    } else {
      this.createReservation(reservationToSend);
    }
  }

  private createReservation(reservation: Reservation) {
    this.resourceService.getResourceById(reservation.resourceId).pipe(
      switchMap((resource: Resource) => {
        reservation.resources = [resource];
        return this.reservationService.createResourceReservation(reservation);
      })
    ).subscribe({
      next: () => this.handleSuccess('Réservation créée avec succès'),
      error: (error) => this.handleError(error, 'Erreur de création')
    });
  }

  private updateReservation(reservation: Reservation) {
    this.resourceService.getResourceById(reservation.resourceId).pipe(
      switchMap((resource: Resource) => {
        reservation.resources = [resource];
        return this.reservationService.updateResourceReservation(
          this.currentReservationId!,
          reservation
        );
      })
    ).subscribe({
      next: () => this.handleSuccess('Reservation created successfully'),
      error: (error) => this.handleError(error, 'Erreur de mise à jour')
    });
  }

  deleteReservation(id: number) {
    this.reservationService.deleteResourceReservation(id).subscribe({
      next: () => {
        this.toastr.success('Reservation successfully deleted', 'success');
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
    this.reservationForm.patchValue({
      resourceId: reservation.resources?.[0]?.idResource || '',
      user: reservation.user,
      startTime: format(parseISO(reservation.startTime.toString()), 'yyyy-MM-dd'),
      endTime: format(parseISO(reservation.endTime.toString()), 'yyyy-MM-dd')
    });
  }

  cancelEdit() {
    this.isEditing = false;
    this.currentReservationId = null;
    this.reservationForm.reset();
  }

  private handleSuccess(message: string) {
    this.toastr.success(message, 'Success');
    this.reservationForm.reset();
    this.loadReservations();
    this.isEditing = false;
    this.currentReservationId = null;
  }

  private handleError(error: any, title: string) {
    this.toastr.error(error.message || title, 'Erreur');
    console.error(error);
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}
