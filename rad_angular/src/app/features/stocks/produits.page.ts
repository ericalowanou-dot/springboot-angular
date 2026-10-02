import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { ConfirmService } from '../../core/confirm.service';
import { FcfaPipe } from '../../core/format';
import { Fournisseur, Produit, ProduitRequest } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

type FormProduit = ProduitRequest & { idProduit?: number };

@Component({
  selector: 'app-produits',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, RouterLink, IconComponent, ModalComponent, FcfaPipe],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Stocks</h1>
          <p>Matières premières, niveaux de stock et seuils d'alerte.</p>
        </div>
        <div class="actions">
          <a routerLink="/approvisionnements" class="btn btn-secondary"><app-icon name="clipboard" [size]="16" /> Approvisionner</a>
          <button class="btn btn-primary" (click)="ouvrir()"><app-icon name="plus" [size]="16" /> Nouveau produit</button>
        </div>
      </div>

      <div class="grid grid-3">
        <div class="card stat">
          <span class="stat-icon info"><app-icon name="package" [size]="22" /></span>
          <div><div class="stat-label">Produits suivis</div><div class="stat-value">{{ produits()?.length ?? 0 }}</div></div>
        </div>
        <div class="card stat">
          <span class="stat-icon warning"><app-icon name="alert" [size]="22" /></span>
          <div><div class="stat-label">En alerte</div><div class="stat-value">{{ alertes() }}</div></div>
        </div>
        <div class="card stat">
          <span class="stat-icon success"><app-icon name="wallet" [size]="22" /></span>
          <div><div class="stat-label">Valeur du stock</div><div class="stat-value">{{ valeur() | fcfa }}</div></div>
        </div>
      </div>

      <div class="card mt">
        <div class="toolbar">
          <div class="segmented">
            <button [class.active]="!seulementAlertes()" (click)="seulementAlertes.set(false)">Tous</button>
            <button [class.active]="seulementAlertes()" (click)="seulementAlertes.set(true)">
              En alerte <span class="count">{{ alertes() }}</span>
            </button>
          </div>
        </div>
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th>Produit</th>
                <th class="hide-sm">Fournisseur</th>
                <th>Stock</th>
                <th class="hide-sm">Prix unitaire</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              @for (p of affiches(); track p.idProduit) {
                <tr>
                  <td>
                    <div class="strong">{{ p.nom }}</div>
                    <div class="small muted">Seuil : {{ p.seuil }} {{ p.unite }}</div>
                  </td>
                  <td class="hide-sm muted">{{ p.fournisseur?.nom ?? '—' }}</td>
                  <td style="min-width: 180px">
                    <div class="row between small">
                      <span class="strong">{{ p.quantiteStock }} {{ p.unite }}</span>
                      @if (p.enAlerte) {
                        <span class="badge danger plain">Bas</span>
                      }
                    </div>
                    <div class="jauge"><span [class.bas]="p.enAlerte" [style.width.%]="jauge(p)"></span></div>
                  </td>
                  <td class="hide-sm nowrap">{{ p.prixUnitaire | fcfa }}</td>
                  <td class="actions-cell">
                    <button class="icon-btn" title="Ajuster le stock" (click)="ajuster(p)"><app-icon name="refresh" [size]="16" /></button>
                    <button class="icon-btn" title="Modifier" (click)="ouvrir(p)"><app-icon name="pencil" [size]="16" /></button>
                    <button class="icon-btn danger" title="Supprimer" (click)="supprimer(p)"><app-icon name="trash" [size]="16" /></button>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="5"><div class="empty"><span class="empty-icon"><app-icon name="package" [size]="26" /></span><h3>Aucun produit</h3></div></td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>

    @if (form(); as f) {
      <app-modal [titre]="f.idProduit ? 'Modifier le produit' : 'Nouveau produit'" (fermer)="form.set(null)">
        <form #ngf="ngForm" class="form" id="form-produit" (ngSubmit)="enregistrer(ngf)">
          <div class="form-row">
            <div class="field">
              <label for="nom">Nom</label>
              <input id="nom" class="input" name="nom" required maxlength="80" [(ngModel)]="f.nom" />
            </div>
            <div class="field">
              <label for="unite">Unité</label>
              <input id="unite" class="input" name="unite" maxlength="20" placeholder="kg, L, pièce…" [(ngModel)]="f.unite" />
            </div>
          </div>
          <div class="form-row">
            <div class="field">
              <label for="prix">Prix unitaire (FCFA)</label>
              <input id="prix" class="input" type="number" min="0" name="prix" required [(ngModel)]="f.prixUnitaire" />
            </div>
            <div class="field">
              <label for="seuil">Seuil d'alerte</label>
              <input id="seuil" class="input" type="number" min="0" name="seuil" required [(ngModel)]="f.seuil" />
            </div>
          </div>
          <div class="form-row">
            <div class="field">
              <label for="fourn">Fournisseur</label>
              <select id="fourn" class="select" name="fournisseur" [(ngModel)]="f.fournisseurId">
                <option [ngValue]="null">Aucun</option>
                @for (fo of fournisseurs(); track fo.idFournisseur) {
                  <option [ngValue]="fo.idFournisseur">{{ fo.nom }}</option>
                }
              </select>
            </div>
            <div class="field">
              <label for="qte">Quantité en stock</label>
              <input id="qte" class="input" type="number" min="0" name="quantite" [(ngModel)]="f.quantite" />
            </div>
          </div>
        </form>
        <div pied>
          <button class="btn btn-secondary" (click)="form.set(null)">Annuler</button>
          <button class="btn btn-primary" type="submit" form="form-produit">Enregistrer</button>
        </div>
      </app-modal>
    }

    @if (ajustement(); as a) {
      <app-modal titre="Ajuster le stock" [sousTitre]="a.produit.nom" taille="sm" (fermer)="ajustement.set(null)">
        <div class="field">
          <label for="nq">Quantité réelle constatée ({{ a.produit.unite }})</label>
          <input id="nq" class="input" type="number" min="0" [(ngModel)]="a.quantite" />
          <span class="hint">Stock actuel enregistré : {{ a.produit.quantiteStock }} {{ a.produit.unite }}</span>
        </div>
        <div pied>
          <button class="btn btn-secondary" (click)="ajustement.set(null)">Annuler</button>
          <button class="btn btn-primary" (click)="validerAjustement(a.produit, a.quantite)">Mettre à jour</button>
        </div>
      </app-modal>
    }
  `,
  styles: `
    .jauge {
      height: 6px;
      margin-top: 6px;
      border-radius: 999px;
      background: var(--surface-hover);
      overflow: hidden;
    }
    .jauge span {
      display: block;
      height: 100%;
      border-radius: inherit;
      background: var(--success);
    }
    .jauge span.bas {
      background: var(--danger);
    }
  `,
})
export class ProduitsPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);

  protected produits = signal<Produit[] | null>(null);
  protected fournisseurs = signal<Fournisseur[]>([]);
  protected seulementAlertes = signal(false);
  protected form = signal<FormProduit | null>(null);
  protected ajustement = signal<{ produit: Produit; quantite: number } | null>(null);

  protected alertes = computed(() => (this.produits() ?? []).filter((p) => p.enAlerte).length);
  protected valeur = computed(() =>
    (this.produits() ?? []).reduce((s, p) => s + p.quantiteStock * (p.prixUnitaire ?? 0), 0),
  );
  protected affiches = computed(() =>
    (this.produits() ?? []).filter((p) => !this.seulementAlertes() || p.enAlerte),
  );

  constructor() {
    this.charger();
  }

  charger(): void {
    forkJoin({ produits: this.api.produits.liste(), fournisseurs: this.api.fournisseurs.liste() }).subscribe({
      next: (r) => {
        this.produits.set(r.produits);
        this.fournisseurs.set(r.fournisseurs);
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  /** Remplissage de la jauge : le seuil correspond à 25 %. */
  jauge(p: Produit): number {
    const reference = Math.max(p.seuil * 4, 1);
    return Math.min((p.quantiteStock / reference) * 100, 100);
  }

  ouvrir(p?: Produit): void {
    this.form.set({
      idProduit: p?.idProduit,
      nom: p?.nom ?? '',
      unite: p?.unite ?? '',
      prixUnitaire: p?.prixUnitaire ?? 0,
      seuil: p?.seuil ?? 0,
      fournisseurId: p?.fournisseur?.idFournisseur ?? null,
      quantite: p?.quantiteStock ?? 0,
    });
  }

  enregistrer(ngf: NgForm): void {
    const f = this.form();
    if (!f || ngf.invalid) {
      ngf.control.markAllAsTouched();
      return;
    }
    const { idProduit, ...requete } = f;
    const appel = idProduit ? this.api.produits.modifier(idProduit, requete) : this.api.produits.creer(requete);
    appel.subscribe({
      next: () => {
        this.toasts.succes(idProduit ? 'Produit mis à jour' : 'Produit ajouté');
        this.form.set(null);
        this.charger();
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  ajuster(p: Produit): void {
    this.ajustement.set({ produit: p, quantite: p.quantiteStock });
  }

  validerAjustement(p: Produit, quantite: number): void {
    this.api.produits.stock(p.idProduit, quantite).subscribe({
      next: (maj) => {
        this.produits.update((l) => (l ?? []).map((x) => (x.idProduit === maj.idProduit ? maj : x)));
        this.ajustement.set(null);
        this.toasts.succes('Stock mis à jour');
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  async supprimer(p: Produit): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Supprimer « ${p.nom} » ?`,
      message: 'Le produit et son stock seront supprimés.',
      libelle: 'Supprimer',
      danger: true,
    });
    if (!ok) return;
    this.api.produits.supprimer(p.idProduit).subscribe({
      next: () => {
        this.produits.update((l) => (l ?? []).filter((x) => x.idProduit !== p.idProduit));
        this.toasts.succes('Produit supprimé');
      },
      error: (e) => this.toasts.erreur(e),
    });
  }
}
