import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-login-phoneno',
  templateUrl: './login-phoneno.component.html',
  styleUrls: ['./login-phoneno.component.css'],
})
export class LoginPhonenoComponent {
  loginForm = this.formBuilder.group({
    phoneNo: [
      '',
      [Validators.required, Validators.minLength(10), Validators.maxLength(10)],
    ],
    password: ['', [Validators.required]],
  });

  private isBadCredentials = false;
  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {}

  get phoneNo() {
    return this.loginForm.controls['phoneNo'];
  }

  get password() {
    return this.loginForm.controls['password'];
  }

  get badCredentials() {
    return this.isBadCredentials;
  }

  onSubmit() {
    const { phoneNo, password } = this.loginForm.value;
    this.authService
      .loginByPhoneNo({
        phoneNumber: phoneNo as string,
        password: password as string,
      })
      .subscribe({
        next: (value) => {
          this.isBadCredentials = false;
          this.authService.authToken = {
            ...value.authenticationResults,
          };
          this.authService.user = {
            ...value,
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
