import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ConfirmService } from '../../core/confirm.service';
import { FcfaPipe, emojiCategorie } from '../../core/format';
import { Menu, Plat } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

interface FormMenu {
  idMenu?: number;
  nom: string;
  description: string;
  prix: number | null;
  platIds: number[];
}

@Component({
  selector: 'app-menus',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, IconComponent, ModalComponent, FcfaPipe],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Menus & formules</h1>
          <p>Associez plusieurs plats à un prix avantageux.</p>
        </div>
        @if (auth.peutGerer()) {
          <button class="btn btn-primary" (click)="ouvrir()"><app-icon name="plus" [size]="16" /> Nouveau menu</button>
        }
      </div>

      <div class="menus">
        @for (m of menus() ?? []; track m.idMenu) {
          <article class="card menu">
            <div class="row between">
              <div>
                <h3>{{ m.nom }}</h3>
                <p class="muted">{{ m.description }}</p>
              </div>
              <div class="right">
                <div class="prix">{{ m.prix | fcfa }}</div>
                @if ((m.prixSepare ?? 0) > m.prix) {
                  <span class="badge success plain">−{{ (m.prixSepare ?? 0) - m.prix | fcfa }}</span>
                }
              </div>
            </div>
            <ul>
              @for (p of m.plats; track p.idPlat) {
                <li><span>{{ emoji(p.categorie?.nom) }}</span> {{ p.nom }} <span class="muted small">{{ p.prix | fcfa }}</span></li>
              }
            </ul>
            @if (auth.peutGerer()) {
              <div class="row pied">
                <button class="btn btn-secondary btn-sm" (click)="ouvrir(m)"><app-icon name="pencil" [size]="14" /> Modifier</button>
                <button class="btn btn-ghost btn-sm danger-text" (click)="supprimer(m)"><app-icon name="trash" [size]="14" /> Supprimer</button>
              </div>
            }
          </article>
        } @empty {
          @if (menus() !== null) {
            <div class="empty card" style="grid-column: 1 / -1">
              <span class="empty-icon"><app-icon name="book" [size]="26" /></span>
              <h3>Aucun menu</h3>
              <p>Créez une formule midi ou un menu dégustation.</p>
            </div>
          }
        }
      </div>
    </div>

    @if (form(); as f) {
      <app-modal [titre]="f.idMenu ? 'Modifier le menu' : 'Nouveau menu'" taille="lg" (fermer)="form.set(null)">
        <form #ngf="ngForm" class="form" id="form-menu" (ngSubmit)="enregistrer(ngf)">
          <div class="form-row">
            <div class="field">
              <label for="nom">Nom</label>
              <input id="nom" class="input" name="nom" required maxlength="80" [(ngModel)]="f.nom" />
            </div>
            <div class="field">
              <label for="prix">Prix du menu (FCFA)</label>
              <input id="prix" class="input" type="number" min="0" step="50" name="prix" required [(ngModel)]="f.prix" />
              <span class="hint">Prix des plats séparés : {{ prixSepare() | fcfa }}</span>
            </div>
          </div>
          <div class="field">
            <label for="desc">Description</label>
            <input id="desc" class="input" name="description" maxlength="255" [(ngModel)]="f.description" />
          </div>
          <div class="field">
            <span class="label">Plats inclus ({{ f.platIds.length }})</span>
            <div class="choix">
              @for (p of plats(); track p.idPlat) {
                <button type="button" class="choix-plat" [class.on]="f.platIds.includes(p.idPlat!)" (click)="basculer(p)">
                  <span>{{ emoji(p.categorie?.nom) }}</span>
                  <span class="grow">{{ p.nom }}</span>
                  <span class="small muted">{{ p.prix | fcfa }}</span>
                  <app-icon [name]="f.platIds.includes(p.idPlat!) ? 'check' : 'plus'" [size]="14" />
                </button>
              }
            </div>
          </div>
        </form>
        <div pied>
          <button class="btn btn-secondary" (click)="form.set(null)">Annuler</button>
          <button class="btn btn-primary" type="submit" form="form-menu">Enregistrer</button>
        </div>
      </app-modal>
    }
  `,
  styles: `
    .menus {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 18px;
    }
    .menu {
      padding: 20px;
      display: flex;
      flex-direction: column;
      gap: 12px;
      background: linear-gradient(160deg, var(--primary-soft), var(--surface) 55%);
    }
    .menu h3 {
      font-size: 18px;
      font-weight: 800;
    }
    .menu p {
      margin: 2px 0 0;
    }
    .prix {
      font-size: 20px;
      font-weight: 800;
      color: var(--primary-text);
      white-space: nowrap;
    }
    ul {
      list-style: none;
      margin: 0;
      padding: 0;
      display: grid;
      gap: 8px;
      flex: 1;
    }
    li {
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .pied {
      border-top: 1px solid var(--border);
      padding-top: 12px;
    }
    .danger-text {
      color: var(--danger);
    }
    .choix {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
      gap: 8px;
      max-height: 300px;
      overflow-y: auto;
    }
    .choix-plat {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 9px 12px;
      border-radius: 10px;
      border: 1px solid var(--border);
      background: var(--surface);
      cursor: pointer;
      text-align: left;
    }
    .choix-plat.on {
      border-color: var(--primary);
      background: var(--primary-soft);
      color: var(--primary-text);
    }
    .grow {
      flex: 1;
      font-weight: 600;
    }
  `,
})
export class MenusPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);
  protected auth = inject(AuthService);

  protected emoji = emojiCategorie;
  protected menus = signal<Menu[] | null>(null);
  protected plats = signal<Plat[]>([]);
  protected form = signal<FormMenu | null>(null);

  protected prixSepare = computed(() => {
    const f = this.form();
    if (!f) return 0;
    return this.plats()
      .filter((p) => f.platIds.includes(p.idPlat!))
      .reduce((s, p) => s + p.prix, 0);
  });

  constructor() {
    forkJoin({ menus: this.api.menus.liste(), plats: this.api.plats.liste() }).subscribe({
      next: (r) => {
        this.menus.set(r.menus);
        this.plats.set(r.plats);
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  ouvrir(m?: Menu): void {
    this.form.set({
      idMenu: m?.idMenu,
      nom: m?.nom ?? '',
      description: m?.description ?? '',
      prix: m?.prix ?? null,
      platIds: m?.plats.map((p) => p.idPlat!) ?? [],
    });
  }

  basculer(p: Plat): void {
    const f = this.form();
    if (!f) return;
    const ids = f.platIds.includes(p.idPlat!)
      ? f.platIds.filter((id) => id !== p.idPlat)
      : [...f.platIds, p.idPlat!];
    this.form.set({ ...f, platIds: ids });
  }

  enregistrer(ngf: NgForm): void {
    const f = this.form();
    if (!f || ngf.invalid || f.prix === null) {
      ngf.control.markAllAsTouched();
      return;
    }
    if (!f.platIds.length) {
      this.toasts.erreur('Sélectionnez au moins un plat.');
      return;
    }
    const menu: Menu = {
      nom: f.nom.trim(),
      description: f.description.trim(),
      prix: f.prix,
      plats: f.platIds.map((idPlat) => ({ idPlat, nom: '', prix: 0 })),
    };
    const appel = f.idMenu ? this.api.menus.modifier(f.idMenu, menu) : this.api.menus.creer(menu);
    appel.subscribe({
      next: (m) => {
        this.menus.update((l) =>
          f.idMenu ? (l ?? []).map((x) => (x.idMenu === m.idMenu ? m : x)) : [...(l ?? []), m],
        );
        this.toasts.succes(f.idMenu ? 'Menu mis à jour' : 'Menu créé');
        this.form.set(null);
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  async supprimer(m: Menu): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Supprimer « ${m.nom} » ?`,
      message: 'Les plats du menu restent disponibles à la carte.',
      libelle: 'Supprimer',
      danger: true,
    });
    if (!ok) return;
    this.api.menus.supprimer(m.idMenu!).subscribe({
      next: () => {
        this.menus.update((l) => (l ?? []).filter((x) => x.idMenu !== m.idMenu));
        this.toasts.succes('Menu supprimé');
      },
      error: (e) => this.toasts.erreur(e),
    });
  }
}
