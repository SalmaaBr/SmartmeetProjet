import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ReservationService, MaintenancePeriod } from '../../services/reservation.service';
import { Reservation } from '../../models/reservation.model';
import { Resource } from 'src/app/models/resource.model';
import { ResourceService } from 'src/app/services/resource.service';
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/app/models/user.model';
import { format, parseISO, isWithinInterval, startOfDay, isBefore, isAfter, eachDayOfInterval } from 'date-fns';
import { ToastrService } from 'ngx-toastr';
import { switchMap } from 'rxjs';
import { DatePipe } from '@angular/common';
import flatpickr from 'flatpickr';
import { French } from 'flatpickr/dist/l10n/fr';


@Component({
  selector: 'app-resource-reservation-management',
  templateUrl: './resource-reservation-management.component.html',
  styleUrls: ['./resource-reservation-management.component.css'],
  providers: [DatePipe]
})
export class ResourceReservationManagementComponent implements OnInit {
  reservations: Reservation[] = [];
  reservationForm!: FormGroup;
  resources: Resource[] = [];
  users: User[] = [];
  isEditing = false;
  currentReservationId: number | null = null;
  reservedDates: string[] = [];
  maintenancePeriods: MaintenancePeriod[] = [];
  minDate: string;
  startDatePicker: any;
  endDatePicker: any;
  maintenanceDates: string[] = [];
  flatpickrConfig = {
    dateFormat: 'Y-m-d',
    locale: French,
    minDate: 'today',
    disable: [] as string[],
    disableMobile: true,
    onChange: (selectedDates: Date[], dateStr: string, instance: any) => {
      // Update form control value when date is selected
      if (instance.element.id === 'start-time') {
        this.reservationForm.get('startTime')?.setValue(dateStr);
      } else if (instance.element.id === 'end-time') {
        this.reservationForm.get('endTime')?.setValue(dateStr);
      }
    }
  };

  constructor(
    private resourceService: ResourceService,
    private reservationService: ReservationService,
    private userService: UserService,
    private toastr: ToastrService,
    private datePipe: DatePipe
  ) {
    this.minDate = this.datePipe.transform(new Date(), 'yyyy-MM-dd') || '';
    this.initForm();
  }

  private initForm() {
    this.reservationForm = new FormGroup({
      resourceId: new FormControl('', Validators.required),
      userId: new FormControl('', Validators.required),
      startTime: new FormControl('', [Validators.required]),
      endTime: new FormControl('', [Validators.required])
    });

    // Listen for changes to validate dates
    this.reservationForm.get('startTime')?.valueChanges.subscribe(value => {
      if (value && this.reservedDates.includes(value)) {
        setTimeout(() => this.reservationForm.get('startTime')?.setValue(''), 100);
        this.toastr.error('This date is already reserved', 'Error');
      }
    });

    this.reservationForm.get('endTime')?.valueChanges.subscribe(value => {
      if (value && this.reservedDates.includes(value)) {
        setTimeout(() => this.reservationForm.get('endTime')?.setValue(''), 100);
        this.toastr.error('This date is already reserved', 'Error');
      }
    });
  }

  ngOnInit(): void {
    this.loadResources();
    this.loadUsers();
    this.loadReservations();
    this.initFlatpickr();
  }

  private initFlatpickr() {
    setTimeout(() => {
      const startDateInput = document.getElementById('start-time');
      const endDateInput = document.getElementById('end-time');

      if (startDateInput) {
        this.startDatePicker = flatpickr(startDateInput, {
          ...this.flatpickrConfig,
          onChange: (selectedDates: Date[], dateStr: string) => {
            this.reservationForm.get('startTime')?.setValue(dateStr);
            if (this.endDatePicker && selectedDates[0]) {
              this.endDatePicker.set('minDate', selectedDates[0]);
            }
          }
        });
      }

      if (endDateInput) {
        this.endDatePicker = flatpickr(endDateInput, {
          ...this.flatpickrConfig,
          onChange: (selectedDates: Date[], dateStr: string) => {
            this.reservationForm.get('endTime')?.setValue(dateStr);
          }
        });
      }
    }, 0);
  }

