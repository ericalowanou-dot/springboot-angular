import { Injectable, computed, effect, signal } from '@angular/core';
import { LigneRequete, Menu, Plat } from './models';
import { stockage } from './stockage';

export interface LignePanier {
  /** Identifiant unique de la ligne : « p12 » pour un plat, « m3 » pour une formule. */
  cle: string;
  platId?: number;
  menuId?: number;
  nom: string;
  prix: number;
  categorie?: string | null;
  /** Pour une formule : les plats qu'elle contient. */
  detail?: string | null;
  quantite: number;
}

const CLE_PANIER = 'rad.panier';
const CLE_SUIVI = 'rad.suivi';

export const clePlat = (id: number | undefined) => `p${id}`;
export const cleMenu = (id: number | undefined) => `m${id}`;

/** Panier du client sur la carte publique, conservé dans le navigateur entre deux visites. */
@Injectable({ providedIn: 'root' })
export class PanierService {
  readonly lignes = signal<LignePanier[]>(this.restaurer());
  /** Code de suivi de la dernière commande passée depuis ce navigateur. */
  readonly dernierCode = signal<string | null>(stockage.lire(CLE_SUIVI));

  readonly nbArticles = computed(() => this.lignes().reduce((s, l) => s + l.quantite, 0));
  readonly total = computed(() => this.lignes().reduce((s, l) => s + l.prix * l.quantite, 0));

  constructor() {
    effect(() => stockage.ecrire(CLE_PANIER, JSON.stringify(this.lignes())));
  }

  quantite(cle: string): number {
    return this.lignes().find((l) => l.cle === cle)?.quantite ?? 0;
  }

  ajouter(plat: Plat): void {
    if (!plat.idPlat) return;
    this.ajouterLigne({
      cle: clePlat(plat.idPlat),
      platId: plat.idPlat,
      nom: plat.nom,
      prix: plat.prix,
      categorie: plat.categorie?.nom,
      quantite: 1,
    });
  }

  ajouterMenu(menu: Menu): void {
    if (!menu.idMenu) return;
    this.ajouterLigne({
      cle: cleMenu(menu.idMenu),
      menuId: menu.idMenu,
      nom: menu.nom,
      prix: menu.prix,
      detail: menu.plats.map((p) => p.nom).join(', '),
      quantite: 1,
    });
  }

  changer(cle: string, delta: number): void {
    this.lignes.update((lignes) =>
      lignes
        .map((l) => (l.cle === cle ? { ...l, quantite: Math.min(l.quantite + delta, 50) } : l))
        .filter((l) => l.quantite > 0),
    );
  }

  /** Met à jour noms et prix avec la carte actuelle et retire ce qui n'est plus proposé. */
  synchroniser(plats: Plat[], menus: Menu[]): void {
    const platsDispo = new Map(plats.filter((p) => p.disponible !== false).map((p) => [p.idPlat, p]));
    const menusDispo = new Map(menus.filter((m) => m.commandable !== false).map((m) => [m.idMenu, m]));
    this.lignes.update((lignes) =>
      lignes.flatMap((l) => {
        if (l.menuId) {
          const m = menusDispo.get(l.menuId);
          return m ? [{ ...l, nom: m.nom, prix: m.prix, detail: m.plats.map((p) => p.nom).join(', ') }] : [];
        }
        const p = platsDispo.get(l.platId);
        return p ? [{ ...l, nom: p.nom, prix: p.prix }] : [];
      }),
    );
  }

  requete(): LigneRequete[] {
    return this.lignes().map((l) =>
      l.menuId ? { menuId: l.menuId, quantite: l.quantite } : { platId: l.platId, quantite: l.quantite },
    );
  }

  vider(): void {
    this.lignes.set([]);
  }

  memoriserCommande(code: string): void {
    this.dernierCode.set(code);
    stockage.ecrire(CLE_SUIVI, code);
  }

  oublierCommande(): void {
    this.dernierCode.set(null);
    stockage.supprimer(CLE_SUIVI);
  }

  private ajouterLigne(nouvelle: LignePanier): void {
    this.lignes.update((lignes) =>
      lignes.some((l) => l.cle === nouvelle.cle)
        ? lignes.map((l) => (l.cle === nouvelle.cle ? { ...l, quantite: Math.min(l.quantite + 1, 50) } : l))
        : [...lignes, nouvelle],
    );
  }

  /** Relit le panier enregistré (y compris au format d'une version précédente, sans « cle »). */
  private restaurer(): LignePanier[] {
    try {
      const brut = stockage.lire(CLE_PANIER);
      const lignes = brut ? (JSON.parse(brut) as LignePanier[]) : [];
      return lignes
        .filter((l) => l.platId || l.menuId)
        .map((l) => ({ ...l, cle: l.cle ?? (l.menuId ? cleMenu(l.menuId) : clePlat(l.platId)) }));
    } catch {
      return [];
    }
  }
}
