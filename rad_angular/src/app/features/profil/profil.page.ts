import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { LibellePipe, initiales } from '../../core/format';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';

@Component({
  selector: 'app-profil',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, DatePipe, IconComponent, LibellePipe],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Mon profil</h1>
          <p>Vos informations et la sécurité de votre compte.</p>
        </div>
      </div>

      <div class="grid grid-2">
        @if (auth.utilisateur(); as u) {
          <section class="card card-pad identite">
            <span class="avatar xl">{{ initiales() }}</span>
            <h2>{{ u.prenom }} {{ u.nom }}</h2>
            <span class="badge primary plain">{{ u.role | libelle: 'role' }}</span>
            <dl class="kv">
              <dt>Email</dt>
              <dd>{{ u.email }}</dd>
              <dt>Téléphone</dt>
              <dd>{{ u.telephone || '—' }}</dd>
              <dt>Dernière connexion</dt>
              <dd>{{ u.derniereConnexion ? (u.derniereConnexion | date: 'd MMM y, HH:mm') : '—' }}</dd>
            </dl>
          </section>
        }

        <section class="card">
          <div class="card-head"><h2><app-icon name="key" [size]="16" /> Changer de mot de passe</h2></div>
          <form #ngf="ngForm" class="form card-body" (ngSubmit)="changer(ngf)">
            <div class="field">
              <label for="ancien">Mot de passe actuel</label>
              <input id="ancien" class="input" type="password" name="ancien" required autocomplete="current-password" [(ngModel)]="ancien" />
            </div>
            <div class="field">
              <label for="nouveau">Nouveau mot de passe</label>
              <input id="nouveau" class="input" type="password" name="nouveau" required minlength="6" autocomplete="new-password" [(ngModel)]="nouveau" />
            </div>
            <div class="field">
              <label for="confirmation">Confirmation</label>
              <input id="confirmation" class="input" type="password" name="confirmation" required autocomplete="new-password" [(ngModel)]="confirmation" />
            </div>
            <button class="btn btn-primary" type="submit" [disabled]="envoi()">Mettre à jour</button>
          </form>
        </section>
      </div>
    </div>
  `,
  styles: `
    .identite {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
      text-align: center;
    }
    .avatar.xl {
      width: 84px;
      height: 84px;
      font-size: 28px;
    }
    h2 {
      font-size: 20px;
      font-weight: 800;
    }
    .kv {
      width: 100%;
      margin-top: 12px;
      text-align: left;
    }
    .card-head h2 {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 15px;
    }
  `,
})
export class ProfilPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  protected auth = inject(AuthService);

  protected initiales = computed(() => initiales(this.auth.utilisateur()?.prenom, this.auth.utilisateur()?.nom));
  protected ancien = '';
  protected nouveau = '';
  protected confirmation = '';
  protected envoi = signal(false);

  changer(ngf: NgForm): void {
    if (ngf.invalid) {
      ngf.control.markAllAsTouched();
      this.toasts.erreur('Le nouveau mot de passe doit contenir au moins 6 caractères.');
      return;
    }
    if (this.nouveau !== this.confirmation) {
      this.toasts.erreur('La confirmation ne correspond pas au nouveau mot de passe.');
      return;
    }
    this.envoi.set(true);
    this.api.changerMotDePasse(this.ancien, this.nouveau).subscribe({
      next: () => {
        this.toasts.succes('Mot de passe modifié');
        ngf.resetForm();
        this.envoi.set(false);
      },
      error: (e) => {
        this.toasts.erreur(e);
        this.envoi.set(false);
      },
    });
  }
}
