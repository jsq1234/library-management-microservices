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
import { ChangePassword } from '../interfaces/change-password';
import { InitiateAuthResponse } from '../interfaces/initiate-auth-response';
import { TotpCodeVerificationRequest } from '../interfaces/totp-code-verification-request';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authenticatedUser: User | null = null;
  public authToken: AuthToken | null = null;
  private baseUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient, private router: Router) {}

  public loginByEmail(
    requestBody: EmailLoginReq
  ): Observable<InitiateAuthResponse> {
    return this.http.post<InitiateAuthResponse>(
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
    return this.http.post<void>(`${this.baseUrl}/confirm_account`, requestBody);
  }

  public signUp(requestBody: SignUpRequest): Observable<SignUpResponse> {
    return this.http.post<SignUpResponse>(
      `${this.baseUrl}/signup`,
      requestBody
    );
  }

  public sendChangePasswordRequest(email: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/forgot_password_request`, {
      email,
    });
  }

  public changePassword(requestBody: ChangePassword): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/confirm_forgot_password`,
      requestBody
    );
  }

  public logout() {
    this.authenticatedUser = null;
    this.authToken = null;
    localStorage.removeItem('user');
    localStorage.removeItem('auth_tokens');
    this.router.navigate(['login']);
  }

  public verifySoftwareToken(requestBody: {
    code: string;
    accessToken: string;
  }): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/verify_software_token`,
      requestBody
    );
  }

  public verifyTotpCode(
    requestBody: TotpCodeVerificationRequest
  ): Observable<SignInResponse> {
    return this.http.post<SignInResponse>(
      `${this.baseUrl}/verify_totp_code`,
      requestBody
    );
  }

  get user(): User | null {
    return this.authenticatedUser;
  }

  set user(userInfo: User) {
    this.authenticatedUser = userInfo;
  }
}