  onResourceSelected() {
    const resourceId = this.reservationForm.get('resourceId')?.value;
    if (resourceId) {
      // Reset dates when resource changes
      this.reservationForm.patchValue({
        startTime: '',
        endTime: ''
      });

      // Load reserved dates
      this.reservationService.getReservedDatesForResource(resourceId).subscribe({
        next: (dates: string[]) => {
          this.reservedDates = dates;
          this.updateDisabledDates();
        },
        error: (error) => {
          console.error('Error loading reserved dates:', error);
          this.toastr.error('Error loading reserved dates');
        }
      });

      // Load maintenance periods
      this.reservationService.getMaintenancePeriods(resourceId).subscribe({
        next: (periods: MaintenancePeriod[]) => {
          this.maintenancePeriods = periods;

          // Extract all dates from maintenance periods
          this.maintenanceDates = this.extractMaintenanceDates(periods);
          this.updateDisabledDates();

          if (periods.length > 0) {
            this.toastr.warning(`This resource has ${periods.length} scheduled maintenance periods`);
          }
        },
        error: (error) => {
          console.error('Error loading maintenance periods:', error);
          this.toastr.error('Error loading maintenance periods');
        }
      });
    }
  }

  // Extract all individual dates from maintenance periods
  private extractMaintenanceDates(periods: MaintenancePeriod[]): string[] {
    const allDates: string[] = [];

    periods.forEach(period => {
      const startDate = parseISO(period.startDate);
      const endDate = parseISO(period.endDate);

      // Get all dates in between (inclusive)
      const datesInRange = eachDayOfInterval({ start: startDate, end: endDate });

      // Format dates to string
      datesInRange.forEach(date => {
        allDates.push(format(date, 'yyyy-MM-dd'));
      });
    });

    return allDates;
  }

  // Update disabled dates in flatpickr
  private updateDisabledDates() {
    // Combine reserved dates and maintenance dates
    const disabledDates = [...new Set([...this.reservedDates, ...this.maintenanceDates])];

    if (this.startDatePicker) {
      this.startDatePicker.set('disable', disabledDates);
      this.startDatePicker.redraw();
    }

    if (this.endDatePicker) {
      this.endDatePicker.set('disable', disabledDates);
      this.endDatePicker.redraw();
    }

    if (disabledDates.length > 0) {
      const reservedCount = this.reservedDates.length;
      const maintenanceCount = this.maintenanceDates.length;

      if (maintenanceCount > 0) {
        this.toastr.info(`${maintenanceCount} dates are blocked due to scheduled maintenance`, 'Maintenance Periods');
      }
    }
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

  private loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des utilisateurs', 'Erreur');
        console.error('Error loading users:', error);
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
    const startDate = new Date(formValue.startTime);
    const endDate = new Date(formValue.endTime);

    // Check if end date is before start date
    if (startDate > endDate) {
      this.toastr.error('Start date must be before end date', 'Error');
      return;
    }

    // Check if any date in the range is reserved
    let currentDate = new Date(startDate);
    const dateInRange = [];

    // Loop through the date range
    while (currentDate <= endDate) {
      const dateStr = this.datePipe.transform(currentDate, 'yyyy-MM-dd') || '';
      if (this.reservedDates.includes(dateStr)) {
        dateInRange.push(dateStr);
      }
      currentDate.setDate(currentDate.getDate() + 1);
    }

    // If any date in the range is reserved, show error
    if (dateInRange.length > 0) {
      this.toastr.error(`These dates are reserved: ${dateInRange.join(', ')}`, 'Error');
      return;
    }

    // Check if the date range overlaps with any maintenance period
    const maintenanceOverlap = this.maintenancePeriods.filter(period => {
      const maintStartDate = new Date(period.startDate);
      const maintEndDate = new Date(period.endDate);

      return !(endDate < maintStartDate || startDate > maintEndDate);
    });

    if (maintenanceOverlap.length > 0) {
      const formattedPeriods = maintenanceOverlap.map(period => {
        const start = new Date(period.startDate);
        const end = new Date(period.endDate);
        return `${format(start, 'MMM dd, yyyy')} to ${format(end, 'MMM dd, yyyy')}`;
      });

      this.toastr.error(`The selected dates overlap with scheduled maintenance: ${formattedPeriods.join('; ')}`, 'Maintenance Conflict');
      return;
    }

    const reservationToSend: Reservation = {
      resourceId: formValue.resourceId,
      user: this.users.find(user => user.userID === parseInt(formValue.userId)),
      startTime: format(startDate, 'yyyy-MM-dd'),
      endTime: format(endDate, 'yyyy-MM-dd'),
      resource: { idResource: formValue.resourceId, name: '', typeResource: '', typeResourceStatus: '' }
    };

    if (this.isEditing && this.currentReservationId) {
      this.updateReservation(reservationToSend);
    } else {
      this.createReservation(reservationToSend);
    }
  }

