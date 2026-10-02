import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ConfirmService } from '../../core/confirm.service';
import { FcfaPipe, LibellePipe, STATUTS, fcfa, nomClient } from '../../core/format';
import { Livraison } from '../../core/models';
import { ThemeService } from '../../core/theme.service';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

const ACTUALISATION_MS = 30_000;

/** Espace mobile du livreur : ses courses, « Je pars », puis « Livrée » ou « Échec ». */
@Component({
  selector: 'app-livreur',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, DatePipe, IconComponent, ModalComponent, FcfaPipe, LibellePipe],
  templateUrl: './livreur.page.html',
  styleUrl: './livreur.page.css',
})
export class LivreurPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);
  protected auth = inject(AuthService);
  protected theme = inject(ThemeService);

  protected statuts = STATUTS;
  protected nomClient = nomClient;
  protected motifs = [
    'Client absent',
    'Client injoignable',
    'Adresse introuvable',
    'Commande refusée',
    'Problème de paiement',
    'Autre',
  ];

  protected livraisons = signal<Livraison[] | null>(null);
  protected onglet = signal<'actives' | 'terminees'>('actives');
  protected enCours = signal<number | null>(null);
  protected echec = signal<{ livraison: Livraison; motif: string; commentaire: string } | null>(null);

  protected actives = computed(() =>
    (this.livraisons() ?? []).filter((l) => l.statut === 'ASSIGNEE' || l.statut === 'EN_COURS'),
  );
  protected terminees = computed(() =>
    (this.livraisons() ?? []).filter((l) => l.statut === 'LIVREE' || l.statut === 'ECHOUEE'),
  );
  protected aRecuperer = computed(() => this.actives().filter((l) => l.statut === 'ASSIGNEE').length);
  protected enRoute = computed(() => this.actives().filter((l) => l.statut === 'EN_COURS').length);
  protected livrees = computed(() => this.terminees().filter((l) => l.statut === 'LIVREE').length);
  protected affichees = computed(() => (this.onglet() === 'actives' ? this.actives() : this.terminees()));

  constructor() {
    this.charger();
    // les nouvelles assignations apparaissent sans recharger la page
    const minuteur = setInterval(() => this.charger(true), ACTUALISATION_MS);
    inject(DestroyRef).onDestroy(() => clearInterval(minuteur));
  }

  charger(silencieux = false): void {
    this.api.livreur.mesLivraisons().subscribe({
      next: (l) => this.livraisons.set(l),
      error: (e) => {
        if (!silencieux) this.toasts.erreur(e);
      },
    });
  }

  lienCarte(adresse: string): string {
    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(adresse)}`;
  }

  lienTel(tel: string): string {
    return `tel:${tel.replace(/[^\d+]/g, '')}`;
  }

  depart(l: Livraison): void {
    this.executer(l, this.api.livreur.depart(l.idLivraison), 'Bonne route ! 🛵');
  }

  async livree(l: Livraison): Promise<void> {
    const aEncaisser = l.commande && !l.commande.payee;
    const ok = await this.confirm.demander({
      titre: `Commande #${l.commande?.idCommande} livrée ?`,
      message: aEncaisser
        ? `Confirmez la remise au client. N'oubliez pas d'encaisser ${fcfa(l.commande?.montantTotal)}.`
        : 'Confirmez la remise de la commande au client.',
      libelle: 'Oui, livrée',
    });
    if (ok) this.executer(l, this.api.livreur.livree(l.idLivraison), 'Livraison terminée 🎉');
  }

  ouvrirEchec(l: Livraison): void {
    this.echec.set({ livraison: l, motif: '', commentaire: '' });
  }

  validerEchec(): void {
    const e = this.echec();
    if (!e) return;
    if (!e.motif) {
      this.toasts.erreur('Choisissez un motif.');
      return;
    }
    if (e.motif === 'Autre' && !e.commentaire.trim()) {
      this.toasts.erreur('Précisez ce qui s’est passé.');
      return;
    }
    this.executer(
      e.livraison,
      this.api.livreur.echec(e.livraison.idLivraison, e.motif, e.commentaire.trim() || null),
      'Échec signalé au restaurant',
    );
    this.echec.set(null);
  }

  private executer(l: Livraison, appel: ReturnType<ApiService['livreur']['depart']>, message: string): void {
    this.enCours.set(l.idLivraison);
    appel.subscribe({
      next: (maj) => {
        this.livraisons.update((liste) => (liste ?? []).map((x) => (x.idLivraison === maj.idLivraison ? maj : x)));
        this.enCours.set(null);
        this.toasts.succes(message);
      },
      error: (err) => {
        this.enCours.set(null);
        this.toasts.erreur(err);
        this.charger(true);
      },
    });
  }
}
