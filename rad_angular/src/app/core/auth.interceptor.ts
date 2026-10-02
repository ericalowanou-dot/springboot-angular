import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';
import { ToastService } from './toast.service';

/** Ajoute le jeton JWT aux appels de l'API et déconnecte l'utilisateur si le jeton est refusé. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const toasts = inject(ToastService);
  const versApi = req.url.startsWith(environment.apiUrl);
  const token = auth.token;

  const requete = versApi && token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(requete).pipe(
    catchError((err: HttpErrorResponse) => {
      if (versApi && err.status === 401 && token) {
        toasts.erreur('Votre session a expiré, veuillez vous reconnecter.');
        auth.logout();
      }
      return throwError(() => err);
    }),
  );
};
