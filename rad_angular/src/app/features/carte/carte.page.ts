import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { FcfaPipe, ImagePipe, emojiCategorie } from '../../core/format';
import { Categorie, Menu, Plat } from '../../core/models';
import { PanierService, cleMenu, clePlat } from '../../core/panier.service';
import { stockage } from '../../core/stockage';
import { ThemeService } from '../../core/theme.service';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

const CLE_COORDONNEES = 'rad.coordonnees';

interface Coordonnees {
  type: 'A_EMPORTER' | 'LIVRAISON';
  nom: string;
  telephone: string;
  adresse: string;
  notes: string;
}

/** Carte publique : consultation, panier et commande en ligne sans compte. */
@Component({
  selector: 'app-carte',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, RouterLink, IconComponent, ModalComponent, FcfaPipe, ImagePipe],
  templateUrl: './carte.page.html',
  styleUrl: './carte.page.css',
})
export class CartePage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private router = inject(Router);
  protected auth = inject(AuthService);
  protected theme = inject(ThemeService);
  protected panier = inject(PanierService);

  protected categories = signal<Categorie[]>([]);
  protected plats = signal<Plat[]>([]);
  protected menus = signal<Menu[]>([]);
  protected chargement = signal(true);
  protected erreur = signal(false);
  protected filtre = signal<number | null>(null);
  protected panierOuvert = signal(false);
  protected envoi = signal(false);

  // coordonnées pré-remplies avec celles de la dernière commande (sur cet appareil uniquement)
  protected coord: Coordonnees = this.coordonneesMemorisees();

  protected emoji = emojiCategorie;
  protected clePlat = clePlat;
  protected cleMenu = cleMenu;

  protected platsAffiches = computed(() => {
    const f = this.filtre();
    return this.plats().filter(
      (p) => p.disponible !== false && (f === null || p.categorie?.idCategorie === f),
    );
  });

  constructor() {
    forkJoin({
      categories: this.api.categories.liste(),
      plats: this.api.plats.liste(),
      menus: this.api.menus.liste(),
    }).subscribe({
      next: (r) => {
        this.categories.set(r.categories.filter((c) => (c.nombrePlats ?? 0) > 0));
        this.plats.set(r.plats);
        this.menus.set(r.menus);
        this.panier.synchroniser(r.plats, r.menus);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set(true);
        this.chargement.set(false);
      },
    });
  }

  ajouter(p: Plat): void {
    this.panier.ajouter(p);
  }

  commander(ngf: NgForm): void {
    if (ngf.invalid) {
      ngf.control.markAllAsTouched();
      this.toasts.erreur('Indiquez votre nom et un numéro de téléphone valide.');
      return;
    }
    if (this.coord.type === 'LIVRAISON' && !this.coord.adresse.trim()) {
      this.toasts.erreur('Indiquez l’adresse de livraison.');
      return;
    }
    if (!this.panier.lignes().length) return;

    this.envoi.set(true);
    this.api.enLigne
      .commander({
        type: this.coord.type,
        nom: this.coord.nom.trim(),
        telephone: this.coord.telephone.trim(),
        adresse: this.coord.type === 'LIVRAISON' ? this.coord.adresse.trim() : null,
        notes: this.coord.notes.trim() || null,
        lignes: this.panier.requete(),
      })
      .subscribe({
        next: (r) => {
          stockage.ecrire(CLE_COORDONNEES, JSON.stringify({ ...this.coord, notes: '' }));
          this.panier.vider();
          this.panier.memoriserCommande(r.codeSuivi);
          this.panierOuvert.set(false);
          this.envoi.set(false);
          this.router.navigate(['/suivi', r.codeSuivi], { queryParams: { nouvelle: 1 } });
        },
        error: (e) => {
          this.envoi.set(false);
          this.toasts.erreur(e);
        },
      });
  }

  private coordonneesMemorisees(): Coordonnees {
    const vide: Coordonnees = { type: 'A_EMPORTER', nom: '', telephone: '', adresse: '', notes: '' };
    try {
      const brut = stockage.lire(CLE_COORDONNEES);
      return brut ? { ...vide, ...JSON.parse(brut), notes: '' } : vide;
    } catch {
      return vide;
    }
  }
}
