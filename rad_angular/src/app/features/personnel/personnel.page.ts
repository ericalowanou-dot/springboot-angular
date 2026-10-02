import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { ConfirmService } from '../../core/confirm.service';
import { FcfaPipe, LIBELLES, LibellePipe, initiales } from '../../core/format';
import { Personnel, Utilisateur } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

@Component({
  selector: 'app-personnel',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, DatePipe, IconComponent, ModalComponent, FcfaPipe, LibellePipe],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Personnel</h1>
          <p>{{ actifs() }} employé(s) actif(s) · masse salariale {{ masseSalariale() | fcfa }} / mois</p>
        </div>
        <button class="btn btn-primary" (click)="ouvrir()"><app-icon name="plus" [size]="16" /> Nouvel employé</button>
      </div>

      <div class="segmented mb">
        <button [class.active]="fonction() === null" (click)="fonction.set(null)">Tous</button>
        @for (f of fonctions; track f) {
          <button [class.active]="fonction() === f" (click)="fonction.set(f)">{{ f | libelle: 'fonction' }}</button>
        }
      </div>

      <div class="cards">
        @for (p of affiches(); track p.idPersonnel) {
          <article class="card card-pad employe" [class.inactif]="p.actif === false">
            <div class="row">
              <span class="avatar lg">{{ initiales(p.prenom, p.nom) }}</span>
              <div class="grow">
                <h3>{{ p.prenom }} {{ p.nom }}</h3>
                <span class="badge primary plain">{{ p.fonction | libelle: 'fonction' }}</span>
                @if (p.actif === false) {
                  <span class="badge plain">Inactif</span>
                }
              </div>
            </div>
            <dl class="kv small">
              <dt>Téléphone</dt>
              <dd>{{ p.telephone || '—' }}</dd>
              <dt>Salaire</dt>
              <dd>{{ p.salaire ? (p.salaire | fcfa) : '—' }}</dd>
              <dt>Embauché le</dt>
              <dd>{{ p.dateEmbauche ? (p.dateEmbauche | date: 'd MMM y') : '—' }}</dd>
            </dl>
            @if (p.fonction.toUpperCase() === 'LIVREUR') {
              <div class="acces">
                @if (acces()[p.idPersonnel!]; as compte) {
                  <div class="grow">
                    <span class="small muted">Accès application</span>
                    <div class="small strong ellipsis">{{ compte.email }}</div>
                  </div>
                  <span class="badge" [class.success]="compte.enabled" [class.danger]="!compte.enabled">{{ compte.enabled ? 'Actif' : 'Bloqué' }}</span>
                  <button class="btn btn-ghost btn-sm" (click)="ouvrirAcces(p)">Gérer</button>
                } @else {
                  <span class="small muted grow">Pas encore d'accès à l'application</span>
                  <button class="btn btn-secondary btn-sm" (click)="ouvrirAcces(p)"><app-icon name="key" [size]="14" /> Créer un accès</button>
                }
              </div>
            }
            <div class="row pied">
              <button class="btn btn-secondary btn-sm" (click)="ouvrir(p)"><app-icon name="pencil" [size]="14" /> Modifier</button>
              <button class="icon-btn danger" title="Supprimer" (click)="supprimer(p)"><app-icon name="trash" [size]="16" /></button>
            </div>
          </article>
        } @empty {
          @if (personnel() !== null) {
            <div class="empty card" style="grid-column: 1 / -1">
              <span class="empty-icon"><app-icon name="chef" [size]="26" /></span>
              <h3>Aucun employé</h3>
            </div>
          }
        }
      </div>
    </div>

    @if (formAcces(); as a) {
      <app-modal
        [titre]="a.existant ? 'Accès de ' + a.personnel.prenom : 'Créer un accès livreur'"
        [sousTitre]="a.personnel.prenom + ' ' + a.personnel.nom + ' pourra suivre ses livraisons depuis son téléphone.'"
        (fermer)="formAcces.set(null)"
      >
        <form #nga="ngForm" class="form" id="form-acces" (ngSubmit)="enregistrerAcces(nga)">
          <div class="field">
            <label for="a-email">Email de connexion</label>
            <input id="a-email" class="input" type="email" name="email" required email autocomplete="off" [(ngModel)]="a.email" />
          </div>
          <div class="field">
            <label for="a-mdp">{{ a.existant ? 'Nouveau mot de passe (laisser vide pour conserver)' : 'Mot de passe' }}</label>
            <input id="a-mdp" class="input" type="password" name="password" minlength="6" autocomplete="new-password" [required]="!a.existant" [(ngModel)]="a.password" />
            <span class="hint">6 caractères minimum. Communiquez-le au livreur de vive voix.</span>
          </div>
          @if (a.existant) {
            <label class="switch">
              <input type="checkbox" name="enabled" [(ngModel)]="a.enabled" /> Accès autorisé
            </label>
          }
        </form>
        <div pied class="pied-acces">
          @if (a.existant) {
            <button class="btn btn-ghost danger-text" (click)="supprimerAcces(a.personnel)"><app-icon name="trash" [size]="16" /> Retirer l'accès</button>
          }
          <span class="grow"></span>
          <button class="btn btn-secondary" (click)="formAcces.set(null)">Annuler</button>
          <button class="btn btn-primary" type="submit" form="form-acces">{{ a.existant ? 'Enregistrer' : 'Créer l’accès' }}</button>
        </div>
      </app-modal>
    }

    @if (form(); as f) {
      <app-modal [titre]="f.idPersonnel ? 'Modifier l’employé' : 'Nouvel employé'" (fermer)="form.set(null)">
        <form #ngf="ngForm" class="form" id="form-pers" (ngSubmit)="enregistrer(ngf)">
          <div class="form-row">
            <div class="field">
              <label for="prenom">Prénom</label>
              <input id="prenom" class="input" name="prenom" required maxlength="50" [(ngModel)]="f.prenom" />
            </div>
            <div class="field">
              <label for="nom">Nom</label>
              <input id="nom" class="input" name="nom" required maxlength="50" [(ngModel)]="f.nom" />
            </div>
          </div>
          <div class="form-row">
            <div class="field">
              <label for="fonction">Fonction</label>
              <select id="fonction" class="select" name="fonction" required [(ngModel)]="f.fonction">
                @for (fo of fonctions; track fo) {
                  <option [value]="fo">{{ fo | libelle: 'fonction' }}</option>
                }
              </select>
            </div>
            <div class="field">
              <label for="tel">Téléphone</label>
              <input id="tel" class="input" name="telephone" maxlength="30" [(ngModel)]="f.telephone" />
            </div>
          </div>
          <div class="form-row">
            <div class="field">
              <label for="salaire">Salaire mensuel (FCFA)</label>
              <input id="salaire" class="input" type="number" min="0" name="salaire" [(ngModel)]="f.salaire" />
            </div>
            <div class="field">
              <label for="date">Date d'embauche</label>
              <input id="date" class="input" type="date" name="dateEmbauche" [(ngModel)]="f.dateEmbauche" />
            </div>
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input id="email" class="input" type="email" name="email" email maxlength="100" [(ngModel)]="f.email" />
          </div>
          <label class="switch">
            <input type="checkbox" name="actif" [(ngModel)]="f.actif" /> Employé actif
          </label>
        </form>
        <div pied>
          <button class="btn btn-secondary" (click)="form.set(null)">Annuler</button>
          <button class="btn btn-primary" type="submit" form="form-pers">Enregistrer</button>
        </div>
      </app-modal>
    }
  `,
  styles: `
    .mb {
      margin-bottom: 20px;
    }
    .employe {
      display: flex;
      flex-direction: column;
      gap: 14px;
    }
    .employe.inactif {
      opacity: 0.6;
    }
    h3 {
      font-size: 15.5px;
      font-weight: 700;
      margin-bottom: 4px;
    }
    .grow {
      flex: 1;
      min-width: 0;
    }
    .kv {
      margin: 0;
    }
    .acces {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 12px;
      border-radius: 12px;
      background: var(--surface-2);
      border: 1px dashed var(--border-strong);
    }
    .ellipsis {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    .pied-acces {
      display: flex;
      align-items: center;
      gap: 8px;
      width: 100%;
    }
    .danger-text {
      color: var(--danger);
    }
    .pied {
      justify-content: space-between;
      border-top: 1px solid var(--border);
      padding-top: 12px;
    }
  `,
})
export class PersonnelPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);

  protected initiales = initiales;
  protected fonctions = Object.keys(LIBELLES['fonction']);
  protected personnel = signal<Personnel[] | null>(null);
  protected fonction = signal<string | null>(null);
  protected form = signal<Personnel | null>(null);
  protected acces = signal<Record<number, Utilisateur>>({});
  protected formAcces = signal<{
    personnel: Personnel;
    existant: boolean;
    email: string;
    password: string;
    enabled: boolean;
  } | null>(null);

  protected affiches = computed(() =>
    (this.personnel() ?? []).filter((p) => !this.fonction() || p.fonction?.toUpperCase() === this.fonction()),
  );
  protected actifs = computed(() => (this.personnel() ?? []).filter((p) => p.actif !== false).length);
  protected masseSalariale = computed(() =>
    (this.personnel() ?? []).filter((p) => p.actif !== false).reduce((s, p) => s + (p.salaire ?? 0), 0),
  );

  constructor() {
    this.charger();
  }

  charger(): void {
    forkJoin({ personnel: this.api.personnel.liste(), acces: this.api.accesLivreur.tous() }).subscribe({
      next: (r) => {
        this.personnel.set(r.personnel);
        this.acces.set(r.acces);
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  ouvrirAcces(p: Personnel): void {
    const compte = this.acces()[p.idPersonnel!];
    this.formAcces.set({
      personnel: p,
      existant: !!compte,
      email: compte?.email ?? p.email ?? '',
      password: '',
      enabled: compte?.enabled ?? true,
    });
  }

  enregistrerAcces(nga: NgForm): void {
    const a = this.formAcces();
    if (!a || nga.invalid) {
      nga.control.markAllAsTouched();
      this.toasts.erreur('Renseignez un email valide et un mot de passe d’au moins 6 caractères.');
      return;
    }
    const donnees = { email: a.email.trim(), password: a.password || null, enabled: a.enabled };
    const id = a.personnel.idPersonnel!;
    const appel = a.existant ? this.api.accesLivreur.modifier(id, donnees) : this.api.accesLivreur.creer(id, donnees);
    appel.subscribe({
      next: (compte) => {
        this.acces.update((m) => ({ ...m, [id]: compte }));
        this.formAcces.set(null);
        this.toasts.succes(a.existant ? 'Accès mis à jour' : `Accès créé : ${a.personnel.prenom} peut se connecter`);
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  async supprimerAcces(p: Personnel): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Retirer l'accès de ${p.prenom} ?`,
      message: 'Son compte de connexion sera supprimé. Sa fiche et ses livraisons sont conservées.',
      libelle: 'Retirer l’accès',
      danger: true,
    });
    if (!ok) return;
    const id = p.idPersonnel!;
    this.api.accesLivreur.supprimer(id).subscribe({
      next: () => {
        this.acces.update((m) => {
          const copie = { ...m };
          delete copie[id];
          return copie;
        });
        this.formAcces.set(null);
        this.toasts.succes('Accès retiré');
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  ouvrir(p?: Personnel): void {
    this.form.set(
      p
        ? { ...p, fonction: p.fonction?.toUpperCase(), actif: p.actif !== false }
        : { nom: '', prenom: '', fonction: 'SERVEUR', telephone: '', email: '', salaire: null, dateEmbauche: null, actif: true },
    );
  }

  enregistrer(ngf: NgForm): void {
    const f = this.form();
    if (!f || ngf.invalid) {
      ngf.control.markAllAsTouched();
      return;
    }
    const donnees = { ...f, email: f.email || null, dateEmbauche: f.dateEmbauche || null };
    const appel = f.idPersonnel ? this.api.personnel.modifier(f.idPersonnel, donnees) : this.api.personnel.creer(donnees);
    appel.subscribe({
      next: () => {
        this.toasts.succes(f.idPersonnel ? 'Employé mis à jour' : 'Employé ajouté');
        this.form.set(null);
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  async supprimer(p: Personnel): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Supprimer ${p.prenom} ${p.nom} ?`,
      message: 'Son éventuel accès à l’application sera aussi supprimé. S’il a effectué des livraisons, désactivez-le plutôt.',
      libelle: 'Supprimer',
      danger: true,
    });
    if (!ok) return;
    this.api.personnel.supprimer(p.idPersonnel!).subscribe({
      next: () => {
        this.toasts.succes('Employé supprimé');
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }
}
