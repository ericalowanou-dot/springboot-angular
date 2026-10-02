/** Accès à localStorage tolérant aux erreurs (navigation privée, stockage bloqué). */
export const stockage = {
  lire(cle: string): string | null {
    try {
      return localStorage.getItem(cle);
    } catch {
      return null;
    }
  },
  ecrire(cle: string, valeur: string): void {
    try {
      localStorage.setItem(cle, valeur);
    } catch {
      /* stockage indisponible : la session ne survivra pas au rechargement */
    }
  },
  supprimer(cle: string): void {
    try {
      localStorage.removeItem(cle);
    } catch {
      /* rien à faire */
    }
  },
};
