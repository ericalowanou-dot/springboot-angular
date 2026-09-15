import { LigneCommandeInterne } from "./ligne-commande-interne.model";

export interface CommandeInterne {
  idCmdInt: number;
  date: string;
  etat: string;
  idFournisseur: number;
  lignes: LigneCommandeInterne[];
}
