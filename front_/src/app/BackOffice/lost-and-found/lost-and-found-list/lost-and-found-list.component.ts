import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LostAndFoundResponse, LostAndFoundType } from '../../../models/lost-and-found.model';
import { LostAndFoundService } from '../../../services/lost-and-found.service';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { ChatModalComponent } from '../chat-modal/chat-modal.component';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-lost-and-found-list',
  templateUrl: './lost-and-found-list.component.html',
  styleUrls: ['./lost-and-found-list.component.css']
})
export class LostAndFoundListComponent implements OnInit {
  items: LostAndFoundResponse[] = [];
  filteredItems: LostAndFoundResponse[] = [];
  eventId?: number;
  loading = true;
  error: string | null = null;
  typeFilter = new FormControl('ALL');
  searchControl = new FormControl('');
  userId?: number;

  LostAndFoundType = LostAndFoundType; // Make enum available in template

  constructor(
    private lostAndFoundService: LostAndFoundService,
    private route: ActivatedRoute,
    private userService: UserService,

    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    // Get current user ID from local storage
    this.userService.getUserByEmail(localStorage.getItem('email') || '')
    .subscribe({
      next: (data) => {
        console.log("🚀 ~ LostAndFoundListComponent ~ ngOnInit ~ data:", data)
        this.userId = data.userID;

    },
    error: (err) => {
      this.error = 'Failed to load lost and found items.';
      this.loading = false;
      console.error(err);
    }
  });

    // Get event ID from route parameters
    this.route.params.subscribe(params => {
      this.eventId = +params['eventId'];
      this.loadItems();
    });

    // Set up filtering by search term
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(term => {
        this.applyFilters();
      });

    // Set up filtering by type
    this.typeFilter.valueChanges.subscribe(() => {
      this.applyFilters();
    });
  }

  loadItems(): void {
    this.loading = true;
    if (this.eventId) {
      this.lostAndFoundService.getLostAndFoundByEvent(this.eventId)
        .subscribe({
          next: (data) => {
            this.items = data;
          this.filteredItems = [...data];
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load lost and found items.';
          this.loading = false;
          console.error(err);
        }
      });
    }
  }

  applyFilters(): void {
    let result = [...this.items];

    // Apply type filter
    const typeValue = this.typeFilter.value;
    if (typeValue !== 'ALL') {
      result = result.filter(item => item.type === typeValue);
    }

    // Apply search filter
    const searchTerm = this.searchControl.value?.toLowerCase();
    if (searchTerm) {
      result = result.filter(item =>
        item.title.toLowerCase().includes(searchTerm) ||
        item.description.toLowerCase().includes(searchTerm)
      );
    }

    this.filteredItems = result;
  }

  deleteItem(id: number): void {
    if (confirm('Are you sure you want to delete this item?')) {
      this.lostAndFoundService.deleteLostAndFound(id)
        .subscribe({
          next: () => {
            this.items = this.items.filter(item => item.id !== id);
            this.filteredItems = this.filteredItems.filter(item => item.id !== id);
          },
          error: (err) => {
            console.error('Failed to delete item:', err);
            alert('Failed to delete item. Please try again.');
          }
        });
    }
  }

  // Renamed from canEdit to match the template
  canEditItem(item: LostAndFoundResponse): boolean {
    // Check if the current user is the creator of the item
    return item.creatorId === this.userId;
  }

  // Open chat modal for the selected item
  openChatModal(item: LostAndFoundResponse): void {
    this.dialog.open(ChatModalComponent, {
      width: '550px',
      height: '650px',
      data: {
        lostFoundItem: item,
        currentUserId: this.userId
      },
      disableClose: false,
      autoFocus: true,
      panelClass: 'chat-dialog-container'
    });
  }
}
