import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable,of,throwError } from 'rxjs';
import { map,catchError } from 'rxjs/operators'; 
import { Document,DocumentLike } from '../models/document';
import { TypeDocument, TypeDocumentVisibility, TypeAccessLevelDocument, TypeDocumentTheme } from 'src/app/models/document.enum';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private apiUrl = 'http://localhost:8082/Document'; 
  private baseUrl = 'http://localhost:8082';
  private authUrl = 'http://localhost:8082/api/auth'; // Add auth URL for user endpoint

  constructor(private http: HttpClient,
    private authService: AuthService,
    private router: Router

  ) {}

  // Helper method to get headers with authentication token
  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    });
}

likeDocument(documentId: number): Observable<Document> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });
  return this.http.post<Document>(`${this.apiUrl}/like/${documentId}`, null, { headers }).pipe(
    catchError(this.handleError)
  );
}
private handleError(error: any): Observable<never> {
  console.error('An error occurred:', error);
  return throwError(() => error);
}
  // Handle token expiration
  private handleTokenExpiration() {
    localStorage.removeItem('auth_token');
    this.router.navigate(['/login']); // Redirige vers la page de login
  }
// Get current user's details (including userId)
getCurrentUser(): Observable<any> {
  return this.http.get<any>(`${this.authUrl}/me`, { headers: this.getHeaders() }).pipe(
    catchError(err => {
      if (err.status === 401) {
        this.handleTokenExpiration();
      }
      return throwError(() => new Error('Error fetching user details: ' + err.message));
    })
  );
}
  // Get all documents
  getDocuments(): Observable<Document[]> {
    return this.http.get<Document[]>(`${this.apiUrl}/ReadAllDocuments`, { headers: this.getHeaders() }).pipe(
      map(documents => documents),
      catchError(err => {
        if (err.status === 401) { 
          this.handleTokenExpiration();
        }
        return throwError(() => new Error('Error fetching documents: ' + err.message));
      })
    );
  }

  // Get a document by ID
  getDocument(id: number): Observable<Document> {
    return this.http.get<Document>(`${this.apiUrl}/ReadDocumentByID/${id}`, { headers: this.getHeaders() }).pipe(
      
      catchError(err => {
        if (err.status === 401) {
          this.handleTokenExpiration();
        }
        return throwError(() => new Error('Error fetching document: ' + err.message));
      })
    );
  }

  // Add a document with optional file upload
  addDocument(document: Document, file?: File): Observable<Document> {
    if (file) {
      const formData = new FormData();
      formData.append('document', JSON.stringify(document));
      formData.append('image', file);
      return this.http.post<Document>(`${this.apiUrl}/AddDocument`, formData, { 
        headers: this.getHeaders() 
      }).pipe(
        
        catchError(err => {
          if (err.status === 401) {
            this.handleTokenExpiration();
          }
          return throwError(() => new Error('Error adding document with file: ' + err.message));
        })
      );
    }
    return this.http.post<Document>(`${this.apiUrl}/AddDocument`, document, { 
      headers: this.getHeaders() 
    }).pipe(
      
      catchError(err => {
        if (err.status === 401) {
          this.handleTokenExpiration();
        }
        return throwError(() => new Error('Error adding document: ' + err.message));
      })
    );
  }

  // Update a document with optional file upload
  updateDocument(id: number, document: Document, file?: File): Observable<Document> {
    if (file) {
      const formData = new FormData();
      formData.append('document', JSON.stringify(document));
      formData.append('image', file);
      return this.http.put<Document>(`${this.apiUrl}/UpdateDocumentByID/${id}`, formData, { 
        headers: this.getHeaders() 
      }).pipe(
       
        catchError(err => {
          if (err.status === 401) {
            this.handleTokenExpiration();
          }
          return throwError(() => new Error('Error updating document with file: ' + err.message));
        })
      );
    }
    return this.http.put<Document>(`${this.apiUrl}/UpdateDocumentByID/${id}`, document, { 
      headers: this.getHeaders() 
    }).pipe(
      
      catchError(err => {
        if (err.status === 401) {
          this.handleTokenExpiration();
        }
        return throwError(() => new Error('Error updating document: ' + err.message));
      })
    );
  }

  // Delete a document
  deleteDocument(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/DeletDocumentByID/${id}`, { headers: this.getHeaders() }).pipe(
      catchError(err => {
        if (err.status === 401) {
          this.handleTokenExpiration();
        }
        return throwError(() => new Error('Error deleting document: ' + err.message));
      })
    );
  }

  // Download a document as a Blob
 /* downloadDocument(id: number): Observable<Blob> {
    const headers = this.getHeaders().set('Accept', 'text/plain');
    console.log('Downloading from:', `${this.apiUrl}/Download?id=${id}`);
    // Journalisation des en-têtes sans toJSON
    console.log('Headers:', headers.keys().reduce((acc, key) => {
      acc[key] = headers.get(key);
      return acc;
    }, {} as { [key: string]: string | null }));
    return this.http.get(`${this.apiUrl}/Download`, { 
      headers: headers, 
      responseType: 'blob',
      params: { id: id.toString() } 
    }).pipe(
      catchError(err => {
        console.error('Download error:', {
          status: err.status,
          statusText: err.statusText,
          message: err.message,
          error: err.error
        });
        if (err.status === 401) {
          this.handleTokenExpiration();
        }
        return throwError(() => new Error(`Error downloading document: ${err.status} ${err.statusText}`));
      })
    );

  }*/
 

  /*likeDocument(id: number): Observable<Document> {
    const headers = this.getHeaders();
    console.log('Liking document with ID:', id);
    return this.http.post<Document>(`${this.apiUrl}/like/${id}`, {}, { headers }).pipe(
      catchError(err => {
        console.error('Like error:', {
          status: err.status,
          statusText: err.statusText,
          message: err.message,
          error: err.error
        });
        if (err.status === 401) {
          this.handleTokenExpiration();
        }
        return throwError(() => new Error(`Error liking document: ${err.status} ${err.statusText}`));
      })
    );
  }*/


}