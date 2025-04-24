import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Get the token from the auth service
    const token = this.authService.getToken();

    // If there's a token, add it to the request headers
    if (token) {
      request = this.addToken(request, token);
    }

    // Handle the request and catch any errors
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        // If the error is 401 (Unauthorized) and we're not already refreshing
        if (error.status === 401) {
          if (this.isRefreshing) {
            return this.refreshTokenSubject.pipe(
              filter(token => token != null),
              take(1),
              switchMap(() => next.handle(this.addToken(request, this.authService.getToken()!)))
            );
          } else {
            this.isRefreshing = true;
            this.refreshTokenSubject.next(null);

            // Try to refresh the token
            return this.authService.refreshToken().pipe(
              switchMap((newToken: any) => {
                this.isRefreshing = false;
                this.refreshTokenSubject.next(newToken.token);
                // Retry the original request with the new token
                return next.handle(this.addToken(request, newToken.token));
              }),
              catchError((refreshError) => {
                this.isRefreshing = false;
                // If refresh fails, logout and redirect to login
                this.authService.logout();
                this.router.navigate(['/login']);
                return throwError(() => refreshError);
              })
            );
          }
        }

        // For other errors, just throw them
        return throwError(() => error);
      })
    );
  }

  private addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
}
