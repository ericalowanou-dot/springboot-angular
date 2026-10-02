import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { FcfaPipe, ImagePipe, emojiCategorie } from '../../core/format';
import { Categorie, Menu, Plat } from '../../core/models';
import { ThemeService } from '../../core/theme.service';
import { IconComponent } from '../../ui/icon.component';

/** Carte publique du restaurant, consultable sans compte. */
@Component({
  selector: 'app-carte',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, IconComponent, FcfaPipe, ImagePipe],
  templateUrl: './carte.page.html',
  styleUrl: './carte.page.css',
})
export class CartePage {
  private api = inject(ApiService);
  protected auth = inject(AuthService);
  protected theme = inject(ThemeService);

  protected categories = signal<Categorie[]>([]);
  protected plats = signal<Plat[]>([]);
  protected menus = signal<Menu[]>([]);
  protected chargement = signal(true);
  protected erreur = signal(false);
  protected filtre = signal<number | null>(null);

  protected emoji = emojiCategorie;

  protected platsAffiches = computed(() => {
    const f = this.filtre();
    return this.plats().filter(
      (p) => p.disponible !== false && (f === null || p.categorie?.idCategorie === f),
    );
  });

  constructor() {
    forkJoin({
      categories: this.api.categories.liste(),
      plats: this.api.plats.liste(),
      menus: this.api.menus.liste(),
    }).subscribe({
      next: (r) => {
        this.categories.set(r.categories.filter((c) => (c.nombrePlats ?? 0) > 0));
        this.plats.set(r.plats);
        this.menus.set(r.menus);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set(true);
        this.chargement.set(false);
      },
    });
  }
}
