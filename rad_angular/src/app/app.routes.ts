import { Routes } from '@angular/router';
import { authGuard, equipeGuard, inviteGuard, roleGuard } from './core/guards';
import { ShellComponent } from './layout/shell.component';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [inviteGuard],
    title: 'Connexion · Le Gourmet',
    loadComponent: () => import('./features/login/login.page').then((m) => m.LoginPage),
  },
  {
    path: 'carte',
    title: 'Notre carte · Le Gourmet',
    loadComponent: () => import('./features/carte/carte.page').then((m) => m.CartePage),
  },
  {
    path: 'suivi',
    title: 'Suivre ma commande · Le Gourmet',
    loadComponent: () => import('./features/suivi/suivi.page').then((m) => m.SuiviPage),
  },
  {
    path: 'suivi/:code',
    title: 'Suivre ma commande · Le Gourmet',
    loadComponent: () => import('./features/suivi/suivi.page').then((m) => m.SuiviPage),
  },
  {
    path: 'livreur',
    title: 'Mes livraisons · Le Gourmet',
    canActivate: [authGuard, roleGuard('LIVREUR')],
    loadComponent: () => import('./features/livreur/livreur.page').then((m) => m.LivreurPage),
  },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard, equipeGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        title: 'Tableau de bord · Le Gourmet',
        loadComponent: () => import('./features/dashboard/dashboard.page').then((m) => m.DashboardPage),
      },
      {
        path: 'commandes',
        title: 'Commandes · Le Gourmet',
        loadComponent: () => import('./features/commandes/commandes.page').then((m) => m.CommandesPage),
      },
      {
        path: 'commandes/nouvelle',
        title: 'Nouvelle commande · Le Gourmet',
        loadComponent: () => import('./features/commandes/caisse.page').then((m) => m.CaissePage),
      },
      {
        path: 'commandes/:id/modifier',
        title: 'Modifier la commande · Le Gourmet',
        loadComponent: () => import('./features/commandes/caisse.page').then((m) => m.CaissePage),
      },
      {
        path: 'livraisons',
        title: 'Livraisons · Le Gourmet',
        loadComponent: () => import('./features/livraisons/livraisons.page').then((m) => m.LivraisonsPage),
      },
      {
        path: 'clients',
        title: 'Clients · Le Gourmet',
        loadComponent: () => import('./features/clients/clients.page').then((m) => m.ClientsPage),
      },
      {
        path: 'plats',
        title: 'Plats · Le Gourmet',
        loadComponent: () => import('./features/catalogue/plats.page').then((m) => m.PlatsPage),
      },
      {
        path: 'categories',
        title: 'Catégories · Le Gourmet',
        loadComponent: () => import('./features/catalogue/categories.page').then((m) => m.CategoriesPage),
      },
      {
        path: 'menus',
        title: 'Menus · Le Gourmet',
        loadComponent: () => import('./features/catalogue/menus.page').then((m) => m.MenusPage),
      },
      {
        path: 'stocks',
        title: 'Stocks · Le Gourmet',
        canActivate: [roleGuard('ADMIN', 'GERANT')],
        loadComponent: () => import('./features/stocks/produits.page').then((m) => m.ProduitsPage),
      },
      {
        path: 'fournisseurs',
        title: 'Fournisseurs · Le Gourmet',
        canActivate: [roleGuard('ADMIN', 'GERANT')],
        loadComponent: () => import('./features/stocks/fournisseurs.page').then((m) => m.FournisseursPage),
      },
      {
        path: 'approvisionnements',
        title: 'Approvisionnements · Le Gourmet',
        canActivate: [roleGuard('ADMIN', 'GERANT')],
        loadComponent: () =>
          import('./features/stocks/approvisionnements.page').then((m) => m.ApprovisionnementsPage),
      },
      {
        path: 'personnel',
        title: 'Personnel · Le Gourmet',
        canActivate: [roleGuard('ADMIN', 'GERANT')],
        loadComponent: () => import('./features/personnel/personnel.page').then((m) => m.PersonnelPage),
      },
      {
        path: 'utilisateurs',
        title: 'Utilisateurs · Le Gourmet',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () => import('./features/admin/utilisateurs.page').then((m) => m.UtilisateursPage),
      },
      {
        path: 'profil',
        title: 'Mon profil · Le Gourmet',
        loadComponent: () => import('./features/profil/profil.page').then((m) => m.ProfilPage),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
