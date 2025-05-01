import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ReservationService } from '../../services/reservation.service';
import { ResourceService } from '../../services/resource.service';
import { UserService } from '../../services/user.service';
import { Reservation } from '../../models/reservation.model';
import { Resource } from '../../models/resource.model';
import { User } from '../../models/user.model';
import { ToastrService } from 'ngx-toastr';
import { Chart, ChartConfiguration, ChartEvent, registerables, TooltipItem } from 'chart.js';
import { DatePipe } from '@angular/common';
import { format, parseISO, eachDayOfInterval, getMonth, getYear, isWithinInterval, addMonths, subMonths } from 'date-fns';

// Register all Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-resource-reservation-statistics',
  templateUrl: './resource-reservation-statistics.component.html',
  styleUrls: ['./resource-reservation-statistics.component.css'],
  providers: [DatePipe]
})
export class ResourceReservationStatisticsComponent implements OnInit, AfterViewInit {
  @ViewChild('resourceUsageChart') resourceUsageChartRef!: ElementRef;
  @ViewChild('monthlyStatsChart') monthlyStatsChartRef!: ElementRef;
  @ViewChild('userStatsChart') userStatsChartRef!: ElementRef;
  @ViewChild('dayDistributionChart') dayDistributionChartRef!: ElementRef;

  reservations: Reservation[] = [];
  resources: Resource[] = [];
  users: User[] = [];
  filteredReservations: Reservation[] = [];

  // Chart instances
  resourceUsageChart: Chart | null = null;
  monthlyStatsChart: Chart | null = null;
  userStatsChart: Chart | null = null;
  dayDistributionChart: Chart | null = null;

  // Statistics data
  totalReservations = 0;
  totalReservationDays = 0;
  averageReservationLength = 0;
  mostReservedResource = '';
  mostActiveUser = '';

  filterForm = new FormGroup({
    resourceId: new FormControl(''),
    userId: new FormControl(''),
    startDate: new FormControl(''),
    endDate: new FormControl('')
  });

  // Color palette for charts
  chartColors = [
    '#4285F4', '#EA4335', '#FBBC05', '#34A853', // Google colors
    '#3b82f6', '#ef4444', '#f59e0b', '#10b981', // Tailwind colors
    '#6366f1', '#8b5cf6', '#ec4899', '#14b8a6'  // More colors
  ];

  constructor(
    private reservationService: ReservationService,
    private resourceService: ResourceService,
    private userService: UserService,
    private toastr: ToastrService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.loadResources();
    this.loadUsers();
    this.loadReservations();

    // Set initial date range filter to last 6 months
    const today = new Date();
    const sixMonthsAgo = subMonths(today, 6);

    this.filterForm.patchValue({
      startDate: format(sixMonthsAgo, 'yyyy-MM-dd'),
      endDate: format(today, 'yyyy-MM-dd')
    });
  }

  ngAfterViewInit(): void {
    // Charts will be initialized after data is loaded
  }

