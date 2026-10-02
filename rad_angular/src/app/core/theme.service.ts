import { Injectable, effect, signal } from '@angular/core';
import { stockage } from './stockage';

type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  readonly theme = signal<Theme>(this.initial());

  constructor() {
    effect(() => {
      document.documentElement.setAttribute('data-theme', this.theme());
      stockage.ecrire('rad.theme', this.theme());
    });
  }

  basculer(): void {
    this.theme.update((t) => (t === 'dark' ? 'light' : 'dark'));
  }

  private initial(): Theme {
    const enregistre = stockage.lire('rad.theme');
    if (enregistre === 'dark' || enregistre === 'light') return enregistre;
    return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }
}
