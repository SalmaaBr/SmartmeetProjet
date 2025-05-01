import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { PublicationComment } from '../models/publication-comment.model';
import { PublicationLike } from '../models/publication-like.model';

@Injectable({
  providedIn: 'root'
})
export class PublicationInteractionService {
  private baseUrl = 'http://localhost:8082/InteractivePublication';
  // Flag to determine if we should use mock data (for development) or real API calls
  private useMockData = false; // Set to false to use real backend

  constructor(private http: HttpClient) {}

  // Helper to validate publication ID
  private validatePublicationId(publicationId: number | undefined | null): boolean {
    if (publicationId === undefined || publicationId === null) {
      console.error('Invalid publication ID:', publicationId);
      return false;
    }
    return true;
  }

  // =========================
  // REAL API IMPLEMENTATION
  // =========================

  // Comments related methods
  getCommentsByPublicationId(publicationId: number): Observable<PublicationComment[]> {
    if (!this.validatePublicationId(publicationId) || this.useMockData) {
      return this.getMockComments(publicationId || 0);
    }

    return this.http.get<PublicationComment[]>(`${this.baseUrl}/${publicationId}/comments`).pipe(
      catchError(error => {
        console.error('Error fetching comments:', error);
        // Fallback to mock data if API fails
        return this.getMockComments(publicationId);
      })
    );
  }

  addComment(comment: PublicationComment): Observable<PublicationComment> {
    if (!this.validatePublicationId(comment.publicationId) || this.useMockData) {
      return this.addMockComment(comment);
    }

    console.log('Sending comment to backend:', comment);
    return this.http.post<PublicationComment>(`${this.baseUrl}/comments`, comment).pipe(
      catchError(error => {
        console.error('Error adding comment:', error);
        // Fallback to mock data if API fails
        return this.addMockComment(comment);
      })
    );
  }

  updateComment(commentId: number, content: string): Observable<PublicationComment> {
    if (this.useMockData) return this.updateMockComment(commentId, content);

    return this.http.put<PublicationComment>(`${this.baseUrl}/comments/${commentId}`, { content }).pipe(
      catchError(error => {
        console.error('Error updating comment:', error);
        // Fallback to mock data if API fails
        return this.updateMockComment(commentId, content);
      })
    );
  }

  deleteComment(commentId: number): Observable<void> {
    if (this.useMockData) return this.deleteMockComment(commentId);

    return this.http.delete<void>(`${this.baseUrl}/comments/${commentId}`).pipe(
      catchError(error => {
        console.error('Error deleting comment:', error);
        // Fallback to mock data if API fails
        return this.deleteMockComment(commentId);
      })
    );
  }

  // Likes related methods
  getLikesByPublicationId(publicationId: number): Observable<PublicationLike[]> {
    if (this.useMockData) return of(this.getMockLikes(publicationId));

    return this.http.get<PublicationLike[]>(`${this.baseUrl}/${publicationId}/likes`).pipe(
      catchError(error => {
        console.error('Error fetching likes:', error);
        // Fallback to mock data if API fails
        return of(this.getMockLikes(publicationId));
      })
    );
  }

  getLikesCount(publicationId: number): Observable<number> {
    if (!this.validatePublicationId(publicationId) || this.useMockData) {
      return this.getMockLikesCount(publicationId || 0);
    }

    return this.http.get<number>(`${this.baseUrl}/${publicationId}/likes/count`).pipe(
      catchError(error => {
        console.error('Error fetching likes count:', error);
        // Fallback to mock data if API fails
        return this.getMockLikesCount(publicationId);
      })
    );
  }

  toggleLike(publicationId: number, userId: number): Observable<boolean> {
    if (!this.validatePublicationId(publicationId) || this.useMockData) {
      return this.toggleMockLike(publicationId || 0, userId);
    }

    return this.http.post<boolean>(`${this.baseUrl}/${publicationId}/likes/toggle`, { userId }).pipe(
      catchError(error => {
        console.error('Error toggling like:', error);
        // Fallback to mock data if API fails
        return this.toggleMockLike(publicationId, userId);
      })
    );
  }

  getUserHasLiked(publicationId: number, userId: number): Observable<boolean> {
    if (!this.validatePublicationId(publicationId) || this.useMockData) {
      return this.getUserHasLikedMock(publicationId || 0, userId);
    }

    return this.http.get<boolean>(`${this.baseUrl}/${publicationId}/likes/user/${userId}`).pipe(
      catchError(error => {
        console.error('Error checking if user liked publication:', error);
        // Fallback to mock data if API fails
        return this.getUserHasLikedMock(publicationId, userId);
      })
    );
  }

  // =========================
  // MOCK DATA IMPLEMENTATION
  // =========================

  // Mock data storage
  private mockComments: { [key: number]: PublicationComment[] } = {};
  private mockLikes: { [key: number]: { [key: number]: PublicationLike } } = {};

  // Mock methods for comments
  private getMockComments(publicationId: number): Observable<PublicationComment[]> {
    if (!this.mockComments[publicationId]) {
      this.mockComments[publicationId] = [];
    }
    return of(this.mockComments[publicationId]);
  }

  private addMockComment(comment: PublicationComment): Observable<PublicationComment> {
    const newComment = {
      ...comment,
      commentId: Date.now(),
      createdAt: new Date().toISOString(),
      username: 'Current User'
    };

    if (!this.mockComments[comment.publicationId]) {
      this.mockComments[comment.publicationId] = [];
    }

    this.mockComments[comment.publicationId].push(newComment);
    return of(newComment);
  }

  private updateMockComment(commentId: number, content: string): Observable<PublicationComment> {
    let updatedComment: PublicationComment | null = null;

    Object.keys(this.mockComments).forEach(pubId => {
      const comment = this.mockComments[+pubId].find(c => c.commentId === commentId);
      if (comment) {
        comment.content = content;
        comment.updatedAt = new Date().toISOString();
        updatedComment = comment;
      }
    });

    return of(updatedComment!);
  }

  private deleteMockComment(commentId: number): Observable<void> {
    Object.keys(this.mockComments).forEach(pubId => {
      this.mockComments[+pubId] = this.mockComments[+pubId].filter(c => c.commentId !== commentId);
    });

    return of(void 0);
  }

  // Mock methods for likes
  private getMockLikes(publicationId: number): PublicationLike[] {
    if (!this.mockLikes[publicationId]) {
      return [];
    }

    return Object.values(this.mockLikes[publicationId]);
  }

  private getMockLikesCount(publicationId: number): Observable<number> {
    if (!this.mockLikes[publicationId]) {
      return of(0);
    }

    return of(Object.keys(this.mockLikes[publicationId]).length);
  }

  private toggleMockLike(publicationId: number, userId: number): Observable<boolean> {
    if (!this.mockLikes[publicationId]) {
      this.mockLikes[publicationId] = {};
    }

    const hasLiked = !!this.mockLikes[publicationId][userId];

    if (hasLiked) {
      delete this.mockLikes[publicationId][userId];
      return of(false);
    } else {
      this.mockLikes[publicationId][userId] = {
        likeId: Date.now(),
        publicationId,
        userId,
        createdAt: new Date().toISOString()
      };
      return of(true);
    }
  }

  private getUserHasLikedMock(publicationId: number, userId: number): Observable<boolean> {
    if (!this.mockLikes[publicationId]) {
      return of(false);
    }

    return of(!!this.mockLikes[publicationId][userId]);
  }
}
