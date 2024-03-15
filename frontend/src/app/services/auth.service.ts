import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SignUpRequest } from '../interfaces/signup-request';
import { Router } from '@angular/router';
import { EmailLoginReq } from '../interfaces/email-login-req';
import { PhonenoLoginReq } from '../interfaces/phoneno-login-req';
import { ConfirmRequest } from '../interfaces/confirm-request';
import { SignInResponse } from '../interfaces/signin-response';
import { User } from '../interfaces/user';
import { AuthToken } from '../interfaces/auth-token';
import { SignUpResponse } from '../interfaces/signup-response';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authenticatedUser: User | null = null;
  public authToken: AuthToken | null = null;
  private baseUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient, private router: Router) {}

  public loginByEmail(requestBody: EmailLoginReq): Observable<SignInResponse> {
    return this.http.post<SignInResponse>(
      `${this.baseUrl}/signin`,
      requestBody
    );
  }

  public loginByPhoneNo(
    requestBody: PhonenoLoginReq
  ): Observable<SignInResponse> {
    return this.http.post<SignInResponse>(
      `${this.baseUrl}/signin`,
      requestBody
    );
  }

  public confirmAccount(requestBody: ConfirmRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/confirm`, requestBody);
  }

  public signUp(requestBody: SignUpRequest): Observable<SignUpResponse> {
    return this.http.post<SignUpResponse>(
      `${this.baseUrl}/signup`,
      requestBody
    );
  }

  public logout() {
    this.authenticatedUser = null;
    localStorage.removeItem('user');
    this.router.navigate(['login']);
  }

  get user(): User | null {
    return this.authenticatedUser;
  }

  set user(userInfo: User) {
    this.authenticatedUser = userInfo;
  }
}
