import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ResourceService, Resource } from '../../services/resource.service';
import { ToastrService } from 'ngx-toastr';
import { ReservationService, MaintenancePeriod, AffectedReservation } from '../../services/reservation.service';
import { differenceInDays, parseISO, isWithinInterval, isSameDay, isAfter, isBefore, format } from 'date-fns';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-resource-maintenance',
  templateUrl: './resource-maintenance.component.html',
  styleUrls: ['./resource-maintenance.component.css']
})
export class ResourceMaintenanceComponent implements OnInit {
  resources: Resource[] = [];
  selectedResource?: Resource;
  maintenanceForm!: FormGroup;
  upcomingMaintenancePeriods: MaintenancePeriod[] = [];
  affectedReservations: AffectedReservation[] = [];
  isLoading = false;
  notificationDays: number = 7;
  notificationsSent: number = 0;
  isNotifying: boolean = false;

  constructor(
    private resourceService: ResourceService,
    private reservationService: ReservationService,
    private notificationService: NotificationService,
    private formBuilder: FormBuilder,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadResources();
    this.initForm();
  }

  loadResources(): void {
    this.isLoading = true;
    this.resourceService.getAllResources().subscribe({
      next: (resources) => {
        this.resources = resources;
        this.isLoading = false;
      },
      error: (error) => {
        this.toastr.error('Error loading resources');
        console.error(error);
        this.isLoading = false;
      }
    });
  }

  initForm(): void {
    this.maintenanceForm = this.formBuilder.group({
      resourceId: ['', Validators.required],
      maintenanceEnabled: [false],
      maintenancePeriodMonths: [6, [Validators.required, Validators.min(1), Validators.max(24)]],
      maintenanceDurationDays: [1, [Validators.required, Validators.min(1), Validators.max(14)]],
      initialMaintenanceDate: ['', Validators.required]
    });

    // React to resource selection
    this.maintenanceForm.get('resourceId')?.valueChanges.subscribe((resourceId) => {
      if (resourceId) {
        this.loadResourceDetails(parseInt(resourceId));
      }
    });
  }

  loadResourceDetails(resourceId: number): void {
    this.isLoading = true;
    this.resourceService.getResourceById(resourceId).subscribe({
      next: (resource) => {
        this.selectedResource = resource;
        // Update form with resource's maintenance settings
        this.maintenanceForm.patchValue({
          maintenanceEnabled: resource['maintenanceEnabled'] || false,
          maintenancePeriodMonths: resource['maintenancePeriodMonths'] || 6,
          maintenanceDurationDays: resource['maintenanceDurationDays'] || 1,
          initialMaintenanceDate: resource['initialMaintenanceDate'] || ''
        });

        this.loadUpcomingMaintenancePeriods(resourceId);
        this.loadAffectedReservations(resourceId);
        this.isLoading = false;
      },
      error: (error) => {
        this.toastr.error('Error loading resource details');
        console.error(error);
        this.isLoading = false;
      }
    });
  }

  loadUpcomingMaintenancePeriods(resourceId: number): void {
    this.reservationService.getMaintenancePeriods(resourceId).subscribe({
      next: (periods) => {
        this.upcomingMaintenancePeriods = periods.sort((a, b) =>
          new Date(a.startDate).getTime() - new Date(b.startDate).getTime()
        );
      },
      error: (error) => {
        this.toastr.error('Error loading maintenance periods');
        console.error(error);
      }
    });
  }

