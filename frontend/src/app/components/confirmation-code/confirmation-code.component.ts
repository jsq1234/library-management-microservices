import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-confirmation-code',
  templateUrl: './confirmation-code.component.html',
  styleUrls: ['./confirmation-code.component.css'],
})
export class ConfirmationCodeComponent implements OnInit {
  codeSubmition = this.formBuilder.group({
    code: ['', [Validators.required]],
  });

  public isInvalidCode = false;
  public userId: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.userId = params.get('userId');
    });
  }

  onSubmit() {
    const { code } = this.codeSubmition.value;
    this.authService
      .confirmAccount({
        code: code as string,
        userId: this.userId as string,
      })
      .subscribe({
        next: (_) => {
          this.isInvalidCode = false;
          console.log(`User account[${this.userId}] confirmed!`);
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.isInvalidCode = true;
          console.log(err);
        },
      });
  }
}
