import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-container',
  templateUrl: './login-container.component.html',
  styleUrls: ['./login-container.component.css'],
})
export class LoginContainerComponent {
  constructor(private router: Router) {}
  toggleEmailForm() {
    this.router.navigate(['login', 'email']);
  }
  togglePhoneNoForm() {
    this.router.navigate(['login', 'phoneno']);
  }
}
