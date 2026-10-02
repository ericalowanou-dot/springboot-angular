import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ConfirmService } from '../../core/confirm.service';
import { LibellePipe, initiales } from '../../core/format';
import { Role, Utilisateur, UtilisateurRequest } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

type FormUtilisateur = UtilisateurRequest & { id?: number };

@Component({
  selector: 'app-utilisateurs',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, DatePipe, IconComponent, ModalComponent, LibellePipe],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Utilisateurs</h1>
          <p>Comptes ayant accès à l'application et leurs droits.</p>
        </div>
        <button class="btn btn-primary" (click)="ouvrir()"><app-icon name="plus" [size]="16" /> Nouveau compte</button>
      </div>

      <div class="grid grid-4 roles">
        @for (r of roles; track r.id) {
          <div class="card card-pad">
            <div class="row"><span class="badge {{ r.ton }} plain">{{ r.id | libelle: 'role' }}</span></div>
            <p class="small muted">{{ r.description }}</p>
          </div>
        }
      </div>

      <div class="card mt">
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th>Utilisateur</th>
                <th>Rôle</th>
                <th class="hide-sm">Dernière connexion</th>
                <th>État</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              @for (u of utilisateurs() ?? []; track u.id) {
                <tr>
                  <td>
                    <div class="row">
                      <span class="avatar">{{ initiales(u.prenom, u.nom) }}</span>
                      <div>
                        <div class="strong">{{ u.prenom }} {{ u.nom }} @if (u.id === auth.utilisateur()?.id) { <span class="small muted">(vous)</span> }</div>
                        <div class="small muted">{{ u.email }}</div>
                      </div>
                    </div>
                  </td>
                  <td><span class="badge {{ tonRole(u.role) }} plain">{{ u.role | libelle: 'role' }}</span></td>
                  <td class="hide-sm muted">{{ u.derniereConnexion ? (u.derniereConnexion | date: 'd MMM y, HH:mm') : 'Jamais' }}</td>
                  <td>
                    <span class="badge" [class.success]="u.enabled" [class.danger]="!u.enabled">{{ u.enabled ? 'Actif' : 'Désactivé' }}</span>
                  </td>
                  <td class="actions-cell">
                    <button class="icon-btn" title="Modifier" (click)="ouvrir(u)"><app-icon name="pencil" [size]="16" /></button>
                    @if (u.id !== auth.utilisateur()?.id) {
                      <button class="icon-btn danger" title="Supprimer" (click)="supprimer(u)"><app-icon name="trash" [size]="16" /></button>
                    }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>

    @if (form(); as f) {
      <app-modal [titre]="f.id ? 'Modifier le compte' : 'Nouveau compte'" (fermer)="form.set(null)">
        <form #ngf="ngForm" class="form" id="form-user" (ngSubmit)="enregistrer(ngf)">
          <div class="form-row">
            <div class="field">
              <label for="prenom">Prénom</label>
              <input id="prenom" class="input" name="prenom" required [(ngModel)]="f.prenom" />
            </div>
            <div class="field">
              <label for="nom">Nom</label>
              <input id="nom" class="input" name="nom" required [(ngModel)]="f.nom" />
            </div>
          </div>
          <div class="form-row">
            <div class="field">
              <label for="email">Email de connexion</label>
              <input id="email" class="input" type="email" name="email" required email [(ngModel)]="f.email" />
            </div>
            <div class="field">
              <label for="role">Rôle</label>
              @if (f.role === 'LIVREUR') {
                <input id="role" class="input" value="Livreur" disabled />
                <span class="hint">Les accès livreur se gèrent depuis la page Personnel.</span>
              } @else {
                <select id="role" class="select" name="role" [(ngModel)]="f.role">
                  @for (r of rolesAttribuables; track r.id) {
                    <option [value]="r.id">{{ r.id | libelle: 'role' }}</option>
                  }
                </select>
              }
            </div>
          </div>
          <div class="field">
            <label for="mdp">{{ f.id ? 'Nouveau mot de passe (laisser vide pour conserver)' : 'Mot de passe' }}</label>
            <input id="mdp" class="input" type="password" name="password" minlength="6" autocomplete="new-password" [required]="!f.id" [(ngModel)]="f.password" />
            <span class="hint">6 caractères minimum.</span>
          </div>
          <label class="switch">
            <input type="checkbox" name="enabled" [(ngModel)]="f.enabled" /> Compte actif
          </label>
        </form>
        <div pied>
          <button class="btn btn-secondary" (click)="form.set(null)">Annuler</button>
          <button class="btn btn-primary" type="submit" form="form-user">Enregistrer</button>
        </div>
      </app-modal>
    }
  `,
  styles: `
    .roles p {
      margin: 8px 0 0;
    }
  `,
})
export class UtilisateursPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);
  protected auth = inject(AuthService);

  protected initiales = initiales;
  protected roles: { id: Role; ton: string; description: string }[] = [
    { id: 'ADMIN', ton: 'danger', description: 'Accès complet, y compris la gestion des comptes utilisateurs.' },
    { id: 'GERANT', ton: 'primary', description: 'Gère la carte, les stocks, le personnel et peut supprimer des données.' },
    { id: 'EMPLOYE', ton: 'info', description: 'Prend les commandes, encaisse, gère les clients et les livraisons.' },
    { id: 'LIVREUR', ton: 'violet', description: 'Voit uniquement ses livraisons : « Je pars », « Livrée » ou « Échec ». Accès créé depuis la page Personnel.' },
  ];
  protected rolesAttribuables = this.roles.filter((r) => r.id !== 'LIVREUR');

  protected utilisateurs = signal<Utilisateur[] | null>(null);
  protected form = signal<FormUtilisateur | null>(null);

  constructor() {
    this.charger();
  }

  tonRole(role: Role): string {
    return this.roles.find((r) => r.id === role)?.ton ?? '';
  }

  charger(): void {
    this.api.utilisateurs.liste().subscribe({
      next: (l) => this.utilisateurs.set(l),
      error: (e) => this.toasts.erreur(e),
    });
  }

  ouvrir(u?: Utilisateur): void {
    this.form.set(
      u
        ? { id: u.id, email: u.email, nom: u.nom, prenom: u.prenom, telephone: u.telephone, role: u.role, enabled: u.enabled, password: '' }
        : { email: '', nom: '', prenom: '', role: 'EMPLOYE', enabled: true, password: '' },
    );
  }

  enregistrer(ngf: NgForm): void {
    const f = this.form();
    if (!f || ngf.invalid) {
      ngf.control.markAllAsTouched();
      return;
    }
    const { id, ...requete } = f;
    const donnees = { ...requete, password: requete.password || null };
    const appel = id ? this.api.utilisateurs.modifier(id, donnees) : this.api.utilisateurs.creer(donnees);
    appel.subscribe({
      next: () => {
        this.toasts.succes(id ? 'Compte mis à jour' : 'Compte créé');
        this.form.set(null);
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  async supprimer(u: Utilisateur): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Supprimer le compte de ${u.prenom} ${u.nom} ?`,
      message: 'Cette personne ne pourra plus se connecter.',
      libelle: 'Supprimer',
      danger: true,
    });
    if (!ok) return;
    this.api.utilisateurs.supprimer(u.id).subscribe({
      next: () => {
        this.toasts.succes('Compte supprimé');
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }
}
