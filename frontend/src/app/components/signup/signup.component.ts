import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { passwordValidator } from './validators';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent {
  signUpForm = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: [
      '',
      [Validators.required, passwordValidator(), Validators.minLength(8)],
    ],
    name: ['', [Validators.required]],
    phoneNo: [
      '',
      [Validators.required, Validators.minLength(10), Validators.maxLength(10)],
    ],
  });

  private isBadCredentials = false;
  private userExists = false;
  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {}

  get email() {
    return this.signUpForm.controls['email'];
  }

  get password() {
    return this.signUpForm.controls['password'];
  }

  get phoneNo() {
    return this.signUpForm.controls['phoneNo'];
  }

  get name() {
    return this.signUpForm.controls['name'];
  }

  get badCredentials() {
    return this.isBadCredentials;
  }

  get userExist() {
    return this.userExists;
  }

  onSubmit() {
    const { email, password, name, phoneNo } = this.signUpForm.value;
    this.authService
      .signUp({
        email: email as string,
        password: password as string,
        name: name as string,
        phoneNumber: phoneNo as string,
      })
      .subscribe({
        next: (value) => {
          this.isBadCredentials = false;
          this.userExists = false;
          const confirmationRoute = `/confirm/${value.userSub}`;
          this.router.navigate([confirmationRoute]);
        },
        error: (err) => {
          if (err instanceof HttpErrorResponse) {
            if (err.error.status === 'UNAUTHORIZED') {
              this.isBadCredentials = true;
            }
            if (err.error.status === 'CONFLICT') {
              this.userExists = true;
            }
            // TODO : Add support of internal server error and a custom error page
          }
        },
      });
  }
}
