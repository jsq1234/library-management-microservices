import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const userGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  if (authService.user) {
    const router = inject(Router);
    router.navigate(['home']);
    return false;
  }
  return true;
};
