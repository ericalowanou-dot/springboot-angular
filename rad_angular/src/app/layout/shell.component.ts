import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map } from 'rxjs';
import { AuthService } from '../core/auth.service';
import { ThemeService } from '../core/theme.service';
import { LibellePipe, initiales } from '../core/format';
import { IconComponent } from '../ui/icon.component';

interface Lien {
  route: string;
  libelle: string;
  icone: string;
}

interface Section {
  titre: string;
  liens: Lien[];
  visible: () => boolean;
}

@Component({
  selector: 'app-shell',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, IconComponent, LibellePipe],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css',
})
export class ShellComponent {
  protected auth = inject(AuthService);
  protected theme = inject(ThemeService);
  private router = inject(Router);

  protected menuOuvert = signal(false);
  protected profilOuvert = signal(false);

  protected initiales = computed(() =>
    initiales(this.auth.utilisateur()?.prenom, this.auth.utilisateur()?.nom),
  );

  protected sections: Section[] = [
    {
      titre: 'Service',
      visible: () => true,
      liens: [
        { route: '/dashboard', libelle: 'Tableau de bord', icone: 'dashboard' },
        { route: '/commandes', libelle: 'Commandes', icone: 'receipt' },
        { route: '/livraisons', libelle: 'Livraisons', icone: 'truck' },
        { route: '/clients', libelle: 'Clients', icone: 'users' },
      ],
    },
    {
      titre: 'Carte',
      visible: () => true,
      liens: [
        { route: '/plats', libelle: 'Plats', icone: 'utensils' },
        { route: '/categories', libelle: 'Catégories', icone: 'tag' },
        { route: '/menus', libelle: 'Menus', icone: 'book' },
      ],
    },
    {
      titre: 'Gestion',
      visible: () => this.auth.peutGerer(),
      liens: [
        { route: '/stocks', libelle: 'Stocks', icone: 'package' },
        { route: '/approvisionnements', libelle: 'Approvisionnements', icone: 'clipboard' },
        { route: '/fournisseurs', libelle: 'Fournisseurs', icone: 'store' },
        { route: '/personnel', libelle: 'Personnel', icone: 'chef' },
      ],
    },
    {
      titre: 'Administration',
      visible: () => this.auth.estAdmin(),
      liens: [{ route: '/utilisateurs', libelle: 'Utilisateurs', icone: 'shield' }],
    },
  ];

  /** Titre de la page courante, déduit du lien actif. */
  protected titrePage = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map(() => this.titreDepuisUrl(this.router.url)),
    ),
    { initialValue: this.titreDepuisUrl(this.router.url) },
  );

  constructor() {
    this.router.events.pipe(filter((e) => e instanceof NavigationEnd)).subscribe(() => {
      this.menuOuvert.set(false);
      this.profilOuvert.set(false);
    });
  }

  private titreDepuisUrl(url: string): string {
    if (url.startsWith('/commandes/nouvelle')) return 'Nouvelle commande';
    if (url.startsWith('/profil')) return 'Mon profil';
    for (const s of this.sections) {
      const lien = s.liens.find((l) => url.startsWith(l.route));
      if (lien) return lien.libelle;
    }
    return '';
  }
}
