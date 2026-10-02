import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { FcfaPipe, STATUTS } from '../../core/format';
import { Livraison, Personnel, StatutLivraison } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';

type Colonne = { statut: StatutLivraison; titre: string; icone: string };

/** Tableau de suivi des livraisons, organisé en colonnes par statut. */
@Component({
  selector: 'app-livraisons',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, DatePipe, IconComponent, FcfaPipe],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Livraisons</h1>
          <p>Assignez un livreur et suivez chaque course jusqu'au client.</p>
        </div>
        <button class="btn btn-secondary" (click)="charger()"><app-icon name="refresh" [size]="16" /> Actualiser</button>
      </div>

      @if (livraisons() === null) {
        <div class="grid grid-3">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton" style="height: 320px"></div>
          }
        </div>
      } @else {
        <div class="board">
          @for (col of colonnes; track col.statut) {
            <section class="colonne">
              <header>
                <app-icon [name]="col.icone" [size]="18" />
                <h2>{{ col.titre }}</h2>
                <span class="badge plain">{{ parStatut()[col.statut].length }}</span>
              </header>
              @for (l of parStatut()[col.statut]; track l.idLivraison) {
                <article class="card livraison">
                  <div class="row between">
                    <strong>Commande #{{ l.commande?.idCommande }}</strong>
                    <span class="strong">{{ l.commande?.montantTotal | fcfa }}</span>
                  </div>
                  <div class="ligne"><app-icon name="pin" [size]="15" /> {{ l.adresseDestination }}</div>
                  @if (l.commande?.client; as c) {
                    <div class="ligne"><app-icon name="user" [size]="15" /> {{ c.prenom }} {{ c.nom }} · {{ c.telephone }}</div>
                  }
                  <div class="ligne muted small">
                    <app-icon name="clock" [size]="14" />
                    {{ l.commande?.creeLe ?? l.commande?.dateCommande | date: 'd MMM, HH:mm' }}
                    @if (l.commande?.payee) {
                      <span class="badge success plain">Payée</span>
                    }
                  </div>

                  @if (l.statut === 'A_ASSIGNER' || l.statut === 'EN_COURS' || l.statut === null) {
                    <div class="field">
                      <select
                        class="select"
                        [ngModel]="l.livreur?.idPersonnel ?? null"
                        (ngModelChange)="assigner(l, $event)"
                        [attr.aria-label]="'Livreur pour la commande ' + l.commande?.idCommande"
                      >
                        <option [ngValue]="null" disabled>Choisir un livreur…</option>
                        @for (p of livreurs(); track p.idPersonnel) {
                          <option [ngValue]="p.idPersonnel">{{ p.prenom }} {{ p.nom }}</option>
                        }
                      </select>
                    </div>
                  } @else if (l.livreur) {
                    <div class="ligne"><app-icon name="truck" [size]="15" /> {{ l.livreur.prenom }} {{ l.livreur.nom }}</div>
                  }

                  @if (l.statut === 'EN_COURS') {
                    <div class="row">
                      <button class="btn btn-success btn-sm grow" (click)="changer(l, 'LIVREE')">
                        <app-icon name="check" [size]="14" /> Livrée
                      </button>
                      <button class="btn btn-secondary btn-sm" (click)="changer(l, 'ECHOUEE')">Échec</button>
                    </div>
                  }
                  @if (l.statut === 'LIVREE' && l.dateLivraison) {
                    <span class="small muted">Livrée le {{ l.dateLivraison | date: 'd MMMM' }}</span>
                  }
                </article>
              } @empty {
                <p class="vide">Aucune livraison</p>
              }
            </section>
          }
        </div>
        @if (!livreurs().length) {
          <p class="muted mt">Astuce : ajoutez des employés avec la fonction « Livreur » pour pouvoir les assigner.</p>
        }
      }
    </div>
  `,
  styles: `
    .board {
      display: grid;
      grid-template-columns: repeat(4, minmax(240px, 1fr));
      gap: 16px;
      overflow-x: auto;
      padding-bottom: 8px;
    }
    .colonne {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 14px;
      border-radius: 18px;
      background: var(--surface-hover);
      min-height: 200px;
    }
    .colonne header {
      display: flex;
      align-items: center;
      gap: 8px;
      color: var(--text-2);
    }
    .colonne h2 {
      font-size: 14px;
      font-weight: 700;
      flex: 1;
    }
    .livraison {
      display: flex;
      flex-direction: column;
      gap: 8px;
      padding: 14px;
      animation: apparition 0.2s ease-out;
    }
    .ligne {
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .grow {
      flex: 1;
    }
    .vide {
      text-align: center;
      color: var(--muted);
      margin: 24px 0;
    }
  `,
})
export class LivraisonsPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);

  protected statuts = STATUTS;
  protected colonnes: Colonne[] = [
    { statut: 'A_ASSIGNER', titre: 'À assigner', icone: 'clock' },
    { statut: 'EN_COURS', titre: 'En route', icone: 'truck' },
    { statut: 'LIVREE', titre: 'Livrées', icone: 'check' },
    { statut: 'ECHOUEE', titre: 'Échouées', icone: 'ban' },
  ];

  protected livraisons = signal<Livraison[] | null>(null);
  protected livreurs = signal<Personnel[]>([]);

  protected parStatut = computed(() => {
    const groupes: Record<StatutLivraison, Livraison[]> = { A_ASSIGNER: [], EN_COURS: [], LIVREE: [], ECHOUEE: [] };
    for (const l of this.livraisons() ?? []) {
      // les commandes annulées ne sont plus à livrer
      if (l.commande?.statut === 'ANNULEE' && l.statut !== 'ECHOUEE') continue;
      groupes[l.statut ?? 'A_ASSIGNER'].push(l);
    }
    return groupes;
  });

  constructor() {
    this.charger();
  }

  charger(): void {
    forkJoin({ livraisons: this.api.livraisons.liste(), livreurs: this.api.personnel.liste({ fonction: 'LIVREUR' }) })
      .subscribe({
        next: (r) => {
          this.livraisons.set(r.livraisons);
          this.livreurs.set(r.livreurs.filter((p) => p.actif !== false));
        },
        error: (e) => this.toasts.erreur(e),
      });
  }

  assigner(l: Livraison, livreurId: number): void {
    this.maj(l, { livreurId }, 'Livreur assigné');
  }

  changer(l: Livraison, statut: StatutLivraison): void {
    this.maj(l, { statut }, statut === 'LIVREE' ? 'Livraison terminée 🎉' : 'Livraison marquée en échec');
  }

  private maj(l: Livraison, data: { livreurId?: number; statut?: StatutLivraison }, message: string): void {
    this.api.livraisons.maj(l.idLivraison, data).subscribe({
      next: (maj) => {
        this.livraisons.update((liste) => (liste ?? []).map((x) => (x.idLivraison === maj.idLivraison ? maj : x)));
        this.toasts.succes(message);
      },
      error: (e) => this.toasts.erreur(e),
    });
  }
}
