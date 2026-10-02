import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ConfirmService } from '../../core/confirm.service';
import { FcfaPipe, LibellePipe, STATUTS, initiales } from '../../core/format';
import { Client, Commande } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

@Component({
  selector: 'app-clients',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, DatePipe, IconComponent, ModalComponent, FcfaPipe, LibellePipe],
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <h1>Clients</h1>
          <p>{{ clients()?.length ?? 0 }} clients enregistrés.</p>
        </div>
        <button class="btn btn-primary" (click)="ouvrir()"><app-icon name="plus" [size]="16" /> Nouveau client</button>
      </div>

      <div class="card">
        <div class="toolbar">
          <div class="input-icon">
            <app-icon name="search" [size]="16" />
            <input class="input" placeholder="Nom, téléphone ou email…" [ngModel]="recherche()" (ngModelChange)="recherche.set($event)" />
          </div>
        </div>
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th>Client</th>
                <th>Téléphone</th>
                <th class="hide-sm">Email</th>
                <th class="hide-sm">Adresse</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              @for (c of filtres(); track c.idClient) {
                <tr>
                  <td>
                    <div class="row">
                      <span class="avatar">{{ initiales(c.prenom, c.nom) }}</span>
                      <span class="strong">{{ c.prenom }} {{ c.nom }}</span>
                    </div>
                  </td>
                  <td class="nowrap">{{ c.telephone }}</td>
                  <td class="hide-sm muted">{{ c.email }}</td>
                  <td class="hide-sm muted">{{ c.address }}</td>
                  <td class="actions-cell">
                    <button class="icon-btn" title="Historique" (click)="historique(c)"><app-icon name="receipt" [size]="16" /></button>
                    <button class="icon-btn" title="Modifier" (click)="ouvrir(c)"><app-icon name="pencil" [size]="16" /></button>
                    @if (auth.peutGerer()) {
                      <button class="icon-btn danger" title="Supprimer" (click)="supprimer(c)"><app-icon name="trash" [size]="16" /></button>
                    }
                  </td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="5">
                    <div class="empty">
                      <span class="empty-icon"><app-icon name="users" [size]="26" /></span>
                      <h3>{{ clients() === null ? 'Chargement…' : 'Aucun client' }}</h3>
                    </div>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>

    @if (form(); as f) {
      <app-modal [titre]="f.idClient ? 'Modifier le client' : 'Nouveau client'" (fermer)="form.set(null)">
        <form #ngf="ngForm" class="form" (ngSubmit)="enregistrer(ngf)" id="form-client">
          <div class="form-row">
            <div class="field">
              <label for="prenom">Prénom</label>
              <input id="prenom" class="input" name="prenom" required maxlength="30" [(ngModel)]="f.prenom" />
            </div>
            <div class="field">
              <label for="nom">Nom</label>
              <input id="nom" class="input" name="nom" required maxlength="30" [(ngModel)]="f.nom" />
            </div>
          </div>
          <div class="form-row">
            <div class="field">
              <label for="tel">Téléphone</label>
              <input id="tel" class="input" name="telephone" required maxlength="30" placeholder="+228 90 00 00 00" [(ngModel)]="f.telephone" />
            </div>
            <div class="field">
              <label for="email">Email</label>
              <input id="email" class="input" type="email" name="email" required email maxlength="30" [(ngModel)]="f.email" />
            </div>
          </div>
          <div class="field">
            <label for="adr">Adresse</label>
            <input id="adr" class="input" name="address" required maxlength="30" placeholder="Quartier, ville" [(ngModel)]="f.address" />
            <span class="hint">Utilisée par défaut pour les livraisons.</span>
          </div>
        </form>
        <div pied>
          <button class="btn btn-secondary" (click)="form.set(null)">Annuler</button>
          <button class="btn btn-primary" type="submit" form="form-client" [disabled]="envoi()">Enregistrer</button>
        </div>
      </app-modal>
    }

    @if (historiqueClient(); as h) {
      <app-modal [titre]="h.client.prenom + ' ' + h.client.nom" sousTitre="Historique des commandes" (fermer)="historiqueClient.set(null)">
        @if (h.commandes === null) {
          <div class="skeleton" style="height: 120px"></div>
        } @else {
          <div class="grid grid-2 resume">
            <div class="card card-pad"><div class="stat-label">Commandes</div><div class="stat-value">{{ h.commandes.length }}</div></div>
            <div class="card card-pad"><div class="stat-label">Total dépensé</div><div class="stat-value">{{ totalDepense(h.commandes) | fcfa }}</div></div>
          </div>
          <div class="stack mt">
            @for (c of h.commandes; track c.idCommande) {
              <div class="row between ligne-hist">
                <div>
                  <div class="strong">#{{ c.idCommande }} · {{ c.dateCommande | date: 'd MMM y' }}</div>
                  <div class="small muted">{{ c.lignes.length }} plat(s) · {{ c.type | libelle: 'type' }}</div>
                </div>
                <div class="right">
                  <div class="strong">{{ c.montantTotal | fcfa }}</div>
                  <span class="badge {{ statuts[c.statut ?? '']?.ton }}">{{ c.statut | libelle: 'statut' }}</span>
                </div>
              </div>
            } @empty {
              <p class="muted">Ce client n'a pas encore passé de commande.</p>
            }
          </div>
        }
      </app-modal>
    }
  `,
  styles: `
    .ligne-hist {
      padding: 10px 0;
      border-bottom: 1px solid var(--border);
    }
    .resume .stat-value {
      font-size: 20px;
    }
  `,
})
export class ClientsPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);
  protected auth = inject(AuthService);

  protected initiales = initiales;
  protected statuts = STATUTS;
  protected clients = signal<Client[] | null>(null);
  protected recherche = signal('');
  protected form = signal<Client | null>(null);
  protected envoi = signal(false);
  protected historiqueClient = signal<{ client: Client; commandes: Commande[] | null } | null>(null);

  protected filtres = computed(() => {
    const q = this.recherche().trim().toLowerCase();
    return (this.clients() ?? []).filter(
      (c) => !q || `${c.prenom} ${c.nom} ${c.telephone} ${c.email}`.toLowerCase().includes(q),
    );
  });

  constructor() {
    this.api.clients.liste().subscribe({
      next: (l) => this.clients.set(l),
      error: (e) => this.toasts.erreur(e),
    });
  }

  ouvrir(c?: Client): void {
    this.form.set(c ? { ...c } : { nom: '', prenom: '', telephone: '', email: '', address: '' });
  }

  enregistrer(ngf: NgForm): void {
    const f = this.form();
    if (!f) return;
    if (ngf.invalid) {
      ngf.control.markAllAsTouched();
      this.toasts.erreur('Complétez les champs obligatoires.');
      return;
    }
    this.envoi.set(true);
    const appel = f.idClient ? this.api.clients.modifier(f.idClient, f) : this.api.clients.creer(f);
    appel.subscribe({
      next: (c) => {
        this.clients.update((l) =>
          f.idClient ? (l ?? []).map((x) => (x.idClient === c.idClient ? c : x)) : [...(l ?? []), c],
        );
        this.toasts.succes(f.idClient ? 'Client mis à jour' : 'Client ajouté');
        this.form.set(null);
        this.envoi.set(false);
      },
      error: (e) => {
        this.envoi.set(false);
        this.toasts.erreur(e);
      },
    });
  }

  async supprimer(c: Client): Promise<void> {
    const ok = await this.confirm.demander({
      titre: 'Supprimer ce client ?',
      message: `${c.prenom} ${c.nom} sera définitivement supprimé.`,
      libelle: 'Supprimer',
      danger: true,
    });
    if (!ok) return;
    this.api.clients.supprimer(c.idClient!).subscribe({
      next: () => {
        this.clients.update((l) => (l ?? []).filter((x) => x.idClient !== c.idClient));
        this.toasts.succes('Client supprimé');
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  historique(c: Client): void {
    this.historiqueClient.set({ client: c, commandes: null });
    this.api.commandes.duClient(c.idClient!).subscribe({
      next: (commandes) => this.historiqueClient.set({ client: c, commandes }),
      error: (e) => this.toasts.erreur(e),
    });
  }

  totalDepense(commandes: Commande[]): number {
    return commandes.filter((c) => c.statut !== 'ANNULEE').reduce((s, c) => s + (c.montantTotal ?? 0), 0);
  }
}
