import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ConfirmService } from '../../core/confirm.service';
import { FcfaPipe, ImagePipe, emojiCategorie } from '../../core/format';
import { Categorie, Plat } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';
import { ModalComponent } from '../../ui/modal.component';

interface FormPlat {
  idPlat?: number;
  nom: string;
  prix: number | null;
  description: string;
  imageUrl: string | null;
  disponible: boolean;
  categorieId: number | null;
}

@Component({
  selector: 'app-plats',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, IconComponent, ModalComponent, FcfaPipe, ImagePipe],
  templateUrl: './plats.page.html',
  styleUrl: './plats.page.css',
})
export class PlatsPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  private confirm = inject(ConfirmService);
  protected auth = inject(AuthService);

  protected emoji = emojiCategorie;
  protected plats = signal<Plat[] | null>(null);
  protected categories = signal<Categorie[]>([]);
  protected categorie = signal<number | null>(null);
  protected recherche = signal('');
  protected form = signal<FormPlat | null>(null);
  protected envoi = signal(false);
  protected upload = signal(false);

  protected affiches = computed(() => {
    const c = this.categorie();
    const q = this.recherche().trim().toLowerCase();
    return (this.plats() ?? []).filter(
      (p) => (c === null || p.categorie?.idCategorie === c) && (!q || p.nom.toLowerCase().includes(q)),
    );
  });

  constructor() {
    forkJoin({ plats: this.api.plats.liste(), categories: this.api.categories.liste() }).subscribe({
      next: (r) => {
        this.plats.set(r.plats);
        this.categories.set(r.categories);
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  ouvrir(p?: Plat): void {
    this.form.set({
      idPlat: p?.idPlat,
      nom: p?.nom ?? '',
      prix: p?.prix ?? null,
      description: p?.description ?? '',
      imageUrl: p?.imageUrl ?? null,
      disponible: p?.disponible !== false,
      categorieId: p?.categorie?.idCategorie ?? this.categorie() ?? null,
    });
  }

  choisirImage(event: Event, f: FormPlat): void {
    const fichier = (event.target as HTMLInputElement).files?.[0];
    if (!fichier) return;
    if (fichier.size > 5 * 1024 * 1024) {
      this.toasts.erreur('Image trop volumineuse (5 Mo maximum).');
      return;
    }
    this.upload.set(true);
    this.api.uploadImage(fichier).subscribe({
      next: (r) => {
        this.form.set({ ...f, imageUrl: r.imageUrl });
        this.upload.set(false);
      },
      error: (e) => {
        this.upload.set(false);
        this.toasts.erreur(e);
      },
    });
  }

  retirerImage(f: FormPlat): void {
    this.form.set({ ...f, imageUrl: null });
  }

  enregistrer(ngf: NgForm): void {
    const f = this.form();
    if (!f) return;
    if (ngf.invalid || f.prix === null) {
      ngf.control.markAllAsTouched();
      this.toasts.erreur('Renseignez au moins le nom et le prix.');
      return;
    }
    const plat: Plat = {
      nom: f.nom.trim(),
      prix: f.prix,
      description: f.description.trim() || null,
      imageUrl: f.imageUrl,
      disponible: f.disponible,
      categorie: f.categorieId ? { idCategorie: f.categorieId, nom: '' } : null,
    };
    this.envoi.set(true);
    const appel = f.idPlat ? this.api.plats.modifier(f.idPlat, plat) : this.api.plats.creer(plat);
    appel.subscribe({
      next: (p) => {
        this.plats.update((l) =>
          f.idPlat ? (l ?? []).map((x) => (x.idPlat === p.idPlat ? p : x)) : [...(l ?? []), p],
        );
        this.toasts.succes(f.idPlat ? 'Plat mis à jour' : 'Plat ajouté à la carte');
        this.form.set(null);
        this.envoi.set(false);
      },
      error: (e) => {
        this.envoi.set(false);
        this.toasts.erreur(e);
      },
    });
  }

  basculerDisponibilite(p: Plat): void {
    this.api.plats.disponibilite(p.idPlat!, p.disponible === false).subscribe({
      next: (maj) => {
        this.plats.update((l) => (l ?? []).map((x) => (x.idPlat === maj.idPlat ? maj : x)));
        this.toasts.succes(maj.disponible ? `${maj.nom} est de nouveau disponible` : `${maj.nom} est indisponible`);
      },
      error: (e) => this.toasts.erreur(e),
    });
  }

  async supprimer(p: Plat): Promise<void> {
    const ok = await this.confirm.demander({
      titre: `Supprimer « ${p.nom} » ?`,
      message: 'Le plat sera retiré de la carte. S’il figure dans des commandes, rendez-le plutôt indisponible.',
      libelle: 'Supprimer',
      danger: true,
    });
    if (!ok) return;
    this.api.plats.supprimer(p.idPlat!).subscribe({
      next: () => {
        this.plats.update((l) => (l ?? []).filter((x) => x.idPlat !== p.idPlat));
        this.toasts.succes('Plat supprimé');
      },
      error: (e) => this.toasts.erreur(e),
    });
  }
}
