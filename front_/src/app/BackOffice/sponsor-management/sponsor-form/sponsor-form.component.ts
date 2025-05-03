import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { SponsorService } from '../../../services/sponsor.service';
import { Sponsor } from '../../../models/sponsor.model';
import { User } from '../../../models/user.model';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-sponsor-form',
  templateUrl: './sponsor-form.component.html',
  styleUrls: ['./sponsor-form.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule, CommonModule]
})
export class SponsorFormComponent implements OnInit {
  sponsorForm: FormGroup;
  isEditMode = false;
  sponsorId: number | null = null;
  errorMessage: string = '';
  isSubmitting = false;
  users: User[] = [];
  niveaux = ['PLATINE', 'OR', 'ARGENT', 'BRONZE'];

  constructor(
    private fb: FormBuilder,
    private sponsorService: SponsorService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService
  ) {
    this.sponsorForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      image: ['', Validators.required],
      niveau: ['BRONZE', Validators.required],
      statut: [true],
      siteWeb: ['', [Validators.required, Validators.pattern('https?://.+')]],
      responsibleUserId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUsers();
    this.sponsorId = this.route.snapshot.params['id'];
    if (this.sponsorId) {
      this.isEditMode = true;
      this.loadSponsor();
    }
  }

  loadUsers(): void {
    this.sponsorService.getUsersWithSponsorRole().subscribe(
      (users) => {
        this.users = users;
      },
      (error) => {
        console.error('Error loading users:', error);
        this.toastr.error('Failed to load users with sponsor role', 'Error');
      }
    );
  }

  loadSponsor(): void {
    if (this.sponsorId) {
      this.sponsorService.getSponsorById(this.sponsorId).subscribe(
        (sponsor) => {
          this.sponsorForm.patchValue({
            nom: sponsor.nom,
            description: sponsor.description,
            image: sponsor.image,
            niveau: sponsor.niveau,
            statut: sponsor.statut,
            siteWeb: sponsor.siteWeb,
            responsibleUserId: sponsor.responsibleUser?.userID
          });
        },
        (error) => {
          console.error('Error loading sponsor:', error);
          this.toastr.error('Failed to load sponsor details', 'Error');
        }
      );
    }
  }

  onSubmit(): void {
    if (this.sponsorForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      this.errorMessage = '';
      
      const sponsorData: Sponsor = {
        ...this.sponsorForm.value,
        idSponsor: this.isEditMode ? this.sponsorId! : 0
      };
      
      const request = this.isEditMode ? 
        this.sponsorService.updateSponsor(sponsorData) :
        this.sponsorService.createSponsor(sponsorData);

      request.subscribe(
        (response) => {
          const message = this.isEditMode ? 
            `Sponsor "${sponsorData.nom}" has been updated successfully.` :
            `Sponsor "${sponsorData.nom}" has been created successfully.`;
          this.toastr.success(message, 'Success');
          this.router.navigate(['/admin/sponsors']);
        },
        (error) => {
          console.error('Error saving sponsor:', error);
          this.isSubmitting = false;
          if (error.status === 404) {
            this.toastr.error('Sponsor not found. It may have been deleted.', 'Error');
          } else if (error.status === 400) {
            this.toastr.error('Invalid sponsor data. Please check your input.', 'Error');
          } else {
            this.toastr.error('An error occurred while saving the sponsor. Please try again.', 'Error');
          }
        }
      );
    } else {
      // Mark all fields as touched to trigger validation messages
      Object.keys(this.sponsorForm.controls).forEach(key => {
        const control = this.sponsorForm.get(key);
        control?.markAsTouched();
      });
      this.toastr.warning('Please fill in all required fields correctly.', 'Validation Error');
    }
  }

  onCancel(): void {
    this.router.navigate(['/admin/sponsors']);
  }
} 