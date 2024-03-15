import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { passwordValidator } from '../signup/validators';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  codeSubmition = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, passwordValidator()]],
    code: ['', [Validators.required]],
  });

  public passwordChangeRequestSent = false;
  public isInvalidCode = false;
  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  get email() {
    return this.codeSubmition.controls['email'];
  }

  get password() {
    return this.codeSubmition.controls['password'];
  }

  get code() {
    return this.codeSubmition.controls['code'];
  }

  sendConfirmationCodeRequest() {
    const { email } = this.codeSubmition.value;
    this.authService.sendChangePasswordRequest(email as string).subscribe({
      next: (value) => {
        console.log('Confirmation code sent!');
        this.passwordChangeRequestSent = true;
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  sendChangePasswordRequest() {
    const { email, password, code } = this.codeSubmition.value;
    // console.log(email);
    // console.log(password);
    // console.log(code);
    console.log('Sending password change request');
    this.authService
      .changePassword({
        email: email as string,
        password: password as string,
        code: code as string,
      })
      .subscribe({
        next: (value) => {
          console.log('Password successfully changed!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.log(err);
          if (err instanceof HttpErrorResponse) {
            if (err.error.status === 'UNAUTHORIZED') {
              this.isInvalidCode = true;
            }
          }
        },
      });
  }
}
