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
  return auth.connecte() ? inject(Router).parseUrl(auth.accueil()) : true;
};

/** Espace de gestion réservé à l'équipe du restaurant : le livreur est renvoyé vers son espace. */
export const equipeGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.estLivreur() ? inject(Router).parseUrl('/livreur') : true;
};

export const roleGuard =
  (...roles: Role[]): CanActivateFn =>
  () => {
    const auth = inject(AuthService);
    return auth.aRole(...roles) ? true : inject(Router).createUrlTree(['/dashboard']);
  };
