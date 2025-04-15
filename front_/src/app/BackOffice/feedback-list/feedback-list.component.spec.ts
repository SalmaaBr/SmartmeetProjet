import { Component, OnInit } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { ToastrService } from 'ngx-toastr';
import { Feedback } from '../../models/feedback.model';
import { Router } from '@angular/router'; // Importer Router

@Component({
  selector: 'app-feedback-list',
  templateUrl: './feedback-list.component.html',
  styleUrls: ['./feedback-list.component.css']
})
export class FeedbackListComponent implements OnInit {
  feedbacks: Feedback[] = [];

  constructor(
    private feedbackService: FeedbackService,
    private toastr: ToastrService,
    private router: Router // Injecter Router
  ) {}

  ngOnInit(): void {
    this.getAllFeedbacks();
  }

  getAllFeedbacks() {
    this.feedbackService.getAllFeedbacks().subscribe({
      next: (response) => {
        this.feedbacks = response;
      },
      error: (error) => {
        this.toastr.error('Erreur de chargement des feedbacks', 'Erreur');
        console.error('Erreur lors de la récupération des feedbacks:', error);
      }
    });
  }

  deleteFeedback(id: number) {
    this.feedbackService.deleteFeedback(id).subscribe({
      next: () => {
        this.toastr.success('Feedback supprimé avec succès', 'Succès');
        this.feedbacks = this.feedbacks.filter(feedback => feedback.idFeedback !== id);
      },
      error: (error) => {
        this.toastr.error('Erreur de suppression', 'Erreur');
        console.error('Erreur lors de la suppression du feedback:', error);
      }
    });
  }

  editFeedback(feedback: Feedback) {
    // Naviguer vers add-feedback avec l'ID du feedback
    this.router.navigate(['/add-feedback', feedback.idFeedback]);
  }
}
