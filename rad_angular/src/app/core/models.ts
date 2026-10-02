// Types partagés, alignés sur les réponses JSON de l'API Spring Boot.

export type Role = 'ADMIN' | 'GERANT' | 'EMPLOYE' | 'LIVREUR' | 'CLIENT';

export interface Utilisateur {
  id: number;
  email: string;
  nom: string;
  prenom: string;
  telephone?: string | null;
  role: Role;
  enabled: boolean;
  derniereConnexion?: string | null;
  personnelId?: number | null;
}

export interface UtilisateurRequest {
  email: string;
  nom: string;
  prenom: string;
  telephone?: string | null;
  role: Role;
  password?: string | null;
  enabled?: boolean;
}

export interface AccesLivreurRequest {
  email: string;
  password?: string | null;
  enabled?: boolean;
}

export interface AuthResponse {
  token: string;
  expireDans: number;
  utilisateur: Utilisateur;
}

export interface Categorie {
  idCategorie?: number;
  nom: string;
  description?: string | null;
  nombrePlats?: number;
}

export interface Plat {
  idPlat?: number;
  nom: string;
  prix: number;
  description?: string | null;
  imageUrl?: string | null;
  disponible?: boolean;
  categorie?: Categorie | null;
}

export interface Menu {
  idMenu?: number;
  nom: string;
  description?: string | null;
  prix: number;
  plats: Plat[];
  prixSepare?: number;
}

export interface Client {
  idClient?: number;
  nom: string;
  prenom: string;
  address: string;
  telephone: string;
  email: string;
}

export type StatutCommande = 'EN_ATTENTE' | 'EN_PREPARATION' | 'PRETE' | 'SERVIE' | 'LIVREE' | 'ANNULEE';
export type TypeCommande = 'SUR_PLACE' | 'A_EMPORTER' | 'LIVRAISON';
export type StatutLivraison = 'A_ASSIGNER' | 'ASSIGNEE' | 'EN_COURS' | 'LIVREE' | 'ECHOUEE';

export interface LigneCommande {
  idLigne: number;
  quantite: number;
  prixUnitaire: number;
  sousTotal: number;
  plat: Plat;
}

export interface Paiement {
  idPaiement: number;
  datePaiement: string;
  montant: number;
  methode: string;
  commande?: Commande;
}

export interface Livraison {
  idLivraison: number;
  dateLivraison?: string | null;
  adresseDestination: string;
  heureDepart?: string | null;
  heureFin?: string | null;
  motifEchec?: string | null;
  statut: StatutLivraison | null;
  livreur?: Personnel | null;
  commande?: Commande;
}

export interface Commande {
  idCommande: number;
  dateCommande: string;
  creeLe?: string | null;
  statut: StatutCommande | null;
  type: TypeCommande | null;
  numeroTable?: number | null;
  notes?: string | null;
  montantTotal: number | null;
  client?: Client | null;
  lignes: LigneCommande[];
  paiement?: Paiement | null;
  livraison?: Livraison | null;
  payee: boolean;
}

export interface CommandeRequest {
  clientId?: number | null;
  type: TypeCommande;
  numeroTable?: number | null;
  notes?: string | null;
  adresseLivraison?: string | null;
  lignes: { platId: number; quantite: number }[];
}

export interface Personnel {
  idPersonnel?: number;
  nom: string;
  prenom: string;
  fonction: string;
  telephone?: string | null;
  email?: string | null;
  salaire?: number | null;
  dateEmbauche?: string | null;
  actif?: boolean | null;
}

export interface Fournisseur {
  idFournisseur?: number;
  nom: string;
  contact?: string | null;
  telephone?: string | null;
  email?: string | null;
  adresse?: string | null;
}

export interface Produit {
  idProduit: number;
  nom: string;
  prixUnitaire: number;
  seuil: number;
  unite?: string | null;
  fournisseur?: Fournisseur | null;
  quantiteStock: number;
  enAlerte: boolean;
}

export interface ProduitRequest {
  nom: string;
  prixUnitaire: number;
  seuil: number;
  unite?: string | null;
  fournisseurId?: number | null;
  quantite?: number | null;
}

export type EtatAppro = 'EN_COURS' | 'RECUE' | 'ANNULEE';

export interface Approvisionnement {
  idCmdInt: number;
  date: string;
  dateReception?: string | null;
  etat: EtatAppro;
  montantTotal: number;
  fournisseur: Fournisseur;
  lignes: { idLigneInt: number; quantite: number; prixUnitaire: number; sousTotal: number; produit: Produit }[];
}

export interface ApprovisionnementRequest {
  fournisseurId: number;
  lignes: { produitId: number; quantite: number; prixUnitaire: number }[];
}

export interface Dashboard {
  chiffreAffairesJour: number;
  chiffreAffairesMois: number;
  commandesJour: number;
  commandesEnCours: number;
  totalClients: number;
  totalPlats: number;
  panierMoyen: number;
  variationJour: number;
  livraisonsLivreesJour: number;
  livraisonsEchoueesJour: number;
  ventes7Jours: { date: string; montant: number; commandes: number }[];
  topPlats: { idPlat: number; nom: string; imageUrl?: string | null; quantite: number; montant: number }[];
  repartitionStatuts: Record<string, number>;
  produitsEnAlerte: Produit[];
  dernieresCommandes: Commande[];
}
