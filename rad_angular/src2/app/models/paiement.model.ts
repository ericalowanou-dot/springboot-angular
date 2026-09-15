export interface Paiement {
  idPaiement: number;
  datePaiement: string;
  montant: number;
  methode: string; // "CB", "Cash", "Mobile Money", etc.
  idCommande: number;
}
