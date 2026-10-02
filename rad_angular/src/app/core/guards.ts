import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Role } from './models';

export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  return auth.connecte()
    ? true
    : inject(Router).createUrlTree(['/login'], { queryParams: { retour: state.url } });
};

export const inviteGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.connecte() ? inject(Router).createUrlTree(['/dashboard']) : true;
};

export const roleGuard =
  (...roles: Role[]): CanActivateFn =>
  () => {
    const auth = inject(AuthService);
    return auth.aRole(...roles) ? true : inject(Router).createUrlTree(['/dashboard']);
  };
