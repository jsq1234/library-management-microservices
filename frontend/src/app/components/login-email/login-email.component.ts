import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-login-email',
  templateUrl: './login-email.component.html',
  styleUrls: ['./login-email.component.css'],
})
export class LoginEmailComponent {
  loginForm = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  codeConfirmationForm = this.formBuilder.group({
    code: ['', [Validators.required]],
  });

  private isBadCredentials = false;
  public isModalVisible = false;
  public accessToken = '';
  public qrCodeUrl = '';
  public showTotpCodeConfirmationForm = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {}

  openModal(mfaTokens: any) {
    this.qrCodeUrl = mfaTokens.secretCodeUrl;
    this.accessToken = mfaTokens.accessToken;
    this.isModalVisible = true;
  }

  get email() {
    return this.loginForm.controls['email'];
  }

  get password() {
    return this.loginForm.controls['password'];
  }

  get badCredentials() {
    return this.isBadCredentials;
  }

  onSubmit() {
    const { email, password } = this.loginForm.value;
    this.authService
      .loginByEmail({
        email: email as string,
        password: password as string,
      })
      .subscribe({
        next: (value) => {
          this.isBadCredentials = false;
          console.log(value);
          // user is signing in for the first time
          if (!value.challengeName) {
            this.openModal(value.mfaTokens);
          }

          // user has signed up and setted mfa authentication
          if (value.challengeName === 'SOFTWARE_TOKEN_MFA') {
            this.router.navigate(['/verify_totp_code'], {
              queryParams: {
                session: value.session,
                email: email as string,
              },
            });
          }
        },
        error: (err) => {
          if (err instanceof HttpErrorResponse) {
            if (err.error.status === 'UNAUTHORIZED') {
              this.isBadCredentials = true;
            }
            // TODO : Add support of internal server error and a custom error page
          }
        },
      });
  }
}