  loadAffectedReservations(resourceId: number): void {
    this.reservationService.getConflictingReservations(resourceId).subscribe({
      next: (reservations: AffectedReservation[]) => {
        this.affectedReservations = reservations;
      },
      error: (error: any) => {
        console.error('Error loading affected reservations:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.maintenanceForm.invalid) {
      this.toastr.error('Please fill in all required fields correctly');
      return;
    }

    const resourceId = parseInt(this.maintenanceForm.get('resourceId')?.value);
    const maintenanceConfig = {
      maintenanceEnabled: this.maintenanceForm.get('maintenanceEnabled')?.value,
      maintenancePeriodMonths: this.maintenanceForm.get('maintenancePeriodMonths')?.value,
      maintenanceDurationDays: this.maintenanceForm.get('maintenanceDurationDays')?.value,
      initialMaintenanceDate: this.maintenanceForm.get('initialMaintenanceDate')?.value
    };

    console.log('Submitting maintenance config:', maintenanceConfig);
    this.isLoading = true;

    this.resourceService.updateMaintenanceSettings(resourceId, maintenanceConfig).subscribe({
      next: (resource) => {
        console.log('Received response:', resource);
        this.toastr.success('Maintenance settings updated successfully');
        this.selectedResource = resource;
        this.loadUpcomingMaintenancePeriods(resourceId);
        this.loadAffectedReservations(resourceId);
        this.isLoading = false;

        if (maintenanceConfig.maintenanceEnabled && this.affectedReservations.length > 0) {
          this.toastr.warning(`${this.affectedReservations.length} existing reservations are affected by maintenance windows. Users will be notified.`);

          // Offer to send notifications
          if (confirm(`Do you want to notify users about the ${this.affectedReservations.length} affected reservations now?`)) {
            this.sendMaintenanceNotifications(resourceId);
          }
        }
      },
      error: (error) => {
        console.error('Error updating maintenance settings:', error);
        this.toastr.error('Error updating maintenance settings: ' + (error.message || 'Unknown error'));
        this.isLoading = false;
      }
    });
  }

  disableMaintenance(): void {
    if (!this.selectedResource) {
      this.toastr.error('No resource selected');
      return;
    }

    const resourceId = this.selectedResource.idResource || 0;
    const maintenanceConfig = {
      maintenanceEnabled: false,
      maintenancePeriodMonths: this.maintenanceForm.get('maintenancePeriodMonths')?.value || 6,
      maintenanceDurationDays: this.maintenanceForm.get('maintenanceDurationDays')?.value || 1,
      initialMaintenanceDate: this.maintenanceForm.get('initialMaintenanceDate')?.value || ''
    };

    this.isLoading = true;
    this.resourceService.updateMaintenanceSettings(resourceId, maintenanceConfig).subscribe({
      next: (resource) => {
        this.toastr.success('Maintenance disabled successfully');
        this.selectedResource = resource;
        this.maintenanceForm.patchValue({
          maintenanceEnabled: false
        });
        this.upcomingMaintenancePeriods = [];
        this.affectedReservations = [];
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error disabling maintenance:', error);
        this.toastr.error('Error disabling maintenance: ' + (error.message || 'Unknown error'));
        this.isLoading = false;
      }
    });
  }

  isCurrentMaintenance(period: MaintenancePeriod): boolean {
    const today = new Date();
    const startDate = parseISO(period.startDate);
    const endDate = parseISO(period.endDate);

    return (isWithinInterval(today, { start: startDate, end: endDate }) ||
            isSameDay(today, startDate) ||
            isSameDay(today, endDate));
  }

  isNextMaintenance(period: MaintenancePeriod): boolean {
    if (this.upcomingMaintenancePeriods.length === 0) return false;

    // Get the first upcoming period that's not in the past
    const today = new Date();
    const upcomingPeriods = this.upcomingMaintenancePeriods
      .filter(p => isAfter(parseISO(p.endDate), today))
      .sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime());

    if (upcomingPeriods.length === 0) return false;

    // Check if this is the first upcoming period
    return period.startDate === upcomingPeriods[0].startDate;
  }

  calculateDuration(period: MaintenancePeriod): number {
    const startDate = parseISO(period.startDate);
    const endDate = parseISO(period.endDate);
    return differenceInDays(endDate, startDate) + 1; // +1 because it's inclusive
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    return format(parseISO(dateString), 'MMM dd, yyyy');
  }

  // Send notifications to affected users
  sendMaintenanceNotifications(resourceId: number): void {
    this.isNotifying = true;
    this.notificationService.sendUpcomingMaintenanceNotifications(resourceId, this.notificationDays).subscribe({
      next: (count: number) => {
        this.notificationsSent = count;
        if (count > 0) {
          this.toastr.success(`${count} notification(s) sent successfully`, 'Notifications Sent');
        } else {
          this.toastr.info('No new notifications needed to be sent', 'Notifications');
        }
        this.isNotifying = false;
      },
      error: (error) => {
        console.error('Error sending notifications:', error);
        this.toastr.error('Failed to send notifications', 'Error');
        this.isNotifying = false;
      }
    });
  }
}
