import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { FcfaPipe, ImagePipe, LibellePipe, STATUTS, emojiCategorie, fcfa, nomClient } from '../../core/format';
import { Dashboard } from '../../core/models';
import { ToastService } from '../../core/toast.service';
import { IconComponent } from '../../ui/icon.component';

@Component({
  selector: 'app-dashboard',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, DatePipe, IconComponent, FcfaPipe, ImagePipe, LibellePipe],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.css',
})
export class DashboardPage {
  private api = inject(ApiService);
  private toasts = inject(ToastService);
  protected auth = inject(AuthService);

  protected data = signal<Dashboard | null>(null);
  protected aujourdHui = new Date();
  protected statuts = STATUTS;
  protected emoji = emojiCategorie;
  protected nomClient = nomClient;

  protected salutation = this.aujourdHui.getHours() < 18 ? 'Bonjour' : 'Bonsoir';

  /** Barres du graphique des ventes, hauteur en % du jour le plus fort. */
  protected barres = computed(() => {
    const ventes = this.data()?.ventes7Jours ?? [];
    const max = Math.max(...ventes.map((v) => v.montant), 1);
    return ventes.map((v) => ({
      ...v,
      hauteur: Math.max((v.montant / max) * 100, v.montant > 0 ? 4 : 1.5),
      libelle: fcfa(v.montant),
    }));
  });

  protected totalSemaine = computed(() =>
    (this.data()?.ventes7Jours ?? []).reduce((s, v) => s + v.montant, 0),
  );

  protected repartition = computed(() => {
    const r = this.data()?.repartitionStatuts ?? {};
    const total = Object.values(r).reduce((a, b) => a + b, 0) || 1;
    return Object.entries(r)
      .filter(([, n]) => n > 0)
      .map(([statut, n]) => ({ statut, n, pct: (n / total) * 100 }));
  });

  constructor() {
    this.charger();
  }

  charger(): void {
    this.api.dashboard().subscribe({
      next: (d) => this.data.set(d),
      error: (e) => this.toasts.erreur(e),
    });
  }
}
