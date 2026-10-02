import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: number;
  type: 'succes' | 'erreur' | 'info';
  message: string;
}

/** Extrait un message lisible d'une erreur HTTP renvoyée par l'API. */
export function messageErreur(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) return 'Serveur injoignable. Vérifiez votre connexion.';
    const msg = err.error?.message;
    if (typeof msg === 'string' && msg) return msg;
    if (err.status === 403) return "Vous n'avez pas les droits pour cette action.";
  }
  return 'Une erreur est survenue.';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private compteur = 0;
  readonly toasts = signal<Toast[]>([]);

  succes(message: string): void {
    this.afficher('succes', message);
  }

  erreur(messageOuErreur: unknown): void {
    this.afficher(
      'erreur',
      typeof messageOuErreur === 'string' ? messageOuErreur : messageErreur(messageOuErreur),
    );
  }

  info(message: string): void {
    this.afficher('info', message);
  }

  fermer(id: number): void {
    this.toasts.update((liste) => liste.filter((t) => t.id !== id));
  }

  private afficher(type: Toast['type'], message: string): void {
    const id = ++this.compteur;
    this.toasts.update((liste) => [...liste, { id, type, message }]);
    setTimeout(() => this.fermer(id), type === 'erreur' ? 5000 : 3500);
  }
}
