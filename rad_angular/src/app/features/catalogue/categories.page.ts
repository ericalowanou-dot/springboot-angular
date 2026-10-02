import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ConfirmService } from '../../core/confirm.service';
import { emojiCategorie } from '../../core/format';
import { Categorie } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

@Component({
  selector: 'app-categories',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, RouterLink, IconComponent, ModalComponent],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Catégories</h1>
          <p>Organisez la carte en rubriques.</p>
        </div>
        @if (auth.peutGerer()) {
          <button class="btn btn-primary" (click)="ouvrir()"><app-icon name="plus" [size]="16" /> Nouvelle catégorie</button>
        }
      </div>

      <div class="cards">
        @for (c of categories() ?? []; track c.idCategorie) {
          <article class="card categorie">
            <span class="emoji">{{ emoji(c.nom) }}</span>
            <div class="info">
              <h3>{{ c.nom }}</h3>
              <p class="muted">{{ c.description || 'Sans description' }}</p>
              <a routerLink="/plats" class="badge primary plain">{{ c.nombrePlats ?? 0 }} plat(s)</a>
            </div>
            @if (auth.peutGerer()) {
              <div class="actions-c">
                <button class="icon-btn" title="Modifier" (click)="ouvrir(c)"><app-icon name="pencil" [size]="16" /></button>
                <button class="icon-btn danger" title="Supprimer" (click)="supprimer(c)"><app-icon name="trash" [size]="16" /></button>
              </div>
            }
          </article>
        } @empty {
          @if (categories() === null) {
            @for (i of [1, 2, 3]; track i) {
              <div class="skeleton" style="height: 120px"></div>
            }
          } @else {
            <div class="empty card" style="grid-column: 1 / -1">
              <span class="empty-icon"><app-icon name="tag" [size]="26" /></span>
              <h3>Aucune catégorie</h3>
            </div>
          }
        }
      </div>
    </div>

    @if (form(); as f) {
      <app-modal [titre]="f.idCategorie ? 'Modifier la catégorie' : 'Nouvelle catégorie'" (fermer)="form.set(null)">
        <form #ngf="ngForm" class="form" id="form-cat" (ngSubmit)="enregistrer(ngf)">
          <div class="field">
            <label for="nom">Nom</label>
            <input id="nom" class="input" name="nom" required maxlength="60" placeholder="Ex. Grillades" [(ngModel)]="f.nom" />
          </div>
          <div class="field">
            <label for="desc">Description</label>
            <textarea id="desc" class="textarea" name="description" maxlength="255" [(ngModel)]="f.description"></textarea>
          </div>
        </form>
        <div pied>
          <button class="btn btn-secondary" (click)="form.set(null)">Annuler</button>
          <button class="btn btn-primary" type="submit" form="form-cat">Enregistrer</button>
        </div>
      </app-modal>
    }
  `,
  styles: `
    .categorie {
      display: flex;
      gap: 16px;
      padding: 18px;
      align-items: flex-start;
    }
    .emoji {
      display: grid;
      place-items: center;
      width: 56px;
      height: 56px;
      border-radius: 16px;
      font-size: 28px;
      flex-shrink: 0;
      background: var(--primary-soft);
    }
    .info {
      flex: 1;
      min-width: 0;
    }
    .info h3 {
      font-size: 16px;
      font-weight: 700;
    }
    .info p {
      margin: 4px 0 10px;
      font-size: 13px;
    }
    .actions-c {
      display: flex;
      flex-direction: column;
    }
  `,
})
export class CategoriesPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);
  protected auth = inject(AuthService);

  protected emoji = emojiCategorie;
  protected categories = signal<Categorie[] | null>(null);
  protected form = signal<Categorie | null>(null);

  constructor() {
    this.charger();
  }

  charger(): void {
    this.api.categories.liste().subscribe({
      next: (l) => this.categories.set(l),
      error: (e) => this.toasts.erreur(e),
    });
  }

  ouvrir(c?: Categorie): void {
    this.form.set(c ? { ...c } : { nom: '', description: '' });
  }

  enregistrer(ngf: NgForm): void {
    const f = this.form();
    if (!f || ngf.invalid) {
      ngf.control.markAllAsTouched();
      return;
    }
    const appel = f.idCategorie ? this.api.categories.modifier(f.idCategorie, f) : this.api.categories.creer(f);
    appel.subscribe({
      next: () => {
        this.toasts.succes(f.idCategorie ? 'Catégorie mise à jour' : 'Catégorie créée');
        this.form.set(null);
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  async supprimer(c: Categorie): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Supprimer « ${c.nom} » ?`,
      message: 'Une catégorie ne peut être supprimée que si elle ne contient plus aucun plat.',
      libelle: 'Supprimer',
      danger: true,
    });
    if (!ok) return;
    this.api.categories.supprimer(c.idCategorie!).subscribe({
      next: () => {
        this.toasts.succes('Catégorie supprimée');
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }
}
