import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable, forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ConfirmService } from '../../core/confirm.service';
import { FcfaPipe, LibellePipe, STATUTS, fcfa, lienItineraire, nomClient } from '../../core/format';
import { Livraison } from '../../core/models';
import { ThemeService } from '../../core/theme.service';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

const ACTUALISATION_MS = 20_000;

type Onglet = 'disponibles' | 'actives' | 'terminees';

/**
 * Espace mobile du livreur :
 * - « Disponibles » : courses prêtes que personne n'a prises (visibles par tous les livreurs) ;
 * - « Mes courses » : « Je pars », puis « Livrée » ou « Échec » ;
 * - « Terminées » : bilan du jour.
 */
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
  protected lienItineraire = lienItineraire;
  protected motifs = [
    'Client absent',
    'Client injoignable',
    'Adresse introuvable',
    'Commande refusée',
    'Problème de paiement',
    'Autre',
  ];

  protected livraisons = signal<Livraison[] | null>(null);
  protected disponibles = signal<Livraison[]>([]);
  protected onglet = signal<Onglet>('actives');
  protected enCours = signal<number | null>(null);
  protected echec = signal<{ livraison: Livraison; motif: string; commentaire: string } | null>(null);

  protected actives = computed(() =>
    (this.livraisons() ?? []).filter((l) => l.statut === 'ASSIGNEE' || l.statut === 'EN_COURS'),
  );
  protected terminees = computed(() =>
    (this.livraisons() ?? []).filter((l) => l.statut === 'LIVREE' || l.statut === 'ECHOUEE'),
  );
  protected enRoute = computed(() => this.actives().filter((l) => l.statut === 'EN_COURS').length);
  protected livrees = computed(() => this.terminees().filter((l) => l.statut === 'LIVREE').length);
  protected affichees = computed(() => {
    switch (this.onglet()) {
      case 'disponibles':
        return this.disponibles();
      case 'terminees':
        return this.terminees();
      default:
        return this.actives();
    }
  });

  private premierChargement = true;

  constructor() {
    this.charger();
    // nouvelles courses et assignations sans recharger la page
    const minuteur = setInterval(() => this.charger(true), ACTUALISATION_MS);
    inject(DestroyRef).onDestroy(() => clearInterval(minuteur));
  }

  charger(silencieux = false): void {
    const connues = new Set(this.disponibles().map((l) => l.idLivraison));
    forkJoin({ miennes: this.api.livreur.mesLivraisons(), libres: this.api.livreur.disponibles() }).subscribe({
      next: ({ miennes, libres }) => {
        const nouvelles = libres.filter((l) => !connues.has(l.idLivraison));
        this.livraisons.set(miennes);
        this.disponibles.set(libres);
        if (this.premierChargement) {
          this.premierChargement = false;
          if (!miennes.some((l) => l.statut === 'ASSIGNEE' || l.statut === 'EN_COURS') && libres.length) {
            this.onglet.set('disponibles');
          }
        } else if (nouvelles.length) {
          this.toasts.info(`🛵 ${nouvelles.length} nouvelle(s) course(s) disponible(s)`);
          navigator.vibrate?.(200);
        }
      },
      error: (e) => {
        if (!silencieux) this.toasts.erreur(e);
      },
    });
  }

  lienTel(tel: string): string {
    return `tel:${tel.replace(/[^\d+]/g, '')}`;
  }

  prendre(l: Livraison): void {
    this.enCours.set(l.idLivraison);
    this.api.livreur.prendre(l.idLivraison).subscribe({
      next: (maj) => {
        this.disponibles.update((liste) => liste.filter((x) => x.idLivraison !== maj.idLivraison));
        this.livraisons.update((liste) => [maj, ...(liste ?? [])]);
        this.enCours.set(null);
        this.onglet.set('actives');
        this.toasts.succes(`Course #${maj.commande?.idCommande} pour vous : récupérez-la au restaurant`);
      },
      error: (err) => {
        this.enCours.set(null);
        this.toasts.erreur(err);
        this.charger(true); // elle a sans doute été prise par un autre livreur
      },
    });
  }

  async liberer(l: Livraison): Promise<void> {
    const ok = await this.confirm.demander({
      titre: 'Libérer cette course ?',
      message: 'Elle redeviendra disponible pour les autres livreurs.',
      libelle: 'Oui, la libérer',
      danger: true,
    });
    if (!ok) return;
    this.enCours.set(l.idLivraison);
    this.api.livreur.liberer(l.idLivraison).subscribe({
      next: () => {
        this.enCours.set(null);
        this.toasts.succes('Course libérée');
        this.charger(true);
      },
      error: (err) => {
        this.enCours.set(null);
        this.toasts.erreur(err);
        this.charger(true);
      },
    });
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

  private executer(l: Livraison, appel: Observable<Livraison>, message: string): void {
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
