import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Add the Authorization header with the ID token from AuthService
    const idToken = this.authService.authToken?.idToken;
    if (idToken) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${idToken}`,
        },
      });
    }

    return next.handle(request);
  }
}
