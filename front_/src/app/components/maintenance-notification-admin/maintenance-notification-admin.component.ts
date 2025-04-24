import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from '../../services/notification.service';
import { ResourceService } from '../../services/resource.service';

interface Resource {
  idResource: number;
  name: string;
}

@Component({
  selector: 'app-maintenance-notification-admin',
  templateUrl: './maintenance-notification-admin.component.html',
  styleUrls: ['./maintenance-notification-admin.component.scss']
})
export class MaintenanceNotificationAdminComponent implements OnInit {
  resources: any[] = [];
  form: FormGroup;
  loading = false;
  success = false;
  error: string | null = null;
  affectedReservations: any[] = [];
  showAffectedReservations = false;

  constructor(
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private resourceService: ResourceService
  ) {
    this.form = this.fb.group({
      resourceId: [null, Validators.required],
      startDate: [null, Validators.required],
      endDate: [null, Validators.required],
      daysInAdvance: [7, [Validators.required, Validators.min(1), Validators.max(30)]]
    });
  }

  ngOnInit(): void {
    this.loadResources();
  }

  loadResources(): void {
    this.loading = true;
    this.resourceService.getAllResources().subscribe({
      next: (data) => {
        this.resources = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading resources', err);
        this.error = 'Failed to load resources';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      return;
    }

    this.loading = true;
    this.success = false;
    this.error = null;

    const { resourceId, daysInAdvance } = this.form.value;

    this.notificationService.sendUpcomingMaintenanceNotifications(resourceId, daysInAdvance).subscribe({
      next: (count) => {
        this.success = true;
        this.loading = false;
        this.form.reset({
          resourceId: null,
          startDate: null,
          endDate: null,
          daysInAdvance: 7
        });
      },
      error: (err) => {
        console.error('Error sending notifications', err);
        this.error = 'Failed to send notifications';
        this.loading = false;
      }
    });
  }

  checkAffectedReservations(): void {
    if (!this.form.value.resourceId || !this.form.value.startDate || !this.form.value.endDate) {
      return;
    }

    const { resourceId, startDate, endDate } = this.form.value;

    this.loading = true;
    this.notificationService.getAffectedReservations(
      resourceId,
      this.formatDate(startDate),
      this.formatDate(endDate)
    ).subscribe({
      next: (data) => {
        this.affectedReservations = data;
        this.showAffectedReservations = true;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error checking affected reservations', err);
        this.error = 'Failed to check affected reservations';
        this.loading = false;
      }
    });
  }

  hideAffectedReservations(): void {
    this.showAffectedReservations = false;
  }

  // Format date to ISO string (YYYY-MM-DD)
  private formatDate(date: Date): string {
    const d = new Date(date);
    let month = '' + (d.getMonth() + 1);
    let day = '' + d.getDate();
    const year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
  }

  triggerAllNotifications(): void {
    if (this.loading) return;

    this.loading = true;
    this.success = false;
    this.error = null;

    this.notificationService.triggerAllNotifications(this.form.value.daysInAdvance || 7).subscribe({
      next: (count) => {
        this.success = true;
        this.loading = false;
        console.log(`Sent ${count} notifications`);
      },
      error: (err) => {
        console.error('Error triggering notifications', err);
        this.error = 'Failed to trigger notifications';
        this.loading = false;
      }
    });
  }
}
