import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { LostAndFoundListComponent } from './lost-and-found-list/lost-and-found-list.component';
import { LostAndFoundFormComponent } from './lost-and-found-form/lost-and-found-form.component';
import { UserEventsComponent } from './user-events/user-events.component';
import { ChatModule } from './chat/chat.module';

// Angular Material
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';

const routes: Routes = [
  { path: '', redirectTo: 'events', pathMatch: 'full' },
  { path: 'events', component: UserEventsComponent },
  { path: 'event/:eventId', component: LostAndFoundListComponent },
  { path: 'event/:eventId/new', component: LostAndFoundFormComponent },
  { path: 'edit/:id', component: LostAndFoundFormComponent }
];

@NgModule({
  declarations: [
    LostAndFoundListComponent,
    LostAndFoundFormComponent,
    UserEventsComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatIconModule,
    ChatModule
  ],
  exports: [
    RouterModule
  ]
})
export class LostAndFoundModule { }
