import { Component } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-logout-button',
  templateUrl: './logout-button.component.html',
  styleUrls: ['./logout-button.component.css'],
})
export class LogoutButtonComponent {
  constructor(private authService: AuthService) {}
  logOutUser() {
    this.authService.logout();
  }
}
