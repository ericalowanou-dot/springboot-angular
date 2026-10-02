import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, DestroyRef, computed, effect, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { FcfaPipe } from '../../core/format';
import { SuiviCommande } from '../../core/models';
import { PanierService } from '../../core/panier.service';
import { ThemeService } from '../../core/theme.service';
import { IconComponent } from '../../ui/icon.component';

interface Etape {
  libelle: string;
  detail: string;
  icone: string;
}

/** Suivi public d'une commande en ligne à partir de son code. */
@Component({
  selector: 'app-suivi',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, RouterLink, DatePipe, IconComponent, FcfaPipe],
  templateUrl: './suivi.page.html',
  styleUrl: './suivi.page.css',
})
export class SuiviPage {
  private api = inject(ApiService);
  private router = inject(Router);
  private panier = inject(PanierService);
  protected theme = inject(ThemeService);

  /** Paramètre de route :code, et ?nouvelle=1 juste après la commande. */
  readonly code = input<string>();
  readonly nouvelle = input<string>();

  protected saisie = '';
  protected suivi = signal<SuiviCommande | null>(null);
  protected introuvable = signal(false);
  protected chargement = signal(false);

  protected livraison = computed(() => this.suivi()?.type === 'LIVRAISON');
  protected annulee = computed(() => this.suivi()?.statut === 'ANNULEE');
  protected echec = computed(() => this.suivi()?.statutLivraison === 'ECHOUEE');
  protected terminee = computed(() => {
    const s = this.suivi()?.statut;
    return s === 'SERVIE' || s === 'LIVREE' || s === 'ANNULEE';
  });

  protected etapes = computed<Etape[]>(() =>
    this.livraison()
      ? [
          { libelle: 'Commande reçue', detail: 'Le restaurant a bien reçu votre commande', icone: 'receipt' },
          { libelle: 'En préparation', detail: 'Nos cuisiniers s’en occupent', icone: 'flame' },
          { libelle: 'Prête', detail: 'En attente du livreur', icone: 'bag' },
          { libelle: 'En route', detail: 'Le livreur arrive chez vous', icone: 'truck' },
          { libelle: 'Livrée', detail: 'Bon appétit !', icone: 'check' },
        ]
      : [
          { libelle: 'Commande reçue', detail: 'Le restaurant a bien reçu votre commande', icone: 'receipt' },
          { libelle: 'En préparation', detail: 'Nos cuisiniers s’en occupent', icone: 'flame' },
          { libelle: 'Prête à retirer', detail: 'Vous pouvez venir la chercher', icone: 'bag' },
          { libelle: 'Retirée', detail: 'Bon appétit !', icone: 'check' },
        ],
  );

  /** Index de l'étape atteinte dans la liste ci-dessus. */
  protected etapeCourante = computed(() => {
    const s = this.suivi();
    if (!s) return 0;
    const ordre: Record<string, number> = { EN_ATTENTE: 0, EN_PREPARATION: 1, PRETE: 2, SERVIE: 3, LIVREE: 4 };
    let i = ordre[s.statut ?? 'EN_ATTENTE'] ?? 0;
    if (this.livraison() && s.statutLivraison === 'EN_COURS' && i < 3) i = 3;
    return i;
  });

  private minuteur: ReturnType<typeof setInterval> | null = null;

  constructor() {
    effect(() => {
      const code = this.code();
      this.suivi.set(null);
      this.introuvable.set(false);
      if (code) {
        this.saisie = code.toUpperCase();
        this.charger(code);
      }
    });
    // actualisation automatique tant que la commande n'est pas terminée
    this.minuteur = setInterval(() => {
      const code = this.code();
      if (code && this.suivi() && !this.terminee()) this.charger(code, true);
    }, 20_000);
    inject(DestroyRef).onDestroy(() => this.minuteur && clearInterval(this.minuteur));
  }

  rechercher(): void {
    const code = this.saisie.trim().toUpperCase();
    if (code) this.router.navigate(['/suivi', code]);
  }

  charger(code: string, silencieux = false): void {
    if (!silencieux) this.chargement.set(true);
    this.api.enLigne.suivre(code).subscribe({
      next: (s) => {
        this.suivi.set(s);
        this.chargement.set(false);
        if (s.statut === 'SERVIE' || s.statut === 'LIVREE' || s.statut === 'ANNULEE') {
          // commande terminée : plus besoin de la rappeler sur la carte
          if (this.panier.dernierCode() === s.codeSuivi) this.panier.oublierCommande();
        }
      },
      error: (e: HttpErrorResponse) => {
        this.chargement.set(false);
        if (e.status === 404) this.introuvable.set(true);
      },
    });
  }
}
