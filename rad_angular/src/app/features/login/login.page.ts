import { ChangeDetectionStrategy, Component, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth.service';
import { messageErreur } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';

@Component({
  selector: 'app-login',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, RouterLink, IconComponent],
  templateUrl: './login.page.html',
  styleUrl: './login.page.css',
})
export class LoginPage {
  private auth = inject(AuthService);
  private router = inject(Router);

  /** URL demandée avant la redirection vers la connexion (?retour=...). */
  readonly retour = input<string>();

  protected email = '';
  protected motDePasse = '';
  protected voirMotDePasse = signal(false);
  protected chargement = signal(false);
  protected erreur = signal<string | null>(null);

  // identifiants du jeu de démonstration (DataInitializer), affichés uniquement en développement
  protected readonly demo = environment.production
    ? []
    : [
        { role: 'Administrateur', email: 'admin@restaurant.com', mdp: 'admin123' },
        { role: 'Gérant', email: 'gerant@restaurant.com', mdp: 'gerant123' },
        { role: 'Employé', email: 'employe@restaurant.com', mdp: 'employe123' },
      ];

  remplir(email: string, mdp: string): void {
    this.email = email;
    this.motDePasse = mdp;
  }

  connexion(): void {
    if (!this.email || !this.motDePasse) {
      this.erreur.set('Renseignez votre email et votre mot de passe.');
      return;
    }
    this.chargement.set(true);
    this.erreur.set(null);
    this.auth.login(this.email, this.motDePasse).subscribe({
      next: () => {
        const cible = this.retour();
        this.router.navigateByUrl(cible && cible.startsWith('/') ? cible : '/dashboard');
      },
      error: (err) => {
        this.erreur.set(messageErreur(err));
        this.chargement.set(false);
      },
    });
  }
}
