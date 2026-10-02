# Le Gourmet — gestion de restaurant

Application complète de gestion de restaurant : prise de commande (caisse), suivi cuisine, encaissement,
livraisons, carte (plats, catégories, menus), clients, stocks, fournisseurs, approvisionnements, personnel
et comptes utilisateurs. Elle comprend aussi une carte publique consultable sans compte.

| Partie | Dossier | Technologies |
|---|---|---|
| API REST | [`springboot/`](springboot) | Spring Boot 3.5, Java 17, Spring Security + JWT, JPA/Hibernate, PostgreSQL (H2 en dev) |
| Interface web | [`rad_angular/`](rad_angular) | Angular 20 (standalone, signals, zoneless), CSS maison avec thème clair/sombre |

## Démarrage rapide (sans PostgreSQL)

**1. API**, avec le profil `h2` (base enregistrée dans `springboot/data/`) :

```bash
cd springboot
./gradlew bootRun --args='--spring.profiles.active=h2'
```

L'API écoute sur http://localhost:8081. Au premier démarrage, un jeu de données de démonstration est créé
(plats, menus, clients, commandes des 7 derniers jours, stocks…).

**2. Interface** :

```bash
cd rad_angular
npm install
npm start
```

Puis ouvrir http://localhost:4200.

### Comptes de démonstration

| Rôle | Email | Mot de passe |
|---|---|---|
| Administrateur | admin@restaurant.com | admin123 |
| Gérant | gerant@restaurant.com | gerant123 |
| Employé | employe@restaurant.com | employe123 |

> ⚠️ Ces comptes ne sont créés que si la table des utilisateurs est vide. En production, définissez
> `ADMIN_EMAIL` / `ADMIN_PASSWORD` et mettez `DEMO_DATA=false`.

## Rôles et droits

| Fonctionnalité | Employé | Gérant | Admin |
|---|:-:|:-:|:-:|
| Tableau de bord, commandes, encaissement, clients, livraisons | ✅ | ✅ | ✅ |
| Consultation de la carte | ✅ | ✅ | ✅ |
| Modification de la carte, stocks, fournisseurs, approvisionnements, personnel | | ✅ | ✅ |
| Suppressions | | ✅ | ✅ |
| Gestion des comptes utilisateurs | | | ✅ |

La carte (`GET /api/plats`, `/api/categories`, `/api/menus`) et les images sont publiques.

## Configuration de l'API (variables d'environnement)

| Variable | Défaut (dev) | Rôle |
|---|---|---|
| `DATABASE_URL` | — | URL complète de la base (format Render `postgresql://user:mdp@hôte:5432/base`) ; prioritaire sur les `DB_*` |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `5432` / `rad_db` | Base PostgreSQL |
| `DB_USER` / `DB_PASSWORD` | `postgres` / `postgres` | Identifiants PostgreSQL |
| `JWT_SECRET` | valeur de dev | **Obligatoire en prod**, 32 caractères minimum |
| `JWT_EXPIRATION` | `86400000` (24 h) | Durée de validité du jeton (ms) |
| `CORS_ORIGINS` | `http://localhost:4200,https://*.onrender.com` | Origines autorisées |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | `admin@restaurant.com` / `admin123` | Compte admin initial |
| `DEMO_DATA` | `true` | Insère les données de démo si la base est vide |
| `PORT` | `8081` | Port HTTP |

Aucun secret n'est versionné : tout passe par ces variables.

## API REST (extrait)

Toutes les routes sont préfixées par `/api`. Les erreurs ont toujours le format
`{ "status", "message", "erreurs"?, "horodatage" }`.

| Méthode | Route | Description |
|---|---|---|
| POST | `/auth/login` | Connexion, renvoie le jeton JWT |
| GET | `/auth/me` · PUT `/auth/password` | Profil · changement de mot de passe |
| GET | `/dashboard` | Indicateurs (CA, ventes 7 j, top plats, alertes stock…) |
| CRUD | `/plats`, `/categories`, `/menus`, `/clients`, `/personnel`, `/fournisseurs`, `/users` | Gestion standard (`GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`) |
| POST | `/commandes` | Crée une commande `{ type, clientId?, numeroTable?, adresseLivraison?, notes?, lignes: [{ platId, quantite }] }` |
| PATCH | `/commandes/{id}/statut` | `EN_ATTENTE → EN_PREPARATION → PRETE → SERVIE / LIVREE` (ou `ANNULEE`) |
| POST | `/commandes/{id}/paiement` | Encaissement `{ methode: ESPECES \| CARTE \| MOBILE_MONEY }` |
| GET · PATCH | `/livraisons`, `/livraisons/{id}` | Assignation d'un livreur, changement de statut |
| CRUD + PATCH | `/produits`, `/produits/{id}/stock` | Produits et ajustement de stock |
| POST | `/approvisionnements`, `/{id}/reception`, `/{id}/annulation` | La réception incrémente le stock |
| POST · GET | `/images`, `/images/{id}` | Upload d'image (stockée en base) et affichage |

Règles métier garanties côté serveur :
- le montant d'une commande est **toujours recalculé** à partir des prix en base ;
- le prix de chaque ligne est figé au moment de la commande ;
- une commande payée ne peut être ni annulée ni modifiée, et ne peut pas être payée deux fois ;
- une livraison exige une adresse et un employé de fonction « Livreur » ;
- un client avec des commandes, ou une catégorie contenant des plats, ne peut pas être supprimé.

## Tests

```bash
cd springboot && ./gradlew test        # tests d'intégration (H2 en mémoire)
cd rad_angular && npm run build        # compilation stricte de l'interface
```

## Déploiement sur Render

Le fichier [`render.yaml`](render.yaml) décrit l'infrastructure complète : base PostgreSQL, API en Docker et
frontend statique avec réécriture des routes vers `index.html`. Dans Render : **New → Blueprint**, choisir le
dépôt, puis saisir `ADMIN_PASSWORD`. L'URL de l'API utilisée par le frontend se règle dans
[`rad_angular/src/environments/environment.prod.ts`](rad_angular/src/environments/environment.prod.ts).

Les images des plats sont stockées en base de données, si bien qu'elles survivent aux redéploiements (le disque
des conteneurs Render est éphémère).
