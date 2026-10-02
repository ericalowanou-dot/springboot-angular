import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ConfirmService } from '../../core/confirm.service';
import { FcfaPipe, LibellePipe, STATUTS, lienItineraire, nomClient } from '../../core/format';
import { Commande, StatutCommande } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

type Filtre = 'TOUTES' | 'EN_COURS' | 'EN_ATTENTE' | 'EN_PREPARATION' | 'PRETE' | 'TERMINEES' | 'ANNULEE';

const FILTRES: { id: Filtre; libelle: string; test: (c: Commande) => boolean }[] = [
  { id: 'TOUTES', libelle: 'Toutes', test: () => true },
  {
    id: 'EN_COURS',
    libelle: 'En cours',
    test: (c) => ['EN_ATTENTE', 'EN_PREPARATION', 'PRETE'].includes(c.statut ?? ''),
  },
  { id: 'EN_ATTENTE', libelle: 'En attente', test: (c) => c.statut === 'EN_ATTENTE' },
  { id: 'EN_PREPARATION', libelle: 'En préparation', test: (c) => c.statut === 'EN_PREPARATION' },
  { id: 'PRETE', libelle: 'Prêtes', test: (c) => c.statut === 'PRETE' },
  { id: 'TERMINEES', libelle: 'Terminées', test: (c) => c.statut === 'SERVIE' || c.statut === 'LIVREE' },
  { id: 'ANNULEE', libelle: 'Annulées', test: (c) => c.statut === 'ANNULEE' },
];

@Component({
  selector: 'app-commandes',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, RouterLink, DatePipe, IconComponent, ModalComponent, FcfaPipe, LibellePipe],
  templateUrl: './commandes.page.html',
  styleUrl: './commandes.page.css',
})
export class CommandesPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);
  private router = inject(Router);
  protected auth = inject(AuthService);

  protected statuts = STATUTS;
  protected nomClient = nomClient;
  protected lienItineraire = lienItineraire;
  protected filtres = FILTRES;
  protected commandes = signal<Commande[] | null>(null);
  protected filtre = signal<Filtre>('EN_COURS');
  protected recherche = signal('');
  protected selection = signal<Commande | null>(null);
  protected enCours = signal(false);
  protected methode = signal('ESPECES');

  protected compteurs = computed(() => {
    const liste = this.commandes() ?? [];
    return Object.fromEntries(FILTRES.map((f) => [f.id, liste.filter(f.test).length])) as Record<Filtre, number>;
  });

  protected affichees = computed(() => {
    const f = FILTRES.find((x) => x.id === this.filtre())!;
    const q = this.recherche().trim().toLowerCase();
    return (this.commandes() ?? []).filter((c) => {
      if (!f.test(c)) return false;
      if (!q) return true;
      const client = c.client ? `${c.client.prenom} ${c.client.nom}`.toLowerCase() : '';
      return String(c.idCommande).includes(q) || client.includes(q);
    });
  });

  constructor() {
    this.charger();
    // les commandes passées en ligne arrivent sans recharger la page
    const minuteur = setInterval(() => this.charger(true), 30_000);
    inject(DestroyRef).onDestroy(() => clearInterval(minuteur));
  }

  charger(silencieux = false): void {
    const avant = new Set((this.commandes() ?? []).map((c) => c.idCommande));
    this.api.commandes.liste().subscribe({
      next: (l) => {
        const nouvelles = this.commandes() ? l.filter((c) => c.enLigne && !avant.has(c.idCommande)) : [];
        if (nouvelles.length) {
          this.toasts.info(`🛎️ ${nouvelles.length} nouvelle(s) commande(s) en ligne`);
        }
        this.commandes.set(l);
        const sel = this.selection();
        if (sel) this.selection.set(l.find((c) => c.idCommande === sel.idCommande) ?? null);
      },
      error: (e) => {
        if (!silencieux) this.toasts.erreur(e);
      },
    });
  }

  /** Étape suivante du cycle de vie, selon le type de commande. */
  prochaineEtape(c: Commande): { statut: StatutCommande; libelle: string } | null {
    switch (c.statut) {
      case 'EN_ATTENTE':
        return { statut: 'EN_PREPARATION', libelle: 'Lancer la préparation' };
      case 'EN_PREPARATION':
        return { statut: 'PRETE', libelle: 'Marquer comme prête' };
      case 'PRETE':
        return c.type === 'LIVRAISON'
          ? { statut: 'LIVREE', libelle: 'Marquer comme livrée' }
          : { statut: 'SERVIE', libelle: 'Marquer comme servie' };
      default:
        return null;
    }
  }

  changerStatut(c: Commande, statut: StatutCommande): void {
    this.enCours.set(true);
    this.api.commandes.statut(c.idCommande, statut).subscribe({
      next: (maj) => {
        this.remplacer(maj);
        this.toasts.succes(`Commande #${maj.idCommande} : ${STATUTS[statut]?.label.toLowerCase()}`);
      },
      error: (e) => this.finErreur(e),
    });
  }

  async annuler(c: Commande): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Annuler la commande #${c.idCommande} ?`,
      message: 'La commande sera conservée dans l’historique avec le statut « Annulée ».',
      libelle: 'Annuler la commande',
      danger: true,
    });
    if (ok) this.changerStatut(c, 'ANNULEE');
  }

  payer(c: Commande): void {
    this.enCours.set(true);
    this.api.commandes.payer(c.idCommande, this.methode()).subscribe({
      next: (maj) => {
        this.remplacer(maj);
        this.toasts.succes(`Paiement de la commande #${maj.idCommande} enregistré`);
      },
      error: (e) => this.finErreur(e),
    });
  }

  async supprimer(c: Commande): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Supprimer la commande #${c.idCommande} ?`,
      message: 'Cette action est définitive : la commande, son paiement et sa livraison seront effacés.',
      libelle: 'Supprimer',
      danger: true,
    });
    if (!ok) return;
    this.api.commandes.supprimer(c.idCommande).subscribe({
      next: () => {
        this.commandes.update((l) => (l ?? []).filter((x) => x.idCommande !== c.idCommande));
        this.selection.set(null);
        this.toasts.succes('Commande supprimée');
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  modifier(c: Commande): void {
    this.router.navigate(['/commandes', c.idCommande, 'modifier']);
  }

  platsDuMenu(menu: { plats?: { nom: string }[] }): string {
    return (menu.plats ?? []).map((p) => p.nom).join(', ');
  }

  imprimer(): void {
    window.print();
  }

  private remplacer(maj: Commande): void {
    this.commandes.update((l) => (l ?? []).map((c) => (c.idCommande === maj.idCommande ? maj : c)));
    if (this.selection()?.idCommande === maj.idCommande) this.selection.set(maj);
    this.enCours.set(false);
  }

  private finErreur(e: unknown): void {
    this.enCours.set(false);
    this.toasts.erreur(e);
  }
}
