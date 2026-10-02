import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AccesLivreurRequest,
  Approvisionnement,
  ApprovisionnementRequest,
  Categorie,
  Client,
  Commande,
  CommandeEnLigneRequest,
  CommandeRequest,
  Dashboard,
  Fournisseur,
  Livraison,
  Menu,
  Personnel,
  Plat,
  Produit,
  ProduitRequest,
  StatutCommande,
  StatutLivraison,
  SuiviCommande,
  Utilisateur,
  UtilisateurRequest,
} from './models';

/** CRUD REST standard : GET /x, GET /x/:id, POST /x, PUT /x/:id, DELETE /x/:id. */
class Ressource<T, R = T> {
  constructor(
    protected http: HttpClient,
    protected url: string,
  ) {}

  liste(params?: Record<string, string | number>): Observable<T[]> {
    return this.http.get<T[]>(this.url, { params: new HttpParams({ fromObject: params ?? {} }) });
  }

  un(id: number): Observable<T> {
    return this.http.get<T>(`${this.url}/${id}`);
  }

  creer(data: R): Observable<T> {
    return this.http.post<T>(this.url, data);
  }

  modifier(id: number, data: R): Observable<T> {
    return this.http.put<T>(`${this.url}/${id}`, data);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api`;

  readonly categories = new Ressource<Categorie>(this.http, `${this.base}/categories`);
  readonly menus = new Ressource<Menu>(this.http, `${this.base}/menus`);
  readonly clients = new Ressource<Client>(this.http, `${this.base}/clients`);
  readonly personnel = new Ressource<Personnel>(this.http, `${this.base}/personnel`);
  readonly fournisseurs = new Ressource<Fournisseur>(this.http, `${this.base}/fournisseurs`);
  readonly utilisateurs = new Ressource<Utilisateur, UtilisateurRequest>(this.http, `${this.base}/users`);

  readonly plats = Object.assign(new Ressource<Plat>(this.http, `${this.base}/plats`), {
    disponibilite: (id: number, disponible: boolean) =>
      this.http.patch<Plat>(`${this.base}/plats/${id}/disponibilite`, null, {
        params: { disponible },
      }),
  });

  readonly commandes = Object.assign(
    new Ressource<Commande, CommandeRequest>(this.http, `${this.base}/commandes`),
    {
      statut: (id: number, statut: StatutCommande) =>
        this.http.patch<Commande>(`${this.base}/commandes/${id}/statut`, { statut }),
      payer: (id: number, methode: string) =>
        this.http.post<Commande>(`${this.base}/commandes/${id}/paiement`, { methode }),
      duClient: (idClient: number) =>
        this.http.get<Commande[]>(`${this.base}/clients/${idClient}/commandes`),
    },
  );

  readonly livraisons = {
    liste: () => this.http.get<Livraison[]>(`${this.base}/livraisons`),
    maj: (id: number, data: { livreurId?: number | null; statut?: StatutLivraison | null; motif?: string | null }) =>
      this.http.patch<Livraison>(`${this.base}/livraisons/${id}`, data),
  };

  /** Espace du livreur connecté. */
  readonly livreur = {
    mesLivraisons: () => this.http.get<Livraison[]>(`${this.base}/livreur/livraisons`),
    disponibles: () => this.http.get<Livraison[]>(`${this.base}/livreur/livraisons/disponibles`),
    prendre: (id: number) => this.http.post<Livraison>(`${this.base}/livreur/livraisons/${id}/prendre`, null),
    liberer: (id: number) => this.http.post<Livraison>(`${this.base}/livreur/livraisons/${id}/liberer`, null),
    depart: (id: number) => this.http.post<Livraison>(`${this.base}/livreur/livraisons/${id}/depart`, null),
    livree: (id: number) => this.http.post<Livraison>(`${this.base}/livreur/livraisons/${id}/livree`, null),
    echec: (id: number, motif: string, commentaire: string | null) =>
      this.http.post<Livraison>(`${this.base}/livreur/livraisons/${id}/echec`, { motif, commentaire }),
  };

  /** Accès de connexion des livreurs, gérés depuis la page Personnel. */
  readonly accesLivreur = {
    tous: () => this.http.get<Record<number, Utilisateur>>(`${this.base}/personnel/acces`),
    creer: (idPersonnel: number, data: AccesLivreurRequest) =>
      this.http.post<Utilisateur>(`${this.base}/personnel/${idPersonnel}/acces`, data),
    modifier: (idPersonnel: number, data: AccesLivreurRequest) =>
      this.http.put<Utilisateur>(`${this.base}/personnel/${idPersonnel}/acces`, data),
    supprimer: (idPersonnel: number) => this.http.delete<void>(`${this.base}/personnel/${idPersonnel}/acces`),
  };

  readonly produits = Object.assign(
    new Ressource<Produit, ProduitRequest>(this.http, `${this.base}/produits`),
    {
      stock: (id: number, quantite: number) =>
        this.http.patch<Produit>(`${this.base}/produits/${id}/stock`, { quantite }),
    },
  );

  readonly approvisionnements = Object.assign(
    new Ressource<Approvisionnement, ApprovisionnementRequest>(
      this.http,
      `${this.base}/approvisionnements`,
    ),
    {
      recevoir: (id: number) =>
        this.http.post<Approvisionnement>(`${this.base}/approvisionnements/${id}/reception`, null),
      annuler: (id: number) =>
        this.http.post<Approvisionnement>(`${this.base}/approvisionnements/${id}/annulation`, null),
    },
  );

  /** Routes publiques : commande en ligne par un client et suivi par code. */
  readonly enLigne = {
    commander: (data: CommandeEnLigneRequest) =>
      this.http.post<{ codeSuivi: string; numero: number; montantTotal: number }>(
        `${this.base}/public/commandes`,
        data,
      ),
    suivre: (code: string) =>
      this.http.get<SuiviCommande>(`${this.base}/public/commandes/${encodeURIComponent(code)}`),
  };

  dashboard(): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${this.base}/dashboard`);
  }

  uploadImage(fichier: File): Observable<{ imageUrl: string }> {
    const form = new FormData();
    form.append('file', fichier);
    return this.http.post<{ imageUrl: string }>(`${this.base}/images`, form);
  }

  changerMotDePasse(ancienMotDePasse: string, nouveauMotDePasse: string): Observable<void> {
    return this.http.put<void>(`${this.base}/auth/password`, { ancienMotDePasse, nouveauMotDePasse });
  }
}
