import { Injectable, computed, effect, signal } from '@angular/core';
import { Plat } from './models';
import { stockage } from './stockage';

export interface LignePanier {
  platId: number;
  nom: string;
  prix: number;
  categorie?: string | null;
  imageUrl?: string | null;
  quantite: number;
}

const CLE_PANIER = 'rad.panier';
const CLE_SUIVI = 'rad.suivi';

/** Panier du client sur la carte publique, conservé dans le navigateur entre deux visites. */
@Injectable({ providedIn: 'root' })
export class PanierService {
  readonly lignes = signal<LignePanier[]>(this.lire<LignePanier[]>(CLE_PANIER) ?? []);
  /** Code de suivi de la dernière commande passée depuis ce navigateur. */
  readonly dernierCode = signal<string | null>(stockage.lire(CLE_SUIVI));

  readonly nbArticles = computed(() => this.lignes().reduce((s, l) => s + l.quantite, 0));
  readonly total = computed(() => this.lignes().reduce((s, l) => s + l.prix * l.quantite, 0));

  constructor() {
    effect(() => stockage.ecrire(CLE_PANIER, JSON.stringify(this.lignes())));
  }

  quantite(platId: number | undefined): number {
    return this.lignes().find((l) => l.platId === platId)?.quantite ?? 0;
  }

  ajouter(plat: Plat): void {
    if (!plat.idPlat) return;
    this.lignes.update((lignes) =>
      lignes.some((l) => l.platId === plat.idPlat)
        ? lignes.map((l) => (l.platId === plat.idPlat ? { ...l, quantite: Math.min(l.quantite + 1, 50) } : l))
        : [
            ...lignes,
            {
              platId: plat.idPlat!,
              nom: plat.nom,
              prix: plat.prix,
              categorie: plat.categorie?.nom,
              imageUrl: plat.imageUrl,
              quantite: 1,
            },
          ],
    );
  }

  changer(platId: number, delta: number): void {
    this.lignes.update((lignes) =>
      lignes
        .map((l) => (l.platId === platId ? { ...l, quantite: Math.min(l.quantite + delta, 50) } : l))
        .filter((l) => l.quantite > 0),
    );
  }

  /** Met à jour prix et noms avec la carte actuelle et retire les plats qui n'y sont plus proposés. */
  synchroniser(plats: Plat[]): void {
    const parId = new Map(plats.filter((p) => p.disponible !== false).map((p) => [p.idPlat, p]));
    this.lignes.update((lignes) =>
      lignes
        .filter((l) => parId.has(l.platId))
        .map((l) => ({ ...l, nom: parId.get(l.platId)!.nom, prix: parId.get(l.platId)!.prix })),
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

  private lire<T>(cle: string): T | null {
    try {
      const brut = stockage.lire(cle);
      return brut ? (JSON.parse(brut) as T) : null;
    } catch {
      return null;
    }
  }
}
