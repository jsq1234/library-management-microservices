import { Component, Input } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-qr-code-modal',
  templateUrl: './qr-code-modal.component.html',
  styleUrls: ['./qr-code-modal.component.css'],
})
export class QrCodeModalComponent {
  @Input() showModal: boolean = false;
  @Input() qrCodeUrl: string = '';

  @Input() accessToken: string = '';

  codeSubmition = this.formBuilder.group({
    code: ['', [Validators.required]],
  });

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    const { code } = this.codeSubmition.value;
    this.authService
      .verifySoftwareToken({
        code: code as string,
        accessToken: this.accessToken,
      })
      .subscribe({
        next: (_) => {
          this.router.navigate(['login']);
        },
        error: (err) => {
          console.log(err);
        },
      });
  }
}