  private createReservation(reservation: Reservation) {
    const resourceId = reservation.resourceId;

    // Create reservation object in the format expected by the API
    const reservationData = {
      startTime: reservation.startTime,
      endTime: reservation.endTime,
      user: reservation.user,
      resource: {
        idResource: resourceId
      }
    };

    // Use the simple create endpoint
    this.reservationService.createReservation(reservationData).subscribe({
      next: () => this.handleSuccess('Reservation created successfully'),
      error: (error) => this.handleError(error, 'Error creating reservation')
    });
  }

  private updateReservation(reservation: Reservation) {
    const resourceId = reservation.resourceId;

    // Create reservation object in the format expected by the API
    const reservationData = {
      startTime: reservation.startTime,
      endTime: reservation.endTime,
      user: reservation.user,
      resource: {
        idResource: resourceId
      }
    };

    // Use the update endpoint
    this.reservationService.updateReservation(this.currentReservationId!, reservationData).subscribe({
      next: () => this.handleSuccess('Reservation updated successfully'),
      error: (error) => this.handleError(error, 'Error updating reservation')
    });
  }

  deleteReservation(id: number) {
    this.reservationService.deleteReservation(id).subscribe({
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

    // Find the user
    const userId = reservation.user?.userID || reservation.userId;

    this.reservationForm.patchValue({
      resourceId: reservation.resource?.idResource || '',
      userId: userId,
      startTime: format(parseISO(reservation.startTime.toString()), 'yyyy-MM-dd'),
      endTime: format(parseISO(reservation.endTime.toString()), 'yyyy-MM-dd')
    });

    // Update flatpickr instances with new dates
    if (this.startDatePicker) {
      this.startDatePicker.setDate(format(parseISO(reservation.startTime.toString()), 'yyyy-MM-dd'));
    }

    if (this.endDatePicker) {
      this.endDatePicker.setDate(format(parseISO(reservation.endTime.toString()), 'yyyy-MM-dd'));
    }

    // Load reserved dates for the resource, excluding the current reservation
    const resourceId = reservation.resource?.idResource;
    if (resourceId) {
      this.reservationService.getReservedDatesForResource(resourceId).subscribe({
        next: (dates: string[]) => {
          // Filter out dates from the current reservation
          const currentStartDate = format(parseISO(reservation.startTime.toString()), 'yyyy-MM-dd');
          const currentEndDate = format(parseISO(reservation.endTime.toString()), 'yyyy-MM-dd');

          this.reservedDates = dates.filter(date => {
            // Check if this date is not part of the current reservation
            const dateObj = new Date(date);
            const currentStart = new Date(currentStartDate);
            const currentEnd = new Date(currentEndDate);

            return !(dateObj >= currentStart && dateObj <= currentEnd);
          });

          // Update flatpickr disable dates
          if (this.startDatePicker) {
            this.startDatePicker.set('disable', this.reservedDates);
            this.startDatePicker.redraw();
          }

          if (this.endDatePicker) {
            this.endDatePicker.set('disable', this.reservedDates);
            this.endDatePicker.redraw();
          }
        },
        error: (error) => {
          console.error('Error loading reserved dates:', error);
          this.toastr.error('Error loading reserved dates');
        }
      });
    }
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
