import { Injectable, signal } from '@angular/core';

export interface DemandeConfirmation {
  titre: string;
  message: string;
  libelle?: string;
  danger?: boolean;
  resoudre: (ok: boolean) => void;
}

/** Boîte de confirmation asynchrone : `if (await confirm.demander({...})) { ... }`. */
@Injectable({ providedIn: 'root' })
export class ConfirmService {
  readonly demande = signal<DemandeConfirmation | null>(null);

  demander(options: Omit<DemandeConfirmation, 'resoudre'>): Promise<boolean> {
    return new Promise((resolve) => {
      this.demande.set({ ...options, resoudre: resolve });
    });
  }

  repondre(ok: boolean): void {
    this.demande()?.resoudre(ok);
    this.demande.set(null);
  }
}
