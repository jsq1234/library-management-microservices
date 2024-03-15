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

  private isBadCredentials = false;
  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {}

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

          const { accessToken, idToken, refreshToken } =
            value.authenticationResults;
          const { userId, email, phoneNumber, role } = value;
          this.authService.authToken = {
            accessToken,
            idToken,
            refreshToken,
          };

          this.authService.user = {
            userId,
            email,
            phoneNumber,
            role,
            name: 'random',
          };

          localStorage.setItem('user', JSON.stringify(this.authService.user));

          localStorage.setItem(
            'auth_tokens',
            JSON.stringify(this.authService.authToken)
          );

          this.router.navigate(['/home']);
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
