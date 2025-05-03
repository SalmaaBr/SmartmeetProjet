import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SponsorService } from '../../../services/sponsor.service';
import { Sponsor } from '../../../models/sponsor.model';
import { ToastrService } from 'ngx-toastr';
import { SponsorEventService, SponsorEventAssignment, EventData } from '../../../services/sponsor-event.service';

@Component({
  selector: 'app-sponsor-list',
  templateUrl: './sponsor-list.component.html',
  styleUrls: ['./sponsor-list.component.css'],
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule]
})
export class SponsorListComponent implements OnInit {
  sponsors: Sponsor[] = [];
  filteredSponsors: Sponsor[] = [];
  searchTerm: string = '';
  selectedNiveau: string = 'ALL';
  events: EventData[] = [];
  selectedSponsor: any;
  showAssignmentModal = false;
  assignmentForm = {
    eventId: '',
    terms: '',
    amount: 0,
    expiryDate: '',
    contractFile: null as File | null
  };
  selectedFileName: string = '';
  isSubmitting = false;
  isCheckingAssignment = false;

  constructor(
    private sponsorService: SponsorService,
    private toastr: ToastrService,
    private sponsorEventService: SponsorEventService
  ) { }

  ngOnInit(): void {
    this.loadSponsors();
    this.loadEvents();
  }

  loadSponsors(): void {
    this.sponsorService.getAllSponsors().subscribe(
      (data) => {
        this.sponsors = data;
        this.filterSponsors();
      },
      (error) => {
        console.error('Error loading sponsors:', error);
        this.toastr.error('Failed to load sponsors. Please try again later.', 'Error');
      }
    );
  }

  loadEvents(): void {
    this.sponsorEventService.getAllEvents().subscribe(
      (events: EventData[]) => {
        this.events = events;
      },
      (error: any) => {
        console.error('Error loading events:', error);
        this.toastr.error('Failed to load events. Please try again later.', 'Error');
      }
    );
  }

  onSearchChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchTerm = target.value;
    this.filterSponsors();
  }

  onLevelChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.selectedNiveau = target.value;
    this.filterSponsors();
  }

  filterSponsors(): void {
    this.filteredSponsors = this.sponsors.filter(sponsor => {
      const matchesSearch = !this.searchTerm || 
        sponsor.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        sponsor.description.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesLevel = this.selectedNiveau === 'ALL' || sponsor.niveau === this.selectedNiveau;
      
      return matchesSearch && matchesLevel;
    });
  }

  handleImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    if (img) {
      img.src = 'https://placehold.co/100x100/e9ecef/495057?text=Logo';
    }
  }

  deleteSponsor(id: number): void {
    const sponsor = this.sponsors.find(s => s.idSponsor === id);
    if (!sponsor) return;

    if (confirm(`Are you sure you want to delete the sponsor "${sponsor.nom}"?`)) {
      this.sponsorService.deleteSponsor(id).subscribe(
        () => {
          this.sponsors = this.sponsors.filter(s => s.idSponsor !== id);
          this.filterSponsors();
          this.toastr.success(`Sponsor "${sponsor.nom}" has been deleted successfully.`, 'Success');
        },
        (error) => {
          console.error('Error deleting sponsor:', error);
          this.toastr.error(`Failed to delete sponsor "${sponsor.nom}". Please try again.`, 'Error');
        }
      );
    }
  }

  getNiveauClass(niveau: string): string {
    switch (niveau) {
      case 'PLATINE': return 'badge bg-primary';
      case 'OR': return 'badge bg-warning';
      case 'ARGENT': return 'badge bg-secondary';
      case 'BRONZE': return 'badge bg-danger';
      default: return 'badge bg-info';
    }
  }

  getStatusClass(statut: boolean): string {
    return statut ? 'badge bg-success' : 'badge bg-danger';
  }

  openAssignmentModal(sponsor: any): void {
    this.selectedSponsor = sponsor;
    this.showAssignmentModal = true;
  }

  closeAssignmentModal(): void {
    this.showAssignmentModal = false;
    this.selectedSponsor = null;
    this.resetAssignmentForm();
  }

  resetAssignmentForm(): void {
    this.assignmentForm = {
      eventId: '',
      terms: '',
      amount: 0,
      expiryDate: '',
      contractFile: null
    };
    this.selectedFileName = '';
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (file.type === 'application/pdf') {
        this.assignmentForm.contractFile = file;
        this.selectedFileName = file.name;
      } else {
        this.toastr.error('Please select a PDF file', 'Error');
        this.assignmentForm.contractFile = null;
        this.selectedFileName = '';
      }
    }
  }

  generateContractPDF(): void {
    if (!this.assignmentForm.eventId || !this.assignmentForm.terms || !this.assignmentForm.amount || !this.assignmentForm.expiryDate) {
      this.toastr.error('Please fill all required fields before generating the contract', 'Error');
      return;
    }

    const selectedEvent = this.events.find(e => e.id.toString() === this.assignmentForm.eventId);
    if (!selectedEvent) {
      this.toastr.error('Selected event not found', 'Error');
      return;
    }

    const assignment = {
      sponsorId: this.selectedSponsor.idSponsor,
      eventId: this.assignmentForm.eventId,
      terms: this.assignmentForm.terms,
      amount: this.assignmentForm.amount,
      expiryDate: this.assignmentForm.expiryDate
    };

    this.sponsorEventService.generateContractPDF(assignment, selectedEvent.title);
  }

  submitAssignment(): void {
    if (!this.selectedSponsor) {
      this.toastr.error('No sponsor selected', 'Error');
      return;
    }

    if (!this.assignmentForm.contractFile) {
      this.toastr.error('Please select a PDF file', 'Error');
      return;
    }

    if (!this.assignmentForm.eventId || !this.assignmentForm.terms || !this.assignmentForm.amount || !this.assignmentForm.expiryDate) {
      this.toastr.error('Please fill all required fields', 'Error');
      return;
    }

    this.isCheckingAssignment = true;
    this.sponsorEventService.checkExistingAssignment(
      this.selectedSponsor.idSponsor,
      this.assignmentForm.eventId
    ).subscribe(
      (exists) => {
        this.isCheckingAssignment = false;
        if (exists) {
          this.toastr.warning('This sponsor is already assigned to this event', 'Warning');
          return;
        }

        // At this point, we know contractFile is not null due to the earlier check
        const contractFile = this.assignmentForm.contractFile as File;
        
        this.isSubmitting = true;
        const assignment: SponsorEventAssignment = {
          sponsorId: this.selectedSponsor.idSponsor,
          eventId: this.assignmentForm.eventId,
          terms: this.assignmentForm.terms,
          amount: this.assignmentForm.amount,
          expiryDate: this.assignmentForm.expiryDate,
          contractFile: contractFile
        };

        this.sponsorEventService.assignSponsorToEvent(assignment).subscribe(
          (response) => {
            this.toastr.success('Sponsor successfully assigned to event', 'Success');
            this.closeAssignmentModal();
            this.loadSponsors(); // Refresh the list
          },
          (error) => {
            console.error('Error assigning sponsor to event:', error);
            this.toastr.error('Failed to assign sponsor to event', 'Error');
          },
          () => {
            this.isSubmitting = false;
          }
        );
      },
      (error) => {
        this.isCheckingAssignment = false;
        console.error('Error checking existing assignment:', error);
        this.toastr.error('Failed to check existing assignment', 'Error');
      }
    );
  }
} 