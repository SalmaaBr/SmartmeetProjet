import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { LostAndFoundService } from '../../../services/lost-and-found.service';
import { LostAndFoundResponse, LostAndFoundType } from '../../../models/lost-and-found.model';
import { finalize } from 'rxjs/operators';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-lost-and-found-form',
  templateUrl: './lost-and-found-form.component.html',
  styleUrls: ['./lost-and-found-form.component.css']
})
export class LostAndFoundFormComponent implements OnInit {
  lostAndFoundForm: FormGroup;
  isEditMode = false;
  itemId?: number;
  eventId?: number;
  loading = false;
  submitting = false;
  errorMessage: string | null = null;
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  events: any[] = [];
  loadingEvents = false;

  LostAndFoundType = LostAndFoundType; // Make enum available in template

  constructor(
    private fb: FormBuilder,
    private lostAndFoundService: LostAndFoundService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    // Initialize form in constructor to ensure it's never undefined
    this.lostAndFoundForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
      type: [LostAndFoundType.LOST, Validators.required],
      imageUrl: [''],
      eventId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    // Load available events
    this.loadEvents();

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.itemId = +params['id'];
        this.loadItem();
      } else if (params['eventId']) {
        this.eventId = +params['eventId'];
        this.lostAndFoundForm.patchValue({ eventId: this.eventId });
      }
    });
  }

  loadEvents(): void {
    this.loadingEvents = true;
    this.lostAndFoundService.getAllEvents()
      .pipe(finalize(() => this.loadingEvents = false))
      .subscribe({
        next: (response) => {
          // Handle the response based on your API's response format
          // Adjust the property access as needed based on your API response
          this.events = Array.isArray(response) ? response : (response['content'] || []);

          // Ensure the event object has the correct property names
          this.events = this.events.map(event => ({
            id: event.id || event.eventId,
            title: event.title || event.name || 'Unnamed Event',
            // Add any other properties you need
          }));
        },
        error: (err) => {
          console.error('Failed to load events:', err);
          this.errorMessage = 'Failed to load available events.';
        }
      });
  }

  loadItem(): void {
    this.loading = true;
    if (this.itemId) {
      this.lostAndFoundService.getLostAndFoundById(this.itemId)
        .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (item: LostAndFoundResponse) => {
          this.lostAndFoundForm.patchValue({
            title: item.title,
            description: item.description,
            type: item.type,
            imageUrl: item.imageUrl,
            eventId: item.eventId
          });

          this.eventId = item.eventId;

          // Set image preview if available
          if (item.imageUrl) {
            this.previewUrl = item.imageUrl;
          }
        },
        error: (err) => {
          this.errorMessage = 'Failed to load item details.';
          console.error(err);
        }
        });
      }
  }

  onFileSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedFile = fileInput.files[0];

      // Create preview URL
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result as string;
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  async onSubmit(): Promise<void> {
    if (this.lostAndFoundForm.invalid) {
      // Mark all fields as touched to trigger validation visuals
      Object.keys(this.lostAndFoundForm.controls).forEach(key => {
        this.lostAndFoundForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.submitting = true;
    this.errorMessage = null;

    try {
      // Upload image if selected
      if (this.selectedFile) {
        const imageUrl = await firstValueFrom(this.lostAndFoundService.uploadImage(this.selectedFile));
        this.lostAndFoundForm.patchValue({ imageUrl });
      }

      const formData = this.lostAndFoundForm.value;

      if (this.isEditMode && this.itemId) {
        await firstValueFrom(this.lostAndFoundService.updateLostAndFound(this.itemId, formData));
        this.router.navigate(['/front/lost-and-found/event', this.eventId]);
      } else {
        await firstValueFrom(this.lostAndFoundService.createLostAndFound(formData));
        this.router.navigate(['/front/lost-and-found/event', formData.eventId]);
      }
    } catch (error) {
      console.error('Error submitting form:', error);
      this.errorMessage = 'Failed to save item. Please try again.';
    } finally {
      this.submitting = false;
    }
  }

  onCancel(): void {
    if (this.eventId) {
      this.router.navigate(['/front/lost-and-found/event', this.eventId]);
    } else {
      this.router.navigate(['/front/events']);
    }
  }
}
