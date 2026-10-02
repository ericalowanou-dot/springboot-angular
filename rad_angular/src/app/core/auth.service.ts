import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, Role, Utilisateur } from './models';
import { stockage } from './stockage';

const CLE_SESSION = 'rad.session';

interface Session {
  token: string;
  expireLe: number;
  utilisateur: Utilisateur;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private session = signal<Session | null>(this.restaurer());

  readonly utilisateur = computed(() => this.session()?.utilisateur ?? null);
  readonly connecte = computed(() => this.session() !== null);
  readonly peutGerer = computed(() => this.aRole('ADMIN', 'GERANT'));
  readonly estAdmin = computed(() => this.aRole('ADMIN'));
  readonly estLivreur = computed(() => this.aRole('LIVREUR'));

  /** Page d'accueil selon le rôle : le livreur a son propre espace. */
  accueil(): string {
    return this.estLivreur() ? '/livreur' : '/dashboard';
  }

  get token(): string | null {
    return this.session()?.token ?? null;
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/api/auth/login`, { email, password })
      .pipe(
        tap((r) => {
          const session: Session = {
            token: r.token,
            expireLe: Date.now() + r.expireDans,
            utilisateur: r.utilisateur,
          };
          this.session.set(session);
          stockage.ecrire(CLE_SESSION, JSON.stringify(session));
        }),
      );
  }

  logout(redirection = true): void {
    this.session.set(null);
    stockage.supprimer(CLE_SESSION);
    if (redirection) {
      this.router.navigate(['/login']);
    }
  }

  aRole(...roles: Role[]): boolean {
    const u = this.session()?.utilisateur;
    return !!u && roles.includes(u.role);
  }

  private restaurer(): Session | null {
    try {
      const brut = stockage.lire(CLE_SESSION);
      if (!brut) return null;
      const session = JSON.parse(brut) as Session;
      return session.expireLe > Date.now() ? session : null;
    } catch {
      return null;
    }
  }
}