  loadResources(): void {
    this.resourceService.getAllResources().subscribe({
      next: (resources) => {
        this.resources = resources;
      },
      error: (error) => {
        this.toastr.error('Error loading resources');
        console.error(error);
      }
    });
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        this.toastr.error('Error loading users');
        console.error(error);
      }
    });
  }

  loadReservations(): void {
    this.reservationService.getAllResourceReservations().subscribe({
      next: (reservations) => {
        this.reservations = reservations;
        this.applyFilters(); // Apply default filters and update charts
      },
      error: (error) => {
        this.toastr.error('Error loading reservations');
        console.error(error);
      }
    });
  }

  applyFilters(): void {
    const filters = this.filterForm.value;

    // Start with all reservations
    let filtered = [...this.reservations];

    // Filter by resource
    if (filters.resourceId) {
      filtered = filtered.filter(res =>
        res.resourceId === parseInt(filters.resourceId || '') ||
        res.resource?.idResource === parseInt(filters.resourceId || '')
      );
    }

    // Filter by user
    if (filters.userId) {
      filtered = filtered.filter(res =>
        res.userId === parseInt(filters.userId || '') ||
        res.user?.userID === parseInt(filters.userId || '')
      );
    }

    // Filter by date range
    if (filters.startDate && filters.endDate) {
      const startDate = new Date(filters.startDate);
      const endDate = new Date(filters.endDate);

      filtered = filtered.filter(res => {
        const resStartDate = new Date(res.startTime.toString());
        const resEndDate = new Date(res.endTime.toString());

        // Check if the reservation overlaps with the filter date range
        return (
          isWithinInterval(resStartDate, { start: startDate, end: endDate }) ||
          isWithinInterval(resEndDate, { start: startDate, end: endDate }) ||
          (resStartDate <= startDate && resEndDate >= endDate)
        );
      });
    }

    this.filteredReservations = filtered;
    this.calculateStatistics();
    this.updateCharts();
  }

  calculateStatistics(): void {
    const reservations = this.filteredReservations;
    this.totalReservations = reservations.length;

    // Calculate total reservation days and average length
    let totalDays = 0;
    reservations.forEach(res => {
      const start = new Date(res.startTime.toString());
      const end = new Date(res.endTime.toString());
      const days = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1;
      totalDays += days;
    });

    this.totalReservationDays = totalDays;
    this.averageReservationLength = this.totalReservations > 0
      ? +(totalDays / this.totalReservations).toFixed(1)
      : 0;

    // Find most reserved resource
    const resourceCount: {[key: string]: number} = {};
    reservations.forEach(res => {
      const resourceId = res.resourceId || res.resource?.idResource;
      const resourceName = this.resources.find(r => r.idResource === resourceId)?.name || `Resource ${resourceId}`;

      if (resourceCount[resourceName]) {
        resourceCount[resourceName]++;
      } else {
        resourceCount[resourceName] = 1;
      }
    });

    let maxResourceCount = 0;
    this.mostReservedResource = '';

    Object.entries(resourceCount).forEach(([name, count]) => {
      if (count > maxResourceCount) {
        maxResourceCount = count;
        this.mostReservedResource = name;
      }
    });

    // Find most active user
    const userCount: {[key: string]: number} = {};
    reservations.forEach(res => {
      const userId = res.userId || res.user?.userID;
      const userName = this.users.find(u => u.userID === userId)?.username || `User ${userId}`;

      if (userCount[userName]) {
        userCount[userName]++;
      } else {
        userCount[userName] = 1;
      }
    });

    let maxUserCount = 0;
    this.mostActiveUser = '';

    Object.entries(userCount).forEach(([name, count]) => {
      if (count > maxUserCount) {
        maxUserCount = count;
        this.mostActiveUser = name;
      }
    });
  }

  updateCharts(): void {
    this.updateResourceUsageChart();
    this.updateMonthlyStatsChart();
    this.updateUserStatsChart();
    this.updateDayDistributionChart();
  }

  updateResourceUsageChart(): void {
    if (!this.resourceUsageChartRef) return;

    // Group reservations by resource
    const resourceData: {[key: string]: number} = {};

    this.filteredReservations.forEach(res => {
      const resourceId = res.resourceId || res.resource?.idResource;
      const resourceName = this.resources.find(r => r.idResource === resourceId)?.name || `Resource ${resourceId}`;

      const days = this.calculateReservationDays(res);

      if (resourceData[resourceName]) {
        resourceData[resourceName] += days;
      } else {
        resourceData[resourceName] = days;
      }
    });

    // Sort by usage (descending)
    const sortedResources = Object.entries(resourceData)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 10); // Show top 10

    const labels = sortedResources.map(item => item[0]);
    const data = sortedResources.map(item => item[1]);

    // Destroy previous chart if exists
    if (this.resourceUsageChart) {
      this.resourceUsageChart.destroy();
    }

    // Create new chart
    this.resourceUsageChart = new Chart(this.resourceUsageChartRef.nativeElement, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Days Reserved',
          data: data,
          backgroundColor: this.chartColors.slice(0, labels.length),
          borderColor: 'rgba(0, 0, 0, 0.1)',
          borderWidth: 1
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: 'Resource Usage (Days)',
            font: {
              size: 16,
              weight: 'bold'
            }
          },
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              label: function(tooltipItem) {
                const value = typeof tooltipItem.parsed === 'object'
                  ? tooltipItem.parsed.x
                  : tooltipItem.parsed;
                return `${value} days`;
              }
            }
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Number of Days'
            }
          }
        }
      }
    });
  }

  updateMonthlyStatsChart(): void {
    if (!this.monthlyStatsChartRef) return;

    const monthlyData: {[key: string]: number} = {};

    // Initialize with last 12 months
    const today = new Date();
    for (let i = 0; i < 12; i++) {
      const date = subMonths(today, i);
      const monthYear = format(date, 'MMM yyyy');
      monthlyData[monthYear] = 0;
    }

    // Count reservations by month
    this.filteredReservations.forEach(res => {
      const start = new Date(res.startTime.toString());
      const end = new Date(res.endTime.toString());

      // Count each day of the reservation in its respective month
      let current = new Date(start);
      while (current <= end) {
        const monthYear = format(current, 'MMM yyyy');

        // Only count if it's in the last 12 months
        if (monthlyData[monthYear] !== undefined) {
          monthlyData[monthYear]++;
        }

        // Move to next day
        current.setDate(current.getDate() + 1);
      }
    });

    // Sort months chronologically
    const sortedMonths = Object.entries(monthlyData)
      .map(([month, count]) => ({ month, count }))
      .sort((a, b) => {
        const dateA = new Date(a.month);
        const dateB = new Date(b.month);
        return dateA.getTime() - dateB.getTime();
      });

    const labels = sortedMonths.map(item => item.month);
    const data = sortedMonths.map(item => item.count);

    // Destroy previous chart if exists
    if (this.monthlyStatsChart) {
      this.monthlyStatsChart.destroy();
    }

    // Create new chart
    this.monthlyStatsChart = new Chart(this.monthlyStatsChartRef.nativeElement, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Reserved Days',
          data: data,
          fill: true,
          backgroundColor: 'rgba(66, 133, 244, 0.2)',
          borderColor: 'rgb(66, 133, 244)',
          tension: 0.3,
          pointBackgroundColor: 'rgb(66, 133, 244)',
          pointBorderColor: '#fff',
          pointRadius: 4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: 'Monthly Reservation Trends',
            font: {
              size: 16,
              weight: 'bold'
            }
          },
          tooltip: {
            callbacks: {
              label: function(tooltipItem) {
                const value = typeof tooltipItem.parsed === 'object'
                  ? tooltipItem.parsed.y
                  : tooltipItem.parsed;
                return `${value} days reserved`;
              }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: 'Number of Days'
            }
          }
        }
      }
    });
  }

  updateUserStatsChart(): void {
    if (!this.userStatsChartRef) return;

    // Group reservations by user
    const userData: {[key: string]: number} = {};

    this.filteredReservations.forEach(res => {
      const userId = res.userId || res.user?.userID;
      const userName = this.users.find(u => u.userID === userId)?.username || `User ${userId}`;

      if (userData[userName]) {
        userData[userName]++;
      } else {
        userData[userName] = 1;
      }
    });

    // Sort by number of reservations (descending)
    const sortedUsers = Object.entries(userData)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 8); // Show top 8

    const labels = sortedUsers.map(item => item[0]);
    const data = sortedUsers.map(item => item[1]);

    // Destroy previous chart if exists
    if (this.userStatsChart) {
      this.userStatsChart.destroy();
    }

    // Create new chart
    this.userStatsChart = new Chart(this.userStatsChartRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: this.chartColors.slice(0, labels.length),
          borderColor: 'white',
          borderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: 'Top Users by Reservations',
            font: {
              size: 16,
              weight: 'bold'
            }
          },
          tooltip: {
            callbacks: {
              label: function(tooltipItem) {
                const label = tooltipItem.label || '';
                const value = tooltipItem.parsed;
                const dataset = tooltipItem.dataset;
                const total = dataset.data.reduce((a, b) => Number(a) + Number(b), 0);
                const percentage = Math.round((Number(value) * 100) / total);
                return `${label}: ${value} (${percentage}%)`;
              }
            }
          }
        }
      }
    });
  }

  updateDayDistributionChart(): void {
    if (!this.dayDistributionChartRef) return;

    // Count reservations by day of week
    const dayData = [0, 0, 0, 0, 0, 0, 0]; // Sun, Mon, ..., Sat
    const dayLabels = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

    this.filteredReservations.forEach(res => {
      const start = new Date(res.startTime.toString());
      const end = new Date(res.endTime.toString());

      // Count each day in its respective day of week
      let current = new Date(start);
      while (current <= end) {
        const dayOfWeek = current.getDay(); // 0 = Sunday, 1 = Monday, etc.
        dayData[dayOfWeek]++;

        // Move to next day
        current.setDate(current.getDate() + 1);
      }
    });

    // Destroy previous chart if exists
    if (this.dayDistributionChart) {
      this.dayDistributionChart.destroy();
    }

    // Create new chart
    this.dayDistributionChart = new Chart(this.dayDistributionChartRef.nativeElement, {
      type: 'polarArea',
      data: {
        labels: dayLabels,
        datasets: [{
          data: dayData,
          backgroundColor: [
            'rgba(255, 99, 132, 0.7)',
            'rgba(54, 162, 235, 0.7)',
            'rgba(255, 206, 86, 0.7)',
            'rgba(75, 192, 192, 0.7)',
            'rgba(153, 102, 255, 0.7)',
            'rgba(255, 159, 64, 0.7)',
            'rgba(199, 199, 199, 0.7)'
          ]
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: 'Day of Week Distribution',
            font: {
              size: 16,
              weight: 'bold'
            }
          },
          tooltip: {
            callbacks: {
              label: function(tooltipItem) {
                return `${tooltipItem.label}: ${tooltipItem.parsed} days`;
              }
            }
          }
        }
      }
    });
  }

  resetFilters(): void {
    // Reset form but keep the date range
    const currentDates = {
      startDate: this.filterForm.get('startDate')?.value,
      endDate: this.filterForm.get('endDate')?.value
    };

    this.filterForm.reset(currentDates);
    this.applyFilters();
    this.toastr.info('Filters have been reset');
  }

  calculateReservationDays(reservation: Reservation): number {
    const start = new Date(reservation.startTime.toString());
    const end = new Date(reservation.endTime.toString());
    return Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1;
  }
}
