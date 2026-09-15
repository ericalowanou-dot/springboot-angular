import { LigneCommande } from "./ligne-commande.model";

export interface Commande {
  idCommande: number;
  date: string;
  etat: string; // ex: "en préparation", "livrée"
  idClient: number;
  lignes: LigneCommande[];
  paiement?: number;  // idPaiement
  livraison?: number; // idLivraison
}
