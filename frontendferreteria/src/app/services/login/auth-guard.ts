import { inject } from '@angular/core';
import { CanActivateFn, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../auth.service';

export const AuthGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }
  const expectedRole = route.data['role'];
  const currentUser = authService.getCurrentUser();
  if (expectedRole && currentUser?.rol !== expectedRole) {
    router.navigate(['/app']);
    return false;
  }
  return true;
};
