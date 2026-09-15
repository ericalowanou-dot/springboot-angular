import { Routes } from '@angular/router';

// LOGIN
import { LoginComponent } from './pages/login/login.component';

// CATEGORIES
import { CreateCategorieComponent } from './pages/categories/create-categorie/create-categorie.component';
import { ListCategorieComponent } from './pages/categories/list-categorie/list-categorie.component';
import { UpdateCategorieComponent } from './pages/categories/update-categorie/update-categorie.component';

// CLIENTS
import { CreateClientComponent } from './pages/clients/create-client/create-client.component';
import { ListClientComponent } from './pages/clients/list-client/list-client.component';
import { UpdateClientComponent } from './pages/clients/update-client/update-client.component';

// COMMANDES
import { CreateCommandeComponent } from './pages/commandes/create-commande/create-commande.component';
import { ListCommandeComponent } from './pages/commandes/list-commande/list-commande.component';
import { UpdateCommandeComponent } from './pages/commandes/update-commande/update-commande.component';

// MENUS
import { CreateMenuComponent } from './pages/menus/create-menu/create-menu.component';
import { ListMenuComponent } from './pages/menus/list-menu/list-menu.component';
import { UpdateMenuComponent } from './pages/menus/update-menu/update-menu.component';

// PLATS
import { CreatePlatComponent } from './pages/plats/create-plat/create-plat.component';
import { ListPlatComponent } from './pages/plats/list-plat/list-plat.component';
import { UpdatePlatComponent } from './pages/plats/update-plat/update-plat.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: LoginComponent },

  // CATEGORIES
  { path: 'categories', component: ListCategorieComponent },
  { path: 'categories/create', component: CreateCategorieComponent },
  { path: 'categories/update/:id', component: UpdateCategorieComponent },

  // CLIENTS
  { path: 'clients', component: ListClientComponent },
  { path: 'clients/create', component: CreateClientComponent },
  { path: 'clients/update/:id', component: UpdateClientComponent },

  // COMMANDES
  { path: 'commandes', component: ListCommandeComponent },
  { path: 'commandes/create', component: CreateCommandeComponent },
  { path: 'commandes/update/:id', component: UpdateCommandeComponent },

  // MENUS
  { path: 'menus', component: ListMenuComponent },
  { path: 'menus/create', component: CreateMenuComponent },
  { path: 'menus/update/:id', component: UpdateMenuComponent },

  // PLATS
  { path: 'plats', component: ListPlatComponent },
  { path: 'plats/create', component: CreatePlatComponent },
  { path: 'plats/update/:id', component: UpdatePlatComponent },
];
