import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface ModerationResult {
  success: boolean;
  passes: boolean;
  message?: string;
  checks?: {
    badWords: boolean;
    illegalContent: boolean;
    badPublicity: boolean;
  };
  badWordsMessage?: string;
  illegalContentMessage?: string;
  badPublicityMessage?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ContentModerationService {
  private baseUrl = 'http://localhost:8082/InteractivePublication/moderation';

  constructor(private http: HttpClient) { }

  /**
   * Checks if content passes moderation
   * @param content The text content to check
   * @returns Observable with moderation results
   */
  checkContent(content: string): Observable<ModerationResult> {
    if (!content || content.trim() === '') {
      return of({ success: true, passes: true });
    }

    return this.http.post<ModerationResult>(`${this.baseUrl}/check`, { content })
      .pipe(
        catchError(error => {
          console.error('Error checking content moderation:', error);
          return of({
            success: false,
            passes: false,
            message: 'Error checking content moderation'
          });
        })
      );
  }
}
