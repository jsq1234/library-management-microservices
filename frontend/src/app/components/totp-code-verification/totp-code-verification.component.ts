import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-totp-code-verification',
  templateUrl: './totp-code-verification.component.html',
  styleUrls: ['./totp-code-verification.component.css'],
})
export class TotpCodeVerificationComponent implements OnInit {
  private session: string = '';
  private email: string = '';
  public isInvalidCode = false;

  codeSubmition = this.formBuilder.group({
    code: ['', [Validators.required]],
  });

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe({
      next: (params) => {
        this.session = params['session'];
        this.email = params['email'];
      },
    });
  }

  onSubmit() {
    const { code } = this.codeSubmition.value;

    this.authService
      .verifyTotpCode({
        code: code as string,
        session: this.session,
        email: this.email,
      })
      .subscribe({
        next: (value) => {
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
          console.log(err);
          this.isInvalidCode = true;
        },
      });
  }
}
