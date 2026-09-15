export interface Menu {
  idMenu: number;
  nom: string;
  description?: string;
  prix: number;
  plats: number[]; // liste d'idPlat
}
