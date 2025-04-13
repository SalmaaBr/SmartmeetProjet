import { Component } from '@angular/core';
import { InteractivePublicationService } from 'src/app/services/interactive-publication.service';
import { InteractivePublication } from 'src/app/models/interactive-publication.model';

@Component({
  selector: 'app-add-interactive-publication',
  templateUrl: './add-interactive-publication.component.html',
  styleUrls: ['./add-interactive-publication.component.css']
})
export class AddInteractivePublicationComponent {
  publication: InteractivePublication = new InteractivePublication();
  submitted = false;

  constructor(private publicationService: InteractivePublicationService) { }

  addPublication(): void {
    this.publicationService.createPublication(this.publication)
      .subscribe({
        next: (data) => {
          console.log('Publication ajoutée avec succès', data);
          this.submitted = true; // Marquer comme soumise pour cacher le formulaire ou afficher un message de succès
        },
        error: (error) => {
          console.error('Erreur lors de l\'ajout de la publication', error);
        }
      });
  }
}
