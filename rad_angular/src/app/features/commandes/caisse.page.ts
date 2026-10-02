import { ChangeDetectionStrategy, Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { FcfaPipe, ImagePipe, emojiCategorie } from '../../core/format';
import { Categorie, Client, Menu, Plat, TypeCommande } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';

interface LignePanier {
  plat: Plat;
  quantite: number;
}

/** Prise de commande façon caisse : grille de plats à gauche, ticket à droite. Sert aussi à la modification. */
@Component({
  selector: 'app-caisse',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, RouterLink, IconComponent, FcfaPipe, ImagePipe],
  templateUrl: './caisse.page.html',
  styleUrl: './caisse.page.css',
})
export class CaissePage implements OnInit {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private router = inject(Router);

  /** Présent en mode modification (route commandes/:id/modifier). */
  readonly id = input<string>();

  protected emoji = emojiCategorie;
  protected types: { id: TypeCommande; libelle: string; icone: string }[] = [
    { id: 'SUR_PLACE', libelle: 'Sur place', icone: 'table' },
    { id: 'A_EMPORTER', libelle: 'À emporter', icone: 'bag' },
    { id: 'LIVRAISON', libelle: 'Livraison', icone: 'truck' },
  ];

  protected plats = signal<Plat[]>([]);
  protected categories = signal<Categorie[]>([]);
  protected menus = signal<Menu[]>([]);
  protected clients = signal<Client[]>([]);
  protected chargement = signal(true);
  protected envoi = signal(false);

  protected categorie = signal<number | null>(null);
  protected recherche = signal('');
  protected panier = signal<LignePanier[]>([]);
  protected type = signal<TypeCommande>('SUR_PLACE');
  protected panierOuvert = signal(false);

  // champs simples liés par ngModel
  protected numeroTable: number | null = null;
  protected clientId: number | null = null;
  protected adresse = '';
  protected notes = '';

  protected platsAffiches = computed(() => {
    const c = this.categorie();
    const q = this.recherche().trim().toLowerCase();
    return this.plats().filter(
      (p) =>
        (c === null || p.categorie?.idCategorie === c) && (!q || p.nom.toLowerCase().includes(q)),
    );
  });

  protected total = computed(() =>
    this.panier().reduce((s, l) => s + l.plat.prix * l.quantite, 0),
  );
  protected nbArticles = computed(() => this.panier().reduce((s, l) => s + l.quantite, 0));

  ngOnInit(): void {
    const id = this.id();
    forkJoin({
      plats: this.api.plats.liste(),
      categories: this.api.categories.liste(),
      menus: this.api.menus.liste(),
      clients: this.api.clients.liste(),
    }).subscribe({
      next: (r) => {
        this.plats.set(r.plats);
        this.categories.set(r.categories);
        this.menus.set(r.menus);
        this.clients.set(r.clients);
        if (id) {
          this.chargerCommande(Number(id));
        } else {
          this.chargement.set(false);
        }
      },
      error: (e) => {
        this.toasts.erreur(e);
        this.chargement.set(false);
      },
    });
  }

  private chargerCommande(id: number): void {
    this.api.commandes.un(id).subscribe({
      next: (c) => {
        this.type.set(c.type ?? 'SUR_PLACE');
        this.numeroTable = c.numeroTable ?? null;
        this.clientId = c.client?.idClient ?? null;
        this.adresse = c.livraison?.adresseDestination ?? '';
        this.notes = c.notes ?? '';
        this.panier.set(
          c.lignes.map((l) => ({
            plat: this.plats().find((p) => p.idPlat === l.plat.idPlat) ?? l.plat,
            quantite: l.quantite,
          })),
        );
        this.chargement.set(false);
      },
      error: (e) => {
        this.toasts.erreur(e);
        this.router.navigate(['/commandes']);
      },
    });
  }

  ajouter(plat: Plat): void {
    if (plat.disponible === false) return;
    this.panier.update((lignes) => {
      const existante = lignes.find((l) => l.plat.idPlat === plat.idPlat);
      return existante
        ? lignes.map((l) => (l === existante ? { ...l, quantite: l.quantite + 1 } : l))
        : [...lignes, { plat, quantite: 1 }];
    });
  }

  /** Un menu ajoute chacun de ses plats (le prix appliqué reste celui des plats). */
  ajouterMenu(menu: Menu): void {
    menu.plats.forEach((p) => this.ajouter(this.plats().find((x) => x.idPlat === p.idPlat) ?? p));
    this.toasts.info(`${menu.nom} ajouté au ticket`);
  }

  changerQuantite(ligne: LignePanier, delta: number): void {
    this.panier.update((lignes) =>
      lignes
        .map((l) => (l === ligne ? { ...l, quantite: l.quantite + delta } : l))
        .filter((l) => l.quantite > 0),
    );
  }

  quantiteDe(plat: Plat): number {
    return this.panier().find((l) => l.plat.idPlat === plat.idPlat)?.quantite ?? 0;
  }

  vider(): void {
    this.panier.set([]);
  }

  choisirClient(id: number | null): void {
    this.clientId = id;
    const client = this.clients().find((c) => c.idClient === id);
    if (client && !this.adresse) this.adresse = client.address;
  }

  valider(): void {
    if (!this.panier().length) {
      this.toasts.erreur('Ajoutez au moins un plat.');
      return;
    }
    if (this.type() === 'LIVRAISON' && !this.adresse.trim()) {
      this.toasts.erreur('Indiquez une adresse de livraison.');
      return;
    }
    const requete = {
      type: this.type(),
      clientId: this.clientId,
      numeroTable: this.type() === 'SUR_PLACE' ? this.numeroTable : null,
      adresseLivraison: this.type() === 'LIVRAISON' ? this.adresse.trim() : null,
      notes: this.notes.trim() || null,
      lignes: this.panier().map((l) => ({ platId: l.plat.idPlat!, quantite: l.quantite })),
    };
    const id = this.id();
    this.envoi.set(true);
    const appel = id ? this.api.commandes.modifier(Number(id), requete) : this.api.commandes.creer(requete);
    appel.subscribe({
      next: (c) => {
        this.toasts.succes(id ? `Commande #${c.idCommande} mise à jour` : `Commande #${c.idCommande} enregistrée`);
        this.router.navigate(['/commandes']);
      },
      error: (e) => {
        this.envoi.set(false);
        this.toasts.erreur(e);
      },
    });
  }
}
