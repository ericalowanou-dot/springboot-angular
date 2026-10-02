import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { ConfirmService } from '../../core/confirm.service';
import { initiales } from '../../core/format';
import { Fournisseur } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

@Component({
  selector: 'app-fournisseurs',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, IconComponent, ModalComponent],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Fournisseurs</h1>
          <p>Vos partenaires pour les achats de matières premières.</p>
        </div>
        <button class="btn btn-primary" (click)="ouvrir()"><app-icon name="plus" [size]="16" /> Nouveau fournisseur</button>
      </div>

      <div class="cards">
        @for (f of fournisseurs() ?? []; track f.idFournisseur) {
          <article class="card card-pad fournisseur">
            <div class="row">
              <span class="avatar lg">{{ initiales(f.nom, f.nom.split(' ')[1]) }}</span>
              <div class="grow">
                <h3>{{ f.nom }}</h3>
                @if (f.contact) {
                  <span class="small muted">Contact : {{ f.contact }}</span>
                }
              </div>
            </div>
            <ul>
              @if (f.telephone) {
                <li><app-icon name="phone" [size]="15" /> {{ f.telephone }}</li>
              }
              @if (f.email) {
                <li><app-icon name="mail" [size]="15" /> {{ f.email }}</li>
              }
              @if (f.adresse) {
                <li><app-icon name="pin" [size]="15" /> {{ f.adresse }}</li>
              }
            </ul>
            <div class="row pied">
              <button class="btn btn-secondary btn-sm" (click)="ouvrir(f)"><app-icon name="pencil" [size]="14" /> Modifier</button>
              <button class="icon-btn danger" title="Supprimer" (click)="supprimer(f)"><app-icon name="trash" [size]="16" /></button>
            </div>
          </article>
        } @empty {
          @if (fournisseurs() !== null) {
            <div class="empty card" style="grid-column: 1 / -1">
              <span class="empty-icon"><app-icon name="store" [size]="26" /></span>
              <h3>Aucun fournisseur</h3>
            </div>
          }
        }
      </div>
    </div>

    @if (form(); as f) {
      <app-modal [titre]="f.idFournisseur ? 'Modifier le fournisseur' : 'Nouveau fournisseur'" (fermer)="form.set(null)">
        <form #ngf="ngForm" class="form" id="form-fourn" (ngSubmit)="enregistrer(ngf)">
          <div class="form-row">
            <div class="field">
              <label for="nom">Raison sociale</label>
              <input id="nom" class="input" name="nom" required maxlength="80" [(ngModel)]="f.nom" />
            </div>
            <div class="field">
              <label for="contact">Personne à contacter</label>
              <input id="contact" class="input" name="contact" maxlength="80" [(ngModel)]="f.contact" />
            </div>
          </div>
          <div class="form-row">
            <div class="field">
              <label for="tel">Téléphone</label>
              <input id="tel" class="input" name="telephone" maxlength="30" [(ngModel)]="f.telephone" />
            </div>
            <div class="field">
              <label for="email">Email</label>
              <input id="email" class="input" type="email" name="email" email maxlength="100" [(ngModel)]="f.email" />
            </div>
          </div>
          <div class="field">
            <label for="adr">Adresse</label>
            <input id="adr" class="input" name="adresse" maxlength="150" [(ngModel)]="f.adresse" />
          </div>
        </form>
        <div pied>
          <button class="btn btn-secondary" (click)="form.set(null)">Annuler</button>
          <button class="btn btn-primary" type="submit" form="form-fourn">Enregistrer</button>
        </div>
      </app-modal>
    }
  `,
  styles: `
    .fournisseur {
      display: flex;
      flex-direction: column;
      gap: 14px;
    }
    h3 {
      font-size: 16px;
      font-weight: 700;
    }
    .grow {
      flex: 1;
      min-width: 0;
    }
    ul {
      list-style: none;
      padding: 0;
      margin: 0;
      display: grid;
      gap: 8px;
      color: var(--text-2);
      flex: 1;
    }
    li {
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .pied {
      justify-content: space-between;
      border-top: 1px solid var(--border);
      padding-top: 12px;
    }
  `,
})
export class FournisseursPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);

  protected initiales = initiales;
  protected fournisseurs = signal<Fournisseur[] | null>(null);
  protected form = signal<Fournisseur | null>(null);

  constructor() {
    this.charger();
  }

  charger(): void {
    this.api.fournisseurs.liste().subscribe({
      next: (l) => this.fournisseurs.set(l),
      error: (e) => this.toasts.erreur(e),
    });
  }

  ouvrir(f?: Fournisseur): void {
    this.form.set(f ? { ...f } : { nom: '', contact: '', telephone: '', email: '', adresse: '' });
  }

  enregistrer(ngf: NgForm): void {
    const f = this.form();
    if (!f || ngf.invalid) {
      ngf.control.markAllAsTouched();
      return;
    }
    const appel = f.idFournisseur
      ? this.api.fournisseurs.modifier(f.idFournisseur, f)
      : this.api.fournisseurs.creer(f);
    appel.subscribe({
      next: () => {
        this.toasts.succes(f.idFournisseur ? 'Fournisseur mis à jour' : 'Fournisseur ajouté');
        this.form.set(null);
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  async supprimer(f: Fournisseur): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Supprimer « ${f.nom} » ?`,
      message: 'Impossible si des produits ou des approvisionnements lui sont rattachés.',
      libelle: 'Supprimer',
      danger: true,
    });
    if (!ok) return;
    this.api.fournisseurs.supprimer(f.idFournisseur!).subscribe({
      next: () => {
        this.toasts.succes('Fournisseur supprimé');
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }
}
