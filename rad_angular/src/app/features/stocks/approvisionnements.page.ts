import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { ConfirmService } from '../../core/confirm.service';
import { FcfaPipe, LibellePipe, STATUTS } from '../../core/format';
import { Approvisionnement, Fournisseur, Produit } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

interface LigneForm {
  produitId: number | null;
  quantite: number;
  prixUnitaire: number;
}

@Component({
  selector: 'app-approvisionnements',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, DatePipe, IconComponent, ModalComponent, FcfaPipe, LibellePipe],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Approvisionnements</h1>
          <p>Commandes passées aux fournisseurs. À la réception, le stock est mis à jour automatiquement.</p>
        </div>
        <button class="btn btn-primary" (click)="ouvrir()"><app-icon name="plus" [size]="16" /> Nouvelle commande fournisseur</button>
      </div>

      <div class="stack">
        @for (a of appros() ?? []; track a.idCmdInt) {
          <article class="card appro">
            <div class="entete">
              <div class="row">
                <span class="stat-icon {{ statuts[a.etat]?.ton }}"><app-icon name="clipboard" [size]="20" /></span>
                <div>
                  <h3>Bon n° {{ a.idCmdInt }} · {{ a.fournisseur.nom }}</h3>
                  <span class="small muted">
                    Commandé le {{ a.date | date: 'd MMMM y' }}
                    @if (a.dateReception) {
                      · reçu le {{ a.dateReception | date: 'd MMMM y' }}
                    }
                  </span>
                </div>
              </div>
              <div class="row">
                <span class="badge {{ statuts[a.etat]?.ton }}">{{ a.etat | libelle: 'statut' }}</span>
                <strong class="montant">{{ a.montantTotal | fcfa }}</strong>
              </div>
            </div>
            <div class="lignes">
              @for (l of a.lignes; track l.idLigneInt) {
                <span class="chip">{{ l.produit.nom }} · {{ l.quantite }} {{ l.produit.unite }} × {{ l.prixUnitaire | fcfa }}</span>
              }
            </div>
            @if (a.etat === 'EN_COURS') {
              <div class="row actions-a">
                <button class="btn btn-success btn-sm" (click)="recevoir(a)"><app-icon name="check" [size]="14" /> Marquer comme reçue</button>
                <button class="btn btn-secondary btn-sm" (click)="annuler(a)">Annuler</button>
              </div>
            }
          </article>
        } @empty {
          @if (appros() !== null) {
            <div class="empty card">
              <span class="empty-icon"><app-icon name="clipboard" [size]="26" /></span>
              <h3>Aucun approvisionnement</h3>
              <p>Créez une commande fournisseur pour réapprovisionner vos stocks.</p>
            </div>
          }
        }
      </div>
    </div>

    @if (form(); as f) {
      <app-modal titre="Nouvelle commande fournisseur" taille="lg" (fermer)="form.set(null)">
        <div class="form">
          <div class="field">
            <label for="fourn">Fournisseur</label>
            <select id="fourn" class="select" [(ngModel)]="f.fournisseurId">
              <option [ngValue]="null" disabled>Choisir…</option>
              @for (fo of fournisseurs(); track fo.idFournisseur) {
                <option [ngValue]="fo.idFournisseur">{{ fo.nom }}</option>
              }
            </select>
          </div>

          <div class="stack">
            <span class="label">Produits</span>
            @for (l of f.lignes; track $index) {
              <div class="ligne-form">
                <select class="select" [(ngModel)]="l.produitId" (ngModelChange)="choisirProduit(l, $event)" aria-label="Produit">
                  <option [ngValue]="null" disabled>Produit…</option>
                  @for (p of produits(); track p.idProduit) {
                    <option [ngValue]="p.idProduit">{{ p.nom }}{{ p.enAlerte ? ' ⚠' : '' }}</option>
                  }
                </select>
                <input class="input" type="number" min="1" [(ngModel)]="l.quantite" (ngModelChange)="recalculer()" aria-label="Quantité" />
                <input class="input" type="number" min="0" [(ngModel)]="l.prixUnitaire" (ngModelChange)="recalculer()" aria-label="Prix unitaire" />
                <button class="icon-btn danger" (click)="retirerLigne($index)" aria-label="Retirer"><app-icon name="trash" [size]="16" /></button>
              </div>
            }
            <button class="btn btn-ghost btn-sm ajout" (click)="ajouterLigne()"><app-icon name="plus" [size]="14" /> Ajouter un produit</button>
          </div>
          <div class="row between total">
            <span>Total estimé</span>
            <strong>{{ totalForm() | fcfa }}</strong>
          </div>
        </div>
        <div pied>
          <button class="btn btn-secondary" (click)="form.set(null)">Annuler</button>
          <button class="btn btn-primary" (click)="enregistrer()">Passer la commande</button>
        </div>
      </app-modal>
    }
  `,
  styles: `
    .appro {
      padding: 18px 20px;
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    .entete {
      display: flex;
      justify-content: space-between;
      gap: 12px;
      flex-wrap: wrap;
    }
    h3 {
      font-size: 15px;
      font-weight: 700;
    }
    .montant {
      font-size: 16px;
    }
    .lignes {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
    }
    .ligne-form {
      display: grid;
      grid-template-columns: minmax(0, 2fr) 90px 120px auto;
      gap: 8px;
      align-items: center;
    }
    .ajout {
      align-self: flex-start;
    }
    .total {
      padding: 12px 14px;
      border-radius: 12px;
      background: var(--surface-2);
      font-size: 15px;
    }
  `,
})
export class ApprovisionnementsPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);

  protected statuts = STATUTS;
  protected appros = signal<Approvisionnement[] | null>(null);
  protected fournisseurs = signal<Fournisseur[]>([]);
  protected produits = signal<Produit[]>([]);
  protected form = signal<{ fournisseurId: number | null; lignes: LigneForm[] } | null>(null);
  // incrémenté à chaque saisie pour recalculer le total (les champs sont modifiés par ngModel)
  private version = signal(0);

  protected totalForm = computed(() => {
    this.version();
    return (this.form()?.lignes ?? []).reduce((s, l) => s + (l.quantite || 0) * (l.prixUnitaire || 0), 0);
  });

  constructor() {
    this.charger();
  }

  charger(): void {
    forkJoin({
      appros: this.api.approvisionnements.liste(),
      fournisseurs: this.api.fournisseurs.liste(),
      produits: this.api.produits.liste(),
    }).subscribe({
      next: (r) => {
        this.appros.set(r.appros);
        this.fournisseurs.set(r.fournisseurs);
        this.produits.set(r.produits);
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  ouvrir(): void {
    // pré-remplit avec les produits en alerte pour gagner du temps
    const alertes = this.produits().filter((p) => p.enAlerte);
    const lignes = alertes.length
      ? alertes.map((p) => ({ produitId: p.idProduit, quantite: Math.max(p.seuil * 2 - p.quantiteStock, 1), prixUnitaire: p.prixUnitaire }))
      : [{ produitId: null, quantite: 1, prixUnitaire: 0 }];
    const fournisseurId = alertes[0]?.fournisseur?.idFournisseur ?? null;
    this.form.set({ fournisseurId, lignes });
  }

  choisirProduit(l: LigneForm, id: number): void {
    const p = this.produits().find((x) => x.idProduit === id);
    if (p) l.prixUnitaire = p.prixUnitaire;
    this.version.update((v) => v + 1);
  }

  recalculer(): void {
    this.version.update((v) => v + 1);
  }

  ajouterLigne(): void {
    const f = this.form();
    if (f) this.form.set({ ...f, lignes: [...f.lignes, { produitId: null, quantite: 1, prixUnitaire: 0 }] });
  }

  retirerLigne(i: number): void {
    const f = this.form();
    if (f) this.form.set({ ...f, lignes: f.lignes.filter((_, j) => j !== i) });
  }

  enregistrer(): void {
    const f = this.form();
    if (!f) return;
    const lignes = f.lignes.filter((l) => l.produitId !== null && l.quantite > 0);
    if (!f.fournisseurId || !lignes.length) {
      this.toasts.erreur('Choisissez un fournisseur et au moins un produit.');
      return;
    }
    this.api.approvisionnements
      .creer({
        fournisseurId: f.fournisseurId,
        lignes: lignes.map((l) => ({ produitId: l.produitId!, quantite: l.quantite, prixUnitaire: l.prixUnitaire })),
      })
      .subscribe({
        next: () => {
          this.toasts.succes('Commande fournisseur enregistrée');
          this.form.set(null);
          this.charger();
        },
        error: (e) => this.toasts.erreur(e),
      });
  }

  recevoir(a: Approvisionnement): void {
    this.api.approvisionnements.recevoir(a.idCmdInt).subscribe({
      next: () => {
        this.toasts.succes('Réception enregistrée : stocks mis à jour');
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  async annuler(a: Approvisionnement): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Annuler le bon n° ${a.idCmdInt} ?`,
      message: 'La commande fournisseur sera marquée comme annulée.',
      libelle: 'Annuler la commande',
      danger: true,
    });
    if (!ok) return;
    this.api.approvisionnements.annuler(a.idCmdInt).subscribe({
      next: () => this.charger(),
      error: (e) => this.toasts.erreur(e),
    });
  }
}
